<template>
    <div class="topbar">
      <div class="breadcrumb">
        <span>{{ mainTitle }}</span>
        <span v-show="hasSubPage">＞</span>
        <span v-if="hasSubPage" class="current-page">{{ currentPage }}</span>
      </div>
      <div class="topbar-actions">
        <slot name="actions"></slot>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue'
  import { useRoute } from 'vue-router'
  
  const props = defineProps({
    mainTitle: {
      type: String,
      default: '展演查詢'
    },
    pageItems: {
      type: Array,
      default: () => []
    }
  })
  
  const route = useRoute()
  
  // 計算屬性
  const hasSubPage = computed(() => {
    return props.pageItems.some(item => item.path === '#' + route.hash.slice(1))
  })
  
  const currentPage = computed(() => {
    const item = props.pageItems.find(item => item.path === '#' + route.hash.slice(1))
    return item ? item.title : ''
  })
  </script>
  
  <style scoped>
  .topbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 20px;
    background-color: #f8f9fa;
    height: 50px;
    width: 100%;
    font-family: 'CustomFont', Arial, sans-serif;
  }
  
  .breadcrumb {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .breadcrumb span {
    color: #333;
  }
  
  .current-page {
    color: #007bff;
  }
  
  .topbar-actions {
    display: flex;
    align-items: center;
    gap: 15px;
  }
  </style>