<template>
  <div>
    <div>
      <PersonSidebar />
      <div class="main-content">
        <h3>個人資料</h3>
        <div class="person">
        
        <!-- 顯示資料 -->
        <table class="out-table">
          <tbody>
            <tr class="out-tr">
              <td class="out-td">Email帳號</td>
              <td class="out-td">{{ account }}</td>
            </tr>
            <tr class="out-tr">
              <td class="out-td">姓名</td>
              <td class="out-td">{{ name }}</td>
            </tr>
            <tr class="out-tr">
              <td class="out-td">性別</td>
              <td class="out-td">{{ gender }}</td>
            </tr>
            <tr class="out-tr">
              <td class="out-td">生日</td>
              <td class="out-td">{{ birthday }}</td>
            </tr>
            <tr class="out-tr">
              <td class="out-td">電話</td>
              <td class="out-td">{{ phone }}</td>
            </tr>
            <tr class="out-tr">
              <td class="out-td">地址</td>
              <td class="out-td">{{ address }}</td>
            </tr>
          </tbody>
        </table>

                <!-- 編輯按鈕 -->
                <button class="out-edit" type="button" @click="openModal">✎ 編輯個人資料 </button>

        <!-- 模態視窗 -->
          <div v-if="isModalOpen" class="modal-overlay">
            <div class="modal-content">
              <h3>編輯資料</h3>
              <form @submit.prevent="saveChanges">
                <table>
                  <tbody>
                    <tr>
                      <td><label for="account">Email帳號</label></td>
                      <td><label for="account">{{account}}</label></td>
                    </tr>
                    <tr>
                      <td><label for="name">姓名</label></td>
                      <td><input type="text" id="name" v-model="editableData.name" /></td>
                    </tr>
                    <tr>
                      <td><label for="gender">性別</label></td>
                      <td>
                        <select id="gender" v-model="editableData.gender">
                          <option value="男">男</option>
                          <option value="女">女</option>
                          <option value="不提供">不提供</option>
                        </select>
                      </td>
                    </tr>
                    <tr>
                      <td><label for="birthday">生日</label></td>
                      <td><input type="date" id="birthday" v-model="editableData.birthday" /></td>
                    </tr>
                    <tr>
                      <td><label for="phone">電話</label></td>
                      <td><input type="text" id="phone" v-model="editableData.phone" /></td>
                    </tr>
                    <tr>
                      <td><label for="address">地址</label></td>
                      <td><input type="text" id="address" v-model="editableData.address" /></td>
                    </tr>
                  </tbody>
                </table>
                <table class="in-down-table">
                  <tbody>
                    <tr>
                      <td colspan="2">
                        <button class="in-cancel" type="button" @click="closeModal">取消</button>
                        <button class="in-submit" type="submit">儲存</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
  
