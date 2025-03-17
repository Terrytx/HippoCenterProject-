<template>
  <div>
    <PersonSidebar />
    <div class="main-content">
      <h3>節目收藏</h3>
      <div class="ticket">
        <!-- 節目列表 -->
        <table class="ticket-list">
          <thead>
            <tr>
              <th @click="toggleDateSort">展演日期
                  <i :class="dateSortOrder === 'desc' ? 'fas fa-sort-down' : 'fas fa-sort-up'"></i>
                </th>
              <th>展演名稱</th>
              <th>展演價格</th>
              <th>取消收藏</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(event, index) in events" :key="event.myloveId">
              <td>{{ event.eventStartDate }} ~ {{ event.eventEndDate }}  </td>
              <td @click="goToEventDetail(event.myloveId)">
                <!-- 展演名稱是超連結 -->
                <a class="event-link">
                  {{ event.event_name }}
                </a>
              </td>
              <td>{{ event.event_price }}</td>
              <td>
                <!-- 收藏按鈕 -->
                <button @click="toggleFavorite(event)">
                  ❤
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import PersonSidebar from '@/components/navbar/b4member/PersonSidebar.vue';
import axiosapi from "@/plugins/axios.js";
import { ref, onMounted } from 'vue';
import Swal from 'sweetalert2';
import { useRouter } from 'vue-router'

const router = useRouter()
const events = ref([]);  // 存儲節目數據
const loading = ref(true);  // 載入狀態
const errorMessage = ref("");  // 錯誤訊息
// 用來控制排序的狀態，'asc' 是正序，'desc' 是倒序
const dateSortOrder = ref('desc'); 

// 從後端 API 獲取收藏的節目並排序
const fetchFavoriteShows = async () => {
  const token = localStorage.getItem('authToken');  // 從本地儲存或 cookie 取得 Token
  if (!token) {
    errorMessage.value = "未找到有效的授權 Token";
    loading.value = false;
    return;
  }

  try {
    const response = await axiosapi.get(`/secure/mylove/e`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    if (response.data.list) {
      // 先將數據映射並排序，按照展演日期倒序
      events.value = response.data.list.map(item => ({
        myloveId: item.myloveId,
        event_name: item.name,
        eventStartDate: item.eventStartDate,
        eventEndDate: item.eventEndDate,
        event_price: item.price,
        isFavorite: true  // 設定為已收藏
      }));

      // 根據 'event_start_date' 進行倒序排序
      events.value.sort((a, b) => new Date(b.eventStartDate) - new Date(a.eventStartDate));

    } else {
      errorMessage.value = "沒有找到收藏的節目";
    }
  } catch (error) {
    errorMessage.value = "取得收藏的節目資料時發生錯誤";
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 修改導航函數，直接使用 myloveId
const goToEventDetail = (myloveId) => {
  console.log('Navigating to event:', myloveId); // 調試用
  
  if (!myloveId) {
    console.error('No event ID provided');
    Swal.fire({
      icon: 'error',
      title: '錯誤',
      text: '無法找到該節目的詳細資訊',
      confirmButtonText: '確定',
      confirmButtonColor: '#0e0e0e'
    });
    return;
  }

  try {
    router.push({
      name: 'EventDetail',
      params: { 
        id: myloveId.toString() // 直接使用 myloveId
      }
    });
  } catch (error) {
    console.error('Navigation error:', error);
    Swal.fire({
      icon: 'error',
      title: '導航錯誤',
      text: '無法前往節目詳情頁面，請稍後再試',
      confirmButtonText: '確定',
      confirmButtonColor: '#0e0e0e'
    });
  }
};

// 排序節目列表
const sortEventsByDate = () => {
  events.value.sort((a, b) => {
    const dateA = new Date(a.eventStartDate);
    const dateB = new Date(b.eventStartDate);
    
    if (dateSortOrder.value === 'desc') {
      return dateB - dateA; // 倒序
    } else {
      return dateA - dateB; // 正序
    }
  });
};

// 切換排序順序
const toggleDateSort = () => {
  dateSortOrder.value = dateSortOrder.value === 'desc' ? 'asc' : 'desc';
  sortEventsByDate(); // 每次切換後重新排序
};

// 切換收藏狀態
const toggleFavorite = async (event) => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    errorMessage.value = "未找到有效的授權 Token";
    return;
  }

  try {
    const response = await axiosapi.post(`/secure/mylove/toggle/e`, {
      myloveId: event.myloveId  // 傳送節目的 myloveId
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    if (response.data.message) {
      // 更新收藏狀態
      event.isFavorite = !event.isFavorite;
      // 如果是取消收藏，從 events 列表中刪除該節目
      if (!event.isFavorite) {
        events.value = events.value.filter(e => e.myloveId !== event.myloveId);
      }
      
      Swal.fire({
        icon: 'success',
        title: event.isFavorite ? '加入收藏成功' : '取消收藏成功',
        text: `${event.event_name} 已移除收藏！`,
        confirmButtonText: '確定',
        confirmButtonColor: '#0e0e0e'
      });
    } else {
      errorMessage.value = "操作失敗，請稍後再試";
    }
  } catch (error) {
    console.error(error);
    errorMessage.value = "操作時發生錯誤";
  }
};

// 初始化加載收藏的節目
onMounted(() => {
  fetchFavoriteShows();
});
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

/* 展演名稱超連結樣式 */
.event-link {
  color: #007bff;
  text-decoration: none;
}

.event-link:hover {
  text-decoration: underline;
  color: #f8961e;
  cursor: pointer;
}

button {
  padding: 8px 16px;
  border: none;
  cursor: pointer;
  background-color: #ff6347;
  color: white;
  border-radius: 4px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #e9533b;
}

.error-message {
  color: red;
  font-size: 14px;
}
i{
  cursor: pointer;
}

</style>
