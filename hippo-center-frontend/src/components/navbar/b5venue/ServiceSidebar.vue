<template>
    <div class="sidebar">
        <div class="breadcrumb">
            <span>觀眾服務</span>
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
                { id: 1, title: '服務中心', path: '/venue/service/center' },
                { id: 2, title: '顧客服務', path: '/venue/service/customer' },
                { id: 3, title: '演出服務', path: '/venue/service/performance' },
                { id: 4, title: '失物招領', path: '/venue/service/lost-found' }
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
