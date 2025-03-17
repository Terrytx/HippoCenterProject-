<!-- EventDetail.vue -->
<template>
  <div>
    <!-- 麵包屑 -->
    <div class="breadcrumb-wrapper">
      <BreadCrumb :breadcrumb-items="breadcrumbItems" />
    </div>  
    <div>
      <!-- 篩選元件，可依需求自行取用 -->
      <FilterComponent @refreshData="fetchDataFromParent" />

      <div class="event-detail container">
        <div v-if="event" class="event-content">
          <div class="event-grid">
            <!-- 左側圖片 -->
            <div class="event-image">
              <img
                :src="event?.images?.[0]?.imageUrl || '/default-image.jpg'"
                alt="封面圖片"
              />
            </div>

            <!-- 右側活動內容 -->
            <div class="event-info">
              <h1>{{ event.eventName }}</h1>
              <p class="date">開始日期: {{ event.eventStartDate }}</p>
              <p class="date">結束日期: {{ event.eventEndDate }}</p>
              <p><strong>更新日期：</strong>{{ event.eventUpdateDate }}</p>
              <p><strong>票價：</strong>NT$ {{ event.eventPrice }}</p>
              <p><strong>類別：</strong>{{ event.eventCategory }}</p>
              <p><strong>場地：</strong>{{ event.venueName }}</p>

              <div class="description-container">
                {{ event.eventDescription1 }}
              </div>

              <div class="button-group">
                <!-- 加入我的最愛 (改用與購票一樣的 .purchase 樣式) -->
                <button class="btn purchase" @click="addToFavorites(event.eventId, 'A')">
                  加入最愛
                </button>

                <!-- 立即購票 -->
                <button class="btn purchase" @click="handlePurchaseClick">
                  立即購票
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="loading">
          <p>載入中...</p>
        </div>
      </div>

      <!-- 購票 Modal -->
      <div v-if="showModal" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h3>確認購票</h3>
            <button class="close-button" @click="closeModal">&times;</button>
          </div>

          <div class="modal-body">
            <div class="ticket-info">
              <p><strong>活動名稱：</strong>{{ event?.eventName }}</p>
              <p><strong>票價：</strong>NT$ {{ event?.eventPrice }}</p>
              <div class="quantity-control">
                <label>購買數量：</label>
                <div class="quantity-buttons">
                  <button
                    class="quantity-btn"
                    @click="decreaseTickets"
                    :disabled="ticketQuantity <= 1"
                  >
                    -
                  </button>
                  <input type="number" v-model.number="ticketQuantity" min="1" readonly />
                  <button class="quantity-btn" @click="increaseTickets">+</button>
                </div>
              </div>
              <p class="total-amount">
                <strong>總金額：</strong>
                <span>NT$ {{ totalAmount }}</span>
              </p>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn cancel" @click="closeModal">取消</button>
            <button class="btn confirm" @click="confirmPurchase" :disabled="isProcessing">
              {{ isProcessing ? "處理中..." : "確認購票" }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import axiosapi from '@/plugins/axios.js'
import Swal from "sweetalert2";

import FilterComponent from "@/components/model/FilterComponent.vue";
import BreadCrumb from "@/components/model/BreadCrumb.vue";

// ------------------麵包屑資料------------------
const breadcrumbItems = [
  { text: "荷馬市藝術文化中心", path: "/" },
  { text: "節目總覽", path: "/event" },
  { text: "節目資訊" },
];

// ------------------路由相關------------------
const route = useRoute();
const router = useRouter();

// ------------------狀態管理------------------
const event = ref(null);
const ticketQuantity = ref(1);
const showModal = ref(false);
const isProcessing = ref(false);

// ------------------計算總金額------------------
const totalAmount = computed(() => {
  if (!event.value) return 0;
  return event.value.eventPrice * ticketQuantity.value;
});

// ------------------Token相關函式------------------
const getAuthToken = () => localStorage.getItem("authToken");

const getMemberIdFromToken = () => {
  const token = getAuthToken();
  if (!token) return null;
  try {
    const parts = token.split(".");
    if (parts.length !== 3) {
      console.error("Token 格式錯誤");
      return null;
    }
    const payload = JSON.parse(atob(parts[1]));
    const userData = JSON.parse(payload.sub);
    return userData.memberId;
  } catch (error) {
    console.error("解析 Token 失敗:", error);
    return null;
  }
};

// ------------------API 相關函式------------------
const fetchDataFromParent = async () => {
  await fetchEvent();
};

const fetchEvent = async () => {
  const eventId = route.params.id;
  const eventName = route.params.eventName;

  if (!eventId && !eventName) {
    console.error("活動 ID 或名稱無效");
    return;
  }

  try {
    // 使用反引號包裹字串插值
    // const url = `http://localhost:8080/api/events-user/${eventId}`;
    // 若需要用 eventName 查詢，可自行修改 URL 或加上判斷

    // const response = await axios.get(url);
    const response = await axiosapi.get(`/api/events-user/${eventId}`);

    if (!response.data) {
      console.error("未找到活動資料");
      return;
    }

    event.value = Array.isArray(response.data) ? response.data[0] : response.data;
  } catch (error) {
    console.error("獲取活動詳情失敗:", error);
  }
};

// ------------------數量控制------------------
const increaseTickets = () => {
  ticketQuantity.value++;
};

const decreaseTickets = () => {
  if (ticketQuantity.value > 1) {
    ticketQuantity.value--;
  }
};

// ------------------Modal 控制------------------
const handlePurchaseClick = () => {
  const token = getAuthToken();
  if (!token) {
    Swal.fire({
      icon: "warning",
      title: "請先登入",
      text: "您需要先登入才能購票。",
      confirmButtonText: "前往登入",
      confirmButtonColor: "#6c757d",
    }).then(() => {
      localStorage.setItem("redirectTo", "/");
      router.push("/secure/login");
    });
    return;
  }
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  ticketQuantity.value = 1;
  isProcessing.value = false;
};

const confirmPurchase = async () => {  
  const token = localStorage.getItem("authToken");
  if (!token) {
    alert("請先登入");
    router.push("/secure/login");
    return;
  }

  isProcessing.value = true;

  // 顯示處理中提示
  Swal.fire({
    title: "處理中...",
    allowOutsideClick: false,
    didOpen: () => {
      Swal.showLoading();
    }
  });

  try {
    console.log("準備發送購票請求...");
    const requestData = {
      eventId: event.value.eventId,
      memberId: getMemberIdFromToken(),
      ticketAmount: ticketQuantity.value,
      eventPrice: event.value.eventPrice,
      orderStatus: "PENDING",
    };

    const response = await axiosapi.post(
      "/api/tickets/purchase",
      requestData,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    console.log("✅ 後端回應成功，收到的 ECPay 付款表單:", response.data);

    // 關閉處理中提示
    Swal.close();

    // 插入並提交 ECPay 付款表單
    document.body.insertAdjacentHTML("beforeend", response.data);

    setTimeout(() => {
      const formEl = document.getElementById("payForm");
      if (formEl) {
        formEl.submit();
      }
    }, 1000);

    closeModal();
  } catch (error) {
    console.error("❌ 購票失敗:", error);
    Swal.close();
    Swal.fire({
      icon: "error",
      title: "購票失敗",
      text: "發生錯誤，請稍後再試",
      confirmButtonColor: "#6c757d",
    });
  } finally {
    isProcessing.value = false;
  }
};

// const confirmPurchase = async () => { 
//   const token = localStorage.getItem("authToken");
//   if (!token) {
//     alert("請先登入");
//     router.push("/secure/login");
//     return;
//   }

//   isProcessing.value = true;

//   // 加入「處理中」提示
//   Swal.fire({
//     title: "處理中...",
//     allowOutsideClick: false,
//     didOpen: () => {
//       Swal.showLoading();
//     }
//   });

//   try {
//     console.log("準備發送購票請求...");
//     const requestData = {
//       eventId: event.value.eventId,
//       memberId: getMemberIdFromToken(),
//       ticketAmount: ticketQuantity.value,
//       eventPrice: event.value.eventPrice,
//       orderStatus: "PENDING",
//     };

//     const response = await axiosapi.post(
//       "/api/tickets/purchase",
//       requestData,
//       {
//         headers: { Authorization: `Bearer ${token}` },
//       }
//     );

//     console.log("✅ 後端回應成功，收到的 ECPay 付款表單:", response.data);

//     // 關閉「處理中」提示
//     Swal.close();

//     // 顯示「正在跳轉付款」提示，防止使用者誤以為交易已完成
//     Swal.fire({
//       title: "正在跳轉付款...",
//       text: "請稍候，系統將導向綠界付款頁面。",
//       icon: "info",
//       showConfirmButton: false,
//       allowOutsideClick: false,
//       didOpen: () => {
//         Swal.showLoading();
//       }
//     });

//     // 1. 將後端回傳的「整段HTML字串」插入到網頁 body
//     document.body.insertAdjacentHTML("beforeend", response.data);

//     // 2. 延遲 1.5 秒後觸發表單提交，確保使用者看到 Swal 提示
//     setTimeout(() => {
//       const formEl = document.getElementById("payForm");
//       if (formEl) {
//         console.log("🔄 正在跳轉到綠界...");
//         formEl.submit();
//       } else {
//         console.error("❌ 未找到 payForm，跳轉失敗");
//         Swal.fire({
//           icon: "error",
//           title: "跳轉失敗",
//           text: "找不到付款表單，請聯繫客服。",
//           confirmButtonColor: "#6c757d",
//         });
//       }
//     }, 1500);

//     closeModal();

//   } catch (error) {
//     console.error("❌ 購票失敗:", error);
//     Swal.close();
//     Swal.fire({
//       icon: "error",
//       title: "購票失敗",
//       text: "發生錯誤，請稍後再試",
//       confirmButtonColor: "#6c757d",
//     });
//   } finally {
//     isProcessing.value = false;
//   }
// };


// ------------------加入「我的最愛」------------------
const addToFavorites = async (id) => {
  console.log("切換我的最愛狀態，ID:", id);

  const token = localStorage.getItem("authToken");
  if (!token) {
    Swal.fire({
      icon: "warning",
      title: "請先登入！",
      confirmButtonText: "確定",
      confirmButtonColor: "#6c757d",
    }).then(() => {
      // 跳轉至會員登入頁面
      router.push("/secure/login");
    });
    return;
  }

  // 假設後端定義 'E' 代表活動
  const myloveType = "E";

  try {
    const response = await axiosapi.post(
      `/secure/mylove/toggle/${myloveType}`,
      { myloveId: id },
      {
        headers: {
          Authorization: "Bearer " + token,
          "Content-Type": "application/json",
        },
      }
    );

    if (response.data.message) {
      Swal.fire({
        icon: "success",
        title: "提示",
        text: "成功加入我的最愛",
        confirmButtonText: "確定",
        confirmButtonColor: "#6c757d",
      });
    }

    // 如果需要更新前端列表的 isFavorite，請自行定義 cards 或其他結構
    /*
    const card = cards.value.find((c) => c.id === id);
    if (card) {
      card.isFavorite = !card.isFavorite;
    }
    */
  } catch (error) {
    console.error("切換最愛狀態時出錯:", error);
    Swal.fire({
      icon: "error",
      title: "錯誤",
      text: "切換最愛狀態時出錯，請稍後再試！",
      confirmButtonText: "確定",
      confirmButtonColor: "#6c757d",
    });
  }
};

// ------------------生命週期------------------
onMounted(fetchEvent);

// ------------------路由監聽------------------
watch(
  () => route.params.id,
  () => {
    fetchEvent();
  }
);
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.breadcrumb-wrapper{
  margin-left: 11%;
  margin-top: 0.5%;
}
.event-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.event-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 20px;
}

.event-image img {
  width: 100%;
  max-width: 500px;
  height: auto;
  object-fit: cover;
  border-radius: 8px;
}

.event-info {
  padding: 20px;
}

.event-info h1 {
  margin-bottom: 20px;
  color: #333;
}

.event-info p {
  margin-bottom: 10px;
  color: #666;
}

.description-container {
  margin: 20px 0;
  padding: 15px;
  border-left: 4px solid #ff4b4b;
  background: #f8f8f8;
  border-radius: 4px;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 這兩個 class 都設為相同樣式 */
.purchase {
  background-color: #ff4b4b;
  color: white;
}

.purchase:hover {
  opacity: 0.9;
}

/* 若您仍需保留 .favorite，可不刪，只是改成沒特殊樣式即可
.favorite {
  background-color: #ff4b4b;
  color: white;
}

.favorite:hover {
  opacity: 0.9;
}
*/

/* Modal 樣式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.modal-body {
  padding: 20px;
}

.ticket-info {
  margin-bottom: 20px;
}

.quantity-control {
  margin: 20px 0;
}

.quantity-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-control input {
  width: 60px;
  text-align: center;
  border: 1px solid #ddd;
  padding: 5px;
}

.total-amount {
  font-size: 1.2em;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.cancel {
  background-color: #6c757d;
  color: white;
}

.confirm {
  background-color: #ff4b4b;
  color: white;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

@media (max-width: 768px) {
  .event-grid {
    grid-template-columns: 1fr;
  }

  .event-image img {
    max-width: 100%;
  }

  .modal-content {
    width: 95%;
    margin: 10px;
  }
}
</style>
