<template>
    <div class="content">
            <div class="main-content">
            <!-- <div id="news" class="section"><B01News /></div> -->
            <!-- <div id="process" class="section"><B02Process /></div> -->
            <div id="introduction" class="section"><B03Introduction /></div>
            <div id="schedule" class="section"><B04Schedule /></div>
            <!-- <div id="faq" class="section"><B05FAQ /></div> -->
        </div>
    </div>
</template>

<script setup>

// import B01News from './B01News.vue'
// import B02Process from './B02Process.vue'
import B03Introduction from './B03Introduction.vue'
import B04Schedule from './B04Schedule.vue'
// import B05FAQ from './B05FAQ.vue'

</script>





<style scoped>
.main-content{
    margin-left: 22%;
    /* margin-top:10%; */
}
.section {
    scroll-margin-top: 11vh;
    min-height: 20vh;   
    margin-bottom: 5%;

}




</style>