<template>
  <div class="dashboard">
    <div class="chart-container">
      <h1 class="title">網站流量統計</h1>
      <TrafficChart />
      <!-- 下載 CSV 按鈕 -->
      <el-button 
  type="primary" 
  @click="downloadCSV" 
  class="military-green" 
  style="margin-top: 20px;"
>
  下載 CSV
</el-button>
    </div>
  </div>
</template>

<script>
import TrafficChart from "./TrafficChart.vue";
import axiosapi from "@/plugins/axios.js";
import Swal from 'sweetalert2';

export default {  
  components: { TrafficChart },
  methods: {
    async downloadCSV() {
      try {
        // 取得 API 資料
        const response = await axiosapi.get("/api/traffic/stats");
        const data = Array.isArray(response.data) ? response.data : [];
        if (data.length === 0) {
          await Swal.fire({
            icon: 'warning',
            title: '目前無流量資料可供下載'
          });
          return;
        }
        // 定義 API pageUrl 與友善名稱 mapping
        const pageNameMapping = {
          "/api/events-user/upcoming-events": "節目預告",
          "/api/events-user/past-events": "節目回顧",
          "/api/events-user/ongoing-events": "最新節目",
          "/api/events-user/${eventId}": "節目詳情",
          "/api/events-user/search/multi": "節目搜尋",
          // 可依需求新增其他 mapping
        };
        // 只保留 mapping 中定義的項目，並加入 friendlyName 欄位
        const addFriendlyNames = (data) => {
          const filtered = data.filter((item) =>
            pageNameMapping.hasOwnProperty(item.pageUrl)
          );
          filtered.forEach((item) => {
            item.friendlyName = pageNameMapping[item.pageUrl];
          });
          return filtered;
        };
        const processedData = addFriendlyNames(data);
    
        // 轉換資料成 CSV 字串（可根據需求調整欄位）
        // 例如：輸出 pageUrl, friendlyName, visitTime 三個欄位
        const headers = ["pageUrl", "friendlyName", "visitTime"];
        const csvRows = [];
        // 加入表頭
        csvRows.push(headers.join(","));
        // 加入每一筆資料
        processedData.forEach((item) => {
          const row = headers
            .map((header) => {
              const val = item[header] ? String(item[header]).replace(/"/g, '""') : "";
              return `"${val}"`;
            })
            .join(",");
          csvRows.push(row);
        });
        const csvString = "\uFEFF" + csvRows.join("\n"); // 加上 BOM
    
        // 建立 blob 並觸發下載
        const blob = new Blob([csvString], { type: "text/csv;charset=utf-8;" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.setAttribute("href", url);
        // 設定檔名（可依需求修改）
        link.setAttribute("download", "traffic_data.csv");
        link.style.visibility = "hidden";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      } catch (error) {
        console.error("CSV 下載失敗:", error);
        await Swal.fire({
          icon: 'error',
          title: 'CSV 下載失敗'
        });
      }
    },
  },
};
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: auto;
  text-align: center;
}

.title {
  font-size: 40px;
  font-weight: bold;
  margin-bottom: 20px;
}

.chart-container {
  padding: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.military-green {
  background-color: #c80606!important;
  border-color: #c80606 !important;
}
:deep(.el-tag) {
  font-size: 16px; /* 調整文字大小 */
  padding: 8px 12px; /* 調整內邊距 */
}
</style>
