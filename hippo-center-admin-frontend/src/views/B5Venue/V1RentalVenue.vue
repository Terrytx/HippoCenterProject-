<!-- VenueList.vue -->
<template>
    <div class="rental-venue-container">
        <!-- 頁面標題和搜尋區塊 -->
        <VenueHeader 
            :venues="venues"        
            @search="searchById"     
            @get-available="getAvailableVenues" 
            @get-all="getAllVenues"   
            @open-dialog="dialogVisible = true"   
        />

        <!-- 場地列表 -->
        <div class="main-content">
            <VenueTable
                :venues="venues"
                :loading="loading"
                :sort-config="sortConfig"
                @sort-table="sortTable"
                @edit="handleEdit"
                @save="handleSave"
                @cancel="handleCancel"
                @download="downloadSpecifications"
                @file-upload="handleFileUpload"
                @delete-success="handleDeleteSuccess"
            />
        </div>

        <!-- 新增場地對話框 -->
        <div v-if="dialogVisible" class="dialog-overlay" @click="handleDialogClose">
            <div class="dialog-content" @click.stop>
                <div class="dialog-header">
                    <h2>新增場地</h2>
                    <button class="close-btn" @click="handleDialogClose">
                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="18" y1="6" x2="6" y2="18"/>
                            <line x1="6" y1="6" x2="18" y2="18"/>
                        </svg>
                    </button>
                </div>
                <div class="dialog-body">
                    <VenueInsert @submit-success="handleInsertSuccess" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import VenueHeader from './V1RentalHeader.vue'
import VenueInsert from './V1RentalVenueInsert.vue'
import VenueTable from './V1RentalVenueTable.vue'
import Swal from 'sweetalert2'
import axiosapi from '@/plugins/axios.js'
// 狀態管理
const venues = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const sortConfig = ref({ key: 'venueId', order: 'asc' })

// API 基礎URL
const baseURL = '/api/venue'

// 排序方法
const sortTable = (key) => {
    if (sortConfig.value.key === key) {
        sortConfig.value.order = sortConfig.value.order === 'asc' ? 'desc' : 'asc'
    } else {
        sortConfig.value.key = key
        sortConfig.value.order = 'asc'
    }
}

// 對話框相關方法
const handleDialogClose = async () => {
    // const result = await Swal.fire({
    //     title: '確定要關閉嗎？',
    //     text: '未保存的數據將會丟失',
    //     icon: 'warning',
    //     showCancelButton: true,
    //     confirmButtonColor: '#4a6741',
    //     cancelButtonColor: '#d33',
    //     confirmButtonText: '確定',
    //     cancelButtonText: '取消',
    //     background: '#fff8f0',
    //     iconColor: '#f8bb86'
    // })

    // if (result.isConfirmed) {
    //     dialogVisible.value = false
    // }
    dialogVisible.value = false
}

const handleInsertSuccess = () => {
    dialogVisible.value = false
    showMessage('success', '場地新增成功')
    getAllVenues()
}


// 消息提示方法
const showMessage = (type, message) => {
    const commonConfig = {
        title: message,
        confirmButtonColor: '#4a6741',
        showClass: {
            popup: 'animate__animated animate__fadeInDown'
        },
        hideClass: {
            popup: 'animate__animated animate__fadeOutUp'
        }
    }

    switch (type) {
        case 'success':
            Swal.fire({
                ...commonConfig,
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                showConfirmButton: false,
                background: '#f0f8f0',
                iconColor: '#4a6741'
            })
            break
        case 'error':
            Swal.fire({
                ...commonConfig,
                icon: 'error',
                showConfirmButton: true,
                confirmButtonText: '確定',
                background: '#fff5f5',
                iconColor: '#f27474'
            })
            break
        case 'warning':
            Swal.fire({
                ...commonConfig,
                icon: 'warning',
                showConfirmButton: true,
                confirmButtonText: '確定',
                background: '#fff8f0',
                iconColor: '#f8bb86'
            })
            break
        default:
            Swal.fire({
                ...commonConfig,
                icon: 'info',
                timer: 2000,
                timerProgressBar: true,
                showConfirmButton: false,
                background: '#f0f8ff',
                iconColor: '#3fc3ee'
            })
    }
}
// API 方法
const getAllVenues = async () => {
    loading.value = true
    try {
        const response = await axiosapi.get(`${baseURL}/findall`, { withCredentials: true })
        if (response.data.success) {
            venues.value = response.data.list
            // showMessage('success', '場地資料載入成功')
        } else {
            showMessage('warning', response.data.message)
        }
    } catch (err) {
        showMessage('error', '獲取場地資料失敗')
        console.error('Error:', err)
    } finally {
        loading.value = false
    }
}

const searchById = async (searchVenueId) => {
    if (!searchVenueId) {
        showMessage('warning', '請輸入場地編號')
        return
    }

    loading.value = true
    try {
        const response = await axiosapi.get(`${baseURL}/${searchVenueId}`, { withCredentials: true })
        if (response.data.success) {
            venues.value = response.data.list
            // showMessage('success', '搜尋成功')
        } else {
            venues.value = []
            showMessage('warning', response.data.message)
        }
    } catch (err) {
        showMessage('error', '搜尋場地失敗')
        console.error('Error:', err)
    } finally {
        loading.value = false
    }
}

