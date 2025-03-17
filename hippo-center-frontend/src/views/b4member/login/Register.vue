<template>
  <div>
    <h3>會員註冊</h3>
    <div class="main-content">
      <div class="center-div">
        <table class="change">
          <tbody>
            <tr>
              <td id="td1" :class="{'active': selectedTab === 'login'}"
              @click="goToLogin">登入</td>
              <td id="td2" :class="{'active': selectedTab === 'register'}"
              @click="selectTab('register')">註冊</td>
            </tr>
          </tbody>
        </table>
        <table>
          <tbody>
            <tr>
              <td colspan="2">※以下所有欄位均為必填</td>
            </tr>
            <tr>
              <td @keydown.enter="register"> 姓名</td>
              <td><input type="text" placeholder="請輸入姓名" v-model="name" /></td>
            </tr>
            <tr>
              <td @keydown.enter="register">帳號</td>
              <td><input type="text" placeholder="請輸入Email" v-model="account" /></td>
            </tr>
            <tr>
              <td @keydown.enter="register">密碼</td>
              <td><input type="password" placeholder="請輸入密碼" v-model="password"/></td>
            </tr>
            <tr>
              <td></td>
              <td>8~12 字元，包含至少 1 個數字和 1 個英文字母</td>
            </tr>
            <tr>
              <td @keydown.enter="register">確認密碼</td>
              <td><input type="password" placeholder="請再次輸入密碼" v-model="confirmPassword" /></td>
            </tr>
            <tr>
              <td @keydown.enter="register"></td>
              <td><input type="checkbox" v-model="isChecked"/>我同意　<a @click="goToPolicy">隱私權政策</a>　和　<a @click="goToTerms">會員條款</a></td>
            </tr>
          </tbody>
        </table>
        <button class="submit" type="submit" @click="register()">註冊</button>
      </div>
    </div>
</div>
</template>

<script setup>
    import useUserStore from '@/stores/user.js'
    import axiosapi from '@/plugins/axios.js'
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    import Swal from "sweetalert2";

    const userStore = useUserStore()
    const confirmPassword = ref('');
    const router = useRouter()
    const name = ref("")
    const account = ref("");
    const password = ref("");
    const passwordError = ref('');
    const message = ref("");
    const selectedTab = ref('register');  // 預設選擇註冊頁面
    const isChecked = ref(false); // 新增 checkbox 的勾選狀態

    const goToLogin = () => {
      // 跳轉到登入頁面
      router.push({ path: '/secure/login' });
    }
    
    const goToPolicy = () => {
      // 跳轉到隱私權頁面
      window.open('/secure/policy', '_blank');
    }

    const goToTerms = () => {
      // 跳轉到隱私權頁面
      window.open('/secure/terms', '_blank');
    }

    // 註冊邏輯
    async function register() {
      message.value = '';
    passwordError.value = '';

    console.log('開始註冊流程');
    console.log('當前輸入值：', {
        密碼: password.value,
        確認密碼: confirmPassword.value,
        條款勾選: isChecked.value
    });

    // 檢查空的欄位
    if (!name.value || !account.value || !password.value || !confirmPassword.value) {
        console.log('欄位檢查失敗：有空欄位');
        message.value = '所有欄位均為必填';
        Swal.fire({
            icon: 'warning',
            title: '所有欄位均為必填',
            text: '所有欄位均為必填'
        });
        return;
    }

    // 檢查 Email 格式
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(account.value)) {
        console.log('Email 格式檢查失敗');
        message.value = '請輸入有效的 Email 格式';
        Swal.fire({
            icon: 'warning',
            title: '無效的 Email 格式',
            text: '請輸入有效的 Email 格式',
            confirmButtonColor: '#0e0e0e'
        });
        return;
    }

    // 密碼驗證條件：8~12 字元，至少 1 個數字和 1 個英文字母
    const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,12}$/;
    console.log('密碼規則檢查：', regex.test(password.value));

    if (!regex.test(password.value)) {
        console.log('密碼格式檢查失敗');
        passwordError.value = '密碼必須是 8~12 字元，且至少包含 1 個數字和 1 個英文字母';
        Swal.fire({
            icon: 'warning',
            title: '密碼格式不符',
            text: '密碼必須是 8~12 字元，且至少包含 1 個數字和 1 個英文字母',
            confirmButtonColor: '#0e0e0e'
        });
        return;
    }

    // 檢查密碼是否匹配
    console.log('檢查密碼匹配：', password.value === confirmPassword.value);
    
    if (password.value !== confirmPassword.value) {
        console.log('密碼匹配檢查失敗');
        message.value = '密碼與確認密碼不一致';
        Swal.fire({
            icon: 'warning',
            title: '密碼與確認密碼不一致',
            text: '密碼與確認密碼不一致',
            confirmButtonColor: '#0e0e0e'
        });
        return;
    }

    // 檢查是否同意條款
    console.log('檢查條款同意：', isChecked.value);
    
    if (!isChecked.value) {
        console.log('條款同意檢查失敗');
        message.value = '您必須同意隱私權政策及會員條款';
        Swal.fire({
            icon: 'warning',
            title: '您必須同意隱私權政策及會員條款',
            text: '您必須同意隱私權政策及會員條款',
            confirmButtonColor: '#0e0e0e'
        });
        return;
    }

    console.log('所有驗證通過，準備發送註冊請求');

    const body = {
        name: name.value,
        account: account.value,
        password: password.value,
    };
    console.log('準備發送註冊請求，數據為:', body);
    console.log('API 端點:', '/secure/register');

    try {
      // 發送註冊請求
      console.log('正在發送註冊請求...');
      const response = await axiosapi.post('/secure/register', body);
      console.log('收到註冊響應:', response);
      if (response.data.success) {
        // 註冊成功，跳轉到
        console.log('註冊成功:', response.data.message);
        router.push({ path: '/secure/login' });
        Swal.fire({
        icon: 'success',
        title: '註冊成功。',
        text: '註冊成功，請重新登入。',
        confirmButtonColor: '#0e0e0e'
        });
      } else {
        // 註冊失敗，顯示錯誤訊息
        message.value = response.data.message;
        console.log('註冊失敗:', response.data.message);
        Swal.fire({
        icon: 'error',
        title: '註冊失敗',
        text: `註冊失敗，${response.data.message}`,
        confirmButtonColor: '#0e0e0e'
        });
      }
    } catch (error) {
      // 捕獲錯誤，顯示錯誤訊息
      console.log('錯誤:', error);
      message.value = '執行失敗，請再試一次';
    }
  }
