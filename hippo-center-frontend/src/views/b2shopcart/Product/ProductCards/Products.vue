<template>
  <div class="products container">
    <!-- <h2 class="text-center">商品</h2> -->
    <div class="products-grid">
      <div v-for="product in activeProducts" :key="product.productId" class="product-card">
        <img :src="product.image" class="product-image" alt="商品圖片" />
        <div class="product-details">
          <h5 class="product-title">{{ product.productName }}</h5>
          <p class="product-price">價格: {{ product.price }}</p>
          <div class="action-buttons">
            <router-link :to="`/product/${product.productId}`" class="btn btn-view"> 查看商品 </router-link>
            <!-- 商品卡片中的收藏按鈕 -->
            <button class="btn btn-favorite" @click="emitToggleFavorite(product)">
              <i :class="product.isFavorite ? 'fas fa-heart favorite-icon' : 'far fa-heart not-favorite-icon'"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, defineEmits } from "vue";

// **接收 `products` 作為 props**
const props = defineProps({
  products: {
    type: Array,
    required: true,
  },
});

// **向父組件傳遞收藏變更**
const emit = defineEmits(["toggle-favorite"]);

const emitToggleFavorite = (product) => {
  emit("toggle-favorite", product);
};

// **篩選 `ACTIVE` 狀態的商品**
const activeProducts = computed(() => {
  return props.products.filter((product) => product.status === "ACTIVE");
});
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.text-center {
  text-align: center;
  margin-bottom: 20px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.product-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-details {
  padding: 15px;
}

.product-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-price {
  font-size: 16px;
  color: #555;
  margin-bottom: 15px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
}

.btn {
  padding: 8px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
  font-size: 14px;
}

.btn-view {
  background-color: #000000;
  color: #fff;
  transition: background-color 0.3s ease;
}

.btn-view:hover {
  background-color: #696969;
}

.btn-favorite {
  background-color: #ff4d4d;
  color: #fff;
  transition: background-color 0.3s ease;
}

.btn-favorite:hover {
  background-color: #cc0000;
}
</style>
