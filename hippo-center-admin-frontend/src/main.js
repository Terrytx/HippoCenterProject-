import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import '@fortawesome/fontawesome-free/css/all.css'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import useUserStore from './stores/user.js';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate)

createApp(App)
    .use(pinia)
    .use(router)
    .use(ElementPlus)
    .mount('#app')


const userStore = useUserStore();
const savedToken = localStorage.getItem('authToken');
const savedEmail = localStorage.getItem('email');
if (savedToken && savedEmail) {
  userStore.setEmail(savedEmail);
  userStore.setToken(savedToken);
}