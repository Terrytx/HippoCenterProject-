<!-- VenueRentalHeader.vue -->
<template>
    <div class="fixed-header">
        <div class="card">
            <div class="card-header">
                <h2 class="title">館場租借申請系統</h2>
                
                <div class="button-group">
                    <div class="search-input">
                        <input 
                            type="text"
                            v-model="searchText"
                            placeholder="輸入申請編號/場地/主辦單位/聯絡人/備註"
                            @keyup.enter="handleSearch"
                        /><svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="11" cy="11" r="8"/>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                        </svg>
                        <button 
                            class="btn btn-primary"
                            @click="handleSearch"
                            :disabled="loading"
                        >
                            <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="11" cy="11" r="8"/>
                                <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                            </svg>
                            {{ loading ? '搜尋中...' : '搜尋申請' }}
                        </button>
                    </div>
                    <button 
                        class="btn btn-info"
                        @click="handleViewSubmitted"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                            <polyline points="22 4 12 14.01 9 11.01"/>
                        </svg>
                        新申請案件
                    </button>
                    <button 
                        class="btn btn-secondary"
                        @click="handleViewPending"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="8" x2="12" y2="12"/>
                            <line x1="12" y1="16" x2="12" y2="16"/>
                        </svg>
                        待審核
                    </button>
                    <button 
                        class="btn btn-success"
                        @click="handleViewConfirmed"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M20 6L9 17l-5-5"/>
                        </svg>
                        檔期已確認
                    </button>
                    <button 
                        class="btn btn-outline"
                        @click="handleViewAll"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="8" y1="6" x2="21" y2="6"/>
                            <line x1="8" y1="12" x2="21" y2="12"/>
                            <line x1="8" y1="18" x2="21" y2="18"/>
                            <line x1="3" y1="6" x2="3.01" y2="6"/>
                            <line x1="3" y1="12" x2="3.01" y2="12"/>
                            <line x1="3" y1="18" x2="3.01" y2="18"/>
                        </svg>
                        所有申請
                    </button>
                    
                    <button 
                        class="btn btn-add"
                        @click="handleNewApplication"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"/>
                            <line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        新增申請
                    </button>
                </div>
            </div>

            <div class="stats-container">
                <div class="stat-card">
                    <div class="stat-title">
                        <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                            <line x1="3" y1="9" x2="21" y2="9"/>
                            <line x1="9" y1="21" x2="9" y2="9"/>
                        </svg>
                        總申請數
                    </div>
                    <div class="stat-value">{{ statistics.total }}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-title">
                        <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                            <polyline points="22 4 12 14.01 9 11.01"/>
                        </svg>
                        新案件
                    </div>
                    <div class="stat-value">{{ statistics.submitted }}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-title">
                        <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="8" x2="12" y2="12"/>
                            <line x1="12" y1="16" x2="12" y2="16"/>
                        </svg>
                        待審核
                    </div>
                    <div class="stat-value">{{ statistics.reviewing }}</div>

                </div>
                
            </div>
        </div>
    </div>
    <V3RentalFormInsert
            v-model:visible="showRentalForm"
            @submit-success="handleRentalSuccess"
        />
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import V3RentalFormInsert from './V3RentalFormInsert.vue'
import axios from 'axios'


// Props
const props = defineProps({
    applications: {
        type: Array,
        required: true
    },
    allApplications: {  // 新增這個 prop
        type: Array,
        required: true
    }
})

// Emits
const emit = defineEmits(['search','view-pending', 'view-all', 'new-application','view-submitted','view-confirmed','search-complete'])

// Refs
const searchApplicant = ref({
    bookingId: '',
    venue: '',
    organizer: '',
    contact: '',
    remarks: ''
    })
const searchText = ref('')

const loading = ref(false)
const error = ref(null)
const showRentalForm = ref(false)

