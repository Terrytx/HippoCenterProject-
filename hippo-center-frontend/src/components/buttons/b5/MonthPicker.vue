# MonthPicker.vue
<template>
<div class="month-picker" v-if="show" @click.stop>
<!-- 年份選擇 -->
<div class="year-selector">
    <button @click="changeYear(-1)" class="year-btn">&lt;</button>
    <span class="year-text">{{ selectedYear }}</span>
    <button @click="changeYear(1)" class="year-btn">&gt;</button>
</div>

<!-- 月份網格 -->
<div class="months-grid">
    <div
    v-for="(month, index) in months"
    :key="index"
    @click="selectMonth(index)"
    class="month-item"
    :class="{ 'month-selected': selectedMonth === index }"
    >
    {{ month }}
    </div>
</div>
</div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
show: Boolean,
year: Number,
month: Number
})

const emit = defineEmits(['update:show', 'select'])

const selectedYear = ref(props.year)
const selectedMonth = ref(props.month)

const months = [
'1月', '2月', '3月', '4月', 
'5月', '6月', '7月', '8月',
'9月', '10月', '11月', '12月'
]

const changeYear = (offset) => {
selectedYear.value += offset
}

const selectMonth = (index) => {
selectedMonth.value = index
emit('select', {
year: selectedYear.value,
month: index
})
emit('update:show', false)
}

watch(() => props.year, (newYear, oldYear) => {
    if (newYear === oldYear) return
    selectedYear.value = newYear
}, { immediate: true })

watch(() => props.month, (newMonth, oldMonth) => {
    if (newMonth === oldMonth) return
    selectedMonth.value = newMonth
}, { immediate: true })
</script>

<style scoped>
.month-picker {
position: absolute;
top: 100%;
left: 50%;
transform: translateX(-50%);
width: 256px;
padding: 16px;
background-color: #f8f9fa;
border-radius: 8px;
box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
z-index: 1000;
}

.year-selector {
display: flex;
align-items: center;
justify-content: space-between;
margin-bottom: 24px;
}

.year-text {
color: #343a40;
}

.year-btn {
color: #f8961e;
font-size: 1.125rem;
cursor: pointer;
background: none;
border: none;
padding: 4px;
}

.year-btn:hover {
color: #ffb649;
}

.months-grid {
display: grid;
grid-template-columns: repeat(3, 1fr);
gap: 12px;
}

.month-item {
padding: 8px 0;
text-align: center;
cursor: pointer;
color: #495057;
border-radius: 8px;
transition: all 0.2s ease;
}

.month-item:hover {
background-color: #e9ecef;
}

.month-selected {
background-color: #f8961e;
color: white;
}

.month-selected:hover {
background-color: #f8961e;
opacity: 0.9;
}
</style>