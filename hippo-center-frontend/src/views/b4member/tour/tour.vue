<template>
  <div>
    <div>
      <PersonSidebar />
      <div class="main-content">
        <h3>導覽預約</h3>
        <div class="person">
          <!-- 預約導覽表格 -->
    <table class="tour-list">
      <thead>
        <tr>
          <th @click="sortData('toursDate')">導覽日期<i :class="getSortIcon('toursDate')"></i></th>
          <th>導覽時間</th>
          <th>導覽名稱</th>
          <th>預約人數</th>
          <th @click="sortData('reservationStatus')">預約狀態<i :class="getSortIcon('reservationStatus')"></i></th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(reservation, index) in reservations" :key="reservation.reservationId">
          <td>{{ reservation.toursDate  }}</td>
          <td>{{ reservation.timeSlot }}</td>
          <td>{{ reservation.toursName }}</td>
          <td>{{ reservation.peopleCount }}</td>
          <td>{{ reservation.reservationStatus  }}</td>
          <td>
            <button @click="openEditModal(index)" 
              class="edit-btn" 
              :disabled="reservation.reservationStatus === '已過期' || new Date(reservation.toursDate) < new Date()">
              修改人數</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 修改人數的模擬視窗 -->
    <div v-if="isModalVisible" class="modal-overlay">
      <div class="modal-content">
        <h2>修改預約資訊</h2>
        <div class="modal-fields">
          <div>
            <label for="toursDate">導覽日期：</label>
            <input type="text" v-model="editedTour.toursDate" disabled />
          </div>
          <div>
            <label for="timeSlot">導覽時間：</label>
            <input type="text" v-model="editedTour.timeSlot" disabled />
          </div>
          <div>
            <label for="toursName">導覽名稱：</label>
            <input type="text" v-model="editedTour.toursName" disabled />
          </div>
          <div>
            <label for="peopleCount">預約人數：</label>
            <input type="number" v-model="editedTour.peopleCount" min="1" />
          </div>
          <div>
            <label for="reservationStatus">預約狀態：</label>
            <input type="text" v-model="editedTour.reservationStatus" disabled />
          </div>
        </div>
        <div class="modal-actions">
          <button @click="saveChanges">保存</button>
          <button @click="closeModal">取消</button>
        </div>
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
  import { ref, onMounted } from 'vue';
  import Swal from 'sweetalert2';

  const userStore = useUserStore(); // 使用自定義的用戶狀態管理
  const isModalVisible = ref(false); // 控制模態視窗開關
  const editedTour = ref(null);
  const isLoading = ref(false); // 控制加載狀態
  const errorMessage = ref(""); // 顯示錯誤信息
  const reservations = ref([]); // 用來存儲預約資料
  const sortKey = ref('toursDate'); // 當前排序的欄位
  const sortOrder = ref(1); // 1 是升序，-1 是降序

  // 排序圖示
