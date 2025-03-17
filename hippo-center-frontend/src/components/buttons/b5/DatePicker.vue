<!-- src/components/buttons/b5/DatePicker.vue -->
<template>
<div class="datepicker-container">
    <input
    type="text"
    :value="formatDate(modelValue)"
    @click="handleInputClick"
    readonly
    class="datepicker-input"
    :placeholder="placeholder"
    />
    
    <Transition name="fade">
        <div v-if="showCalendar" v-click-outside="hideCalendar" class="calendar-wrapper" @click.stop>
            <!-- 標題和導航 -->
            <div class="calendar-header">
            <button 
                @click.stop="handleNavigation(-1)"
                class="nav-button"
            >
                &lt;
            </button>

            <button
                @click.stop="handleTitleClick"
                class="title-button"
            >
                {{ renderTitle() }}
            </button>

            <button 
                @click.stop="handleNavigation(1)"
                class="nav-button"
            >
                &gt;
            </button>
            </div>

        <!-- 年份視圖 -->
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

        <!-- 月份視圖 -->
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

        <!-- 日期視圖 -->
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
    </Transition>
</div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
modelValue: {
    type: String,
    required: true
},
placeholder: {
    type: String,
    default: '選擇日期'
},
minDate: {
    type: Date,
    default: null
},
maxDate: {
    type: Date,
    default: null
}
})

const emit = defineEmits(['update:modelValue'])
const showCalendar = ref(false)
const view = ref('date')
const currentDate = ref(new Date())

const months = ['1月', '2月', '3月', '4月', '5月', '6月', 
                '7月', '8月', '9月', '10月', '11月', '12月']
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// 生成年份網格
const generateYearGrid = () => {
    const currentYear = currentDate.value.getFullYear()
    // 確保起始年份總是 12 的倍數
    const startYear = currentYear - (currentYear % 12)
    return Array.from({ length: 12 }, (_, i) => startYear + i)
}
// 獲取月份第一天是星期幾
const getFirstDayOfMonth = (date) => {
return new Date(date.getFullYear(), date.getMonth(), 1).getDay()
}

// 獲取月份天數
const getDaysInMonth = (date) => {
return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate()
}

// 生成日曆天數
const generateCalendarDays = () => {
const firstDay = getFirstDayOfMonth(currentDate.value)
const daysInMonth = getDaysInMonth(currentDate.value)
const days = []

const prevMonthDays = getDaysInMonth(
    new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1)
)

// 上個月的日期
for (let i = firstDay - 1; i >= 0; i--) {
    days.push({
    date: new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, prevMonthDays - i),
    isCurrentMonth: false
    })
}

// 當前月的日期
for (let i = 1; i <= daysInMonth; i++) {
    days.push({
    date: new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), i),
    isCurrentMonth: true
    })
}

// 下個月的日期
const remainingDays = 42 - days.length
for (let i = 1; i <= remainingDays; i++) {
    days.push({
    date: new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, i),
    isCurrentMonth: false
    })
}

return days
}

// 檢查是否為當前年
const isCurrentYear = (year) => {
return year === currentDate.value.getFullYear()
}

// 檢查是否為當前月
const isCurrentMonth = (month) => {
return month === currentDate.value.getMonth()
}

// 檢查是否為選中日期
const isSelected = (date) => {
if (!props.modelValue) return false
const selectedDate = new Date(props.modelValue)
return date.toDateString() === selectedDate.toDateString()
}

// 檢查是否為今天
const isToday = (date) => {
return date.toDateString() === new Date().toDateString()
}

// 格式化日期顯示
const formatDate = (dateString) => {
if (!dateString) return ''
const date = new Date(dateString)
const year = date.getFullYear()
const month = String(date.getMonth() + 1).padStart(2, '0')
const day = String(date.getDate()).padStart(2, '0')
return `${year}-${month}-${day}`
}

// 處理標題點擊
const handleTitleClick = () => {
if (view.value === 'date') {
    view.value = 'month'
} else if (view.value === 'month') {
    view.value = 'year'
}
}

