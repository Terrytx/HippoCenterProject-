<template>
  <div class="promotion-container">
      <h2>促銷管理</h2>

      <!-- 按鈕操作 -->
      <div class="action-bar">
          <button class="btn btn-warning" @click="expireOldPromotions">檢驗過期促銷券</button>
          <button class="btn btn-info" @click="showAddModal = true">新增促銷活動</button>
      </div>

      <!-- 促銷活動列表 -->
      <table class="promotion-table">
            <thead>
                <tr>
                    <th>促銷名稱</th>
                    <th>促銷碼</th>
                    <th>折扣率</th>
                    <th>開始時間</th>
                    <th>結束時間</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="promotion in promotions" :key="promotion.promotionId">
                    <td>{{ promotion.title }}</td>
                    <td>{{ promotion.promotionCode }}</td>
                    <td>{{ (promotion.discountRate * 100).toFixed(1) }}%</td>
                    <td>{{ new Date(promotion.startDate).toLocaleString() }}</td>
                    <td>{{ new Date(promotion.endDate).toLocaleString() }}</td>
                    <td>
                        <button class="btn btn-success" @click="sendPromotionToAllMembers(promotion.promotionCode)">發送</button>
                    </td>
                </tr>
            </tbody>
      </table>


      <!-- 新增促銷活動 Modal -->
      <div v-if="showAddModal" class="modal">
        <div class="modal-content">
        <h3>新增促銷活動</h3>
        <label>促銷名稱：</label>
        <input type="text" v-model="newPromotion.title" />

        <label>促銷碼（5~7 碼）：</label>
        <input type="text" v-model="newPromotion.promotionCode" maxlength="7" minlength="5" />

        <label>促銷描述：</label>
        <textarea v-model="newPromotion.description"></textarea>

        <label>折扣率：</label>
        <input type="number" v-model="newPromotion.discountRate" step="0.01" min="0.01" max="1" />

        <label>開始時間：</label>
        <input type="datetime-local" v-model="newPromotion.startDate" />

        <label>結束時間：</label>
        <input type="datetime-local" v-model="newPromotion.endDate" />

        <div class="modal-actions">
            <button class="btn btn-secondary" @click="showAddModal = false">取消</button>
            <button class="btn btn-primary" @click="createPromotion">確定新增</button>
        </div>
      </div>
</div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import Swal from "sweetalert2";
import axiosapi from "@/plugins/axios.js";

const promotions = ref([]);
const showAddModal = ref(false);
const newPromotion = ref({
  title: "",
  discountRate: 0.1,
  startDate: "",
  endDate: ""
});

// 🚀 取得所有促銷活動，確保新活動在最上面
const fetchPromotions = async () => {
    try {
        const response = await axiosapi.get("/mowmow/admin/promotions/all");
        promotions.value = response.data.sort((a, b) => new Date(b.startDate) - new Date(a.startDate));
    } catch (error) {
        console.error("無法獲取促銷活動列表:", error);
        Swal.fire({
            title: "獲取促銷活動失敗",
            text: "無法獲取促銷活動列表，請稍後再試",
            icon: "error",
            confirmButtonColor: "#4A6741",
            confirmButtonText: "確定"
        });
    }
};


// 🚀 發送促銷券（透過 promotionCode）
const sendPromotionToAllMembers = async (promotionCode) => {
  try {
      await axiosapi.post(`/mowmow/admin/promotions/send/${promotionCode}`);

      Swal.fire({
          title: "發送成功",
          text: "促銷券已發送給所有會員！",
          icon: "success",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });

  } catch (error) {
      console.error("發送促銷券失敗:", error);

      Swal.fire({
          title: "發送失敗",
          text: "無法發送促銷券，請稍後再試",
          icon: "error",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });
  }
};



// 🚀 檢驗過期促銷券
const expireOldPromotions = async () => {
  try {
      await axiosapi.post("/mowmow/admin/promotions/check-expired");

      Swal.fire({
          title: "檢查完成",
          text: "過期促銷券已檢查並更新！",
          icon: "success",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });

  } catch (error) {
      console.error("檢查促銷券失敗:", error);

      Swal.fire({
          title: "檢查失敗",
          text: "無法檢查過期促銷券，請稍後再試",
          icon: "error",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });
  }
};



// 🚀 新增促銷活動
const createPromotion = async () => {
  if (!newPromotion.value.title || !newPromotion.value.promotionCode || !newPromotion.value.description) {
      Swal.fire({
          title: "輸入錯誤",
          text: "促銷名稱、促銷碼、描述不可為空！",
          icon: "warning",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });
      return;
  }

  if (newPromotion.value.promotionCode.length < 5 || newPromotion.value.promotionCode.length > 7) {
      // ✅ 促銷碼長度驗證
      Swal.fire({
          title: "促銷碼長度錯誤",
          text: "促銷碼長度必須介於 5 到 7 碼之間！",
          icon: "warning",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });
      return;
  }

  if (newPromotion.value.startDate >= newPromotion.value.endDate) {
      // ✅ 開始時間驗證
      Swal.fire({
          title: "時間錯誤",
          text: "開始時間不可晚於結束時間！",
          icon: "warning",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });
      return;
  }

  try {
      await axiosapi.post("/mowmow/admin/promotions/add", newPromotion.value);

      // ✅ 成功訊息
      Swal.fire({
          title: "新增成功",
          text: "促銷活動已成功新增！",
          icon: "success",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });

      showAddModal.value = false;
      fetchPromotions();
  } catch (error) {
      console.error("新增促銷活動失敗:", error);

      // ✅ 失敗訊息
      Swal.fire({
          title: "新增失敗",
          text: `新增促銷活動失敗：${error.response?.data || error.message}`,
          icon: "error",
          confirmButtonColor: "#4A6741",
          confirmButtonText: "確定"
      });
  }
};


// 初始化時載入促銷活動
onMounted(fetchPromotions);
</script>

<style scoped>
.promotion-container {
  padding: 20px;
  max-width: 1200px;
  margin: auto;
}

.action-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-end;  /* 新增這行 */
}

.promotion-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.promotion-table th, .promotion-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

.promotion-table th {
  background-color: #f8f9fa;
}

/* 🚀 按鈕樣式 */
.btn {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-warning {
  background-color: #ffc107;
  color: black;
}

.btn-info {
  background-color: #17a2b8;
  color: white;
}

.btn-secondary {
  background-color: #4A6741;
  color: white;
}

.btn:hover {
  opacity: 0.8;
}

/* 🚀 新增促銷活動 Modal */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.modal input {
  width: 100%;
  padding: 8px;
  margin-top: 5px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>