const getAvailableVenues = async () => {
    loading.value = true
    try {
        const response = await axiosapi.get(`${baseURL}/available`, { withCredentials: true })
        if (response.data.success) {
            venues.value = response.data.list
            // showMessage('success', '已顯示所有可租借場地')
        } else {
            showMessage('warning', response.data.message)
        }
    } catch (err) {
        showMessage('error', '獲取可租借場地失敗')
        console.error('Error:', err)
    } finally {
        loading.value = false
    }
}

const downloadSpecifications = async (venueId) => {
    try {
        const response = await axiosapi.get(`${baseURL}/download-specifications/${venueId}`, {
            responseType: 'blob',
            withCredentials: true
        })
        
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `technical_specifications_${venueId}.pdf`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        showMessage('success', '技術規格下載成功')
    } catch (err) {
        showMessage('error', '下載技術規格失敗')
        console.error('Error:', err)
    }
}

const handleEdit = (row) => {
    row.originalData = { ...row }
    row.isEditing = true
}

const handleSave = async (row) => {
    try {
        const formData = new FormData()
        formData.append('venueId', row.venueId)
        formData.append('venueName', row.venueName)
        formData.append('venueDescription', row.venueDescription || '')
        formData.append('rentalStatue', row.rentalStatue)
        if (row.capacity !== undefined) formData.append('capacity', row.capacity)
        if (row.venueFeeByDay !== undefined) formData.append('venueFeeByDay', row.venueFeeByDay)
        if (row.areaPings !== undefined) formData.append('areaPings', row.areaPings)
        if (row.venueHeight !== undefined) formData.append('venueHeight', row.venueHeight)
        
        const response = await axiosapi.put(`${baseURL}/modify`, formData, {
            withCredentials: true,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })

        if (response.data.success) {
            showMessage('success', '修改成功')
            row.isEditing = false
            delete row.originalData
            getAllVenues()
        } else {
            showMessage('error', response.data.message || '修改失敗')
        }
    } catch (error) {
        console.error('Error:', error)
        showMessage('error', error.response?.data?.message || '修改失敗')
    }
}

const handleCancel = (row) => {
    Object.assign(row, row.originalData)
    row.isEditing = false
    delete row.originalData
}

const handleFileUpload = async ({ event, venueId }) => {
    const file = event.target.files[0]
    if (!file) return

    if (file.type !== 'application/pdf') {
        showMessage('error', '只能上傳 PDF 檔案！')
        return
    }

    const maxSize = 10 // MB
    if (file.size / 1024 / 1024 > maxSize) {
        showMessage('error', `檔案大小不能超過 ${maxSize}MB！`)
        return
    }

    try {
        const formData = new FormData()
        formData.append('file', file)

        const response = await axiosapi.post(
            `${baseURL}/upload-specifications/${venueId}`,
            formData,
            {
                withCredentials: true,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        )

        if (response.data.success) {
            showMessage('success', 'PDF規格檔案上傳成功')
            getAllVenues()
        } else {
            showMessage('error', response.data.message || 'PDF規格檔案上傳失敗')
        }
    } catch (error) {
        console.error('上傳錯誤：', error)
        showMessage('error', 'PDF規格檔案上傳失敗，請稍後再試')
    }
}


// 處理刪除成功事件
const handleDeleteSuccess = (newVenues) => {
    // 更新場地列表
    venues.value = newVenues
}
// 頁面載入時獲取所有場地
onMounted(() => {
    getAllVenues()
})
</script>

<style scoped>
.rental-venue-container {
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

.dialog-overlay {
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
}

.dialog-content {
    background: white;
    border-radius: 8px;
    width: 80%;
    max-height: 90vh;
    overflow-y: auto;
}

.dialog-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    border-bottom: 1px solid var(--border-color);
}

.dialog-body {
    padding: 24px;
}

.close-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
}

.close-btn:hover {
    color: var(--primary-color);
}

.icon {
    /* 通用圖標樣式 */
    width: 16px;
    height: 16px;
}

.message {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 12px 24px;
    border-radius: 4px;
    color: white;
    z-index: 1000;
    animation: slideIn 0.3s ease;
}

.message-success {
    background-color: var(--primary-color);
}

.message-warning {
    background-color: #ff9800;
}

.message-error {
    background-color: #f44336;
}

@keyframes slideIn {
    from {
        transform: translateX(100%);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}

@media (max-width: 1200px) {
    .rental-venue-container {
        padding: 0.5rem;
    }
    
    .main-content {
        padding-top: 20%;
    }
}

@media (max-width: 768px) {
    .dialog-content {
        width: 95%;
        margin: 10px;
    }

    .main-content {
        padding-top: 25%;
    }
}

/* 列印樣式 */
@media print {
    .rental-venue-container {
        padding: 0;
    }

    .dialog-overlay {
        display: none;
    }
}
</style>