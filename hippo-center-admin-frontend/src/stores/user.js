import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

const useUserStore = defineStore('user', () => {
    const email = ref(localStorage.getItem('email') || '');  // 從 localStorage 讀取 email
    const token = ref(localStorage.getItem('authToken') || '');  // 從 localStorage 讀取 token
    const name = ref(localStorage.getItem('name') || '');  // 從 localStorage 讀取 name

    // 計算屬性：檢查是否已登入
    const isLogin = computed(() => {
        return !!(email.value && token.value && name.value); // 檢查 token 和 email 是否存在
    });

    // 設置 email
    function setEmail(value) {
        email.value = value;
        localStorage.setItem('email', value);  // 儲存至 localStorage
    }

    // 設置 token
    function setToken(value) {
        token.value = value;
        localStorage.setItem('authToken', value);  // 儲存至 localStorage
    }

    // 設置 name
    function setName(value) {
        name.value = value;
        localStorage.setItem('name', value);  // 儲存至 localStorage
    }

    // 清除用戶資料
    function clearUserInfo() {
        email.value = '';
        token.value = '';
        localStorage.removeItem('email');  // 從 localStorage 刪除
        localStorage.removeItem('authToken');  // 從 localStorage 刪除
        localStorage.removeItem('name');  // 從 localStorage 刪除
    }

    return {
        name,
        email,
        token,
        isLogin,
        setEmail,
        setToken,
        setName,
        clearUserInfo
    };
});

export default useUserStore;