<template>
    <div v-if="dialogVisible" class="dialog-overlay">
        <div class="dialog-container">
            <div class="dialog-header">
                <h3 class="dialog-title">場地租借申請表</h3>
                <div v-if="formData.errors.general" class="general-error">
                    {{ formData.errors.general }}
                </div>
                <button class="close-button" @click="handleClose">
                    <svg class="icon-small" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M18 6L6 18M6 6l12 12"/>
                    </svg>
                </button>
            </div>

            <div class="dialog-body">
                <form @submit.prevent="submitForm">
                    <!-- First row - Venue only -->
                    <div class="form-row venue-row">
                        <div class="form-group full-width">
                            <label>
                                場地選擇 
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.venue" class="error-message">
                                    {{ formData.errors.venue }}
                                </span>
                            </label>
                            <div class="venue-checkbox-list" :class="{ 'error-input': isSubmitted && formData.errors.venue }">
                                <div class="venue-grid">
                                    <label v-for="venue in venues" 
                                        :key="venue.venueId" 
                                        class="venue-checkbox-item">
                                        <input
                                            type="checkbox"
                                            :value="venue.venueId"
                                            v-model="formData.venue"
                                            class="venue-checkbox"
                                        />
                                        <span class="checkbox-custom"></span>
                                        <span class="venue-name">{{ venue.venueId }}　{{ venue.venueName }}</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                </div>

                    <!-- Second row - Start and End Date -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="startDate">
                                使用開始日期
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.rentalStartDatetime" class="error-message">
                                    {{ formData.errors.rentalStartDatetime }}
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
                                        { 'error-input': isSubmitted && formData.errors.rentalStartDatetime }
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

                        <div class="form-group">
                            <label for="endDate">
                                使用結束日期
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.rentalEndDatetime" class="error-message">
                                    {{ formData.errors.rentalEndDatetime }}
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
                                        { 'error-input': isSubmitted && formData.errors.rentalEndDatetime }
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

                    <!-- Third row - Organizer and Contact Person -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="organizer">
                                主辦單位
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.organizer" class="error-message">
                                    {{ formData.errors.organizer }}
                                </span>
                            </label>
                            <input
                                type="text"
                                id="organizer"
                                v-model="formData.organizer"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.organizer }
                                ]"
                                placeholder="請輸入主辦單位"
                                required
                            />
                        </div>

                        <div class="form-group">
                            <label for="contact">
                                聯絡人
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.contact" class="error-message">
                                    {{ formData.errors.contact }}
                                </span>
                            </label>
                            <input
                                type="text"
                                id="contact"
                                v-model="formData.contact"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.contact }
                                ]"
                                placeholder="請輸入聯絡人姓名"
                                required
                            />
                        </div>
                    </div>

                    <!-- Fourth row - Phone and Email -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="phone">
                                聯絡電話
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.contactPersonMobile" class="error-message">
                                    {{ formData.errors.contactPersonMobile }}
                                </span>
                            </label>
                            <input
                                type="tel"
                                id="phone"
                                v-model="formData.contactPersonMobile"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.contactPersonMobile }
                                ]"
                                placeholder="請輸入聯絡電話"
                                required
                            />
                        </div>

                        <div class="form-group">
                            <label for="email">
                                電子郵件
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.contactEmail" class="error-message">
                                    {{ formData.errors.contactEmail }}
                                </span>
                            </label>
                            <input
                                type="email"
                                id="email"
                                v-model="formData.contactEmail"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.contactEmail }
                                ]"
                                placeholder="請輸入電子郵件"
                                required
                            />
                        </div>
                    </div>

                    <!-- Fifth row - Event Description -->
                    <div class="form-row">
                        <div class="form-group full-width">
                            <label for="eventDescription">
                                活動說明
                                <span class="required-mark">*</span>
                                <span v-if="isSubmitted && formData.errors.eventDescription" class="error-message">
                                    {{ formData.errors.eventDescription }}
                                </span>
                            </label>
                            <textarea
                                id="eventDescription"
                                v-model="formData.eventDescription"
                                :class="[
                                    'form-control',
                                    { 'error-input': isSubmitted && formData.errors.eventDescription }
                                ]"
                                rows="3"
                                placeholder="請輸入活動說明"
                                required
                            ></textarea>
                        </div>
                    </div>

                    <!-- Sixth row - Note -->
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