// 處理導航
const handleNavigation = (step) => {
    const newDate = new Date(currentDate.value)
    if (view.value === 'year') {
    // 確保年份導航時以 12 年為單位
    newDate.setFullYear(newDate.getFullYear() + (step * 12))
    } else if (view.value === 'month') {
    newDate.setFullYear(newDate.getFullYear() + step)
    } else {
    newDate.setMonth(newDate.getMonth() + step)
    }
    currentDate.value = newDate
}
// 處理日期選擇
const handleDateSelect = (date) => {
    emit('update:modelValue', formatDate(date))
    setTimeout(() => {
        showCalendar.value = false
    }, 100)
}

// 渲染標題
const renderTitle = () => {
if (view.value === 'year') {
    const years = generateYearGrid()
    return `${years[0]} - ${years[years.length - 1]}`
} else if (view.value === 'month') {
    return `${currentDate.value.getFullYear()}年`
}
return `${currentDate.value.getFullYear()}年 ${currentDate.value.getMonth() + 1}月`
}

// 處理輸入框點擊
const handleInputClick = (event) => {
event.stopPropagation() // 阻止事件冒泡
showCalendar.value = !showCalendar.value
}

// 點擊外部關閉日曆
const hideCalendar = (event) => {
    const target = event.target
    const isInput = target.classList.contains('datepicker-input')
    const isCalendarPart = target.closest('.calendar-wrapper')

    if (!isInput && !isCalendarPart) {
        showCalendar.value = false
        view.value = 'date' // 重置視圖
    }
}
// 監聽值變化
watch(() => props.modelValue, (newValue) => {
if (newValue) {
    currentDate.value = new Date(newValue)
}
}, { immediate: true })

// 自定義指令：點擊外部關閉
const vClickOutside = {
    mounted(el, binding) {
        el._clickOutside = (event) => {
        // 檢查點擊的元素是否是日期選擇器的一部分
        const isDatepickerPart = el === event.target || 
                                el.contains(event.target) || 
                                event.target.classList.contains('datepicker-input')
        
        if (!isDatepickerPart) {
            binding.value(event)
        }
        }
        
        // 改用 mousedown 事件而不是 click
        document.addEventListener('mousedown', el._clickOutside)
    },
    unmounted(el) {
        document.removeEventListener('mousedown', el._clickOutside)
    }
    }
</script>

<style scoped>

.datepicker-container {
position: relative;
width: 100%;
}

.datepicker-input {
width: 100%;
padding: 8px 12px;
border: 1px solid #ddd;

border-radius: 4px;
font-size: 14px;
cursor: pointer;
background-color: white;
color: #333;
}

.datepicker-input:focus {
outline: none;
border-color: #f8961e;
}

.calendar-wrapper {
position: absolute;
top: 100%;
left: 0;
margin-top: 4px;
padding: 1rem;
max-width: 28rem;
background-color: #ffffff;
border-radius: 0.5rem;
box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
border: 1px solid #333;
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
color: #333;
background: none;
border: none;
cursor: pointer;
}

.nav-button:hover {
background-color: #f1f1f1;
}

.title-button {
padding: 0.5rem 1rem;
font-weight: 600;
border-radius: 0.25rem;
color: #000;
background: none;
border: none;
cursor: pointer;
}

.title-button:hover {
background-color: #f1f1f1;
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
color: #333;
background: none;
border: none;
cursor: pointer;
}

.grid-button:hover {
background-color: #f1f1f1;
}

.grid-button.current {
background-color: #f8961e;
color: white;
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
color: #666;
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
color: #333;
background: none;
border: none;
cursor: pointer;
}

.day-button:hover {
background-color: #f1f1f1;
}

.day-button.other-month {
color: #999;
}

.day-button.current {
background-color: #f8961e;
color: white;
}

.day-button.today {
border: 2px solid #333;
}

.fade-enter-active,
.fade-leave-active {
transition: all 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
opacity: 0;
transform: translateY(10px);
}
</style>