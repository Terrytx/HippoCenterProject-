<!-- V1RentalVenueTable.vue -->
<template>
    <div class="card" :class="{ 'loading': loading }">
        <div class="card-header">
            <h3 class="title">場地列表</h3>
        </div>

        <div class="table-container">
            <table class="venue-table">
                <thead>
                    <tr>
                        <th @click="sortTable('venueId')" class="sortable">
                            場地編號
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('venueName')" class="sortable">
                            場地名稱
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('capacity')" class="sortable">
                            容納人數
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('venueHeight')" class="sortable">
                            場地高度
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('areaPings')" class="sortable">
                            場地坪數
                            <span class="sort-icon">↕</span>
                        </th>
                        <th @click="sortTable('venueFeeByDay')" class="sortable">
                            租金
                            <span class="sort-icon">↕</span>
                        </th>
                        <th>租借狀態</th>
                        <th>規格</th>
                        <th>編輯</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="venue in sortedVenues" :key="venue.venueId">
                        <!-- 場地編號 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <input
                                    type="text"
                                    v-model="venue.venueId"
                                    class="form-control"
                                    disabled
                                />
                            </template>
                            <span v-else>{{ venue.venueId }}</span>
                        </td>

                        <!-- 場地名稱 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <input
                                    type="text"
                                    v-model="venue.venueName"
                                    class="form-control"
                                />
                            </template>
                            <span v-else>{{ venue.venueName }}</span>
                        </td>

                        <!-- 容納人數 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <input
                                    type="number"
                                    v-model="venue.capacity"
                                    class="form-control"
                                    min="0"
                                />
                            </template>
                            <span v-else-if="venue.capacity" class="tag">
                                {{ venue.capacity }}人
                            </span>
                        </td>

                        <!-- 場地高度 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <input
                                    type="number"
                                    v-model="venue.venueHeight"
                                    class="form-control"
                                    min="0"
                                    step="0.1"
                                />
                            </template>
                            <span v-else-if="venue.venueHeight" class="tag">
                                {{ venue.venueHeight }}公尺
                            </span>
                        </td>

                        <!-- 場地坪數 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <input
                                    type="number"
                                    v-model="venue.areaPings"
                                    class="form-control"
                                    min="0"
                                />
                            </template>
                            <span v-else-if="venue.areaPings" class="tag">
                                {{ venue.areaPings }}坪
                            </span>
                        </td>

                        <!-- 租金 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <input
                                    type="number"
                                    v-model="venue.venueFeeByDay"
                                    class="form-control"
                                    min="0"
                                />
                            </template>
                            <span v-else class="fee">
                                {{ venue.venueFeeByDay }}{{ venue.venueFeeByDay ? ' 元/日' : '' }}
                            </span>
                        </td>

                        <!-- 租借狀態 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <div class="toggle-switch">
                                    <input
                                        type="checkbox"
                                        :id="'switch-' + venue.venueId"
                                        v-model="venue.rentalStatue"
                                    />
                                    <label :for="'switch-' + venue.venueId">
                                        {{ venue.rentalStatue ? '可租借' : '不可租借' }}
                                    </label>
                                </div>
                            </template>
                            <span 
                                v-else 
                                class="status-tag"
                                :class="venue.rentalStatue ? 'status-available' : 'status-unavailable'"
                            >
                                {{ venue.rentalStatue ? '可租借' : '不可租借' }}
                            </span>
                        </td>

                        <!-- 規格 -->
                        <td>
                            <template v-if="venue.isEditing">
                                <div class="file-actions">
                                    <label class="file-upload-btn">
                                        <input
                                            type="file"
                                            @change="(e) => handleFileUpload(e, venue, 'technicalSpecifications')"
                                            accept="application/pdf"
                                            class="hidden"
                                        />
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                            <polyline points="17 8 12 3 7 8"/>
                                            <line x1="12" y1="3" x2="12" y2="15"/>
                                        </svg>
                                        上傳
                                    </label>
                                    <!-- 確認上傳按鈕 (只在選擇檔案後顯示) -->
                                    <button
                                        v-if="venue.pendingFile"
                                        @click="confirmFileUpload(venue)"
                                        class="confirm-upload-btn"
                                        >
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <polyline points="20 6 9 17 4 12"/>
                                        </svg>
                                        確認上傳
                                    </button>

                                    <button
                                        v-if="venue.technicalSpecifications"
                                        class="download-btn"
                                        @click="handleDownload(venue.venueId)"
                                    >
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                            <polyline points="7 10 12 15 17 10"/>
                                            <line x1="12" y1="15" x2="12" y2="3"/>
                                        </svg>
                                        下載
                                    </button>
                                </div>
                            </template>
                            <button
                                v-else-if="venue.technicalSpecifications"
                                class="download-btn"
                                @click="handleDownload(venue.venueId)"
                            >
                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                    <polyline points="7 10 12 15 17 10"/>
                                    <line x1="12" y1="15" x2="12" y2="3"/>
                                </svg>
                                下載
                            </button>
                        </td>

                        <!-- 編輯按鈕 -->
                        <td>
                            <div class="action-buttons">
                                <template v-if="venue.isEditing">
                                    <button class="btn btn-success" @click="handleSave(venue)">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <polyline points="20 6 9 17 4 12"/>
                                        </svg>
                                        儲存
                                    </button>
                                    <button class="btn btn-danger" @click="handleCancel(venue)">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <line x1="18" y1="6" x2="6" y2="18"/>
                                            <line x1="6" y1="6" x2="18" y2="18"/>
                                        </svg>
                                        取消
                                    </button>
                                </template>
                                <template v-else>
                                    <button class="btn btn-edit" @click="handleEdit(venue)">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                        </svg>
                                        修改
                                    </button>
                                    <button class="btn btn-delete" @click="handleDelete(venue)">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M3 6h18"/>
                                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/>
                                            <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                                            <line x1="10" y1="11" x2="10" y2="17"/>
                                            <line x1="14" y1="11" x2="14" y2="17"/>
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
</template>

