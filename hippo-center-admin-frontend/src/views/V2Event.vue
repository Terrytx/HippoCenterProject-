<template>
  <div class="venue-container">
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
// 路由配置，注意路徑要加上 /event 前綴
const routes = [
  { 
      path: '/event/event-management',  // 改為與router配置一致
      name: '節目管理',
      icon: 'fas fa-building'
  },
  { 
      path: '/event/event-publish-status', // 改為與router配置一致
      name: '發布管理',
      icon: 'fas fa-calendar-alt'
  },
  { 
      path: '/event/ticket-management', // 改為與router配置一致
      name: '門票管理',
      icon: 'fas fa-file-invoice-dollar'
  },
  { 
      path: '/event/dash-board', // 改為與router配置一致
      name: '來訪者流量統計表',
      icon: 'fas fa-file-contract'
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

.venue-container {
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