<template>
    <div class="products-container">
        <h2>產品管理</h2>
        
        <!-- 操作區域 -->
        <div class="action-bar">
            <div class="filter-section">
                <label>篩選類別：</label>
                <select v-model="selectedCategory">
                    <option value="">所有類別</option>
                    <option v-for="(info, englishName) in categoryMap" :key="englishName" :value="englishName">
                        {{ info.name }}
                    </option>
                </select>
            </div>
            <div class="button-section">
                <button @click="openProductModal(null)">新增產品</button>
            </div>
        </div>

        <!-- 產品列表 -->
        <table class="product-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>產品圖片</th>
                    <th>產品名稱</th>
                    <th>類別</th>
                    <th>價格</th>
                    <th>庫存</th>
                    <th>狀態</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
    <tr v-for="product in filteredProducts" :key="product.productId">
        <td>{{ product.productId }}</td>
        <td>
            <img :src="getProductImage(product)" alt="產品圖片" class="product-image">
        </td>
        <td>{{ product.productName }}</td>
        <td>{{ categoryMap[product.categoryName]?.name || "未知類別" }}</td>
        <td>${{ product.price }}</td>
        <td>{{ product.stock }}</td>
        <td>{{ product.status }}</td>
        <td class="action-buttons">
            <button class="edit-btn" @click="openProductModal(product)">編輯</button>
            <button 
                class="status-btn" 
                :class="{ 'active': product.status === 'ACTIVE' }"
                @click="updateProductStatus(product.productId, product.status, product.productName)"
            >
                {{ product.status === 'ACTIVE' ? '下架' : '上架' }}
            </button>
            <button class="preview-btn" @click="openPreviewModal(product)">預覽</button>
        </td>
    </tr>
</tbody>

        </table>

        <!-- 彈出視窗 -->
        <div v-if="isModalOpen" class="modal">
            <div class="modal-content">
                <h3>{{ currentProduct.productId ? "編輯產品" : "新增產品" }}</h3>

                <label>名稱：</label>
                <input v-model="currentProduct.productName" type="text">

                <label>描述：</label>
                <textarea v-model="currentProduct.description"></textarea>

                <label>類別：</label>
                <select v-model="currentProduct.categoryId">
                <option v-for="(info, englishName) in categoryMap" :key="englishName" :value="info.id">
                    {{ info.name }}
                </option>
                </select>


                <!-- <label>類別：</label>
                <select v-model="currentProduct.categoryName">
                    <option v-for="(info, englishName) in categoryMap" :key="englishName" :value="englishName">
                        {{ info.name }}
                    </option>
                </select> -->

                
                <label>價格：</label>
                <input v-model="currentProduct.price" type="number">

                <label>庫存：</label>
                <input v-model="currentProduct.stock" type="number">
                
                <!-- 產品圖片顯示 -->
                <div class="image-container">
                    <div v-for="(imageUrl, index) in currentProduct.imagePaths" :key="index" class="image-wrapper">
                        <img :src="imageUrl" alt="產品圖片" class="product-preview">
                        <button class="delete-image-btn" @click="confirmDeleteImage(index, imageUrl)">❌</button>
                    </div>
                </div>

                <label>上傳圖片：</label>
                <input type="file" multiple @change="handleImageUpload">

                <div class="modal-actions">
                    <button @click="saveProduct">確定</button>
                    <button @click="closeModal">取消</button>
                </div>
            </div>
        </div>

        <!-- 更新後的預覽 Modal -->
        <div v-if="isPreviewOpen" class="preview-modal">
            <div class="preview-modal-content">
                <!-- 左側圖片區域 -->
                <div class="preview-image-section">
                    <img :src="previewProduct.image" alt="產品圖片" class="preview-image">
                </div>
                
                <!-- 右側資訊區域 -->
                <div class="preview-info-section">
                    <div class="preview-header">
                        <h2 class="preview-title">{{ previewProduct.productName }}</h2>
                        <button class="preview-close-btn" @click="closePreviewModal">
                            <span>×</span>
                        </button>
                    </div>

                    <div class="preview-price">
                        <h3>價格</h3>
                        <p>${{ previewProduct.price }}</p>
                    </div>

                    <div class="preview-description">
                        <h3>商品描述</h3>
                        <p>{{ previewProduct.description || '暫無描述' }}</p>
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
                                :disabled="quantity >= previewProduct.stock"
                            >+</button>
                        </div>
                    </div>

                    <button class="add-to-cart-btn">
                        加入購物車
                    </button>
                </div>
            </div>
        </div>

    </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import Swal from "sweetalert2";
