<template>
  <div id="app">
    <Header />
    <main class="content">
      <router-view></router-view>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import useUserStore from '@/stores/user'  // 根據實際路徑調整
import Header from './components/footers/Header.vue'
import Footer from './components/footers/Footer.vue'

const userStore = useUserStore()

onMounted(() => {
  const token = localStorage.getItem('authToken')
  const account = localStorage.getItem('account')
  if (token) {
    userStore.setToken(token)
    if (account) {
      userStore.setAccount(account)
    }
    console.log('Token 已從 localStorage 同步到 store')
    console.log('同步後的 token:', userStore.token)
    console.log('同步後的 account:', userStore.account)
  } else {
    console.log('未發現 token')
  }
})
</script>

<style>
@import '@/assets/styles/main.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body,
html {
  width: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  font-family: Kantumruy Pro;

}
</style>
