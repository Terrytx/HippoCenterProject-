<template>
  <div class="venue-info">
  <h3>租借場地介紹</h3>
            <!-- SVG 地圖容器 -->
                <div class="venue-container">
                    <!-- <div v-html="svgContent" class="venue-map w-full">
                    </div> -->
                    <div class="venue-map w-full">
                      <WarehouseSVG 
                      :selected-area="selectedArea"
                      @select="handleSelect"  
                      />
                    </div>
                    <!-- 場地詳細資訊 -->
                    <div class="venue-detail" >
                      <div class="info-section">
                          <table class="venue-table">
                              <tbody>
                                  <tr>
                                      <th colspan="2" class="venue-name"> {{ venueStore.selectedVenue?.venueName || '尚未選擇場地' }}</th>
                                      <td colspan="2">
                                        <a href="#" class="pdf-link" @click.prevent="downloadSpecifications">
                                        <span class="download-text">下載詳細資料</span>
                                        </a>
                                      </td>
                                  </tr>
                                  
                                  <tr>
                                      <td class="td1"><img src="@/assets/images/b5venue/area.png" class="icon1" alt="capacity icon"></td>
                                      <td class="label">場地坪數</td>
                                      <td class="label2">{{ venueStore.selectedVenue?.areaPings }}</td>
                                      <td class="label3">坪</td>
                                  </tr>
                                  <tr>
                                      <td class="td1"><img src="@/assets/images/b5venue/leadership.png" class="icon2" alt="capacity icon"></td>
                                      <td class="label">容納人數</td>
                                      <td class="label2">{{ venueStore.selectedVenue?.capacity }}</td>
                                      <td class="label3">人</td>
                                  </tr>
                                  <tr>
                                      <td class="td1"><img src="@/assets/images/b5venue/height.png" class="icon3" alt="capacity icon"></td>
                                      <td class="label">場地高度</td>
                                      <td class="label2">{{ venueStore.selectedVenue?.venueHeight }}</td>
                                      <td class="label3">公尺</td>
                                  </tr>
                              </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                    <B03venueImage :venue-id="venueId" @image-loaded="handleImageLoaded"/>

                  </div>
</template>
    
<script setup>
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
import WarehouseSVG from '@/components/buttons/b5/WarehouseSVG.vue'
import axiosapi from '@/plugins/axios.js'
import { useVenueStore } from '@/stores/venueStore'
import { storeToRefs } from 'pinia'  
// import venueSvg from '@/assets/images/venue_map.svg?raw'
import B03venueImage from './B03venueImage.vue'

// Store 相關
const venueStore = useVenueStore()  // 這行之前漏掉了 venueStore 的初始化

// 響應式數據
// const svgContent = ref('')
const apiToken = ref('')

const selectedArea = ref('A01') // 預設選擇 A01

const handleSelect = (area) => {
  selectedArea.value = area
  venueStore.fetchVenueById(area) // 添加這行來獲取場地資料
}
// Props 定義
const props = defineProps({
  venueId: {
    type: String,
    default: 'A01'
  }
})

// API 相關方法
const getApiToken = async () => {
  try {
    const response = await axiosapi.get('/api/auth/token')
    apiToken.value = response.data.token
  } catch (err) {
    console.error('Failed to get API token:', err)
  }
}

// 事件處理方法
const handleImageLoaded = (images) => {
  console.log('Images loaded:', images)
}

const handleMapClick = async (event) => {
  const target = event.target.closest('.warehouse')
  if (!target) return
  
  const venueId = target.id.replace('venue-', '')
  console.log('點擊的場館ID:', venueId)
  
  try {
    await venueStore.fetchVenueById(venueId)
  } catch (error) {
    console.error('獲取場館資料失敗:', error)
  }
}

// 下載規格方法
const downloadSpecifications = async () => {
  if (venueStore.selectedVenue?.venueId) {
    try {
      await venueStore.downloadSpecifications(venueStore.selectedVenue.venueId)
    } catch (error) {
      console.error('下載失敗:', error)
    }
  }
}