const getSortIcon = (field) => {
  if (sortOrder.value.field === field) {
    return sortOrder.value.direction === 1 ? "fas fa-sort-up" : "fas fa-sort-down";
  }
  return "fas fa-sort";  // 默認排序圖示
};

   // 排序函數
    function sortData(key) {
    if (sortKey.value === key) {
      // 如果點擊相同的標題，則反轉排序順序
      sortOrder.value = sortOrder.value === 1 ? -1 : 1;
    } else {
      // 如果點擊不同的標題，則默認升序
      sortKey.value = key;
      sortOrder.value = 1;
    }

    reservations.value.sort((a, b) => {
      let valA = a[key];
      let valB = b[key];

      if (key === 'toursDate') {
        // 如果排序的是日期，將其轉換為 Date 物件進行比較
        valA = new Date(valA);
        valB = new Date(valB);
      }

      // 根據 sortOrder 決定升序或降序
      return sortOrder.value * (valA > valB ? 1 : valA < valB ? -1 : 0);
    });
  }

  // 當元件掛載時，從 localStorage 讀取 account 並呼叫 API 查詢預約資料
  onMounted(async () => {
      const account = userStore.account; // 從 store 讀取 account
      console.log(account)
      if (account) {
        try {
          isLoading.value = true; // 開始載入
          const response = await axiosapi.post(`/admin/reservations/findmember`, 
          { memberAccount: account, // 傳送 account 作為請求體的一部分
          });
          console.log(response.data)
          if (response.status === 200) {
            // 更新預約資料並設置狀態
          reservations.value = response.data.map(reservation => {
          const toursDate = new Date(reservation.toursDate); // 假設 toursDate 是可以被轉換成日期格式的字符串
          const currentDate = new Date();
          console.log('toursDate'+toursDate)
          console.log('currentDate'+currentDate)
          // 比較日期，設置 reservationStatus
          // 只比較年月日，忽略時間
  const isSameDay = toursDate.toDateString() === currentDate.toDateString();
  const isFutureDate = toursDate > currentDate;

  if (isSameDay || isFutureDate) {
    reservation.reservationStatus = '已預約';
  } else {
    reservation.reservationStatus = '已過期';
  }
  return reservation;
});
            console.log(response.data)
            // 排序：依照導覽日期 (toursDate) 進行升序排列
            reservations.value = response.data; // 更新預約資料
            reservations.value.sort((a, b) => new Date(a.toursDate) - new Date(b.toursDate));
          } else {
            errorMessage.value = "無法取得預約資料。";
          }
        } catch (error) {
          errorMessage.value = "請求錯誤，請稍後再試。";
        } finally {
          isLoading.value = false; // 載入結束
        }
      } else {
        errorMessage.value = "未找到會員ID，請先登入。";
      }
    });

  // 開啟編輯模態視窗
  function openEditModal(index) {
    const reservation = reservations.value[index];
    if (reservation) {
      editedTour.value = {
        ...reservation
      };
  } else {
    editedTour.value = { ...reservation }; // 預防 tours 不存在的情況
  }
    isModalVisible.value = true;
  }

  // 關閉編輯模態視窗
  function closeModal() {
    isModalVisible.value = false;
  }

// 儲存修改的人數
async function saveChanges() {
  if (editedTour.value) {
    const index = reservations.value.findIndex(reservation => reservation.reservationId === editedTour.value.reservationId);
    
    if (index !== -1) {
      // 更新預約資料中的人數
      reservations.value[index].peopleCount = editedTour.value.peopleCount;

      // 準備傳送到後端的資料，只包含需要更新的欄位（人數）
      const modifiedReservation = {
        reservationId: editedTour.value.reservationId, // 輸送 ID 用於後端識別
        numGuests: editedTour.value.peopleCount         // 只更新人數
      };

      try {
        // 發送 PUT 請求到後端 API
        const response = await axiosapi.put('/user/reservations/modify', modifiedReservation, {
          headers: {
            'Content-Type': 'application/json'
          }
        });

        // 處理後端回應
        if (response.status === 200) {
          Swal.fire({
            icon: 'success',
            title: '預約更新成功',
            text: `人數已更新為${ modifiedReservation.numGuests }人`,
            confirmButtonText: '確定',
            confirmButtonColor: '#0e0e0e'
          })
          console.log('預約更新成功:', response.data);
        } else {
          console.error('預約更新失敗');
        }
      } catch (error) {
        console.error('請求錯誤:', error);
      }

      closeModal(); // 關閉模擬視窗
    }
  }
}
</script>


<style scoped>
@import '@/assets/styles/main.css';
@import '@/assets/styles/mainContent.css';


.main-content {
  padding-top: 15px;
  min-height: 700px; /* 設定最小高度為600px（根據需求調整） */
}

/* 表格樣式 */
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
.edit-btn {
  background-color: #f8961e;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.edit-btn:hover {
  background-color: #d75f00;
  color: #fff;
}

/* 禁用的按鈕 */
.edit-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 模擬視窗背景層 */
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
  z-index: 999; /* 背景層 */
}

/* 模擬視窗內容 */
.modal-content {
  background-color: #f4f4f4;
  padding: 30px;
  border-radius: 8px;
  width: 600px; /* 擴大視窗範圍 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  color: #333;
  max-height: 80vh; /* 限制最大高度為視窗的 80% */
  overflow-y: auto; /* 顯示滾動條 */
}

.modal-content h2 {
  text-align: center;
  color: #333;
}

.modal-fields {
  margin-top: 20px;
}

.modal-fields div {
  margin-bottom: 15px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  box-sizing: border-box;
}

input:disabled {
  background-color: #f0f0f0;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

button {
  padding: 8px 16px;
  border: none;
  cursor: pointer;
  background-color: #f8961e;
  color: white;
  border-radius: 4px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #e07b00;
}

i{
  cursor: pointer;
}
</style> 