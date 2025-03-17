<template>
    <div class="schedule-wrapper">
        <div class="schedule-container">
            <h3>檔期行事曆</h3>
            <Selector @location-selected="handleLocationSelected" />
        </div>
        <div class="calendar-container" v-show="selectedLocation">
            <B04Calendar 
                :selected-location="selectedLocation"
                :schedule-data="scheduleData"
                @month-change="handleMonthChange"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axiosapi from '@/plugins/axios.js'
import Selector from '../../../components/buttons/b5/Selector.vue';
import B04Calendar from './B04Calendar.vue';

const selectedLocation = ref(null);
const selectedYear = ref(new Date().getFullYear());
const selectedMonth = ref(new Date().getMonth() + 1);
const scheduleData = ref([]);
const loading = ref(false);

// 新增處理月份變化的函數
const handleMonthChange = async ({ year, month }) => {
    console.log('月份變化:', { year, month });
    
    // 確保 selectedLocation 存在且有 venueId
    if (!selectedLocation.value || !selectedLocation.value.venueId) {
        console.warn('沒有選擇場地');
        return;
    }
     // 更新選中的年月
    selectedYear.value = year;
    selectedMonth.value = month;

    loading.value = true;
    try {
        console.log('準備獲取數據，場地ID:', selectedLocation.value.venueId);
        const response = await axiosapi.get(`/venue-status/${year}/${month}/venue/${selectedLocation.value.venueId}`);
        
        if (Array.isArray(response.data)) {
            const transformedData = response.data.map(item => ({
                date: item.date,
                status: item.status,
                venueId: selectedLocation.value.venueId,
                venueStatusId: item.venueStatusId
            }));
            console.log('更新月份數據:', transformedData);
            scheduleData.value = transformedData;
        }
    } catch (err) {
        console.error('獲取新月份數據錯誤:', err);
        scheduleData.value = [];
    } finally {
        loading.value = false;
    }
};

// 修改獲取數據的函數，使其可復用
const fetchScheduleData = async (venueId, year, month) => {
    loading.value = true;
    try {
        const response = await axiosapi.get(`/venue-status/${year}/${month}/venue/${venueId}`);
        
        if (Array.isArray(response.data)) {
            return response.data.map(item => ({
                date: item.date,
                status: item.status,
                venueId: venueId,
                venueStatusId: item.venueStatusId
            }));
        }
        return [];
    } catch (err) {
        console.error('API 調用錯誤:', err);
        return [];
    } finally {
        loading.value = false;
    }
};

const handleLocationSelected = async (selectedData) => {
    console.log('收到選擇器數據:', selectedData);

    if (!selectedData.venueId) {
        scheduleData.value = [];
        selectedLocation.value = null;
        return;
    }

    selectedLocation.value = selectedData; // 先更新 location
    
    selectedLocation.value = selectedData; // 先更新 location
    
    const data = await fetchScheduleData(
        selectedData.venueId,
        selectedYear.value,  // 使用已選擇的年份
        selectedMonth.value  // 使用已選擇的月份
    );
    scheduleData.value = data;
};

onMounted(() => {
    console.log('組件掛載，準備初始化 A01');
    handleLocationSelected({
        category: 'warehouse',
        venueId: 'A01',
        venueName: '1號倉庫',
        type: 'venue'
    });
});
</script>

<style scoped>
.schedule-wrapper {
    padding-top: 0.5%;
    width: 97%;
    /* margin: 0 auto; */
    position: relative;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    height: 88vh;

    
}

.schedule-container {
    text-align: center;
    margin-bottom: 1%;

} 
h3{
    margin-bottom: 2.5%;
    font-size: 28px;
  }

.calendar-container {
    background-color: #fff;
    border-radius: 8px;
    /* box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); */
    padding: 20px;
    }

.loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3498db;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 8px;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
    .schedule-wrapper {
        width: 100%;
        padding: 10px;
    }
    
    .calendar-container {
        /* padding: 10px; */
    }
}
</style>