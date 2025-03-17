<template>
    <div v-if="dialogVisible" class="dialog-overlay">
        <div class="dialog-container">
            <div class="dialog-header">
                <h3 class="dialog-title">新增預約</h3>
                <button class="close-button" @click="handleClose">
                    <svg 
                        class="icon-small" 
                        width="24" 
                        height="24" 
                        viewBox="0 0 24 24" 
                        fill="none" 
                        stroke="currentColor" 
                        stroke-width="2" 
                        stroke-linecap="round" 
                        stroke-linejoin="round"
                    >
                        <path d="M18 6L6 18M6 6l12 12"/>
                    </svg>
                </button>
            </div>

            <div class="dialog-body">
                <form @submit.prevent="submitForm">
                    <!-- 第一排 -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="venueId">
                                場地選擇 
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.venueId" class="error-message">
                                    {{ formData.errors.venueId }}
                                </span>
                            </label>
                            <select
                                id="venueId"
                                v-model="formData.venue.venueId"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.venueId }
                                ]"
                                required
                            >
                                <option value="">請選擇場地</option>
                                <option v-for="venue in venues" :key="venue.venueId" :value="venue.venueId">
                                    {{ venue.venueId }}　{{ venue.venueName }}
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="status">
                                場地狀態
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.status" class="error-message">
                                    {{ formData.errors.status }}
                                </span>
                            </label>
                            <select
                                id="status"
                                v-model="formData.status"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.status }
                                ]"
                                required
                            >
                                <option value="">請選擇狀態</option>
                                <option value="休館日">休館日</option>
                                <option value="保養日">保養日</option>
                                <option value="已出租">已出租</option>
                            </select>
                        </div>
                    </div>

                    <!-- 第二排 -->
                    <div class="form-row">
                        <div class="form-group">
                        <label for="startDate">
                            使用啟日
                            <span class="required-mark">*</span>
                            <span v-if="isSubmitted && formData.errors.startDate" class="error-message">
                                {{ formData.errors.startDate }}
                            </span>
                        </label>
                        <div class="date-input-container">
                            <input
                                type="text"
                                id="startDate"
                                v-model="formDisplayData.startDateDisplay"
                                :placeholder="'YYYY-MM-DD'"
                                readonly
                                @click="showStartDatePicker = true"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.startDate }
                                ]"
                                required
                            />
                            <DatePicker
                            v-if="showStartDatePicker"
                            v-model="startDate"
                            :selected-date="startDateSelected"
                            @update:model-value="handleStartDateSelect"
                            v-click-outside="() => showStartDatePicker = false"
                        />
                        </div>
                    </div>

                    <!-- 使用迄日 -->
                    <div class="form-group">
                        <label for="endDate">
                            使用迄日
                            <span class="required-mark">*</span>
                            <span v-if="isSubmitted && formData.errors.endDate" class="error-message">
                                {{ formData.errors.endDate }}
                            </span>
                        </label>
                        <div class="date-input-container">
                            <input
                                type="text"
                                id="endDate"
                                v-model="formDisplayData.endDateDisplay"
                                :placeholder="'YYYY-MM-DD'"
                                readonly
                                @click="handleShowEndDatePicker"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.endDate }
                                ]"
                                required
                            />
                            <DatePicker
                                v-if="showEndDatePicker"
                                v-model="endDate"
                                :selected-date="endDateSelected"
                                :default-date="startDate"  
                                @update:model-value="handleEndDateSelect"
                                v-click-outside="() => showEndDatePicker = false"
                            />
                        </div>
                    </div>
                    </div>

                    <!-- 顯示一般性錯誤 -->
                    <div v-if="formData.errors.general" class="general-error">
                        {{ formData.errors.general }}
                    </div>

                    <!-- 第三排 -->
                    <div class="form-row">
                        <div class="form-group full-width">
                            <label for="note">備註</label>
                            <textarea
                                id="note"
                                v-model="formData.note"
                                class="form-control"
                                rows="3"
                                placeholder="請輸入備註"
                            ></textarea>
                        </div>
                    </div>
                </form>
            </div>

            <div class="dialog-footer">
                <button class="btn btn-outline" @click="handleClose">取消</button>
                <button class="btn btn-primary" @click="submitForm">確認新增</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import DatePicker from '../../components/buttons/b5/DatePicker.vue'
import { dateConfig } from '../../utils/dateConfig.js'

const props = defineProps({
    visible: {
        type: Boolean,
        required: true
    },
})

const emit = defineEmits(['update:visible', 'submit-success'])
const startDate = ref(null)
const endDate = ref(null)

// 日期選擇器相關的狀態
const showStartDatePicker = ref(false)
const showEndDatePicker = ref(false)

const today = new Date()
const startDateSelected = ref({
    year: today.getFullYear(),
    month: today.getMonth(),
    date: today.getDate()
})

const endDateSelected = ref({
    year: today.getFullYear(),
    month: today.getMonth(),
    date: today.getDate()
})

const formDisplayData = reactive({
    startDateDisplay: '',
    endDateDisplay: ''
})

