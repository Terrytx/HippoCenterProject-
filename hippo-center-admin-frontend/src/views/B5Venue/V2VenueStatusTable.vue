<!-- ScheduleTable.vue -->
<template>
    <div class="w-full">
        <div class="card" :class="{ 'loading': loading }">
            <!-- Header -->
            <div class="card-header">
                <h3 class="title">場地檔期列表</h3>
            </div>
    
            <!-- 表格外層容器 -->
            <div class="table-wrapper">
                <!-- 固定表頭區域 -->
                <div class="table-header">
                    <table class="venue-table">
                        <thead>
                            <tr>
                                <th 
                                    v-for="column in columns" 
                                    :key="column.key"
                                    class="sortable"
                                    :style="{ width: column.width }"
                                    @click="column.sortable ? handleSort(column.key) : null"
                                >
                                    {{ column.label }}
                                    <span v-if="column.sortable" class="sort-icon">
                                        {{ sortConfig.key === column.key ? (sortConfig.order === 'asc' ? '▲' : '▼') : '' }}
                                    </span>
                                </th>
                            </tr>
                        </thead>
                    </table>
                </div>
    
                <!-- 可滾動的內容區域 -->
                <div class="table-body">
                    <table class="venue-table">
                        <tbody>
                            <tr v-for="row in sortedSchedules" 
                                :key="row.venueStatusId" 
                                class="table-row"
                            >
                                <!-- 日期欄位 -->
                                <td>{{ formatDate(row.date) }}</td>
    
                                <!-- 場地欄位 -->
                                <td>
                                    {{ currentVenue?.venueId || '-' }} - {{ currentVenue?.venueName || '-' }}
                                </td>
    
                                <!-- 狀態欄位 -->
                                <td>
                                    <template v-if="editingRow?.venueStatusId === row.venueStatusId">
                                        <select 
                                            v-model="editForm.status"
                                            class="form-control"
                                        >
                                            <option value="休館日">休館日</option>
                                            <option value="保養日">保養日</option>
                                            <option value="已出租">已出租</option>
                                        </select>
                                    </template>
                                    <template v-else>
                                        <span 
                                            class="status-tag"
                                            :class="getStatusClass(row.status)"
                                        >
                                            {{ row.status }}
                                        </span>
                                    </template>
                                </td>
    
                                <!-- 訂單編號欄位 -->
                                <!-- <td>
                                    <template v-if="editingRow?.venueStatusId === row.venueStatusId">
                                        <input 
                                            v-model="editForm.bookingId"
                                            type="text"
                                            class="form-control"
                                            placeholder="請輸入訂單編號"
                                        />
                                    </template>
                                    <template v-else>
                                        {{ row.bookingId || '-' }}
                                    </template>
                                </td> -->
    
                                <!-- 備註欄位 -->
                                <td>
                                    <template v-if="editingRow?.venueStatusId === row.venueStatusId">
                                        <textarea
                                            v-model="editForm.note"
                                            rows="2"
                                            class="form-control"
                                            placeholder="請輸入備註"
                                        ></textarea>
                                    </template>
                                    <template v-else>
                                        <span class="note-text">{{ row.note || '-' }}</span>
                                    </template>
                                </td>
    
                                <!-- 建立日期欄位 -->
                                <td>{{ formatDateTime(row.createdTime) }}</td>
    
                                <!-- 更新日期欄位 -->
                                <td>{{ formatDateTime(row.updatedTime) }}</td>
    
                                <!-- 操作欄位 -->
                                <td>
                                    <div class="action-buttons">
                                        <template v-if="editingRow?.venueStatusId === row.venueStatusId">
                                            <button
                                                @click="handleSaveClick(row)"
                                                class="btn btn-success"
                                            >
                                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <polyline points="20 6 9 17 4 12"/>
                                                </svg>
                                                儲存
                                            </button>
                                            <button
                                                @click="handleCancel"
                                                class="btn btn-cancel"
                                            >
                                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <line x1="18" y1="6" x2="6" y2="18"/>
                                                    <line x1="6" y1="6" x2="18" y2="18"/>
                                                </svg>
                                                取消
                                            </button>
                                        </template>
                                        <template v-else>
                                            <button
                                                @click="handleEdit(row)"
                                                class="btn btn-edit"
                                            >
                                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                                </svg>
                                                修改
                                            </button>
                                            <button
                                                @click="handleDelete(row)"
                                                class="btn btn-delete"
                                            >
                                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <path d="M3 6h18"/>
                                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/>
                                                    <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                                                </svg>
                                                刪除
                                            </button>
                                        </template>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import Swal from 'sweetalert2'

