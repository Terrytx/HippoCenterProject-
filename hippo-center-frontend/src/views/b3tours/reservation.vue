<template>
<div class="modal-overlay" v-if="isVisible">
    <div class="modal-content">
        <h2 class="modal-title">預約導覽</h2>

        <!-- 以表格方式呈現導覽資訊 -->
        <table class="reservation-table">
            <tr>
                <th>導覽名稱</th>
                <td>{{ reservationData.toursName }}</td>
            </tr>
            <tr>
                <th>導覽日期</th>
                <td>{{ reservationData.toursDate }}</td>
            </tr>
            <tr>
                <th>導覽敘述</th>
                <td>{{ reservationData.toursDescription }}</td>
            </tr>
            <tr>
                <th>時段</th>
                <td>{{ reservationData.timeSlot }}</td>
            </tr>
            <tr>
                <th>可預約人數</th>
                <td>{{ reservationData.capacity }}</td>
            </tr>
            <tr>
                <th>預約人數</th>
                <td>
                    <input 
                        type="number" 
                        v-model.number="reservationCount" 
                        min="1" 
                        :max="reservationData.capacity" 
                        @input="validateReservationCount"
                        class="reservation-input"
                    />
                    <span v-if="reservationError" class="error-message">{{ reservationError }}</span>
                </td>
            </tr>
        </table>

        <!-- 按鈕 -->
        <div class="actions">
            <button type="button" class="btn btn-primary" @click="submitReservation">送出</button>
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
        </div>
    </div>
</div>
</template>

<script>
import Swal from "sweetalert2";
import useUserStore from "@/stores/user.js"; // 使用 Pinia 的用戶狀態管理
import axiosapi from '@/plugins/axios.js';

export default {
    props: {
        isVisible: Boolean,
        toursId: {
            type: Number,
            required: true,
        },
    },
    data() {
        return {
            reservationData: {
                toursName: "未知",
                toursDate: "未知",
                toursDescription: "未知",
                timeSlot: "未知",
                capacity: "未知",
            },
            reservationCount: 1,
            reservationError: "",
        };
    },
    watch: {
        "reservationData.capacity"(newCapacity){
            if (this.reservationCount > newCapacity){
                this.reservationCount = newCapacity; // 讓輸入框的值不超過上限
                this.validateReservationCount();
            }
        },
        toursId(newVal) {
            if (newVal) {
                this.reservationCount = 1 ; // 當點擊不同導覽時,重設輸入框
                this.reservationError = ""; // 當點擊不同導覽,清除錯訊
                this.fetchReservationData();
            }
        },
        isVisible(newVal) {
            if (newVal) {
                this.fetchReservationData();
            }
        },
    },
    methods: {
        closeModal() {
            this.reservationCount = 1 ; // 關閉時重置輸入框
            this.reservationError = ""; // 關閉時消除錯訊
            this.$emit("close");
        },
        async submitReservation() {
            const userStore = useUserStore();

            // 檢查是否已登入
            if (!userStore.isLogin) {
            Swal.fire({
                title: "需要登入",
                text: "預約導覽需登入會員，將為您跳轉至登入頁面",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "確定",
                cancelButtonText: "取消",
                confirmButtonColor: "#6c757d",  // 修改確認按鈕顏色
            }).then((result) => {
                if (result.isConfirmed) {
                    localStorage.setItem("redirectTo", "/b3tours");
                    this.$router.push("/secure/login");
                }
            });
            return;
        }

            try {
        // 確保預約人數有效
        if (this.reservationCount > 0 && this.reservationCount <= this.reservationData.capacity) {
            const account = localStorage.getItem("account"); // 直接取得會員帳號

            if (!account) {
                throw new Error("未找到有效的會員帳號，請重新登入。");
            }

            // 發送新增預約的請求
            const requestData = {
                toursId: this.toursId, // 導覽行程 ID
                account, // 會員帳號直接傳遞
                numGuests: this.reservationCount, // 符合後端要求的欄位名稱
            };

            const response = await axiosapi.post(
                "/user/reservations/create",
                JSON.stringify(requestData), // 傳遞 JSON 格式字串
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("authToken")}`,
                        "Content-Type": "application/json", // 確保使用 JSON 格式
                    },
                    withCredentials: true,
                }
            );

            if (response.status === 200) {
                Swal.fire({
                    title: "預約成功！",
                    text: "您的導覽預約已成功，確認信已發送至您的 Email。",
                    icon: "success",
                    confirmButtonText: "確定",
                    confirmButtonColor: "#6c757d"
                }).then(() => {
                    this.fetchReservationData(); // 更新資料，動態減少剩餘人數
                    this.closeModal();
                });
            } else {
                Swal.fire({
                    title: "預約失敗",
                    text: "請稍後再試！",
                    icon: "error",
                    confirmButtonText: "確定",
                    confirmButtonColor: "#6c757d" 
                });
            }
        } else {
            this.reservationError = "請輸入有效的預約人數，且不得超過可預約人數。";
        }
    } catch (error) {
        console.error("提交預約時發生錯誤:", error);
        Swal.fire({
            title: "錯誤",
            text: error.response?.data?.message || "系統發生錯誤，請稍後再試！",
            icon: "error",
            confirmButtonText: "確定",
            confirmButtonColor: "#6c757d"
        });
    }
},
        async fetchReservationData() {
            try {
                const response = await axiosapi.get(
                    `/user/tours/${this.toursId}`,
                    {
                        headers: { Authorization: `Bearer ${localStorage.getItem("authToken")}` },
                        withCredentials: true,
                    }
                );

                if (response.data) {
                    this.reservationData = {
                        toursName: response.data.toursName || "無名稱",
                        toursDate: response.data.toursDate || "無日期",
                        toursDescription: response.data.toursDescription || "無敘述",
                        timeSlot: response.data.timeSlot || "無時段",
                        capacity: response.data.capacity || 0, // 確保容納人數
                    };
                    this.reservationCount = 1 ;  // 這行可以讓預約人數回到初值
                    this.reservationError = "";  // 這行則是讓錯訊重置
                }
            } catch (error) {
                console.error("獲取行程資料時發生錯誤:", error);
            }
        },
        validateReservationCount() {
            if (this.reservationCount < 1 || this.reservationCount > this.reservationData.capacity) {
                this.reservationError = `預約人數必須在 1 到 ${this.reservationData.capacity} 之間`;
            } else {
                this.reservationError = "";
            }
        },
    },
};
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 760px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
    text-align: center;
}

/* 表格樣式 */
.reservation-table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 15px;
}

.reservation-table th, 
.reservation-table td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
}

.reservation-table th {
    background-color: #f4f4f4;
    font-weight: bold;
    text-align: center;
}

/* 預約人數輸入框 */
.reservation-input {
    width: 60px;
    padding: 5px;
    text-align: center;
    border: 1px solid #ccc;
    border-radius: 5px;
}

/* 按鈕 */
.actions {
    display: flex;
    justify-content: space-between;
}

.btn-primary {
    background-color: #f8961e;
    color: white;
    padding: 8px 15px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.btn-secondary {
    background-color: #ccc;
    color: black;
    padding: 8px 15px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

/* 錯誤訊息 */
.error-message {
    color: red;
    font-size: 12px;
    margin-top: 5px;
}

</style>
