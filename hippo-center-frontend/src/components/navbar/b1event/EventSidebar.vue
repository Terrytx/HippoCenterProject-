<template>
    <div class="sidebar">
        <div class="breadcrumb">
            <span>展演查詢</span>
            <span v-show="hasMatchingPage">＞</span>
            <span v-if="hasMatchingPage" class="current-page">{{ currentPage }}</span>
        </div>
        <ul class="sidebar-menu">
            <li v-for="item in menuItems" :key="item.id">
                <a :href="item.path" class="menu-item" @click="scrollToSection(item.path)">{{ item.title }}
                </a>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const menuItems = ref([
    { id: 1, title: '活動總覽', path: '#Allevents' },
    { id: 2, title: '當期展覽', path: '#permanent exhibition' },
    { id: 3, title: '活動回顧', path: '#limitedtime exhibition'},
    { id: 4, title: '展覽預告', path: '#activity' },
    { id: 5, title: '購票資訊', path: '#TicketInfo' },

])


// 計算屬性
const hasMatchingPage = computed(() => {
    return menuItems.value.some(item => item.path === '#' + route.hash.slice(1))
})

const currentPage = computed(() => {
    const item = menuItems.value.find(item => item.path === '#' + route.hash.slice(1))
    return item ? item.title : ''
})


const scrollToSection = (path) => {
    const element = document.querySelector(path)
        if (element) {
            element.scrollIntoView({ 
                top: offsetPosition,
                behavior: 'smooth',
                block: 'start'
            })
        }
    }    
</script>
<style scoped>
@import '@/assets/fonts/font.css';
@import '@/assets/styles/sidebar.css';
@import '@/assets/styles/main.css';

.venue-link {
    text-decoration: none;  /* 移除預設的底線 */
    color: inherit;        /* 繼承原本的顏色 */
    /* cursor: pointer;       滑鼠變成指標 */
 
}


</style>