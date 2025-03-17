<template>
    <aside class="sidebar">
        <div class="sidebar-header">
            <div class="avatar">
            <!-- <img src="/api/placeholder/50/50" alt="用戶頭像"> -->
            </div>
            <div class="user-info">
                <h3>{{ userStore.name }}</h3>
                <p>系統管理員</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <router-link v-for="item in menuItems" 
            :key="item.path"
            :to="item.path"
            class="nav-item"
            active-class="active">
            <i :class="item.icon"></i>
            {{ item.title }}
            </router-link>
            <!-- 新增登出選項 -->
            <button class="nav-item logout-btn" @click="logout">
                <i class="fas fa-sign-out-alt"></i> 登出
            </button>
        </nav>
    </aside>
</template>

<script>
import axiosapi from '../../plugins/axios.js';  // 引入配置過的 axios 實例
import useUserStore from '../../stores/user.js';

export default {
    name: 'AdminLayout',
    data() {
        return {
            menuItems: [
                { path: '/navigation', title: '導覽管理', icon: 'fas fa-compass' },
                { path: '/event', title: '節目管理', icon: 'fas fa-tv' },
                { path: '/venue', title: '租借管理', icon: 'fas fa-key' },
                { path: '/shop', title: '商店管理', icon: 'fas fa-store' },
                { path: '/members', title: '會員管理', icon: 'fas fa-users' },
                { path: '/news', title: '最新消息', icon: 'fas fa-newspaper' },
                { path: '/faq', title: '常見問題', icon: 'fas fa-question-circle' }
            ]
        } 
    },
    computed: {
        // 使用 Pinia Store 中的 name 屬性來顯示管理員名稱
        userStore() {
            const store = useUserStore();
            console.log(store.name); // 打印 name 确认值
            return store;
        }
    },
    methods: {
        logout() {
            // 清除 localStorage 或 sessionStorage
            localStorage.removeItem("authToken");
            localStorage.removeItem("email");
            localStorage.removeItem("name");

            // 清除 axios 預設的授權標頭
            axiosapi.defaults.headers.authorization = "";

            // 跳轉回登入頁面
            this.$router.push({ path: '/login' });
        }
    }
}
</script>

<style scoped>
:root {
    --primary-color: #4A6741;
    --secondary-color: #8AA682;
    --hover-color: #C2D5BB;
    --background-color: #F5F7F4;
    --text-primary: #2C3E2D;
    --text-secondary: #5C715E;
    --border-color: #D8E0D5;
}

.sidebar {
    top: 0;
    position: flex;
    height: 100%;
    width: 250px;
    background-color: #fff;
    border-right: 1px solid var(--border-color);
    padding: 1.5rem;
}

.sidebar-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid var(--border-color);
}

.avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background-color: var(--hover-color);
}

.user-info h3 {
    margin: 0;
    font-size: 1rem;
}

.user-info p {
    margin: 0;
    color: var(--text-secondary);
    font-size: 0.875rem;
}

.sidebar-nav {
    margin-top: 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.nav-item {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.75rem;
    text-decoration: none;
    color: var(--text-primary);
    border-radius: 0.5rem;
    transition: all 0.3s ease;
}

.nav-item:hover {
    background-color: var(--hover-color);
    color: var(--primary-color);
}

.nav-item.active {
    background-color: var(--primary-color);
    color: white;
}

.logout-btn {
    background: none;
    border: none;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.75rem;
    color: var(--text-primary);
    border-radius: 0.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 1rem;
}

.logout-btn:hover {
    background-color: var(--hover-color);
    color: var(--primary-color);
}

.main-content {
    flex: 1;
    padding: 2rem;
}

</style>