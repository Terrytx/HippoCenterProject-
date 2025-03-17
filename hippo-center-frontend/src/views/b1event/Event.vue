<template>
  <div class="page-wrapper">
    <div class="breadcrumb-wrapper">
      <BreadCrumb :breadcrumb-items="breadcrumbItems" />
    </div>

    <div class="container">
      <div class="main-content">
        <div class="title">節目總覽</div>
        <div class="title">Events </div>
        <div class="filter-container">
          <FilterComponent @refreshData="fetchDataFromParent" />
        </div>

        <div class="cards-container">
          <div v-for="(card, index) in cards" :key="index" class="card">
            <div class="card-image-container">
              <img
                :src="card.image || '/src/assets/images/default.jpg'"
                :alt="card.eventName"
                class="card-img-top"
                @click="goToEventDetail(card.id, card.eventName)"
                style="cursor: pointer"
              />
            </div>
            <a
              class="btn-btn-secondary"
              :class="{ active: card.isFavorite }"
              @click="addToFavorites(card.id)"
            >
              <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                <path
                  d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 
                     2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 
                     4.5 2.09C13.09 3.81 14.76 3 
                     16.5 3 19.58 3 22 5.42 
                     22 8.5c0 3.78-3.4 6.86-8.55 
                     11.54L12 21.35z"
                ></path>
              </svg>
            </a>

            <div class="card-body">
              <p class="card-text event-category">
                {{ card.eventCategory }}
              </p>
              <h5 class="card-title">{{ card.eventName }}</h5>
              <p class="cardvenue-text">活動場地:{{ card.venueName }}</p>
              <p class="cardDate-text">
                活動日期：{{ formatDate(card.eventStartDate) }} ~
                {{ formatDate(card.eventEndDate) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axiosapi from '@/plugins/axios.js'
// sweetalert 已匯入，但建議可以改為 import Swal from "sweetalert2"
import sweetalert from "sweetalert2";
import FilterComponent from "../../components/model/FilterComponent.vue";
import BreadCrumb from "@/components/model/BreadCrumb.vue";

// 定義該頁面的麵包屑項目
const breadcrumbItems = [
  { text: "荷馬市藝術文化中心", path: "/" },
  { text: "節目總覽", path: "/event" },
  { text: "節目回顧" },
];

const router = useRouter();
const cards = ref([]);

const fetchDataFromParent = async () => {
  await fetchEvents(); // 重新獲取活動資料
};

// 日期格式化函式
const formatDate = (dateString) => {
  if (!dateString) return "未提供";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

const goToEventDetail = (id, eventName) => {
  console.log(`準備導航 - ID: ${id}, Name: ${eventName}`);

  if (!id) {
    console.error("錯誤：活動 ID 未定義");
    return;
  }

  router.push({
    name: "EventDetail",
    params: {
      id: String(id),
      eventName: eventName,
    },
  });
};


// 新增「我的最愛」按鈕的點擊事件函式，改用 sweetalert2 做通知
const addToFavorites = async (id) => { 
  console.log("切換我的最愛狀態，ID:", id);

  // 從 localStorage 或其他地方取得 JWT token
  const token = localStorage.getItem("authToken");
  if (!token) {
    sweetalert
      .fire({
        icon: "warning",
        title: "請先登入！",
        confirmButtonText: "確定",
        confirmButtonColor: "#6c757d"
      })
      .then(() => {
        // 跳轉至會員登入頁面
        window.location.href = "/secure/login";
      });
    return;
  }

  // 假設 myloveType 為 'E' (代表活動)
  const myloveType = "E";

  try {
    // 呼叫後端 API，傳送 myloveId (這裡後端會自動切換最愛狀態)
    const response = await axiosapi.post(
      `/secure/mylove/toggle/${myloveType}`,
      { myloveId: id },
      {
        headers: {
          Authorization: "Bearer " + token,
          "Content-Type": "application/json",
        },
      }
    );

    // 更新卡片的 isFavorite 狀態，並根據新狀態顯示相應訊息
    const card = cards.value.find((c) => c.id === id);
    if (card) {
      card.isFavorite = !card.isFavorite;
      const messageText = card.isFavorite ? "成功加入我的最愛" : "成功從我的最愛移除";
      sweetalert.fire({
        icon: "success",
        title: "提示",
        text: messageText,
        confirmButtonText: "確定",
        confirmButtonColor: "#6c757d"
      });
    }
  } catch (error) {
    console.error("切換最愛狀態時出錯:", error);
    sweetalert.fire({
      icon: "error",
      title: "錯誤",
      text: "切換最愛狀態時出錯，請稍後再試！",
      confirmButtonText: "確定",
      confirmButtonColor: "#6c757d"
    });
  }
};

// 從後端獲取活動數據
const fetchEvents = async () => {
  try {
    const response = await axiosapi.get("/api/events-user/all");

    console.log("API 回傳的活動資料:", response.data);
    console.log(
      "每個事件的 ID:",
      response.data.map((event) => event.id)
    );
    // 檢查原始資料
    console.log("API 原始回應:", response.data);

    if (response.data) {
      // 檢查每個活動的結構
      response.data.forEach((event, index) => {
        console.log(`活動 ${index + 1} 的完整結構:`, event);
        console.log(`活動 ${index + 1} 的 ID:`, event.eventId);
      });

      cards.value = response.data.map((event) => {
        // 確保從正確的屬性獲取 ID
        const eventId = event.eventId || event.id || event.event_id;
        return {
          id: eventId,
          eventName: event.eventName,
          eventStartDate: event.eventStartDate,
          eventEndDate: event.eventEndDate,
          eventCategory: event.eventCategory,
          venueName: event.venueName,
          image: event.images?.[0]?.imageUrl || "/src/assets/images/default.jpg",
          isFavorite: false, // 預設為未加入最愛
        };
      });
      console.log("列表頁 API 原始數據:", JSON.stringify(response.data, null, 2));
      console.log("處理後的卡片數據:", cards.value);
    }
  } catch (error) {
    console.error("Error fetching events:", error);
  }
};

// 在元件掛載時請求數據
onMounted(fetchEvents);
//測試如果不行改回去上面的
//onMounted(() => {
  // fetchEvent();
  // const urlParams = new URLSearchParams(window.location.search);
  // const paymentStatus = urlParams.get('payment');
  // if (paymentStatus === 'success') {
  //   Swal.fire({
  //     icon: 'success',
  //     title: '購票成功',
  //     text: '感謝您的購票，請至個人信箱查看詳情',
  //     confirmButtonColor: '#6c757d'
  //   });
  // }
//});
</script>

<style scoped>
:root {
  --card-gap: 20px;
  --card-border-radius: 10px;
  /* 同時設定底部與右側陰影 */
  --card-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 4px 0 6px rgba(0, 0, 0, 0.1);
  --card-hover-shadow: 0 8px 12px rgba(0, 0, 0, 0.15), 8px 0 12px rgba(0, 0, 0, 0.15);
  --scroll-margin: 11vh;
}

.title {
  position: center;
  font-size: 2rem;
  font-weight: bold;
  /* margin-top: 20px; */
  margin-bottom: 20px;
  text-align: center;
}
.breadcrumb-wrapper{
  margin-left: 11%;
  margin-top: 0.5%;
}
.main-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  /* margin-top: 1%; */
  /* padding: 20px; */
  width: 100%;
  box-sizing: border-box;
}

/* 卡片容器 */
.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 0 auto;
  padding: 20px;
  justify-content: center;
  max-width: 1550px;
}
/* 單張卡片 */
.card {
  position: relative;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.15); /* 柔和的陰影 */
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  padding: 0;
}

