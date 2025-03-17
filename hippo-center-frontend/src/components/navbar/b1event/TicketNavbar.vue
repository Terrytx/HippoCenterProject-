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
 name: 'TicketNavbar',
 data() {
     return {
         menuItems: [
             { id: 1, title: '購票資訊', path: '/venue/eventservice/' },
             { id: 2, title: '節目介紹', path: '/venue/eventservice/customer' },
             { id: 3, title: '折扣方案', path: '/venue/eventservice/performance' },
             { id: 4, title: '重要須知', path: '/venue/eventservice/lost-found' }
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
