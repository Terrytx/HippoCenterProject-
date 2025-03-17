<!-- VenueForm.vue -->
<template>
    <div class="venue-form">
        <div class="card">
            <form @submit.prevent="submitForm" enctype="multipart/form-data">
                <!-- 第一排 -->
                <div class="form-row">
                    <div class="form-col">
                        <div class="form-group">
                            <label for="venueId">場地ID編碼 <span class="required">*</span></label>
                            <input
                                type="text"
                                id="venueId"
                                v-model="formData.venueId"
                                required
                                class="form-control"
                            />
                        </div>
                    </div>

                    <div class="form-col">
                        <div class="form-group">
                            <label for="venueName">場地名稱 <span class="required">*</span></label>
                            <input
                                type="text"
                                id="venueName"
                                v-model="formData.venueName"
                                required
                                class="form-control"
                            />
                        </div>
                    </div>

                    <div class="form-col">
                        <div class="form-group">
                            <label for="rentalStatue">是否可出租 <span class="required">*</span></label>
                            <select
                                id="rentalStatue"
                                v-model="formData.rentalStatue"
                                required
                                class="form-control"
                            >
                                <option value="">請選擇</option>
                                <option :value="true">可出租</option>
                                <option :value="false">不可出租</option>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- 第二排 -->
                <div class="form-row">
                    <div class="form-col">
                        <div class="form-group">
                            <label for="capacity">容納人數</label>
                            <input
                                type="number"
                                id="capacity"
                                v-model="formData.capacity"
                                min="1"
                                class="form-control"
                            />
                        </div>
                    </div>

                    <div class="form-col">
                        <div class="form-group">
                            <label for="areaPings">場地坪數</label>
                            <input
                                type="number"
                                id="areaPings"
                                v-model="formData.areaPings"
                                min="1"
                                class="form-control"
                            />
                        </div>
                    </div>

                    <div class="form-col">
                        <div class="form-group">
                            <label for="venueHeight">場地高度（公尺）</label>
                            <input
                                type="number"
                                id="venueHeight"
                                v-model="formData.venueHeight"
                                min="2.8"
                                step="0.1"
                                class="form-control"
                            />
                        </div>
                    </div>
                </div>

                <!-- 第三排 -->
                <div class="form-row">
                    <div class="form-col">
                        <div class="form-group">
                            <label for="venueFeeByDay">每日場地費</label>
                            <input
                                type="number"
                                id="venueFeeByDay"
                                v-model="formData.venueFeeByDay"
                                min="0"
                                class="form-control"
                            />
                        </div>
                    </div>

                    <div class="form-col">
                        <div class="form-group">
                            <label for="technicalSpecifications">技術規格檔案</label>
                            <div class="file-input-wrapper">
                                <input
                                    type="file"
                                    id="technicalSpecifications"
                                    @change="handleTechnicalSpecificationsUpload"
                                    class="form-control"
                                />
                                <svg class="file-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"/>
                                    <polyline points="13 2 13 9 20 9"/>
                                </svg>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                            <polyline points="17 21 17 13 7 13 7 21"/>
                            <polyline points="7 3 7 8 15 8"/>
                        </svg>
                        新增場地
                    </button>
                    <button type="button" @click="resetForm" class="btn btn-secondary">
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 12a9 9 0 1 1-9-9c2.52 0 4.93 1 6.74 2.74L21 8"/>
                            <path d="M21 3v5h-5"/>
                        </svg>
                        清除
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import Swal from 'sweetalert2'
import axiosapi from '@/plugins/axios.js'

const emit = defineEmits(['submit-success'])

const formData = ref({
    venueId: '',
    venueName: '',
    rentalStatue: '',
    capacity: null,
    areaPings: null,
    venueHeight: null,
    venueFeeByDay: null,
    technicalSpecifications: null
})

const handleTechnicalSpecificationsUpload = (event) => {
    formData.value.technicalSpecifications = event.target.files[0]
}

