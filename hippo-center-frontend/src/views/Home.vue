<template>
<div class="home">
    <!-- 其他現有的首頁內容 -->
    
    <div class="carousel">
    <ImagesCarousel />
    </div>
    <div class="news-page">
    <!-- 左側選單 -->
        <div class="side"></div>
        <news-sidebar />
        <!-- 右側動態內容 -->
        <div class="content">
        <News />
        </div>
    </div>
</div>
</template>

<script setup>
// 引入輪播圖組件
import ImagesCarousel from '@/views/front/ImagesCarousel.vue'
import News from './front/news.vue';
import NewsSidebar from '../components/navbar/front/newsSidebar.vue';
</script>

<style scoped>
.home {
min-height: 100vh;
}
.carousel{
    padding-top: 1%;
}
.news-page {
    display: flex;
    box-shadow: 1px 1px 1px gray;
    margin-top: 5%;
}
.side{
    position: relative;
    margin-left: 5%;
    width: 10%;
    
}
.content {

    flex-grow: 1; 
    /* padding: 20px; */
    
}
</style>