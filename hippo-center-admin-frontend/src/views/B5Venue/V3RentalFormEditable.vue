<!-- src/views/B5Venue/V3RentalFormEditable.vue -->
<template>
  <div class="detail-item">
    <label>{{ label }}：</label>
    <!-- 非編輯狀態 -->
    <div class="editable-content" @click="startEdit" v-if="!isEditing">
      <template v-if="type === 'date'">
        <span>{{ formatDate(modelValue) }}</span>
      </template>
      <template v-else-if="type === 'textarea'">
        <p>{{ modelValue || '' }}</p>
      </template>
      <template v-else>
        <span>{{ modelValue || '' }}</span>
      </template>
    </div>
    
    <!-- 編輯狀態 -->
    <div v-else class="edit-input-container">
      <template v-if="type === 'date'">
          <div class="date-picker-wrapper">  <!-- 新增這個 div -->
          <V3DatePicker
          v-model="dateValue"
          :selected-date="dateValue"
          @update:modelValue="handleDateChangeAndClose"
          />
      </div>
      </template>
      <!-- 其他輸入類型保持不變 -->
      <template v-else-if="type === 'textarea'">
        <textarea
          v-model="editValue"
          @blur="saveEdit"
          @keydown.esc="cancelEdit"
          ref="inputRef"
          :disabled="disabled"
          class="edit-input textarea"
        ></textarea>
      </template>
      <template v-else>
        <input
          :type="type"
          v-model="editValue"
          @blur="saveEdit"
          @keydown.enter="saveEdit"
          @keydown.esc="cancelEdit"
          ref="inputRef"
          :disabled="disabled"
          class="edit-input"
        >
      </template>
    </div>
    
  </div>
</template>


<script setup>
import { ref, defineProps, defineEmits, watch, nextTick } from 'vue'
import V3DatePicker from '../../components/buttons/b5/V3DatePicker.vue'
import { dateConfig } from '../../utils/dateConfig.js'

const props = defineProps({
  modelValue: {
    type: [String, Number, Date],
    default: ''
  },
  label: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'text'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const isEditing = ref(false)
const editValue = ref('')
const inputRef = ref(null)

const saveEdit = () => {
  if (editValue.value !== props.modelValue) {
    emit('update:modelValue', editValue.value)
  }
  isEditing.value = false
}

const cancelEdit = () => {
  editValue.value = props.modelValue
  isEditing.value = false
}

const dateValue = ref(props.modelValue ? 
  dateConfig.dateToObject(props.modelValue) : 
  {
    year: new Date().getFullYear(),
    month: new Date().getMonth(),
    date: new Date().getDate()
  }
)

// 使用 dateConfig 處理日期

const handleDateChangeAndClose = (value) => {
  if (!value) return
  
  const date = dateConfig.objectToDate(value)
  if (date) {
    const isoDate = date.toISOString().split('T')[0] // YYYY-MM-DD 格式
    emit('update:modelValue', isoDate)
    isEditing.value = false
  }
}
const formatDate = (date) => {
  if (!date) return ''
  try {
    return dateConfig.formatFullDate(new Date(date))
  } catch (error) {
    console.error('Date formatting error:', error)
    return ''
  }
}

const startEdit = (event) => {
  if (!props.disabled) {
    event?.stopPropagation()
    
    // 設置編輯狀態
    isEditing.value = true
    
    if (props.type === 'date') {
      // 日期類型：更新 dateValue
      dateValue.value = props.modelValue ? 
        dateConfig.dateToObject(props.modelValue) : 
        {
          year: new Date().getFullYear(),
          month: new Date().getMonth(),
          date: new Date().getDate()
        }
    } else {
      // 其他類型：focus 輸入框
      nextTick(() => {
        if (inputRef.value) {
          inputRef.value.focus()
        }
      })
    }
  }
}
watch(() => props.modelValue, (newValue) => {
  if (!isEditing.value) {
    if (props.type === 'date' && newValue) {
      try {
        dateValue.value = dateConfig.dateToObject(newValue)
      } catch (error) {
        console.error('Date conversion error:', error)
      }
    } else {
      editValue.value = newValue
    }
  }
}, { immediate: true })
</script>

<style scoped>
.detail-item {
  margin-bottom: 1rem;
}

.detail-item label {
  display: block;
  color: var(--text-secondary);
  margin-bottom: 0.25rem;
  font-weight: 500;
}

.editable-content {
  padding: 0.75rem;  /* 增加內距 */
  min-height: 1.5rem;
  border: 1px dashed #ccc;  /* 改為虛線邊框 */
  border-radius: 0.5rem;
  cursor: pointer;
  position: relative; 
}
.editable-content::after {
  content: '✎';  /* 編輯圖標 */
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  opacity: 0;
  transition: opacity 0.2s;
}

.editable-content:hover {
  background-color: var(--background-color);
  border-color: var(--border-color);
}

.editable-content p {
  margin: 0;
  white-space: pre-wrap;
}

.edit-input-container {
  width: 100%;
}

.edit-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  font-size: 1rem;
  color: var(--text-primary);
  background-color: white;
}

.edit-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(74, 103, 65, 0.2);
}

.edit-input.textarea {
  min-height: 100px;
  resize: vertical;
}

.edit-input:disabled {
  background-color: var(--background-color);
  cursor: not-allowed;
}
.edit-input-container {
  position: relative;
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.save-btn,
.cancel-btn {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 0.25rem;
  cursor: pointer;
  font-size: 0.875rem;
}

.save-btn {
  background-color: var(--primary-color);
  color: white;
}

.cancel-btn {
  background-color: var(--secondary-color);
  color: white;
}

.save-btn:hover,
.cancel-btn:hover {
  opacity: 0.9;
}
/* 適配移動設備 */
@media (max-width: 768px) {
  .edit-input {
    font-size: 16px; /* 防止 iOS 自動縮放 */
  }
}
</style>