import axiosapi from "@/plugins/axios.js";

const products = ref([])
const isModalOpen = ref(false)
const categories = ref([]); // ✅ 新增類別列表
const currentProduct = ref({
    productId: null,
    productName: '',
    description: '',
    price: 0,
    stock: 0,
    status: 'ACTIVE',
    categoryId: null, // ✅ 類別 ID 預設為空
    imagePaths: [],
});
const selectedCategory = ref(""); // 目前選擇的類別
const categoryMap = {
    "Stationeries": { id: 1, name: "風格文具" },
    "Accessories": { id: 2, name: "設計配件" },
    "Books": { id: 3, name: "經典藏書" },
    "Boutiques": { id: 4, name: "典藏精品" },
    "Decrations": { id: 5, name: "意象擺飾" },
    "Paintings": { id: 6, name: "山水書畫" }
};

const deletedImages = ref([]); // 🚀 儲存被刪除的圖片
const newImageFiles = ref([]); // 🚀 新增圖片的檔案

// 預覽產品相關狀態
const isPreviewOpen = ref(false);
const previewProduct = ref({});
const quantity = ref(1);

// 🚀 取得產品列表
const fetchProducts = async () => {
    try {
        const response = await axiosapi.get('/mowmow/admin/products/findAll');
        products.value = response.data;
    } catch (error) {
        Swal.fire({
            title: '錯誤',
            text: '無法獲取產品列表，請稍後再試。',
            icon: 'error',
            confirmButtonColor: '#4A6741',
            confirmButtonText: '確定'
        });
        console.error(error);
    }
};

// 打開預覽彈出視窗
const openPreviewModal = (product) => {
    previewProduct.value = {
        ...product,
        image: getProductImage(product),
        stock: product.stock ?? 1 // 確保 stock 不是 undefined
    };
    quantity.value = 1; // 預設數量為 1
    isPreviewOpen.value = true;
};


// 關閉預覽彈出視窗
const closePreviewModal = () => {
    isPreviewOpen.value = false;
};

// 更新數量
const updateQuantity = (change) => {
    quantity.value += change;
    if (quantity.value < 1) quantity.value = 1;
    if (quantity.value > (previewProduct.value.stock || 1)) quantity.value = previewProduct.value.stock || 1;
};



// 🔍 計算篩選後的商品列表
const filteredProducts = computed(() => {
    if (!selectedCategory.value) {
        return products.value;
    }
    return products.value.filter(product => (product.categoryName ?? '') === selectedCategory.value);
});

// 📌 更新篩選條件
const filterProducts = () => {
    console.log(`🎯 選擇的類別: ${selectedCategory.value}`);
};

// 🚀 取得類別清單
const fetchCategories = async () => {
    try {
        const response = await axiosapi.get('/mowmow/admin/products/categories');
        categories.value = response.data;
    } catch (error) {
        Swal.fire({
            title: '錯誤',
            text: '無法獲取類別列表，請稍後再試。',
            icon: 'error',
            confirmButtonColor: '#4A6741',
            confirmButtonText: '確定'
        });
        console.error(error);
    }
};

// const getCategoryName = (categoryId) => {
//     const category = categories.value.find(cat => cat.categoryId === categoryId);
//     return category ? category.categoryName : '未知類別';
// };


