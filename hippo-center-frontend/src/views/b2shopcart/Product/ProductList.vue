<template>
  <div>
    <!-- 分类导航 -->
    <ShopSidebar @category-selected="handleCategorySelected" />

    <div class="content">
      <div class="main-content">
        
        <!-- 推荐商品（隨機 3 個） -->
        <div id="RecommendedProducts" class="section" v-if="showRecommendedProducts">
          <RecommendedProducts :products="randomRecommendedProducts" @toggle-favorite="toggleFavorite" />
        </div>
        
        <!-- 商品列表 -->
        <!-- 動態分類標題 -->
        <h2 class="text-center">{{ currentCategoryName }}</h2>
        <div id="Products" class="section-products">
          <Products :products="products" @toggle-favorite="toggleFavorite" />
        </div>
      </div>
    </div>

    <!-- 付款成功通知 -->
    <div id="paymentSuccessModal" class="modal-container" v-if="showPaymentModal">
      <div class="modal-content">
        <div class="modal-header">
          <h5>付款成功</h5>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <p>您的付款已成功，感謝您的購買！</p>
        </div>
        <div class="modal-footer">
          <button class="confirm-btn" @click="closeModal">確定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";
import ShopSidebar from "@/components/navbar/b2shopcart/ShopSidebar.vue";
import RecommendedProducts from "../Product/ProductCards/RecommendedProducts.vue";
import Products from "../Product/ProductCards/Products.vue";
import Swal from "sweetalert2";
import axiosapi from "@/plugins/axios.js";

const router = useRouter();
const route = useRoute();
const products = ref([]); 
const showRecommendedProducts = ref(true); 
const showPaymentModal = ref(false); 
const categoryId = ref(null); 
const currentCategoryName = ref("所有商品"); // ✅ **新增變數來存分類名稱**

// **分類名稱對應表**
const categoryMap = {
  "所有商品": null,
  "風格文具": 1,
  "設計配件": 2,
  "經典藏書": 3,
  "典藏精品": 4,
  "意象擺飾": 5,
  "山水書畫": 6,
};

// **隨機選擇 3 個推薦商品**
const randomRecommendedProducts = computed(() => {
  return products.value.length > 3
    ? [...products.value].sort(() => Math.random() - 0.5).slice(0, 3)
    : products.value;
});

// **取得 JWT Token**
const getToken = () => {
  const token = localStorage.getItem("authToken");
  return token ? `Bearer ${token}` : null;
};

// **取得收藏列表**
const fetchFavorites = async () => {
  try {
    let token = getToken();
    if (!token) return [];

    // **修正 Bearer 重複問題**
    if (token.startsWith("Bearer Bearer ")) {
      token = token.substring(14);
    }

    console.log("🛠 發送 API 時的 Token:", token);

    const response = await axiosapi.get("/secure/mylove/P", {
      headers: { Authorization: token }, 
    });

    console.log("✅ 收藏列表 API 回應:", response.data);

    return response.data.list?.map((item) => item.myloveId) || [];
    
  } catch (error) {
    console.error("❌ 獲取收藏列表失敗:", error);
    return [];
  }
};

// **取得商品，並合併收藏狀態**
const fetchProducts = async () => {
  try {
    const response = await axiosapi.post("/mowmow/user/products/find", categoryId.value ? { categoryId: categoryId.value } : {});
    
    const favoriteList = await fetchFavorites(); 
    // ✅ 確保 `isFavorite` 以 `productId` 正確匹配
    products.value = response.data.list.map((product) => ({
      ...product,
      image: product.imageUrls?.[0] || "/images/default-product.jpg",
      isFavorite: favoriteList.includes(product.productId), 
    }));

  } catch (error) {
    console.error("❌ 獲取商品數據失敗:", error);
  }
};

// 修改 toggleFavorite 函數
const toggleFavorite = async (product) => {
  try {
    const token = getToken();
    if (!token) {
      Swal.fire({
        title: "請先登入",
        text: "您需要登入才能收藏商品",
        icon: "warning",
        confirmButtonColor: "#6c757d",
      });
      return;
    }

    // 先更新UI狀態
    product.isFavorite = !product.isFavorite;

    const response = await axiosapi.post(
      `/secure/mylove/toggle/P`,
      { 
        myloveId: product.productId,
        myloveType: 'P'
      },
      { headers: { Authorization: token } }
    );

    // 檢查響應
    if (response.data.message === "add" || response.data.message === "remove") {
      Swal.fire({
        title: response.data.message === "add" ? "收藏成功" : "已移除收藏",
        icon: "success",
        confirmButtonColor: "#6c757d",
      });
    } else {
      throw new Error("Unexpected response");
    }

  } catch (error) {
    console.error("❌ 收藏錯誤:", error);
    
    // 發生錯誤時恢復UI狀態
    product.isFavorite = !product.isFavorite;

    Swal.fire({
      title: "錯誤",
      text: "收藏操作失敗，請稍後再試",
      icon: "error",
      confirmButtonColor: "#6c757d",
    });
  }
};

// **分類篩選**
const handleCategorySelected = (categoryName) => {
  categoryId.value = categoryMap[categoryName] || null;
  showRecommendedProducts.value = categoryName === "所有商品";

  currentCategoryName.value = categoryName; // ✅ **更新分類名稱**
  fetchProducts();
};

// **檢查付款狀態**
const checkPaymentSuccess = () => {
  let paymentStatus = route.query.payment;
  if (paymentStatus === "success") {
    showPaymentModal.value = true;
    setTimeout(() => {
      closeModal();
    }, 3000);
  }
};

// **關閉付款通知**
const closeModal = () => {
  showPaymentModal.value = false;
  router.replace("/shopcart");
};

// **初始化**
onMounted(() => {
  fetchProducts();
  setTimeout(checkPaymentSuccess, 500);
});
</script>

<style scoped>
/* 基本布局 */
.main-content {
  margin-left: 22%;
  margin-top: 1%;
}

.text-center {
  text-align: center;
  margin-bottom: 0px;
}

.section-products {
  scroll-margin-top: 11vh;
  min-height: 30vh;
}

.section {
  scroll-margin-top: 5vh;
  min-height: auto;
  margin-bottom: -300px;
}

/* 模态框样式 */
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
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  text-align: center;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.confirm-btn {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.confirm-btn:hover {
  background-color: #0056b3;
}
</style>
