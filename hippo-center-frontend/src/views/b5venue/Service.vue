<template>
    <div class="container">
        <ServiceSidebar />
        <div class="content">
            <router-view></router-view>
        </div>
    </div>
</template>
    
<script>
import ServiceSidebar from '../../components/navbar/b5venue/ServiceSidebar.vue';
export default {
    name: 'B5Service',
    // 註冊組件
    components: {
        ServiceSidebar
    }
}
</script>

<style>


</style>