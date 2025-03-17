<!-- CalendarPicker.vue -->
<template>
<div class="calendar-wrapper">
    <!-- 標題和導航 -->
    <div class="calendar-header">
    <button 
        @click="handleNavigation(-1)"
        class="nav-button"
    >
        &lt;
    </button>

    <button
        @click="handleTitleClick"
        class="title-button"
    >
        {{ renderTitle() }}
    </button>

    <button 
        @click="handleNavigation(1)"
        class="nav-button"
    >
        &gt;
    </button>
    </div>

    <!-- 內容區域 -->
    <template v-if="view === 'year'">
    <div class="grid year-grid">
        <button
        v-for="year in generateYearGrid()"
        :key="year"
        @click="() => {
            currentDate = new Date(year, currentDate.getMonth(), 1);
            view = 'month';
        }"
        class="grid-button"
        :class="{ 'current': isCurrentYear(year) }"
        >
        {{ year }}
        </button>
    </div>
    </template>

    <template v-else-if="view === 'month'">
    <div class="grid month-grid">
        <button
        v-for="(month, index) in months"
        :key="month"
        @click="() => {
            currentDate = new Date(currentDate.getFullYear(), index, 1);
            view = 'date';
        }"
        class="grid-button"
        :class="{ 'current': isCurrentMonth(index) }"
        >
        {{ month }}
        </button>
    </div>
    </template>

    <template v-else>
    <div class="weekdays-grid">
        <div 
        v-for="day in weekDays"
        :key="day"
        class="weekday"
        >
        {{ day }}
        </div>
    </div>
    <div class="days-grid">
        <button
            v-for="(day, index) in generateCalendarDays()"
            :key="index"
            @click="handleDateSelect(day.date)"
            class="day-button"
            :class="{
            'other-month': !day.isCurrentMonth,
            'current': isSelected(day.date),
            'today': isToday(day.date) && !isSelected(day.date)
            }"
        >
            {{ day.date.getDate() }}
        </button>
    </div>
    </template>

    
</div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
const props = defineProps({
    modelValue: {
        type: Date,
        required: true
    },
    selectedDate: {
        type: Object,
        required: true
    }, 
    defaultDate: {  // 新增這個 prop
        type: Date,
        default: null
    }
})

const currentDate = ref(new Date())
const emit = defineEmits(['update:modelValue'])
const view = ref('date') // 'year', 'month', 'date'

const months = ['1月', '2月', '3月', '4月', '5月', '6月', 
                '7月', '8月', '9月', '10月', '11月', '12月']
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// ... [其他函數保持不變] ...
const generateYearGrid = () => {
const currentYear = currentDate.value.getFullYear()
const startYear = currentYear - (currentYear % 3) - 3
return Array.from({ length: 12 }, (_, i) => startYear + i)
}

const getFirstDayOfMonth = (date) => {
return new Date(date.getFullYear(), date.getMonth(), 1).getDay()
}

const getDaysInMonth = (date) => {
return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate()
}

const generateCalendarDays = () => {
const firstDay = getFirstDayOfMonth(currentDate.value)
const daysInMonth = getDaysInMonth(currentDate.value)
const days = []

const prevMonthDays = getDaysInMonth(
    new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1)
)

for (let i = firstDay - 1; i >= 0; i--) {
    days.push({
    date: new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, prevMonthDays - i),
    isCurrentMonth: false
    })
}

for (let i = 1; i <= daysInMonth; i++) {
    days.push({
    date: new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), i),
    isCurrentMonth: true
    })
}

const remainingDays = 42 - days.length
for (let i = 1; i <= remainingDays; i++) {
    days.push({
    date: new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, i),
    isCurrentMonth: false
    })
}

return days
}

const isCurrentYear = (year) => {
return year === currentDate.value.getFullYear()
}

// 修改 isCurrentMonth 方法
const isCurrentMonth = (month) => {
    const propsDate = new Date(props.selectedDate.year, props.selectedDate.month, props.selectedDate.date)
    return month === currentDate.value.getMonth() && 
        currentDate.value.getFullYear() === propsDate.getFullYear()
}

// 修改 isSelected 方法
const isSelected = (date) => {
    const propsDate = new Date(props.selectedDate.year, props.selectedDate.month, props.selectedDate.date)
    return date.toDateString() === propsDate.toDateString()
}

const isToday = (date) => {
return date.toDateString() === new Date().toDateString()
}

