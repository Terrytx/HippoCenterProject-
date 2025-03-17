<!-- A1EventForm.vue -->
<template>
  <el-card class="form-card">
    <el-form 
      @submit.prevent="submitForm" 
      :model="formData"
      :rules="rules"
      ref="formRef"
      label-position="right"
      label-width="120px"
      class="max-w-5xl mx-auto p-6"
    >
      <el-form-item label="活動名稱" prop="eventName" required>
        <el-input 
          v-model="formData.eventName"
          placeholder="請輸入活動名稱"
          class="form-input"
        />
      </el-form-item>

      <el-form-item 
        label="活動描述" 
        prop="eventDescription1" 
        required
        :error="descriptionError"
      >
        <el-input 
          v-model="formData.eventDescription1"
          type="textarea"
          :rows="6"
          placeholder="請輸入活動描述"
          class="form-input"
          @input="handleDescriptionInput"
        />
        <div class="mt-2 flex justify-between items-center">
          <span class="text-gray-500 text-sm">※ 字數需介於10到1000字之間</span>
          <span 
            :class="[
              'text-sm', 
              descriptionLength < 10 || descriptionLength > 1000 
                ? 'text-red-500' 
                : 'text-gray-500'
            ]"
          >
            目前字數：{{ descriptionLength }} 字
          </span>
        </div>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="開始日期" prop="eventStartDate" required>
            <el-date-picker
              v-model="formData.eventStartDate"
              type="date"
              placeholder="選擇開始日期"
              class="form-input"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="結束日期" prop="eventEndDate" required>
            <el-date-picker
              v-model="formData.eventEndDate"
              type="date"
              placeholder="選擇結束日期"
              class="form-input"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item 
        label="價格" 
        prop="eventPrice" 
        required
        :error="priceError"
      >
        <el-input-number 
          v-model="formData.eventPrice"
          :min="0"
          :max="1000"
          :precision="0"
          class="form-input"
          @change="handlePriceChange"
        />
        <div class="mt-2">
          <span class="text-gray-500 text-sm">※ 價格上限為1000元</span>
        </div>
      </el-form-item>

      <el-form-item label="場地編號" prop="venueId" required>
        <el-select 
          v-model="formData.venueId"
          placeholder="請選擇場地"
          class="form-input"
        >
          <el-option label="倉庫群" value="A00" />
          <el-option label="1號倉庫" value="A01" />
          <el-option label="2號倉庫" value="A02" />
          <el-option label="3號倉庫" value="A03" />
          <el-option label="4號倉庫" value="A04" />
          <el-option label="5號倉庫" value="A05" />
          <el-option label="春田綠地藝術廳" value="B00" />
          <el-option label="春田序曲" value="B02" />
          <el-option label="瑪吉的畫廊" value="B03" />
          <el-option label="巴特的藝術空間" value="B04" />
          <el-option label="荷馬的闢護所" value="B05" />
          <el-option label="麗莎的實驗室" value="B06" />
        </el-select>
      </el-form-item>

      <el-form-item label="活動分類" prop="eventCategoryId" required>
        <el-select 
          v-model="formData.eventCategoryId"
          placeholder="請選擇分類"
          class="form-input"
        >
          <el-option label="展演" value="1" />
          <el-option label="演唱會" value="2" />
          <el-option label="活動與講座" value="3" />
          <el-option label="音樂劇" value="4" />
          <el-option label="其他" value="5" />
        </el-select>
      </el-form-item>

      <el-form-item label="圖片上傳">
        <el-upload
          action="#"
          :auto-upload="false"
          :on-change="handleFileUpload"
          multiple
          accept="image/*"
          class="upload-area"
        >
          <template #trigger>
            <el-button type="primary">選擇圖片</el-button>
          </template>
          <template #tip>
            <div class="text-gray-400 mt-2">支援多張圖片上傳</div>
          </template>
        </el-upload>
      </el-form-item>

      <div class="flex justify-center gap-4 mt-8">
        <el-button 
          type="primary" 
          @click="submitForm"
          :loading="isSubmitting"
          size="large"
        >
          {{ isEditing ? '更新' : '新增' }}
        </el-button>
        <el-button 
          type="warning"
          @click="resetForm"
          size="large"
        >
          清除
        </el-button>
     
      </div>
    </el-form>
  </el-card>

  <!-- 價格超限對話框 -->
  <el-dialog
    v-model="showPriceDialog"
    title="價格超過限制"
    width="30%"
    center
  >
    <span>票價超過1000元，需要提出特殊申請。請聯繫管理員進行處理。</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handlePriceDialogClose">我知道了</el-button>
        <el-button type="primary" @click="handlePriceDialogConfirm">
          前往申請
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 字數警告對話框 -->
  <el-dialog
    v-model="showDescriptionDialog"
    title="字數警告"
    width="30%"
    center
  >
    <span>{{ descriptionError }}</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showDescriptionDialog = false">我知道了</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 確認清除對話框 -->
  <el-dialog
    v-model="showResetDialog"
    title="確認清除"
    width="30%"
    center
  >
    <span>確定要清除所有已填寫的內容嗎？</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showResetDialog = false">取消</el-button>
        <el-button type="warning" @click="confirmReset">
          確定清除
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import Swal from 'sweetalert2';
import { defineProps, defineEmits, ref, computed } from 'vue'
import axiosapi from "@/plugins/axios.js";
import { ElMessage } from 'element-plus'

