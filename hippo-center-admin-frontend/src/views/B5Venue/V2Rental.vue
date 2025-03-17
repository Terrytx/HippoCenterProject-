<template>
    <div class="rental-container">
        <V2VenueStatusHeader
            :schedules="schedules"
            @search="handleSearch"
            @get-available="handleGetAvailable"
            @get-all="handleGetAll" 
            @open-dialog="handleOpenDialog"
        />

        <div class="main-content">
            <V2VenueStatusTable 
                v-if="!isCalendarView"
                :schedules="schedules"
                :loading="isLoading"
                :current-venue="currentVenue"
                :editing-row="editingRow"
                :is-editing="isEditing"
                @edit="handleEdit"
                @save="handleSave"
                @cancel="handleCancel"
                @delete="handleDelete"
            />
            <V2VenueStatusCalendar
                v-else
                :schedules="schedules"
                :selected-month="currentMonth" 
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import V2VenueStatusHeader from './V2VenueStatusHeader.vue'
import V2VenueStatusTable from './V2VenueStatusTable.vue'
import V2VenueStatusCalendar from './V2VenueStatusCalendar.vue'
import Swal from 'sweetalert2'
import axios from 'axios'
const venuesList = ref([])
const isLoading = ref(false)
// 新增行事曆視圖控制狀態
const isCalendarView = ref(false)
// 響應式變數
const schedules = ref([])
const loading = ref(false)
const currentVenue = ref(null)
const currentMonth = ref('')  // 保存當前選擇的月份
const searchParams = ref({    // 保存搜索參數
    month: '',
    venueId: '',
    venue: null
})

// 編輯相關
const editingRow = ref(null)
const isEditing = ref(false)

const handleGetAll = async ({ month, venueId, venue }) => {
    isCalendarView.value = true  // 設置為行事曆視圖
    await fetchData({ month, venueId, venue })
}
const handleSearch = async ({ month, venueId, venue }) => {
    isCalendarView.value = false  // 確保切換到表格視圖
    await fetchData({ month, venueId, venue })
}



// 抽取共用的數據獲取邏輯
const fetchData = async ({ month, venueId, venue }) => {
    if (!month && !venueId) {
        await Swal.fire({
        icon: 'warning',
        title: '請注意',
        text: '請至少選擇場地或年月',
        confirmButtonColor: '#4A6741'
})
        return
    }

    // 保存搜索參數
    searchParams.value = { month, venueId, venue }
    currentMonth.value = month
    if (venue) {
        currentVenue.value = venue
    }

    loading.value = true

    try {
        let response;
        if (month && venueId) {
            const [year, monthStr] = month.split('-');
            response = await axios.get(`${import.meta.env.VITE_API_URL}/venue-status/${year}/${monthStr}/venue/${venueId}`);
        } else if (month) {
            const [year, monthStr] = month.split('-');
            response = await axios.get(`${import.meta.env.VITE_API_URL}/venue-status/${year}/${monthStr}`);
        } else if (venueId) {
            response = await axios.get(`${import.meta.env.VITE_API_URL}/venue-status/venue/${venueId}`);
        }

        if (response.data) {
            if (Array.isArray(response.data)) {
                schedules.value = response.data;
            } else if (response.data.success && response.data.list) {
                schedules.value = response.data.list;
            } else if (response.data.success === false) {
                await Swal.fire({
                    icon: 'warning',
                    title: '查無資料',
                    text: response.data.message || '找不到相關資料',
                    confirmButtonColor: '#4A6741'
                })                
                schedules.value = [];
            }
        } else {
            schedules.value = [];
            await Swal.fire({
                icon: 'error',
                title: '查詢失敗',
                text: error.response?.data?.message || error.message,
                confirmButtonColor: '#4A6741'
            })
        }
    } catch (error) {
        console.error('查詢失敗:', error);
        await Swal.fire({
                icon: 'error',
                title: '查詢失敗',
                text: error.response?.data?.message || error.message,
                confirmButtonColor: '#4A6741'
            })
        schedules.value = [];
    } finally {
        loading.value = false;
    }
}


// 計算表格高度
const tableHeight = ref(500)
onMounted(() => {
    tableHeight.value = window.innerHeight - 300
})


const handleEdit = (row) => {
    editingRow.value = row
    isEditing.value = true
}

