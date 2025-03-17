<template>
    <header class="header">
    <!-- Logo 區域 -->
    <div class="logo-container">
        <h1 class="company-name">公司名稱</h1>
    </div>

    <!-- 導航菜單 -->
    <nav class="nav-menu">
        <ul>
            <li><a href="/" class="nav-link">首頁</a></li>
            <li><a href="/products" class="nav-link">產品</a></li>
            <li><a href="/services" class="nav-link">服務</a></li>
            <li><a href="/about" class="nav-link">關於我們</a></li>
            <li><a href="/contact" class="nav-link">聯絡我們</a></li>
        </ul>
    </nav>
    
        <!-- 搜尋框 -->
    <div class="search-container">
        <input 
        type="text" 
        v-model="searchQuery"
        @keyup.enter="handleSearch"
        placeholder="搜尋..." 
        class="search-input"
        >
        <button @click="handleSearch" class="search-button">搜尋</button>
    </div>
    </header>
</template>
  
  <script>
  export default {
    name: 'Header',
    data() {
      return {
        searchQuery: ''
      }
    },
    methods: {
      handleSearch() {
        // 處理搜尋邏輯
        console.log('搜尋:', this.searchQuery)
        // 這裡可以加入實際的搜尋功能
      }
    }
  }
  </script>
  
  <style scoped>
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 2rem;
    background-color: #ffffff;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }
  
  .logo-container {
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  .logo {
    height: 40px;
    width: auto;
  }
  
  .company-name {
    font-size: 1.5rem;
    font-weight: bold;
    color: #333;
  }
  
  .nav-menu ul {
    display: flex;
    gap: 2rem;
    list-style: none;
    margin: 0;
    padding: 0;
  }
  
  .nav-link {
    text-decoration: none;
    color: #333;
    font-weight: 500;
    transition: color 0.3s ease;
  }
  
  .nav-link:hover {
    color: #007bff;
  }
  
  .search-container {
    display: flex;
    gap: 0.5rem;
  }
  
  .search-input {
    padding: 0.5rem 1rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    min-width: 200px;
  }
  
  .search-button {
    padding: 0.5rem 1rem;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .search-button:hover {
    background-color: #0056b3;
  }
  
  /* 響應式設計 */
  @media (max-width: 768px) {
    .header {
      flex-direction: column;
      padding: 1rem;
    }
  
    .nav-menu ul {
      flex-direction: column;
      gap: 1rem;
      text-align: center;
      margin: 1rem 0;
    }
  
    .search-container {
      width: 100%;
      margin-top: 1rem;
    }
  
    .search-input {
      flex-grow: 1;
    }
  }
  </style>