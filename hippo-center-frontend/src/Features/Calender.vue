
<template>
  <div class="calendar-container">
      <h1>節目行事曆</h1>
  
  
      <!-- 月曆導航 -->
      <div class="calendar-navigation d-flex justify-content-center align-items-center my-3">
      <!-- 上一月按鈕 -->
      <button class="btn btn-primary me-3" @click="goToPreviousMonth">上一月</button>
  
  
      <!-- 年份和月份選項 -->
      <div class="d-flex align-items-center">
          <select class="form-select me-2" v-model="selectedYear" @change="updateCalendar">
          <option v-for="year in yearRange" :key="year" :value="year">{{ year }}</option>
          </select>
          <select class="form-select" v-model="selectedMonth" @change="updateCalendar">
          <option v-for="(month, index) in months" :key="index" :value="index">
              {{ month }}
          </option>
          </select>
      </div>
  
  
      <!-- 下一月按鈕 -->
      <button class="btn btn-primary ms-3" @click="goToNextMonth">下一月</button>
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
              v-for="day in week"
              :key="day.date"
              :class="{ 'table-primary': day.isToday, 'text-muted': !day.isCurrentMonth }"
          >
              <div v-if="day.date">{{ day.date.getDate() }}</div>
              <!-- 顯示事件 -->
              <div v-for="event in day.events" :key="event.title" class="event">
              <button class="btn btn-outline-primary btn-sm" @click="handleEventClick(event)">
                  {{ event.title }}
              </button>
              </div>
          </td>
          </tr>
      </tbody>
      </table>
  </div>
  </template>
  
  
  <script>
  import axios from "axios";
  
  
  export default {
  name: "Calendar",
  data() {
      return {
      currentMonth: new Date(), // 當前月份
      selectedYear: new Date().getFullYear(), // 選中的年份
      selectedMonth: new Date().getMonth(), // 選中的月份 (0-11)
      events: [], // 從後端獲取的事件列表
      };
  },
  computed: {
      yearRange() {
      const startYear = 2000;
      const endYear = 2030;
      return Array.from({ length: endYear - startYear + 1 }, (_, i) => startYear + i);
      },
      months() {
      return [
          "一月",
          "二月",
          "三月",
          "四月",
          "五月",
          "六月",
          "七月",
          "八月",
          "九月",
          "十月",
          "十一月",
          "十二月",
      ];
      },
      calendar() {
      const startOfMonth = new Date(this.selectedYear, this.selectedMonth, 1);
      const endOfMonth = new Date(this.selectedYear, this.selectedMonth + 1, 0);
  
  
      // 以星期一為第一天的偏移計算
      const startDay = (startOfMonth.getDay() + 6) % 7;
      const daysInMonth = endOfMonth.getDate();
  
  
      const calendar = [];
      let week = [];
  
  
      // 補全本月第一天前的空白
      for (let i = 0; i < startDay; i++) {
          week.push({ date: null, isCurrentMonth: false, isToday: false, events: [] });
      }
  
  
      // 填入本月的日期
      for (let day = 1; day <= daysInMonth; day++) {
          const date = new Date(this.selectedYear, this.selectedMonth, day);
          const isToday = this.isToday(date);
          const dayEvents = this.events.filter(
          (event) => event.date === date.toISOString().split("T")[0]
          );
  
  
          week.push({ date, isCurrentMonth: true, isToday, events: dayEvents });
  
  
          if (week.length === 7) {
          calendar.push(week);
          week = [];
          }
      }
  
  
      // 補全本月最後一天後的空白
      while (week.length < 7) {
          week.push({ date: null, isCurrentMonth: false, isToday: false, events: [] });
      }
      calendar.push(week);
  
  
      return calendar;
      },
      weekDays() {
      return ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
      },
  },
  methods: {
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
      this.currentMonth = new Date(this.selectedYear, this.selectedMonth);
      this.fetchEvents(); // 每次更新月份時重新獲取資料
      },
      isToday(date) {
      const today = new Date();
      return (
          today.getDate() === date.getDate() &&
          today.getMonth() === date.getMonth() &&
          today.getFullYear() === date.getFullYear()
      );
      },
      async fetchEvents() {
      try {
          const response = await axios.get("http://localhost:8080/user/tours", {
          withCredentials: true,
          });
  
  
          // 確保後端返回的資料格式為(date: "YYYY-MM-DD", title: "活動名稱")
          this.events = response.data.map((event) => ({
          date: event.date, // 日期
          title: event.title, // 標題
          }));
      } catch (error) {
          console.error("獲取事件資料出錯時:", error);
      }
      },
      handleEventClick(event) {
      alert(`你點擊了活動：${event.title}`);
      // 在此處添加其他業務邏輯，例如導航到活動詳情頁
      },
  },
  mounted() {
      this.fetchEvents(); // 初始化時載入事件資料
  },
  };
  </script>
  
  
  <style>
  .event {
  margin-top: 5px;
  }
  
  
  .btn-sm {
  font-size: 14px;
  padding: 5px 10px;
  }
  
  
  .table-primary {
  background-color: #f8f9fa !important; /* 更改選中格子的背景色 (非藍色) */
  }
  
  
  /* 固定單元格的大小 */
  table.table {
  table-layout: fixed; /* 設定表格為固定佈局 */
  width: 100%; /* 確保表格寬度佔滿 */
  }
  
  
  table.table td {
  width: 14.28%; /* 平均分配 7 列的寬度 */
  height: 120px; /* 固定高度 */
  vertical-align: top; /* 內容靠上對齊 */
  overflow: hidden; /* 隱藏超出部分 */
  text-overflow: ellipsis; /* 加上省略號 */
  white-space: nowrap; /* 禁止文字換行 */
  }
  </style>
  