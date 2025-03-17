<template>
    <div class="calendar">
        <div class="main">
            <!-- 星期列 -->
            <div class="week-row">
                <div v-for="day in weekdays" :key="day" class="week-cell">
                    {{ day }}
                </div>
            </div>

            <!-- 日期網格 -->
            <div class="date-grid">
                <div
                    v-for="(day, index) in calendarDays"
                    :key="index"
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
                        <div 
                            v-if="day.scheduleInfo" 
                            class="schedule-tag-container"
                        >
                            <span 
                                class="schedule-tag"
                                :class="getStatusClass(day.scheduleInfo.status)"
                            >
                                {{ day.scheduleInfo.status }}
                            </span>
                            <!-- 自定義 tooltip -->
                            <div class="tooltip" v-if="day.scheduleInfo.note">
                                <strong>備註：</strong>{{ day.scheduleInfo.note || '無' }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import MonthPicker from '../../components/buttons/b5/MonthPicker.vue'

// 定義 props
const props = defineProps({
    schedules: {  // 改為直接接收 schedules
        type: Array,
        default: () => [],
        required: true
    },
    // 新增月份 prop
    selectedMonth: {
        type: String,  // 格式: "YYYY-MM"
        required: false,
        default: () => {
            const now = new Date()
            return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
        }
    }
})

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

// 狀態樣式對應
const getStatusClass = (status) => {
    const statusMap = {
        '休館日': 'status-closed',
        '已出租': 'status-rented',
        '保養日': 'status-maintainance'
    }
    return statusMap[status] || 'status-default'
}

// 處理檔期資料的計算屬性
const processedScheduleData = computed(() => {
    if (!props.schedules.length) return {}
    
    return props.schedules.reduce((acc, schedule) => {
        const date = new Date(schedule.date)
        const dateKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
        acc[dateKey] = {
            status: schedule.status,
            // 添加其他需要的資訊
            note: schedule.note,
            bookingId: schedule.bookingId
        }
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

// 監聽外部傳入的月份變化
watch(
    () => props.selectedMonth,
    (newMonth) => {
        console.log('Calendar - received new month:', newMonth)
        if (newMonth) {
            const [year, month] = newMonth.split('-')
            console.log('Calendar - parsing year:', year, 'month:', month)
            
            // 更新當前日期
            currentDate.value = new Date(parseInt(year), parseInt(month) - 1, 1)
            console.log('Calendar - currentDate updated to:', currentDate.value)
        }
    },
    { immediate: true }
)



// 監聽年月變化

watch(
    [currentYear, currentMonth],
    ([newYear, newMonth], [oldYear, oldMonth]) => {
        if (newYear === oldYear && newMonth === oldMonth) return
        
        emit('month-change', {
            year: newYear,
            month: newMonth + 1
        })
    }
)


// 處理日期點擊
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
@import '../../assets/fonts/font.css';

.calendar {
    font-family: Kantumruy Pro; 
    width: 100%;
    max-width: 1500px;
    margin: 0;
    max-height: 57vh;
    color: var(--text-primary);

}
.btn {
    display: flex;
    align-items: center;
    padding: 8px 16px;
    border: none;
    background: none;
    /* cursor: pointer; */
    font-size: 20px;
    font-family: Kantumruy Pro;
    color: var(--text-primary);
}

.btn:hover {
    color: var(--primary-color);
}


.main {
    border: 1px solid var(--border-color);
    border-radius: 10px;
    background: #fff;
    
}

.week-row {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    border-bottom: 1px solid var(--border-color);
    background: var(--primary-color);
    border-radius: 4PX;
}

.week-cell {
    padding: 10px;
    text-align: center;
    font-size: 18px;
    color: var(--border-color);

}

.date-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    
}

.cell {
    min-height: 80px;
    padding: 8px;
    border-right: 1px solid var(--border-color);
    border-bottom: 1px solid var(--border-color);
    /* cursor: pointer; */
    transition: background-color 0.2s ease;
}

/* .cell:hover {
    background-color: var(--hover-color);
} */

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
.schedule-tag-container {
    position: relative;
    display: inline-block;
}
.schedule-tag {
    font-size: 14px;
    padding: 2px 6px;
    border-radius: 4px;
    display: inline-block;
    margin-top: 4px;
    color: white;
    font-family: Kantumruy Pro;
}
.tooltip {
    display: none;  /* 改用 display 而不是 visibility */
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    background-color: rgba(0, 0, 0, 0.8);
    color: white;
    padding: 8px;
    border-radius: 4px;
    font-size: 14px;
    white-space: nowrap;
    z-index: 1000;
    min-width: 120px;
    margin-bottom: 5px;  /* 添加一點間距 */
}

/* 箭頭樣式 */
.tooltip::after {
    content: "";
    position: absolute;
    top: 100%;
    left: 50%;
    margin-left: -5px;
    border-width: 5px;
    border-style: solid;
    border-color: rgba(0, 0, 0, 0.8) transparent transparent transparent;
}

/* 滑鼠移上去時顯示 tooltip */
.schedule-tag-container:hover .tooltip {
    display: block;  /* 改用 display: block */
}

/* 確保容器有正確的定位 */
.schedule-tag-container {
    position: relative;
    display: inline-block;
    cursor: help;  /* 添加提示游標 */
}
/* 狀態標籤顏色 */
.status-closed { 
    background-color: #6b7280; 
}
.status-rented { 
    background-color: #f59e0b; 
}
.status-maintainance { 
    background-color: #6b7280; 
}
.status-default { 
    background-color: #6b7280; 
}



.other-month {
    background: var(--background-color);
    color: var(--border-color);
}

.past {
    color: var(--text-secondary);
    opacity: 0.7;
}

.today {
    /* background-color: var(--hover-color); */
    font-weight: bold;
}

/* .selected {
    background-color: var(--secondary-color);
    color: white;
} */

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