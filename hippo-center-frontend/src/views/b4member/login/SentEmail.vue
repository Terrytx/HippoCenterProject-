<template>
  <div>
    <h3>忘記密碼</h3>
    <div class="main-content">
      <div class="center-div">
        <table>
          <tbody>
            <tr>
              <td>帳號</td>
              <td>
                <input type="text" placeholder="請輸入Email" v-model="account" @keydown.enter="login" />
              </td>
            </tr>
          </tbody>
        </table>
        <button class="submit" type="submit" @click="sent">寄送重設密碼信</button>
        <p v-if="message" class="error-message">{{ message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axiosapi from "@/plugins/axios.js";
import Swal from "sweetalert2";

const account = ref(""); // 用來綁定 Email 欄位
const message = ref(""); // 顯示錯誤或成功訊息
const router = useRouter(); // 用來導航頁面，若有需要跳轉可以使用

// 寄送重設密碼郵件
const sent = async () => {
  // 確保使用者輸入了有效的電子郵件
  if (!account.value) {
    message.value = "請輸入有效的 Email 地址";
    return;
  }

  // 這裡可以直接設置 contextPath 為 `/`，因為您沒有額外的 contextPath
  const contextPath = '/'; // 根目錄沒有額外的 contextPath

  // 準備要發送的資料
  const payload = {
    email: account.value,
    contextPath: window.location.origin, // 從瀏覽器的當前 URL 獲取 contextPath
  };

  try {
    // 呼叫後端 API 來寄送重設密碼郵件
    const response = await axiosapi.post("/user/requestResetPassword", payload);
      // 顯示 SweetAlert2 錯誤訊息
      Swal.fire({
      icon: 'success',
      title: '信件發送成功',
      text: '信件發送成功，請至信箱確認。',
      confirmButtonColor: '#0e0e0e'
    });
  } catch (error) {
    // 處理錯誤
    if (error.response && error.response.data) {
      console.log(error)
      message.value = error.response.data; // 從後端回傳的錯誤訊息
    } else {
      console.log(error)
      message.value = "發生錯誤，請稍後再試。";
    }
  }
};

</script>

<style scoped>
@import "@/assets/fonts/font.css";
@import "@/assets/styles/mainContent.css";

h3 {
  display: flex;
  align-items: center;
  width: 500px;
  margin-right: auto;
  margin-left: 11%;
}

.main-content {
  position: relative;
  margin-right: auto;
  width: 100%;
  margin-bottom: 100px;
  margin-top: 50px;
  display: flex; /* 開啟 flexbox */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

.center-div {
  width: 30%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 10px; /* 表格和按鈕之間的間隔 */
  border: 1px lightgrey solid;
  padding-bottom: 20px;
  border-radius: 10px; /* 設定圓角邊框 */
}

/* 設定表格內的內容居中，並去掉框線 */
table {
  border-collapse: collapse;
  width: 100%;
  margin-top: 10px;
  margin-bottom: 10px; /* 表格與按鈕之間的間距 */
  font-size: 18px;
}

td {
  padding: 30px;
  border: none;
}


td.active {
  background-color: #a7a2a2;
  color: white;
}


tr:nth-child(1) td:nth-child(2) input{
  width: 95%; /* 設定 input 寬度為 100% 減去左右邊距 */
  padding: 8px; /* 設定內邊距，讓文字不會直接貼著邊框 */
  border: 1px lightgray solid; /* 邊框寬度、樣式及顏色合併 */
  border-radius: 4px; /* 設定圓角邊框 */
  margin-right: 10px; /* input 框右側與 div 保持距離 */
}


td.active {
  background-color: #a7a2a2;
  color: white;
}

.error-message {
  color: red;
  margin-top: 10px;
  font-size: 14px;
}

.submit {
  background-color: #f8961e;
  color: white;
  padding: 10px 20px;
  font-size: 16px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
}

.submit:hover {
  background-color: rgb(51, 49, 46);
  transform: scale(1);
  transition: all 0.3s ease;
}

.change {
  padding: 0;
  margin: 0;
  border-bottom: 1px lightgrey solid;
}

</style>