// Props
const props = defineProps({
    schedules: {
        type: Array,
        required: true,
        default: () => []
    },
    loading: {
        type: Boolean,
        default: false
    },
    currentVenue: {
        type: Object,
        default: null
    },
    editingRow: {
        type: Object,
        default: null
    },
    isEditing: {
        type: Boolean,
        default: false
    }
})

// Emits
const emit = defineEmits(['edit', 'delete', 'save', 'cancel', 'sort'])

// State
const editForm = ref({})
const sortConfig = ref({
    key: 'date',
    order: 'asc'
})

// Column definitions
const columns = [
    { key: 'date', label: '日期', sortable: true },
    { key: 'venue', label: '場地', sortable: false },
    { key: 'status', label: '狀態', sortable: true },
    // { key: 'bookingId', label: '訂單編號', width: '150px', sortable: true },
    { key: 'note', label: '備註', sortable: false },
    { key: 'createdTime', label: '建立日期', sortable: true },
    { key: 'updatedTime', label: '更新日期',  sortable: true },
    { key: 'actions', label: '操作',  sortable: false }
]

// Computed Properties
const sortedSchedules = computed(() => {
    // 檢查 props.schedules 是否存在，如果不存在返回空數組
    if (!props.schedules) return []
    
    // 使用展開運算符 [...] 創建 schedules 數組的複本進行排序
    return [...props.schedules].sort((a, b) => {
        // 根據 sortConfig 中的 key 獲取要比較的值
        const aValue = a[sortConfig.value.key]
        const bValue = b[sortConfig.value.key]
        
        // 根據 sortConfig 中的排序順序（升序/降序）進行排序
        if (sortConfig.value.order === 'asc') {
            // 升序排序：如果 aValue 大於 bValue，返回 1（交換位置）
            return aValue > bValue ? 1 : -1
        } else {
            // 降序排序：如果 aValue 小於 bValue，返回 1（交換位置）
            return aValue < bValue ? 1 : -1
        }
    })
})
// Methods
const formatDate = (date) => {
    if (!date) return '-'
    return new Date(date).toLocaleDateString('zh-TW')
}

const formatDateTime = (datetime) => {
    if (!datetime) return '-'
    return new Date(datetime).toLocaleString('zh-TW')
}

const getStatusClass = (status) => {
    const statusMap = {
        '休館日': 'status-closed',
        '已出租': 'status-rented',
        '保養日': 'status-maintainance'
    }
    return statusMap[status] || 'status-default'
}

const handleSort = (key) => {
    if (sortConfig.value.key === key) {
        sortConfig.value.order = sortConfig.value.order === 'asc' ? 'desc' : 'asc'
    } else {
        sortConfig.value.key = key
        sortConfig.value.order = 'asc'
    }
    emit('sort', { key, order: sortConfig.value.order })
}

const handleEdit = async (schedule) => {
    editForm.value = { ...schedule }
    emit('edit', schedule)
    
    await Swal.fire({
        icon: 'info',
        title: '開始編輯',
        text: '您現在可以編輯此筆資料',
        confirmButtonColor: '#4A6741',
        timer: 1500,
        timerProgressBar: true,
        showConfirmButton: false
    })
}

const handleDelete = async (row) => {
    if (!row.venueStatusId) {
        await Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '無法刪除：缺少場地狀態ID',
            confirmButtonColor: '#4A6741'
        })
        return
    }

    const result = await Swal.fire({
        icon: 'warning',
        title: '確認刪除',
        text: '您確定要刪除這筆資料嗎？此操作無法復原。',
        showCancelButton: true,
        confirmButtonColor: '#4A6741',
        cancelButtonColor: '#6b7280',
        confirmButtonText: '確定刪除',
        cancelButtonText: '取消'
    })

    if (result.isConfirmed) {
        try {
            emit('delete', row)
            await Swal.fire({
                icon: 'success',
                title: '刪除成功',
                text: '資料已成功刪除',
                confirmButtonColor: '#4A6741',
                timer: 1500
            })
        } catch (error) {
            await Swal.fire({
                icon: 'error',
                title: '刪除失敗',
                text: '刪除資料時發生錯誤，請稍後再試',
                confirmButtonColor: '#4A6741'
            })
        }
    }
}

