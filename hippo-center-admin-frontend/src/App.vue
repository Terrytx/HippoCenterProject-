<template>
  <div id="app">
    <router-view></router-view>

  
  </div>
</template>


<script>

export default {
name: 'App'
}
</script>

<style scoped>

:root {
    --primary-color: #4A6741;
    /* 主要深綠色 */
    --secondary-color: #8AA682;
    /* 次要淺綠色 */
    --hover-color: #C2D5BB;
    /* 懸停淺綠色 */
    --background-color: #F5F7F4;
    /* 背景淺灰綠 */
    --text-primary: #2C3E2D;
    /* 主要文字色 */
    --text-secondary: #5C715E;
    /* 次要文字色 */
    --border-color: #D8E0D5;
    /* 邊框顏色 */
}

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    background-color: var(--background-color);
    color: var(--text-primary);
}

</style>