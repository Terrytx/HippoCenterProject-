<template>
  <div class="member-center">
    <h1>會員中心</h1>
    <div class="form-container">
      <div class="tab-switcher">
        <button :class="{ active: isLoginForm }" @click="isLoginForm = true">登入</button>
        <button :class="{ active: !isLoginForm }" @click="isLoginForm = false">註冊</button>
      </div>
      <div class="form-content">
        <LoginForm v-if="isLoginForm" @switchForm="toggleForm" @login="handleLogin" />
        <RegisterForm v-else @switchForm="toggleForm" @register="handleRegister" />
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref } from 'vue';
import LoginForm from '../../components/form/LoginForm.vue';
import RegisterForm from '../../components/form/RegisterForm.vue';

const isLoginForm = ref(true);

const toggleForm = () => {
  isLoginForm.value = !isLoginForm.value;
};

const handleLogin = (user) => {
  // 模擬登入成功並跳轉至會員資料頁面
  console.log('登入成功', user);
  // 這裡可以使用 router.push() 跳轉頁面
  router.push({ name: 'MemberProfile' });
};

const handleRegister = (user) => {
  // 模擬註冊成功
  console.log('註冊成功', user);
};
</script>



