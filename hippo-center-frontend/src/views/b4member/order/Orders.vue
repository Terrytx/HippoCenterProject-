<template>
  <div>
    <div>
        <PersonSidebar />
        <div class="main-content">
      <!-- 主內容 -->
      <div class="order">
        <h3>我的訂單</h3>
        <div v-if="orders.length > 0">
          <!-- 自定義表格 -->
          <table class="order-list">
            <thead>
              <tr>
                <th>訂單編號</th>
                <th>總金額</th>
                <th>訂單狀態</th>
                <th>促銷碼</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.orderId">
                <td>{{ order.orderId }}</td>
                <td>NT$ {{ order.totalAmount }}</td>
                <td>{{ getOrderStatusInChinese(order.orderStatus) }}</td>
                <td>{{ order.promotionCode || '無' }}</td>
                <td>
                  <button
                    class="view-details-btn"
                    @click="openOrderDetails(order.orderId)"
                  >
                    查看詳情
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <p v-else>目前沒有訂單。</p>

        <!-- 自定義模態視窗 -->
        <div v-if="isModalVisible" class="modal-overlay" @click.self="closeModal">
          <div class="modal-content">
            <div class="modal-header">
              <h2>訂單詳情</h2>
              <button id="modal-close-btn" class="close-btn" @click="closeModal">X</button>
            </div>
            <div class="modal-body">
              <div v-if="order">
                <p><strong>訂單編號：</strong>{{ order.orderId }}</p>
                <p><strong>總金額：</strong>NT$ {{ order.totalAmount }}</p>
                <p><strong>訂單狀態：</strong>{{ getOrderStatusInChinese(order.orderStatus) }}</p>
                <p><strong>促銷碼：</strong>{{ order.promotionCode || '無' }}</p>

                <h2>商品列表</h2>
                <table class="order-details-table">
                  <thead>
                    <tr>
                      <th>商品名稱</th>
                      <th>數量</th>
                      <th>單價</th>
                      <th>圖片</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in order.orderDetails" :key="item.productId">
                      <td>{{ item.productName }}</td>
                      <td>{{ item.quantity }}</td>
                      <td>NT$ {{ item.price }}</td>
                      <td>
                        <img
                          :src="item.imageUrl"
                          alt="商品圖片"
                          width="80"
                          height="80"
                        />
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <p v-else>載入中...</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script setup>
import PersonSidebar from "@/components/navbar/b4member/PersonSidebar.vue";
import { ref, onMounted, computed } from "vue";
import axiosapi from "@/plugins/axios.js"; // 使用 axios
import useUserStore from "@/stores/user.js";
import { useRouter } from "vue-router";

const orders = ref([]); 
const order = ref(null); // 用於存儲當前顯示的訂單詳情
const userStore = useUserStore();
const router = useRouter();
const isModalVisible = ref(false); // 控制模態視窗的顯示與隱藏

// 將訂單狀態轉換為中文
const orderStatusMap = {
  NEW: "新訂單",
  PAID: "已付款",
  SHIPPED: "已寄出",
  DELIVERED: "已送達",
  CANCELLED: "已取消",
};

// 計算屬性，將英文訂單狀態轉換為中文
const getOrderStatusInChinese = (status) => {
  return orderStatusMap[status] || status; // 若無對應值，顯示原狀態
};

// 獲取所有訂單
async function fetchOrders() {
  try {
    const token = userStore.token;
    if (!token) {
      alert("請先登入");
      router.push("/secure/login");
      return;
    }

    const response = await axiosapi.get(
      "/mowmow/user/orders/findAll",
      {
        headers: { Authorization: `Bearer ${userStore.token}` },
      }
    );

    orders.value = response.data;
  } catch (error) {
    console.error("獲取訂單失敗:", error);
    alert("載入訂單失敗，請稍後再試！");
  }
}

// 打開訂單詳情的彈出視窗
async function openOrderDetails(orderId) {
  try {
    const token = userStore.token;
    if (!token) {
      alert("請先登入");
      router.push("/secure/login");
      return;
    }

    const response = await axiosapi.get(
      `/mowmow/user/orders/${orderId}`,
      {
        headers: { Authorization: `Bearer ${userStore.token}` },
      }
    );

    order.value = response.data;
    isModalVisible.value = true; // 顯示模態視窗
    
  } catch (error) {
    console.error("獲取訂單詳情失敗:", error);
    alert("載入訂單詳情失敗，請稍後再試！");
  }
}

// 關閉模態視窗
function closeModal() {
  isModalVisible.value = false;
  
}

onMounted(fetchOrders);
</script>

<style scoped>
@import '@/assets/styles/main.css';
@import '@/assets/styles/mainContent.css';


.main-content {
  padding-top: 15px;
  min-height: 700px; /* 設定最小高度為600px（根據需求調整） */
}

/* 表格樣式 */
table {
  width: 90%;
  border-collapse: collapse;
  margin-bottom: 30px;
  border-radius: 8px; /* 圓角邊框 */
  overflow: hidden; /* 防止圓角效果被遮擋 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 表格陰影效果 */
  position: relative; /* 用於定位關閉按鈕 */
}

th, td {
  padding: 12px;
  text-align: center;
  border: 1px solid #ddd;
  transition: background-color 0.3s ease;
}

th {
  background-color: #f1f1f1;
}

td {
  background-color: #fff;
}

tr:hover td {
  background-color: #f9f9f9;
}

/* 按鈕樣式 */
button {
  background-color: #f8961e;
  color: #f9f9f9;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 14px;
  border-radius: 6px;
  transition: background-color 0.3s, transform 0.3s ease-in-out;
}

.edit-btn:hover {
  background-color: #d75f00;
  color: #fff;
  transform: translateY(-2px); /* 按鈕懸停時浮動 */
}

.edit-btn:active {
  transform: translateY(1px); /* 按下去時稍微下沉 */
}

/* 禁用的按鈕 */
.edit-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 模態視窗 */
.modal-overlay {
  z-index: 999; /* 背景層 */
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  animation: fadeIn 0.3s ease-in-out;
}

.modal-content {
  background-color: #ffffff;
  padding: 30px;
  border-radius: 8px;
  width: 600px;
  max-height: 80vh;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  color: #333;
  animation: fadeIn 0.3s ease-in-out;
  position: relative;
  overflow: hidden; /* 防止內容超出 */
}

.modal-body {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px; /* 保證滾動條顯示不會遮擋內容 */
}

/* 關閉按鈕樣式 */
.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-weight: bold;
  font-size: 24px;
  color: #555;
}

</style>
