<template>
  <div class="carousel relative w-full h-96 overflow-hidden">
    <!-- 輪播圖 -->
    <div class="carousel-container relative h-full">
      <div
        v-for="(slide, index) in slides"
        :key="slide.id"
        class="carousel-slide absolute w-full h-full transition-transform duration-500"
        :class="{ 
          'translate-x-0': index === currentSlide,
          '-translate-x-full': index < currentSlide,
          'translate-x-full': index > currentSlide
        }"
      >
        <img :src="slide.image" :alt="slide.alt" class="w-full h-full object-cover">
      </div>
    </div>

    <!-- 下一張按鈕 -->
    <button
      @click="prevSlide"
      class="absolute left-4 top-1/2 -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full"
    >
      ←
    </button>
    <button
      @click="nextSlide"
      class="absolute right-4 top-1/2 -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full"
    >
      →
    </button>

    <!-- 導航原點 -->
    <div class="absolute bottom-4 left-1/2 -translate-x-1/2 flex space-x-2">
      <button
        v-for="(_, index) in slides"
        :key="index"
        @click="goToSlide(index)"
        class="w-3 h-3 rounded-full transition-all duration-300"
        :class="[
          index === currentSlide 
            ? 'bg-white scale-125' 
            : 'bg-white bg-opacity-50 hover:bg-opacity-75'
        ]"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const currentSlide = ref(0)
const slides = ref([
  {
    id: 1,
    image: '/Users/black/Desktop/eventImages',
    alt: 'Slide 1'
  },
  // ... 添加圖片
])

const nextSlide = () => {
  currentSlide.value = 
    currentSlide.value === slides.value.length - 1 
      ? 0 
      : currentSlide.value + 1
}

const prevSlide = () => {
  currentSlide.value = 
    currentSlide.value === 0 
      ? slides.value.length - 1 
      : currentSlide.value - 1
}

const goToSlide = (index) => {
  currentSlide.value = index
}

// 自動播放
let timer = null
onMounted(() => {
  timer = setInterval(() => {
    nextSlide()
  }, 5000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>