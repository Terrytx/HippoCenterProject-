<!-- src/views/B5Venue/V3RentalFormFind.vue -->
<template>
<div v-if="show" class="modal">
    <div class="modal-content">
    <div class="modal-header">
        <h3>申請詳情</h3>
        <button class="close-button" @click="closeModal">&times;</button>
    </div>
    
    <div class="modal-body">
        <div class="detail-item">
        <label>申請編號：</label>
        <span>{{ application?.bookingId }}</span>
        </div>

        <V3RentalFormEditable
            v-model="formData.rentalStartDatetime"
            label="使用起日"  
            type="date"
            @update:modelValue="updateField('rentalStartDatetime', $event)"
        />

        <V3RentalFormEditable
            v-model="formData.rentalEndDatetime"
            label="使用迄日"
            type="date"
            @update:modelValue="updateField('rentalEndDatetime', $event)"
        />
        <V3RentalFormEditable
    v-model="formData.venue"
    label="租借場地"
    @update:modelValue="updateField('venueName', $event)"
/>
<div class="detail-item" v-if="formData.venueName">
    <label>場地名稱：</label>
    <span>{{ formData.venueName }}</span>
</div>
        <V3RentalFormEditable
        v-model="formData.organizer"
        label="主辦單位"
        @update:modelValue="updateField('organizer', $event)"
        />

        <V3RentalFormEditable
        v-model="formData.contact"
        label="聯絡人"
        @update:modelValue="updateField('contactPerson', $event)"
        />

        <V3RentalFormEditable
        v-model="formData.contactPersonMobile"
        label="聯絡電話"
        type="tel"
        @update:modelValue="updateField('contactPhone', $event)"
        />

        <V3RentalFormEditable
        v-model="formData.contactEmail"
        label="電子郵件"
        type="email"
        @update:modelValue="updateField('contactEmail', $event)"
        />

        <V3RentalFormEditable
        v-model="formData.eventDescription"
        label="活動說明"
        type="textarea"
        @update:modelValue="updateField('eventDescription', $event)"
        />

        <div class="detail-item">
        <label>申請狀態：</label>
        <span class="status-badge" :class="getStatusClass(formData.orderStatus)">
            {{ formData.orderStatus }}
        </span>
        </div>

        <V3RentalFormEditable
        v-model="formData.note"
        label="備註"
        type="textarea"
        @update:modelValue="updateField('note', $event)"
        />
    </div>

    <div class="modal-footer">
        <button 
        class="primary-button" 
        @click="saveChanges" 
        v-if="hasChanges || isEditMode"
        :disabled="!hasChanges && !isEditMode"
    >
        儲存變更</button>
        <button class="secondary-button" @click="closeModal">關閉</button>
    </div>
    </div>
</div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'
import axios from 'axios'
import V3RentalFormEditable from './V3RentalFormEditable.vue'
import { dateConfig } from '../../utils/MonthConfig.js'
import Swal from 'sweetalert2'

