<template>
  <div class="report-container">
    <h2>📊 報表分析</h2>

    <!-- 選擇報表類型 -->
    <div class="report-buttons">
      <button @click="switchReport('sales')" :class="{ active: activeReport === 'sales' }">銷售報表</button>
      <button @click="switchReport('promotion')" :class="{ active: activeReport === 'promotion' }">促銷券報表</button>
    </div>

    <!-- 銷售報表 -->
    <div v-if="activeReport === 'sales'">
      <div class="summary">
        <p>總銷售額：<strong>NT${{ totalSales }}</strong></p>
      </div>
      <canvas ref="salesChartCanvas"></canvas>
    </div>

    <!-- 促銷券報表 -->
    <div v-if="activeReport === 'promotion'">
      <div class="promotion-buttons">
        <button @click="fetchPromotionUsage('used')" :class="{ active: promotionType === 'used' }">已使用</button>
        <button @click="fetchPromotionUsage('active')" :class="{ active: promotionType === 'active' }">未使用</button>
        <button @click="fetchPromotionUsage('expired')" :class="{ active: promotionType === 'expired' }">已過期</button>
      </div>
      <canvas ref="promotionChartCanvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import axios from "axios";
import Chart from "chart.js/auto";
import axiosapi from "@/plugins/axios.js";

const activeReport = ref("sales"); // 當前選擇的報表
const promotionType = ref("used"); // 當前選擇的促銷券類型

const totalSales = ref(0); // 總銷售額
const salesChartCanvas = ref(null);
const promotionChartCanvas = ref(null);

let salesChartInstance = null; // **存儲銷售報表的 Chart 實例**
let promotionChartInstance = null; // **存儲促銷券報表的 Chart 實例**

// 🚀 切換報表類型
const switchReport = async (type) => {
  activeReport.value = type;

  await nextTick(); // **等待 Vue DOM 更新**
  
  if (type === "sales") {
    destroyChart(promotionChartInstance); // **銷毀促銷券圖表**
    fetchTotalSales();
    fetchSalesTrend();
  } else if (type === "promotion") {
    destroyChart(salesChartInstance); // **銷毀銷售圖表**
    await nextTick(); // **確保 Canvas 已經出現在 DOM 中**
    fetchPromotionUsage("used"); // 預設載入已使用的促銷券
  }
};

// 🚀 銷毀舊的 Chart
const destroyChart = (chartInstance) => {
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }
};

// 🚀 取得總銷售額
const fetchTotalSales = async () => {
  try {
    const response = await axiosapi.get("/mowmow/admin/reports/total-sales");
    totalSales.value = response.data;
  } catch (error) {
    console.error("❌ 獲取銷售總額失敗:", error);
  }
};

// 🚀 取得銷售趨勢
const fetchSalesTrend = async () => {
  try {
    await nextTick(); // **確保 Canvas 存在**
    if (!salesChartCanvas.value) {
      console.error("❌ 找不到 `salesChartCanvas`，無法繪製銷售報表！");
      return;
    }

    const response = await axiosapi.get("/mowmow/admin/reports/sales-trend");
    let salesData = response.data;
    salesData.sort((a, b) => new Date(a.date) - new Date(b.date));

    const labels = salesData.map(entry => entry.date);
    const values = salesData.map(entry => entry.totalAmount);

    destroyChart(salesChartInstance);

    salesChartInstance = new Chart(salesChartCanvas.value, {
      type: "line",
      data: {
        labels,
        datasets: [
          {
            label: "每日銷售額",
            data: values,
            borderColor: "#007bff",
            backgroundColor: "rgba(0, 123, 255, 0.1)",
            fill: true
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true }
        },
        scales: {
          x: { title: { display: true, text: "日期" } },
          y: { title: { display: true, text: "銷售額" }, beginAtZero: true }
        }
      }
    });

  } catch (error) {
    console.error("❌ 獲取銷售趨勢失敗:", error);
  }
};

// 🚀 取得促銷券統計數據
const fetchPromotionUsage = async (type) => {
  try {
    promotionType.value = type;

    await nextTick(); // ✅ 確保 Vue DOM 更新完成

    // ❗ **增加防呆判斷**
    if (!promotionChartCanvas.value) {
      console.warn("⚠️ `promotionChartCanvas` 尚未渲染，延遲執行 Chart 繪製...");
      setTimeout(() => fetchPromotionUsage(type), 300); // 🚀 **延遲 300ms 再次嘗試**
      return;
    }

    let url = "";
    if (type === "used") url = "/mowmow/admin/reports/promotion-used";
    if (type === "active") url = "/mowmow/admin/reports/promotion-active";
    if (type === "expired") url = "/mowmow/admin/reports/promotion-expired";

    const response = await axiosapi.get(url);
    let promotionData = response.data;

    promotionData.sort((a, b) => a.month.localeCompare(b.month));

    const labels = promotionData.map(entry => entry.month);
    const values = promotionData.map(entry => {
      if (type === "used") return entry.usedCount;
      if (type === "active") return entry.activeCount;
      if (type === "expired") return entry.expiredCount;
    });

    destroyChart(promotionChartInstance);

    promotionChartInstance = new Chart(promotionChartCanvas.value, {
      type: "bar",
      data: {
        labels,
        datasets: [
          {
            label: type === "used" ? "已使用促銷券" : type === "active" ? "未使用促銷券" : "已過期促銷券",
            data: values,
            backgroundColor: type === "used" ? "#28a745" : type === "active" ? "#ffc107" : "#dc3545",
            borderColor: "#333",
            borderWidth: 1
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true }
        },
        scales: {
          x: { title: { display: true, text: "月份" } },
          y: { title: { display: true, text: "促銷券數量" }, beginAtZero: true }
        }
      }
    });

  } catch (error) {
    console.error("❌ 獲取促銷券報表失敗:", error);
  }
};


// 🚀 初始化
onMounted(() => {
  fetchTotalSales();
  fetchSalesTrend();
  fetchPromotionUsage("used");
});
</script>

<style scoped>
.report-container {
  padding: 20px;
  max-width: 800px;
  margin: auto;
  text-align: center;
}

.report-buttons, .promotion-buttons {
  margin-bottom: 20px;
}

button {
  margin: 5px;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

button.active {
  background-color: #007bff;
  color: white;
}

button:hover {
  background-color: #0056b3;
}

.summary {
  font-size: 18px;
  margin-bottom: 20px;
}
</style>
