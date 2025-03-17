<template>
  <div class="overview-container">
    <div v-if="loading">加載中...</div>
    <div v-else-if="error">{{ error }}</div>
    <div v-else>
      <div v-for="news in displayedNews" :key="news.newsId" class="news-item">
      <h3 class="news-title">{{ news.newsTitle }}</h3>
      <p class="news-content">{{ news.newsContent }}</p>
      <div class="news-meta">
        <span class="publish-date">發佈日期:{{ formatDate(news.publishDate) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axiosapi from '@/plugins/axios.js';

export default {
  name: 'NewsOverview',
  data(){
    return{
      newsList: [], // 存放公告
      loading: true,
      error: null,
    };
  },
  computed:{
    displayedNews(){
      // 只顯示上架的公告
      return this.newsList
        .filter(news => news.newsStatus === "上架")
        .sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate));
    }
  },
  mounted(){
    this.fetchNews();
  },
  methods:{
    async fetchNews() {
    try {
        const response = await axiosapi.get('/user/news'); // 確保從後端獲取數據
        console.log("後端返回的消息:", response.data); // ✅ 確保後端數據正確
        this.newsList = response.data;
    } catch (err) {
        this.error = "無法獲取公告資料";
        console.error(err);
    } finally {
        this.loading = false;
    }
},
    formatDate(dateString){
      // 格式化日期(YYYY-MM-DD)
      if (!dateString) return "未知日期";
      const date = new Date(dateString);
      return date.toISOString().split("T")[0]; // 轉換為 YYYY-MM-DD 格式  
    }
  }
};
</script>

<style scoped>
.overview-container {
padding-top: 70px;
}

.news-item {
max-width: 1250px;
padding: 15px;
border-bottom: 1px solid #ddd;
margin-bottom: 10px;
}

.news-content {
font-size: 20px;
margin: 10px 0;
}

.news-meta {
font-size: 16px;
color: gray;
}

.publish-date {
color: #f8961e;
}
</style>