.card:hover {
  box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

/* 卡片內圖片 */
.card img {
  width: 100%;
  height: 250px; /* 固定圖片高度 */
  object-fit: cover;
  display: block;
  border-radius: 8px; /* 讓圖片的圓角與卡片一致 */
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.2); /* 圍繞陰影 */
  transition: box-shadow 0.3s ease-in-out;
  margin-bottom: 3px;
}

.card img:hover {
  box-shadow: 4px 4px 15px rgba(0, 0, 0, 0.3);
}

/* 卡片內容 */
.card-body {
  padding: 16px;
}

/* RWD 版型 */
@media (min-width: 1440px) {
  .cards-container {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1024px) {
  .cards-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .cards-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .cards-container {
    grid-template-columns: 1fr;
  }
}

/* 「我的最愛」按鈕樣式 */
.btn-btn-secondary {
  color: #fff;
  width: 30px;
  height: 30px;
  border-radius: 40%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
  /* 絕對定位 */
  position: absolute;
  bottom: 10px;
  right: 10px;
  z-index: 2;
  background-color: #ccc; /* 預設灰色 */
}

.btn-btn-secondary.active {
  background-color: #f20707;
}

.btn-btn-secondary.active:hover {
  background-color: #d10606;
}

.section {
  scroll-margin-top: var(--scroll-margin);
  min-height: 30vh;
}

.filter-container {
  width: 1200px;
  max-width: 100%;
  padding: 0 20px;
  margin: 0 auto;
}

.breadcrumb {
  position: sticky;
  padding: 1rem 0;
  font-size: 1rem;
  color: #666;
  margin-bottom: 2rem;
}

.event-category {
  font-family: Kantumruy Pro;
  display: inline-block;
  border: 1px solid orange;
  background-color: orange;
  color: white;
  padding: 2px 5px;
  border-radius: 4px;
}

.cardDate-text {
  font-family: Kantumruy Pro;
  font-size: 0.8rem;
  color: #666;
}

.cardvenue-text {
  font-family: Kantumruy Pro;
  font-size: 0.8rem;
  color: #666;
}

.card-title {
  padding: auto;
  font-family: Kantumruy Pro;
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}
</style>