</script>

<style scoped>
@import '@/assets/fonts/font.css';
@import '@/assets/styles/mainContent.css';


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


h3 {
  display: flex;
  align-items: center;
  width: 500px;
  margin-right: auto;
  margin-left: 11%;
}


/* center-div：包住 table 和 button，並將內容居中 */
.center-div {
  width: 30%;
  height: auto;
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
  background-color: #a7a2a2;
  color: white;
  border-radius: 0 10px 0 0; /* 設定圓角邊框 */
}

/* 讓第二至第六行的 input 具備相同的樣式 */
tr:nth-child(2) td:nth-child(2) input,
tr:nth-child(3) td:nth-child(2) input,
tr:nth-child(4) td:nth-child(2) input,
tr:nth-child(5) td:nth-child(2) input,
tr:nth-child(6) td:nth-child(2) input {
  width: 95%; /* 設定 input 寬度為 100% 減去左右邊距 */
  padding: 8px; /* 設定內邊距，讓文字不會直接貼著邊框 */
  border: 1px lightgray solid; /* 邊框寬度、樣式及顏色合併 */
  border-radius: 4px; /* 設定圓角邊框 */
  margin-right: 10px; /* input 框右側與 div 保持距離 */
}


/* 使 a 標籤保持在同一行，並且樣式正確 */
tr:nth-child(6) td:nth-child(2) {
  display: flex;                  /* 使用 Flexbox 排列元素 */
  align-items: center;            /* 垂直居中對齊 */
  gap: 10px;                       /* 元素之間的間隔 */
}

tr:nth-child(6) td:nth-child(2) a {
  text-decoration: underline;
  color: #007bff;                 /* 設置鏈接顏色 */
}

tr:nth-child(6) td:nth-child(2) a:hover {
  color: #f8961e;                 /* 懸停時改變顏色 */
}

.submit {
  background-color: #f8961e;
  color: white;
  padding: 10px 20px;        /* 內邊距 */
  font-size: 16px;           /* 字體大小 */
  border-radius: 5px;        /* 邊框圓角 */
  border: none;              /* 去掉邊框 */
  cursor: pointer;          /* 鼠標樣式 */
}

.submit:hover {
  background-color: rgb(51, 49, 46); /* 懸停時的背景顏色 */
  transform: scale(1);     /* 放大按鈕效果 */
  transition: all 0.3s ease; /* 讓變化效果平滑過渡 */
}

/* 更精細調整表格中的 td 和 tr */
#td1 {
  text-align: center; /* 讓文字水平置中 */
  height: 60px;
  cursor: pointer;          /* 鼠標樣式 */
}

#td2 {
  text-align: center; /* 讓文字水平置中 */
  height: 60px;
}

#td1:hover {
  background-color: #f8961e;
  color: white;
  border-radius: 10px 0 0 0; /* 設定圓角邊框 */
}

.change {
  padding: 0;
  margin: 0;
  border-bottom: 1px lightgrey solid;
}

.center-div a {
  text-decoration: underline !important; /* 強制顯示底線 */
  color: #007bff;
  cursor: pointer;
}

.center-div a:hover {
  color: #f8961e !important;
  text-decoration: underline !important;
}


tr:nth-child(6) td:nth-child(2) a {
  text-decoration: underline !important;
  color: #007bff;
}

tr:nth-child(6) td:nth-child(2) a:hover {
  color: #f8961e;
}
</style>
