<!-- src/views/b5venue/venue/B06Booking.vue -->
<template>
<div class="form-container">
    <h2>場地租借申請表</h2>
    
    <form @submit.prevent="handleSubmit" class="form-layout">
        <div class="select-area">
            <div class="select-venue">
                <SelectVenue v-model="formData.selectedVenues" required/>
            </div>
        </div>
        <div class="form-content">
            <div class="form-grid">
                <div class="form-group">
                    <label class="required">租借開始日期</label>
                    <DatePicker
                        v-model="formData.rentalStartDatetime"
                        placeholder="選擇開始日期"
                        @update:modelValue="validateDates"
                    />
                </div>

                <div class="form-group">
                    <label class="required">租借結束日期</label>
                    <DatePicker
                        v-model="formData.rentalEndDatetime"
                        placeholder="選擇結束日期"
                        @update:modelValue="validateDates"
                    />
                </div>
            </div>

            <div class="form-group">
                <label class="required">主辦單位</label>
                <input
                    type="text"
                    v-model="formData.organizer"
                    required
                />
            </div>

            <div class="form-grid">
                <div class="form-group">
                    <label class="required">聯絡人</label>
                    <input
                        type="text"
                        v-model="formData.contact"
                        required
                    />
                </div>

                <div class="form-group">
                    <label class="required">聯絡電話</label>
                    <input
                        type="tel" 
                        v-model="formData.contactPersonMobile"
                        required
                    />
                </div>
            </div>

            <div class="form-group">
                <label class="required">電子郵件</label>
                <input
                    type="email"
                    v-model="formData.contactEmail"
                    required
                />
            </div>

            <div class="form-group">
                <label>活動說明</label>
                <textarea
                    v-model="formData.eventDescription"
                ></textarea>
            </div>

            <div class="form-group">
                <label>備註</label>
                <textarea
                    v-model="formData.remarks"
                ></textarea>
            </div>

            <div class="button-group">
                <!-- <button type="button" class="btn-cancel" @click="handleCancel">取消</button> -->
                <button type="submit" class="btn-submit">送出申請</button>
            </div>
        </div>
    </form>
</div>

<BookingInfo 
    v-if="showBookingInfo"
    :booking-id="currentBookingId"
    :show="showBookingInfo"
    @close="showBookingInfo = false"
/>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axiosapi from '@/plugins/axios.js'
import Swal from 'sweetalert2'
import SelectVenue from '@/components/buttons/b5/SelectVenue.vue'
import DatePicker from '../../../components/buttons/b5/DatePicker.vue'
import BookingInfo from './B06BookingInfo.vue'

const router = useRouter()
const showBookingInfo = ref(false)
const currentBookingId = ref(null)

const validateDates = () => {
    // 檢查是否有選擇日期
    if (!formData.value.rentalStartDatetime || !formData.value.rentalEndDatetime) {
        return
    }

    const startDate = new Date(formData.value.rentalStartDatetime)
    const endDate = new Date(formData.value.rentalEndDatetime)
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    // 檢查開始日期是否小於今天
    if (startDate < today) {
        Swal.fire({
            title: '日期錯誤',
            text: '租借開始日期不能早於今天',
            icon: 'error',
            confirmButtonText: '確定',
            confirmButtonColor:'#0e0e0e'
        })
        formData.value.rentalStartDatetime = ''
        return
    }

    // 檢查結束日期是否小於開始日期
    if (endDate < startDate) {
        Swal.fire({
            title: '日期錯誤',
            text: '租借結束日期不能早於開始日期',
            icon: 'error',
            confirmButtonText: '確定',
            confirmButtonColor:'#0e0e0e'
        })
        formData.value.rentalEndDatetime = ''
        return
    }
}

const formData = ref({
    selectedVenues: [],
    rentalStartDatetime: '',
    rentalEndDatetime: '',
    organizer: '',
    contact: '',
    contactPersonMobile: '',
    contactEmail: '',
    eventName: '',
    eventDescription: '',
    remarks: ''
})

