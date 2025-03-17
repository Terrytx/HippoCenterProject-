<template>
  <div>
    <form @submit.prevent="handleLogin">
      <div>
        <label for="username">用戶名</label>
        <input type="text" id="username" v-model="username" />
      </div>
      <div>
        <label for="password">密碼</label>
        <input type="password" id="password" v-model="password" />
      </div>
      <button type="submit">登入</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const username = ref('');
const password = ref('');
const emit = defineEmits(['switchForm', 'login']);

const handleLogin = () => {
  // 模擬登入成功
  emit('login', { username: username.value });
};

const toggleForm = () => {
  emit('switchForm');
};
</script>