<script setup>
  import PersonSidebar from '@/components/navbar/b4member/PersonSidebar.vue';
  import useUserStore from '@/stores/user.js';
  import axiosapi from '@/plugins/axios.js';
  import { ref, reactive, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import Swal from 'sweetalert2';

  const userStore = useUserStore(); // 使用自定義的用戶狀態管理
  const router = useRouter(); // 路由管理
  const isModalOpen = ref(false); // 控制模態視窗開關
  const errorMessage = ref(""); // 用來顯示錯誤信息

  // 初始化資料
  const account = ref("");
  const name = ref("");
  const gender = ref("");
  const birthday = ref("");
  const phone = ref("");
  const address = ref("");
  const loading = ref(true); // 資料加載狀態
  const userData = ref({}); // 儲存用戶資料

  async function fetchUserData() {
    try {
        const token = localStorage.getItem('authToken'); // 從 localStorage 獲取 token
        if (!token) {
            errorMessage.value = "Token 缺失，請重新登入。";
            return;
        }

        // 設置 axios 預設的 Authorization 標頭
        axiosapi.defaults.headers.authorization = `Bearer ${token}`;

        // 發送 GET 請求獲取用戶資料
        const response = await axiosapi.get("/secure/person");
        if (response.data.list && response.data.list.length > 0) {
            const user = response.data.list[0]; // 假設只有一條資料
            account.value = user.account;
            name.value = user.name;
            gender.value = user.gender || ''; 
            birthday.value = user.birthday || '';
            phone.value = user.phone || '';
            address.value = user.address || '';

            // 更新 editableData
            account.value = user.account;
            editableData.name = user.name;
            editableData.gender = user.gender || '';
            editableData.birthday = user.birthday || '';
            editableData.phone = user.phone || '';
            editableData.address = user.address || '';
        } else {
            errorMessage.value = "無法獲取用戶資料。";
        }
    } catch (error) {
        console.error("獲取資料失敗:", error);
        errorMessage.value = "載入個人資料失敗，請再試一次。";
    } finally {
        loading.value = false; // 停止 loading 狀態
    }
}


  // 開啟模態視窗
const openModal = () => {
    isModalOpen.value = true;
};

// 關閉模態視窗
const closeModal = () => {
    isModalOpen.value = false;
};

  // 儲存修改的資料
const saveChanges = () => {
    // 將 editableData 中的資料同步到顯示資料的變數
    name.value = editableData.name;
    gender.value = editableData.gender;
    birthday.value = editableData.birthday;
    phone.value = editableData.phone;
    address.value = editableData.address;

    // 發送 API 請求保存資料
    axiosapi.post("/secure/person", editableData)
        .then(response => {
            if (response.data.success) {
              Swal.fire({
              icon: 'success',
              title: '個人資料修改',
              text: `個人資料修改已成功`,
              confirmButtonText: '確定',
              confirmButtonColor: '#0e0e0e'
          })
            } else {
              Swal.fire({
              icon: 'error',
              title: '個人資料修改',
              text: `個人資料修改失敗，請再試一次`,
              confirmButtonText: '確定',
              confirmButtonColor: '#0e0e0e'
          })
            }
        })
        .catch(error => {
            console.error("發送請求時發生錯誤:", error);
            Swal.fire({
              icon: 'error',
              title: '發送請求時發生錯誤',
              text: `發送請求時發生錯誤，請再試一次`,
              confirmButtonText: '確定',
              confirmButtonColor: '#0e0e0e'
          })
        });

    // 關閉模態視窗
    closeModal();
};

onMounted(() => {
    fetchUserData(); // 初始化資料
});

// 定義 reactive 的資料模型
const editableData = reactive({
  name: name.value,
  gender: gender.value,
  birthday: birthday.value,
  phone: phone.value,
  address: address.value
});


</script>


<style scoped>
@import '@/assets/styles/main.css';
@import '@/assets/styles/mainContent.css';


.main-content {
  padding-top: 15px;
  min-height: 700px; /* 設定最小高度為600px（根據需求調整） */
}


.person-info {
  margin-top: 20px;
}

.out-table {
  margin-top: 30px;
  font-weight: bold;
  font-size: 22px; /* 增大字體大小 */
  width: 50%;
  border-collapse: collapse; /* 設定表格邊框折疊 */
  border: none; /* 移除整個表格的邊框 */
}

.out-tr {
  padding: 15px 0; /* 調整每一行的間距 */
  /* 移除底線 */
  border-bottom: none; /* 不顯示底線 */
}

.out-td {
  padding: 15px;
  text-align: left;
  font-size: 20px; /* 增大字體大小 */
}

.out-td:first-child {
  font-weight: normal; /* 去掉第一欄字體的粗體效果 */
}

.out-edit {
  border: none;
  background: none;
  cursor: pointer;
  width: 200px;
  height: 40px;
  box-sizing: border-box;
  font-size: 18px;
  font-weight: 600;
  background-color: #f8961e;
  color: white;
  margin-top: 30px;
  border-radius: 4px;
}

.in-submit, .in-cancel {
  background-color: #f8961e;
  color: white;
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  width: 40%;
  margin-top: 20px;
}

.in-cancel {
  background-color: #8a8a8a;
}

.in-down-table {
  text-align: center;
  margin-top: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  width: 600px; /* 更寬的寬度 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

form input,
form select {
  width: 100%;
  padding: 12px;
  margin: 8px 0;
  border: 1px solid #ccc;
  border-radius: 4px;
}

form td {
  padding: 15px 0; /* 調整每一行的間距 */
}

form label {
  font-weight: bold;
}

button:hover {
  background-color: #2b2b2b;
}

form table {
  width: 100%;
  margin-bottom: 20px;
}

form td {
  padding: 15px; /* 讓每個表格的單元格都有足夠的間距 */
}

form input::placeholder, form select {
  color: #888;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

/* 重設日期選擇器的基本樣式 */
input[type="date"] {
  appearance: none; /* 移除瀏覽器預設的樣式 */
  -webkit-appearance: none; /* 防止Safari的預設樣式 */
  -moz-appearance: none; /* 防止Firefox的預設樣式 */
  background-color: #fff; /* 背景為白色 */
  border: 1px solid #ccc; /* 輕微的灰色邊框 */
  padding: 10px 15px; /* 內邊距 */
  font-size: 16px; /* 字體大小 */
  border-radius: 5px; /* 圓角 */
  outline: none; /* 移除聚焦時的邊框 */
  transition: border-color 0.3s ease; /* 加入過渡效果 */
}

/* 聚焦時的邊框顏色 */
input[type="date"]:focus {
  border-color: #f8961e; /* 聚焦時顯示橘色邊框 */
  box-shadow: 0 0 5px rgba(255, 140, 0, 0.3); /* 聚焦時加上陰影 */
}

/* 滑鼠懸停時的邊框顏色 */
input[type="date"]:hover {
  border-color: #888; /* 滑鼠懸停時邊框變深 */
}

/* 為日期選擇器添加更簡約的外觀 */
input[type="date"]::-webkit-calendar-picker-indicator {
  background-color: #f8961e; /* 按鈕背景色 */
  border-radius: 50%; /* 圓形按鈕 */
  padding: 5px;
  cursor: pointer;
}

/* 簡約風格的日期選擇框 */
input[type="date"]::-webkit-datetime-edit {
  font-size: 16px;
  color: #333;
  background-color: transparent;
  border: none;
}

/* 在 Firefox 中自定義日期選擇器 */
input[type="date"]::-moz-calendar-picker-indicator {
  background-color: #f8961e; /* 按鈕背景色 */
  border-radius: 50%; /* 圓形按鈕 */
  cursor: pointer;
}

</style> 