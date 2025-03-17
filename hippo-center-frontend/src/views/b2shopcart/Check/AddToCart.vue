<template>
  <div class="cart-container">
    <h2 class="text-center">購物車</h2>

    <div class="cart-content">
      <!-- 左側區域 -->
      <div class="left-section">
        <!-- 結帳金額區塊 -->
        <div class="checkout-summary card-like">
          <h5>結帳金額</h5>
          <p>商品小計: NT${{ cartTotal }}</p>
          <p>運費: +NT$100</p>
          <p><strong>應付總額: NT${{ discountedTotal }}</strong></p>
        </div>

        <!-- 促銷券區塊 -->
        <div class="promotion-section card-like">
          <h5>促銷券</h5>
          <div v-if="promotions.length > 0">
            <div v-for="promotion in promotions" :key="promotion.promotionMemberId">
              <input
                type="checkbox"
                :id="promotion.promotionMemberId"
                v-model="selectedPromotions"
                :value="promotion.promotionMemberId"
                @change="updateDiscount"
              />
              <label :for="promotion.promotionMemberId">
                {{ promotion.title }} (折扣 {{ (promotion.discountRate * 100).toFixed(0) }}% )
              </label>
            </div>
          </div>
          <p v-else>您沒有可用的促銷券</p>
        </div>
      </div>

      <!-- 右側區域 -->
      <div class="right-section">
        <div v-if="cart.length > 0" class="cart-items">
          <div class="cart-header">
            <span class="product-info">商品明細</span>
            <span class="unit-price">單價</span>
            <span class="quantity">數量</span>
            <span class="subtotal">小計</span>
            <span class="actions"></span>
          </div>
          
          <div v-for="item in cart" :key="item.cartId" class="cart-item">
            <div class="product-info">
              <img :src="item.imageUrl" alt="商品圖片" class="product-image" />
              <span class="product-name">{{ item.productName }}</span>
            </div>
            <div class="unit-price">NT${{ item.price }}</div>
            <div class="quantity">
              <!-- <button @click="updateQuantity(item, -1)" :disabled="item.quantity <= 1">-</button> -->
              <input type="number" v-model.number="item.quantity" disabled />
              <!-- <button @click="updateQuantity(item, 1)">+</button> -->
            </div>
            <div class="subtotal">NT${{ item.price * item.quantity }}</div>
            <div class="actions">
              <button class="delete-btn" @click="removeFromCart(item.productId)">✕</button>
            </div>
          </div>
        </div>
        <p v-else>購物車內沒有商品</p>

        <!-- 寄送資訊 -->
        <div class="shipping-area">
          <h5>寄送內容</h5>
          <div>
            <label for="address">地址</label>
            <input type="text" id="address" v-model="address" placeholder="請輸入地址" />
          </div>
          <div>
            <label for="phone">聯絡電話</label>
            <input type="text" id="phone" v-model="phone" placeholder="請輸入電話" />
          </div>
          <button class="checkout-btn" @click="showModal = true">結帳</button>
        </div>
      </div>
    </div>

    <!-- 替代的彈出視窗 -->
    <div v-if="showModal" class="modal-container">
      <div class="modal-content">
        <h5>確認結帳</h5>
        <p><strong>應付總額：</strong>NT${{ discountedTotal }}</p>
        <p><strong>地址：</strong>{{ address }}</p>
        <p><strong>聯絡電話：</strong>{{ phone }}</p>
        <div class="modal-actions">
          <button @click="showModal = false">取消</button>
          <button @click="checkout">確認結帳</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import Swal from "sweetalert2";
import axiosapi from "@/plugins/axios.js";

const router = useRouter();
const route = useRoute(); // ✅ 確保 `route` 變數存在
const cart = ref([]);
const promotions = ref([]);
const selectedPromotions = ref([]); // 支持多選促銷券
const address = ref("");
const phone = ref("");
const showModal = ref(false); // 控制彈出視窗的顯示

// 計算商品小計
const cartTotal = computed(() =>
  cart.value.reduce((total, item) => total + item.price * item.quantity, 0)
);

