<template>
  <div class="news-page">
    <!-- 左側選單 -->
    <news-sidebar />
    <!-- 右側動態內容 -->
    <div class="content">
      <router-view />
    </div>
  </div>
</template>

<script>
import NewsSidebar from '@/components/navbar/b3news/newsSidebar.vue';

export default {
  name: 'NewsMain',
  components: {
    NewsSidebar
  }
};
</script>

<style scoped>
.news-page {
  display: flex;
}

.content {
  flex-grow: 1; /* 讓內容區域填滿剩餘空間 */
  margin-left: calc(250px + 10% + 20px); /* 250px 是 sidebar 的寬度, 10% 是 margin-left */
  padding: 20px;
}
</style>