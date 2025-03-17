<template>
    <div class="sidebar">
        <div class="breadcrumb">
            <span></span>
            <span></span>
            <span class="current-page"></span>
        </div>
        <ul class="sidebar-menu">
            <li v-for="item in menuItems" :key="item.id">
                <!-- 根據條件判斷，渲染不同的內容 -->
                <template v-if="item.title === '我的收藏'">
                    <!-- "我的收藏"選項使用<details>和<summary>來實現折疊效果 -->
                    <details>
                        <summary class="menu-item">{{ item.title }}</summary>
                        <!-- 展開後顯示的子選單 -->
                        <ul>
                            <li>
                                <router-link :to="item.subMenuItems[0].path" class="menu-item-child">
                                    {{ item.subMenuItems[0].title }}
                                </router-link>
                            </li>
                            <li>
                                <router-link :to="item.subMenuItems[1].path" class="menu-item-child">
                                    {{ item.subMenuItems[1].title }}
                                </router-link>
                            </li>
                        </ul>
                    </details>
                </template>
                <!-- 其他選單項目顯示普通的 router-link -->
                <template v-else>
                    <router-link :to="item.path" class="menu-item" active-class="active-menu-item">{{ item.title }}</router-link>
                </template>
            </li>
            <!-- 新增登出選項 -->
            <li @click="logout" class="menu-item">登出
            </li>
        </ul>
    </div>
</template>


<script>
import useUserStore from '@/stores/user.js';

export default {
    name: 'PersonSidebar',
    data() {
        return {
            menuItems: [
                { id: 1, title: '個人資料', path: '/secure/person' },
                { id: 2, title: '導覽預約', path: '/secure/tour' },
                { id: 3, title: '訂單資訊', path: '/secure/Orders' },
                { id: 4, title: '票券資訊', path: '/secure/ticket' },
                { id: 5, title: '我的收藏', path: '/secure/faq',
                    subMenuItems: [
                        { title: '商品收藏', path: '/secure/mylove/p' },
                        { title: '節目收藏', path: '/secure/mylove/e' }
                    ] },
            ]
        };
    },
    methods: {
        logout() {
            // 1. 清除 token (例如從 localStorage 或 Vuex / Pinia store 中刪除)
            localStorage.removeItem('authToken');
            
            // 2. 清除用戶資料 (這裡假設使用 Pinia 的 store)
            const userStore = useUserStore();
            userStore.clearUserInfo();  // 清除用戶資料
            
            // 3. 跳轉到登入頁面
            this.$router.push({ path: '/secure/login' });
        }
    }
};
</script>

<style scoped>
@import '@/assets/fonts/font.css';
@import '@/assets/styles/sidebar.css';
@import '@/assets/styles/main.css';

.menu-item-child {
    padding: 10px 0 10px 0;
    display: block;
    font-family: NotoSansTC-VariableFont;
    color: #0c0c0c;
    text-decoration: none;
    border-radius: 10px;
    font-weight: 800;
    font-size: 20px;

    /* 加粗字體 */

}

.menu-item-child:hover {
    /* background-color: #f8961e; */
    color: #f8961e;
}

/* 隱藏預設的圖示 */
summary::-webkit-details-marker {
    display: none;
}

/* 使用 flexbox 排列內容，使得箭頭移到文字右邊 */
summary {
    display: flex;
    justify-content: flex-start;  /* 文字對齊左邊 */
    align-items: center;          /* 文字和圖示垂直置中 */
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
}

/* 自訂圖示，預設為向右箭頭，並將其移動到右邊 */
summary::after {
    content: '◀';  /* 預設使用向箭頭符號 */
    font-size: 18px;
    margin-left: 10px;  /* 文字和箭頭之間的距離 */
}

/* 當 details 開啟時，顯示向下箭頭 */
    details[open] summary::after {
    content: '▼';  /* 開啟時顯示向下箭頭 */
}

ul li { 
    padding-left: 0px; /* 設定 ul 的左邊距離（縮排） */
    list-style: none;
}

/* 這個類會被應用到當前活動的路由項目 */
.active-menu-item {
    color: #f8961e;  /* 更改背景顏色 */
}

li{
    cursor: pointer;
}
</style>