<!-- src/components/buttons/b5/DatePicker.vue -->
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

    <template v-else>
        <div class="grid month-grid">
            <button
                v-for="(month, index) in months"
                :key="month"
                @click="handleMonthSelect(index)"
                class="grid-button"
                :class="{ 'current': isCurrentMonth(index) }"
            >
                {{ month }}
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
    defaultDate: {
        type: Date,
        default: null
    }
})

const currentDate = ref(new Date())
const emit = defineEmits(['update:modelValue'])
const view = ref('month') // 'year' or 'month'

const months = ['1月', '2月', '3月', '4月', '5月', '6月', 
                '7月', '8月', '9月', '10月', '11月', '12月']

const generateYearGrid = () => {
    const currentYear = currentDate.value.getFullYear()
    const startYear = currentYear - (currentYear % 3) - 3
    return Array.from({ length: 12 }, (_, i) => startYear + i)
}

const isCurrentYear = (year) => {
    return year === currentDate.value.getFullYear()
}

const isCurrentMonth = (month) => {
    const propsDate = new Date(props.selectedDate.year, props.selectedDate.month, props.selectedDate.date)
    return month === propsDate.getMonth() && 
            currentDate.value.getFullYear() === propsDate.getFullYear()
}

const handleNavigation = (step) => {
    const newDate = new Date(currentDate.value)
    if (view.value === 'year') {
        newDate.setFullYear(newDate.getFullYear() + step * 12)
    } else {
        newDate.setFullYear(newDate.getFullYear() + step)
    }
    currentDate.value = newDate
}

const handleTitleClick = () => {
    if (view.value === 'month') {
        view.value = 'year'
    }
}

const renderTitle = () => {
    if (view.value === 'year') {
        const years = generateYearGrid()
        return `${years[0]} - ${years[years.length - 1]}`
    }
    return `${currentDate.value.getFullYear()}年`
}

const handleMonthSelect = (monthIndex) => {
    const selectedDate = new Date(
        currentDate.value.getFullYear(),
        monthIndex,
        1
    )
    emit('update:modelValue', selectedDate)
}

watch(() => props.selectedDate, (newDate) => {
    if (newDate) {
        currentDate.value = new Date(newDate.year, newDate.month, 1)
        view.value = 'month'
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
padding: 0.1rem 1rem;
/* padding: 0.5rem; */
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
/* padding: 0.5rem 1rem; */
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
padding: 0.68rem;
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
.date-picker {
    position: absolute;
    /* top: 800%; */
    /* left: 50%; */
    background-color: white;
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
    /* padding: 1rem; */
    width: 190px;
    z-index: 50;
}

.year-selector {
    display: flex;
    align-items: center;
    justify-content: space-between;
    /* padding: 0.5rem; */
    width: 190px;
    /* margin-bottom: 1.5rem; */
}

.year-text {
    color: #2C3E2D;
    font-weight: 500;
}

.year-btn {
    color: #4A6741;
    font-size: 1.125rem;
    cursor: pointer;
    background: none;
    border: none;
    transition: color 0.2s ease;
}

.year-btn:hover {
    color: #8AA682;
}

.months-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 0.6rem;
}

.month-item {
    padding: 0.5rem;
    text-align: center;
    cursor: pointer;
    color: #5C715E;
    border-radius: 0.5rem;
    transition: all 0.2s ease;
}

.month-item:hover {
    background-color: #C2D5BB;
}

.month-item.selected {
    background-color: #4A6741;
    color: #F5F7F4;
}

.month-item.selected:hover {
    background-color: #4A6741;
}
</style>