// 計算折扣後的總金額
const discountedTotal = computed(() => {
  if (selectedPromotions.value.length > 0) {
    // ✅ 正確計算折扣率
    let totalDiscountRate = selectedPromotions.value.reduce((acc, promoId) => {
      const promo = promotions.value.find(p => p.promotionMemberId === promoId);
      return promo ? acc * (1 - promo.discountRate) : acc;
    }, 1); // ✅ 初始值設為 1，避免 NaN

    return Math.round(cartTotal.value * totalDiscountRate) + 100; // ✅ 正確計算應付金額
  }
  return cartTotal.value + 100; // ✅ 沒有折扣直接加運費
});


// 獲取購物車內容
const fetchCart = async () => {
  console.log("📢 嘗試載入購物車內容...");
  const token = localStorage.getItem("authToken");
  if (!token) {
    Swal.fire({
      title: "請先登入",
      text: "您需要登入才能查看購物車",
      icon: "warning",
      confirmButtonText: "確定",
      confirmButtonColor: '#6c757d',

    });
    return;
  }
  try {
    const response = await axiosapi.get("/mowmow/user/cart/view", {
      headers: { Authorization: `Bearer ${token}` },
    });
    cart.value = response.data;
    console.log("✅ 購物車載入成功:", cart.value);
  } catch (error) {
    console.error("❌ 獲取購物車失敗:", error);
    Swal.fire({
      title: "獲取購物車失敗",
      text: "請稍後再試",
      icon: "error",
      confirmButtonText: "確定",
      confirmButtonColor: '#6c757d',

    });
  }
};



// 結帳並跳轉至 ECPay
const checkout = async () => {
  const token = localStorage.getItem("authToken");
  if (!token) {
    Swal.fire({
      title: "請先登入",
      text: "您需要登入才能結帳",
      icon: "warning",
      confirmButtonText: "確定",
      confirmButtonColor: '#6c757d',

    });
    return;
  }

  console.log(" 準備發送結帳請求...");
  const selectedPromotionId = selectedPromotions.value.length > 0 ? selectedPromotions.value[0] : null;

  try {
    const response = await axiosapi.post(
      "/mowmow/user/cart/checkout",
      {
        totalAmount: discountedTotal.value,
        address: address.value,
        phone: phone.value,
        promotionMemberId: selectedPromotionId,
      },
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    console.log("✅ 後端回應成功，收到的 ECPay 付款表單:", response.data);

    // 顯示成功提示，並跳轉到 ECPay
    Swal.fire({
      title: "結帳成功",
      text: "即將跳轉到付款頁面",
      icon: "success",
      showConfirmButton: false,
      confirmButtonColor: '#6c757d',

      timer: 2000,
    }).then(() => {
      showModal.value = false; // ✅ 關閉彈窗
      document.body.insertAdjacentHTML("beforeend", response.data);
  const formEl = document.getElementById("payForm");
  if (formEl) {
    formEl.submit();
  } else {
    console.error("找不到 payForm，無法提交表單！");
  }
});
  } catch (error) {
    console.error("❌ 結帳失敗:", error);
    Swal.fire({
      title: "結帳失敗",
      text: error.response?.data || "請稍後再試",
      icon: "error",
      confirmButtonText: "確定",
      confirmButtonColor: '#6c757d',

    });
  }
};

// 獲取促銷券
const fetchPromotions = async () => {
  const token = localStorage.getItem('authToken');
  const memberId = getMemberIdFromToken();
  if (!token || !memberId) {
    console.error('無法獲取會員 ID');
    return;
  }
  try {
    const response = await axiosapi.get(`/mowmow/user/promotions/member/${memberId}`, {
      headers: { Authorization: `Bearer ${token}` },
      params: { timestamp: new Date().getTime() } // 🔥 避免快取問題
    });

    // ✅ 確保過濾掉 USED 和 EXPIRED
    promotions.value = response.data.filter(promo => 
      promo.promotionStatus === 'NEW' || promo.promotionStatus === 'ACTIVE'
    );

    console.log("🔄 最新促銷券清單:", promotions.value); // 確保更新
  } catch (error) {
    console.error('獲取促銷券失敗:', error);
  }
};


// 更新折扣
const updateDiscount = () => {
  console.log("已選促銷券:", selectedPromotions.value);
};

// 解析會員 ID
const getMemberIdFromToken = () => {
  const token = localStorage.getItem('authToken');
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return JSON.parse(payload.sub).memberId || null;
  } catch (error) {
    console.error('解析 Token 失敗:', error);
    return null;
  }
};

