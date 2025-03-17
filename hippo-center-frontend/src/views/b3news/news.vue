<template>
<div class="news-container">
    <div v-if="loading">加載中...</div>
    <div v-else-if="error">{{ error }}</div>
    <div v-else>
    <div v-if="newsList.length === 0">目前沒有新聞稿。</div>
    <div v-else>
        <div v-for="news in displayedNews" :key="news.newsId" class="news-item">
        <h3 class="news-title">{{ news.newsTitle }}</h3>
        <p class="news-content">{{ news.newsContent }}</p>
        <div class="news-meta">
            <span class="publish-date">發佈日期: {{ formatDate(news.publishDate) }}</span>
        </div>
        </div>
    </div>
    </div>
</div>
</template>

<script>
import axiosapi from '@/plugins/axios.js';

export default {
name: 'News',
data() {
    return {
    newsList: [],
    loading: true,
    error: null,
    categoryId: 1 // 設定為新聞稿的類別 ID
    };
},
computed: {
    displayedNews() {
    // 按發佈日期降序排列
    return this.newsList.sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate));
    }
},
mounted() {
    this.fetchNews();
},
methods: {
    async fetchNews() {
    try {
        // 根據類別 ID 取得新聞稿
        const response = await axiosapi.get(`/user/news/category/${this.categoryId}`);

        if (response.status === 204) { // 204 No Content
            this.newsList = [];
        } else {
            this.newsList = Array.isArray(response.data) ? response.data : [];
        }
    } catch (err) {
        this.error = "無法獲取新聞稿資料";
        console.error(err);
    } finally {
        this.loading = false;
    }
},
    formatDate(dateString) {
    if (!dateString) return "未知日期";
    const date = new Date(dateString);
    return date.toISOString().split("T")[0];
    }
}
};
</script>

<style scoped>
.news-container {
margin-bottom: 270px; /* 可以調整數值來改變間距 */
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
