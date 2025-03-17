<template>
    <div class="sidebar">
      <h3>最新消息</h3>
      <ul class="sidebar-menu">
        <li v-for="item in menuItems" :key="item.id">
          <router-link :to="item.path" class="menu-item" :class="{ 'active-menu': item.path === $route.path }">
          {{ item.title }}</router-link>
        </li>
      </ul>
    </div>
</template>

<script>
export default {
  name: 'NewsSidebar',
  data() {
    return {
      menuItems: [
        // { id: 1, title: '總攬', path: '/b3news/newsoverview' },
        { id: 2, title: '新聞稿', path: '/b3news/news' },
        { id: 3, title: '展覽公告', path: '/b3news/exhibition' },
        // { id: 4, title: '館場公告', path: '/b3news/venue' },
        // { id: 5, title: '場租公告', path: '/b3news/rental' },
        // { id: 6, title: '招標公告', path: '/b3news/tender' },
        { id: 7, title: '其他公告', path: '/b3news/other' }
      ]
    };
  },
};
</script>

<style scoped>
/* @import '@/assets/fonts/font.css';
@import '@/assets/styles/sidebar.css'; */
/* @import '@/assets/styles/main.css'; */

/* 當前選中頁面的樣式 */
.active-menu {
color: #f8961e;

}

.sidebar {
position: relative;
width: 150px;

}
h3{
  font-size: 34px;

}
.sidebar-menu {
    list-style: none;
    padding: 0;
    margin-top: 10%;
    /* width: 180px; */


    border-radius: 10px;
    text-align: left;
}

.menu-item {
    padding: 10px 0 10px 0;
    display: block;
    font-family: NotoSansTC-VariableFont;
    color: #0c0c0c;
    text-decoration: none;
    border-radius: 10px;
    font-weight: 800;
    font-size: 26px;

    /* 加粗字體 */

}

.menu-item:hover {
    /* background-color: #f8961e; */
    color: #f8961e;
}


.venue-content {
    flex: 1;
    padding: 20px;
    min-height: calc(100vh - 100px);
}

.breadcrumb {
    padding-bottom: 0px;
    color: #0c0c0c;
    font-family: NotoSansTC-VariableFont;
    font-size: 18px;
    display: flex;
    /* margin-top: 10px; */
}


.current-page {
    font-weight: bold;
    color: #f8961e;
}
</style>