// 刪除商品
const removeFromCart = async (productId) => {
  const token = localStorage.getItem("authToken");
  if (!token) {
    Swal.fire({
      title: "請先登入",
      text: "您需要登入才能刪除商品",
      icon: "warning",
      confirmButtonText: "確定",
      confirmButtonColor: '#6c757d',

    });
    return;
  }
  try {
    await axiosapi.delete(`/mowmow/user/cart/${productId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    fetchCart();
    Swal.fire({
      title: "刪除成功",
      text: "商品已從購物車中移除",
      icon: "success",
      confirmButtonText: "確定",
      confirmButtonColor: '#6c757d',

    });
  } catch (error) {
    console.error("刪除商品失敗:", error);
    Swal.fire({
      title: "刪除失敗",
      text: "請稍後再試",
      icon: "error",
      confirmButtonText: "確定",
      confirmButtonColor: '#6c757d',

    });
  }
};

// 初始化
onMounted(() => {
  console.log("🔄 `onMounted()` 被執行，開始載入購物車...");
  fetchCart(); // ✅ 先載入購物車
  fetchPromotions(); // ✅ 取得促銷券
});

</script>

<style scoped>
.cart-container {
  width: 90%;
  margin: auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.cart-content {
  display: flex;
  gap: 20px;
}

.left-section {
  width: 30%;
}

.right-section {
  width: 70%;
}

.card-like {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.cart-items {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.cart-header {
  display: grid;
  grid-template-columns: 3fr 1fr 1fr 1fr 0.5fr;
  padding: 10px 20px;
  background-color: #cecece;
  border-radius: 8px;
  margin-bottom: 10px;
  font-weight: bold;
  color: #333;
}

.cart-item {
  display: grid;
  grid-template-columns: 3fr 1fr 1fr 1fr 0.5fr;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #dee2e6;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-name {
  font-size: 0.9rem;
  color: #333;
}

.unit-price,
.subtotal {
  font-size: 0.9rem;
  color: #333;
}

.quantity {
  display: flex;
  align-items: center;
  gap: 5px;
}

.quantity button {
  width: 24px;
  height: 24px;
  border: 1px solid #dee2e6;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity button:hover {
  background-color: #f8f9fa;
}

.quantity button:disabled {
  background-color: #e9ecef;
  cursor: not-allowed;
}

.quantity input {
  width: 40px;
  text-align: center;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  padding: 2px;
}

.delete-btn {
  width: 24px;
  height: 24px;
  border: none;
  background: none;
  color: #999;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-btn:hover {
  color: #dc3545;
}

.shipping-area {
  margin-top: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.shipping-area h5 {
  margin-bottom: 15px;
  color: #333;
}

.shipping-area input {
  width: 100%;
  padding: 8px 12px;
  margin-bottom: 15px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  font-size: 0.9rem;
}

.shipping-area label {
  display: block;
  margin-bottom: 5px;
  color: #555;
}

.checkout-btn {
  width: 100%;
  padding: 12px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.checkout-btn:hover {
  background-color: #218838;
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
  padding: 25px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}

.modal-content h5 {
  margin-bottom: 15px;
  color: #333;
}

.modal-content p {
  margin-bottom: 10px;
  color: #555;
}

.modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.modal-actions button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.modal-actions button:first-child {
  background: #6c757d;
  color: white;
}

.modal-actions button:first-child:hover {
  background: #5a6268;
}

.modal-actions button:last-child {
  background: #28a745;
  color: white;
}

.modal-actions button:last-child:hover {
  background: #218838;
}

.promotion-section {
  margin-top: 20px;
}

.promotion-section h5 {
  margin-bottom: 15px;
  color: #333;
}

.promotion-section label {
  display: block;
  margin: 10px 0;
  color: #555;
}

.use-btn {
  padding: 4px 8px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  margin-left: 10px;
}

.use-btn:hover {
  background-color: #0056b3;
}
</style>