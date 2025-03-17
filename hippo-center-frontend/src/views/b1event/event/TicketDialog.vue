<template>
  <div class="modal fade" id="purchaseModal" tabindex="-1" ref="modal">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content" v-if="show">
        <div class="modal-header">
          <h5 class="modal-title">購票資訊</h5>
          <button
            type="button"
            class="btn-close"
            @click="close"
            aria-label="Close"
          ></button>
        </div>

        <div class="modal-body">
          <div class="card border-0">
            <div class="card-body p-0">
              <div v-if="loading">
                <p class="text-center">載入中...</p>
              </div>

              <div v-else-if="eventData">
                <h4 class="text-primary mb-3">{{ eventData.name }}</h4>
                <div class="d-flex align-items-center mb-2">
                  <i class="bi bi-calendar3 me-2"></i>
                  <span>{{ eventData.startDate }} ~ {{ eventData.endDate }}</span>
                </div>
                <div class="d-flex align-items-center mb-3">
                  <i class="bi bi-tag me-2"></i>
                  <span class="text-danger fw-bold">NT$ {{ eventData.price }}</span>
                </div>

                <div class="form-group">
                  <label class="form-label fw-bold">購買張數</label>
                  <div class="input-group">
                    <button
                      class="btn btn-outline-secondary"
                      type="button"
                      @click="decreaseAmount"
                      :disabled="ticketAmount <= 1"
                    >
                      <i class="bi bi-dash"></i>
                    </button>
                    <input
                      type="number"
                      class="form-control text-center"
                      v-model="ticketAmount"
                      min="1"
                      style="max-width: 100px"
                    />
                    <button
                      class="btn btn-outline-secondary"
                      type="button"
                      @click="increaseAmount"
                    >
                      <i class="bi bi-plus"></i>
                    </button>
                  </div>
                </div>

                <div class="alert alert-info mt-3">
                  <div class="d-flex justify-content-between align-items-center">
                    <span>總金額：</span>
                    <span class="h5 mb-0">NT$ {{ totalPrice }}</span>
                  </div>
                </div>
              </div>

              <div v-else>
                <p class="text-center text-danger">活動資訊載入失敗，請稍後再試。</p>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="close">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            @click="checkout"
            :disabled="isProcessing || !eventData"
          >
            {{ isProcessing ? "處理中..." : "確認購票" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, defineProps, defineEmits, onMounted } from "vue";
import { Modal } from "bootstrap";
import axiosapi from '@/plugins/axios.js'
import Swal from "sweetalert2";

const props = defineProps({
  show: Boolean,
  eventName: String,
  memberId: [String, Number],
  jwtToken: String,
});

const emit = defineEmits(["close"]);

const modal = ref(null);
const modalInstance = ref(null);
const ticketAmount = ref(1);
const isProcessing = ref(false);
const eventData = ref(null);
const loading = ref(false);

// 計算總金額
const totalPrice = computed(() => {
  return eventData.value ? eventData.value.price * ticketAmount.value : 0;
});

// 監聽 `show` 變化來控制 modal
watch(
  () => props.show,
  async (newVal) => {
    console.log("🎟️ show 變更:", newVal);
    console.log("📌 傳入的 eventName:", props.eventName);
    if (newVal) {
      modalInstance.value.show();
      await fetchEventDetails();
    } else {
      modalInstance.value.hide();
    }
  }
);

const fetchEventDetails = async () => {
  if (!props.eventName) {
    console.error("❌ eventName 未提供！");
    await Swal.fire({
      icon: "error",
      title: "錯誤",
      text: "無法取得活動資訊，請重新整理頁面。",
      confirmButtonText: "確定",
      confirmButtonColor: "#6c757d",
    });
    return;
  }

  try {
    loading.value = true;
    const response = await axiosapi.get(
      `/api/events-user/search?eventName=${encodeURIComponent(
        props.eventName
      )}`
    );
    eventData.value = response.data;
  } catch (error) {
    console.error("❌ 取得活動資訊失敗:", error);
    eventData.value = null;
    await Swal.fire({
      icon: "error",
      title: "錯誤",
      text: "活動資訊載入失敗，請稍後再試。",
      confirmButtonText: "確定",
      confirmButtonColor: "#6c757d",
    });
  } finally {
    loading.value = false;
  }
};

// 初始化 Bootstrap Modal
onMounted(() => {
  modalInstance.value = new Modal(document.getElementById("purchaseModal"), {
    keyboard: false,
    backdrop: "static",
  });
});

// 增減票數方法
const increaseAmount = () => {
  ticketAmount.value++;
};

const decreaseAmount = () => {
  if (ticketAmount.value > 1) {
    ticketAmount.value--;
  }
};

// 結帳（購票 API 串接）
const checkout = async () => {
  if (isProcessing.value || !eventData.value) return;

  try {
    isProcessing.value = true;

    // 顯示確認對話框
    const result = await Swal.fire({
      icon: "question",
      title: "確認購票",
      text: `確定要購買 ${ticketAmount.value} 張票嗎？總金額：NT$ ${totalPrice.value}`,
      showCancelButton: true,
      confirmButtonText: "確定購買",
      cancelButtonText: "取消",
      confirmButtonColor: "#6c757d",
    });

    if (!result.isConfirmed) {
      isProcessing.value = false;
      return;
    }

    const requestData = {
      eventId: eventData.value.id,
      memberId: props.memberId,
      ticketAmount: ticketAmount.value,
      eventPrice: eventData.value.price,
    };

    const response = await axiosapi.post(
      "/api/tickets/purchase",
      requestData,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${props.jwtToken}`,
        },
      }
    );

    console.log("✅ 購票成功，收到 ECPay 付款表單:", response.data);

    // 顯示成功訊息
    await Swal.fire({
      icon: "success",
      title: "購票成功",
      text: "即將導向至付款頁面...",
      timer: 2000,
      showConfirmButton: false,
      confirmButtonColor: "#6c757d",
    });

    document.body.insertAdjacentHTML("beforeend", response.data); // 這裡直接使用整串
setTimeout(() => {
  document.getElementById("payForm").submit(); // 或 forms["payForm"]
}, 1000);

    close();
  } catch (error) {
    console.error("❌ 購票失敗:", error.response);
    await Swal.fire({
      icon: "error",
      title: "購票失敗",
      text: "系統發生錯誤，請稍後再試！",
      confirmButtonText: "確定",
      confirmButtonColor: "#6c757d",
    });
  } finally {
    isProcessing.value = false;
  }
};

// 關閉 Modal
const close = () => {
  ticketAmount.value = 1;
  emit("close");
};
</script>
