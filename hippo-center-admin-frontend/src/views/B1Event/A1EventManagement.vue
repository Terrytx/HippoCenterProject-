<template>
  <div class="min-h-screen bg-gray-50">
    <div class="px-4 py-4">
      <!-- 標題和按鈕樣式 -->

      <!-- 搜尋和篩選區塊 -->
      <el-card class="search-card" style="margin-bottom: 16px">
        <template #header>
          <div class="card-header">
            <h1 class="text-xl font-bold text-gray-800">活動管理系統</h1>
          </div>
        </template>
        <el-form>
          <el-row :gutter="20">
            <!-- 文字搜尋：活動名稱 -->
            <el-col :span="8">
              <el-form-item label="活動名稱">
                <el-input
                  v-model="searchQuery"
                  placeholder="搜尋活動名稱..."
                  clearable
                />
              </el-form-item>
            </el-col>

            <!-- 發布狀態 -->
            <el-col :span="8">
              <el-form-item label="發布狀態">
                <el-radio-group v-model="publishFilter" class="filter-group">
                  <el-radio-button value="all">全部</el-radio-button>
                  <el-radio-button value="published">已發布</el-radio-button>
                  <el-radio-button value="unpublished">未發布</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>

            <!-- 新增活動按鈕 -->
            <el-col :span="8" class="flex items-center">
              <el-button
                class="common-button"
                type="primary"
                @click="dialogVisible = true"
                :icon="Plus"
              >
                新增活動
              </el-button>
            </el-col>
          </el-row>

          <!-- 日期篩選區 -->
          <el-row :gutter="20" class="mt-4">
            <el-col :span="8">
              <el-form-item label="開始日期">
                <el-date-picker
                  v-model="dateRange.startDate"
                  type="date"
                  placeholder="選擇開始日期"
                  clearable
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="結束日期">
                <el-date-picker
                  v-model="dateRange.endDate"
                  type="date"
                  placeholder="選擇結束日期"
                  clearable
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 活動清單表格 -->
      <el-card class="box-card">
        <el-table :data="filteredEvents" style="width: 100%" :border="true" stripe>
          <el-table-column prop="eventId" label="活動ID" min-width="80" />
          <el-table-column prop="eventName" label="活動名稱" min-width="120" />
          <el-table-column prop="eventCategoryName" label="分類" min-width="100" />
          <el-table-column prop="venueName" label="場地" min-width="100" />
          <el-table-column prop="eventPrice" label="價格" min-width="80" />
          <el-table-column label="開始日期" min-width="100">
            <template #default="scope">
              {{ formatDate(scope.row.eventStartDate) }}
            </template>
          </el-table-column>
          <el-table-column label="結束日期" min-width="100">
            <template #default="scope">
              {{ formatDate(scope.row.eventEndDate) }}
            </template>
          </el-table-column>
          <el-table-column label="發布狀態" min-width="90">
            <template #default="scope">
              <el-tag
                :type="scope.row.isPublished ? 'success' : 'warning'"
                effect="plain"
              >
                {{ scope.row.isPublished ? "已發布" : "未發布" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="280" fixed="right" align="center">
            <template #default="scope">
              <div class="flex justify-center items-center">
                <el-button
                  class="common-button"
                  text
                  type="primary"
                  @click="previewEvent(scope.row)"
                >
                  預覽
                </el-button>
                <el-divider direction="vertical" />
                <el-button
                  class="common-button"
                  text
                  :type="scope.row.isPublished ? 'warning' : 'success'"
                  @click="togglePublishStatus(scope.row)"
                >
                  {{ scope.row.isPublished ? "取消發布" : "發布" }}
                </el-button>
                <el-divider direction="vertical" />
                <el-button
                  class="common-button"
                  text
                  type="info"
                  @click="editEvent(scope.row)"
                >
                  編輯
                </el-button>
                <el-divider direction="vertical" />
                <el-button
                  class="common-button"
                  text
                  type="danger"
                  @click="deleteEvent(scope.row.eventId)"
                >
                  刪除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 添加/編輯活動對話框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="isEditing ? '編輯活動' : '新增活動'"
        width="80%"
        :before-close="handleClose"
        destroy-on-close
      >
        <A1EventForm
          :form-data="formData"
          :is-editing="isEditing"
          @submit-success="handleSubmitSuccess"
        />
      </el-dialog>

      <!-- 預覽對話框 (自動縮至剛好配合 preview-content) -->
      <el-dialog
        v-model="previewDialogVisible"
        title="活動預覽"
        class="preview-dialog"
        destroy-on-close
      >
        <div class="preview-content">
          <!-- 使用自訂結構顯示卡片 -->
          <div v-if="selectedEvent?.image" class="custom-card mb-6">
            <img
              :src="selectedEvent?.image"
              :alt="selectedEvent?.eventName || '預設活動圖片'"
              class="custom-card-img"
              @error="onImageError"
            />
            <div class="custom-card-body">
              <p class="custom-card-text">
                {{ selectedEvent?.eventCategoryName }}
              </p>
              <h5 class="custom-card-title">{{ selectedEvent?.eventName }}</h5>
              <p class="custom-card-text">
                活動場地：{{ selectedEvent?.venueName }}
              </p>
              <p class="custom-card-text">
                活動日期：
                {{ formatDate(selectedEvent?.eventStartDate) }} ~
                {{ formatDate(selectedEvent?.eventEndDate) }}
              </p>
            </div>
          </div>

          <div class="mt-6 flex justify-center space-x-4">
            <el-button
              class="common-button"
              v-if="!selectedEvent?.isPublished"
              type="success"
              @click="publishFromPreview"
            >
              發布活動
            </el-button>
            <el-button class="common-button" @click="previewDialogVisible = false">
              關閉預覽
            </el-button>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import Swal from 'sweetalert2';
import { ref, reactive, computed, onMounted } from "vue";
import {
  ElButton,
  ElDialog,
  ElMessageBox,
  ElMessage,
  ElTable,
  ElTableColumn,
  ElCard,
  ElTag,
  ElDivider,
  ElForm,
  ElRow,
  ElCol,
  ElInput,
  ElRadioGroup,
  ElRadioButton,
  ElDatePicker,
} from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import axiosapi from "@/plugins/axios.js";
import A1EventForm from "./A1EventForm.vue";

// 當圖片載入失敗時替換成預設圖片
const onImageError = (event) => {
  event.target.src = "/src/assets/images/default.jpg";
};

const dialogVisible = ref(false);
const previewDialogVisible = ref(false);
const events = ref([]);
const isEditing = ref(false);
const selectedEvent = ref(null);

// 搜尋與篩選相關變數
const searchQuery = ref("");
const publishFilter = ref("all");

// 日期篩選 reactive 狀態
const dateRange = reactive({
  startDate: null,
  endDate: null,
});

// 根據搜尋與篩選條件計算過濾後的活動列表
const filteredEvents = computed(() => {
  return events.value.filter((event) => {
    const nameMatch = event.eventName
      .toLowerCase()
      .includes(searchQuery.value.toLowerCase());
    let statusMatch = true;
    if (publishFilter.value === "published") {
      statusMatch = event.isPublished;
    } else if (publishFilter.value === "unpublished") {
      statusMatch = !event.isPublished;
    }
    let dateMatch = true;
    const eventStart = new Date(event.eventStartDate);
    const eventEnd = new Date(event.eventEndDate);
    if (dateRange.startDate) {
      const filterStart = new Date(dateRange.startDate);
      if (eventEnd < filterStart) {
        dateMatch = false;
      }
    }
    if (dateMatch && dateRange.endDate) {
      const filterEnd = new Date(dateRange.endDate);
      if (eventStart > filterEnd) {
        dateMatch = false;
      }
    }
    return nameMatch && statusMatch && dateMatch;
  });
});

const formData = reactive({
  eventId: null,
  eventName: "",
  eventCategoryId: "",
  venueId: "",
  eventDescription1: "",
  eventPrice: "",
  eventStartDate: "",
  eventEndDate: "",
  isPublished: false,
  imagePaths: [],
});

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-TW");
};

const previewEvent = (event) => {
  selectedEvent.value = event;
  previewDialogVisible.value = true;
};

const publishFromPreview = async () => {
  try {
    await togglePublishStatus(selectedEvent.value);
    previewDialogVisible.value = false;
  } catch (error) {
    console.error("Error publishing from preview:", error);
  }
};

const togglePublishStatus = async (event) => {
  try {
    const result = await Swal.fire({
      title: `確定要${event.isPublished ? "取消發布" : "發布"}嗎？`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '確定',
      cancelButtonText: '取消',
      didOpen: () => {
        const swalContainer = Swal.getPopup().parentElement;
        if (swalContainer) {
          swalContainer.style.zIndex = 5000;
        }
      }
    });
    if (!result.isConfirmed) return;
    const action = event.isPublished ? "unpublish" : "publish";
    await axiosapi.put(`/api/events-admin/${event.eventId}/${action}`);
    ElMessage.success(`活動${event.isPublished ? "取消發布" : "發布"}成功`);
    await fetchEvents();
  } catch (error) {
    console.error("Error toggling publish status:", error);
    ElMessage.error(`${event.isPublished ? "取消發布" : "發布"}失敗`);
  }
};

const fetchEvents = async () => {
  try {
    const response = await axiosapi.get(`/api/events-admin/all`);
    events.value = response.data.map((event) => {
      return {
        ...event,
        image: event.images?.[0]?.imageUrl || "/src/assets/images/default.jpg",
      };
    });
  } catch (error) {
    console.error("後台活動取得錯誤:", error);
    ElMessage.error("後台獲取活動列表失敗");
  }
};

const handleClose = (done) => { 
  Swal.fire({
    target: document.body,
    title: "提示",
    text: "確定要關閉嗎？未保存的數據將會丟失",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "確定",
    cancelButtonText: "取消",
    didOpen: () => {
      const swalContainer = Swal.getPopup().parentElement;
      if (swalContainer) {
        swalContainer.style.zIndex = 3000;
      }
    }
  })
    .then((result) => {
      if (result.isConfirmed) {
        resetForm();
        done();
      }
    })
    .catch(() => {
      // 取消關閉
    });
};

const resetForm = () => {
  isEditing.value = false;
  Object.keys(formData).forEach((key) => {
    formData[key] =
      key === "imagePaths" ? [] : key === "isPublished" ? false : "";
  });
};

const editEvent = (event) => {
  isEditing.value = true;
  Object.assign(formData, event);
  dialogVisible.value = true;
};

const deleteEvent = async (eventId) => {
  try {
    const result = await Swal.fire({
      title: "提示",
      text: "確定要刪除此活動？",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "確定",
      cancelButtonText: "取消"
    });
    if (!result.isConfirmed) {
      return;
    }
    await axiosapi.delete(`/api/events-admin/${eventId}`);
    await Swal.fire({
      icon: "success",
      title: "活動已刪除",
      showConfirmButton: false,
      timer: 1500
    });
    await fetchEvents();
  } catch (error) {
    console.error("Error deleting event:", error);
    await Swal.fire({
      icon: "error",
      title: "刪除失敗"
    });
  }
};

const handleSubmitSuccess = async () => {
  dialogVisible.value = false;
  await fetchEvents();
  resetForm();
};

onMounted(() => {
  fetchEvents();
});
</script>

<style>
/* 統一按鈕風格 */
.common-button {
  font-size: 14px;
  margin: 0 4px;
}

:deep(.el-card) {
  margin-bottom: 10px;
  border: 1px solid #e5e7eb;
}

:deep(.el-table) {
  --el-table-border-color: #e5e7eb;
  --el-table-header-bg-color: #f9fafb;
  --el-table-row-hover-bg-color: #f3f4f6;
}

:deep(.el-table th) {
  background-color: #f9fafb;
  font-weight: 600;
  color: #374151;
  padding: 8px;
  height: 40px;
}

:deep(.el-table td) {
  padding: 4px 8px;
  height: 40px;
}

:deep(.el-table .cell) {
  padding: 0 8px;
  line-height: 1.5;
}

:deep(.el-tag) {
  border: none;
}

:deep(.el-tag--success) {
  background-color: #ecfdf5;
  color: #047857;
}

:deep(.el-tag--warning) {
  background-color: #fff7ed;
  color: #c2410c;
}

/* 調整預覽對話框，使其縮至剛好配合 preview-content */
.preview-dialog .el-dialog {
  width: auto !important;
  max-width: none !important;
  margin: 0;
  display: inline-block;
}

.preview-dialog :deep(.el-dialog__body) {
  padding: 16px;
}

.preview-dialog .preview-content {
  max-height: 150vh;
  overflow-y: auto;
  margin: 0;
  text-align: left;
}

/* 自定義滾動條樣式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

/* 自訂卡片樣式 */
.custom-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  width: 300px;
  background-color: #fff;
  margin: 0 auto;
}

.custom-card-img {
  display: block;
  width: 100%;
  height: 180px;
  object-fit: cover;
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
}

.custom-card-body {
  padding: 12px;
}

.custom-card-title {
  font-size: 18px;
  margin-bottom: 8px;
  font-family: Kantumruy Pro;
}

.custom-card-text {
  font-size: 14px;
  margin-bottom: 8px;
  font-family: Kantumruy Pro;
}

/* 原有按鈕等樣式 */
.btn {
  display: inline-block;
  padding: 6px 12px;
  font-size: 14px;
  color: #fff;
  background-color: #007bff;
  border-radius: 4px;
  text-decoration: none;
}

</style>
