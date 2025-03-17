<template>
<div class="header-container">
    <div class="content-card">
    <!-- Header -->
    <div class="header-wrapper">
        <h2 class="main-title">檔期查詢系統</h2>
        
        <div class="button-group">
        <!-- Venue Select -->
        <div class="venue-select">
            <button
            @click="showDropdown = !showDropdown"
            class="venue-select-button"
            ><svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/></svg>　
            {{ selectedVenueName || '選擇館場' }}
            
            </button>
            
            <div 
            v-if="showDropdown"
            class="venue-dropdown"
            >
            <div
                v-for="venue in venues"
                :key="venue.venueId"
                @click="selectVenue(venue)"
                class="venue-option"
            >
                <span class="venue-id">{{ venue.venueId }}</span>
                <span class="venue-name">{{ venue.venueName }}</span>
                <span :class="venue.rentalStatue ? 'status-available' : 'status-unavailable'">
                {{ venue.rentalStatue ? '可租借' : '不可租借' }}
                </span>
            </div>
            </div>
        </div>

        <!-- Month Picker -->
        <div class="relative">
            <div class="month-picker-container">
                <button 
                    @click="showMonthPicker = !showMonthPicker"
                    class="month-picker"
                >
                    {{ selectedYear }}年{{ selectedMonth + 1 }}月
                </button>
                <div class="month-picker-dropdown" v-if="showMonthPicker">    
                    <V2MonthPicker
                        v-model="selectedDate"
                        :selected-date="dateSelected"
                        :default-date="defaultDate"
                        @update:model-value="handleMonthPickerSelect"
                        v-click-outside="() => showMonthPicker = false"
                    />
                </div>     
            </div>
        </div>

        <!-- Action Buttons -->
        <div class="button-group">
            <button 
                @click="handleSearch"
                class="btn btn-primary"
            >
                <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="11" cy="11" r="8"/>
                    <path d="M21 21l-4.35-4.35"/>
                </svg>
                檔期列表
            </button>
            <!--
            <button 
                @click="handleGetAvailable"
                class="btn btn-secondary"
            >
                <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12"/>
                </svg>
                可預約時段
            </button> -->
            
            <button 
                @click="handleGetAll"
                class="btn btn-secondary"
            >
                <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="8" y1="6" x2="21" y2="6"/>
                    <line x1="8" y1="12" x2="21" y2="12"/>
                    <line x1="8" y1="18" x2="21" y2="18"/>
                    <line x1="3" y1="6" x2="3.01" y2="6"/>
                    <line x1="3" y1="12" x2="3.01" y2="12"/>
                    <line x1="3" y1="18" x2="3.01" y2="18"/>
                </svg>
                檔期行事曆
            </button>
            
            <button 
                @click="showBookingDialog = true"
                class="btn btn-add"
            >
                <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                新增預約
            </button>
            <button 
                @click="handleBatchUpload"
                class="btn btn-upload"
            >
                <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="17 8 12 3 7 8"/>
                    <line x1="12" y1="3" x2="12" y2="15"/>
                </svg>
                批量上傳
            </button>
        </div>
        </div>
    </div>

    <!-- Statistics -->
    <div class="stats-grid">
        <div 
        v-for="(stat, index) in statistics" 
        :key="index"
        class="stat-card"
        >
        <div class="stat-title">
            <span class="icon" v-html="stat.icon"></span>
            {{ stat.title }}
        </div>
        <div class="stat-value">
            {{ stat.value }}{{ stat.suffix || '' }}
        </div>
        </div>
    </div>
    </div>

        <V2VenueStatusInsert
    v-model:visible="showBookingDialog"
    @submit-success="handleBookingSuccess"
    />
</div>
</template>

<script setup>
import { ref, computed, onMounted,watch } from 'vue'
import { useRouter } from 'vue-router' 
import axios from 'axios'
import { ElMessage } from 'element-plus'
import V2VenueStatusInsert from './V2VenueStatusInsert.vue'
import V2MonthPicker from '../../components/buttons/b5/V2MonthPicker.vue'
import Swal from 'sweetalert2'
const router = useRouter()  

