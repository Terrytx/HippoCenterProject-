<!-- V3RentalFormTable.vue -->
<template>
    <div class="card" :class="{ 'loading': loading }">
        <div class="card-header">
            <h3 class="title">租借申請列表</h3>
        </div>

        <div class="table-container">
            <table class="venue-table">
                <thead>
                    <tr>
                        <th @click="sortTable('bookingId')" class="sortable">
                            申請編號
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('venue')" class="sortable">
                            租借場地
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('rentalStartDatetime')" class="sortable">
                            使用起日
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('rentalEndDatetime')" class="sortable">
                            使用迄日
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('orderStatus')" class="sortable">
                            申請狀態
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('organizer')" class="sortable">
                            主辦單位
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('bookingDatetime')" class="sortable">
                            申請日期
                            <span class="sort-icon">↕</span>
                        </th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="application in sortedApplications" :key="application.bookingId">
                        <td>{{ application.bookingId }}</td>
                        <td>
                            <span class="venue-name">{{ application.venueName }}</span>
                        </td>
                        <td>{{ formatDate(application.rentalStartDatetime) }}</td>
                        <td>{{ formatDate(application.rentalEndDatetime) }}</td>
                        <td>
                            <span 
                                class="status-tag"
                                :class="getStatusClass(application.orderStatus)"
                            >
                                {{ application.orderStatus }}
                            </span>
                        </td>
                        <td>{{ application.organizer }}</td>
                        <td>{{ formatDate(application.bookingDatetime) }}</td>

                        <td>
                            <div class="action-buttons">
                                <button class="btn btn-view" @click="handleView(application)">
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                        <circle cx="12" cy="12" r="3"/>
                                    </svg>
                                    查看
                                </button>
                                <template v-if="application.orderStatus === '送出申請' || application.orderStatus === '審核中'">
                                <button class="btn btn-confirm" @click="handleConfirm(application)":disabled="application.orderStatus === '檔期已確認'" >
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20 6L9 17l-5-5"/>
                                        </svg>
                                        檔期確認
                                    </button>
                                </template>
                                <br>
                                <template v-if="application.orderStatus === '送出申請' || application.orderStatus === '審核中'">
                                    <button class="btn btn-edit" @click="handleEdit(application)">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                        </svg>
                                        修改
                                    </button>
                                    <button class="btn btn-cancel" @click="handleCancel(application)">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <circle cx="12" cy="12" r="10"/>
                                            <line x1="15" y1="9" x2="9" y2="15"/>
                                            <line x1="9" y1="9" x2="15" y2="15"/>
                                        </svg>
                                        取消訂單
                                    </button>
                                    
                                </template>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <V3RentalFormFind
            v-if="showFindModal"
            :show="showFindModal"
            :application="selectedApplication"
            :isEditMode="isEditMode" 
            @close="handleModalClose"
            @update="handleModalUpdate"
        />
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import { dateConfig } from '../../utils/MonthConfig.js'
import V3RentalFormFind from './V3RentalFormFind.vue' 

const props = defineProps({
    applications: {
        type: Array,
        required: true
    },
    loading: {
        type: Boolean,
        default: false
    },
    sortConfig: {
        type: Object,
        required: true
    }
})

const emit = defineEmits([
    'sort-table',
    'edit',
    'delete-success',
    'view'
])

const showFindModal = ref(false)
const selectedApplication = ref(null)
const isEditMode = ref(false)

const formatDate = (date) => {
    return dateConfig.formatFullDate(date)
}

const sortedApplications = computed(() => {
    console.log('應用程序數據更新:', props.applications)  // 添加這行來確認數據更新

    return [...props.applications].sort((a, b) => {
        const aVal = a[props.sortConfig.key]
        const bVal = b[props.sortConfig.key]
        
        if (aVal === null || aVal === undefined) return 1
        if (bVal === null || bVal === undefined) return -1
        
        const compareResult = aVal > bVal ? 1 : aVal < bVal ? -1 : 0
        return props.sortConfig.order === 'asc' ? compareResult : -compareResult
    })
})

const getStatusClass = (status) => {
    switch (status) {
        case '送出申請':
            return 'status-pending'
        case '審核中':
            return 'status-reviewing'
        case '檔期已確認':
            return 'status-confirmed'
        case '訂單已取消':
            return 'status-cancelled'
        default:
            return ''
    }
}

const sortTable = (key) => {
    emit('sort-table', key)
}

const handleView = (application) => {
    selectedApplication.value = application
    showFindModal.value = true    
    isEditMode.value = false
}

