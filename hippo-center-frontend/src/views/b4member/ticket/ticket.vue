<template>
  <div>
    <div>
      <PersonSidebar />
      <div class="main-content">
        <h3>票券資訊</h3>
        <div v-if="isLoading">載入中...</div>
        <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
        <div v-else class="ticket">
          <table class="ticket-list">
            <thead>
              <tr>
                <th>展演日期</th>
                <th>展演名稱</th>
                <th>購票數量</th>
                <th>展演價格</th>
                <th>購買日期</th>
                <th>票券狀態</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="ticket in currentMemberTickets" :key="ticket.ticketId">
                <td>{{ ticket.eventDate }}</td>
                <td @click="goToEventDetail(ticket.eventId)" class="event-link">{{ ticket.eventName }}</td>
                <td>{{ ticket.ticketAmount }}</td>
                <td>{{ ticket.eventPrice }}/張</td>
                <td>{{ ticket.purchaseDate }}</td>
                <td>{{ ticket.ticketStatus }}</td>
              </tr>
            </tbody>
          </table>
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
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';

const userStore = useUserStore();
const router = useRouter();
const isLoading = ref(false);
const errorMessage = ref("");
const currentMemberTickets = ref([])

// 取得會員的票券資料
onMounted(async () => {
  
  try {
    const memberId = userStore.memberId;
    // 發送請求到後端
    const response = await axiosapi.get(`/api/tickets/member/${memberId}`);
    console.log(`response: ${response}`)
    console.log(`response.data: ${response.data}`)
    currentMemberTickets.value = response.data.map(ticket => ({
      eventId: ticket.eventId?.eventId,
      ticketId: ticket.ticketId,
      ticketAmount: ticket.ticketAmount,
      eventName: ticket.eventId?.eventName,
      eventDate: `${ticket.eventId?.eventStartDate} ~ ${ticket.eventId?.eventEndDate}`,
      venueName: ticket.eventId?.venue?.venueName,
      purchaseDate: ticket.purchaseDate,
      ticketStatus: ticket.ticketStatus,
      eventPrice: ticket.eventId?.eventPrice
    }))
  } catch (error) {
    console.error('獲取會員票券失敗:', error)
  } finally {
    isLoading.value = false
  }
});

const goToEventDetail = (eventId) => {
  console.log('Navigating to event:', eventId); // 調試用
  
  if (!eventId) {
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
        id: eventId.toString() // 直接使用 myloveId
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

</script>

<style scoped>
@import '@/assets/styles/main.css';
@import '@/assets/styles/mainContent.css';

.main-content {
  padding-top: 15px;
  min-height: 700px;
}

table {
  width: 90%;
  border-collapse: collapse;
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

th, td {
  padding: 12px;
  text-align: center;
  border: 1px solid #ddd;
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

.event-link {
  color: #007bff;
  text-decoration: none;
}

.event-link:hover {
  text-decoration: underline;
  color: #f8961e;
}

button {
  padding: 8px 16px;
  border: none;
  cursor: pointer;
  background-color: #f8961e;
  color: white;
  border-radius: 4px;
}

button:hover {
  background-color: #e07b00;
}
</style>