const props = defineProps({
    show: {
        type: Boolean,
        required: true
    },
    application: {
        type: Object,
        default: null
    },
    isEditMode: {  // 添加這個 prop
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['close', 'update'])

// 表單數據
const formData = ref({})
// 追蹤修改的欄位
const modifiedFields = ref({})
const hasChanges = ref(false)

// 監聽 application 變化，更新表單數據
watch(() => props.application, (newVal) => {
if (newVal) {
    formData.value = { ...newVal }
}
}, { deep: true, immediate: true })

const updateField = (field, value) => {
    console.log('欄位更新：', field, value)

    if (field.includes('Datetime')) {
        // 使用 dateConfig 處理日期
        formData.value[field] = dateConfig.toISOString(value)
    } else {
        formData.value[field] = value
    }
    modifiedFields.value[field] = value
    hasChanges.value = true
    console.log('hasChanges 已設為：', hasChanges.value)

    }

    const formatDate = (date) => {
    return dateConfig.formatFullDate(date)
}

const saveChanges = async () => {
    try {
        const updatedApplication = {
        ...formData.value
        }
        console.log('準備更新的資料：', updatedApplication)

        const response = await axios.put(
        `${import.meta.env.VITE_API_URL}/api/booking/ad/${formData.value.bookingId}`,
        updatedApplication
        )

        if (response.data) {
        console.log('更新成功：', response.data)
        emit('update', response.data)
        modifiedFields.value = {}
        hasChanges.value = false
        
        // 使用 SweetAlert2 顯示成功訊息
        await Swal.fire({
            icon: 'success',
            title: '更新成功',
            showConfirmButton: false,
            timer: 1500
        })
        }
    } catch (error) {
        console.error('Error updating application:', error)
        
        // 使用 SweetAlert2 顯示錯誤訊息
        await Swal.fire({
        icon: 'error',
        title: '更新失敗',
        text: error.response?.data?.message || '請稍後再試',
        confirmButtonText: '確定'
        })
    }
    }
const closeModal = async () => {
    if (hasChanges.value) {
        // 使用 SweetAlert2 進行確認
        const result = await Swal.fire({
        icon: 'warning',
        title: '尚未儲存變更',
        text: '您有未儲存的變更，確定要關閉嗎？',
        showCancelButton: true,
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        confirmButtonColor: '#4A6741',
        cancelButtonColor: '#F44336'
        })

        if (result.isConfirmed) {
        emit('close')
        hasChanges.value = false
        modifiedFields.value = {}
        }
    } else {
        emit('close')
    }
    }


const getStatusClass = (status) => {
switch (status) {
    case '送出申請':
    return 'status-pending'
    case '審核中':
    return 'status-reviewing'
    case '檔期已確認':
    return 'status-confirmed'
    case '已取消':
    return 'status-cancelled'
    default:
    return ''
}
}
</script>

<style scoped>
.modal {
position: fixed;
top: 0;
left: 0;
right: 0;
bottom: 0;
background-color: rgba(0, 0, 0, 0.5);
display: flex;
justify-content: center;
align-items: center;
z-index: 1000;

}

.modal-content {
background-color: white;
border-radius: 1rem;
padding: 2rem;
width: 100%;
max-width: 700px;
max-height:95vh;
overflow-y: auto;

}
.modal-content::-webkit-scrollbar {
width: 10px;
}


.modal-header {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 1.5rem;
}

.modal-header h3 {
color: var(--text-primary);
font-size: 1.25rem;
font-weight: 600;
}

.close-button {
background: none;
border: none;
font-size: 1.5rem;
color: var(--text-secondary);
cursor: pointer;
}

.modal-body {
margin-bottom: 1.5rem;
}

.detail-item {
margin-bottom: 1rem;
}

.detail-item label {
/* display: block; */
color: var(--text-secondary);
margin-bottom: 0.25rem;
font-weight: 500;
}

.detail-item p {
color: var(--text-primary);
margin: 0;
line-height: 1.5;
}

.modal-footer {
display: flex;
justify-content: flex-end;
gap: 1rem;
margin-top: 2rem;
padding-top: 1rem;
border-top: 1px solid var(--border-color);
}

.primary-button,
.secondary-button {
padding: 0.5rem 1rem;
border: none;
border-radius: 0.5rem;
cursor: pointer;
font-weight: 500;
transition: all 0.3s ease;
}

.primary-button {
background-color: var(--primary-color);
color: var(--border-color);
}

.primary-button:hover {
background-color: var(--secondary-color);
}

.secondary-button {
background-color: var(--secondary-color);
color: var(--text-final);
}

.secondary-button:hover {
background-color: var(--hover-color);
}

.status-badge {
display: inline-block;
padding: 0.25rem 0.75rem;
border-radius: 1rem;
font-size: 0.875rem;
font-weight: 500;
}

.status-badge.status-pending {
background-color: #FFC107;
color: black;
}

.status-badge.status-reviewing {
background-color: #2196F3;
color: white;
}

.status-badge.status-confirmed {
background-color: #4CAF50;
color: white;
}

.status-badge.status-cancelled {
background-color: #F44336;
color: white;
}

@media (max-width: 768px) {
.modal-content {
    width: 95%;
    padding: 1rem;
}

.detail-item {
    margin-bottom: 1.5rem;
}
}

.modal-enter-active,
.modal-leave-active {
transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
opacity: 0;
}
</style>