<!-- A1EventTable.vue -->
<template>
  <div class="event-table">
    <!-- 載入狀態 -->
    <div v-if="loading" class="flex justify-center items-center py-8">
      <el-loading :active="loading" />
    </div>

    <!-- 表格內容 -->
    <el-table
      v-else
      :data="paginatedEvents"
      style="width: 100%"
      border
    >
      <!-- 活動名稱 -->
      <el-table-column
        prop="eventName"
        label="活動名稱"
        min-width="180"
      >
        <template #default="{ row }">
          <div class="flex items-center gap-2">
            <img 
              v-if="row.images && row.images.length > 0"
              :src="row.images[0]"
              class="w-10 h-10 object-cover rounded"
              alt="活動圖片"
            />
            <span>{{ row.eventName }}</span>
          </div>
        </template>
      </el-table-column>

      <!-- 活動類別 -->
      <el-table-column
        prop="eventCategoryName"
        label="活動類別"
        width="150"
      />

      <!-- 展覽地點 -->
      <el-table-column
        prop="venueName"
        label="展覽地點"
        width="150"
      />

      <!-- 活動日期 -->
      <el-table-column
        label="活動日期"
        width="200"
      >
        <template #default="{ row }">
          <div class="text-sm">
            <div>開始：{{ formatDate(row.eventStartDate) }}</div>
            <div>結束：{{ formatDate(row.eventEndDate) }}</div>
          </div>
        </template>
      </el-table-column>

      <!-- 票價 -->
      <el-table-column
        prop="eventPrice"
        label="票價"
        width="100"
      >
        <template #default="{ row }">
          NT$ {{ row.eventPrice }}
        </template>
      </el-table-column>

      <!-- 發布狀態 -->
      <el-table-column
        label="發布狀態"
        width="120"
      >
        <template #default="{ row }">
          <el-tag
            :type="row.isPublished ? 'success' : 'info'"
          >
            {{ row.isPublished ? '已發布' : '未發布' }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 操作按鈕 -->
      <el-table-column
        label="操作"
        width="120"
        fixed="right"
      >
        <template #default="{ row }">
          <div class="flex gap-2">
            <el-button
              v-if="!isPublished"
              type="primary"
              size="small"
              @click="$emit('publish', row.eventId)"
            >
              發布
            </el-button>
            <el-button
              v-else
              type="danger"
              size="small"
              @click="$emit('unpublish', row)"
            >
              取消發布
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分頁 -->
    <div class="flex justify-center mt-4">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="events.length"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Props 定義
const props = defineProps({
  events: {
    type: Array,
    required: true,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  isPublished: {
    type: Boolean,
    default: false
  }
})

// Emits 定義
defineEmits(['publish', 'unpublish'])

// 分頁相關
const currentPage = ref(1)
const pageSize = ref(10)

// 分頁後的數據
const paginatedEvents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return props.events.slice(start, end)
})

// 日期格式化
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-TW')
}

// 分頁處理函數
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 監聽 props 變化，用於偵錯
watch(() => props.events, (newEvents) => {
  console.log('Events updated in table:', {
    total: newEvents.length,
    firstEvent: newEvents[0]
  })
  // 重置分頁
  currentPage.value = 1
}, { immediate: true })
</script>

<style scoped>
.event-table {
  background-color: white;
  border-radius: 8px;
  padding: 16px;
}
</style>