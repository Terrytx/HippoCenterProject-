<template>
  <header>
    <nav>
      <ol class="menu">
        <div class="logo">
          <router-link to="/"><img class="logoP" :src="logoSrc" alt="logo">
          </router-link>
          <div class="logoN">HIPPO ARTs CENTER　　　　荷馬市藝術文化中心</div>
        </div>
        <li v-for="menu in menus" :key="menu.id"
          @mouseover="menu.id === 'm7' ? handleMemberCenterMouseOver(menu) : handleMouseOver(menu)"
          @mouseout="menu.id === 'm7' ? handleMemberCenterMouseOut(menu) : handleMouseOut(menu)">
          <!-- 會員中心使用專屬處理方法 -->
          <router-link :to="menu.link" :class="{ 'active-link': isCurrentPath(menu.link) && !isHovered }">
            {{ menu.title }}
          </router-link>
          <ul v-show="menu.isShow && menu.subitems?.length" class="submenu">
            <li v-for="subitem in menu.subitems" :key="subitem.id">
                <!-- 僅為線上商城的子選單新增點擊事件
                <a v-if="menu.id === 'm5'" href="javascript:void(0)" @click="$emit('category-selected', subitem.cont)">
                {{ subitem.cont }}
              </a> -->
              <router-link v-if="subitem.link" :to="subitem.link">{{ subitem.cont }}</router-link>
              <span class="member" v-else @click="logout">{{ subitem.cont }}</span> <!-- 登出處理 -->
            </li>
          </ul>
        </li>
        <span>
          <router-link to="/secure/person">
            <i class="fa-solid fa-user"></i>
          </router-link>
        </span>
        <span>
          <router-link to="/cart">
            <i class="fa-solid fa-cart-shopping"></i>
          </router-link>
        </span>
        <span class="sp3">
          <router-link to="/calendar">
            <i class="fa-regular fa-calendar-days"></i>
          </router-link>
        </span>
      </ol>
    </nav>
  </header>

</template>

<script>
import hippoLogo from '/src/assets/images/hippo.png'
import axiosapi from '@/plugins/axios.js'; // 引入 axiosapi
import useUserStore from '@/stores/user'; // 引入 Pinia 用戶狀態管理