// Watch
watch(() => props.venueId, (newId) => {
  selectedArea.value = newId
  venueStore.fetchVenueById(newId)

})

// 生命週期鉤子
onMounted(async () => {
  // 初始化數據
  await getApiToken()
  
  // 設置 SVG
  selectedArea.value = props.venueId // 確保初始值同步
  await venueStore.fetchVenueById(props.venueId)

  // svgContent.value = venueSvg
  // console.log('完整的 SVG 內容:', svgContent.value)
  
  // 加載場館數據
  console.log('組件掛載')
  await venueStore.fetchAvailableVenues()
  
  // 設置地圖點擊事件
  const mapContainer = document.querySelector('.venue-map')
  if (mapContainer) {
    mapContainer.addEventListener('click', handleMapClick)
  }
})

onBeforeUnmount(() => {
  const mapContainer = document.querySelector('.venue-map')
  if (mapContainer) {
    mapContainer.removeEventListener('click', handleMapClick)
  }
})

</script>

<style  scoped>
@import '@/assets/styles/main.css';
@import '@/assets/fonts/font.css';
@import '@/assets/styles/mainContent.css';

.venue-info{
  height: 100vh;
  margin-top: 1%;
}

.venue-container {
  margin-top: 5%;
  width: 100%;
  display: flex;

}
h3{
  font-size: 30px;
  margin-bottom: 30px;
}
.venue-map {
  flex: 0 0 60%;
  width: 100%;
  max-width: 600px; 
  display: block; /* 移除可能的額外空間 */

}

.venue-detail {
  flex: 0 0 35%;
  min-width: 400px;
  max-width: 40%;  /* 限制最大寬度 */
  position: sticky;  /* 讓詳細資訊固定在視窗中 */
  height: fit-content;
  margin-left: 2rem; /* 確保與地圖有適當間距 */
  
}
.venue-table{
  width: 420px;
  height: 200px;
  font-size: 22px;
  font-family: Kantumruy Pro; 

}
.td1{
  width: 70px;

}
.pdf-link {   /* 檔案下載連結樣式 */
    text-decoration: underline;  /* 添加底線 */
    color: inherit;  /* 保持原本的顏色 */
    cursor: pointer;  /* 滑鼠變成手指形狀 */
}

.download-text {   /* 檔案下載連結的文字樣式 */
    font-size: 20px;
    font-weight: 400;
    
}

.label{    /* 坪數 人數 高度 */
  align-content: end;
  text-align: left;
  font-size: 26px;
  font-weight:800;

}
.label2{   /* 坪數 人數 高度 帶出的資料 */
  text-align: right;
  align-content: end;
  padding-right: 10px;
  font-size: 24px;
  font-weight:400;
  width:30px ;
  
}
.label3{   /* 單位 */
  text-align: left;
  align-content: end;
  width: 60px;
  font-size: 24px;
  font-weight:400;

}
.venue-name{
  text-align: left;
  font-size: 30px;
  font-weight: 1000;
  font-family: Kantumruy Pro; 
  letter-spacing: 5px;

}
td{
  height: 60px;
  width: 150px;
  align-items: end;

}
.icon1 {
    width:60px;  /* 調整圖片大小 */
    height: 60px;
    vertical-align: middle;  /* 讓圖片垂直置中對齊文字 */
    margin-right: 5px;  /* 與文字的間距 */
    margin-left: 5px;

}
.icon2 {
    width:50px;  /* 調整圖片大小 */
    vertical-align: middle;  /* 讓圖片垂直置中對齊文字 */
    margin-right: 20px;  /* 與文字的間距 */
    margin-left: 5px;
}
.icon3 {
    width:50px;
    height: 45px;  /* 調整圖片大小 */
    vertical-align: middle;  /* 讓圖片垂直置中對齊文字 */
    margin-top: 10px;
    margin-right: 20px;  /* 與文字的間距 */
    margin-left: 5px;
}
</style>