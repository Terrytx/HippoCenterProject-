<template>
  <div class="venue-image-container">
    <div v-for="image in venueImages" :key="image.imageId" class="image-item">
    <img
      :src="`data:image/jpeg;base64,${image.imageData}`"
      :alt="image.altText"
      class="venue-image"
    />
    </div>
</div>
  
</template>
  
<script setup>
import { ref, onMounted, watch } from 'vue'
import { useVenueStore } from '@/stores/venueStore'
import { storeToRefs } from 'pinia'

const props = defineProps({
  venueId: {
    type: String,
    required: true
  }
})

const venueStore = useVenueStore()
const { venueImages } = storeToRefs(venueStore)


watch(() => props.venueId, async (newId) => {
  if (newId) {
    try {
      await venueStore.fetchVenueById(newId)
    } catch (error) {
      console.error('獲取場館圖片失敗:', error)
    }
  }
})

onMounted(async () => {
  if (props.venueId) {
    try {
      await venueStore.fetchVenueById(props.venueId)
    } catch (error) {
      console.error('獲取場館圖片失敗:', error)
    }
  }
})
</script>

<style scoped>
.venue-image-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 3%;
}

.image-item {
  flex: 0 0 calc(33.333% - 20px);
  max-width: calc(33.333% - 20px);
}

.venue-image {
  width: 100%;
  height: auto;

  object-fit: cover;
  aspect-ratio: 16/10;
  box-shadow: 3px 3px 5px gray;
}

@media (max-width: 768px) {
  .image-item {
    flex: 0 0 calc(50% - 10px);
    max-width: calc(50% - 10px);
  }
}

@media (max-width: 480px) {
  .image-item {
    flex: 0 0 100%;
    max-width: 100%;
  }
}
</style>