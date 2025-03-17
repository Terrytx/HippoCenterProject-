<template>
    <div class="app-container">
        <div class="main-sidebar">
            <AdminSidebar />
        </div>
        <div class="main-container">
            <main class="content">
                <router-view></router-view>
            </main>
        </div>
    </div>        
</template>
    
<script setup>
import AdminSidebar from '../components/navbar/AdminSidebar.vue'
    
</script>
    
<style>
:root {
    --primary-color: #4A6741;
    --secondary-color: #8AA682;
    --hover-color: #C2D5BB;
    --background-color: #F5F7F4;
    --text-primary: #2C3E2D;
    --text-secondary: #5C715E;
    --border-color: #D8E0D5;
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

.app-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    min-height: 60vh;

}
.main-sidebar {
    display: flex;
    position: fixed;
    top: 0;
    left: 0;
    width: var(--sidebar-width);
    height: 100vh;
    background-color: white;
    border-right: 1px solid var(--border-color);
    z-index: 1000;
}

.main-container {

    position: relative;
    width: 90%;
    margin: auto;
    min-height: 100vh;
    padding-left: 10%;
}


.content {
    flex: 1;
    padding: 2rem;
    background-color: var(--background-color);
}

@media (max-width: 768px) {
    .main-sidebar {
        position: fixed;
        transform: translateX(-100%);
        transition: transform 0.3s ease;
    }

    .main-sidebar.active {
        transform: translateX(0);
    }

    .main-container {
        margin-left: 0;
    }

    .content {
        padding: 1rem;
    }
}    
</style>