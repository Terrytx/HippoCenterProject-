<template>
  <div class="event-management">
    <!-- 頁面標題 -->
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <h1 class="title">活動發布管理系統</h1>
        </div>
      </template>
      
      <el-form>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="活動名稱">
              <el-input
                v-model="searchQuery"
                placeholder="搜尋活動名稱..."
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="發布狀態">
              <el-radio-group v-model="publishFilter" class="filter-group">
                <el-radio-button label="all">全部</el-radio-button>
                <el-radio-button label="published">已發布</el-radio-button>
                <el-radio-button label="unpublished">未發布</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 新增統計資訊區塊 -->
        <div class="stats-container">
          <div class="stat-item published">
            <span class="label">已發布活動：</span>
            <span class="value">{{ publishedCount }} 筆</span>
          </div>
          <div class="divider"></div>
          <div class="stat-item unpublished">
            <span class="label">未發布活動：</span>
            <span class="value">{{ unpublishedCount }} 筆</span>
          </div>
        </div>
      </el-form>
    </el-card>

    <!-- 活動列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>活動列表</span>
          <span class="total-count">共 {{ filteredEvents.length }} 筆資料</span>
        </div>
      </template>

      <el-table :data="filteredEvents" stripe border>
        <el-table-column prop="eventName" label="活動名稱" min-width="200" />
        <el-table-column label="開始日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.eventStartDate) }}
          </template>
        </el-table-column>
        <el-table-column label="結束日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.eventEndDate) }}
          </template>
        </el-table-column>
        <el-table-column label="發布狀態" width="120">
          <template #default="{ row }">
            <el-tag :type="row.isPublished ? 'success' : 'warning'">
              {{ row.isPublished ? '已發布' : '未發布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button
                v-if="!row.isPublished"
                type="primary"
                link
                @click="publishEvent(row.eventId)"
              >
                發布
              </el-button>
              <el-button
                v-else
                type="danger"
                link
                @click="unpublishEvent(row.eventId)"
              >
                取消發布
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';
const events = ref([]);
const searchQuery = ref('');
const publishFilter = ref('all');

// 載入活動資料
const loadEvents = async () => {
  try {
    const response = await axiosapi.get('/api/events-admin/all');
    events.value = response.data;
  } catch (error) {
    console.error('Failed to load events:', error);
    ElMessage.error('載入活動資料失敗');
  }
};

// 發布活動  
const publishEvent = async (eventId) => {
  try {
    const result = await Swal.fire({
      title: '提示',
      text: '確定要發布此活動嗎？',
      icon: 'info',
      showCancelButton: true,
      confirmButtonText: '確定',
      cancelButtonText: '取消'
    });
    if (!result.isConfirmed) {
      return; // 直接返回而不是拋出錯誤，保持流程結構不變
    }
    await axiosapi.put(`/api/events-admin/${eventId}/publish`);
    await Swal.fire({
      icon: 'success',
      title: '活動發布成功',
      showConfirmButton: false,
      timer: 1500
    });
    await loadEvents();
  } catch (error) {
    console.error('Failed to publish event:', error);
    await Swal.fire({
      icon: 'error',
      title: '活動發布失敗'
    });
  }
};


// 取消發布活動
const unpublishEvent = async (eventId) => {
  try {
    const result = await Swal.fire({
      title: '警告',
      text: '確定要取消發布此活動嗎？',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '確定',
      cancelButtonText: '取消'
    });
    if (!result.isConfirmed) {
      return; // 直接返回，保持流程結構
    }
    await axiosapi.put(`/api/events-admin/${eventId}/unpublish`);
    await Swal.fire({
      icon: 'success',
      title: '取消發布成功',
      showConfirmButton: false,
      timer: 1500
    });
    await loadEvents();
  } catch (error) {
    console.error('Failed to unpublish event:', error);
    await Swal.fire({
      icon: 'error',
      title: '取消發布失敗'
    });
  }
};


// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('zh-TW');
};

// 篩選活動
const filteredEvents = computed(() => {
  let filtered = events.value;
  if (publishFilter.value === 'published') {
    filtered = filtered.filter(event => event.isPublished);
  } else if (publishFilter.value === 'unpublished') {
    filtered = filtered.filter(event => !event.isPublished);
  }
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(event => event.eventName.toLowerCase().includes(query));
  }
  return filtered;
});

// 已發布與未發布數量統計
const publishedCount = computed(() => events.value.filter(event => event.isPublished).length);
const unpublishedCount = computed(() => events.value.filter(event => !event.isPublished).length);

// 組件掛載時載入資料
onMounted(() => {
  loadEvents();
});
</script>

<style scoped>
.event-management {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding: 20px;
}

.title {
  font-size: 40px;
  color: #303133;
  margin: 0;
}

/* 搜尋卡片 */
.search-card {
  margin-bottom: 20px;
}

/* 統一表單 label 欄位 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

/* 篩選群組 */
.filter-group {
  margin-top: 8px;
}

/* 卡片標題區塊 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 統計資訊區塊 */
.stats-container {
  display: flex;
  align-items: center;
  margin-top: 16px;
  padding: 10px 0;
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.stat-item {
  flex: 1;
  text-align: center;
  font-size: 16px;
  color: #303133;
}

.stat-item .label {
  font-weight: 500;
  margin-right: 5px;
}

/* 分隔線 */
.divider {
  width: 1px;
  height: 30px;
  background-color: #ebeef5;
}

/* 已發布與未發布顏色 */
.stat-item.published {
  color: #47a447;
}
.stat-item.unpublished {
  color: #f56c6c;
}

/* 活動列表卡片 */
.list-card {
  margin-top: 10px;
  max-width: 2000px;
}

.total-count {
  font-size: 14px;
  color: #909399;
}

/* 調整操作欄位 */
.action-cell {
  padding-left: 10px;
}

</style>
