<template>
<div class="modal-overlay" @click.self="closeModal">
<div class="modal-container">
    <div class="modal-header">
    <h2>申請資訊查詢</h2>
    <button class="close-button" @click="closeModal">×</button>
    </div>
    
    <!-- 載入中狀態 -->
    <div v-if="loading" class="loading-container">
    <div class="loading-spinner"></div>
    <p>載入中...</p>
    </div>

    <!-- 查詢結果 -->
    <div v-else-if="bookingData" class="modal-content">
        <div class="info-section">
            <h3>申請編號</h3>
            <p class="booking-id">{{ bookingData.bookingId }}</p>
        </div>
        <div class="info-section">
            <h3>申請狀態</h3>
            <div class="status-badge">
            {{ bookingData.orderStatus }}
            </div>
        </div>

    <div class="info-section">
        <h3>場地資訊</h3>
        <div class="info-grid">
        <div class="info-item">
            <label>租借場地</label>
            <p>{{ getVenueNames(bookingData.venue) }}</p>
        </div>
        <div class="info-item">
            <label>租借期間</label>
            <p>{{ formatDate(bookingData.rentalStartDatetime) }} ~ {{ formatDate(bookingData.rentalEndDatetime) }}</p>
        </div>
        </div>
    </div>

    <div class="info-section">
        <h3>申請人資訊</h3>
        <div class="info-grid">
        <div class="info-item">
            <label>主辦單位</label>
            <p>{{ bookingData.organizer }}</p>
        </div>
        <div class="info-item">
            <label>聯絡人</label>
            <p>{{ bookingData.contact }}</p>
        </div>
        <div class="info-item">
            <label>聯絡電話</label>
            <p>{{ bookingData.contactPersonMobile }}</p>
        </div>
        <div class="info-item">
            <label>電子郵件</label>
            <p>{{ bookingData.contactEmail }}</p>
        </div>
        </div>
    </div>

    <div class="info-section">
        <h3>活動資訊</h3>
        <div class="info-content">
        <label>活動說明</label>
        <p>{{ bookingData.eventDescription || '無' }}</p>
        </div>
        <div class="info-content">
        <label>備註</label>
        <p>{{ bookingData.remarks || '無' }}</p>
        </div>
    </div>
    </div>

    <!-- 錯誤訊息 -->
    <div v-else-if="error" class="error-container">
    <p>{{ error }}</p>
    </div>

    <div class="modal-footer">
    <button class="btn-confirm" @click="closeModal">確認</button>
    </div>
</div>
</div>
</template>

<script setup>
import { ref, onMounted, defineProps, defineEmits, watch } from 'vue'
import axiosapi from '@/plugins/axios.js'

const props = defineProps({
    bookingId: {
    type: [String, Number],
    required: true
    },
    show: {
    type: Boolean,
    default: false
    }
})

const emit = defineEmits(['close'])

const loading = ref(true)
const error = ref(null)
const bookingData = ref(null)

// 場地名稱對照表
const venueMap = {
'A01': '1號倉庫',
'A02': '2號倉庫',
'A03': '3號倉庫',
'A04': '4號倉庫',
'A05': '5號倉庫',
'B02': '春田序曲',
'B03': '瑪吉的畫廊',
'B04': '巴特的藝術空間',
'B05': '荷馬的闢護所',
'B06': '麗莎的實驗室',
'C01': '春田綠地',
'C02': '春田大道'
}

// 獲取預約資訊
const fetchBookingInfo = async () => {
try {
loading.value = true
error.value = null
const response = await axiosapi.get(`/api/booking/${props.bookingId}`)

if (response.data.success) {
        bookingData.value = response.data.data
        // 添加預約編號到資料中
        bookingData.value.bookingId = props.bookingId
    } else {
        throw new Error(response.data.message || '查詢失敗')
    }
} catch (err) {
    error.value = '獲取預約資訊失敗：' + (err.response?.data?.message || err.message)
} finally {
    loading.value = false
}
}

// 格式化場地名稱
const getVenueNames = (venues) => {
    if (!venues) return ''
    return venues.split(',')
    .map(venue => venueMap[venue] || venue)
    .join('、')
    }

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    })
}

// 關閉 Modal
const closeModal = () => {
emit('close')
}

// 監聽 ID 變化，重新獲取資料
watch(() => props.bookingId, () => {
if (props.show && props.bookingId) {
fetchBookingInfo()
}
})

// 組件掛載時獲取資料
onMounted(() => {
if (props.show && props.bookingId) {
fetchBookingInfo()
}
})
</script>

<style scoped>


.booking-id {
    font-size: 24px;
    font-weight: bold;
    color: #f8961e;
    text-shadow: 1px 1px 1px #ca8f0f;
    margin: 10px 0;
    padding: 10px;
    background-color: #fff8e1;
    border-radius: 4px;
    display: inline-block;
}
.modal-overlay {
position: fixed;
top: 0;
left: 0;
right: 0;
bottom: 0;
background-color: rgba(0, 0, 0, 0.5);
display: flex;
justify-content: center;
align-items: center;
z-index: 1000;
font-family: system-ui, -apple-system, sans-serif;
}

.modal-container {
background-color: white;
border-radius: 8px;
width: 90%;
max-width: 600px;
max-height: 90vh;
overflow-y: auto;
box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-header {
display: flex;
justify-content: space-between;
align-items: center;
padding: 16px 20px;
border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
margin: 0;
font-size: 20px;
}

.close-button {
background: none;
border: none;
font-size: 24px;
cursor: pointer;
color: #666;
}

.close-button:hover {
color: #333;
}

.modal-content {
padding: 20px;
}

.info-section {
margin-bottom: 20px;
}

.info-section h3 {
font-size: 16px;
color: #374151;
margin-bottom: 12px;
border-bottom: 2px solid #666;
padding-bottom: 4px;
}

.info-grid {
display: grid;
grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
gap: 16px;
}

.info-item {
margin-bottom: 12px;
}

.info-item label {
font-size: 14px;
color: #6b7280;
display: block;
margin-bottom: 4px;
}

.info-item p {
font-size: 15px;
color: #111827;
margin: 0;
}

.status-badge {
display: inline-block;
padding: 6px 12px;
border-radius: 4px;
font-size: 14px;
font-weight: 500;
}

.loading-container {
display: flex;
flex-direction: column;
align-items: center;
justify-content: center;
padding: 40px;
}

.loading-spinner {
border: 4px solid #f3f3f3;
border-top: 4px solid #f8961e;
border-radius: 50%;
width: 40px;
height: 40px;
animation: spin 1s linear infinite;
margin-bottom: 16px;
}

@keyframes spin {
0% { transform: rotate(0deg); }
100% { transform: rotate(360deg); }
}

.error-container {
padding: 20px;
text-align: center;
color: #dc2626;
}

.modal-footer {
padding: 16px 20px;
border-top: 1px solid #e5e7eb;
display: flex;
justify-content: flex-end;
}

.btn-confirm {
background: #0e0e0e;
color: white;
border: none;
padding: 8px 24px;
border-radius: 4px;
cursor: pointer;
transition: background-color 0.2s;
}

.btn-confirm:hover {
background: #333232;
} 

</style>