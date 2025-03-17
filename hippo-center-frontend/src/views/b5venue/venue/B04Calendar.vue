<template>
    <div class="calendar">
        <!-- Keep existing header code -->
        <div class="header">
            <button @click="handlePrevMonth" class="btn">
                <span class="arrow">&lt;</span>
                上個月
            </button>
            
            <div class="title" @click.stop="showMonthPicker = true" style="position: relative; cursor: pointer;">
                {{ currentYear }} 年 {{ currentMonth + 1 }} 月 <span class="chevron-up"></span>
                <MonthPicker
                    v-model:show="showMonthPicker"
                    :year="currentYear"
                    :month="currentMonth"
                    @select="handleMonthSelect"
                />
            </div>
            <button @click="handleNextMonth" class="btn">
                下個月
                <span class="arrow">&gt;</span>
            </button>
        </div>

        <!-- Keep existing calendar main structure -->
        <div class="main">
            <div class="week-row">
                <div v-for="day in weekdays" :key="day" class="week-cell">
                    {{ day }}
                </div>
            </div>

            <div class="date-grid">
                <div
                    v-for="(day, index) in calendarDays"
                    :key="index"
                    @click="handleDateClick(day)"
                    :class="[
                        'cell',
                        {
                            'other-month': !day.isCurrentMonth,
                            'today': day.isToday,
                            'selected': day.isSelected,
                            'past': day.isPast,
                            'has-schedule': day.hasSchedule
                        }
                    ]"
                >
                    <div class="cell-content">
                        <span class="date">{{ day.date }}</span>
                        <!-- Modified schedule tag to use status classes -->
                        <span v-if="day.scheduleInfo" 
                              :class="['schedule-tag', getStatusClass(day.scheduleInfo.status)]">
                            {{ day.scheduleInfo.status || '已預約' }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import MonthPicker from '../../../components/buttons/b5/MonthPicker.vue'
// 定義 props
const props = defineProps({
    selectedLocation: {
        type: Object,
        default: null
    },
    scheduleData: {
        type: Array,
        default: () => []
    }
})
const getStatusClass = (status) => {
    switch(status) {
        case '休館日':
            return 'status-closed'
        case '已出租':
            return 'status-rented'
        case '保養日':
            return 'status-maintainance'
        default:
            return 'status-default'
    }
}
const emit = defineEmits(['month-change'])
const showMonthPicker = ref(false)

// 定義星期標題
const weekdays = ['週日', '週一', '週二', '週三', '週四', '週五', '週六']

// 目前日期狀態
const currentDate = ref(new Date())
const selectedDate = ref(new Date())

// 計算目前年月
const currentYear = computed(() => currentDate.value.getFullYear())
const currentMonth = computed(() => currentDate.value.getMonth())

// 處理檔期資料的計算屬性
const processedScheduleData = computed(() => {
    if (!props.scheduleData.length) return {}
    
    return props.scheduleData.reduce((acc, schedule) => {
        // 使用本地時間而不是 UTC
        const date = new Date(schedule.date)
        const dateKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
        acc[dateKey] = schedule
        return acc
    }, {})
})
// 取得某月份的天數及起始日
const getDaysInMonth = (date) => {
    const year = date.getFullYear()
    const month = date.getMonth()
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)
    return {
        daysInMonth: lastDay.getDate(),
        startingDay: firstDay.getDay()
    }
}

// 計算行事曆顯示的所有日期
const calendarDays = computed(() => {
    const days = []
    const { daysInMonth, startingDay } = getDaysInMonth(currentDate.value)

    // 計算上個月的顯示日期
    const prevMonth = new Date(currentYear.value, currentMonth.value - 1, 1)
    const prevMonthDays = getDaysInMonth(prevMonth).daysInMonth

    for (let i = startingDay - 1; i >= 0; i--) {
        days.push({
            date: prevMonthDays - i,
            isCurrentMonth: false,
            isPast: true
        })
    }

    // 計算當月日期
    for (let i = 1; i <= daysInMonth; i++) {
        const date = new Date(currentYear.value, currentMonth.value, i)
        // 修改這裡，使用與 processedScheduleData 相同的日期格式
        const dateKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
        const scheduleInfo = processedScheduleData.value[dateKey]
        
        days.push({
            date: i,
            isCurrentMonth: true,
            isPast: date < new Date(),
            isToday: date.toDateString() === new Date().toDateString(),
            isSelected: date.toDateString() === selectedDate.value.toDateString(),
            scheduleInfo: scheduleInfo,
            hasSchedule: !!scheduleInfo
        })
    }

    // 計算下個月的顯示日期
    const remainingDays = 42 - days.length // 6列 x 7天
    for (let i = 1; i <= remainingDays; i++) {
        days.push({
            date: i,
            isCurrentMonth: false,
            isPast: false
        })
    }

    return days
})

// 處理月份選擇
const handleMonthSelect = ({ year, month }) => {
    if (year === currentYear.value && month === currentMonth.value) return
    
    currentDate.value = new Date(year, month)
    if (props.selectedLocation) {
        emit('month-change', {
            year,
            month: month + 1
        })
    }
}

// 處理點擊外部關閉月份選擇器
const handleClickOutside = (event) => {
    if (showMonthPicker.value) {
        showMonthPicker.value = false
    }
}