// Icons as SVG strings
const icons = {
calendar: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
    <line x1="16" y1="2" x2="16" y2="6"/>
    <line x1="8" y1="2" x2="8" y2="6"/>
    <line x1="3" y1="10" x2="21" y2="10"/>
</svg>`,
search: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <circle cx="11" cy="11" r="8"/>
    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
</svg>`,
check: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
    <polyline points="22 4 12 14.01 9 11.01"/>
</svg>`,
chart: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <line x1="18" y1="20" x2="18" y2="10"/>
    <line x1="12" y1="20" x2="12" y2="4"/>
    <line x1="6" y1="20" x2="6" y2="14"/>
</svg>`
}

// Props
const props = defineProps({
    schedules: {
        type: Array,
        required: true
    }
})

const selectedDate = ref(new Date())
const dateSelected = ref({
    year: new Date().getFullYear(),
    month: new Date().getMonth(),
    date: new Date().getDate()
})
const defaultDate = ref(new Date())



// Emits
const emit = defineEmits(['search', 'get-available', 'get-all'])

const showMonthPicker = ref(false)
const selectedYear = ref(new Date().getFullYear())
const selectedMonth = ref(new Date().getMonth())

const handleMonthSelect = ({ year, month }) => {
    selectedYear.value = year
    selectedMonth.value = month
    searchMonth.value = `${year}-${String(month + 1).padStart(2, '0')}`
    showMonthPicker.value = false
}
// 新的年月選擇處理函數改名為 handleMonthPickerSelect
const handleMonthPickerSelect = (date) => {
    console.log('handleMonthPickerSelect - received date:', date)
    
    selectedYear.value = date.getFullYear()
    selectedMonth.value = date.getMonth()
    
    console.log('handleMonthPickerSelect - selectedYear:', selectedYear.value)
    console.log('handleMonthPickerSelect - selectedMonth:', selectedMonth.value)
    
    searchMonth.value = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
    console.log('handleMonthPickerSelect - searchMonth set to:', searchMonth.value)
    
    showMonthPicker.value = false
}
// Refs
const searchMonth = ref('')
const searchVenue = ref('')
const venues = ref([])
const loadingVenues = ref(false)
const showBookingDialog = ref(false)
const showDropdown = ref(false)

// Computed
const selectedVenue = ref(null)
const selectedVenueName = computed(() => 
selectedVenue.value ? selectedVenue.value.venueName : ''
)

const statistics = computed(() => {
    // 獲取當月總天數
    const daysInMonth = new Date(selectedYear.value, selectedMonth.value + 1, 0).getDate()
    
    // 計算已預約檔期數量
    const bookedSchedules = props.schedules.filter(s => !s.isAvailable).length
    
    // 計算可預約檔期（當月總天數減去已預約檔期）
    const availableSchedules = daysInMonth - bookedSchedules
    
    return [
        {
            title: '總檔期數',
            value: props.schedules.length,
            icon: icons.calendar
        },
        {
            title: '可預約檔期',
            value: availableSchedules,
            icon: icons.search
        },
        {
            title: '已預約檔期',
            value: bookedSchedules,
            icon: icons.check
        },
        {
            title: '本月預約率',
            value: calculateBookingRate(),
            suffix: '%',
            icon: icons.chart
        }
    ]
})

// Methods
const calculateBookingRate = () => {
    // 獲取當月總天數
    const daysInMonth = new Date(selectedYear.value, selectedMonth.value + 1, 0).getDate()
    
    if (daysInMonth === 0) return 0
    const bookedSchedules = props.schedules.filter(s => !s.isAvailable).length
    return ((bookedSchedules / daysInMonth) * 100).toFixed(2)
}

const selectVenue = (venue) => {
    selectedVenue.value = venue
    searchVenue.value = venue.venueId
    showDropdown.value = false
}

