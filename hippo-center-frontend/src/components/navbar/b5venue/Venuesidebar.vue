<template>
    <div class="sidebar">
        <div class="breadcrumb">
            <span>館場服務</span>
            <span v-show="hasMatchingPage">＞</span>
            <span v-if="hasMatchingPage" class="current-page">{{ currentPage }}</span>
        </div>
        <ul class="sidebar-menu">
            <!-- <li v-for="item in menuItems" :key="item.id">
                <a :href="item.path" class="menu-item" @click="scrollToSection(item.path)">{{ item.title }}
                </a>
            </li> -->
            <li v-for="item in menuItems" :key="item.id">
                <router-link 
                    v-if="item.path.startsWith('/')" 
                    :to="item.path" 
                    class="menu-item"
                    @click="handleRouteClick(item.path)"
                >
                    {{ item.title }}
                </router-link>
                <a 
                    v-else 
                    :href="item.path" 
                    class="menu-item" 
                    @click.prevent="scrollToSection(item.path)"
                >
                    {{ item.title }}
                </a>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter  } from 'vue-router'

const route = useRoute()
const router = useRouter()
const menuItems = ref([
    // { id: 1, title: '場租公告', path: '#news' },
    // { id: 2, title: '租借流程', path: '#process' },
    { id: 3, title: '場地介紹', path: '#introduction' },
    { id: 4, title: '檔期查詢', path: '#schedule' },
    // { id: 5, title: '常見問題', path: '#faq' },
    { id: 6, title: '線上申請', path: '/venue/booking' }
])


const hasMatchingPage = computed(() => {
    return menuItems.value.some(item => 
        item.path === '#' + route.hash.slice(1) || 
        item.path === route.path
    )
})

const currentPage = computed(() => {
    const item = menuItems.value.find(item => 
        item.path === '#' + route.hash.slice(1) ||
        item.path === route.path
    )
    return item ? item.title : ''
})
const scrollToSection = (path) => {
    if (path.startsWith('#')) {
        // 如果在 booking 頁面，先返回主頁面
        if (route.path.includes('booking')) {
            router.push('/venue' + path)
            return
        }
        const element = document.querySelector(path)
        if (element) {
            element.scrollIntoView({ 
                behavior: 'auto',
                block: 'start'
            })
        }
    }
}
const handleRouteClick = async (path) => {
    if (path.startsWith('/')) {
        await router.push(path)
        window.scrollTo({
            top: 0,
            behavior: 'auto'
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