const handleStartDateSelect = (date) => {
    if (!date) return
    
    startDate.value = date
    formData.startDate = dateConfig.formatFullDate(date)
    formDisplayData.startDateDisplay = dateConfig.formatDisplayDate(date)
    showStartDatePicker.value = false
    
    startDateSelected.value = {
        year: date.getFullYear(),
        month: date.getMonth(),
        date: date.getDate()
    }
}

const handleEndDateSelect = (date) => {
    if (!date) return
    
    endDate.value = date
    formData.endDate = dateConfig.formatFullDate(date)
    formDisplayData.endDateDisplay = dateConfig.formatDisplayDate(date)
    showEndDatePicker.value = false
    
    endDateSelected.value = {
        year: date.getFullYear(),
        month: date.getMonth(),
        date: date.getDate()
    }
    
}
const handleShowEndDatePicker = () => {
    // 如果已經選擇了開始日期
    if (startDate.value) {
        // 將結束日期選擇器的預設值設為開始日期
        endDateSelected.value = {
            year: startDate.value.getFullYear(),
            month: startDate.value.getMonth(),
            date: startDate.value.getDate()
        }
    }
    showEndDatePicker.value = true
}

const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
})

const venues = ref([])
const isSubmitted = ref(false)

const formData = reactive({
    startDate: '',
    endDate: '',
    venue: {
        venueId: ''
    },
    status: '',
    note: '',
    errors: {
        venueId: '',
        startDate: '',
        endDate: '',
        status: '',
        general: ''
    }
})

const fetchVenues = async () => {
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/venue/available`)
        if (response.data.success) {
            venues.value = response.data.list.map(venue => ({
                venueId: venue.venueId,
                venueName: venue.venueName
            }))
        } else {
            Swal.fire({
                icon: 'warning',
                title: '警告',
                text: response.data.message || '無法獲取可租借場地資料'
            })
        }
    } catch (error) {
        console.error('獲取可租借場地資料失敗:', error)
        Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '獲取可租借場地資料失敗'
        })
    }
}

const validateForm = () => {
    Object.keys(formData.errors).forEach(key => {
        formData.errors[key] = ''
    })

    let isValid = true

    if (!formData.venue.venueId) {
        formData.errors.venueId = '請選擇場地'
        isValid = false
    }
    if (!formData.startDate) {
        formData.errors.startDate = '請選擇使用啟日'
        isValid = false
    }
    if (!formData.endDate) {
        formData.errors.endDate = '請選擇使用迄日'
        isValid = false
    }
    if (!formData.status) {
        formData.errors.status = '請選擇場地狀態'
        isValid = false
    }
    if (formData.endDate && formData.startDate && formData.endDate < formData.startDate) {
        formData.errors.endDate = '使用迄日不能早於使用啟日'
        isValid = false
    }

    return isValid
}

const submitForm = async () => {
    isSubmitted.value = true
    if (!validateForm()) return

    try {
        const requestData = {
            venueStatus: {
                venue: {
                    venueId: formData.venue.venueId
                },
                status: formData.status,
                note: formData.note
            },
            startDate: formData.startDate,
            endDate: formData.endDate
        }

        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/venue-status/create-range`, 
            requestData
        )

        if (response.data) {
            Swal.fire({
                icon: 'success',
                title: '新增成功',
                showConfirmButton: false,
                timer: 3500,
                position: 'top-end',
                toast: true,
                confirmButtonColor:'#4A6741'
            })
            emit('submit-success')
            handleClose()
        }
    } catch (error) {
        console.error('提交表單時發生錯誤:', error)
        
        if (error.response) {
            const errorMessage = error.response.data.message || error.response.data || '提交失敗'
            formData.errors.general = errorMessage
            Swal.fire({
                icon: 'warning',
                // iconColor:'black',  
                title: '錯誤',
                text: errorMessage,
                confirmButtonColor:'#4A6741'
                
            })
        } else if (error.request) {
            formData.errors.general = '無法連接到伺服器'
            Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '無法連接到伺服器',
                confirmButtonColor:'#4A6741'
            })
        } else {
            formData.errors.general = '系統錯誤'
            Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '系統錯誤',
                confirmButtonColor:'#4A6741'
            })
        }
    }
}

const handleClose = async () => {
    // 檢查表單是否有被修改
    // const isFormModified = formData.venue.venueId || 
    //                       formData.startDate || 
    //                       formData.endDate || 
    //                       formData.status || 
    //                       formData.note

    // if (isFormModified) {
    //     // const result = await Swal.fire({
    //     //     title: '確定要關閉嗎？',
    //     //     text: '您已輸入的資料將會遺失',
    //     //     icon: 'warning',
    //     //     showCancelButton: true,
    //     //     confirmButtonColor: '#4A6741',
    //     //     cancelButtonColor: '#d33',
    //     //     confirmButtonText: '確定',
    //     //     cancelButtonText: '取消',
    //     //     background: '#fff8f0',
    //     //     iconColor: '#f8bb86'
    //     // })

    //     if (!result.isConfirmed) {
    //         return
    //     }
    // }

    // 重置所有表單數據
    dialogVisible.value = false
    isSubmitted.value = false
    formData.venue.venueId = ''
    formData.startDate = ''
    formData.endDate = ''
    formData.status = ''
    formData.note = ''
    formDisplayData.startDateDisplay = ''
    formDisplayData.endDateDisplay = ''
    startDate.value = null
    endDate.value = null
    startDateSelected.value = {
        year: new Date().getFullYear(),
        month: new Date().getMonth(),
        date: new Date().getDate()
    }
    endDateSelected.value = {
        year: new Date().getFullYear(),
        month: new Date().getMonth(),
        date: new Date().getDate()
    }
    Object.keys(formData.errors).forEach(key => {
        formData.errors[key] = ''
    })
}

