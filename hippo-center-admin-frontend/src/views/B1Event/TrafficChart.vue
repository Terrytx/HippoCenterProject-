<template>
  <div class="traffic-chart-container">
    <!-- 篩選區塊：下拉選單 -->
    <div class="filter-container">
      <label for="groupSelect">細分選項：</label>
      <select id="groupSelect" v-model="selectedGroup" @change="updateChart">
        <option value="all">網站瀏覽統計</option>
        <option value="today">今日流量（分頁統計）</option>
        <!-- 依據資料中 friendlyName 產生選項 -->
        <option v-for="group in groupOptions" :key="group" :value="group">
          {{ group }}
        </option>
      </select>
    </div>

    <!-- 圖表區塊 -->
    <div class="chart-container">
      <p v-if="noData">暫無流量數據</p>
      <canvas v-else ref="trafficChart"></canvas>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from "vue";
import { Chart } from "chart.js/auto";
import axiosapi from "@/plugins/axios.js";

export default {
  name: "TrafficChart",
  setup() {
    const trafficChartRef = ref(null); // canvas 參考
    const chartInstance = ref(null);   // Chart.js 實例
    const noData = ref(false);         // 是否有資料
    const rawData = ref([]);           // 存放 API 原始資料（只保留 mapping 中定義的項目）
    const selectedGroup = ref("all");  // 下拉選單目前的選擇

    // 定義 API pageUrl 與友善名稱 mapping
    const pageNameMapping = {
      "/api/events-user/upcoming-events": "節目預告",
      "/api/events-user/past-events": "節目回顧",
      "/api/events-user/ongoing-events": "最新節目",
      "/api/events-user/${eventId}": "節目詳情",
      "/api/events-user/search/multi": "節目搜尋"
      // 可依需求新增其他 mapping
    };

    // 過濾並在每筆資料中加入 friendlyName 欄位，
    // 僅保留 pageUrl 在 pageNameMapping 中有定義的項目
    function addFriendlyNames(data) {
      const filtered = data.filter((item) => pageNameMapping.hasOwnProperty(item.pageUrl));
      filtered.forEach((item) => {
        item.friendlyName = pageNameMapping[item.pageUrl];
      });
      return filtered;
    }

    // 依據 rawData 動態產生所有友善名稱選項（不包含 "today" 與 "all"）
    const groupOptions = computed(() => {
      const groups = new Set();
      rawData.value.forEach((item) => {
        if (item.friendlyName) {
          groups.add(item.friendlyName);
        }
      });
      return Array.from(groups);
    });

    // 資料處理：依據目前選擇進行統計
    // 若 selectedGroup 為 "today" 時，細分各 API 的今日流量（以小時為單位）
    // 否則以日期統計，並依下拉選單選項過濾（"all" 或特定 API）
    function processData() {
      if (selectedGroup.value === "today") {
        const today = new Date().toISOString().split("T")[0];
        // 根據 friendlyName 分組，每個組別統計 0～23 小時的訪問數
        const groups = {};
        rawData.value.forEach((item) => {
          if (item.visitTime) {
            const itemDate = item.visitTime.split("T")[0];
            if (itemDate === today) {
              const friendly = item.friendlyName;
              if (!friendly) return;
              const hour = new Date(item.visitTime).getHours();
              if (!groups[friendly]) {
                groups[friendly] = {};
                for (let i = 0; i < 24; i++) {
                  groups[friendly][i] = 0;
                }
              }
              groups[friendly][hour] = (groups[friendly][hour] || 0) + 1;
            }
          }
        });
        if (Object.keys(groups).length === 0) {
          return { labels: [], datasets: [] };
        }
        // X 軸標籤：0～23 小時
        const labels = [];
        for (let i = 0; i < 24; i++) {
          labels.push(`${i}時`);
        }
        // 為每個 API 產生一個 dataset
        const colors = ["#FF6384", "#36A2EB", "#4BC0C0", "#FFCE56", "#9966FF", "#FF9F40"];
        const datasets = Object.keys(groups).map((friendly, idx) => {
          const counts = [];
          for (let i = 0; i < 24; i++) {
            counts.push(groups[friendly][i] || 0);
          }
          const color = colors[idx % colors.length];
          return {
            label: friendly,
            data: counts,
            borderColor: color,
            backgroundColor: color + "33",
            fill: false,
          };
        });
        return { labels, datasets };
      } else {
        // 每日統計模式：依據日期分組
        const visitsByDate = {};
        rawData.value.forEach((item) => {
          // 若選擇 "all" 則不過濾，否則僅統計符合該 friendlyName 的資料
          if (selectedGroup.value !== "all" && item.friendlyName !== selectedGroup.value) {
            return;
          }
          if (item.visitTime) {
            const date = item.visitTime.split("T")[0];
            visitsByDate[date] = (visitsByDate[date] || 0) + 1;
          }
        });
        const labels = Object.keys(visitsByDate).sort();
        const counts = labels.map((date) => visitsByDate[date]);
        return { labels, counts };
      }
    }

    // 繪製或更新圖表
    function renderChart() {
      if (selectedGroup.value === "today") {
        const { labels, datasets } = processData();
        if (labels.length === 0 || datasets.length === 0) {
          noData.value = true;
          if (chartInstance.value) chartInstance.value.destroy();
          return;
        }
        noData.value = false;
        if (chartInstance.value) chartInstance.value.destroy();
        chartInstance.value = new Chart(trafficChartRef.value, {
          type: "line",
          data: {
            labels,
            datasets,
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              x: {
                title: {
                  display: true,
                  text: "小時",
                },
              },
              y: {
                title: {
                  display: true,
                  text: "訪問數",
                },
                beginAtZero: true,
              },
            },
            plugins: {
              tooltip: {
                mode: "index",
                intersect: false,
              },
              legend: {
                position: "top",
              },
            },
          },
        });
      } else {
        const { labels, counts } = processData();
        if (labels.length === 0) {
          noData.value = true;
          if (chartInstance.value) chartInstance.value.destroy();
          return;
        }
        noData.value = false;
        if (chartInstance.value) chartInstance.value.destroy();
        let datasetLabel = "";
        if (selectedGroup.value === "all") {
          datasetLabel = "全部訪問數";
        } else {
          datasetLabel = `訪問數 - ${selectedGroup.value}`;
        }
        chartInstance.value = new Chart(trafficChartRef.value, {
          type: "line",
          data: {
            labels,
            datasets: [
              {
                label: datasetLabel,
                data: counts,
                borderColor: "blue",
                backgroundColor: "rgba(0, 0, 255, 0.2)",
                fill: true,
              },
            ],
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              x: {
                title: {
                  display: true,
                  text: "日期",
                },
              },
              y: {
                title: {
                  display: true,
                  text: "訪問數",
                },
                beginAtZero: true,
              },
            },
            plugins: {
              tooltip: {
                mode: "index",
                intersect: false,
              },
              legend: {
                position: "top",
              },
            },
          },
        });
      }
    }

    // 當下拉選單改變時更新圖表
    function updateChart() {
      renderChart();
    }

    onMounted(async () => {
      try {
        const response = await axiosapi.get("/api/traffic/stats");
        const data = Array.isArray(response.data) ? response.data : [];
        if (data.length === 0) {
          console.warn("API 回應的流量資料為空");
          noData.value = true;
          return;
        }
        // 只保留 mapping 中定義的項目，並加入 friendlyName 欄位
        rawData.value = addFriendlyNames(data);
        renderChart();
      } catch (error) {
        console.error("API 請求失敗:", error);
        noData.value = true;
      }
    });

    return {
      trafficChart: trafficChartRef,
      noData,
      groupOptions,
      selectedGroup,
      updateChart,
    };
  },
};
</script>

<style scoped>
.traffic-chart-container {
  max-width: 1400px;
  margin: 20px auto;
  padding: 10px;
  background: #fafafa;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.filter-container label {
  margin-right: 10px;
  font-weight: bold;
  color: #333;
}

.filter-container select {
  padding: 5px 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: #fff;
  outline: none;
  transition: border-color 0.3s ease;
}

.filter-container select:focus {
  border-color: #007bff;
}

.chart-container {
  position: relative;
  height: 500px;
}
</style>
