<template>
  <div class="filter-container">
    <div class="filter-header">
      <h2 :class="['title', { 'hidden-title': !isExpanded }]">節目篩選</h2>
      <button @click="toggleFilters" class="filter-toggle">
        篩選條件
        <span :class="['arrow', { 'arrow-up': isExpanded }]">▼</span>
      </button>
    </div>
    <!-- 過濾器區域 -->
    <div :class="['filters', { expanded: isExpanded }]">
      <div class="filter-row">
        <div class="filter-item">
          <select v-model="selectedCategory" @change="fetchEvents" class="form-select">
            <option value="">全部類別</option>
            <option v-for="category in categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
        </div>

        <div class="filter-item">
          <select v-model="selectedLocation" @change="fetchEvents" class="form-select">
            <option value="">節目地點</option>
            <option v-for="location in locations" :key="location" :value="location">
              {{ location }}
            </option>
          </select>
        </div>

        <div class="filter-item">
          <select v-model="selectedYear" @change="fetchEvents" class="form-select">
            <option value="">年份</option>
            <option v-for="year in years" :key="year" :value="year">
              {{ year }}
            </option>
          </select>
        </div>

        <div class="filter-item">
          <select v-model="selectedMonth" @change="fetchEvents" class="form-select">
            <option value="">月份</option>
            <option v-for="month in months" :key="month" :value="month">
              {{ month }}
            </option>
          </select>
        </div>

        <button @click="clearFilters" class="clear-btn">清除選項</button>
      </div>
    </div>

    <div v-if="events.length > 0" class="events-list">
      <div
        v-for="event in events"
        :key="event.eventName"
        class="event-card"
        @click="goToEventDetail(event.eventId)"
      >
        <!-- 如果有圖片則顯示圖片，否則顯示替代文字 -->
        <div v-if="event.images && event.images.length > 0">
          <img :src="event.images[0].imageUrl" alt="活動圖片" class="event-image" />
        </div>
        <div v-else class="no-image-text">Events</div>
        <div class="event-content">
          <h3>
            {{ event.eventName }}
            <span v-if="event.category"> - {{ event.category }}</span>
          </h3>
          <div class="event-description">
            <p>{{ event.eventDescription1 }}</p>
          </div>
          <div class="event-details">
            <div class="event-info">
              <p>
                活動時間：{{ formatDate(event.eventStartDate) }} ~
                {{ formatDate(event.eventEndDate) }}
              </p>
              <p>地點：{{ event.venueName }}</p>
              <p>
                票價：{{ event.eventPrice === 0 ? "免費" : event.eventPrice + " 元" }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <p v-else></p>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axiosapi from '@/plugins/axios.js'
import Swal from "sweetalert2";

// Vue Router
const router = useRouter();

// 篩選條件
const isExpanded = ref(false);
const selectedCategory = ref("");
const selectedLocation = ref("");
const selectedYear = ref("");
const selectedMonth = ref("");

// API 回傳的活動清單
const events = ref([]);

// 選項數據
const categories = ["展覽", "演唱會", "活動與講座", "音樂劇", "其他"];
const locations = [
  "倉庫群",
  "1號倉庫",
  "2號倉庫",
  "3號倉庫",
  "4號倉庫",
  "5號倉庫",
  "春田綠地藝術廳",
  "春田序曲",
  "瑪吉的畫廊",
  "巴特的藝術空間",
  "荷馬的闢護所",
  "瑪吉的畫廊",
];
const years = ["2024", "2025", "2026"];
const months = Array.from({ length: 12 }, (_, i) => i + 1);

// 展開/收合切換
const toggleFilters = () => {
  isExpanded.value = !isExpanded.value;
};

const goToEventDetail = (eventId) => {
  // 清空篩選結果與相關狀態
  events.value = [];
  selectedCategory.value = "";
  selectedLocation.value = "";
  selectedYear.value = "";
  selectedMonth.value = "";

  // 跳轉到活動詳情頁
  router.push({ name: "EventDetail", params: { id: eventId } });
};

// 清除篩選條件
const clearFilters = () => {
  // 重置所有篩選條件
  selectedCategory.value = "";
  selectedLocation.value = "";
  selectedYear.value = "";
  selectedMonth.value = "";

  // 直接清空活動列表
  events.value = [];

  // 使用 SweetAlert2 顯示成功提示，並將 OK 按鈕設為灰色
  Swal.fire({
    icon: "success",
    title: "已清除篩選條件",
    confirmButtonColor: "#6c757d", // 灰色
    showConfirmButton: true,
    timer: 1500,
  });
};

// 發送 API 請求以獲取活動
const showEventsModal = ref(false);
const openEventsModal = () => {
  if (events.value.length > 0) {
    Swal.fire({
      title: "活動列表",
      html: `
        <div style="max-height: 500px; overflow-y: auto; text-align: left;">
          ${events.value
            .map((event) => {
              // 檢查是否有圖片
              const hasImage = event.images?.length > 0;
              const imageHTML = hasImage
                ? `<img src="${event.images[0].imageUrl}"
                        style="width: 100%; height: auto; border-radius: 4px; margin-top: 10px;">`
                : `<div style="width: 100%; height: 150px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; color: #888; border-radius: 4px; margin-top: 10px;">
                    活動圖片
                   </div>`;
              return `
                <div style="border-bottom: 1px solid #ddd; padding: 10px;">
                  <h3 style="color: #333;">${event.eventName}</h3>
                  <p>活動時間：${formatDate(event.eventStartDate)} ~ ${formatDate(
                event.eventEndDate
              )}</p>
                 <p>類別：${event.category}</p>
                  <p>地點：${event.venueName}</p>
                  <p>票價：${
                    event.eventPrice === 0 ? "免費" : event.eventPrice + " 元"
                  }</p>
                  <p>${event.eventDescription1}</p>
                  ${imageHTML}
                  <button style="margin-top: 10px; padding: 8px 16px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;"
                    onclick="goToEventDetail(${event.eventId}, '${event.eventName}')">
                    活動詳細
                  </button>
                </div>
              `;
            })
            .join("")}
        </div>
      `,
      confirmButtonText: "關閉",
      confirmButtonColor: "#6c757d",
    });
  } else {
    Swal.fire({
      icon: "info",
      title: "沒有找到符合的活動",
      text: "請更換篩選條件再試一次。",
      confirmButtonColor: "#6c757d",
    });
  }
};

const fetchEvents = async () => {
  try {
    // 如果使用者輸入了年但沒輸入月，則提示要輸入月份
    if (selectedYear.value && !selectedMonth.value) {
      Swal.fire({
        icon: "warning",
        title: "請選擇月份",
        text: "請選擇一個月份以進行查詢",
        confirmButtonColor: "#6c757d",
      });
      return;
    }

    const response = await axiosapi.get(
      "/api/events-user/search/multi",
      {
        params: {
          eventCategoryName: selectedCategory.value || null,
          venueName: selectedLocation.value || null,
          // 確保 year 和 month 以數字傳遞
          year: selectedYear.value ? parseInt(selectedYear.value, 10) : null,
          month: selectedMonth.value ? parseInt(selectedMonth.value, 10) : null,
        },
      }
    );
    events.value = response.data;
    // 當篩選完後，3秒後自動縮起來
    setTimeout(() => {
      isExpanded.value = false;
    }, 10000);
    isExpanded.value = true;
  } catch (error) {
    console.error("獲取活動失敗", error);
    events.value = [];
    Swal.fire({
      icon: "error",
      title: "未找到符合條件的活動",
      text: "請稍後再試",
      confirmButtonColor: "#6c757d",
    });
  }
};

// 日期格式化函數
const formatDate = (dateString) => {
  if (!dateString) return "未知";
  const date = new Date(dateString);
  return date.toISOString().split("T")[0];
};
</script>

<style scoped>
.filter-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: system-ui, -apple-system, sans-serif;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: #fff;
  padding: 15px;
}