const fetchVenues = async () => {
    loadingVenues.value = true
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/venue/findall`)
        if (response.data.success) {
            venues.value = response.data.list.map(venue => ({
                venueId: venue.venueId,
                venueName: venue.venueName,
                rentalStatue: venue.rentalStatue
            }))
        } else {
            ElMessage.warning(response.data.message || '無法獲取館場資料')
        }
    } catch (error) {
        console.error('獲取館場資料失敗:', error)
        ElMessage.error('獲取館場資料失敗')
    } finally {
        loadingVenues.value = false
    }
}

const handleSearch = () => {
    if (!searchMonth.value && !selectedVenue.value) {
        ElMessage.warning('請至少選擇場地或年月')
        return
    }
    
    emit('search', {
        month: searchMonth.value,
        venueId: selectedVenue.value?.venueId,
        venue: selectedVenue.value,
        viewType: 'table'  // 添加視圖類型標識
    })
}

const handleGetAvailable = () => {
    emit('get-available')
}

const handleGetAll = () => {
    // 構建月份字符串，確保月份是兩位數
    const month = `${selectedYear.value}-${String(selectedMonth.value + 1).padStart(2, '0')}`
    
    console.log('handleGetAll - selectedYear:', selectedYear.value)
    console.log('handleGetAll - selectedMonth:', selectedMonth.value)
    console.log('handleGetAll - constructed month:', month)
    console.log('handleGetAll - searchMonth:', searchMonth.value)
    console.log('handleGetAll - selectedVenue:', selectedVenue.value)
    
    // 如果沒有選擇館場也沒有選擇月份，顯示警告
    if (!month && !selectedVenue.value) {
        ElMessage.warning('請至少選擇場地或年月')
        return
    }
    
    const payload = {
        month: month,
        venueId: selectedVenue.value?.venueId,
        venue: selectedVenue.value,
    }
    
    console.log('handleGetAll - emitting payload:', payload)
    emit('get-all', payload)
}
const handleBookingSuccess = () => {
    handleGetAll() // 重新加載所有檔期
}

const handleBatchUpload = () => {
    // 創建一個隱藏的文件輸入元素
    const fileInput = document.createElement('input')
    fileInput.type = 'file'
    fileInput.accept = '.csv'
    
    fileInput.onchange = async (e) => {
        const file = e.target.files[0]
        if (!file) return
        
        // 顯示上傳中的提示
        Swal.fire({
            title: '上傳中...',
            text: '請稍候',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
                // 直接修改載入圖示的顏色
                const loader = document.querySelector('.swal2-loader')
                if (loader) {
                    loader.style.borderColor = '#4A6741 transparent #4A6741 transparent'
                }
            },
            confirmButtonColor: '#4A6741'
        })
        // 創建 FormData 對象
        const formData = new FormData()
        formData.append('file', file)
        
        try {
    const response = await axios.post(
        `${import.meta.env.VITE_API_URL}/venue-status/import`,
        formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }
    )
    
    // 檢查 response 狀態
    if (response.status === 200) {  // 如果後端沒有返回 success 字段，可以用 status 判斷
        await Swal.fire({
            icon: 'success',
            title: '成功',
            text: '檔期批量上傳成功',
            timer: 1500,
            iconColor: '#4A6741',
            confirmButtonColor: '#4A6741'
        })
        handleGetAll()
            } else {
                await Swal.fire({
                    icon: 'error',
                    title: '錯誤',
                    text: response.data.message || '上傳失敗',
                    // iconColor: '#4A6741',
                    confirmButtonColor: '#4A6741'
                })
            }
        } catch (error) {
            console.error('批量上傳失敗:', error)
            console.log('Error response:', error.response) // 添加這行來查看詳細錯誤信息
            await Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: error.response?.data?.message || '批量上傳失敗',
                // iconColor: '#4A6741',
                confirmButtonColor: '#4A6741'
            })
        }
    }
    fileInput.click()
}

watch(
    [selectedYear, selectedMonth],
    ([newYear, newMonth], [oldYear, oldMonth]) => {
        console.log('Year/Month watch triggered:')
        console.log('Old values:', { year: oldYear, month: oldMonth })
        console.log('New values:', { year: newYear, month: newMonth })
    }
)
// Lifecycle
onMounted(() => {
    fetchVenues()
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
--border-color: #D8E0D5;
}

.header-container {
/* top: 0;
width: 76.7%; */
background-color: var(--background-color);
/* position: fixed; */
padding-top: 5%;
box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
z-index: 40;

}

.content-card {
background-color: white;
border-radius: 0.5rem;
box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
padding: 1.5rem;
border: 1px solid var(--border-color);
position: fixed; 
width: 76.7%;
}

.header-wrapper {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 1.5rem;
}

.main-title {
font-size: 1.25rem;
font-weight: bold;
color: var(--text-primary);
}

.relative {
    position: relative;
    display: inline-block;
}

.month-picker-container {
    position: relative;
}

.month-picker-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    z-index: 1000;
    background: white;
    border-radius: 4px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.button-group {
    display: flex;
    gap: 8px;
    align-items: center;
}

.btn-upload {
    background-color: #577590;
    color: white;
    border: 1px solid #577590;
}

.btn-upload:hover {
    background-color: #43597b;
    border-color: #43597b;
}
/* Venue Select Styles */
.venue-select {
position: relative;
width: 340px;
z-index: 600;

}

.venue-select-button {
width: 100%;
padding: 0.5rem 1rem;
text-align: left;
background: white;
border: 1px solid var(--border-color);
color: var(--text-primary);
transition: all 0.2s;
align-items: center;

}
.venue-select-button,
.month-picker {
    border-radius: 4px;  /* 統一圓弧度 */
    font-size: 16px;    /* 統一文字大小 */
}

.venue-select-button:hover {
background-color: var(--background-color);

}

.venue-dropdown {
position: absolute;
width: 100%;
margin-top: 0.25rem;
background: white;
border: 1px solid var(--border-color);
border-radius: 4px;
box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
/* z-index: 1000; */
max-height: 190px;
overflow-y: auto;
scrollbar-width: thin;
scrollbar-color: var(--secondary-color) var(--background-color);
}

.venue-option {
padding: 0.5rem 1rem;
display: flex;
justify-content: space-between;
align-items: center;
cursor: pointer;
transition: all 0.2s;
}

.venue-option:hover {
background-color: var(--background-color);
}

.venue-id {
color: var(--text-secondary);
font-family: monospace;
}

.venue-name {
color: var(--text-primary);
font-weight: 500;
}

.status-available {
color: var(--primary-color);
}

.status-unavailable {
color: #DC2626;
}

/* Month Picker */
.month-picker {
width: 12rem;
padding: 0.5rem 1rem;
border: 1px solid var(--border-color);
border-radius: 0.5rem;
color: var(--text-primary);
background-color: white;
transition: all 0.2s;
}

.month-picker:hover {
background-color: var(--background-color);
}

/* Button Styles */
.btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 4px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid transparent;
    height: 40px;  /* 統一按鈕高度 */
}

.btn-primary {
    background-color: var(--primary-color);
    color: white;
    border: 1px solid var(--primary-color);
}

.btn-primary:hover {
    background-color: #3d563a;
}

.btn-secondary {
background-color: var(--secondary-color);
color: white;
border: 1px solid var(--secondary-color);
}

.btn-secondary:hover {
background-color: var(--hover-color);
border-color: var(--hover-color);
}

.btn-outline {
border: 1px solid var(--border-color);
color: var(--text-primary);
background-color: white;
}

.btn-outline:hover {
background-color: var(--background-color);
}

/* Statistics Grid */
/* Statistics Grid */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-top: 20px;
}

.stat-card {
    padding: 16px;
    border-radius: 4px;  /* 統一圓弧度 */
    background-color: white;
    border: 1px solid var(--border-color);
}

.stat-title {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--text-secondary);
    font-size: 16px;  /* 統一文字大小 */
    margin-bottom: 4px;
}

.stat-value {
    color: var(--primary-color);
    font-size: 26px;  /* 統一數值大小 */
    font-weight: 600;
}

.icon {
    width: 18px;
    height: 18px;
    color: currentColor;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: currentColor;
}
.btn-add {
    background-color: #f8961e;
    color: white;
    border: 1px solid #f8961e;
}
/* Icon Styles */
.icon-wrapper {
display: inline-flex;
align-items: center;
}

.icon-small {
width: 14px;
height: 14px;
vertical-align: middle;
}

/* Focus Styles */
.btn:focus,
.venue-select-button:focus,
.month-picker:focus {
outline: 2px solid var(--secondary-color);
outline-offset: 2px;
}

/* Responsive Styles */
@media (max-width: 1280px) {
.venue-select {
    width: 280px;
}

.month-picker {
    width: 10rem;
}

.btn {
    padding: 8px 16px;  /* 統一按鈕 padding */
    border-radius: 4px;  /* 統一圓弧度 */
    font-size: 16px;    /* 統一文字大小 */}
}

@media (max-width: 1024px) {
.header-container {
    width: 90%;
}

.stats-grid {
    grid-template-columns: repeat(2, 1fr);
}
}

</style>