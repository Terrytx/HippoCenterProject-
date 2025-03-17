<template>
    <div class="admin-container">
        <!-- 頂部導航欄 -->
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
        <router-view></router-view>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 路由配置，對應購物車相關管理功能
const routes = [
    { 
        path: '/shop/products', 
        name: '產品管理',
        icon: 'fas fa-box'
    },
    { 
        path: '/shop/promotions', 
        name: '促銷券管理',
        icon: 'fas fa-ticket-alt'
    },
    { 
        path: '/shop/orders', 
        name: '訂單管理',
        icon: 'fas fa-file-invoice-dollar'
    },
    { 
        path: '/shop/carts', 
        name: '購物車管理',
        icon: 'fas fa-shopping-cart'
    },
    { 
        path: '/shop/reports', 
        name: '報表分析',
        icon: 'fas fa-chart-line'
    }
]

// 當前路徑
const currentPath = computed(() => route.path)
</script>

<style scoped>
:root {
--primary-color: #4A6741;
--gray-light: #f8f9fa;
--gray: #6c757d;
}

.admin-container {
padding: 20px;
max-width: 1200px;
margin: 0 auto;
}

/* 導航樣式 */
.nav-tabs {
display: flex;
gap: 10px;
margin-bottom: 30px;
background: white;
padding: 10px;
border-radius: 8px;
box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
