<!-- VenueSearch.vue -->
<template>
    <div class="fixed-header">
        <div class="card">
            <div class="card-header">
                <h2 class="title">場地查詢系統</h2>
                
                <div class="button-group">
                    <div class="search-input">
                        <input 
                            type="text"
                            v-model="searchVenueId"
                            placeholder="輸入場地編號"
                            @keyup.enter="handleSearch"
                        />
                        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="11" cy="11" r="8"/>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                        </svg>
                    </div>
                    
                    <button 
                        class="btn btn-primary"
                        @click="handleSearch"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="11" cy="11" r="8"/>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                        </svg>
                        搜尋場地
                    </button>
                    
                    <button 
                        class="btn btn-secondary"
                        @click="handleGetAvailable"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="20 6 9 17 4 12"/>
                        </svg>
                        可租借場地
                    </button>
                    
                    <button 
                        class="btn btn-outline"
                        @click="handleGetAll"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="8" y1="6" x2="21" y2="6"/>
                            <line x1="8" y1="12" x2="21" y2="12"/>
                            <line x1="8" y1="18" x2="21" y2="18"/>
                            <line x1="3" y1="6" x2="3.01" y2="6"/>
                            <line x1="3" y1="12" x2="3.01" y2="12"/>
                            <line x1="3" y1="18" x2="3.01" y2="18"/>
                        </svg>
                        所有場地
                    </button>
                    
                    <button 
                        class="btn btn-add"
                        @click="handleOpenDialog"
                    >
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"/>
                            <line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        新增場地
                    </button>
                </div>
            </div>

            <div class="stats-container">
                <div class="stat-card">
                    <div class="stat-title">
                        <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                            <polyline points="9 22 9 12 15 12 15 22"/>
                        </svg>
                        總場地數
                    </div>
                    <div class="stat-value">{{ venues.length }}</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-title">
                        <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="20 6 9 17 4 12"/>
                        </svg>
                        可租借場地
                    </div>
                    <div class="stat-value">
                        {{ venues.filter(v => v.rentalStatue).length }}
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-title">
                        <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="15" y1="9" x2="9" y2="15"/>
                            <line x1="9" y1="9" x2="15" y2="15"/>
                        </svg>
                        不可租借場地
                    </div>
                    <div class="stat-value">
                        {{ venues.filter(v => !v.rentalStatue).length }}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'

// Props
const props = defineProps({
    venues: {
        type: Array,
        required: true
    }
})

// Emits
const emit = defineEmits(['search', 'get-available', 'get-all', 'open-dialog'])

// Refs
const searchVenueId = ref('')

// Methods
const handleSearch = () => {
    emit('search', searchVenueId.value)
}

const handleGetAvailable = () => {
    emit('get-available')
}

const handleGetAll = () => {
    emit('get-all')
}

const handleOpenDialog = () => {
    emit('open-dialog')
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
    background-color: var(--background-color);
/* position: fixed; */
padding-top: 5%;
box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
z-index: 40;
}

.card {
    background-color: white;
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
    width: 240px;
}

.search-input input {
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
    border-radius: 6px;
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
</style>