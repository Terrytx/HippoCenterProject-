<template>
  <div>
    <PersonSidebar />
    <div class="main-content">
      <h3>商品收藏</h3>
      <div v-if="loading" class="loading">載入中...</div> <!-- 顯示載入中提示 -->

      <div class="product-list" v-if="!loading">
        <div 
          v-for="product in products" 
          :key="product.myloveId" 
          class="product-item">
          <img :src="product.imageUrl" :alt="product.name" /> <!-- 顯示商品圖片 -->
          <div class="product-info">
            <p class="product-name">{{ product.name }}</p> <!-- 顯示商品名稱 -->
            <p class="product-price">價格：{{ product.price | currency }}</p> <!-- 顯示商品價格 -->
          </div>
          <div class="button-container">
            <router-link :to="`/product/${product.myloveId}`" class="btn btn-view-product">
              查看商品
            </router-link>
            <button @click="addToFavorites(product)" class="btn btn-remove-favorite">❤</button>
          </div>
        </div>
      </div>

      <!-- 顯示錯誤訊息 -->
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div> 
      <!-- 如果沒有最愛商品顯示 "沒有找到收藏" -->
      <div v-if="!products.length && !loading && !errorMessage" class="no-favorites-message">
        沒有找到收藏的商品
      </div>
    </div>
  </div>
</template>

<script setup>
import PersonSidebar from '@/components/navbar/b4member/PersonSidebar.vue';
import axiosapi from "@/plugins/axios.js";
import { ref, onMounted } from 'vue';
import Swal from 'sweetalert2';

const products = ref([]);  // 存儲最愛商品數據
const loading = ref(true);  // 載入狀態
const errorMessage = ref("");  // 錯誤訊息

// 從後端 API 獲取最愛商品
// 修改 fetchMyLoveProducts 函數
const fetchMyLoveProducts = async (myloveType = 'P') => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    errorMessage.value = "未找到有效的授權 Token";
    loading.value = false;
    return;
  }

  try {
    const response = await axiosapi.get(`/secure/mylove/${myloveType}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
console.log(`看看我拜託${response.data.list}`)
    if (response.data.list) {
      // 現在後端直接返回完整的商品信息，不需要額外處理
      products.value = response.data.list.map(item => ({
        myloveNum: item.myloveNum,
        myloveId: item.myloveId,
        name: item.name,
        price: item.price,
        imageUrl: item.imageUrl,
        eventStartDate: item.eventStartDate
      }));
    } else {
      products.value = [];
      errorMessage.value = "沒有找到收藏商品";
    }
  } catch (error) {
    console.error(error);
    errorMessage.value = "取得收藏商品資料時發生錯誤";
  } finally {
    loading.value = false;
  }
};

//取消我的最愛
const addToFavorites = async (product) => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    errorMessage.value = "未找到有效的授權 Token";
    return;
  }

  try {
    // 呼叫後端 API 來切換最愛狀態（取消收藏）
    const response = await axiosapi.post(`/secure/mylove/toggle/p`, {
      myloveId: product.myloveId  // 傳送被點擊的商品的 myloveId
    }, {
      headers: {
        Authorization: `Bearer ${token}`  // 傳遞授權的 Token
      }
    });

    console.log(response.data)
    // 判斷後端返回的結果，並更新商品資料
    if (response.data.message) {
      Swal.fire({
        icon: 'success',
        title: '取消收藏成功',
        text: `${product.name} 已被從最愛中移除！`,
        confirmButtonText: '確定',
        confirmButtonColor: '#0e0e0e'
      }).then(() => {
        // 成功後移除該商品，並重新抓取最愛商品
        products.value = products.value.filter(item => item.myloveId !== product.myloveId);
        // 若所有商品都被取消後，顯示 "沒有找到收藏"
        if (products.value.length === 0) {
          errorMessage.value = "沒有找到收藏的商品";
        }
      });
    } else {
      errorMessage.value = "取消收藏失敗，請稍後再試";
    }
  } catch (error) {
    console.error(error);
    errorMessage.value = "取消收藏時發生錯誤";
  }
};

// 初始化加載最愛商品
onMounted(() => {
  fetchMyLoveProducts('p');  // 查詢最愛商品，若需要其他類型的最愛可更改 myloveType
});
</script>

<style scoped>
@import '@/assets/styles/main.css';
@import '@/assets/styles/mainContent.css';

.main-content {
  padding-top: 15px;
  min-height: 700px; /* 設定最小高度為600px（根據需求調整） */
}


.product-list {
  display: grid;
  gap: 30px; /* 商品間的間距 */
  grid-template-columns: repeat(4, 1fr); /* 每行最多顯示4個商品 */
  margin-bottom: 40px; /* 整體商品區域與頂部的間距 */
}

.product-item {
  background-color: #f9f9f9;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease-in-out;
}

.product-item:hover {
  transform: translateY(-10px);
}

.product-item img {
  width: 100%;
  height: auto;
}

.product-info {
  margin-top: 10px;
  margin-left: 5px;
}

.product-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}



.loading {
  text-align: center;
  font-size: 18px;
  color: #999;
}

.error-message {
  color: red;
  text-align: center;
  margin-top: 20px;
}

.no-favorites-message {
  color: #555;
  text-align: center;
  margin-top: 20px;
}

.button-container {
  display: flex;
  justify-content: space-between; /* 讓按鈕分開並推到左右兩邊 */
  align-items: center; /* 垂直居中對齊 */
  margin-top: 10px;
}

.btn-view-product {
  background-color: #000000;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9rem;
  text-decoration: none;
  text-align: center;
  transition: background-color 0.3s ease;
}

.btn-view-product:hover {
  background-color: #0056b3;
}

.btn-remove-favorite {
  background-color: #ff6347;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
}

.btn-remove-favorite:hover {
  background-color: #e9533b;
}

</style>