export default {
  name: 'Header',
  data() {
    return {
      logoSrc: hippoLogo,
      isHovered: false,  // 新增狀態來追踪是否有選單被hover 
      menus: [
        {
          id: 'm1',
          title: '　參觀　',
          link: '#',
          isShow: false,
          subitems: [
            // { id: 1, cont: '計畫行程', link: '#' },
            // { id: 2, cont: '參訪須知', link: '#' },
            // { id: 3, cont: '館場資訊', link: '#' },
            { id: 4, cont: '導覽服務', link: '/b3tours' },
            // { id: 5, cont: '問卷調查', link: '#' },
          ]
        },
        {
          id: 'm2',
          title: '關於我們',
          link: '#',
          isShow: false,
          subitems: [
            // { id: 1, cont: '關於荷馬市', link: '#' },
            { id: 2, cont: '最新消息', link: '/b3news' },
            // { id: 3, cont: '人才招募', link: '#' },
            // { id: 4, cont: '支持荷馬市', link: '#' },
            // { id: 5, cont: '聯絡我們', link: '#' },
          ]
        },
      {
        id: 'm3',
        title: '節目總覽',
        link: '/event#introduction', // 改為帶錨點的路徑
        isShow: false,
        subitems: [
          { id: 1, cont: '最新節目', link: '/event/AllEvent' },//AllEvent
          { id: 2, cont: '節目回顧', link: '/event/EventReview' },//limitedtimeEvent
          { id: 3, cont: '節目預告', link: '/event/EventUpcoming' },//UpComingEvent
          // { id: 4, cont: '購票資訊', link: '/event/TicketInfo' },//TicketInfo
        ]
      },
        {
          id: 'm4',
          title: '館場租借',
          link: '/venue#introduction', // 改為帶錨點的路徑
          isShow: false,
          subitems: [
            // { id: 1, cont: '場地介紹', link: '/venue#introduction' },         
            // { id: 2, cont: '檔期行事曆', link: '/venue#schedule' },         
            { id: 3, cont: '租借申請', link: '/venue/booking' },  
          ]
        },
        {
          id: 'm5',
          title: '線上商城',
          isShow: false,
          link: '/shopcart#introduction',
          subitems: [
            { id: 1, cont: '所有商品', link: '/shopcart#introduction' },
            // { id: 2, cont: '風格文具', link: '#' },
            // { id: 3, cont: '設計配件', link: '#' },
            // { id: 4, cont: '經典藏書', link: '#' },
            // { id: 5, cont: '典藏精品', link: '#' },
            // { id: 6, cont: '意象擺飾', link: '#' },
            // { id: 7, cont: '山水書畫', link: '#' },
          ]
        },
        {
          id: 'm6',
          title: '常見問題',
          link: '#',
          isShow: false,
          subitems: [
            { id: 1, cont: '參觀問題', link: '#' },
            { id: 2, cont: '導覽問題', link: '#' },
            { id: 3, cont: '會員問題', link: '#' },
            { id: 4, cont: '購票問題', link: '#' },
            { id: 5, cont: '其他問題', link: '#' },
          ]
        },
        {
          id: 'm7',
          title: '會員中心',
          link: '/secure/person',
          isShow: false,
          subitems: [],
          // 會員中心選單特別處理
          handleMouseOver: this.handleMemberCenterMouseOver,
          handleMouseOut: this.handleMemberCenterMouseOut,
        }
      ]
    }
  },
  computed: {
    // 計算用戶是否已經登入
    isLoggedIn() {
      return !!localStorage.getItem('authToken'); // 檢查是否有 authToken
    }
  },
  methods: {
    isCurrentPath(link) {
      // 移除錨點部分進行比較
      const currentPath = this.$route.path
      const linkPath = link.split('#')[0]
      // 主選單路徑比對
      if (linkPath === '#') {
        return false // 如果是 # 號則不顯示底線
      }
      // 判斷是否處於需要顯示橘色底線的路徑中
      const highlightPaths = [
        '/secure/login',
        '/secure/register',
        '/secure/sent',
        '/secure/person',
        '/secure/tour',
        '/secure/Orders',
        '/secure/ticket',
        '/secure/mylove/p',
        '/secure/mylove/e'
      ];
    if (highlightPaths.includes(currentPath)) {
      return linkPath === '/secure/person'; // 只對會員中心頁面顯示底線
    }
      return currentPath.startsWith(linkPath) && linkPath !== ''
    },
    handleMouseOver(menu) {
      menu.isShow = true
      this.isHovered = true  // 當滑鼠移入時設置為true
    },
    handleMouseOut(menu) {
      menu.isShow = false
      this.isHovered = false  // 當滑鼠移出時設置為false
    },

    // 當滑鼠移入會員中心選單時，顯示下拉菜單
    handleMemberCenterMouseOver(menu) {
      if (this.isLoggedIn) {
        menu.isShow = true
        // 動態設定登出選項
        menu.subitems = [{ id: 1, cont: '登出', link: '' }]
      }
      this.isHovered = true
    },

    // 當滑鼠移出會員中心選單時，隱藏下拉菜單
    handleMemberCenterMouseOut(menu) {
      menu.isShow = false
      this.isHovered = false
    },

    // 點擊會員中心處理
    handleMemberCenter() {
      if (!this.isLoggedIn) {
        // 未登入，跳轉到登入頁面
        this.$router.push('/secure/login')
      } else {
        // 已登入，跳轉到個人資料頁面
        this.$router.push('/secure/person')
      }
    },

    // 登出邏輯
    logout() {
      axiosapi.defaults.headers.authorization = "";

      // 2. 使用 Pinia store 清除用戶資料
      const userStore = useUserStore();
      userStore.clearUserInfo();  // 清除用戶資料

      // 3. 跳轉到登入頁面
      this.$router.push({ path: '/secure/login' });
    }
  }
}
</script>

<style scoped>
@import '@/assets/fonts/font.css';
@import '@/assets/styles/header.css';

.member {
  height: auto;
  padding: 0;
  display: block;

}

.member {
  text-align: center;
  display: block;
  padding: 8px 20px;
  color: black;
  text-decoration: none;
  white-space: nowrap;
  border-radius: 5px;
  background-color: #ffffff;
  width: 140px;


}

.member:hover {

  background-color: #f8961e;
  cursor: pointer;
}
</style>