<template>
    <div class="sidebar">
      <div class="breadcrumb">
        <span>最新消息</span>
        <span v-show="hasMatchingPage">＞</span>
        <span v-if="hasMatchingPage" class="current-page">{{ currentPage }}</span>
      </div>
      <ul class="sidebar-menu">
        <li v-for="item in menuItems" :key="item.id">
          <router-link :to="item.path" class="menu-item" :class="{ 'active-menu': item.path === $route.path }">
          {{ item.title }}</router-link>
        </li>
      </ul>
    </div>
</template>

<script>
export default {
  name: 'NewsSidebar',
  data() {
    return {
      menuItems: [
        { id: 1, title: '總攬', path: '/b3news/newsoverview' },
        { id: 2, title: '新聞稿', path: '/b3news/news' },
        { id: 3, title: '展覽公告', path: '/b3news/exhibition' },
        { id: 4, title: '館場公告', path: '/b3news/venue' },
        { id: 5, title: '場租公告', path: '/b3news/rental' },
        { id: 6, title: '招標公告', path: '/b3news/tender' },
        { id: 7, title: '其他公告', path: '/b3news/other' }
      ]
    };
  },
  computed: {
    hasMatchingPage() {
      return this.menuItems.some(item => item.path === this.$route.path);
    },
    currentPage() {
      const item = this.menuItems.find(item => item.path === this.$route.path);
      return item ? item.title : '最新消息';
    }
  }
};
</script>

<style scoped>
@import '@/assets/fonts/font.css';
@import '@/assets/styles/sidebar.css';
@import '@/assets/styles/main.css';

/* 當前選中頁面的樣式 */
.active-menu {
color: #f8961e;
}
</style>