watch(startDate, (newDate) => {
    if (newDate) {
        const dateObj = new Date(newDate)
        startDateSelected.value = {
            year: dateObj.getFullYear(),
            month: dateObj.getMonth(),
            date: dateObj.getDate()
        }
    }
})

watch(endDate, (newDate) => {
    if (newDate) {
        const dateObj = new Date(newDate)
        endDateSelected.value = {
            year: dateObj.getFullYear(),
            month: dateObj.getMonth(),
            date: dateObj.getDate()
        }
    }
})

onMounted(() => {
    fetchVenues()
})
</script>

<style scoped>
/* @import '../../assets/styles/swal.css' */

:root {
    --primary-color: #4A6741;
    --secondary-color: #8AA682;
    --hover-color: #C2D5BB;
    --background-color: #F5F7F4;
    --text-primary: #2C3E2D;
    --text-secondary: #5C715E;
    --border-color: #D8E0D5;
    --error-color: #DC2626;
    --error-light: #FEF2F2;
}

.dialog-overlay {
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

.dialog-container {
    background: white;
    border-radius: 0.5rem;
    width: 70%;
    max-height: 90vh;
    /* margin: auto; */
    /* overflow-y: auto; */
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.dialog-header {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid var(--border-color);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.dialog-title {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--text-primary);
}

.close-button {
    padding: 0.5rem;
    border: none;
    background: none;
    cursor: pointer;
    color: var(--text-secondary);
}

.close-button:hover {
    color: var(--text-primary);
}

.dialog-body {
    padding: 1.5rem;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 1.5rem;
}

.form-group {
    display: flex;
    flex-direction: column;
}

.form-group.full-width {
    grid-column: span 2;
}

.form-control {
    padding: 0.5rem;
    border: 1px solid var(--border-color);
    border-radius: 0.25rem;
    font-size: 0.875rem;
    color: var(--text-primary);
    background-color: white;
    transition: all 0.2s;
}

.form-control:focus {
    border-color: var(--primary-color);
    outline: none;
    box-shadow: 0 0 0 2px var(--hover-color);
}

.form-control:hover {
    border-color: var(--secondary-color);
}

select.form-control {
    padding-right: 2rem;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%235C715E'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 0.5rem center;
    background-size: 1.5em 1.5em;
    appearance: none;
}

textarea.form-control {
    min-height: 100px;
    resize: vertical;
}

label {
    font-size: 0.875rem;
    font-weight: 500;
    color: var(--text-secondary);
    margin-bottom: 0.5rem;
}

.required-mark {
    color: var(--error-color);
    margin-left: 0.25rem;
    color: rgb(218, 108, 18);
}

.error-message {
    color: var(--error-color);
    font-size: 0.75rem;
    margin-left: 0.5rem;
}

.error-input {
    border-color: var(--error-color) !important;
}

.general-error {
    color: var(--error-color);
    background-color: var(--error-light);
    padding: 0.75rem;
    border-radius: 0.25rem;
    margin-bottom: 1.5rem;
    border: 1px solid var(--error-color);
}

.dialog-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid var(--border-color);
    display: flex;
    justify-content: flex-end;
    gap: 0.75rem;
}

.icon-small {
    width: 20px;
    height: 20px;
}
.btn {
    padding: 0.5rem 1rem;
    border-radius: 0.25rem;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-primary {
    background-color: var(--primary-color);
    color: white;
    border: 1px solid var(--primary-color);
}

.btn-primary:hover {
    background-color: var(--secondary-color);
    border-color: var(--secondary-color);
}

.btn-outline {
    background-color: white;
    color: var(--text-primary);
    border: 1px solid var(--border-color);
}

.btn-outline:hover {
    background-color: var(--background-color);
    border-color: var(--secondary-color);
}


.date-input-container {
    position: relative;
    }

    .date-picker-container {
    position: absolute;
    top: 100%;
    left: 0;
    z-index: 1000;
    background-color: white;
    border: 1px solid var(--border-color);
    border-radius: 0.25rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-top: 0.25rem;
    }

    input[readonly] {
    background-color: white;
    cursor: pointer;
    }

/* Responsive styles */
@media (max-width: 768px) {
    .dialog-container {
        width: 90%;
        margin: 1rem;
    }

    .form-row {
        grid-template-columns: 1fr;
    }

    .form-group.full-width {
        grid-column: span 1;
    }
}
</style>