const showMessage = (type, message) => {
    const commonConfig = {
        title: message,
        confirmButtonColor: '#4a6741',
        cancelButtonColor: '#d33',
        showClass: {
            popup: 'animate__animated animate__fadeInDown'
        },
        hideClass: {
            popup: 'animate__animated animate__fadeOutUp'
        }
    }

    switch (type) {
        case 'success':
            Swal.fire({
                ...commonConfig,
                icon: 'success',
                timer: 2000,
                timerProgressBar: true,
                showConfirmButton: false,
                background: '#f0f8f0',
                iconColor: '#4a6741'
            })
            break
        case 'error':
            Swal.fire({
                ...commonConfig,
                icon: 'error',
                showConfirmButton: true,
                confirmButtonText: '確定',
                background: '#fff5f5',
                iconColor: '#f27474'
            })
            break
        case 'warning':
            Swal.fire({
                ...commonConfig,
                icon: 'warning',
                showConfirmButton: true,
                confirmButtonText: '確定',
                background: '#fff8f0',
                iconColor: '#f8bb86'
            })
            break
        default:
            Swal.fire({
                ...commonConfig,
                icon: 'info',
                timer: 2000,
                timerProgressBar: true,
                showConfirmButton: false,
                background: '#f0f8ff',
                iconColor: '#3fc3ee'
            })
    }
}
const submitForm = async () => {
    try {
        const submitData = new FormData()

        Object.keys(formData.value).forEach(key => {
            if (formData.value[key] !== null) {
                submitData.append(key, formData.value[key])
            }
        })

        const response = await axiosapi.post('/api/venue/create', submitData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            withCredentials: true
        })

        if (response.data.success) {
            showMessage('success', '場地新增成功！')
            emit('submit-success')
            resetForm()
        } else {
            showMessage('error', response.data.message || '場地新增失敗')
        }
    } catch (error) {
        console.error('新增場地時發生錯誤:', error)
        let errorMessage = '新增場地時發生錯誤'
        
        if (error.response) {
            errorMessage = error.response.data?.message || errorMessage
        } else if (error.request) {
            errorMessage = '無法連接到服務器'
        } else {
            errorMessage = '請求配置錯誤'
        }
        
        showMessage('error', errorMessage)
    }
}

const resetForm = () => {
    formData.value = {
        venueId: '',
        venueName: '',
        rentalStatue: '',
        capacity: null,
        areaPings: null,
        venueHeight: null,
        venueFeeByDay: null,
        technicalSpecifications: null
    }
    // 重置檔案輸入
    const fileInputs = document.querySelectorAll('input[type="file"]')
    fileInputs.forEach(input => {
        input.value = ''
    })
}
</script>

<style scoped>
.venue-form {
    width: 100%;
    /* padding: 20px; */
}

.card {
    background: white;
    border-radius: 8px;
    padding: 24px;
}

.title {
    font-size: 1.25rem;
    font-weight: bold;
    color: var(--text-primary);
    margin-bottom: 24px;
}

.form-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 1rem;
}

.form-control {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid var(--border-color);
    border-radius: 4px;
    font-size: 16px;
    color: var(--text-primary);
    background-color: white;
    transition: all 0.2s;
}

.form-control:focus {
    outline: none;
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(74, 103, 65, 0.1);
}

select.form-control {
    appearance: none;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%235C715E' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 12px center;
    padding-right: 36px;
}

.file-input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
}

.file-icon {
    position: absolute;
    right: 12px;
    color: var(--text-secondary);
    pointer-events: none;
}

input[type="file"] {
    padding-right: 36px;
}

input[type="file"]::-webkit-file-upload-button {
    display: none;
}

input[type="file"]::file-selector-button {
    display: none;
}

label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: var(--text-secondary);
}

.required {
    color: #f56c6c;
}

.form-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-top: 32px;
}

.btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 24px;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.2s;
    border: none;
}

.icon {
    width: 16px;
    height: 16px;
}

.btn-primary {
    background-color: #4A6741;
    color: white;
}

.btn-primary:hover {
    background-color: #3d563a;
}

.btn-secondary {
    background-color: var(--secondary-color);
    color: white;
}

.btn-secondary:hover {
    background-color: #7b9674;
}

@media (max-width: 1200px) {
    .form-row {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 768px) {
    .form-row {
        grid-template-columns: 1fr;
    }
}

/* 移除數字輸入框的箭頭 */
input[type="number"] {
    -moz-appearance: textfield;
}

input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}
</style>