// 🔍 取得產品圖片（取第一張）
const getProductImage = (product) => {
    if (product.imageUrls && product.imageUrls.length > 0) {
        return product.imageUrls[0] // 只顯示第一張
    } else {
        return '/default-image.png' // 預設圖片
    }
}

// ✨ 打開新增/編輯產品的 Modal
const openProductModal = (product) => {
    if (product) {
        currentProduct.value = {
            ...product,
            categoryId: product.categoryId, // 直接用 ID
            // categoryName: product.categoryName || null, // ✅ 預設為英文名稱
            imagePaths: product.imageUrls || [],
        };
    } else {
        currentProduct.value = {
            productId: null,
            productName: '',
            description: '',
            price: 0,
            stock: 0,
            status: 'ACTIVE',
            categoryId: null,
            // categoryName: null, // ✅ 使用 categoryName 而不是 categoryId
            imagePaths: [],
        };
    }
    isModalOpen.value = true;
};


// ❌ 關閉 Modal
const closeModal = () => {
    isModalOpen.value = false;
};

// 💾 新增或更新產品
const saveProduct = async () => {
    try {
        if (!currentProduct.value.productName || currentProduct.value.price <= 0 || currentProduct.value.stock < 0) {
            Swal.fire({
                title: '錯誤',
                text: '請輸入有效的產品資訊',
                icon: 'warning',
                confirmButtonColor: '#4A6741',
                confirmButtonText: '確定'
            });
            return;
        }

        // ✅ 使用 FormData 確保圖片能正確傳輸
        const formData = new FormData();
        formData.append('productName', currentProduct.value.productName);
        formData.append('description', currentProduct.value.description);
        formData.append('price', currentProduct.value.price);
        formData.append('stock', currentProduct.value.stock);
        formData.append('status', "INACTIVE");  // ✅ **恢復 status，並設為 INACTIVE**
        // formData.append('status', currentProduct.value.status);
        formData.append('categoryId', currentProduct.value.categoryId);
        // formData.append('categoryId', categoryMap[currentProduct.value.categoryName]?.id);

        // ✅ **處理圖片**
        if (currentProduct.value.productId) {
            console.log("🟡 這是更新產品，使用 PUT 方法");
            formData.append("data", new Blob([JSON.stringify(currentProduct.value)], { type: "application/json" }));

            // **加入要新增的圖片**
            newImageFiles.value.forEach((file) => {
                formData.append("images", file);
            });

            // **加入要刪除的圖片**
            deletedImages.value.forEach((imageUrl) => {
                formData.append("deleteImages", imageUrl);
            });

            const url = `/mowmow/admin/products/${currentProduct.value.productId}`;
            await axiosapi.put(url, formData, { headers: { "Content-Type": "multipart/form-data" } });

            Swal.fire({
                title: '成功',
                text: '產品更新成功',
                icon: 'success',
                confirmButtonColor: '#4A6741',
                confirmButtonText: '確定'
            });

        } else {
            console.log("🟢 這是新增產品，使用 POST 方法");

            // **新增產品時加入圖片**
            newImageFiles.value.forEach((file) => {
                formData.append("images", file);
            });

            const url = "/mowmow/admin/products";
            await axiosapi.post(url, formData, { headers: { "Content-Type": "multipart/form-data" } });

            Swal.fire({
                title: '成功',
                text: '產品新增成功',
                icon: 'success',
                confirmButtonColor: '#4A6741',
                confirmButtonText: '確定'
            });
        }

        fetchProducts();
        closeModal();
    } catch (error) {
        console.error("操作失敗:", error);
        Swal.fire({
            title: '錯誤',
            text: '操作失敗，請檢查後端日誌',
            icon: 'error',
            confirmButtonColor: '#4A6741',
            confirmButtonText: '確定'
        });
    }
};




