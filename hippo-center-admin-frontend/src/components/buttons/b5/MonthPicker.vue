<template>
    <div 
    class="date-picker-panel"
    @click.stop
    >
    <!-- 年份選擇 -->
    <div class="date-picker-year">
        <button @click="changeYear(-1)" class="date-picker-year-btn">&lt;</button>
        <span class="date-picker-year-text">{{ formattedDate }}</span>
        <button @click="changeYear(1)" class="date-picker-year-btn">&gt;</button>
    </div>

    <!-- 月份網格 -->
    <div class="date-picker-months">
        <div
        v-for="(month, index) in months"
        :key="index"
        @click="selectMonth(index)"
        class="date-picker-months-item"
        :class="{ 'selected': selectedMonth === index }"
        >
        {{ month }}
        </div>
    </div>
    </div>

</template>

<script setup>
import { ref, computed, watch } from 'vue'  // 添加 computed 的導入

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => ({
        year: new Date().getFullYear(),
        month: new Date().getMonth()
        })
    }
    })

const emit = defineEmits(['update:modelValue'])

const selectedYear = ref(props.modelValue.year)
const selectedMonth = ref(props.modelValue.month)

// 這裡使用了 computed
const formattedDate = computed(() => {
    const monthStr = String(selectedMonth.value + 1).padStart(2, '0')
    return `${selectedYear.value}年${monthStr}月`
    })

const months = [
    '1月', '2月', '3月', '4月', 
    '5月', '6月', '7月', '8月',
    '9月', '10月', '11月', '12月'
    ]

const changeYear = (offset) => {
    selectedYear.value += offset
    updateValue()
    }

const selectMonth = (index) => {
    selectedMonth.value = index
    updateValue()
    }

const updateValue = () => {
    emit('update:modelValue', {
        year: selectedYear.value,
        month: selectedMonth.value
    })
    }

    watch(() => props.modelValue, (newValue) => {
    if (newValue) {
        selectedYear.value = newValue.year
        // 只在月份真的改變時才更新月份
        if (newValue.month !== selectedMonth.value) {
            selectedMonth.value = newValue.month
        }
    }
}, { deep: true })
</script>

<style scoped>
@import '../../../assets/styles/datepicker.css';
</style>