// Date related refs
const startDate = ref(null)
const endDate = ref(null)
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

const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
})

const venues = ref([])
const isSubmitted = ref(false)

const formData = reactive({
    venue: [],
    organizer: '',
    contact: '', // Add this new field
    rentalStartDatetime: '',
    rentalEndDatetime: '',
    contactPersonMobile: '',
    contactEmail: '',
    eventDescription: '',
    remarks: '',
    errors: {
        venue: '',
        organizer: '',
        contact: '', // Add this new field
        rentalStartDatetime: '',
        rentalEndDatetime: '',
        contactPersonMobile: '',
        contactEmail: '',
        eventDescription: '',
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
    console.log('進入驗證函數')
    console.log('當前表單數據:', {
        venue: formData.venue,
        organizer: formData.organizer,
        rentalStartDatetime: formData.rentalStartDatetime,
        rentalEndDatetime: formData.rentalEndDatetime,
        contactPersonMobile: formData.contactPersonMobile,
        contactEmail: formData.contactEmail,
        eventDescription: formData.eventDescription
    })

    Object.keys(formData.errors).forEach(key => {
        formData.errors[key] = ''
    })

    let isValid = true

     if (!formData.venue.length) { // 檢查是否有選擇場地
        console.log('場地驗證失敗: 未選擇場地')
        formData.errors.venue = '請選擇至少一個場地'
        isValid = false
    }

    if (!formData.organizer) {
        console.log('主辦單位驗證失敗: 未輸入主辦單位')
        formData.errors.organizer = '請輸入主辦單位'
        isValid = false
    }
    if (!formData.contact) {
        console.log('聯絡人驗證失敗: 未輸入聯絡人')
        formData.errors.contact = '請輸入聯絡人姓名'
        isValid = false
    }

    if (!formData.rentalStartDatetime) {
        console.log('開始日期驗證失敗: 未選擇日期')
        formData.errors.rentalStartDatetime = '請選擇使用開始日期'
        isValid = false
    }

    if (!formData.rentalEndDatetime) {
        console.log('結束日期驗證失敗: 未選擇日期')
        formData.errors.rentalEndDatetime = '請選擇使用結束日期'
        isValid = false
    }

    if (!formData.contactPersonMobile) {
        console.log('聯絡電話驗證失敗: 未輸入電話')
        formData.errors.contactPersonMobile = '請輸入聯絡電話'
        isValid = false
    }

    if (!formData.contactEmail) {
        console.log('電子郵件驗證失敗: 未輸入郵件')
        formData.errors.contactEmail = '請輸入電子郵件'
        isValid = false
    }

    if (formData.rentalEndDatetime && formData.rentalStartDatetime && 
        new Date(formData.rentalEndDatetime) < new Date(formData.rentalStartDatetime)) {
        console.log('日期範圍驗證失敗: 結束日期早於開始日期')
        formData.errors.rentalEndDatetime = '使用結束日期不能早於使用開始日期'
        isValid = false
    }

    console.log('驗證結果:', isValid)
    console.log('錯誤信息:', Object.fromEntries(
        Object.entries(formData.errors).filter(([_, value]) => value !== '')
    ))
    return isValid
}

const submitForm = async () => {
    isSubmitted.value = true
    console.log('開始表單提交，isSubmitted:', isSubmitted.value)
    
    if (!validateForm()) {
        console.log('表單驗證失敗，errors:', formData.errors)
        return
    }

    // 顯示處理中提示，並自定義顏色
    Swal.fire({
        title: '處理中...',
        text: '請稍候',
        allowOutsideClick: false,
        showConfirmButton: false,
        willOpen: () => {
            Swal.showLoading()
        },
        // 自定義 CSS 樣式
        customClass: {
            popup: 'my-swal'
        },
        didOpen: () => {
            // 動態添加樣式
            const style = document.createElement('style')
            style.textContent = `
                .my-swal .swal2-title {
                    color: #4A6741;
                }
                .my-swal .swal2-loading {
                    color: #4A6741;
                }
                .my-swal .swal2-loader {
                    border-color: #4A6741 transparent #4A6741 transparent;
                }
            `
            document.head.appendChild(style)
        }
    })

    try {
        const requestData = {
            venue: formData.venue.join(','),
            organizer: formData.organizer,
            contact: formData.contact,
            rentalStartDatetime: formData.rentalStartDatetime,
            rentalEndDatetime: formData.rentalEndDatetime,
            contactPersonMobile: formData.contactPersonMobile,
            contactEmail: formData.contactEmail,
            eventDescription: formData.eventDescription,
            remarks: formData.remarks
        }
        console.log('選擇的場地ID:', formData.venue)

        console.log('準備發送的請求數據:', requestData)
        console.log('API URL:', `${import.meta.env.VITE_API_URL}/api/booking`)

        const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/booking`,
            requestData
        )
        
        console.log('收到的響應數據:', response.data)

        if (response.data) {
            await Swal.fire({
                icon: 'success',
                title: '新增成功！',
                text: '場地租借申請已成功送出',
                confirmButtonText: '確定',
                confirmButtonColor: '#4A6741',
                allowOutsideClick: false
            })
            emit('submit-success')
            handleClose()
        }
    } catch (error) {
        console.error('提交表單時發生錯誤:', error)
        console.log('錯誤詳情:', {
            response: error.response,
            request: error.request,
            message: error.message
        })
        
        if (error.response) {
            const errorMessage = error.response.data.message || error.response.data || '提交失敗'
            console.log('服務器返回的錯誤:', errorMessage)
            formData.errors.general = errorMessage
            await Swal.fire({
                icon: 'warning',
                title: '錯誤',
                text: errorMessage,
                confirmButtonColor: '#4A6741'
            })
        } else if (error.request) {
            console.log('網絡請求錯誤:', error.request)
            formData.errors.general = '無法連接到伺服器'
            await Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '無法連接到伺服器',
                confirmButtonColor: '#4A6741'
            })
        } else {
            console.log('其他錯誤:', error.message)
            formData.errors.general = '系統錯誤'
            await Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '系統錯誤',
                confirmButtonColor: '#4A6741'
            })
        }
    }
}

const handleStartDateSelect = (date) => {
    console.log('選擇開始日期:', date)
    if (!date) {
        console.log('開始日期為空')
        return
    }
    
    startDate.value = date
    formData.rentalStartDatetime = dateConfig.formatFullDate(date)
    formDisplayData.startDateDisplay = dateConfig.formatDisplayDate(date)
    console.log('設置開始日期:', {
        rawDate: date,
        formattedDate: formData.rentalStartDatetime,
        displayDate: formDisplayData.startDateDisplay
    })
    
    showStartDatePicker.value = false
    
    startDateSelected.value = {
        year: date.getFullYear(),
        month: date.getMonth(),
        date: date.getDate()
    }
    console.log('更新開始日期選擇狀態:', startDateSelected.value)
}

const handleEndDateSelect = (date) => {
    console.log('選擇結束日期:', date)
    if (!date) {
        console.log('結束日期為空')
        return
    }
    
    endDate.value = date
    formData.rentalEndDatetime = dateConfig.formatFullDate(date)
    formDisplayData.endDateDisplay = dateConfig.formatDisplayDate(date)
    console.log('設置結束日期:', {
        rawDate: date,
        formattedDate: formData.rentalEndDatetime,
        displayDate: formDisplayData.endDateDisplay
    })
    
    showEndDatePicker.value = false
    
    endDateSelected.value = {
        year: date.getFullYear(),
        month: date.getMonth(),
        date: date.getDate()
    }
    console.log('更新結束日期選擇狀態:', endDateSelected.value)
}

const handleShowEndDatePicker = () => {
    if (startDate.value) {
        endDateSelected.value = {
            year: startDate.value.getFullYear(),
            month: startDate.value.getMonth(),
            date: startDate.value.getDate()
        }
    }
    showEndDatePicker.value = true
}

const handleClose = () => {
    // 直接重置所有表單數據
    dialogVisible.value = false
    isSubmitted.value = false
    formData.venue = []
    formData.organizer = ''
    formData.contact = ''
    formData.rentalStartDatetime = ''
    formData.rentalEndDatetime = ''
    formData.contactPersonMobile = ''
    formData.contactEmail = ''
    formData.eventDescription = ''
    formData.remarks = ''
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
    max-height: 95vh;
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

.required-mark {
    color: rgb(218, 108, 18);
    margin-left: 0.25rem;
}

.error-message {
    color: rgb(218, 108, 18);
    font-size: 0.8rem;
    margin-left: 1rem;
}

.error-input {
    border-color: var(--error-color) !important;
}

.dialog-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid var(--border-color);
    display: flex;
    justify-content: flex-end;
    gap: 0.75rem;
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

.icon-small {
    width: 20px;
    height: 20px;
}

input[readonly] {
    background-color: white;
    cursor: pointer;
}
.venue-row {
    margin-bottom: 1.5rem;
}

.venue-checkbox-list {
    border: 1px solid var(--border-color);
    border-radius: 0.25rem;
    background: white;
    padding: 0.5rem;
    max-height: 150px; /* 增加高度 */
    overflow-y: auto;
}

/* 使用 grid 創建兩欄布局 */
.venue-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 0.5rem 1rem; /* 增加列間距 */
}

.venue-checkbox-item {
    display: flex;
    align-items: center;
    padding: 0.5rem;
    cursor: pointer;
    position: relative;
    transition: background-color 0.2s;
    border-radius: 0.25rem;
}

.venue-checkbox-item:hover {
    background-color: var(--background-color);
}

.venue-checkbox {
    position: absolute;
    opacity: 0;
    cursor: pointer;
}

.checkbox-custom {
    position: relative;
    display: inline-block;
    width: 20px;
    height: 20px;
    margin-right: 0.75rem;
    border: 2px solid var(--border-color);
    border-radius: 4px;
    background-color: white;
    transition: all 0.2s;
    flex-shrink: 0; /* 防止 checkbox 被壓縮 */
}

.venue-checkbox:checked + .checkbox-custom {
    background-color: var(--primary-color);
    border-color: var(--primary-color);
}

.venue-checkbox:checked + .checkbox-custom::after {
    content: '';
    position: absolute;
    left: 6px;
    top: 2px;
    width: 5px;
    height: 10px;
    border: solid white;
    border-width: 0 2px 2px 0;
    transform: rotate(45deg);
}

.venue-name {
    margin-left: 0.5rem;
    white-space: nowrap; /* 防止文字換行 */
    overflow: hidden;
    text-overflow: ellipsis; /* 文字過長時顯示省略號 */
}

/* 當有錯誤時的樣式 */
.error-input {
    border-color: #f8961e;
}
.general-error {
    color: var(--error-color);
    background-color: var(--error-light);
    padding: 0.75rem;
    border-radius: 0.25rem;
    margin-bottom: 1rem;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .venue-grid {
        grid-template-columns: 1fr; /* 在小螢幕上改為單欄 */
    }
}
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