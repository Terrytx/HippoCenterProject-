<template>
  <div>
    <form @submit.prevent="handleRegister">
      <div>
        <label for="username">用戶名</label>
        <input type="text" id="username" v-model="username" />
      </div>
      <div>
        <label for="password">密碼</label>
        <input type="password" id="password" v-model="password" />
      </div>
      <button type="submit">註冊</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const username = ref('');
const password = ref('');
const emit = defineEmits(['switchForm', 'register']);

const handleRegister = () => {
  // 模擬註冊成功
  emit('register', { username: username.value });
};

const toggleForm = () => {
  emit('switchForm');
};
</script>
