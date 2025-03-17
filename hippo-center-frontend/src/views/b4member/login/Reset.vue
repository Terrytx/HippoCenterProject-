<template>
  <div>
    <h3>重設密碼</h3>
    <div class="main-content">
      <div class="center-div">
        <table>
          <tbody>
            <tr id="tr1">
              <td colspan="2">※以下所有欄位均為必填</td>
            </tr>
            <tr>
              <td>驗證碼</td>
              <td>
                <input type="text" placeholder="請輸入驗證碼" v-model="resetCode" />
              </td>
            </tr>
            <tr>
              <td>新密碼</td>
              <td>
                <input type="password" placeholder="請輸入新密碼" v-model="newPassword" />
              </td>
            </tr>
            <tr>
              <td>確認密碼</td>
              <td>
                <input type="password" placeholder="請再次輸入密碼" v-model="confirmPassword" />
              </td>
            </tr>
          </tbody>
        </table>
        <button class="submit" type="submit" @click="submitResetPassword">確認更改密碼</button>
        <p v-if="message" class="error-message">{{ message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axiosapi from '@/plugins/axios.js';
import Swal from "sweetalert2";

// 用來獲取路由參數
const route = useRoute();
const router = useRouter();

const resetCode = ref(""); // 用戶輸入的驗證碼
const newPassword = ref(""); // 用戶輸入的新密碼
const confirmPassword = ref(""); // 用戶輸入的確認密碼
const message = ref(""); // 顯示錯誤訊息

// 用於從 URL 取得 uuid 參數
const uuid = ref("");

onMounted(() => {
  // 從 URL 獲取 uuid 參數，這裡應該使用 route.params.uuid
  uuid.value = route.params.uuid;
  console.log(`uuid.value: ${uuid.value}`);

  // 檢查是否缺少 uuid 參數
  if (!uuid.value) {
    Swal.fire({
              icon: 'error',
              title: '無效的鏈接',
              text: `無效的鏈接，請檢查郵件中的連結。`,
              confirmButtonText: '確定',
              confirmButtonColor: '#0e0e0e'
          })
    router.push("/secure/login"); // 跳轉到登入頁面
  }
});

const submitResetPassword = async () => {
  // 檢查驗證碼是否輸入
  if (!resetCode.value || !newPassword.value || !confirmPassword.value) {
    message.value = "請輸入所有資料！";
    return;
  }


  // 檢查新密碼和確認密碼是否匹配
  if (newPassword.value !== confirmPassword.value) {
    message.value = "新密碼與確認密碼不一致！";
    return;
  }

  try {
    // 發送請求給後端進行密碼重設
    const response = await axiosapi.post("/user/resetPassword", {
      uuid: uuid.value,       // 提供從 URL 獲取的 uuid
      resetCode: resetCode.value, // 提供用戶輸入的驗證碼
      newPassword: newPassword.value, // 提供用戶輸入的新密碼
    });

    // 密碼重設成功
    Swal.fire({
              icon: 'success',
              title: '密碼重設成功！',
              text: `密碼重設成功！請重新登入`,
              confirmButtonText: '確定',
              confirmButtonColor: '#0e0e0e'
          })
    router.push("/secure/login"); // 跳轉到登入頁面
  } catch (error) {
    // 捕獲錯誤並顯示錯誤訊息
    if (error.response && error.response.data) {
      // 從後端的響應中獲取錯誤訊息
      message.value = error.response.data;  // 假設後端返回的錯誤訊息在 response body 中
    } else {
      message.value = "發生錯誤，請稍後再試。"; // 其他未知錯誤
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
  margin-top: 20px;
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
  background-color: #a7a2a2;
  color: white;
}

/* 讓第二至第六行的 input 具備相同的樣式 */
tr:nth-child(2) td:nth-child(2) input,
tr:nth-child(3) td:nth-child(2) input,
tr:nth-child(4) td:nth-child(2) input{
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