const handleEdit = async (application) => {
    try {
        let updatedApplication = application;

        // 檢查當前狀態是否為審核中
        if (application.orderStatus !== '審核中') {
            // 如果不是審核中，才調用 API
            const response = await axios.put(`${import.meta.env.VITE_API_URL}/api/booking/ad/${application.bookingId}/under-review`);
            
            if (response.status === 200) {
                updatedApplication = {
                    ...application,
                    ...response.data,
                    orderStatus: '審核中'
                };
                
                // 發出更新事件
                emit('view', updatedApplication);
                
                // 顯示狀態更新提示
                await Swal.fire({
                    title: '狀態更新',
                    text: '已進入審核狀態',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: 1500
                });
            }
        }
        
        // 不管是否調用 API，都打開修改視窗
        selectedApplication.value = updatedApplication;
        showFindModal.value = true;
        isEditMode.value = true;
        
    } catch (error) {
        console.error('狀態更新失敗:', error);
        
        await Swal.fire({
            title: '錯誤',
            text: error.response?.data?.message || '狀態更新失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        });
    }
}
const handleModalClose = () => {
    showFindModal.value = false
    selectedApplication.value = null
    isEditMode.value = false
}

const handleModalUpdate = (updatedApplication) => {
    emit('view', updatedApplication)
}

const handleConfirm = async (application) => {
    try {
        const result = await Swal.fire({
            title: '確認檔期',
            text: `確定要確認申請編號：${application.bookingId} 的檔期嗎？`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#4A6741',
            cancelButtonColor: '#d33',
            confirmButtonText: '確認',
            cancelButtonText: '取消'
        });

        if (result.isConfirmed) {
            const response = await axios.put(`${import.meta.env.VITE_API_URL}/api/booking/ad/${application.bookingId}/confirm`);

            // 檢查 response.status 是否為 200
            if (response.status === 200) {
                // 使用 API 回傳的資料來更新
                const updatedBooking = {
                    ...application,
                    ...response.data,  // 合併後端回傳的新資料
                    orderStatus: '檔期已確認'
                };

                // 發出更新事件
                emit('view', updatedBooking);

                await Swal.fire({
                    title: '成功',
                    text: '檔期確認成功',
                    icon: 'success',
                    confirmButtonColor: '#4A6741'
                });
            } else {
                throw new Error('檔期確認失敗');
            }
        }
    } catch (error) {
        console.error('檔期確認失敗:', error);
        
        await Swal.fire({
            title: '錯誤',
            text: error.response?.data?.message || error.message || '檔期確認失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        });
    }
};

const handleCancel = async (application) => {
    try {
        const result = await Swal.fire({
            title: '確定要取消訂單嗎？',
            text: `您即將取消申請編號：${application.bookingId}`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#4A6741',
            cancelButtonColor: '#d33',
            confirmButtonText: '確定取消',
            cancelButtonText: '返回'
        })

        if (result.isConfirmed) {
            // 發送取消請求到 API
            const response = await axios.put(`${import.meta.env.VITE_API_URL}/api/booking/ad/${application.bookingId}/cancel`)
            
            // 檢查 response 的資料結構
            console.log('API Response:', response.data)
            
            // 檢查 response 是否包含必要的資料
            if (response.data && response.status === 200) {
                // 更新前端狀態
                const updatedApplication = {
                    ...application,
                    orderStatus: '訂單已取消'
                }
                
                // 發出更新事件
                emit('view', updatedApplication)
                
                // 顯示成功訊息
                await Swal.fire({
                    title: '成功',
                    text: '訂單取消成功',
                    icon: 'success',
                    confirmButtonColor: '#4A6741'
                })
            } else {
                // 如果回應不符合預期，拋出錯誤
                throw new Error(response.data.message || '取消訂單失敗')
            }
        }
    } catch (error) {
        // 更詳細的錯誤記錄
        console.error('取消訂單失敗:', {
            error: error,
            message: error.message,
            response: error.response?.data
        })
        
        // 顯示錯誤訊息
        await Swal.fire({
            title: '錯誤',
            text: error.response?.data?.message || '取消訂單失敗，請稍後再試',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        })
    }
}

</script>

<style scoped>
.card {
    font-family: 'Kantumruy Pro', sans-serif;
    background: white;
    border-radius: 8px;
    position: relative;
}
    .card.loading::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    z-index: 1;
}

.card.loading::after {
    content: '載入中...';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 2;
    color: var(--primary-color);
}

.card-header {
    margin-bottom: 20px;
}

.title {
    font-size: 20px;
    font-weight: bold;
    color: var(--text-primary);
}

.table-container {
    /* overflow-x: auto; */
    max-height: 51vh;
    overflow-y: auto;
    border-radius: 10px;

}

.venue-table {
    width: 100%;
    border-collapse: collapse;
    border: 1px solid var(--border-color);
    font-size: 15px;
    border-radius: 5px;
}

.venue-table th,
.venue-table td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid var(--border-color);
}

/* 設定各欄位寬度 */
.venue-table th:nth-child(1),
.venue-table td:nth-child(1) {
    width: 100px;  /* 申請編號 */
    min-width: 100px;
}

.venue-table th:nth-child(2),
.venue-table td:nth-child(2) {
    width: 170px;  /* 租借場地 */
    min-width: 170px;
}

.venue-table th:nth-child(3),
.venue-table td:nth-child(3),
.venue-table th:nth-child(4),
.venue-table td:nth-child(4) {
    width: 150px;  /* 使用起日和迄日 */
    min-width: 150px;
    }

.venue-table th:nth-child(5),
.venue-table td:nth-child(5) {
    width: 160px;  /* 申請狀態 */
    min-width: 160px;
}

.venue-table th:nth-child(6),
.venue-table td:nth-child(6) {
    width: 180px;  /* 主辦單位 */
    min-width: 180px;
}

.venue-table th:nth-child(7),
.venue-table td:nth-child(7) {
    width: 120px;  /* 申請日期 */
    min-width: 120px;
    font-size: 14px;
}

.venue-table th:nth-child(8),
.venue-table td:nth-child(8) {
    width: 250px;  /* 操作欄位 */
    min-width: 250px;
}

.venue-table th {
    background-color: var(--primary-color);
    color: white;
    font-weight: 600;
    font-size: 16px;
}

.sortable {
    cursor: pointer;
}

.sort-icon {
    font-size: 14px;
    margin-left: 4px;
}

.venue-name {
    white-space: pre-wrap;
}

.status-tag {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 14px;
    font-weight: 500;
}

.status-pending {
    background-color: #f6bd60;
    color: rgb(53, 48, 48);
}

.status-reviewing {
    background-color: #8AA682;
    color: white;
}

.status-confirmed {
    background-color: #4A6741;
    color: white;
}

.status-cancelled {
    background-color: #d3d4d9;
    color: black;
}

.btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
    margin-right: 8px;
}

.btn .icon {
    width: 16px;
    height: 16px;
}

.btn-view {
    background-color: #f6bd60;
    color: rgb(78, 74, 74);
}

.btn-view:hover {
    background-color: #ec9d25;
}

.btn-edit {
    background-color: #adc178;
    color: rgb(78, 74, 74);
}

.btn-edit:hover {
    background-color: #9baa6c9c;
}

.btn-cancel {
    background-color: #8d99ae;
    color: white;
}

.btn-cancel:hover {
    background-color: #ef233c;
}

.btn-confirm {
    background-color: #4A6741;
    color: white;
}

.btn-confirm:hover {
    background-color: #5C715E;
}

.action-buttons {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}

@media (max-width: 1200px) {
    .venue-table {
        font-size: 14px;
    }
    
    .btn {
        padding: 4px 8px;
        font-size: 12px;
    }
}

@media (max-width: 768px) {
    .venue-table {
        display: block;
        overflow-x: auto;
        white-space: nowrap;
    }

    .action-buttons {
        flex-direction: column;
        gap: 4px;
    }

    .btn {
        width: 100%;
        justify-content: center;
        margin-right: 0;
        margin-bottom: 4px;
    }
}

/* 捲動條樣式 */
.table-container::-webkit-scrollbar {
    height: 8px;
    width: 8px;
}

.table-container::-webkit-scrollbar-track {
    background: var(--background-color);
    border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb {
    background: var(--secondary-color);
    border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb:hover {
    background: var(--primary-color);
}
.table-container::-webkit-scrollbar-corner {
    background: #f1f1f1;
}

/* 確保表頭固定 */
.venue-table thead th {
    position: sticky;
    top: 0;
    z-index: 1;
    background-color: var(--primary-color);
}
/* 列印樣式 */
@media print {
    .btn {
        display: none;
    }

    .venue-table {
        border: 1px solid #000;
    }

    .venue-table th,
    .venue-table td {
        border: 1px solid #000;
    }

    .status-tag {
        border: 1px solid #000;
        background: none !important;
        color: #000 !important;
    }
}
</style>