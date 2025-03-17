import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
import axiosapi from '@/plugins/axios.js'; // 引入配置過的 axios 實例



const useUserStore = defineStore('user', () => {
    const account = ref(localStorage.getItem('account') || '');  // 從 localStorage 讀取 account
    const token = ref(localStorage.getItem('authToken') || '');  // 從 localStorage 讀取 token
    const memberId = ref(localStorage.getItem('memberId') || null);
    const errorMessage = ref('');  // 用來儲存錯誤訊息


    // 計算屬性：檢查是否已登入
    const isLogin = computed(() => {
        console.log(`account${account.value}`)
        console.log(`token${token.value}`)
        console.log(`memberId${memberId.value}`)
        return !!(account.value && token.value); // 檢查 token 和 account 是否存在
    });

    // 設置 account
    function setAccount(value) {
        account.value = value;
        localStorage.setItem('account', value);  // 儲存至 localStorage
    }

    // 設置 token 並解析出 memberId
    function setToken(value) {
        token.value = value;
        localStorage.setItem('authToken', value);  // 儲存至 localStorage
		axiosapi.defaults.headers['Authorization'] = `Bearer ${value}`; // 設置 axios 預設 Authorization 標頭
        parseToken(`Bearer ${value}`);  // 解析 token 並設置 memberId
    }

    // 設置 memberId
    function setMemberId(value) {
        console.log(value)
        memberId.value = value;
        localStorage.setItem('memberId', value);  // 儲存至 localStorage
    }

    function parseToken(tokenValue) {
        try {
            if (!tokenValue) {
                console.log('Token 為空');
                errorMessage.value = 'Token 為空';
                return;
            }

            // 確保 Token 是以 "Bearer " 開頭
            if (!tokenValue.startsWith('Bearer ')) {
                console.log(tokenValue + 'Authorization 標頭錯誤或缺少 Token');
                errorMessage.value = 'Authorization 標頭錯誤或缺少 Token';
                return;
            }

            // 移除 "Bearer " 前綴
            const jwtToken = tokenValue.substring(7);
            console.log(`jwtToken: ${jwtToken}`);

            // 使用 jwt-decode 解析 Token
            const decoded = jwtDecode(jwtToken);
            console.log('decoded:', decoded);  // 打印出解碼後的結果

            // `sub` 是一個包含 JSON 字串的屬性，我們需要解析它
            const sub = JSON.parse(decoded.sub); // 解析 sub 屬性
            console.log('parsed sub:', sub);

            // 假設 `memberId` 是在 sub 中
            if (sub && sub.memberId !== undefined) {
                console.log(`memberId: ${sub.memberId}`);
                setMemberId(sub.memberId);  // 設置 memberId
            } else {
                console.log('未找到 memberId');
                errorMessage.value = '無效的 memberId';
            }
        } catch (error) {
            console.error('解析 Token 出錯:', error);
            errorMessage.value = '解析 Token 出錯';
        }
    }


    // 清除用戶資料
	function clearUserInfo() {
        account.value = '';
        token.value = '';
        memberId.value = null;  // 清空 memberId
        localStorage.removeItem('account');  // 從 localStorage 刪除
        localStorage.removeItem('authToken');  // 從 localStorage 刪除
        localStorage.removeItem('memberId');
        axiosapi.defaults.headers['Authorization'] = ""; // 清除 Authorization 標頭
    }

    return {
        account,
        token,
        isLogin,
        memberId,
        errorMessage,
        setAccount,
        setToken,
        clearUserInfo,
        setMemberId,
    };
});

export default useUserStore;