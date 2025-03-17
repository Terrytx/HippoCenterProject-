<template>
  <div class="full-screen-container">
    <div class="login-container">
      <div class="login-form">
        <h2>管理員登入</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="account">使用者帳號</label>
            <input type="text" id="account" v-model="account" placeholder="請輸入使用者帳號" required />
          </div>
          <div class="form-group">
            <label for="password">密碼</label>
            <input type="password" id="password" v-model="password" placeholder="請輸入密碼" required />
          </div>
          <button type="submit" class="login-button" @click="handleLogin">登入</button>
          <p class="error-message" v-if="message">{{ message }}</p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';  // 引入 ref 用於創建響應式變數
import { useRouter } from 'vue-router';  // 用於頁面跳轉
import { nextTick } from 'vue';  // 用於確保狀態更新後執行跳轉
import useUserStore from '../stores/user.js';  // 用於使用 Pinia store 存儲用戶資料
import axiosapi from '../plugins/axios.js';  // 引入配置過的 axios 實例


const account = ref("");  // 使用者帳號
const password = ref("");  // 密碼
const message = ref("");  // 錯誤訊息
const userStore = useUserStore();  // 使用 Pinia store 保存用戶資料
const router = useRouter();  // 用於導航跳轉

const handleLogin = async () => {
  message.value = ""; // 清空錯誤訊息

  // 檢查空的欄位，設置為 null

  if (account.value == "") {
    account.value = null;
  }
  if (password.value == "") {
    password.value = null;
  }

  const body = {
    account: account.value,
    password: password.value,
  };
  console.log(body)
  console.log("account", account.value);
  console.log("password", password.value);

  // 清除舊的授權資訊
  axiosapi.defaults.headers.authorization = "";
  userStore.setEmail("");

  try {
    // 發送登入請求
    const response = await axiosapi.post('/admin/login', body); // 這裡的路徑使用了 `baseURL`
    console.log("response", response);

    // 如果後端返回的 success 為 true，表示登入成功
    if (response.data.success) {
      // 登入成功，顯示成功訊息並跳轉
      console.log("登入成功:", response.data.message);

      // 將 token 存入 localStorage
      localStorage.setItem("authToken", response.data.token);
      localStorage.setItem("email", response.data.user);
      localStorage.setItem("name", response.data.name);

      // 設置 axios 預設授權標頭
      axiosapi.defaults.headers.authorization = "Bearer " + response.data.token;

      // 設置用戶的電子郵件
      userStore.setEmail(response.data.user);
      userStore.setToken(response.data.token);
      userStore.setName(response.data.name);

      // 等待 Pinia 更新狀態
      await nextTick(() => {
        // 跳轉
        router.push({ path: "/navigation" });
      });
    } else {
      // 登入失敗，顯示錯誤訊息
      message.value = response.data.message; // 顯示錯誤訊息
      console.log("登入失敗:", response.data.message);
    }
  } catch (error) {
    // 捕獲錯誤，顯示錯誤訊息
    console.log("錯誤:", error);
    message.value = "執行失敗，請再試一次";
  }
};

</script>

<style scoped>
:root {
  --primary-color: #4A6741;
  --secondary-color: #8AA682;
  --hover-color: #C2D5BB;
  --background-color: #F5F7F4;
  --text-primary: #2C3E2D;
  --text-secondary: #5C715E;
  --border-color: #D8E0D5;
}

/* 重設瀏覽器的 margin 和 padding */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 外層容器設置為全螢幕大小 */
.full-screen-container {
  height: 100vh; /* 使容器的高度填滿整個視窗 */
  display: flex;
  justify-content: center;  /* 水平居中 */
  align-items: center;  /* 垂直居中 */
  background-color: var(--background-color);
}

.login-container {
  background-color: white;
  padding: 40px;  /* 增加內邊距，使容器內部空間更大 */
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px; /* 擴大容器的最大寬度 */
  border: 1px solid var(--border-color);
}

h2 {
  text-align: center;
  font-size: 28px; /* 增大標題字體 */
  color: var(--primary-color);
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  font-size: 16px; /* 增大標籤字體 */
  color: var(--text-secondary);
  display: block;
  margin-bottom: 8px;
}

input {
  width: 100%;
  padding: 12px;  /* 增加輸入框的內邊距 */
  border-radius: 5px;
  border: 1px solid var(--border-color);
  font-size: 16px;
  color: var(--text-primary);
}

input:focus {
  outline: none;
  border-color: var(--primary-color);
}

button {
  width: 100%;
  padding: 12px;  /* 增加按鈕的內邊距 */
  background-color: var(--primary-color);
  color: white;
  font-size: 18px;  /* 增大按鈕字體 */
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: var(--secondary-color);
}

.error-message {
  color: #e74c3c;
  text-align: center;
  margin-top: 10px;
}
</style>