<script setup>
import { computed } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'


const props = defineProps({
    venues: {
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
    'save',
    'cancel',
    'download',
    'file-upload',
    'delete-success'
])

// 排序後的場地列表
const sortedVenues = computed(() => {
    return [...props.venues].sort((a, b) => {
        const aVal = a[props.sortConfig.key]
        const bVal = b[props.sortConfig.key]
        
        if (aVal === null || aVal === undefined) return 1
        if (bVal === null || bVal === undefined) return -1
        
        const compareResult = aVal > bVal ? 1 : aVal < bVal ? -1 : 0
        return props.sortConfig.order === 'asc' ? compareResult : -compareResult
    })
})

// 事件處理方法
const sortTable = (key) => {
    emit('sort-table', key)
}

const handleEdit = (venue) => {
    venue.pendingFile = null;
    emit('edit', venue)
}

const handleSave = (venue) => {
    emit('save', venue)
}

const handleCancel = (venue) => {
    emit('cancel', venue)
}

const handleDownload = (venueId) => {
    emit('download', venueId)
}
const validateFile = (file) => {
    // 檢查檔案是否存在
    if (!file) {
        return "請選擇檔案";
    }

    // 檢查檔案類型
    if (file.type !== 'application/pdf') {
        return "只能上傳 PDF 檔案";
    }

    // 檢查檔案大小 (例如限制在 10MB)
    const maxSize = 10 * 1024 * 1024; // 10MB in bytes
    if (file.size > maxSize) {
        return "檔案大小不能超過 10MB";
    }

    return null; // 驗證通過
};

const handleFileUpload = (event, venue, fileType) => {
    const file = event.target.files[0];
    console.log('Selected file:', file);
    console.log('Venue:', venue);
    console.log('File type:', fileType);
    
    if (!file) return;
    
    // 檢查檔案類型
    if (file.type !== 'application/pdf') {
        Swal.fire({
            title: '錯誤',
            text: '只能上傳 PDF 檔案',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        });
        event.target.value = '';
        return;
    }

    // 儲存待上傳的檔案
    venue.pendingFile = {
        file,
        fileType
    };
    
    console.log('Pending file set:', venue.pendingFile);
};
// 確認上傳檔案
const confirmFileUpload = async (venue) => {
    try {
        if (!venue.pendingFile) return;

        const formData = new FormData();
        formData.append('venueId', venue.venueId);
        formData.append('fileType', venue.pendingFile.fileType);
        formData.append('file', venue.pendingFile.file);

        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/venue/modify/upload-file`,
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        );

        if (response.data.success) {
            // 更新場地資料
            const updatedVenue = response.data.list[0];
            if (venue.pendingFile.fileType === 'technicalSpecifications') {
                venue.technicalSpecifications = updatedVenue.technicalSpecifications;
            } else {
                venue.rentalRegulation = updatedVenue.rentalRegulation;
            }
            
            // 清除待上傳檔案
            venue.pendingFile = null;

            // 詢問是否繼續編輯
            const result = await Swal.fire({
                title: '上傳成功',
                text: '檔案已成功上傳，是否繼續編輯？',
                icon: 'success',
                showCancelButton: true,
                confirmButtonColor: '#4A6741',
                cancelButtonColor: '#6c757d',
                confirmButtonText: '繼續編輯',
                cancelButtonText: '完成'
            });

            // 根據用戶選擇決定是否關閉編輯模式
            if (!result.isConfirmed) {
                venue.isEditing = false;
            }
        }
    } catch (error) {
        console.error('檔案上傳失敗:', error);
        await Swal.fire({
            title: '錯誤',
            text: error.response?.data?.message || '檔案上傳失敗',
            icon: 'error',
            confirmButtonColor: '#4A6741'
        });
    }
};
const handleDelete = async (venue) => {
    try {
        // 使用 SweetAlert 顯示確認對話框
        const result = await Swal.fire({
            title: '確定要刪除嗎？',
            text: `您即將刪除場地：${venue.venueName}`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#4A6741',
            cancelButtonColor: '#d33',
            confirmButtonText: '確定刪除',
            cancelButtonText: '取消',
            customClass: {
                container: 'my-swal'
            }
        })

        // 如果用戶點擊確認
        if (result.isConfirmed) {
            // 調用刪除 API
            const response = await axios.delete(
                `${import.meta.env.VITE_API_URL}/api/venue/delete/${venue.venueId}`
            )

            if (response.data.success) {
                // 顯示成功提示
                await Swal.fire({
                    title: '刪除成功！',
                    text: '場地已成功刪除',
                    icon: 'success',
                    confirmButtonColor: '#4A6741',
                    customClass: {
                        container: 'my-swal'
                    }
                })
                      // 重新載入場地列表
                    try {
                    const venueResponse = await axios.get(`${import.meta.env.VITE_API_URL}/api/venue/findall`)
                    if (venueResponse.data.success) {
                        // 發送刪除成功事件，並傳遞新的場地列表
                        emit('delete-success', venueResponse.data.list)
                    } else {
                        throw new Error(venueResponse.data.message || '無法獲取最新場地列表')
                    }
                } catch (error) {
                    console.error('重新載入場地列表失敗:', error)
                    Swal.fire({
                        title: '警告',
                        text: '場地已刪除，但重新載入列表失敗，請手動刷新頁面',
                        icon: 'warning',
                        confirmButtonColor: '#4A6741',
                        customClass: {
                            container: 'my-swal'
                        }
                    })
                }
            } else {
                // 顯示錯誤提示 else {
                // 顯示錯誤提示
                await Swal.fire({
                    title: '刪除失敗',
                    text: response.data.message || '刪除場地時發生錯誤',
                    icon: 'error',
                    confirmButtonColor: '#4A6741',
                    customClass: {
                        container: 'my-swal'
                    }
                })
            }
        }
    } catch (error) {
        console.error('刪除場地時發生錯誤:', error)
        // 顯示錯誤提示
        await Swal.fire({
            title: '系統錯誤',
            text: error.response?.data?.message || '刪除場地時發生錯誤',
            icon: 'error',
            confirmButtonColor: '#4A6741',
            customClass: {
                container: 'my-swal'
            }
        })
    }
}
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
    font-family: 'Kantumruy Pro', sans-serif;
    font-size: 20px;
    font-weight: bold;
    color: var(--text-primary);
}

.table-container {
    font-family: Kantumruy Pro;
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
    font-family: 'Kantumruy Pro', sans-serif; /* 新增：確保表格內容也使用相同字體 */

}

.venue-table th {
    background-color: var(--primary-color);
    color: white;
    font-weight: 600;
    font-size: 18px;
    font-family: 'Kantumruy Pro', sans-serif; /* 新增：確保表格內容也使用相同字體 */
}
.venue-table thead th {
    position: sticky;
    top: 0;
    z-index: 1;
    background-color: var(--primary-color);
}
.venue-table tbody tr:nth-child(even) {
    background-color: #F4F7F5;
}

.venue-table tbody tr:hover {
    background-color: var(--hover-color);
}

.sortable {
    cursor: pointer;
    position: relative;
}

.sort-icon {
    font-family: Kantumruy Pro;
    font-size: 14px;
    margin-left: 4px;
}

.form-control {
    font-family: 'Kantumruy Pro', sans-serif;
    width: 100%;
    padding: 6px 12px;
    border: 1px solid var(--border-color);
    border-radius: 4px;
    font-size: 15px;
    transition: all 0.2s;
}

.form-control:focus {
    outline: none;
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(74, 103, 65, 0.1);
}

.tag {
    background-color: transparent;
    color: var(--text-primary);
    padding: 0;
    border-radius: 0;
    font-size: 15px;
}

.status-tag {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 16px;
}

.status-available {
    background-color: #339736c0;
    color: white;
}

.status-unavailable {
    background-color: #e9b2af;
    color: white;
}

.fee {
    color: #F97316;
    font-weight: 600;
    font-size: 16px;

}

.toggle-switch {
    position: relative;
    display: inline-block;
}

.toggle-switch input {
    opacity: 0;
    width: 0;
    height: 0;
}

.toggle-switch label {
    position: relative;
    display: inline-block;
    padding-left: 50px;
    cursor: pointer;
}

.toggle-switch label::before {
    content: '';
    position: absolute;
    left: 0;
    width: 40px;
    height: 20px;
    background-color: #ccc;
    border-radius: 20px;
    transition: all 0.3s;
}

.toggle-switch label::after {
    content: '';
    position: absolute;
    left: 2px;
    top: 2px;
    width: 16px;
    height: 16px;
    background-color: white;
    border-radius: 50%;
    transition: all 0.3s;
}

.toggle-switch input:checked + label::before {
    background-color: var(--primary-color);
}

.toggle-switch input:checked + label::after {
    transform: translateX(20px);
}

.btn {
    font-family: 'Kantumruy Pro', sans-serif;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 15px;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
}

.btn .icon {
    width: 10px;
    height: 10px;
}
.file-upload-btn .icon,
.download-btn .icon {
    width: 16px;
    height: 16px;
}

.file-actions {
display: flex;
gap: 8px;
align-items: center;
}

.confirm-upload-btn {
display: inline-flex;
align-items: center;
gap: 4px;
padding: 4px 12px;
border-radius: 4px;
font-size: 14px;
cursor: pointer;
background-color: var(--primary-color);
color: white;
border: none;
transition: all 0.2s;
}

.confirm-upload-btn:hover {
background-color: #3d563a;
}

.btn-success {
    background-color: var(--primary-color);
    color: white;
}

.btn-success:hover {
    background-color: #3d563a;
}

.btn-danger {
    background-color: #f8961e;
    color: white;
}

.btn-danger:hover {
    background-color: #e28615;
}

.btn-edit {
    background-color: var(--secondary-color);
    color: white;
}

.btn-edit:hover {
    background-color: #7b9674;
}

.action-buttons {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
}

.file-actions {
    display: flex;
    gap: 8px;
}

.file-upload-btn{
    display: inline-flex;
    align-items: center;
    gap: 4px;
    /* padding: 4px 8px; */
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    background-color: var(--secondary-color);
    color: white;
    border: none;
    transition: all 0.2s;
}

.file-upload-btn:hover,
.download-btn:hover {
    background-color: #7b9674;
}
/* 就像你的規格欄位設定一樣 */
.venue-table th:nth-child(7),
.venue-table td:nth-child(7) {
    min-width: 140px;  /* 設定租借狀態欄位最小寬度 */
    width: 140px;      /* 設定租借狀態欄位固定寬度 */
}
.venue-table th:nth-child(8),
.venue-table td:nth-child(8) {
    min-width: 140px;  /* 設定規格欄位最小寬度 */
    width: 140px;      /* 設定規格欄位固定寬度 */
}
.file-upload-btn,
.download-btn {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    background-color: var(--secondary-color);
    color: white;
    border: none;
    transition: all 0.2s;
    white-space: nowrap;  /* 防止文字換行 */
    min-width: auto;    
    justify-content: center; /* 文字置中 */
}

.hidden {
    display: none;
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
.btn-delete {
    background-color: #ff6163;
    color: white;
    transition: background-color 0.2s;
}

.btn-delete:hover {
    background-color: #df7373;
}

.btn-delete .icon {
    width: 16px;
    height: 16px;
}

/* 動畫效果 */
.btn {
    transition: all 0.2s ease;
}

.action-buttons {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
}


/* RWD 樣式 */
@media (max-width: 1200px) {
    .venue-table {
        font-size: 14px;
    }
    
    .btn {
        padding: 4px 8px;
        font-size: 12px;
    }

    .tag {
        font-size: 12px;
        padding: 2px 6px;
    }

    .form-control {
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

    .venue-table th,
    .venue-table td {
        padding: 8px;
        min-width: 100px;
    }

    .file-actions {
        flex-direction: column;
        gap: 4px;
    }

    .action-buttons {
        flex-direction: column;
        gap: 4px;
    }

    .btn {
        width: 100%;
        justify-content: center;
    }
}

/* 列印樣式 */
@media print {
    .btn,
    .file-actions {
        display: none;
    }

    .venue-table {
        border: 1px solid #000;
    }

    .venue-table th,
    .venue-table td {
        border: 1px solid #000;
    }

    .tag,
    .status-tag {
        border: 1px solid #000;
        background: none !important;
        color: #000 !important;
    }
}

/* 動畫效果 */
.venue-table tbody tr {
    transition: background-color 0.2s ease;
}

.btn, .tag, .status-tag {
    transition: all 0.2s ease;
}

.form-control {
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

/* 工具提示樣式 */
.sortable::after {
    content: attr(data-sort);
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    padding: 4px 8px;
    background-color: var(--text-primary);
    color: white;
    border-radius: 4px;
    font-size: 12px;
    opacity: 0;
    visibility: hidden;
    transition: all 0.2s ease;
    white-space: nowrap;
}

.sortable:hover::after {
    opacity: 1;
    visibility: visible;
}

/* 無數據時的樣式 */
.venue-table tbody:empty::after {
    content: '暫無數據';
    display: block;
    text-align: center;
    padding: 20px;
    color: var(--text-secondary);
    font-style: italic;
}

/* 載入動畫 */
@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.loading::after {
    content: '';
    display: inline-block;
    width: 20px;
    height: 20px;
    border: 2px solid var(--primary-color);
    border-top-color: transparent;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-left: 8px;
    vertical-align: middle;
}

/* 聚焦時的無障礙輪廓 */
.btn:focus,
.form-control:focus,
.file-upload-btn:focus,
.download-btn:focus {
    outline: 2px solid var(--primary-color);
    outline-offset: 2px;
}
</style>