const handleSaveClick = async (row) => {
    if (!editForm.value.venueStatusId) {
        await Swal.fire({
            icon: 'warning',
            title: '警告',
            text: '缺少場地狀態ID，請確認資料完整性',
            confirmButtonColor: '#4A6741'
        })
        return
    }

    const updateData = {
        venueStatusId: editForm.value.venueStatusId,
        status: editForm.value.status,
        bookingId: editForm.value.bookingId,
        note: editForm.value.note,
        date: editForm.value.date,
    }

    try {
        emit('save', updateData)
        await Swal.fire({
            icon: 'success',
            title: '儲存成功',
            text: '資料已成功更新',
            confirmButtonColor: '#4A6741',
            timer: 1500
        })
    } catch (error) {
        await Swal.fire({
            icon: 'error',
            title: '儲存失敗',
            text: '更新資料時發生錯誤，請稍後再試',
            confirmButtonColor: '#4A6741'
        })
    }
}

const handleCancel = async () => {
    const result = await Swal.fire({
        icon: 'question',
        title: '確認取消',
        text: '您確定要取消編輯嗎？未儲存的更改將會遺失。',
        showCancelButton: true,
        confirmButtonColor: '#4A6741',
        cancelButtonColor: '#6b7280',
        confirmButtonText: '確定取消',
        cancelButtonText: '繼續編輯'
    })

    if (result.isConfirmed) {
        editForm.value = {}
        emit('cancel')
    }
}

const initializeForm = () => {
    editForm.value = {
        status: '可預約',
        bookingId: '',
        note: '',
        date: new Date().toISOString().split('T')[0]
    }
}

// Watchers
watch(() => props.editingRow, (newVal, oldVal) => {
    if (newVal && !oldVal) {
        Swal.fire({
            toast: true,
            position: 'top-end',
            icon: 'info',
            title: '編輯模式已啟動',
            showConfirmButton: false,
            timer: 1500,
            timerProgressBar: true
        })
    }
}, { deep: true })

watch(() => props.schedules, (newVal) => {
    if (newVal && newVal.length > 0) {
        Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1500,
            timerProgressBar: true
        }).fire({
            icon: 'success',
            title: '資料已更新'
        })
    }
}, { deep: true })

// Error Handler
const handleError = async (error, operation) => {
    await Swal.fire({
        icon: 'error',
        title: `${operation}失敗`,
        text: error.message || `執行${operation}時發生錯誤，請稍後再試`,
        confirmButtonColor: '#4A6741'
    })
}

// Loading State Handler
const setLoading = (state) => {
    if (state) {
        Swal.fire({
            title: '處理中...',
            text: '請稍候',
            allowOutsideClick: false,
            allowEscapeKey: false,
            allowEnterKey: false,
            showConfirmButton: false,
            didOpen: () => {
                Swal.showLoading()
            }
        })
    } else {
        Swal.close()
    }
}

// Initialize
onMounted(() => {
    initializeForm()
})

// Expose necessary methods and computed properties
defineExpose({
    sortedSchedules,
    handleSort,
    handleEdit,
    handleDelete,
    handleSaveClick,
    handleCancel,
    setLoading,
    handleError
})
</script>
<style scoped>
.card {
    background: white;
    border-radius: 8px;
    font-family: 'Kantumruy Pro', sans-serif;
}

.card.loading::before {
    content: '';
    position: absolute;
    inset: 0;
    background: rgba(255, 255, 255, 0.8);
    z-index: 10;
}

.card.loading::after {
    content: '載入中...';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 11;
    color: #4A6741;
}
.card-header {
    margin-bottom: 20px;
}

.title {
    font-size: 20px;
    font-weight: bold;
    color: #2C3E2D;

}

.table-wrapper {
    position: relative;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    overflow: hidden;
}

.table-header {
    position: sticky;
    top: 0;
    z-index: 2;
    background-color: #4A6741;
}

.table-body {
    overflow-y: auto;
    max-height: calc(80vh - 300px);
}

.venue-table {
    width: 100%;
    border-collapse: collapse;
    table-layout: fixed;

}

.venue-table th {
    background-color: #4A6741;
    color: white;
    padding: 0.75rem 1rem;
    text-align: left;
    font-weight: 600;
    font-size: 16px;
    
}

