<!-- src/views/B5Venue/V3RentalForm.vue -->
<template>
<div class="application-management">
    <div class="section">
    <div class="section-header">
        <h2>租借申請管理</h2>
        <div class="filter-controls">
        <select v-model="applicationFilter" class="filter-select">
            <option value="all">全部申請</option>
            <option value="送出申請">送出申請</option>
            <option value="審核中">審核中</option>
            <option value="檔期已確認">檔期已確認</option>
            <option value="已取消">已取消</option>
        </select>
        </div>
    </div>

    <table class="applications-table">
        <thead>
        <tr>
            <th>申請編號</th>
            <th>申請單位</th>
            <th>場地</th>
            <th>使用起日</th>
            <th>使用迄日</th>
            <th>申請狀態</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="app in paginatedApplications" :key="app.bookingId">
            <td>{{ app.bookingId }}</td>
            <td>{{ app.organizer }}</td>
            <td>{{ app.venue }}</td>
            <td>{{ formatDate(app.rentalStartDatetime) }}</td>
            <td>{{ formatDate(app.rentalEndDatetime) }}</td>
            <td>
            <span class="status-badge" :class="getStatusClass(app.orderStatus)">
                {{ app.orderStatus }}
            </span>
            </td>
            <td class="action-buttons">
            <button 
                v-if="app.orderStatus === '送出申請'"
                class="approve-button"
                @click="approveApplication(app.bookingId)"
            >
                審核
            </button>
            <button 
                v-if="app.orderStatus === '送出申請'"
                class="reject-button"
                @click="rejectApplication(app.bookingId)"
            >
                取消
            </button>
            <button 
                class="view-button"
                @click="viewApplication(app.bookingId)"
            >
                查看
            </button>
            </td>
        </tr>
        </tbody>
    </table>

    <div class="pagination">
        <button 
        class="secondary-button"
        :disabled="currentPage === 1"
        @click="changePage(currentPage - 1)"
        >
        上一頁
        </button>
        <span class="page-info">第 {{ currentPage }} 頁，共 {{ totalPages }} 頁</span>
        <button 
        class="secondary-button"
        :disabled="currentPage === totalPages"
        @click="changePage(currentPage + 1)"
        >
        下一頁
        </button>
    </div>
    </div>

    <V3RentalFormFind 
    :show="showDetailModal"
    :application="selectedApplication"
    @close="closeDetailModal"
    />
</div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import V3RentalFormFind from './V3RentalFormFind.vue'

// 響應式狀態
const applications = ref([])
const applicationFilter = ref('all')
const currentPage = ref(1)
const itemsPerPage = ref(7)
const showDetailModal = ref(false)
const selectedApplication = ref(null)

// API 基礎 URL
const baseURL = import.meta.env.VITE_API_URL

// 計算屬性
const filteredApplications = computed(() => {
if (applicationFilter.value === 'all') {
    return applications.value
}
return applications.value.filter(app => app.orderStatus === applicationFilter.value)
})

const totalPages = computed(() => {
return Math.ceil(filteredApplications.value.length / itemsPerPage.value)
})

const paginatedApplications = computed(() => {
const start = (currentPage.value - 1) * itemsPerPage.value
const end = start + itemsPerPage.value
return filteredApplications.value.slice(start, end)
})

// 方法
const fetchApplications = async () => {
try {
    const requestBody = applicationFilter.value === 'all' 
    ? null 
    : { orderStatus: applicationFilter.value }
    
    // 使用 GET 方法並添加基礎 URL
    const response = await axios.post(`${baseURL}/api/booking/ad`, {
            params: requestBody
        })
    applications.value = response.data
} catch (error) {
    console.error('Error fetching applications:', error)
}
}

const getStatusClass = (status) => {
switch (status) {
    case '送出申請':
    return 'status-pending'
    case '審核中':
    return 'status-reviewing'
    case '檔期已確認':
    return 'status-confirmed'
    case '已取消':
    return 'status-cancelled'
    default:
    return ''
}
}

const approveApplication = async (id) => {
try {
    const application = applications.value.find(app => app.bookingId === id)
    if (application) {
    application.orderStatus = '審核中'
    await axios.put(`/api/booking/ad/${id}`, application)
    await fetchApplications()
    }
} catch (error) {
    console.error('Error approving application:', error)
}
}

