<template>
  <div>
    <ShopSidebar />
    <div class="product-detail">
      <div v-if="product" class="preview-modal-content">
        <!-- 左側圖片區域 -->
        <div class="preview-image-section">
          <img :src="product.image" alt="產品圖片" class="preview-image">
        </div>
        
        <!-- 右側資訊區域 -->
        <div class="preview-info-section">
          <div class="preview-header">
            <h2 class="preview-title">{{ product.productName }}</h2>
          </div>

          <div class="preview-price">
            <h3>價格</h3>
            <p>${{ product.price }}</p>
          </div>

          <div class="preview-description">
            <h3>商品描述</h3>
            <p>{{ product.description || '暫無描述' }}</p>
          </div>

          <div class="preview-quantity">
            <h3>數量</h3>
            <div class="quantity-control">
              <button 
                class="quantity-btn" 
                @click="updateQuantity(-1)" 
                :disabled="quantity <= 1"
              >−</button>
              <input 
                type="text" 
                v-model.number="quantity" 
                class="quantity-input" 
                readonly
              />
              <button 
                class="quantity-btn" 
                @click="updateQuantity(1)" 
                :disabled="quantity >= product.stock"
              >+</button>
            </div>
          </div>

          <button class="add-to-cart-btn" @click="addToCart">
            加入購物車
          </button>
        </div>
      </div>
      <div v-else>
        <p>載入中...</p>
      </div>
    </div>

    <!-- 彈出視窗 -->
    <div v-if="showModal" class="modal-container">
      <div class="modal-content">
        <h5>{{ modalTitle }}</h5>
        <p>{{ modalMessage }}</p>
        <div class="modal-actions">
          <button @click="handleModalClose" class="modal-btn">關閉</button>
          <button v-if="modalRedirect" @click="handleRedirect" class="modal-btn modal-confirm">登入</button>
        </div>
      </div>
    </div>
  </div>
</template>



<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter} from 'vue-router';
import axios from 'axios';
import ShopSidebar from '@/components/navbar/b2shopcart/ShopSidebar.vue';
import axiosapi from "@/plugins/axios.js";

// 使用 Composition API
const route = useRoute();
const router = useRouter();
const product = ref(null); // 用於存儲商品詳細資訊
const quantity = ref(1); // 數量初始化為 1

// 控制彈出視窗
const showModal = ref(false);
const modalTitle = ref('');
const modalMessage = ref('');
const modalRedirect = ref(false); // 控制是否需要跳轉

// 獲取商品詳細資訊的方法
const fetchProduct = () => {
  const productId = route.params.id;
  console.log('商品 ID:', productId); // 確認是否為正確的 ID
  if (!productId) {
    console.error('商品 ID 無效，請檢查路由參數！');
    return;
  }
  axiosapi
    .get(`/mowmow/user/products/${productId}`)
    .then((response) => {
      product.value = {
        ...response.data,
        image: response.data.imageUrls?.[0] || '/images/default-product.jpg',
      };
    })
    .catch((error) => {
      console.error('獲取商品詳細資訊失敗:', error);
    });
};

// 更新數量的方法
const updateQuantity = (change) => {
  quantity.value += change;
  if (quantity.value < 1) quantity.value = 1; // 確保數量不小於 1
  if (quantity.value > product.value.stock) quantity.value = product.value.stock; // 確保數量不超過庫存
};

// 加入購物車的方法
const addToCart = () => {
  console.log('加入購物車的商品 ID:', product.value.productId);
  console.log('加入購物車的數量:', quantity.value);

  const token = localStorage.getItem('authToken');
  if (!token) {
    // 顯示未登入提示
    showModal.value = true;
    modalTitle.value = '未登入';
    modalMessage.value = '請先登入後再加入購物車';
    modalRedirect.value = true; // 需要跳轉
    return;
  }

  axiosapi
    .post('/mowmow/user/cart/add', {
      productId: product.value.productId,
      quantity: quantity.value,
    }, {
      headers: {
        Authorization: `Bearer ${token}`,
      }
    })
    .then(() => {
      // 顯示加入購物車成功提示
      showModal.value = true;
      modalTitle.value = '成功';
      modalMessage.value = '已成功加入購物車';
      modalRedirect.value = false; // 不需要跳轉
    })
    .catch((error) => {
      console.error('加入購物車失敗:', error);
      // 顯示錯誤提示
      showModal.value = true;
      modalTitle.value = '失敗';
      modalMessage.value = '登入逾時，請重新登入';
      modalRedirect.value = true;
    });
};

// 處理彈出視窗關閉
const handleModalClose = () => {
  showModal.value = false;
};

// 處理跳轉
const handleRedirect = () => {
  router.push('/secure/login');
};

// 初次加載數據
onMounted(fetchProduct);

// 監聽路由參數變化
watch(
  () => route.params.id,
  () => {
    fetchProduct();
  }
);
</script>

<style scoped>
@import '@/assets/styles/main.css';
@import '@/assets/fonts/font.css';
@import '@/assets/styles/mainContent.css';

.product-detail {
  margin-left: 22%;
  margin-top: 1%;
  display: flex;
  justify-content: center;
}

.preview-modal-content {
  display: flex;
  background: white;
  width: 1000px;
  height: 600px;
  border-radius: 8px;
  overflow: hidden;
}

.preview-image-section {
  width: 50%;
  background: #f8f8f8;
  padding: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.preview-info-section {
  width: 50%;
  padding: 2rem;
  display: flex;
  flex-direction: column;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 2rem;
}

.preview-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0;
}

.preview-price {
  margin-bottom: 1.5rem;
}

.preview-price h3 {
  font-size: 1rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.preview-price p {
  font-size: 1.5rem;
  color: #28a745;
  font-weight: bold;
}

.preview-description {
  margin-bottom: 1.5rem;
}

.preview-description h3 {
  font-size: 1rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.preview-description p {
  color: #666;
  line-height: 1.5;
}

.preview-quantity {
  margin-bottom: 1.5rem;
}

.preview-quantity h3 {
  font-size: 1rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  border-radius: 3px;
  border: none;
  background: #f0f0f0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.quantity-btn:hover:not(:disabled) {
  background: #e0e0e0;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-input {
  width: 60px;
  height: 32px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.add-to-cart-btn {
  /* margin-top: auto; */
  background: #000000;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.add-to-cart-btn:hover {
  background: #696969;
}

.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 400px;
  text-align: center;
}

.modal-actions {
  margin-top: 20px;
}

.modal-btn {
  background-color: #ddd;
  border: none;
  padding: 10px 20px;
  margin: 5px;
  border-radius: 5px;
  cursor: pointer;
}

.modal-confirm {
  background-color: #28a745;
  color: white;
}

.modal-btn:hover {
  opacity: 0.9;
}
</style>