const formatDate = (date) => {
const year = date.getFullYear()
const month = (date.getMonth() + 1).toString().padStart(2, '0')
const day = date.getDate().toString().padStart(2, '0')
return `${year}-${month}-${day}`
}

const handleNavigation = (step) => {
const newDate = new Date(currentDate.value)
if (view.value === 'year') {
    newDate.setFullYear(newDate.getFullYear() + step * 12)
} else if (view.value === 'month') {
    newDate.setFullYear(newDate.getFullYear() + step)
} else {
    newDate.setMonth(newDate.getMonth() + step)
}
currentDate.value = newDate
}

const handleTitleClick = () => {
if (view.value === 'date') {
    view.value = 'month'
} else if (view.value === 'month') {
    view.value = 'year'
}
}

const renderTitle = () => {
if (view.value === 'year') {
    const years = generateYearGrid()
    return `${years[0]} - ${years[years.length - 1]}`
} else if (view.value === 'month') {
    return `${currentDate.value.getFullYear()}年`
}
return `${currentDate.value.getFullYear()}年 ${currentDate.value.getMonth() + 1}月`
}
const handleDateSelect = (date) => {
  // 創建新的日期對象
    const selectedDate = new Date(
        date.getFullYear(),
        date.getMonth(),
        date.getDate()
    )
    
    // 發送更新事件
emit('update:modelValue', selectedDate)
    }
// 修改 watch，同時更新 view 回到日期視圖
watch(() => props.selectedDate, (newDate) => {
    if (newDate) {
        currentDate.value = new Date(newDate.year, newDate.month, newDate.date)
        view.value = 'date' // 確保切換回日期視圖
    }
}, { deep: true })
onMounted(() => {
    if (props.defaultDate) {
        currentDate.value = new Date(props.defaultDate)
    }
})
</script>

<style scoped>
.calendar-wrapper {
padding: 1rem;
max-width: 28rem;
/* margin: 0 auto; */
background-color: #F5F7F4;
border-radius: 0.5rem;
box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
border: 1px solid #D8E0D5;
position: absolute; /* 新增這行 */
z-index: 1000; 
}

.calendar-header {
display: flex;
align-items: center;
justify-content: space-between;
margin-bottom: 1rem;
}

.nav-button {
padding: 0.5rem;
border-radius: 9999px;
color: #4A6741;
}

.nav-button:hover {
background-color: #C2D5BB;
}

.title-button {
padding: 0.5rem 1rem;
font-weight: 600;
border-radius: 0.25rem;
color: #2C3E2D;
}

.title-button:hover {
background-color: #C2D5BB;
}

.grid {
display: grid;
gap: 0.25rem;
}

.year-grid,
.month-grid {
grid-template-columns: repeat(4, 1fr);
}

.grid-button {
padding: 1rem;
font-size: 0.875rem;
border-radius: 0.5rem;
color: #2C3E2D;
}

.grid-button:hover {
background-color: #C2D5BB;
}

.grid-button.current {
background-color: #4A6741;
color: white;
}

.grid-button.current:hover {
background-color: #8AA682;
}

.weekdays-grid {
display: grid;
grid-template-columns: repeat(7, 1fr);
margin-bottom: 0.5rem;
}

.weekday {
text-align: center;
font-size: 0.875rem;
font-weight: 500;
color: #5C715E;
padding: 0.5rem 0;
}

.days-grid {
display: grid;
grid-template-columns: repeat(7, 1fr);
gap: 0.25rem;
}

.day-button {
padding: 0.5rem;
font-size: 0.875rem;
border-radius: 0.5rem;
color: #2C3E2D;
}

.day-button:hover {
background-color: #C2D5BB;
}

.day-button.other-month {
color: #8AA682;
}

.day-button.current {
background-color: #4A6741;
color: white;
}

.day-button.current:hover {
background-color: #8AA682;
}

.day-button.today {
border: 2px solid #4A6741;
}

.selected-date {
margin-top: 1rem;
padding: 0.75rem;
background-color: white;
border-radius: 0.375rem;
border: 1px solid #D8E0D5;
}

.date-label {
font-size: 0.875rem;
color: #5C715E;
}

.date-value {
font-size: 1.125rem;
font-weight: 500;
color: #2C3E2D;
}

button {
background: none;
border: none;
cursor: pointer;
outline: none;
}

button:focus {
outline: 2px solid #4A6741;
outline-offset: 2px;
}
</style>