const rejectApplication = async (id) => {
try {
    const application = applications.value.find(app => app.bookingId === id)
    if (application) {
    application.orderStatus = '已取消'
    await axios.put(`/api/booking/ad/${id}`, application)
    await fetchApplications()
    }
} catch (error) {
    console.error('Error rejecting application:', error)
}
}

const viewApplication = (id) => {
selectedApplication.value = applications.value.find(app => app.bookingId === id)
showDetailModal.value = true
}

const closeDetailModal = () => {
showDetailModal.value = false
selectedApplication.value = null
}

const changePage = (page) => {
if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
}
}

const formatDate = (date) => {
if (!date) return ''
return new Date(date).toLocaleDateString('zh-TW')
}

// 監聽篩選器變化
watch(applicationFilter, () => {
currentPage.value = 1
fetchApplications()
})

// 生命週期鉤子
onMounted(() => {
fetchApplications()
})
</script>

<style scoped>
:root {
--primary-color: #4A6741;
--secondary-color: #8AA682;
--hover-color: #C2D5BB;
--background-color: #F5F7F4;
--text-primary: #2C3E2D;
--text-secondary: #5C715E;
--text-final: #F5F7F4;
--border-color: #D8E0D5;
}

.application-management {
background-color: var(--background-color);
/* min-height: 80%; */
padding-top: 5%;

}

.section {
background-color: white;
border-radius: 1rem;
padding: 2rem;
box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);

}

.section-header {
display: flex;
justify-content: space-between;
}

.section-header h2 {
color: var(--text-primary);
font-size: 20px;
font-weight: 600;
}

.filter-controls {
display: flex;
gap: 1rem;
align-items: center;
}

.filter-select {
padding: 0.75rem 1rem;
border: 1px solid var(--border-color);
border-radius: 0.5rem;
background-color: white;
color: var(--text-primary);
font-size: 0.875rem;
cursor: pointer;
min-width: 120px;
}

.applications-table {
width: 100%;
border-collapse: collapse;
background-color: white;
border-radius: 0.5rem;
overflow: hidden;

}

.applications-table th,
.applications-table td {
padding: 1rem;
text-align: left;
border-bottom: 1px solid var(--border-color);
}

.applications-table th {
background-color: var(--primary-color);
/* color: var(--text-final); */
color: white;
font-weight: 600;
}

.applications-table tr:hover {
background-color: var(--background-color);
}

.status-badge {
display: inline-block;
padding: 0.25rem 0.75rem;
border-radius: 1rem;
font-size: 0.875rem;
font-weight: 500;
}

.status-badge.status-pending {
background-color: #FFC107;
color: black;
}

.status-badge.status-reviewing {
background-color: #2196F3;
color: white;
}

.status-badge.status-confirmed {
background-color: #4CAF50;
color: white;
}

.status-badge.status-cancelled {
background-color: #F44336;
color: white;
}

.action-buttons {
display: flex;
gap: 0.5rem;
}

.approve-button,
.reject-button,
.view-button,
.secondary-button {
padding: 0.5rem 1rem;
border: none;
border-radius: 0.5rem;
cursor: pointer;
font-weight: 500;
transition: all 0.3s ease;
}

.approve-button {
background-color: #4CAF50;
color: white;
}

.approve-button:hover {
background-color: #388e3c;
}

.reject-button {
background-color: #F44336;
color: white;
}

.reject-button:hover {
background-color: #d32f2f;
}

.view-button,
.secondary-button {
background-color: var(--secondary-color);
color: var(--text-final);
}

.view-button:hover,
.secondary-button:hover {
background-color: var(--hover-color);
}

.pagination {
display: flex;
justify-content: center;
align-items: center;
gap: 1rem;
margin-top: 2rem;
padding: 1rem;
}

.page-info {
color: var(--text-secondary);
font-size: 0.875rem;
}

@media (max-width: 768px) {
.application-management {
    padding: 1rem;
}

.section {
    padding: 1rem;
}

.section-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
}

.filter-controls {
    flex-direction: column;
}

.applications-table {
    display: block;
    overflow-x: auto;
}

.action-buttons {
    flex-wrap: wrap;
}
}

.text-center {
text-align: center;
}

.text-right {
text-align: right;
}

.mt-2 {
margin-top: 2rem;
}

.mb-2 {
margin-bottom: 2rem;
}
</style>