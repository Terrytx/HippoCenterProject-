<template>
    <div class="sidebar">
        <div class="breadcrumb">
            <span>線上商城</span>
            <span v-show="hasMatchingPage">＞</span>
            <span v-if="hasMatchingPage" class="current-page">{{ currentPage }}</span>
        </div>
        <ul class="sidebar-menu">
            <li v-for="item in menuItems" :key="item.id">
                <a :href="item.path" class="menu-item" @click="selectCategory(item.title)">{{ item.title }}
                </a>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
// 定义 emit 事件
const emit = defineEmits(['category-selected']);

const route = useRoute()
const menuItems = ref([
    { id: 1, title: '所有商品', path: '#All' },
    { id: 2, title: '風格文具', path: '#stationeries' },
    { id: 3, title: '設計配件', path: '#accessories' },
    { id: 4, title: '經典藏書', path: '#books' },
    { id: 5, title: '典藏精品', path: '#boutiques' },
    { id: 6, title: '意象擺飾', path: '#decrations' },
    { id: 7, title: '山水書畫', path: '#paintings' }

])


// 計算屬性
const hasMatchingPage = computed(() => {
    return menuItems.value.some(item => item.path === '#' + route.hash.slice(1))
})

const currentPage = computed(() => {
    const item = menuItems.value.find(item => item.path === '#' + route.hash.slice(1))
    return item ? item.title : ''
})

const selectCategory = (categoryName) => {
    console.log('触发分类选择事件:', categoryName); // 確認是否執行
  emit('category-selected', categoryName); // 发送分类名称到父组件
};
// const scrollToSection = (path) => {
//     const element = document.querySelector(path)
//     if (element) {
//         element.scrollIntoView({
//             top: offsetPosition,
//             behavior: 'smooth',
//             block: 'start'
//         })
//     }
// }    
</script>
<style scoped>
@import '@/assets/fonts/font.css';
@import '@/assets/styles/sidebar.css';
@import '@/assets/styles/main.css';

.venue-link {
    text-decoration: none;
    /* 移除預設的底線 */
    color: inherit;
    /* 繼承原本的顏色 */
    /* cursor: pointer;       滑鼠變成指標 */

}
</style>