// 處理表單提交
const handleSubmit = async (e) => {
    e.preventDefault()
    
    // 使用 Swal 確認對話框
    const result = await Swal.fire({
        title: '確認送出',
        text: '確定要送出這份預約申請嗎？',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '確認',
        confirmButtonColor:'#0e0e0e',
        cancelButtonText: '取消',
        reverseButtons: true
    })

    if (result.isConfirmed) {
        Swal.fire({
            title: '處理中，請稍後...',
            allowOutsideClick: false,
            allowEscapeKey: false,
            allowEnterKey: false,
            showConfirmButton: false,
            didOpen: () => {
                Swal.showLoading()
                // 直接設置 loading 動畫的顏色
                const loader = document.querySelector('.swal2-loader')
                if (loader) {
                    loader.style.borderColor = '#0e0e0e transparent #0e0e0e transparent'
                }
            }
        })
        try {
            // 驗證場地是否已選擇
            if (!formData.value.selectedVenues || formData.value.selectedVenues.length === 0) {
                throw new Error('請選擇場地')
            }

            // 準備提交的數據
            const submitData = {
                ...formData.value,
                venue: formData.value.selectedVenues.join(',')
            }
            
            delete submitData.selectedVenues

            const response = await axiosapi.post('/api/booking', submitData)
            
            if (response.data.success) {
                await Swal.fire({
                    title: '成功',
                    text: response.data.message || '預約申請已送出',
                    icon: 'success',
                    timer: 1500,
                    showConfirmButton: false,
                    iconColor:'#f8961e'
                })

                // 儲存預約編號並顯示查詢 Modal
                currentBookingId.value = response.data.data.bookingId
                showBookingInfo.value = true

                // 清空表單
                resetForm()
            } else {
                throw new Error(response.data.message || '預約失敗')
            }
        } catch (error) {
            Swal.fire({
                title: '錯誤',
                text: error.response?.data?.message || error.message || '預約失敗，請稍後再試',
                icon: 'error',
                confirmButtonColor:'#0e0e0e',
                confirmButtonText: '確定'
                
            })
        }
    }
}

// 重置表單
const resetForm = () => {
    formData.value = {
        selectedVenues: [],
        rentalStartDatetime: '',
        rentalEndDatetime: '',
        organizer: '',
        contact: '',
        contactPersonMobile: '',
        contactEmail: '',
        eventName: '',
        eventDescription: '',
        remarks: ''
    }
}

// 處理取消按鈕
// const handleCancel = async () => {
//     const result = await Swal.fire({
//         title: '確認取消',
//         text: '確定要取消填寫嗎？表單內容將會清空',
//         icon: 'warning',
//         showCancelButton: true,
//         confirmButtonText: '確定',
//         cancelButtonText: '返回',
//         reverseButtons: true
//     })

//     if (result.isConfirmed) {
//         resetForm()
//         // 可以加入導航或其他取消操作
//     }
// }
</script>

<style scoped>
/* 保留原有的樣式，但移除不需要的 alert 相關樣式 */
.required::after {
    content: "＊";
    color: #f8961e;
    margin-left: 4px;
}

.form-desc {
    color: #666;
    font-size: 14px;
    margin-bottom: 20px;
}

.form-container {
    padding: 20px;
    width: 75%;
    margin-left: 20%;
}

.form-layout {
    display: flex;
    gap: 30px;
    align-items: flex-start;
}

.select-area {
    display: flex;
    width: 40%;
}

.form-content {
    flex: 1;
    padding-left: 3%;
}

.select-venue {
    width: 100%;
    flex: 1; 
}

h2 {
    font-size: 30px;
    margin-bottom: 30px;
}

.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}

.form-group {
    margin-bottom: 20px;
}

label {
    display: block;
    margin-bottom: 8px;
    font-size: 20px;
    font-weight: 500;
}

input, textarea {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
}

input:focus, textarea:focus {
    outline: none;
    border-color: #f8961e;
}

input:disabled {
    background: #f5f5f5;
}

textarea {
    min-height: 100px;
    resize: vertical;
}

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 30px;
}

.btn-cancel, .btn-submit {
    padding: 8px 24px;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-cancel {
    border: 1px solid #ddd;
    background: white;
}

.btn-cancel:hover {
    background: #f5f5f5;
}

.btn-submit {
    background: black;
    color: white;
    border: none;
}

.btn-submit:hover {
    background: #333;
}
</style>