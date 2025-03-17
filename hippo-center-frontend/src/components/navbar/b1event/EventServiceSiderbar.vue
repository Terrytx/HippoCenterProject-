<template>
     <div class="sidebar">
      <div class="breadcrumb">
          <span>展覽查詢</span>
          <span v-show="hasMatchingPage">＞</span>
          <span v-if="hasMatchingPage" class="current-page">{{ currentPage }}</span>
      </div>
      <ul class="sidebar-menu">
          <li v-for="item in menuItems" :key="item.id">
              <router-link :to="item.path" class="menu-item">{{ item.title }}</router-link>
          </li>
      </ul>
  </div>
</template>

<script>
export default {
  name: 'ServiceSidebar',
  data() {
      return {
          menuItems: [
              { id: 1, title: '依展覽類型分類', path: '/event/eventservice/Type' },
              { id: 2, title: '依照展場分類', path: '/event/eventservice/Venue' },
              { id: 3, title: '依照日期分類', path: '/event/eventservice/Date' },
              { id: 4, title: '展覽行事曆', path: '/ebent/eventservice/Calender' }
          ]
      }
  },
  computed: {
      hasMatchingPage() {
          return this.menuItems.some(item => item.path === this.$route.path)
      },
      currentPage() {
          const item = this.menuItems.find(item => item.path === this.$route.path)
          return item ? item.title : ''
      }
  }
}
</script>


<style>
@import '@/assets/fonts/font.css';
@import '@/assets/styles/sidebar.css';
@import '@/assets/styles/main.css';
</style>
