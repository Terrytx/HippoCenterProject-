<template>
<div class="calendar-container">
    <h1 class="calendar-header">
        導覽行事曆
    </h1>

    <!-- 月曆導航 -->
    <div class="calendar-navigation">
    <!-- 上個月 -->
    <button class="nav-button" @click="goToPreviousMonth">‹ 上個月</button>

    <!-- 年份與月份選擇 -->
    <div class="month-display">
        <div class="custom-select">
            <select v-model="selectedYear" @change="updateCalendar">
                <option v-for="year in yearRange" :key="year" :value="year">{{ year }} 年</option>
            </select>
            <span class="arrow-down">▼</span>
        </div>
        <div class="custom-select">
            <select v-model="selectedMonth" @change="updateCalendar">
                <option v-for="(month, index) in months" :key="index" :value="index">{{ month }}</option>
            </select>
            <span class="arrow-down">▼</span>
        </div>
    </div>

    <!-- 下個月 -->
    <button class="nav-button" @click="goToNextMonth">下個月 ›</button>
</div>

    <!-- 月曆表格 -->
    <table class="table table-bordered text-center">
    <thead>
        <tr>
        <th v-for="day in weekDays" :key="day">{{ day }}</th>
        </tr>
    </thead>
    <tbody>
        <tr v-for="(week, index) in calendar" :key="index">
            <td
                v-for="(day, dayIndex) in week"
                :key="day.date"
                :class="{
                    'table-primary': day.isToday,
                    'text-muted': !day.isCurrentMonth,
                    'holiday-cell': day.isHoliday,
                    'weekend': dayIndex === 5 || dayIndex === 6  // 標記週六和週日
                }"
            >
                <!-- 日期數字 -->
                <div v-if="day.date" class="date-number">{{ day.date.getDate() }}</div>

                <!-- 休館標籤 -->
                <div v-if="day.label" class="holiday-label">{{ day.label }}</div>

                <!-- 行程活動 -->
                <div class="event-container">
                    <div v-for="tour in day.tours" :key="tour.toursId" class="event">
                        <div class="event-item">
                            <button
                                class="btn btn-outline-primary btn-sm"
                                :class="{ 'expired': isExpired(tour.toursDate, tour.timeSlot) }"
                                :disabled="isExpired(tour.toursDate, tour.timeSlot)"
                                @click="handleTourClick(tour.toursId)"
                            >
                                <span :class="{ 'strikethrough': isExpired(tour.toursDate, tour.timeSlot) }">
                                    {{ tour.toursName }}
                                </span>
                            </button>
                            <span class="time-slot">{{ tour.timeSlot }}</span>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
    </tbody>
    </table>

        <!-- 導覽說明視窗 -->
            <GuideInfo />

        <!-- 預約導覽視窗 -->
        <Reservation
        :isVisible="isModalVisible" :toursId="selectedReservationId" @close="isModalVisible = false"/>
    </div>
</template>

<script>
import axiosapi from '@/plugins/axios.js';
import Reservation from "./reservation.vue";
import GuideInfo from "./guideInfo.vue";

