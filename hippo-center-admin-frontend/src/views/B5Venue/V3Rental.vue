<!-- src/views/B5Venue/V3Rental.vue -->
<template>
    <div class="venue-rental-container">
        <!-- 租借申請表頭組件 -->
        <VenueRentalHeader 
            :applications="applications"
            :allApplications="allApplications"  
            @search="handleSearch"
            @search-complete="handleSearchComplete" 
            @view-pending="handleViewPending"
            @view-all="handleViewAll"
            @new-application="handleNewApplication"
            @view-submitted="handleViewSubmitted"
            @view-confirmed="handleViewConfirmed"
        />

        <!-- 租借申請表格組件 -->
        <div class="content-area">
            <V3RentalFormTable
                :applications="applications"
                :loading="loading"
                :sort-config="sortConfig"
                @view="handleApplicationUpdate"
                @sort-table="handleSort"
                @edit="handleEdit"
                @delete-success="handleDeleteSuccess"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import VenueRentalHeader from './V3RentalFormHeader.vue'
import V3RentalFormTable from './V3RentalFormTable.vue'

// 數據狀態
const applications = ref([])
const loading = ref(false)
const sortConfig = ref({
    key: 'bookingId',
    order: 'asc'
})
const allApplications = ref([])

// 初始化數據
onMounted(async () => {
    await fetchApplications()
})

// 獲取申請列表
const fetchApplications = async () => {
    loading.value = true
    try {
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/api/booking/ad`)
        if (response.data) {
            applications.value = response.data
            allApplications.value = response.data  // 保存所有申請數據
        }
    } catch (error) {
        console.error('獲取申請列表失敗:', error)
        Swal.fire({
            title: '錯誤',
            text: '獲取申請列表失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        })
    } finally {
        loading.value = false
    }
}
const handleApplicationUpdate = (updatedApplication) => {
    // 更新 applications 陣列
    applications.value = applications.value.map(app => 
        app.bookingId === updatedApplication.bookingId ? updatedApplication : app
    )
}

const handleSearchComplete = (data) => {
    console.log('搜尋完成，更新數據:', data)
    applications.value = data
}

// 搜索處理
const handleSearch = async (searchText) => {
    loading.value = true
    try {
        if (!searchText) {
            await fetchApplications()
            return
        }

        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/booking/ad/search`,
            {
                params: {
                    search: searchText
                }
            }
        )
        
        if (response.data) {
            applications.value = response.data
        }
    } catch (error) {
        console.error('搜尋失敗:', error)
        Swal.fire({
            title: '錯誤',
            text: '搜尋失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        })
    } finally {
        loading.value = false
    }
}

// 查看待審核申請
const handleViewPending = async () => {
    loading.value = true
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/booking/ad/status/審核中`
        )
        if (response.data) {
            applications.value = response.data
        }
    } catch (error) {
        console.error('獲取待審核申請失敗:', error)
        Swal.fire({
            title: '錯誤',
            text: '獲取待審核申請失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        })
    } finally {
        loading.value = false
    }
}

// 查看所有申請
const handleViewAll = () => {
    fetchApplications()
}
const handleViewSubmitted = async () => {
    loading.value = true
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/booking/ad/status/送出申請`
        )
        if (response.data) {
            applications.value = response.data
        }
    } catch (error) {
        console.error('獲取送出申請失敗:', error)
        Swal.fire({
            title: '錯誤',
            text: '獲取送出申請失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        })
    } finally {
        loading.value = false
    }
}
const handleViewConfirmed = async () => {
    loading.value = true
    try {
        const response = await axios.get(
            `${import.meta.env.VITE_API_URL}/api/booking/ad/status/檔期已確認`
        )
        if (response.data) {
            applications.value = response.data
        }
    } catch (error) {
        console.error('獲取已確認檔期申請失敗:', error)
        Swal.fire({
            title: '錯誤',
            text: '獲取已確認檔期申請失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        })
    } finally {
        loading.value = false
    }
}

// 新增申請
const handleNewApplication = () => {
    // 導航到新增申請頁面或打開新增申請對話框
}
const handleApplicationSubmit = async () => {
    await fetchApplications() // 重新獲取數據
    Swal.fire({
        icon: 'success',
        title: '新增成功',
        text: '租借申請已成功送出',
        confirmButtonColor: '#4A6741'
    })
}

// 排序處理
const handleSort = (key) => {
    if (sortConfig.value.key === key) {
        sortConfig.value.order = sortConfig.value.order === 'asc' ? 'desc' : 'asc'
    } else {
        sortConfig.value.key = key
        sortConfig.value.order = 'asc'
    }
}

// 編輯處理
const handleEdit = (application) => {
    // 打開編輯對話框或導航到編輯頁面
}

// 刪除成功處理
const handleDeleteSuccess = async (bookingId) => {
    await fetchApplications()
}
watch(applications, (newVal) => {
    console.log('applications 更新:', newVal)
}, { deep: true })
</script>


<style scoped>
.venue-rental-container {
    background-color: var(--background-color, #F5F7F4);
}

.content-area {
    margin-top: 15%; 
    padding: 20px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    border: 1px solid var(--border-color);
    
}
</style>