// 生命週期鉤子
onMounted(() => {
    document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
    showMonthPicker.value = false
})

// 監聽年月變化
watch(
    [currentYear, currentMonth],
    ([newYear, newMonth], [oldYear, oldMonth]) => {
        if (newYear === oldYear && newMonth === oldMonth) return
        
        if (props.selectedLocation) {
            emit('month-change', {
                year: newYear,
                month: newMonth + 1
            })
        }
    }
)
// 修改切換月份的處理函數
const handlePrevMonth = () => {
    if (!props.selectedLocation) {
        console.warn('沒有選擇場地，無法切換月份');
        return;
    }
    currentDate.value = new Date(currentYear.value, currentMonth.value - 1);
    console.log('切換到上個月:', {
        year: currentDate.value.getFullYear(),
        month: currentDate.value.getMonth() + 1
    });
    emit('month-change', {
        year: currentDate.value.getFullYear(),
        month: currentDate.value.getMonth() + 1
    });
};

const handleNextMonth = () => {
    if (!props.selectedLocation) {
        console.warn('沒有選擇場地，無法切換月份');
        return;
    }
    currentDate.value = new Date(currentYear.value, currentMonth.value + 1);
    console.log('切換到下個月:', {
        year: currentDate.value.getFullYear(),
        month: currentDate.value.getMonth() + 1
    });
    emit('month-change', {
        year: currentDate.value.getFullYear(),
        month: currentDate.value.getMonth() + 1
    });
};

// 點擊日期的處理函數
const handleDateClick = (day) => {
    if (day.isCurrentMonth) {
        selectedDate.value = new Date(
            currentYear.value,
            currentMonth.value,
            day.date
        )
        
        if (day.scheduleInfo) {
            console.log('Schedule info:', day.scheduleInfo)
        }
    }
}
</script>

<style scoped>
@import '@/assets/fonts/font.css';

.calendar {
    font-family: Kantumruy Pro; 
    width: 100%;
    max-width: 1500px;
    margin: 0;
    background: #fff;
    max-height: 80vh;
}

/* .container {
    border: 1px solid blue;
    padding: 0;
    /* padding:0 10px 0 10px 
} */

/* 標題區塊 */
.header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;

}

.title {
    font-size: 22px;
    font-weight: bold;
    position: relative;
    cursor: pointer;
    padding: 8px;
    border-radius: 4px;
    color: var(--primary-color);
}

.title:hover {
    background-color: var(--hover-color);
}
.chevron-up {
    display: inline-block;
    margin-left: 4px;
    position: relative;
    width: 10px;
    height: 5px; /* 減小高度確保底部切齊 */
    vertical-align: middle; /* 讓箭頭與文字對齊 */
}

.chevron-up::before,
.chevron-up::after {
    content: '';
    position: absolute;
    bottom: 0;
    width: 2px;
    height: 10px; /* 調整線條長度 */
    background-color: currentColor;
}
.chevron-up::after {
    height: 10.5px; 
}
.chevron-up::before {
    right: 0px;
    transform-origin: bottom right; /* 設置變形的原點在右下 */
    transform: rotate(-45deg);
}

.chevron-up::after {
    left: 7.5px;
    transform-origin: bottom left; /* 設置變形的原點在左下 */
    transform: rotate(45deg);
}


.btn {
    display: flex;
    align-items: center;
    padding: 8px 16px;
    border: none;
    background: none;
    cursor: pointer;
    font-size: 20px;
    font-family: Kantumruy Pro; 

}

.arrow {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    background: #666;
    color: #fff;
    border-radius: 50%;
    margin: 0 8px;
}

/* 主體區塊 */
.main {
    border: 1px solid #ddd;
    border-radius: 4px;

}

/* 星期列 */
.week-row {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    border-bottom: 1px solid #e9ecef;
    background: #666;  /* 改為淺灰色背景 */
}

.week-cell {
    padding: 10px;
    text-align: center;
    font-size: 16px;
    color: #f8f9fa;  /* 改為深灰色文字 */
    font-weight: 500;  /* 加粗一點使其更明顯 */
}

/* 日期網格 */
.date-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
}

.cell {
    min-height: 80px;
    padding: 8px;
    border-right: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
    cursor: default;
}

.cell:nth-child(7n) {
    border-right: none;
}

.cell-content {
    display: flex;
    flex-direction: column;
}

.date {
    font-size: 14px;
    margin-bottom: 4px;

}

/* 新增的檔期標籤樣式 */
.schedule-tag {
    font-size: 16px;
    padding: 2px 6px;
    color: white;
    border-radius: 4px;
    display: inline-block;
    margin-top: 4px;
    font-family: Kantumruy Pro;
}
/* 狀態樣式 */
.other-month {
    background: #f9f9f9;
    color: #ccc;
}

.past {
    color: #999;
}

.status-closed { 
    background-color: #6b7280; 
}

.status-rented { 
    background-color: #f59e0b; 
}

.status-maintainance { 
    background-color: #8AA682; 
}

.status-default { 
    background-color: #6b7280; 
}

/* 響應式設計 */
@media (max-width: 768px) {
    .cell {
        min-height: 60px;
    }

    .title {
        font-size: 16px;
    }

    .week-cell {
        font-size: 12px;
    }
}
</style>