export default {
name: "Calendar",
components: {
    Reservation,
    GuideInfo,
},
data() {
    return {
    currentMonth: new Date(),
    selectedYear: new Date().getFullYear(),
    selectedMonth: new Date().getMonth(),
    tours: [],
    isModalVisible: false,
    selectedReservationId: null,
    };
},
computed: {
    yearRange() {
    const startYear = 2000;
    const endYear = 2030;
    return Array.from({ length: endYear - startYear + 1 }, (_, i) => startYear + i);
    },
    months() {
    return ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"];
    },
    calendar() {
    const startOfMonth = new Date(this.selectedYear, this.selectedMonth, 1);
    const endOfMonth = new Date(this.selectedYear, this.selectedMonth + 1, 0);
    const startDay = (startOfMonth.getDay() + 6) % 7;
    const daysInMonth = endOfMonth.getDate();

    const calendar = [];
    let week = [];

    for (let i = 0; i < startDay; i++) {
        week.push({ date: null, isCurrentMonth: false, isToday: false, tours: [], isHoliday: false });
    }

    for (let day = 1; day <= daysInMonth; day++) {
        const date = new Date(this.selectedYear, this.selectedMonth, day);
        const isToday = this.isToday(date);

        // 判斷是否為過年期間
        const isHoliday = 
            this.selectedMonth === 0 && day >= 25 && day <= 31 ||
            this.selectedMonth === 1 && day >= 2 && day <= 2;
        const label = isHoliday ? "過年期間休館" : null;

        const dayTours = this.tours.filter((tour) => {
        const tourDate = new Date(tour.toursDate);
        return ( // 在計算行程時，確保比較的日期只考慮年月日部分
        tourDate.getFullYear() === date.getFullYear() &&
        tourDate.getMonth() === date.getMonth() &&
        tourDate.getDate() === date.getDate()
    );
});

        week.push({ date, isCurrentMonth: true, isToday, tours: dayTours, isHoliday, label });

        if (week.length === 7) {
        calendar.push(week);
        week = [];
        }
    }

    while (week.length < 7) {
        week.push({ date: null, isCurrentMonth: false, isToday: false, tours: [], isHoliday: false });
    }
    calendar.push(week);

    return calendar;
    },
    weekDays() {
    return ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"];
    },
},
methods: {
    isExpired(tourDate, timeSlot) {
        if (!timeSlot) return false; // 如果沒有時間資訊，預設為未過期

        console.log(`Checking tourDate: ${tourDate}, timeSlot: ${timeSlot}`);

        // 擷取開始時間（去掉 "-12:00" 部分）包含另外兩個時段
        const startTime = timeSlot.split("-")[0].trim(); // 取得 "10:00"
        const [tourHour, tourMinute] = startTime.split(":").map(Number);
        const tourTimeMinutes = tourHour * 60 + tourMinute; // 轉換為總分鐘數

        // 解析行程日期
        const tourDateObj = new Date(tourDate);
        const now = new Date();

        // 取得當前時間（分鐘）
        const currentMinutes = now.getHours() * 60 + now.getMinutes();

        // 只比較年月日，忽略時間
        now.setHours(0, 0, 0, 0);
        tourDateObj.setHours(0, 0, 0, 0);

        // 如果行程是過去的日期，則標記為過期
        if (tourDateObj < now) {
            console.log(`Tour on ${tourDate} is expired (past date)`);
            return true;
        }

        // 如果行程是今天，檢查開始時間
        if (tourDateObj.getTime() === now.getTime()) {
            if (currentMinutes >= tourTimeMinutes) {
                console.log(`Tour at ${startTime} is expired (current time: ${currentMinutes} minutes)`);
                return true;
            }
        }

        console.log(`Tour at ${startTime} is still available.`);
        return false; // 其他情況不過期
    },
    goToPreviousMonth() {
    if (this.selectedMonth === 0) {
        this.selectedMonth = 11;
        this.selectedYear--;
    } else {
        this.selectedMonth--;
    }
    this.updateCalendar();
    },
    goToNextMonth() {
    if (this.selectedMonth === 11) {
        this.selectedMonth = 0;
        this.selectedYear++;
    } else {
        this.selectedMonth++;
    }
    this.updateCalendar();
    },
    updateCalendar() {
    this.fetchTours();
    },
    isToday(date) {
    const today = new Date();
    return (
        today.getDate() === date.getDate() &&
        today.getMonth() === date.getMonth() &&
        today.getFullYear() === date.getFullYear()
        );
    },
    async fetchTours() {
    try {
        const response = await axiosapi.get("/user/tours", {
            withCredentials: true,
        });
        console.log("後端返回的行程資料:", response.data); // 查看返回的完整資料

        // 將後端資料丟到前端格式
        this.tours = response.data.map((tour) => ({
            toursId: tour.toursId,
            toursDate: tour.toursDate,
            toursName: tour.toursName,
            toursDescription: tour.toursDescription,
            timeSlot: tour.timeSlot, 
            capacity: tour.capacity, 
        }));
    } catch (error) {
        console.error("獲取資料錯誤:", error);
    }
},
    handleTourClick(toursId) {
    console.log("Selected toursId:", toursId); // 確認點擊行程按鈕後的 toursId
    this.selectedReservationId = toursId;
    this.isModalVisible = true;
    },
},
mounted() {
    this.fetchTours();
    },
};
</script>

<style scoped>
@import '@/assets/fonts/font.css';
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@700&display=swap');

.calendar-container {
min-height: 100vh; /* 保持行事曆至少填滿視窗高度 */
overflow: auto; /* 保持滾動功能 */
padding: 20px 90px 80px 90px; /* 上 右 下 左 */
}

.calendar-header {
display: inline-flex; /* 使用 inline-flex 讓內部元素排成同一行 */
align-items: center; /* 垂直對齊 */
}

.calendar-navigation {
display: flex;
justify-content: space-between;
align-items: center;
margin: 20px 0;
padding: 10px;
}

/* 讓按鈕更精緻 */
.nav-button {
background-color: transparent;
border: none;
font-size: 21px;
font-weight: bold;
cursor: pointer;
padding: 5px 10px;
transition: 0.3s;
}

.nav-button:hover {
color: #f8961e;
}

/* 讓年份與月份選擇框更美觀 */
.month-display {
display: flex;
align-items: center;
gap: 15px;
font-size: 20px;
font-weight: bold;
}

/* 讓 select 看起來像普通文字 */
.month-display select {
font-size: 20px;
font-weight: bold;
border: none;
background: transparent;
appearance: none; /* 移除瀏覽器的預設樣式 */
text-align: center;
padding: 0;
cursor: pointer;
}

/* 自訂的 select 容器 */
.custom-select {
position: relative;
display: inline-block;
}

/* 讓 select 變得更好看 */
.custom-select select {
font-size: 20px;
font-weight: bold;
border: 1px solid #ccc;
background: white;
padding: 5px 30px 5px 10px;
border-radius: 5px;
cursor: pointer;
appearance: none;
}

