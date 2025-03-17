import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router/router'
import axios from 'axios'
import '@fortawesome/fontawesome-free/css/all.css'
import App from './App.vue'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import useUserStore from '@/stores/user.js';
import PrimeVue from 'primevue/config'


axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.withCredentials = true

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate)

// 在應用啟動時讀取 localStorage 並設置到 store


createApp(App).use(pinia).use(router).use(PrimeVue).mount('#app')

const userStore = useUserStore();
const savedToken = localStorage.getItem('authToken');
const savedEmail = localStorage.getItem('email');
const savedMemberId = localStorage.getItem('memberId');
if (savedToken && savedEmail) {
  userStore.setEmail(savedEmail);
  userStore.setToken(savedToken);
  // 僅當 localStorage 中存在 memberId 時才設置 userStore
  if (savedMemberId) {
    userStore.setMemberId(savedMemberId);
  }
  // 設置 axios 預設授權標頭
  axios.defaults.headers.authorization = "Bearer " + savedToken;
}