const statistics = computed(() => {
    return {
        // 使用 allApplications 來計算統計數據
        total: props.allApplications.length,
        
        submitted: props.allApplications.filter(
            app => app.orderStatus === '送出申請'
        ).length,
        
        reviewing: props.allApplications.filter(
            app => app.orderStatus === '審核中'
        ).length,
        
        confirmed: props.allApplications.filter(
            app => app.orderStatus === '檔期已確認'
        ).length,
        
        cancelled: props.allApplications.filter(
            app => app.orderStatus === '訂單已取消'
        ).length
    }
})
// Methods
const handleSearch = async () => {
    try {
        loading.value = true
        console.log('搜尋文字:', searchText.value)

        emit('search', searchText.value)  // 修改這行，改為發送搜尋文字
        
    } catch (error) {
        console.error('搜尋失敗:', error)
    } finally {
        loading.value = false
    }
}
watch(searchText, (newVal) => {
    console.log('searchText changed:', newVal)
})
const handleViewPending = () => {
    emit('view-pending')
}

const handleViewAll = () => {
    emit('view-all')
}

const handleNewApplication = () => {
    showRentalForm.value = true
}
const handleRentalSuccess = () => {
    emit('view-all') // 重新載入列表
}
const handleViewSubmitted = () => {
    emit('view-submitted')
}
const handleViewConfirmed = () => {
    emit('view-confirmed')
}
</script>

<style scoped>
:root {
    --primary-color: #4A6741;
    --secondary-color: #8AA682;
    --hover-color: #C2D5BB;
    --background-color: #F5F7F4;
    --text-primary: #2C3E2D;
    --text-secondary: #5C715E;
    --border-color: #D8E0D5;
}

.fixed-header {
    /* position: fixed; */
    background-color: var(--background-color);
/* position: fixed; */
padding-top: 5%;
box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
z-index: 40;
}


.card {
    background: white;
    border-radius: 0.5rem;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    padding: 1.5rem;
    border: 1px solid var(--border-color);
    position: fixed; 
    width: 76.7%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.title {
    font-size: 1.25rem;
    font-weight: bold;
    color: var(--text-primary);
}

.button-group {
    display: flex;
    gap: 8px;
    align-items: center;
}

.search-input {
    position: relative;
    width: 500px;
    display: flex;  /* 添加這行 */
    gap: 8px;      /* 添加這行，設置元素間距 */
    align-items: center;  

}

.search-input input {
    flex: 1;       /* 添加這行，讓輸入框佔據剩餘空間 */
    width: 100%; 
    padding: 8px 12px;
    padding-left: 36px;
    border: 1px solid var(--border-color);
    border-radius: 4px;
    font-size: 16px;
    color: var(--text-primary);
}

.search-input input:focus {
    outline: none;
    border-color: var(--primary-color);
}

.search-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: var(--text-secondary);
}

.btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid transparent;
}

.icon {
    color: currentColor;
}

.btn-primary {
    background-color: var(--primary-color);
    color: white;
}

.btn-primary:hover {
    background-color: #3d563a;
}

.btn-secondary {
    background-color: var(--secondary-color);
    color: white;
}

.btn-secondary:hover {
    background-color: #7b9674;
}

.btn-outline {
    border: 1px solid var(--border-color);
    color: var(--text-primary);
    background-color: white;
}

.btn-outline:hover {
    background-color: var(--hover-color);
}

.btn-add {
    background-color: #f8961e;
    color: white;
}

.btn-add:hover {
    background-color: #F97316;
}

.stats-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-top: 20px;
}

.stat-card {
    padding: 16px;
    border-radius: 4px;
    background-color: white;
    border: 1px solid var(--border-color);
}

.stat-title {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--text-secondary);
    font-size: 16px;
    margin-bottom: 4px;
}

.stat-value {
    font-size: 26px;
    font-weight: 600;
    color: var(--primary-color);
}
.btn-info {
    background-color: #f6bd60;
    color: black;
}

.btn-info:hover {
    background-color: #ffbe0b;
}
.btn-success {
    background-color: #5C715E;
    color: white;
}

.btn-success:hover {
    background-color: #2C3E2D;
}
</style>