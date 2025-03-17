<template>
  <div class="container">
    <h1 class="title">🎟 購票資訊</h1>

    <div v-if="loading" class="loading">載入中...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <table v-if="!loading" class="ticket-table">
      <thead>
        <tr>
          <th>會員 ID</th>
          <th>活動名稱</th>
          <th>票價</th>
          <th>日期</th>
          <th>地點</th>
          <th>票券狀態</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="tickets.length === 0">
          <td colspan="5" class="no-data">暫無資料</td>
        </tr>
        <tr v-for="ticket in tickets" :key="ticket.ticketId">
          <td>{{ ticket.memberId }}</td>
          <td>{{ ticket.eventName }}</td>
          <td>${{ ticket.price }}</td>
          <td>{{ ticket.date }}</td>
          <td>{{ ticket.location }}</td>
          <td>{{ ticket.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axiosapi from '@/plugins/axios.js'

// Vue Router
const router = useRouter();

// 定義狀態
const tickets = ref([]);
const loading = ref(true);
const error = ref(null);

// 取得購票資訊
const fetchTickets = async () => {
  try {
    loading.value = true;

    // 檢查是否登入
    const token = localStorage.getItem("authToken");
    if (!token) {
      console.log("🔴 未登入，跳轉到登入頁面");
      localStorage.setItem("redirectTo", router.currentRoute.value.fullPath); // 確定登入後能跳轉回來

      setTimeout(() => {
        alert("請先登入後再查看購票資訊");
        router.push("/secure/login");
      }, 100);

      return;
    }

    // 發送 API 請求
    const response = await axiosapi.get("/api/admin/tickets");

    // 將資料賦值給 tickets
    tickets.value = response.data;

  } catch (err) {
    error.value = err.response?.data?.message || err.message || "無法取得購票資訊";
    console.error("❌ [錯誤]：", err);
  } finally {
    loading.value = false;
  }
};

// API URL（請換成你的後端 API）
// const API_URL = "http://localhost:8080/api/admin/tickets";

// // 取得購票資訊
// const fetchTickets = async () => {
//   try {
//     loading.value = true;

//     // 檢查是否登入
//     const token = localStorage.getItem("authToken");
//     if (!token) {
//       console.log("🔴 未登入，跳轉到登入頁面");
//       localStorage.setItem("redirectTo", router.currentRoute.value.fullPath); //確定登入後能跳轉回來

//       setTimeout(() => {
//         alert("請先登入後再查看購票資訊");
//         router.push("/secure/login");
//       }, 100);

//       return;
//     }

//     // 發送 API 請求
//     const response = await fetch(API_URL, {
//       headers: { Authorization: `Bearer ${token}` },
//     });

//     if (!response.ok) {
//       throw new Error("無法取得購票資訊");
//     }

//     tickets.value = await response.json();
//   } catch (err) {
//     error.value = err.message;
//   } finally {
//     loading.value = false;
//   }
// };

// 當元件掛載時執行
onMounted(fetchTickets);
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.title {
  text-align: center;
  color: #333;
}

.loading {
  text-align: center;
  font-size: 18px;
  color: #555;
}

.error {
  color: red;
  text-align: center;
  font-weight: bold;
}

.ticket-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.ticket-table th,
.ticket-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

.ticket-table th {
  background: #f4f4f4;
}

.no-data {
  text-align: center;
  font-style: italic;
  color: #888;
}
</style>
