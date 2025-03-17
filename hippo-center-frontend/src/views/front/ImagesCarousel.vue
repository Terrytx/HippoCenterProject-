<template>
<div class="carousel">
    <!-- 控制按鈕組 -->
    <div class="controls">
    <button @click="prevSlide" class="control-button">
        <svg
        xmlns="http://www.w3.org/2000/svg"
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        >
        <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
    </button>
    <button @click="nextSlide" class="control-button">
        <svg
        xmlns="http://www.w3.org/2000/svg"
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        >
        <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
    </button>
    <button @click="togglePause" class="control-button">
        <svg
        v-if="isPaused"
        xmlns="http://www.w3.org/2000/svg"
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        >
        <polygon points="5 3 19 12 5 21 5 3"></polygon>
        </svg>
        <svg
        v-else
        xmlns="http://www.w3.org/2000/svg"
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        >
        <rect x="6" y="4" width="4" height="16"></rect>
        <rect x="14" y="4" width="4" height="16"></rect>
        </svg>
    </button>
    </div>

    <!-- 圖片輪播區域 -->
    <div class="carousel-container">
    <div
        v-for="(image, index) in images"
        :key="image.id"
        class="carousel-slide"
        :class="{ active: index === currentIndex }"
        @click="goToEvent"
        >
        <img :src="image.src" :alt="image.alt" />
    </div>
    </div>

    <!-- 資訊區塊 -->
    <div class="info">
    <transition name="fade">
        <div v-if="images[currentIndex]" class="info-content">
        <h2 class="info-title">{{ images[currentIndex].title }}</h2>
        <p class="info-description">{{ images[currentIndex].description }}</p>
        </div>
    </transition>
    </div>

    <!-- 進度指示條 -->
    <div class="progress-container">
    <button
        v-for="(_, index) in images"
        :key="index"
        @click="goToSlide(index)"
        class="progress-bar"
    >
        <div
        class="progress-fill"
        :class="{ active: index === currentIndex }"
        :style="{ width: index === currentIndex ? `${progress}%` : '0%' }"
        ></div>
    </button>
    </div>
</div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
const currentIndex = ref(0);
const progress = ref(0);
const isPaused = ref(false);
// 添加跳轉方法
const goToEvent = () => {
router.push("/event");
};
// 導入圖片
import fr01 from '@/assets/images/front/fr01.jpg'
import fr02 from '@/assets/images/front/fr02.jpg'
import fr03 from '@/assets/images/front/fr03.jpg'
import fr04 from '@/assets/images/front/fr04.jpg'
import fr05 from '@/assets/images/front/fr05.jpg'
import fr06 from '@/assets/images/front/fr06.jpg'
import fr07 from '@/assets/images/front/fr07.jpg'
import fr08 from '@/assets/images/front/fr08.jpg'

const images = [
{
    id: 1,
    src: fr01,
    alt: "藝文展覽",
    title: "當代藝術展覽",
    description: "匯集國內外優秀藝術家作品，展現當代藝術的多元風貌與創新思維。",
},
{
    id: 2,
    src: fr02,
    alt: "攝影展覽",
    title: "攝影藝術展",
    description: "精選國內外優秀攝影作品，透過鏡頭展現世界的獨特視角。",
},
{
    id: 3,
    src: fr03,
    alt: "雕塑展覽",
    title: "當代雕塑展",
    description: "展出極具特色的現代雕塑作品，呈現立體藝術的豐富內涵。",
},
{
    id: 4,
    src: fr04,
    alt: "藝術工作坊",
    title: "創意工作坊",
    description: "透過專業藝術家帶領，體驗藝術創作的樂趣與深度。",
},
{
    id: 5,
    src: fr05,
    alt: "文化講座",
    title: "藝術對談",
    description: "邀請藝文界重量級人物，分享創作理念與藝術見解。",
},
{
    id: 6,
    src: fr06,
    alt: "舞蹈演出",
    title: "現代舞展演",
    description: "結合傳統與現代，展現舞蹈藝術的優美與力量。",
},
{
    id: 7,
    src: fr07,
    alt: "藝術教育",
    title: "藝術教育推廣",
    description: "推動藝術教育，培養下一代的藝術涵養與創造力。",
},
{
    id: 8,
    src: fr08,
    alt: "特展活動",
    title: "跨界藝術節",
    description: "融合各種藝術形式，創造獨特的跨域藝術體驗。",
},
];

const nextSlide = () => {
currentIndex.value =
    currentIndex.value === images.length - 1 ? 0 : currentIndex.value + 1;
progress.value = 0;
};

const prevSlide = () => {
currentIndex.value =
    currentIndex.value === 0 ? images.length - 1 : currentIndex.value - 1;
progress.value = 0;
};

const goToSlide = (index) => {
currentIndex.value = index;
progress.value = 0;
};

const togglePause = () => {
isPaused.value = !isPaused.value;
};

let timer = null;
let progressTimer = null;

const startTimers = () => {
const interval = 4000;
const progressInterval = 50;

timer = setInterval(nextSlide, interval);
progressTimer = setInterval(() => {
    if (progress.value < 100) {
    progress.value += (progressInterval / interval) * 100;
    }
}, progressInterval);
};

const stopTimers = () => {
clearInterval(timer);
clearInterval(progressTimer);
};

onMounted(() => {
if (!isPaused.value) {
    startTimers();
}
});

onUnmounted(() => {
stopTimers();
});

// 監聽暫停狀態變化
watch(isPaused, (newValue) => {
if (newValue) {
    stopTimers();
} else {
    startTimers();
}
});
</script>

<style scoped>
.carousel {
    width: 100%;
    max-width: 78%;
    margin: 0 auto;
}

.controls {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 0.5rem;
    margin-bottom: 1.5rem;
}

.control-button {
    padding: 0.5rem;
    background-color: #f3f4f6;
    border-radius: 0.25rem;
    color: #374151;
    transition: background-color 0.2s;
    border: none;
    cursor: pointer;
}

.control-button:hover {
    background-color: #e5e7eb;
}

.carousel-container {
    position: relative;
    height: 30rem;
    overflow: hidden;
}

.carousel-slide {
    position: absolute;
    width: 100%;
    height: 100%;
    transition: transform 0.7s;
    transform: translateX(100%);
}

.carousel-slide.active {
    transform: translateX(0);
}

.carousel-slide img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.info {
    height: 10rem;
    padding: 2rem;
    /* border-radius: 0.5rem; */
    /* margin: 2rem 0; */
}

.info-content {
    max-width: 100%;
    margin: 0 auto;
    text-align: center;
}

.info-title {
    font-size: 2rem;
    color: #1a202c;
    margin-bottom: 1rem;
    font-weight: bold;
}

.info-description {
    font-size: 1.2rem;
    color: #4a5568;
    line-height: 1.6;
}

.progress-container {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    gap: 1rem;
    width: 100%;
    margin-top: 1rem;
}

.progress-bar {
    position: relative;
    height: 0.5rem;
    background-color: #d1d5db;
    overflow: hidden;
    border: none;
    padding: 0;
    cursor: pointer;
    border-radius: 10px;
}

.progress-fill {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    background-color: #f97316;
    transition: width 0.2s linear;
    opacity: 0;
}

.progress-fill.active {
    opacity: 1;
}

/* 淡入淡出動畫 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>
