<template>
<div class="selector-container">
    <div class="venue-grid">
        <div
            v-for="venue in venues"
            :key="venue.id"
            class="venue-item"
        >
            <button
                @click="handleVenueClick(venue.id)"
                @mouseenter="hoveredVenue = venue.id"
                @mouseleave="hoveredVenue = null"
                :class="[
                    'venue-button',
                    { 'selected': selectedVenue === venue.id },
                    { 'hovered': hoveredVenue === venue.id }
                ]"
                :aria-label="venue.label"
            >
                <img 
                    :src="venue.image" 
                    :alt="venue.label"
                    class="venue-icon"
                    :class="{ 'icon-hovered': hoveredVenue === venue.id }"
                />
            </button>
            <span
                class="venue-id"
                :class="{ 'selected-text': selectedVenue === venue.id }"
            >
                {{ venue.id }}
            </span>
            <span
                class="venue-label"
                :class="{ 'selected-venue-text': selectedVenue === venue.id }"
            >
                {{ venue.label }}
            </span>
        </div>
    </div>
</div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const emit = defineEmits(['location-selected'])
const props = defineProps({
    selectedVenueId: {
        type: String,
        default: 'A01'
    }
})

// 導入圖片
import imgA01 from '@/assets/images/b5venue/selector/A01-3.jpg'
import imgA02 from '@/assets/images/b5venue/selector/A02-1.jpg'
import imgA03 from '@/assets/images/b5venue/selector/A03-2.jpg'
import imgA04 from '@/assets/images/b5venue/selector/A04-1.jpg'
import imgA05 from '@/assets/images/b5venue/selector/A05-2.jpg'
import imgB02 from '@/assets/images/b5venue/selector/B02-1.jpg'
import imgB03 from '@/assets/images/b5venue/selector/B03-1.jpg'
import imgB04 from '@/assets/images/b5venue/selector/B04-2.jpg'
import imgB05 from '@/assets/images/b5venue/selector/B05-1.jpg'
import imgB06 from '@/assets/images/b5venue/selector/B06-1.jpg'

const venues = [
    { id: 'A01', label: '1號倉庫', image: imgA01 },
    { id: 'A02', label: '2號倉庫', image: imgA02 },
    { id: 'A03', label: '3號倉庫', image: imgA03 },
    { id: 'A04', label: '4號倉庫', image: imgA04 },
    { id: 'A05', label: '5號倉庫', image: imgA05 },
    { id: 'B02', label: '春田序曲', image: imgB02 },
    { id: 'B03', label: '瑪吉的畫廊', image: imgB03 },
    { id: 'B04', label: '巴特的藝術空間', image: imgB04 },
    { id: 'B05', label: '荷馬的闢護所', image: imgB05 },
    { id: 'B06', label: '麗莎的實驗室', image: imgB06 }
]

const selectedVenue = ref(props.selectedVenueId)
const hoveredVenue = ref(null)

// 監聽父組件傳入的選中場地變化
watch(() => props.selectedVenueId, (newId) => {
    if (newId && newId !== selectedVenue.value) {
        selectedVenue.value = newId
    }
})

const handleVenueClick = (venueId) => {
    selectedVenue.value = venueId
    const venue = venues.find(v => v.id === venueId)
    emit('location-selected', {
        category: venueId.startsWith('A') ? 'warehouse' : 'venue',
        venueId: venueId,
        venueName: venue?.label || '',
        type: 'venue'
    })
}

onMounted(() => {
    const initialLocation = venues.find(v => v.id === props.selectedVenueId)
    if (initialLocation) {
        emit('location-selected', {
            category: props.selectedVenueId.startsWith('A') ? 'warehouse' : 'venue',
            venueId: props.selectedVenueId,
            venueName: initialLocation.label,
            type: 'venue'
        })
    }
})
</script>
<style scoped>
@import '@/assets/fonts/font.css';
.selector-container {
font-family: Kantumruy Pro; 
height: 10vh;
display: flex;
align-items: center;
background-color: white;
position: relative;
}

.venue-grid {
display: flex;
justify-content: center;
gap: 0.25rem;
width: 100%;
padding: 0 0.5rem;
}

.venue-item {
display: flex;
flex-direction: column;
align-items: center;
flex: 1;
min-width: 0;
max-width: calc(10% - 0.25rem);
}

.venue-button {
width: 6.5rem;
height: 6.5rem;
border-radius: 50%;
display: flex;
align-items: center;
justify-content: center;
border: 1.5px solid #d1d5db;
margin-bottom: 0.3rem;
background-color: white;
overflow: hidden;
padding: 0;
cursor: pointer;
transition: all 0.3s ease;
}

.venue-button.selected {
outline: 3px solid #e67e00;
outline-offset: 2.5px;
}


.venue-button.hovered {
border-color: #f8961e;
transform: scale(1.1);
}

.venue-icon {
width: 100%;
height: 100%;
object-fit: cover;
transition: transform 0.3s ease;
}

.venue-icon.icon-hovered {
transform: scale(1.1);
}

.venue-id {

text-align: center;
color: #4b5563;
transition: color 0.3s ease;
font-size: 14px;
font-weight: 400;
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
width: 100%;
padding: 0 0.125rem;
}

.venue-label {
font-size: 14px;

text-align: center;
color: #4b5563;
transition: color 0.3s ease;
font-weight: 400;
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
width: 100%;
padding: 0 0.2rem;
}

.selected-text {
font-weight: 600;
font-size: small;
font-size: 16px;
}

.selected-venue-text{
font-weight: 600;
font-size: 20px;
text-decoration: underline;
text-decoration-color: #f8961e;
text-decoration-thickness: 2px;
}

@media (max-width: 768px) {
.venue-button {
width: 1.75rem;
height: 1.75rem;
}

.venue-icon {
width: 0.9rem;
height: 0.9rem;
}

.venue-id {
font-size: 0.5rem;
}

.venue-label {
font-size: 0.6rem;
}
}
</style>