<template>
    <div class="orders-container">
        <h2>訂單管理</h2>

        <!-- 操作區域 -->
        <div class="action-bar">
            <label>篩選狀態：</label>
            <select v-model="selectedStatus">
                <option value="">所有訂單</option>
                <option value="PAID">已付款</option>
                <option value="SHIPPED">已出貨</option>
                <option value="DELIVERED">已送達</option>
                <option value="CANCELLED">已取消</option>
            </select>
            <label>訂單日期：</label>
            <input type="date" v-model="selectedStartDate" />
            <span>至</span>
            <input type="date" v-model="selectedEndDate" />
        </div>

        <!-- 訂單列表 -->
        <table class="order-table">
            <thead>
                <tr>
                    <th>訂單編號</th>
                    <th>總金額</th>
                    <th>訂單狀態</th>
                    <th>地址</th>
                    <th>聯絡電話</th>
                    <th>促銷活動</th>
                    <th>建立時間</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="order in filteredOrders" :key="order.orderId">
                    <td>{{ order.merchantTradeNo }}</td>
                    <td>${{ order.totalAmount }}</td>
                    <td>{{ order.orderStatus }}</td>
                    <td>{{ order.orderAddress }}</td>
                    <td>{{ order.phone }}</td>
                    <td>{{ order.promotionMember }}</td>
                    <td>{{ new Date(order.createdAt).toLocaleString() }}</td>
                    <td class="action-buttons">
                        <button class="btn btn-view" @click="fetchOrderDetails(order.orderId)">查看詳情</button>
                        <button 
                            class="btn btn-update" 
                            v-if="order.orderStatus === 'PAID' || order.orderStatus === 'SHIPPED'" 
                            @click="updateOrderStatus(order)">
                            {{ order.orderStatus === 'PAID' ? '已出貨' : '已送達' }}
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- 📌 訂單詳情 Modal -->
        <div v-if="isModalOpen" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>訂單詳情</h3>
                    <button class="btn btn-close" @click="closeModal">關閉</button>
                </div>
                <table class="details-table">
                    <thead>
                        <tr>
                            <th>圖片</th>
                            <th>商品名稱</th>
                            <th>數量</th>
                            <th>單價</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in currentOrderDetails" :key="item.productId">
                            <td><img :src="item.imageUrl || '/default-product.jpg'" class="product-image" /></td>
                            <td>{{ item.productName }}</td>
                            <td>{{ item.quantity }}</td>
                            <td>${{ item.price }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import Swal from "sweetalert2";
import axiosapi from "@/plugins/axios.js";

// ✅ 取得今天的日期
const today = new Date();
const sevenDaysAgo = new Date();
sevenDaysAgo.setDate(today.getDate() - 7); // ✅ 設定 7 天前日期

const orders = ref([]);
const selectedStatus = ref("");
const selectedStartDate = ref(sevenDaysAgo.toISOString().split("T")[0]);  
const selectedEndDate = ref(today.toISOString().split("T")[0]);          
const isModalOpen = ref(false);
const currentOrderDetails = ref([]);

// 🚀 取得訂單列表
const fetchOrders = async () => {
    try {
        const response = await axiosapi.get("/mowmow/admin/orders/findAll");
        orders.value = response.data.map(order => ({
            orderId: order.orderId,
            merchantTradeNo: order.merchantTradeNo,
            orderStatus: order.orderStatus,
            totalAmount: order.totalAmount,
            orderAddress: order.orderAddress,
            phone: order.phone,
            promotionMember: order.promotionMember || "無促銷",
            createdAt: order.createdAt
        }));
    } catch (error) {
        Swal.fire({
            title: "獲取訂單列表失敗",
            text: "請稍後再試",
            icon: "error",
            confirmButtonColor: "#4A6741",
        });
        console.error(error);
    }
};

// 🚀 取得單筆訂單的商品資訊
const fetchOrderDetails = async (orderId) => {
    try {
        const response = await axiosapi.get(`/mowmow/admin/orders/${orderId}/details`);
        currentOrderDetails.value = response.data;
        isModalOpen.value = true;
    } catch (error) {
        Swal.fire({
            title: "獲取訂單詳情失敗",
            text: "請稍後再試",
            icon: "error",
            confirmButtonColor: "#4A6741",
        });
        console.error(error);
    }
};

// 🚀 關閉彈出視窗
const closeModal = () => {
    isModalOpen.value = false;
    currentOrderDetails.value = [];
};

// 🔍 篩選訂單
const filteredOrders = computed(() => {
    return orders.value.filter(order => {
        const orderDate = order.createdAt.split("T")[0];
        return (
            (!selectedStatus.value || order.orderStatus === selectedStatus.value) &&
            orderDate >= selectedStartDate.value && orderDate <= selectedEndDate.value
        );
    });
});

// ✨ 更新訂單狀態
const updateOrderStatus = async (order) => {
    try {
        let newStatus = "";

        if (order.orderStatus === "PAID") {
            newStatus = "SHIPPED";
        } else if (order.orderStatus === "SHIPPED") {
            newStatus = "DELIVERED";
        } else {
            Swal.fire({
                title: "無法更新訂單",
                text: "只有已付款的訂單可標記為『已出貨』",
                icon: "warning",
                confirmButtonColor: "#4A6741",
            });
            return;
        }

        const result = await Swal.fire({
            title: `確定要將訂單 ${order.merchantTradeNo} 設為 ${newStatus} 嗎？`,
            icon: "question",
            showCancelButton: true,
            confirmButtonColor: "#4A6741",
            cancelButtonColor: "#d33",
            confirmButtonText: "確定",
            cancelButtonText: "取消",
        });

        if (result.isConfirmed) {
            await axiosapi.put(`/mowmow/admin/orders/${order.orderId}/status`, { status: newStatus });
            Swal.fire({
                title: "訂單狀態更新成功",
                text: `已將訂單設為 ${newStatus}`,
                icon: "success",
                confirmButtonColor: "#4A6741",
            });
            fetchOrders();
        }
    } catch (error) {
        Swal.fire({
            title: "更新訂單狀態失敗",
            text: "請稍後再試",
            icon: "error",
            confirmButtonColor: "#4A6741",
        });
        console.error(error);
    }
};

onMounted(fetchOrders);
</script>


<style scoped>
/* 🚀 訂單管理 */
.orders-container {
    padding: 20px;
    max-width: 1200px;
    margin: auto;
}

.action-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

.action-bar select {
    padding: 8px 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    min-width: 100px;
}

/* 日期選擇器也要統一樣式 */
.action-bar input[type="date"] {
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
}

.order-table {
    width: 100%;
    border-collapse: collapse;
    background: white;
}

.order-table th, .order-table td {
    border: 1px solid #ddd;
    padding: 10px;
    text-align: center;
}

.order-table th {
    background-color: #f8f9fa;
}

/* 🚀 按鈕樣式 */
.btn {
    padding: 6px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.btn-view {
    background-color: #4a90e2;
    color: white;
}

.btn-update {
    background-color: #37ca5a;
    color: white;
    margin-left: 10px;
}

.btn-close {
    background-color: #37ca5a;
    color: white;
    margin-top: 10px;
}

.btn:hover {
    opacity: 0.8;
}

/* 🚀 訂單詳情 Modal */
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
}

.modal-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    width: 500px;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.modal-header h3 {
    margin: 0;
}

.btn-close {
    background-color: #28a745;
    color: white;
}

.product-image {
    width: 50px;
    height: 50px;
    object-fit: cover;
}
</style>
