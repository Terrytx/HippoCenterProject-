<template>
<div class="venue-select">
    <div class="venue-content">
        <div class="venue-header">
            <h1 class="required">選擇館場</h1>
        </div>
        <div class="checkbox-container">
            <div 
                class="checkbox-item" 
                v-for="venue in venues" 
                :key="venue.id"
                :class="{'selected': selectedVenues.includes(venue.id)}"
                @click="toggleVenue(venue.id)">
                <div class="checkbox-label">
                    <span class="check-icon" v-if="selectedVenues.includes(venue.id)">✓</span>
                    <span>{{ venue.name }}</span>
                </div>
            </div>
        </div>
    </div>    
</div>
</template>

<script setup>
import { ref, defineEmits, defineProps, watch } from 'vue'

const props = defineProps({
    modelValue: {
        type: Array,
        default: () => []
    }
})

const emit = defineEmits(['update:modelValue'])
const selectedVenues = ref(props.modelValue)

watch(() => props.modelValue, (newValue) => {
    selectedVenues.value = newValue
}, { deep: true })

const toggleVenue = (id) => {
    let newSelectedVenues
    if (selectedVenues.value.includes(id)) {
        newSelectedVenues = selectedVenues.value.filter(v => v !== id)
    } else {
        newSelectedVenues = [...selectedVenues.value, id]
    }
    selectedVenues.value = newSelectedVenues
    emit('update:modelValue', newSelectedVenues)
}

const venues = [
    { id: 'A01', name: '1號倉庫' },
    { id: 'A02', name: '2號倉庫' },
    { id: 'A03', name: '3號倉庫' },
    { id: 'A04', name: '4號倉庫' },
    { id: 'A05', name: '5號倉庫' },
    { id: 'B02', name: '春田序曲' },
    { id: 'B03', name: '瑪吉的畫廊' },
    { id: 'B04', name: '巴特的藝術空間' },
    { id: 'B05', name: '荷馬的闢護所' },
    { id: 'B06', name: '麗莎的實驗室' },
    { id: 'C01', name: '春田綠地' },
    { id: 'C02', name: '春田大道' }
]
</script>

<style scoped>
.venue-select {
    width: 100%;
    font-family: system-ui, -apple-system, sans-serif;
}

.venue-header {
    margin-bottom: 1.5rem;
}

.required::after {
    content: "*";
    color: #f8961e;
    margin-left: 4px;
}

h1 {
    font-size: 1.25rem;
    font-weight: 500;
    color: #333;
    margin: 0;
}

.checkbox-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 12px;
    padding: 1.5rem;
    border: 1px solid #e5e5e5;
    border-radius: 8px;
}

.checkbox-item {
    padding: 0.75rem;
    background-color: #fff;
    border: 1px solid #e5e5e5;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
}

.checkbox-item:hover {
    background-color: #fff8f0;
    border-color: #f8961e;
}

.checkbox-item.selected {
    background-color: #fff8f0;
    border-color: #f8961e;
}

.checkbox-label {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    color: #333;
    font-size: 0.875rem;
    cursor: pointer;
}

.check-icon {
    color: #f8961e;
    font-weight: bold;
    font-size: 1rem;
}

/* 選中狀態下的文字顏色 */
.checkbox-item.selected .checkbox-label {
    color: #333;
    font-weight: 500;
}
</style>