.venue-table td {
    padding: 0.75rem 1rem;
    border-bottom: 1px solid #e5e7eb;
    background-color: white;

}

.venue-table th,
.venue-table td {
    padding: 0.75rem 1rem;
}

/* 為每個列設置固定寬度 */
.venue-table th:nth-child(1),
.venue-table td:nth-child(1) {
    width: 150px; /* 日期 */
}

.venue-table th:nth-child(2),
.venue-table td:nth-child(2) {
    width: 180px; /* 場地 */
}

.venue-table th:nth-child(3),
.venue-table td:nth-child(3) {
    width: 180px; /* 狀態 */
}

/* .venue-table th:nth-child(4),
.venue-table td:nth-child(4) {
    width: 100px; /* 訂單編號 
} */

.venue-table th:nth-child(4),
.venue-table td:nth-child(4) {
    width: auto; /* 備註 */
}

.venue-table th:nth-child(5),
.venue-table td:nth-child(5) {
    width: 180px; /* 建立日期 */
}

.venue-table th:nth-child(6),
.venue-table td:nth-child(6) {
    width: 180px; /* 更新日期 */
}

.venue-table th:nth-child(7),
.venue-table td:nth-child(7) {
    width: 200px; /* 操作 */
}

/* 調整表格內容區域的滾動 */
.table-body {
    overflow-y: auto;
    max-height: calc(80vh - 300px);
}

/* 確保內容區域的表格寬度與表頭一致 */
.table-header .venue-table,
.table-body .venue-table {
    min-width: 100%;
}

.table-row:hover {
    background-color: #f3f4f6;
}

.table-row:hover td {
    background-color: #f3f4f6;
}

/* 滾動條樣式 */
.table-body::-webkit-scrollbar {
    width: 8px;
}

.table-body::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
}

.table-body::-webkit-scrollbar-thumb {
    background: #8AA682;
    border-radius: 4px;
}

.table-body::-webkit-scrollbar-thumb:hover {
    background: #4A6741;
}

/* 狀態標籤樣式 */
.status-tag {
    display: inline-block;
    padding: 0.25rem 0.75rem;
    border-radius: 0.375rem;
    font-size: 15px;
    color: white;
}

.status-closed { background-color: #6b7280; }
.status-rented { background-color: #f59e0b; }
.status-maintainance { background-color: #8AA682; }
.status-default { background-color: #6b7280; }

/* 表單控制項樣式 */
.form-control {
    width: 100%;
    padding: 0.375rem 0.75rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.25rem;
    font-size: 15px;
    transition: all 0.2s;
}

.form-control:focus {
    outline: none;
    border-color: #4A6741;
    box-shadow: 0 0 0 2px rgba(74, 103, 65, 0.1);
}

/* 按鈕樣式 */
.action-buttons {
    display: flex;
    gap: 0.5rem;
    justify-content: flex-end;
}

.btn {
    display: inline-flex;
    align-items: center;
    gap: 0.25rem;
    padding: 0.375rem 0.75rem;
    border-radius: 0.25rem;
    font-size: 15px;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
    color: white;
}

.btn-success { background-color: #4A6741; }
.btn-success:hover { background-color: #3d563a; }

.btn-cancel { background-color: #6b7280; }
.btn-cancel:hover { background-color: #4b5563; }

.btn-edit { background-color: #4A6741; }
.btn-edit:hover { background-color: #3d563a; }

.btn-delete { background-color: #dc2626; }
.btn-delete:hover { background-color: #b91c1c; }

.icon {
    width: 1rem;
    height: 1rem;
}

/* 響應式設計 */
@media (max-width: 1200px) {
    .venue-table {
        font-size: 15px;
    }

    .btn {
        padding: 0.25rem 0.5rem;
        font-size: 15px;
    }
}

@media (max-width: 768px) {
    .table-wrapper {
        margin: 0;
    }

    .table-body {
        max-height: none;
    }

    .action-buttons {
        flex-direction: column;
        gap: 0.25rem;
    }

    .btn {
        width: 100%;
        justify-content: center;
    }
}

/* 列印樣式 */
@media print {
    .card {
        box-shadow: none;
    }

    .btn {
        display: none;
    }

    .table-body {
        max-height: none;
        overflow: visible;
    }

    .status-tag {
        border: 1px solid #000;
        background: none !important;
        color: #000 !important;
    }
}
</style>