.title {
  font-size: 20px;
  color: #333;
  margin: 0;
  font-weight: normal;
}

.filter-toggle {
  padding: 8px 16px;
  background: none;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  transition: all 0.3s ease;
}

.filter-toggle:hover {
  background-color: #f5f5f5;
}

.arrow {
  display: inline-block;
  transition: transform 0.3s ease;
  font-size: 12px;
}

.arrow-up {
  transform: rotate(180deg);
}

.filters {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease-out;
  background-color: #fff;
  border-radius: 0 0 4px 4px;
}

.filters.expanded {
  max-height: 500px;
}

.filter-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
  padding: 20px;
}

.filter-item {
  flex: 1;
  min-width: 150px;
}

.form-select {
  cursor: pointer; /* 讓整張卡片變成手型指標 */
  transform: translateY(-5px) scale(1.02); /* 浮起及放大效果 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15); /* 陰影效果 */
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  background-color: #fff;
  color: #333;
  font-size: 14px;
  transition: border-color 0.3s ease;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 8px center;
  background-size: 16px;
}

.form-select:focus {
  outline: none;
  border-color: #666;
}

.clear-btn {
  padding: 8px 16px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.clear-btn:hover {
  background-color: #f5f5f5;
  border-color: #d5d5d5;
}

.events-list {
  margin-top: 20px;
}

.event-card {
  display: flex;
  align-items: stretch;
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 16px;
  gap: 16px;
  overflow: hidden; /* 防止內容溢出 */
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* 添加過渡效果 */
}
.event-card:hover {
  transform: translateY(-5px) scale(1.02); /* 浮起及放大效果 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15); /* 陰影效果 */
}
.event-content {
  font-family: Kantumruy Pro;
  font-family: Kantumruy Pro;
  flex: 1;
}

.event-details {
  display: flex;
  gap: 20px;
}

.event-info {
  flex: 0 0 auto;
  min-width: 200px;
}

.event-description {
  flex: 1;
}
.event-image {
  width: 150px;
  height: 80%; /* 填滿父容器高度 */
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0; /* 防止圖片被壓縮 */
  margin: -16px 0 -16px -16px; /* 抵消父容器的 padding */
}

.event-card h3 {
  font-size: 18px;
  margin: 0 0 12px 0;
  color: #333;
}

.event-card p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}
.title {
  border-color: #666;
  font-size: 1rem;
  font-weight: bold;
  margin-top: 1px;
  margin-bottom: 1px;
  text-align: center;
}
/* 隱藏但保留空間 */
.hidden-title {
  visibility: hidden;
}
.no-image-text {
  width: 150px; /* 與 .event-image 的寬度相同 */
  height: 100%; /* 填滿父容器高度 */
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5; /* 淺灰背景 */
  color: #888; /* 文字顏色 */
  border-radius: 4px;
  font-size: 14px;
}
</style>