const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  isEditing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit-success', 'cancel'])
const isSubmitting = ref(false)
const showPriceDialog = ref(false)
const showDescriptionDialog = ref(false)
const showResetDialog = ref(false)
const priceError = ref('')
const descriptionError = ref('')
const formRef = ref(null)

const selectedFiles = ref([])  // 全局或在 setup 裡定義

const descriptionLength = computed(() => {
  return props.formData.eventDescription1?.length || 0
})

const handleDescriptionInput = () => {
  const length = descriptionLength.value
  if (length < 1) {
    descriptionError.value = '活動描述不得少於10字'
    showDescriptionDialog.value = true
  } else if (length > 1000) {
    descriptionError.value = '活動描述不得超過1000字'
    showDescriptionDialog.value = true
  } else {
    descriptionError.value = ''
    showDescriptionDialog.value = false
  }
}

const rules = {
  eventName: [
    { required: true, message: '請輸入活動名稱', trigger: 'blur' }
  ],
  eventDescription1: [
    { required: true, message: '請輸入活動描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '活動描述不可以超過1000字', trigger: 'blur' }
  ],
  eventStartDate: [
    { required: true, message: '請選擇開始日期', trigger: 'change' }
  ],
  eventEndDate: [
    { required: true, message: '請選擇結束日期', trigger: 'change' }
  ],
  eventPrice: [
    { required: true, message: '請輸入價格', trigger: 'change' }
  ],
  venueId: [
    { required: true, message: '請選擇場地', trigger: 'change' }
  ],
  eventCategoryId: [
    { required: true, message: '請選擇活動分類', trigger: 'change' }
  ]
}

const handlePriceChange = (value) => {
  if (value > 1000) {
    showPriceDialog.value = true
    priceError.value = '票價超過限制，需要特殊申請'
  } else {
    priceError.value = ''
  }
}

const handlePriceDialogClose = () => {
  showPriceDialog.value = false
  props.formData.eventPrice = 1000
}

const handlePriceDialogConfirm = () => {
  showPriceDialog.value = false
  ElMessage({
    type: 'info',
    message: '即將開放申請功能'
  })
}

const handleFileUpload = (file) => {
  // file.raw 為原生 File 物件
  selectedFiles.value.push(file.raw)
  console.log("準備上傳的檔案：", selectedFiles.value)
}

// 新增清除表單相關方法
const resetForm = () => {
  showResetDialog.value = true
}

const confirmReset = () => {
  Swal.fire({
    title: "確定要清除表單嗎？",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "確定",
    cancelButtonText: "取消",
    didOpen: () => {
      const swalContainer = Swal.getPopup().parentElement;
      if (swalContainer) {
        swalContainer.style.zIndex = '3000';
      }
    }
  }).then((result) => {
    if (result.isConfirmed) {
      if (formRef.value) {
        formRef.value.resetFields()
        
        // 重置所有表單字段
        Object.keys(props.formData).forEach(key => {
          props.formData[key] = null
        })
        
        // 重置圖片上傳
        props.formData.imagePaths = []
        
        // 重置錯誤提示
        priceError.value = ''
        descriptionError.value = ''
      }
      
      showResetDialog.value = false
      
      ElMessage({
        type: 'success',
        message: '表單已清除',
        duration: 2000
      })
    }
  })
}

const submitForm = async () => { 
  if (formRef.value) { 
    await formRef.value.validate(async (valid) => {
      if (valid) {
        // 驗證價格與描述字數
        if (props.formData.eventPrice > 1000) {
          showPriceDialog.value = true;
          return;
        }
        const descLength = props.formData.eventDescription1?.length || 0;
        if (descLength < 10 || descLength > 1000) {
          showDescriptionDialog.value = true;
          return;
        }
  
        try {
          isSubmitting.value = true;
          
          // 加上 SweetAlert2 確認提示
          const swalResult = await Swal.fire({
            title: props.isEditing ? '確定要更新嗎？' : '確定要新增嗎？',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '確定',
            cancelButtonText: '取消',
            didOpen: () => {
              const swalContainer = Swal.getPopup().parentElement;
              if (swalContainer) {
                swalContainer.style.zIndex = '3000';
              }
            }
          });
          if (!swalResult.isConfirmed) {
            isSubmitting.value = false;
            return;
          }
  
          // 建立一個 FormData 物件
          const formDataObj = new FormData();
  
          // 以 props.formData 作為 JSON 資料來源
          const dataPayload = {
            eventName: props.formData.eventName,
            eventDescription1: props.formData.eventDescription1,
            eventPrice: props.formData.eventPrice,
            eventStartDate: props.formData.eventStartDate,
            eventEndDate: props.formData.eventEndDate,
            eventCategoryId: props.formData.eventCategoryId,
            venueId: props.formData.venueId
            // 若有其他欄位，也請加入
          };
  
          // 將 JSON 轉成 Blob 並加入 FormData，key 為 "data"
          formDataObj.append("data", new Blob([JSON.stringify(dataPayload)], { type: "application/json" }));
  
          // 將圖片檔案加入 FormData，假設 selectedFiles 已收集 File 物件
          selectedFiles.value.forEach(file => {
            formDataObj.append("images", file);
          });
  
          // 根據是否為編輯模式選擇 API 與 HTTP 方法
          if (props.isEditing) {
            await axiosapi.put(`/api/events-admin/update/${props.formData.eventId}`, formDataObj, {
              headers: { "Content-Type": "multipart/form-data" }
            });
            ElMessage({
              type: 'success',
              message: '活動已更新成功',
              duration: 2000
            });
          } else {
            await axiosapi.post(`/api/events-admin/create`, formDataObj, {
              headers: { "Content-Type": "multipart/form-data" }
            });
            ElMessage({
              type: 'success',
              message: '活動已新增成功',
              duration: 2000
            });
          }
  
          emit('submit-success');
        } catch (error) {
          console.error('Error submitting form:', error);
          ElMessage({
            type: 'error',
            message: props.isEditing ? '更新失敗' : '新增失敗',
            duration: 2000
          });
        } finally {
          isSubmitting.value = false;
        }
      }
    });
  }
}



// const submitForm = async () => {
//   // 建立一個 FormData 物件
//   const formDataObj = new FormData();
  
//   // 以 props.formData 作為 JSON 資料來源
//   const dataPayload = {
//     eventName: props.formData.eventName,
//     eventDescription1: props.formData.eventDescription1,
//     eventPrice: props.formData.eventPrice,
//     eventStartDate: props.formData.eventStartDate,
//     eventEndDate: props.formData.eventEndDate,
//     eventCategoryId: props.formData.eventCategoryId,
//     venueId: props.formData.venueId
//     // ...其他欄位
//   };

//   // 將 JSON 轉成 blob 並加入 formData
//   formDataObj.append("data", new Blob([JSON.stringify(dataPayload)], { type: "application/json" }));
  
//   // 假設你用 selectedFiles 收集了檔案
//   selectedFiles.value.forEach(file => {
//     formDataObj.append("images", file);
//   });
  
//   try {
//     const response = await axiosapi.post('/api/events-admin/create', formDataObj, {
//       headers: { "Content-Type": "multipart/form-data" }
//     });
//     console.log("上傳成功", response.data);
//   } catch (error) {
//     console.error("上傳失敗", error);
//   }
// }



// const submitForm = async () => {
//   if (formRef.value) {
//     await formRef.value.validate(async (valid) => {
//       if (valid) {
//         if (props.formData.eventPrice > 1000) {
//           showPriceDialog.value = true
//           return
//         }
        
//         const descLength = props.formData.eventDescription1?.length || 0
//         if (descLength < 10 || descLength > 1000) {
//           showDescriptionDialog.value = true
//           return
//         }

//         try {
//           isSubmitting.value = true
//           // const baseURL = '/api/events-admin'
//           if (props.isEditing) {
//             await axiosapi.put(`/api/events-admin/update/${props.formData.eventId}`, props.formData)
//             ElMessage({
//               type: 'success',
//               message: '活動已更新成功',
//               duration: 2000
//             })
//           } else {
//             await axiosapi.post(`/api/events-admin/create`, props.formData)
//             ElMessage({
//               type: 'success',
//               message: '活動已新增成功',
//               duration: 2000
//             })
//           }
//           emit('submit-success')
//         } catch (error) {
//           console.error('Error submitting form:', error)
//           ElMessage({
//             type: 'error',
//             message: props.isEditing ? '更新失敗' : '新增失敗',
//             duration: 2000
//           })
//         } finally {
//           isSubmitting.value = false
//         }
//       }
//     })
//   }
// }
</script>

<style scoped>
.form-card {
  margin: 2rem auto;
  max-width: 1200px;
}

.form-input {
  width: 100%;
}

.upload-area {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 20px;
}

:deep(.el-form-item__label) {
  font-size: 1rem;
  font-weight: 500;
}

:deep(.el-input__inner) {
  height: 40px;
  font-size: 1rem;
}

:deep(.el-textarea__inner) {
  font-size: 1rem;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-button) {
  padding: 12px 24px;
  font-size: 1rem;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

/* 新增樣式 */
:deep(.el-button--warning) {
  margin: 0 1rem;
}

:deep(.el-dialog__body) {
  padding: 20px;
  font-size: 1rem;
}

:deep(.el-dialog__title) {
  font-size: 1.2rem;
  font-weight: 500;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-list) {
  margin-top: 1rem;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px;
  border-bottom: 1px solid #e4e4e4;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #e4e4e4;
  padding: 20px;
}

:deep(.el-form-item.is-error .el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--el-color-danger) inset;
}

:deep(.el-date-editor.el-input) {
  width: 100%;
}
</style>