/* 自訂 select 的箭頭 */
.custom-select .arrow-down {
position: absolute;
right: 10px;
top: 50%;
transform: translateY(-50%);
font-size: 14px;
color: gray;
pointer-events: none;
}

/* 當選單被點擊時去除 focus 外框 */
.custom-select select:focus {
outline: none;
border-color: black;
}

/* 禁止選擇框的邊框顯示 */
.month-display select:focus {
outline: none;
}

/* 讓選擇框內的箭頭消失，只保留純文字 */
.month-display select::-ms-expand {
display: none;
}

table.table-bordered {
border-collapse: collapse; /*合併相鄰格子的邊框*/
table-layout: fixed; /* 固定表格列寬,避免因內容改變 */
width: 100%;         /* 確保表格寬度填滿容器 */
border: 3px solid black; /* 設定粗邊框 */
}

table.table-bordered td {
width: 10px;   /* 設定每格的寬度 */
height: 175px;  /* 設定每格的高度 */
vertical-align: top; /* 確保日期從上方對齊 */
overflow: hidden;    /* 隱藏超出部分內容 */
position: relative;
text-align: center;
border: 1px solid darkgray; /* 統一邊框樣式,不能刪 */
}

table.table-bordered thead th {
font-size: 1.2rem; /* 調整星期文字大小 */
font-weight: bold; /* 加粗讓星期標題更明顯 */
text-align: center;
padding: 13px; /* 增加間距 */
background-color: #5B5B5B; /* 深灰色背景 */
color: white; /* 白色字體 */
}

.table-primary {
background-color: #F0F0F0  /* 今天日期底色 */
}

.holiday-label {
position: absolute;
background-color: #ffe6e6;
color: rgba(233, 21, 21, 0.822);
padding: 5px 10px;
border-radius: 5px;
border: 1px solid #ff9999; /* 添加淺紅色邊框 */
display: inline-block;
font-weight: bold;
text-align: center;
position: absolute;
top: 50%;
left: 50%;
transform: translate(-50%,-50%);
}

td.holiday-cell > div:first-child {
position: absolute;
top: 8px;
left: 10px;
}

.btn-outline-primary {
    background-color: #333333; /* 變為黑底 */
    color: white; /* 文字變白 */
    border: 1.5px solid black; /* 邊框黑色 */
}

.btn-outline-primary:hover {
    background-color: white; /* 滑鼠懸停變白底 */
    color: black; /* 文字變黑 */
    border: 2px dashed black; /* 邊框保持虛線 */
}

/* 過期導覽按鈕 */
.expired {
color: gray !important;
border: 1.5px dashed gray !important; /* 虛線邊框 */
background-color: #f0f0f0 !important;
cursor: not-allowed !important;
}

.strikethrough {
text-decoration: line-through; /* 加上刪除線 */
color: gray; /* 讓字體變灰色 */
}

.custom-nav-button {
background-color: white; /* 月份 */
border-color: black; /* 月份 */
font-size: 1rem; /* 調整按鈕文字大小 */
padding: 4px 4px; /* 增加內邊距，讓按鈕更好看 */
background-color: white;
border: 2px solid black;
border-radius: 5px; /* 讓按鈕邊角稍微圓潤 */
}

.custom-nav-button:hover {
color: white;
background-color: black; /* 滑鼠懸停月份時的顏色 */
border-color: black; /* 滑鼠懸停月份時的邊框顏色 */
}

.form-select {
font-size: 1rem; /* 調整年份、月份選擇框的文字大小 */
padding: 4px 4px; /* 增加內邊距 */
}

.time-slot {
font-size: 15px;
color: gray;
white-space: nowrap;   /* 確保不換行 */
}

.event button {
margin: 2px 0; /* 導覽按鈕之間的距離 */
border-radius: 5px;
padding: 5px 4px; /* 增加按鈕內邊距 */
font-size: 15px; /* 調大字體 */
font-family: 'Roboto', sans-serif; /* 設定字型 */
font-weight: bold; /* 讓字體加粗 */
cursor: pointer;
}

.event button:hover {
background-color: #f8aa4b !important;
color: black !important;
border: 2px dotted black !important;
}

.event-item {
display: flex;           /* 讓內容並排 */
align-items: center;      /* 垂直對齊 */
justify-content: flex-start; /* 讓內容靠左對齊 */
gap: 2px;                /* 按鈕與時間區段之間的間距 */
margin-left: 35px;       /* 整體向右移動 */
}

/* 改變日期位置 */
.date-number {
position: absolute;
top: 8px; /* 與邊緣稍微拉開 */
left: 10px; /* 與左邊留點距離 */
font-size: 16px; /* 讓日期變大 */
font-family: 'Roboto', sans-serif; /* 更換字型 */
color: #333; /* 讓數字顏色較深，提高可讀性 */
}

.event-container {
display: flex;
flex-direction: column; /* 確保行程按鈕是直列顯示 */
margin-top: 20px; /* 讓行程內容與日期數字有距離 */
align-items: flex-start; /* 讓內容靠左對齊（避免置中） */
padding-left: 15px; /* 往右移動 */
}

.weekend .date-number {
color: rgba(255, 0, 0, 0.87);
}

</style>