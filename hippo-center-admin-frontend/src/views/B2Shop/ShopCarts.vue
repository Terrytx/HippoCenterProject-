<template>
  <div class="cart-container">
    <h2>購物車管理</h2>

    <!-- 統計未結帳人數 -->
    <div class="cart-summary">
      <p>📌 未結帳人數：<strong>{{ uniqueCartUsers }}</strong> 人</p>
    </div>

    <!-- 購物車列表 -->
    <table class="cart-table">
      <thead>
        <tr>
          <th>會員 ID</th>
          <th>商品 ID</th>
          <th>數量</th>
          <th>加入時間</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cart in carts" :key="cart.cartId">
          <td>{{ cart.memberId }}</td>
          <td>{{ cart.productId }}</td>
          <td>{{ cart.quantity }}</td>
          <td>{{ new Date(cart.addedAt).toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import axiosapi from "@/plugins/axios.js";
import Swal from "sweetalert2";



const carts = ref([]);

// 🚀 取得購物車資訊
const fetchCarts = async () => {
  try {
    const response = await axiosapi.get("/mowmow/admin/cart");
    console.log("📢 取得購物車數據:", response.data); // ✅ 確認 API 是否有回應
    carts.value = response.data; // ✅ 更新購物車數據
  } catch (error) {
    console.error("❌ 無法獲取購物車列表:", error);

    // ✅ 只有 **失敗時** 才顯示 SweetAlert2
    Swal.fire({
      title: "❌ 無法獲取購物車列表",
      text: "請稍後再試",
      icon: "error",
      confirmButtonColor: "#4A6741",
    });
  }
};


// 🚀 計算未結帳人數（依據不同會員 ID 計算）
const uniqueCartUsers = computed(() => {
  return new Set(carts.value.map((cart) => cart.memberId)).size;
});

// 初始化時載入購物車資料
onMounted(fetchCarts);
</script>

<style scoped>
.cart-container {
  padding: 20px;
  max-width: 1200px;
  margin: auto;
}

.cart-summary {
  margin-bottom: 20px;
  font-size: 18px;
}

.cart-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.cart-table th,
.cart-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

.cart-table th {
  background-color: #f8f9fa;
}
</style>
