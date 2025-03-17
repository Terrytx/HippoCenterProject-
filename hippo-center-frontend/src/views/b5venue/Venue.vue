<template>
    <div>
        <Venuesidebar />
        <router-view v-if="$route.path === '/venue/booking'"></router-view>
        <VenueMain v-else></VenueMain>
    </div>
</template>

<script setup>
import Venuesidebar from '../../components/navbar/b5venue/venuesidebar.vue'
import VenueMain from './venue/VenueMain.vue';

</script>





<style scoped>
.main-content{
    margin-left: 22%;
    margin-top: 1%;
}





</style>