// 更新產品狀態
const updateProductStatus = async (productId, currentStatus, productName) => {
    try {
        const newStatus = currentStatus === "ACTIVE" ? "INACTIVE" : "ACTIVE";
        const confirmMessage = `確定要將「${productName}」設為 ${newStatus === "ACTIVE" ? "上架" : "下架"} 嗎？`;

        // 🚀 SweetAlert2 確認對話框
        Swal.fire({
            title: "更新產品狀態",
            text: confirmMessage,
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#4A6741",
            cancelButtonColor: "#dc3545",
            confirmButtonText: "確定",
            cancelButtonText: "取消"
        }).then(async (result) => {
            if (result.isConfirmed) {
                // 🚀 發送請求更新產品狀態
                await axiosapi.put(`/mowmow/admin/products/${productId}/status`, { status: newStatus });

                // ✅ 狀態更新成功提示
                Swal.fire({
                    title: "更新成功",
                    text: `產品「${productName}」狀態更新為 ${newStatus}`,
                    icon: "success",
                    confirmButtonColor: "#4A6741",
                    confirmButtonText: "確定"
                });

                fetchProducts(); // 重新獲取產品列表
            }
        });
    } catch (error) {
        console.error("無法更新產品狀態:", error);
        Swal.fire({
            title: "更新失敗",
            text: "無法更新產品狀態，請稍後再試",
            icon: "error",
            confirmButtonColor: "#4A6741",
            confirmButtonText: "確定"
        });
    }
};


const handleImageUpload = (event) => {
    const files = event.target.files;

    if (!files.length) {
        return;
    }

    // **確保 `newImageFiles` 為空，避免重複添加**
    newImageFiles.value = [];

    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        newImageFiles.value.push(file);
    }

    console.log("🖼️ 準備上傳的圖片文件：", newImageFiles.value);
};


// ⚠️ 管理員手動刪除圖片
// const confirmDeleteImage = async (index, imageUrl) => {
//     console.log("🗑️ 即將刪除圖片：", imageUrl);

//     if (window.confirm("⚠️ 確定刪除此圖片嗎？")) {
//         try {
//             const encodedUrl = encodeURIComponent(imageUrl);
//             await axiosapi.delete(`/mowmow/admin/products/image?imageUrl=${encodedUrl}`);

//             alert("✅ 圖片刪除成功");

//             // **✅ 立即刷新圖片**
//             refreshProductImages(currentProduct.value.productId);
//         } catch (error) {
//             console.error("❌ 刪除圖片失敗:", error);
//             alert("❌ 刪除圖片失敗");
//         }
//     }
// };

// ⚠️ 管理員手動刪除圖片
const confirmDeleteImage = async (index, imageUrl) => {
    console.log("🗑️ 即將刪除圖片：", imageUrl);

    // ✅ 使用 SweetAlert2 來確認刪除
    const result = await Swal.fire({
        title: "確定刪除此圖片嗎？",
        text: "此操作無法復原！",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#4A6741",
        cancelButtonColor: "#d33",
        confirmButtonText: "確定刪除",
        cancelButtonText: "取消"
    });

    if (result.isConfirmed) {
        try {
            const encodedUrl = encodeURIComponent(imageUrl);
            await axiosapi.delete(`/mowmow/admin/products/image?imageUrl=${encodedUrl}`);

            Swal.fire({
                title: "刪除成功",
                text: "圖片已成功刪除",
                icon: "success",
                confirmButtonColor: "#4A6741",
                confirmButtonText: "確定"
            });

            // **✅ 立即刷新圖片**
            refreshProductImages(currentProduct.value.productId);
        } catch (error) {
            console.error("刪除圖片失敗:", error);
            Swal.fire({
                title: "刪除失敗",
                text: "無法刪除圖片，請稍後再試",
                icon: "error",
                confirmButtonColor: "#4A6741",
                confirmButtonText: "確定"
            });
        }
    }
};