const handleSave = async (updatedData) => {
    try {
        loading.value = true
        const updatePayload = {
            venueStatusId: updatedData.venueStatusId,
            venueId: currentVenue.value?.venueId,
            status: updatedData.status,
            bookingId: updatedData.bookingId,
            note: updatedData.note,
            date: updatedData.date
        }

        const response = await axios.put(
            `${import.meta.env.VITE_API_URL}/venue-status/update`,
            updatePayload
        )
        
        if (response.data) {
            await Swal.fire({
                icon: 'success',
                title: '更新成功',
                text: '資料已成功更新',
                confirmButtonColor: '#4A6741',
                timer: 1500
            })            
        await handleSearch(searchParams.value)
        }
    } catch (error) {
        console.error('更新失敗:', error)
        await Swal.fire({
            icon: 'error',
            title: '更新失敗',
            text: error.response?.data?.message || error.message,
            confirmButtonColor: '#4A6741'
        })
    } finally {
        loading.value = false
        isEditing.value = false
        editingRow.value = null
    }
}
const handleSort = (sortConfig) => {
    console.log('排序設定:', sortConfig)
  // 實現排序邏輯
}
const handleCancel = () => {
    editingRow.value = null
    isEditing.value = false
}

const handleDelete = async (row) => {
    try {
        const result = await Swal.fire({
            title: '確認刪除',
            text: '您確定要刪除這筆資料嗎？此操作無法復原。',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc2626',
            cancelButtonColor: '#6b7280',
            confirmButtonText: '確定刪除',
            cancelButtonText: '取消'
        })
                
        loading.value = true

        const response = await axios.delete(
            `${import.meta.env.VITE_API_URL}/venue-status/delete/${row.venueStatusId}`
        )

        if (response.data) {
            if (searchParams.value) {
                await handleSearch(searchParams.value)
            }
        }
    } catch (error) {
        if (error === 'cancel') return
        console.error('刪除失敗:', error)
        await Swal.fire({
            icon: 'error',
            title: '刪除失敗',
            text: error.response?.data?.message || error.message,
            confirmButtonColor: '#4A6741'
        })
    } finally {
        loading.value = false
    }
}

const handleMonthChange = async (date) => {
    if (!currentVenue.value) {
        await Swal.fire({
        icon: 'warning',
        title: '請注意',
        text: '請先選擇場地',
        confirmButtonColor: '#4A6741'
    })        
    return
    }
    
    const month = `${date.year}-${String(date.month).padStart(2, '0')}`
    await fetchData({
        month,
        venueId: currentVenue.value.venueId,
        venue: currentVenue.value
    })
}
</script>

<style scoped>
/* 全域變數定義 */
:root {
    --primary-color: #4A6741;
    --secondary-color: #8AA682;
    --hover-color: #C2D5BB;
    --background-color: #F5F7F4;
    --text-primary: #2C3E2D;
    --text-secondary: #5C715E;
    --border-color: #D8E0D5;
}

.rental-container {
    background-color: var(--background-color);
}

.main-content {
    margin-top: 15%;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 20px;
    border: 1px solid var(--border-color);
    z-index: 1;
}

/* Header 樣式 */
:deep(.venue-status-header) {
    position: relative;
    z-index: 1000;
}

/* Form 控制項樣式 */
.form-control {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid var(--border-color);
    border-radius: 0.25rem;
    font-size: 0.875rem;
    color: var(--text-primary);
    transition: all 0.2s;
}

.form-control:focus {
    border-color: var(--primary-color);
    outline: none;
    box-shadow: 0 0 0 2px var(--hover-color);
}

.form-control:hover {
    border-color: var(--secondary-color);
}

/* Element Plus 元件覆蓋樣式 */
.el-select-dropdown,
.el-picker-panel {
    z-index: 2000 !important;
}

.table-cell.editing {
    padding: 0.25rem;
}

.el-message-box {
    border-radius: 0.5rem;
    border: 1px solid var(--border-color);
}

.el-message-box__title {
    color: var(--text-primary);
}

.el-message-box__content {
    color: var(--text-secondary);
}

.el-button--primary {
    background-color: var(--primary-color);
    border-color: var(--primary-color);
}

.el-button--primary:hover {
    background-color: var(--secondary-color);
    border-color: var(--secondary-color);
}

/* 響應式樣式 */
@media (max-width: 768px) {
    .main-content {
        margin-top: 35%;
        padding: 1rem;
    }
}
</style>
