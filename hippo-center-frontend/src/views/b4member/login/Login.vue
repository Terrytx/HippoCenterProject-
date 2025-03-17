<template>
  <div>
    <h3>會員登入</h3>
    <div class="main-content">
      <div class="center-div">
        <table class="change">
          <tbody>
            <tr class="first-tr">
              <td id="td1" :class="{'active': selectedTab === 'login'}" @click="selectTab('login')">登入</td>
              <td id="td2" :class="{'active': selectedTab === 'register'}" @click="goToRegister">註冊</td>
            </tr>
          </tbody>
        </table>
        <table>
          <tbody>
            <tr>
              <td class="first-td" colspan="2">※以下所有欄位均為必填</td>
            </tr>
            <tr>
              <td>帳號</td>
              <td>
                <input type="text" placeholder="請輸入Email" v-model="account" @keydown.enter="login" />
              </td>
            </tr>
            <tr>
              <td>密碼</td>
              <td>
                <input type="password" placeholder="請輸入密碼" v-model="password" @keydown.enter="login" />
              </td>
            </tr>
            <tr>
              <td class="forget" colspan="2">
              <a @click="goToSent">忘記密碼？</a>
              </td>
            </tr>
          </tbody>
        </table>
        <button class="submit" type="submit" @click="login">登入</button>
        <p v-if="message" class="error-message">{{ message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import useUserStore from "@/stores/user.js";
import axiosapi from "@/plugins/axios.js";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { nextTick } from "vue";
import Swal from "sweetalert2";

const userStore = useUserStore();
const router = useRouter();
const account = ref("");
const password = ref("");
const message = ref("");
const selectedTab = ref("login"); // 預設選擇登入頁面

const goToRegister = () => {
  // 跳轉到註冊頁面
  router.push({ path: "/secure/register" });
};

const goToSent = () => {
  // 跳轉到忘記密碼頁面
  router.push({ path: "/secure/sent" });
};

async function login() {
  message.value = "";

  // 檢查空的欄位，設置為 null
  if (!account.value || !password.value) {
    message.value = "請輸入帳號和密碼！";
    return;
  }

  const body = {
    account: account.value,
    password: password.value,
  };

  // 清除舊的授權資訊
  axiosapi.defaults.headers.authorization = "";

  userStore.setAccount("");

  try {

    // 發送登入請求
    const response = await axiosapi.post("/secure/login", body);

    if (response.data.success) {

      // 登入成功，保存授權資訊
      localStorage.setItem("authToken", response.data.token);
      localStorage.setItem("account", response.data.account);

      
      // 設置 axios 預設授權標頭
      axiosapi.defaults.headers.authorization = "Bearer " + response.data.token;


      // 更新 Pinia 狀態
      userStore.setAccount(response.data.account);
      userStore.setToken(response.data.token);


      // 等待 Pinia 更新狀態
      await nextTick(() => {
        // 確認是否有保存的 redirectTo 路徑
        const redirectTo = localStorage.getItem("redirectTo") || "/secure/person";
        if (redirectTo) {
          router.push(redirectTo); // 導向原本的頁面
          localStorage.removeItem("redirectTo"); // 清除儲存的路徑
        } else {
          // 沒有 redirectTo，默認跳轉到個人資料頁面
          router.push({ path: "/secure/person" });
        }
      });
    } else {
      // 登入失敗，顯示錯誤訊息
      message.value = response.data.message || "登入失敗，請稍後再試！";
      // 顯示 SweetAlert2 錯誤訊息
      Swal.fire({
      icon: 'error',
      title: '登入失敗。',
      text: '登入失敗，請稍後再試！',
      confirmButtonColor: '#0e0e0e'
    });
    }
  } catch (error) {
    // 捕獲錯誤，顯示錯誤訊息
    console.error("登入時發生錯誤:", error);
    message.value = "系統錯誤，請稍後再試！";
  }
}
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
  margin-bottom: 10px; /* 表格與按鈕之間的間距 */
}

td {
  padding: 10px;
  border: none;
}

tr {
  text-align: left;
  margin-bottom: 5px; /* 調整每個 tr 之間的間隔 */
}

td.active {
  border-radius: 10px 0 0 0; /* 設定圓角邊框 */
  background-color: #a7a2a2;
  color: white;
}

/* 讓第二至第六行的 input 具備相同的樣式 */
tr:nth-child(2) td:nth-child(2) input,
tr:nth-child(3) td:nth-child(2) input{
  width: 95%; /* 設定 input 寬度為 100% 減去左右邊距 */
  padding: 8px; /* 設定內邊距，讓文字不會直接貼著邊框 */
  border: 1px lightgray solid; /* 邊框寬度、樣式及顏色合併 */
  border-radius: 4px; /* 設定圓角邊框 */
  margin-right: 10px; /* input 框右側與 div 保持距離 */
}

#td1,
#td2 {
  text-align: center; /* 讓文字水平置中 */
  height: 60px;
  width: 50%;
}

#td2 {
  cursor: pointer; /* 鼠標樣式 */
}

#td2:hover {
  background-color: #f8961e;
  color: white;
  border-radius: 0 10px 0 0; /* 設定圓角邊框 */
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

.forget{
  text-align: right; /* 讓文字水平置中 */
  padding-right: 30px;
  color: rgb(134, 130, 130);
}
a{
  cursor: pointer; /* 鼠標樣式 */
  font-size: 14px;
}
</style>