const refreshProductImages = async (productId) => {
    try {
        const response = await axiosapi.get(`/mowmow/admin/products/${productId}`);
        currentProduct.value.imagePaths = response.data.imageUrls || [];
        console.log("✅ 已更新圖片列表:", currentProduct.value.imagePaths);
    } catch (error) {
        console.error("無法刷新圖片:", error);
        
        // ✅ 使用 SweetAlert2 來顯示錯誤訊息
        Swal.fire({
            title: "刷新圖片失敗",
            text: "無法獲取最新圖片，請稍後再試",
            icon: "error",
            confirmButtonColor: "#4A6741",
            confirmButtonText: "確定"
        });
    }
};



// ⏳ 頁面載入時讀取類別清單
onMounted(() => {
    fetchProducts();
    fetchCategories(); // 加載類別清單
});
</script>

<style scoped>
.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    padding: 30px;
    border-radius: 8px;
    width: 400px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-content h3 {
    margin: 0 0 20px 0;
    color: #333;
    font-size: 1.5rem;
    text-align: center;
}

.modal-content label {
    display: block;
    margin-bottom: 8px;
    color: #555;
    font-weight: 500;
}

.modal-content input,
.modal-content select {
    width: 100%;
    padding: 8px 12px;
    margin-bottom: 20px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
}

.modal-content input:focus,
.modal-content select:focus {
    outline: none;
    border-color: #4a90e2;
    box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.modal-content input[type="number"] {
    appearance: none;
    -moz-appearance: textfield;
}

.modal-content input[type="number"]::-webkit-outer-spin-button,
.modal-content input[type="number"]::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

.modal-actions {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 20px;
}

.modal-actions button {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.modal-actions button:first-child {
    background-color: #4a90e2;
    color: white;
}

.modal-actions button:first-child:hover {
    background-color: #357abd;
}

.modal-actions button:last-child {
    background-color: #e0e0e0;
    color: #333;
}

.modal-actions button:last-child:hover {
    background-color: #d0d0d0;
}

/* 其他現有樣式保持不變 */
.products-container {
    padding: 20px;
    max-width: 1200px;
    margin: auto;
}

.action-bar {
    display: flex;
    justify-content: space-between; /* 改用 space-between */
    gap: 10px;
    margin-bottom: 20px;
}

/* 新增篩選選單的樣式 */
.action-bar select {
    padding: 8px 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    min-width: 100px;
}

.action-bar select:focus {
    outline: none;
    border-color: #4a90e2;
    box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

/* 調整按鈕樣式以符合之前的設計 */
.action-bar button {
    background-color: #37ca5a;  /* 使用與之前相同的綠色 */
    color: white;
    padding: 8px 12px;
    border-radius: 4px;
    border: none;
    cursor: pointer;
    font-size: 14px;
}

.action-bar button:hover {
    opacity: 0.8;
}

.product-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    background: white;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.product-table th, 
.product-table td {
    border: 1px solid #ddd;
    padding: 12px;
    text-align: center;
}

.product-table th {
    background-color: #f8f9fa;
    font-weight: 600;
    color: #333;
}

.product-table tr:hover {
    background-color: #f8f9fa;
}

.product-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
}

.action-buttons {
    display: flex;
    gap: 8px;
    justify-content: center;
}

.action-buttons button {
    padding: 6px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.2s;
}

.edit-btn {
    background-color: #4a90e2;
    color: white;
}

.edit-btn:hover {
    background-color: #357abd;
}

.status-btn {
    background-color: #dc3545;
    color: white;
}

.status-btn:hover {
    background-color: #c82333;
}

.status-btn.active {
    background-color: #37ca5a;
}

.status-btn.active:hover {
    background-color: #10ce39;
}

.product-preview{
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 10px;
}

.preview-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1100;
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

.preview-close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #666;
    padding: 0.5rem;
}

.preview-close-btn:hover {
    color: #333;
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
    background: #28a745;
    color: white;
    border: none;
    padding: 1rem;
    border-radius: 4px;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.2s;
}

.add-to-cart-btn:hover {
    background: #218838;
}


</style>
