<template>
  <!-- 當 visible 為 true 時，顯示彈出視窗 -->
  <div v-if="visible" class="modal-overlay">
    <div class="modal">
      <!-- 這裡使用剛提供的 Create/Edit Form 內容 -->
      <div class="max-w-4xl mx-auto bg-white rounded-lg shadow p-6">
        <h2 class="text-xl font-bold mb-4">
          {{ isEditing ? '編輯活動' : '新增活動' }}
        </h2>
        <form @submit.prevent="onSubmit">
          <table class="w-full mb-4">
            <tr class="border-b">
              <td class="py-4 pr-4 w-1/4">活動名稱</td>
              <td class="py-4">
                <input
                  v-model="localFormData.eventName"
                  type="text"
                  class="w-full px-3 py-2 border rounded"
                  required
                />
              </td>
            </tr>
            <tr class="border-b">
              <td class="py-4 pr-4">活動描述</td>
              <td class="py-4">
                <textarea
                  v-model="localFormData.eventDescription1"
                  class="w-full px-3 py-2 border rounded"
                  rows="4"
                  required
                ></textarea>
              </td>
            </tr>
            <tr class="border-b">
              <td class="py-4 pr-4">開始日期</td>
              <td class="py-4">
                <input
                  v-model="localFormData.eventStartDate"
                  type="date"
                  class="w-full px-3 py-2 border rounded"
                  required
                />
              </td>
            </tr>
            <tr class="border-b">
              <td class="py-4 pr-4">結束日期</td>
              <td class="py-4">
                <input
                  v-model="localFormData.eventEndDate"
                  type="date"
                  class="w-full px-3 py-2 border rounded"
                  required
                />
              </td>
            </tr>
            <tr class="border-b">
              <td class="py-4 pr-4">價格</td>
              <td class="py-4">
                <input
                  v-model="localFormData.eventPrice"
                  type="number"
                  class="w-full px-3 py-2 border rounded"
                  required
                />
              </td>
            </tr>
            <tr class="border-b">
              <td class="py-4 pr-4">場地編號</td>
              <td class="py-4">
                <select
                  v-model="localFormData.venueId"
                  class="w-full px-3 py-2 border rounded"
                  required
                >
                  <option value="A01">1號倉庫</option>
                  <option value="A02">2號倉庫</option>
                  <option value="A03">3號倉庫</option>
                  <option value="A04">4號倉庫</option>
                  <option value="A05">5號倉庫</option>
                </select>
              </td>
            </tr>
            <tr class="border-b">
              <td class="py-4 pr-4">活動分類</td>
              <td class="py-4">
                <select
                  v-model="localFormData.eventCategoryId"
                  class="w-full px-3 py-2 border rounded"
                  required
                >
                  <option value="1">常設展覽</option>
                  <option value="2">特別展覽</option>
                  <option value="3">文化活動</option>
                  <option value="4">市集</option>
                  <option value="5">其他</option>
                </select>
              </td>
            </tr>
            <tr class="border-b">
              <td class="py-4 pr-4">圖片上傳</td>
              <td class="py-4">
                <input
                  type="file"
                  @change="handleFileUpload"
                  multiple
                  accept="image/*"
                  class="w-full"
                />
              </td>
            </tr>
          </table>
          <div class="flex justify-end">
            <button type="button" @click="onCancel" class="mr-2 px-4 py-2 bg-gray-300 rounded">
              取消
            </button>
            <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded">
              送出
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue'

// 傳入的 props：
// - visible：是否顯示彈窗
// - formData：原始的表單資料（編輯時可預先帶入資料）
// - isEditing：判斷目前是編輯或是新增模式
const props = defineProps({
  visible: Boolean,
  formData: Object,
  isEditing: Boolean
})

// 定義 emit 事件：
// - update:visible 用來更新父層控制彈窗顯示與否（v-model 的語法糖）
// - submit：表單送出時傳遞資料給父層
const emit = defineEmits(['update:visible', 'submit'])

// 建立一個本地副本，以避免直接變更父層的資料
const localFormData = ref({ ...props.formData })

// 當 props.formData 改變時，同步更新本地副本
watch(
  () => props.formData,
  (newVal) => {
    localFormData.value = { ...newVal }
  }
)

// 處理檔案上傳（此處僅作示範，可依需求擴充）
function handleFileUpload(e) {
  const files = e.target.files
  console.log('上傳的檔案：', files)
  // 這裡可將檔案資訊儲存到 localFormData 中或上傳到後端
}

// 當按下取消按鈕，關閉彈窗
function onCancel() {
  emit('update:visible', false)
}

// 當表單送出時，傳遞表單資料給父層，並關閉彈窗
function onSubmit() {
  emit('submit', localFormData.value)
  emit('update:visible', false)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal {
  background-color: #fff;
  padding: 30px;
  border-radius: 4px;
  width: 55.55555%;  /* 2/3 of screen width */
 
}
</style>
