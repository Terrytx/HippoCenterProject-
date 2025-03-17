<template>
    <div class="venue-container">
        <!-- 頂部導航欄 -->
        <nav class="top">
            <nav class="nav-tabs">
                <router-link 
                v-for="route in routes" 
                :key="route.path"
                :to="route.path"
                class="nav-tab"
                :class="{ active: currentPath === route.path }"
                >
                <i :class="[route.icon, 'nav-icon', { active: currentPath === route.path }]"></i>
                <span>{{ route.name }}</span>
                </router-link>
            </nav>
        </nav>
            <router-view></router-view>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'


const route = useRoute()
const router = useRouter()

// 路由配置，注意路徑要加上 /venue 前綴
const routes = [
    { 
        path: '/venue/v1-rental-venue', 
        name: '場地管理',
        icon: 'fas fa-building'
    },
    { 
        path: '/venue/v2-rental', 
        name: '檔期管理',
        icon: 'fas fa-calendar-alt'
    },
    { 
        path: '/venue/v3-rental', 
        name: '租借申請',
        icon: 'fas fa-file-contract'
    }
]

// 當前路徑
const currentPath = computed(() => route.path)
// 在組件掛載時導航到租借申請頁面
onMounted(() => {
    router.push('/venue/v3-rental')
})
</script>



<style scoped>
:root {
--primary-color: #4A6741;
--gray-light: #f8f9fa;
--gray: #6c757d;
}

.venue-container {
/* padding: 20px; */
max-width: 100%;
margin: 0 auto;

}
.top{
    display: flex;
    min-height:8vh;
    width: 100%;
    top: 0;
    position: fixed; /* 固定定位 */
    z-index: 800;
    padding-top: 1.5%;
    background-color: #F5F7F4;
    padding-bottom: 3.5%;
    border-radius:  8px;

    /* box-shadow: 0 0px 1px rgba(0,0,0,0.1); */

}
/* 導航樣式 */
.nav-tabs {
display: flex;
gap: 10px;
/* margin-bottom: 20px; */
background: white;
padding: 10px;
border-radius:  8px;
box-shadow: 0 2px 4px rgba(0,0,0,0.1);
position: fixed; /* 固定定位 */
z-index: 1000;

}

.nav-tab {
display: flex;
align-items: center;
gap: 8px;
text-decoration: none;
color: var(--gray);
padding: 12px 20px;
border-radius: 6px;
transition: all 0.3s ease;
}

.nav-tab:hover {
background: var(--gray-light);
}

.nav-tab.active {
background: rgba(74, 103, 65, 0.1);
color: var(--primary-color);
}

.nav-icon {
font-size: 1.2rem;
width: 1.5rem;
text-align: center;
}

.nav-icon.active {
color: var(--primary-color);
}


</style>