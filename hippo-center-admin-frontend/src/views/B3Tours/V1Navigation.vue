<template>
    <div>
        <div class="dashboard-header">
            <!-- 頁籤選單 -->
            <div class="tabs-container">
                <button 
                    class="button-tab" 
                    :class="{ 'active-tab': activeTab === 'tour' }" 
                    @click="activeTab = 'tour'">
                    <i class="fas fa-users"></i> 導覽管理
                </button> 
                <button 
                    class="button-tab" 
                    :class="{ 'active-tab': activeTab === 'reservation' }" 
                    @click="activeTab = 'reservation'">
                    <i class="fas fa-ticket-alt"></i> 預約管理
                </button>
            </div>
        </div>

    <!-- 導覽管理頁面 -->
    <div v-show="activeTab === 'tour'">
        <div class="stats-grid">
            <div class="stat-card" v-for="stat in stats" :key="stat.title">
                <div class="stat-icon">
                    <i :class="stat.icon"></i>
                </div>
                <div class="stat-details">
                    <h3>{{ stat.title }}</h3>
                    <p style="font-size: 1.5rem; font-weight: bold;">{{ stat.value }}</p>
                </div>
            </div>
        </div>

    <!-- 導覽新增區塊 -->
    <div class="form-container">
        <h3>{{ newTour.toursId ? "修改導覽" : "新增導覽" }}</h3>
        <form @submit.prevent="handleTour">
        <div class="form-group">
            <label>導覽 ID (輸入以修改)</label>
            <input v-model="newTour.toursId" type="number" @blur="loadTour" />
        </div>

        <div class="form-group">
            <label>導覽名稱</label>
            <input v-model="newTour.toursName" type="text" required />
        </div>

        <div class="form-group">
            <label>導覽日期</label>
            <input v-model="newTour.toursDate" type="date" required />
        </div>

        <div class="form-group">
            <label>導覽說明</label>
            <textarea v-model="newTour.toursDescription"></textarea>
        </div>

        <div class="form-group">
            <label>場次</label>
            <select v-model="newTour.toursSession" required>
                <option value="場次1">場次1</option>
                <option value="場次2">場次2</option>
                <option value="場次3">場次3</option>
                <option value="場次4">場次4</option>
                <option value="場次5">場次5</option>
                <option value="場次6">場次6</option>
            </select>
        </div>

        <div class="form-group">
            <label>時間區段</label>
            <select v-model="newTour.timeSlot" required>
                <option value="10:00-12:00">10:00-12:00</option>
                <option value="13:00-15:00">13:00-15:00</option>
                <option value="17:00-19:00">17:00-19:00</option>
            </select>
        </div>

        <div class="form-group">
            <label>可預約人數</label>
            <input v-model="newTour.capacity" type="number" required min="1" />
        </div>

        <div class="form-group">
            <label>狀態</label>
            <select v-model="newTour.toursStatus" required>
            <option value="開放報名">開放報名</option>
            <option value="已結束">已結束</option>
            <option value="已取消">已取消</option>
            <option value="已額滿">已額滿</option>
            </select>
        </div>

        <button type="submit">{{ newTour.toursId ? "修改導覽" : "新增導覽" }}</button>
        <button type="button" @click="resetForm" class="clear-btn">清除</button>
        </form>
    </div>


    <!-- 導覽列表（表格格式）+ 篩選條件 -->
    <div class="table-header">
        <h3>導覽列表</h3>

        <div class="filter-container">
            <label>篩選狀態：</label>
                <select v-model="selectedFilter">
                    <option value="全部">全部</option>
                    <option value="開放報名">開放中</option>
                    <option value="已結束">已結束</option>
                </select>

            <!-- 排序按鈕 -->
            <button @click="toggleSortOrder" class="sort-btn">
                排序：{{ sortOrder === 'asc' ? '⬆️' : '⬇️' }}
            </button>
        </div>
    </div>

    <table class="tour-table">
        <thead>
        <tr>
            <th>導覽 ID</th>
            <th>導覽名稱</th>
            <th>導覽日期</th>
            <th>場次</th>
            <th>時間區段</th>
            <th>可預約人數</th>
            <th>狀態</th>
            <th>創建時間</th>
            <th>最後更新時間</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
            <tr v-for="tour in paginatedTours" :key="tour.toursId">
                <td>{{ tour.toursId }}</td>
                <td>{{ tour.toursName }}</td>
                <td>{{ tour.toursDate }}</td>
                <td>{{ tour.toursSession }}</td>
                <td>{{ tour.timeSlot }}</td>
                <td>{{ tour.capacity }}</td>
                <td>{{  tour.toursStatus }}</td>
                <td>{{  formatDate(tour.createdAt) }}</td>
                <td>{{  formatDate(tour.updatedAt) }}</td>
                <td>
                    <button @click="editTour(tour.toursId)" class="edit-btn">修改</button>
                    <button @click="deleteTour(tour.toursId)" class="delete-btn">刪除</button>
                </td>
            </tr>
        </tbody>
    </table>

    <div class="pagination">
        <button @click="prevPage" :disabled="currentPage === 1">上一頁</button>
        <span>第 {{ currentPage }} 頁，共 {{ totalPages }} 頁</span>
        <button @click="nextPage" :disabled="currentPage >= totalPages">下一頁</button>
    </div>
</div>
    <!-- 預約管理頁面 -->
    <div v-show="activeTab === 'reservation'">
        <!-- 左邊框 - 預約管理 + 日期搜尋 -->
        <div class="search-container">
            <div class="reservation-box">
            <h3>預約管理</h3>
            <label>開始日期 :</label>
            <input type="date" v-model="search.startDate">
            <label>結束日期 :</label>
            <input type="date" v-model="search.endDate">
            <button @click="fetchReservations">搜尋</button>
        </div>

        <!-- 右邊框 - 根據導覽 ID 搜尋 -->
    <div class="tour-search-box">
        <h3>導覽 ID 查詢</h3>
            <label>導覽 ID :</label>
            <input type="number" v-model="search.toursId" placeholder="輸入導覽 ID">
            <button @click="fetchReservationsByTour">搜尋</button>
        </div>
    </div>

<!-- 會員預約清單 Modal -->
<div v-if="showMemberReservations" class="modal-overlay">
    <div class="modal-content">
        <h3>會員 {{ selectedMember }} 的預約列表</h3>
        <table class="member-reservation-table">
            <thead>
                <tr>
                    <th>導覽 ID</th>
                    <th>導覽名稱</th>
                    <th>導覽日期</th>
                    <th>場次</th>
                    <th>時間區段</th>
                    <th>預約人數</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="reservation in memberReservations" :key="reservation.reservationId">
                    <td>{{ reservation.toursId }}</td>
                    <td>{{ reservation.toursName }}</td>
                    <td>{{ reservation.toursDate }}</td>
                    <td>{{ reservation.toursSession }}</td>
                    <td>{{ reservation.timeSlot }}</td>
                    <td>{{ reservation.peopleCount }}</td>
                </tr>
            </tbody>
        </table>
        <button @click="showMemberReservations = false">關閉</button>
    </div>
</div>

            <!-- 預約列表 -->
            <div class="reservation-list">
            <h3>預約列表</h3>
            <table class="reservation-table">
                    <thead>
                        <tr v-if="!isTourSearch">
                            <th>預約 ID</th>
                            <th>預約日期</th>
                            <th>會員姓名</th>
                            <th>會員帳號</th>
                            <th>導覽ID</th>
                            <th>導覽名稱</th>
                            <th>導覽日期</th>
                            <th>時間區段</th>
                            <th>預約人數</th>
                        </tr>
                        <tr v-else>
                            <th>導覽 ID</th>
                            <th>導覽日期</th>
                            <th>導覽名稱</th>
                            <th>會員姓名</th>
                            <th>會員帳號</th>
                            <th>預約人數</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="!isTourSearch" v-for="reservation in reservations" :key="reservation.reservationId">
                            <td>{{ reservation.reservationId }}</td>
                            <td>{{ formatDate(reservation.reservationDate) }}</td>
                            <td>{{ reservation.membername }}</td>
                            <td>
                                <a href="#" @click.prevent="fetchReservationsByMember(reservation.memberAccount)">
                                    {{ reservation.memberAccount }}
                                </a>
                            </td>
                            <td>{{ reservation.toursId }}</td>
                            <td>{{ reservation.toursName || '未提供' }}</td>
                            <td>{{ reservation.toursDate || '未提供' }}</td>
                            <td>{{ reservation.timeSlot || '未提供' }}</td>
                            <td>{{ reservation.peopleCount }}</td>
                        </tr>
                        <tr v-else v-for="reservation in reservations" :key="reservation.toursId">
                            <td>{{ reservation.toursId }}</td>
                            <td>{{ reservation.toursDate }}</td>
                            <td>{{ reservation.toursName }}</td>
                            <td>{{ reservation.membername }}</td>
                            <td><a href="#" @click.prevent="fetchReservationsByMember(reservation.memberAccount)">
                                {{ reservation.memberAccount }}
                            </a>
                            </td>
                            <td>{{ reservation.peopleCount }}</td>
                        </tr>
                    </tbody>
                        <!-- 只有在「導覽 ID 查詢」時顯示總計行 -->
                        <tfoot v-if="isTourSearch">
                            <tr>
                                <td colspan="5" class="total-label">總計</td>
                                <td class="total-value">{{ totalPeopleCount }}</td>
                            </tr>
                        </tfoot>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import axiosapi from '@/plugins/axios.js';
import Swal from "sweetalert2";

export default {
name: 'Navigation',
data() {
    return {
        showMemberReservations: false, // 控制 Modal 顯示
        selectedMember: "", // 當前選中的會員帳號
        memberReservations: [], // 該會員的預約紀錄
        activeTab: 'tour', // 預設顯示導覽管理
        search: {
            startDate: '',
            endDate: '',
            toursId: '',
        },
        isTourSearch: false, // true: 用導覽 ID 查詢，false: 用時間查詢
        selectedTour: null, // 儲存符合條件的導覽
        reservations: [], // 預約列表
        tours: [],
        selectedFilter:'全部',
        newTour: {
            toursId:'',
            toursName: '',
            toursDate: '',
            toursDescription: '',
            toursSession: '',
            timeSlot: '',
            capacity: 30,
            toursStatus: '開放報名',
            },
        sortOrder: 'asc', // 'asc'表示升序,'desc'表示降序
        currentPage: 1, // 當前頁數
        itemsPerPage: 50, // 每頁顯示筆數
        };
    },
    computed: {
        totalPeopleCount() {
        if (!this.isTourSearch) return 0; // 只有在導覽 ID 查詢時才計算
        return this.reservations.reduce((sum, reservation) => sum + (reservation.peopleCount || 0), 0);
    },
        totalPages() {
            return this.filteredTours.length > 0 ? Math.ceil(this.filteredTours.length / this.itemsPerPage) : 1;
    },
        paginatedTours() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.filteredTours.slice(start, start + this.itemsPerPage);
    },
        filteredTours() {
            let sortedTours = [...this.tours]; // 複製原始陣列，避免影響原始資料
            
            // 過濾條件（原有的篩選功能）
            sortedTours = sortedTours.filter(tour => {
                const today = new Date().toISOString().split("T")[0];
                
                if (this.selectedFilter === "全部") return true;
                if (this.selectedFilter === "開放報名") {
                    return tour.toursStatus?.trim() === "開放報名" || tour.toursStatus?.trim() === "開放" || tour.toursStatus === "已額滿";
                }
                if (this.selectedFilter === "已結束") return tour.toursStatus === "已結束";
                return false;
            });

            console.log("篩選後的導覽:", sortedTours);

            // 排序功能
            sortedTours.sort((a, b) => {
                if (this.sortOrder === 'asc') {
                    return a.toursId - b.toursId;
                } else {
                    return b.toursId - a.toursId;
                }
            });
            this.currentPage = 1; // 每次篩選時重置分頁
            return sortedTours;
        }
    },
async mounted(){
    await this.fetchTours();
},
methods: {
    prevPage() {
        if (this.currentPage > 1) {
            this.currentPage--;
        }
},
    nextPage() {
        if (this.currentPage < this.totalPages) {
            this.currentPage++;
        }
},
    toggleSortOrder() {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
},
    async fetchReservationsByMember(memberAccount) {
    try {
        const requestData = { memberAccount: memberAccount }; // 確保 key 名稱正確

        const response = await axiosapi.post(
            "/admin/reservations/findmember",
            requestData,
            { headers: { "Content-Type": "application/json" } }
        );

        console.log("會員預約列表 API 回應:", response.data);

        if (response.data && response.data.length > 0) {
            this.selectedMember = memberAccount;

            // 確保 API 回應包含完整的導覽資訊
            this.memberReservations = response.data.map(reservation => ({
                toursId: reservation.toursId,
                toursName: reservation.toursName || "無資料",
                toursDate: reservation.toursDate || "無資料",
                toursSession: reservation.toursSession || "未知場次",
                timeSlot: reservation.timeSlot || "未知時間",
                peopleCount: reservation.peopleCount
            }));

            this.showMemberReservations = true; // 確保 Modal 顯示
            this.$forceUpdate(); // 強制 Vue 更新
        } else {
            this.memberReservations = [];
            this.showMemberReservations = false;
            Swal.fire({
                icon: 'info',
                title: '提示',
                text: '該會員沒有預約紀錄',
                confirmButtonColor: "#4A6741",
                confirmButtonText: '確定'
            });
        }
    } catch (error) {
        console.error("查詢會員預約失敗:", error);
        Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '查詢失敗，請稍後再試',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
},
    formatDate(dateString) {
            if (!dateString || dateString === "null" || dateString === "N/A") return "-"; // ✅ 直接顯示 "-"
            
            const date = new Date(dateString);
            
            // 如果解析的日期無效，則顯示 "-"
            if (isNaN(date.getTime())) return "-";

            // 格式化數字，確保兩位數
            const pad = (num) => String(num).padStart(2, '0');

            return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ` + 
            `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
},
    async fetchReservations() {
    console.log("送出查詢請求:", this.search);

    if (!this.search.startDate || !this.search.endDate) {
        Swal.fire({
            icon: 'warning',
            title: '警告',
            text: '請選擇開始日期和結束日期！',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
        return;
    }

    const endDateTime = new Date(this.search.endDate);
    endDateTime.setHours(23,59,59,999); // 設定為當天的23:59:59

    try {
        const requestData = {
            startDate: this.search.startDate,
            endDate: endDateTime.toISOString().split("T")[0], // 保證包含整天
        };

        console.log("發送請求 JSON:", requestData);

        const response = await axiosapi.post(
            '/admin/reservations/findByDate',
            requestData,
            {
                headers: { "Content-Type": "application/json" }
            }
        );

        console.log("API 回應:", response.data);

        // **問題: 如果 response.data 為空，應該顯示查無預約**
        if (!response.data || response.data.length === 0) {
            Swal.fire({
                icon: 'info',
                title: '查無資料',
                text: '查無預約資料！',
                confirmButtonColor: "#4A6741",
                confirmButtonText: '確定'
            });
            this.reservations = [];
            return;
        }

        // **確保 `this.tours` 陣列已加載**
        if (!this.tours || this.tours.length === 0) {
            console.warn("⚠️警告：尚未載入 `tours`，將無法匹配導覽名稱！");
        }

        // **格式化 API 回傳的 `reservationDate` 並補上 `toursName`**
        this.reservations = response.data.map(reservation => {
            // **在 `this.tours` 陣列中尋找 `toursId` 相同的導覽**
            const matchingTour = this.tours.find(t => t.toursId === reservation.toursId);

            return {
                ...reservation,
                toursName: matchingTour ? matchingTour.toursName : "未提供", // 確保 `toursName` 存在
                toursDate: matchingTour ? matchingTour.toursDate : "未提供",
                timeSlot: matchingTour ? matchingTour.timeSlot : "未提供",
                reservationDate: reservation.reservationDate || null
            };
        });

        // **重設 `isTourSearch = false`，確保是時間查詢模式**
        this.isTourSearch = false;

        console.log("最終處理後的預約列表:", this.reservations);

    } catch (error) {
        console.error("查詢失敗:", error);
        Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '查詢失敗，請稍後再試',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
},
    async fetchReservationsByTour() {
            if (!this.search.toursId) {
                Swal.fire({
            icon: 'warning',
            title: '警告',
            text: '請輸入導覽 ID',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
        return;
            }
            try {
            const response = await axiosapi.get(`/admin/reservations/byTour/${this.search.toursId}`);
            console.log("API 回應 - 查詢導覽預約者:", response.data);

            this.isTourSearch = true; // 設定為導覽ID查詢模式

            if (response.data && response.data.length > 0) {
                this.reservations = response.data.map(reservation => ({
                    toursId: reservation.toursId,
                    toursDate: reservation.toursDate,  
                    toursName: reservation.toursName,  
                    membername: reservation.membername,
                    memberAccount: reservation.memberAccount,
                    peopleCount: reservation.peopleCount
                }));
            } else {
                Swal.fire({
                icon: 'info',
                title: '查無資料',
                text: '查無此導覽的預約資料！',
                confirmButtonColor: "#4A6741",
                confirmButtonText: '確定'
            });
                this.reservations = [];
            }
        } catch (error) {
            console.error("查詢失敗:", error);
            Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '查詢失敗，請稍後再試',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
},
    async fetchTours() {
        try {
            const response = await axiosapi.get('/admin/tours');
            console.log("API 回應:", response.data); // 確認有新資料

            const today = new Date().toISOString().split("T")[0];

            this.tours = response.data.map(tour => {
                
                // 若可預約人數為 0，且導覽尚未結束，則狀態設為 "已額滿"
                if (tour.capacity === 0 && tour.toursDate >= today) {
                    tour.toursStatus = "已額滿";
                }

                // 如果導覽日期過了,狀態會變成已結束
                else if (tour.toursDate < today) {
                    tour.toursStatus = "已結束";
                }

                return {
                    ...tour, 
                    createdAt: tour.createdAt || "-", // ✅ 如果 `null`，顯示 `"-"`
                    updatedAt: tour.updatedAt || "-"  // ✅ 如果 `null`，顯示 `"-"`
                };
            });

            this.$forceUpdate(); // 確保 Vue 重新渲染
        } catch (error) {
            console.error("無法取得導覽資料:", error);
            Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '無法取得導覽資料，請檢查後端是否正常運行。',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
},
    async loadTour() {
        if (!this.newTour.toursId){
            this.resetForm(); // 若toursId被清空,重置表單
            return;
        }
        try {
        const response = await axiosapi.get(`/admin/tours/${this.newTour.toursId}`);
        this.newTour = response.data;
        } catch (error) {
            Swal.fire({
            icon: 'info',
            title: '查無資料',
            text: '查無此導覽',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
        this.resetForm(); // 若查無導覽,也重置表單
        }
    },
    async handleTour() {
        if (this.newTour.toursId) {
        await this.updateTour();
        } else {
        await this.addTour();
        }
    },
    async addTour() {
            try {
                await axiosapi.post('/admin/tours', this.newTour, {
                    headers: { "Content-Type": "application/json" }
                });

                Swal.fire({
                        icon: 'success',
                        title: '成功',
                        text: '導覽已新增！',
                        confirmButtonColor: "#4A6741",
                        confirmButtonText: '確定'
                    });
                await this.fetchTours();
                this.resetForm();
            } catch (error) {
                console.error('新增導覽失敗:', error);
                Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '無法新增導覽，請稍後再試。',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
        },
        resetForm() {
            this.newTour = {
                toursId:'',
                toursName: '',
                toursDate: '',
                toursDescription: '',
                toursSession: '',
                timeSlot: '',
                capacity: 30,
                toursStatus: '開放報名',
            };
    },
    async updateTour() {
        try {
        await axiosapi.put(`/admin/tours/${this.newTour.toursId}`, this.newTour);
        Swal.fire({
            icon: 'success',
            title: '成功',
            text: '導覽已修改！',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
        await this.fetchTours();
        this.resetForm();
        } catch (error) {
            Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '無法修改導覽',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
    },
    async deleteTour(toursId) {
    Swal.fire({
        title: '確定要刪除？',
        text: `導覽 ID: ${toursId} 將被刪除！`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: "#4A6741",
        cancelButtonColor: '#d33',
        confirmButtonText: '是的，刪除！',
        cancelButtonText: '取消'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                await axiosapi.delete(`/admin/tours/${toursId}`);
                Swal.fire({
                    icon: 'success',
                    title: '成功',
                    text: '導覽已刪除！',
                    confirmButtonColor: "#4A6741",
                    confirmButtonText: '確定'
                });
                await this.fetchTours();
            } catch (error) {
                console.error("刪除導覽失敗:", error);
                Swal.fire({
                    icon: 'error',
                    title: '錯誤',
                    text: '該導覽已有預約，不可刪除。',
                    confirmButtonColor: "#4A6741",
                    confirmButtonText: '確定'
                });
            }
        }
    });
},
    async editTour(toursId) {
        try {
            const response = await axiosapi.get(`/admin/tours/${toursId}`);
            this.newTour = response.data; // 填入資料
            this.activeTab = 'tour'; // 確保顯示導覽管理頁面

            // 確保 Vue 完成渲染後再滾動
            this.$nextTick(() => {
                const formContainer = document.querySelector(".form-container");
                if (formContainer) {
                    formContainer.scrollIntoView({ behavior: "smooth", block: "start" });
                }
            });
        } catch (error) {
            Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '無法加載導覽資料',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
    },
}
};
</script>

<style scoped>
.dashboard-header {
    display: flex; /* 讓內部元素排成一列 */
    justify-content: space-between; /* 左右對齊,標題在左、按鈕在右 */
    align-items: center;
    margin-bottom: 2rem; /* 增加底部間距,跟下面內容區分開 */
}

.dashboard-header h2 {
    font-size: 1.5rem;
    color: var(--text-primary);
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
}

.stat-card {
    background-color: #fff;
    padding: 1.5rem;
    border-radius: 0.5rem;
    box-shadow: 0 2px 4px rgba(74, 103, 65, 0.1);
    display: flex;
    gap: 1rem;
    border: 1px solid var(--border-color);
}

.stat-icon {
    background-color: var(--hover-color);
    color: var(--primary-color);
    width: 48px;
    height: 48px;
    border-radius: 0.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
}

.stat-details h3 {
    color: var(--text-secondary);
    font-size: 0.875rem;
    margin-bottom: 0.5rem;
}

/* 新增導覽表單 */
.form-container {
background: #fff;
padding: 1.5rem;
border-radius: 0.5rem;
box-shadow: 0 2px 4px rgba(74, 103, 65, 0.1);
margin-top: -33px;
margin-bottom: 2rem;
border: 1px solid var(--border-color);
}

.form-group {
display: flex;
flex-direction: column;
margin-bottom: 1rem;
}

input, textarea, select {
padding: 0.5rem;
border: 1px solid #ccc;
border-radius: 4px;
}

textarea {
    height: 120px; /* 你可以調整這個數值，例如 200px、250px */
}

button {
padding: 8px 15px;
background-color: #007bff;
color: white;
border: none;
border-radius: 5px;
cursor: pointer;
font-size: 14px;
}

button:hover {
background-color: #0056b3;
}

/* 導覽列表 */
.tour-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1rem;
}

.tour-table th, .tour-table td {
    padding: 0.8rem;
    border: 1px solid var(--border-color);
    text-align: center;
    white-space: nowrap; /* 避免時間換行 */
}

.tour-table th {
    background-color: #4A6741;
    color: white;
}

/* 設定各欄位的最小寬度 */
.tour-table th:nth-child(1), 
.tour-table td:nth-child(1) { 
    width: 7%;  /* 導覽 ID */
}

.tour-table th:nth-child(2), 
.tour-table td:nth-child(2) { 
    width: 15%;  /* 導覽名稱 */
}

.tour-table th:nth-child(3), 
.tour-table td:nth-child(3) { 
    width: 8%;  /* 導覽日期 */
}

.tour-table th:nth-child(4), 
.tour-table td:nth-child(4) { 
    width: 6%;  /* 場次 */
}

.tour-table th:nth-child(5), 
.tour-table td:nth-child(5) { 
    width: 8%;  /* 時間區段 */
}

.tour-table th:nth-child(6), 
.tour-table td:nth-child(6) { 
    width: 8%;  /* 可預約人數 */
}

.tour-table th:nth-child(7), 
.tour-table td:nth-child(7) { 
    width: 10%;  /* 狀態 */
}

.tour-table th:nth-child(8), 
.tour-table td:nth-child(8) { 
    width: 17%;  /* 創建時間 */
}

.tour-table th:nth-child(9), 
.tour-table td:nth-child(9) { 
    width: 17%;  /* 最後更新時間 */
}

.tour-table th:nth-child(10), 
.tour-table td:nth-child(10) { 
    width: 10%;  /* 操作 (修改、刪除按鈕) */
    text-align: center;
}

.tour-table tbody {
    background-color: white; /* 表格底色 */
}

.status {
    padding: 0.3rem 0.5rem;
    border-radius: 4px;
    color: white;
}

.status.open {
    background-color: #28a745;
}

.status.full {
    background-color: #dc3545;
}

.status.canceled {
    background-color: #6c757d;
}

/* 刪除按鈕 */
.delete-btn {
    padding: 5px 10px;
    background-color: #dc3545;
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.delete-btn:hover {
    background-color: #a71d2a;
}

/* 讓導覽列表標題與篩選條件在同一排 */
.table-header {
    display: flex;
    align-items: center; /* 確保內容垂直對齊 */
    justify-content: space-between; /* 讓內容左右對齊 */
    margin-bottom: 1rem; /* 與表格的間距 */
}

/* 讓篩選選單靠近標題 */
.filter-container {
    display: flex;
    align-items: center;
    gap: 10px; /* 設定篩選選單與排序按鈕的間距 */
    margin-left: auto; /* 讓篩選條件稍微遠離標題 */
}

/* 美化篩選選單 */
.filter-container select {
    padding: 0.5rem;
    border: 1px solid var(--border-color);
    border-radius: 0.5rem;
    background-color: white;
    cursor: pointer;
}

/* 頁籤樣式 */
.tabs-container {
    display: flex;
    gap: 1rem;
    margin-bottom: 1rem;
}

.tabs-container button {
    padding: 0.7rem 1.5rem;
    border: none;
    cursor: pointer;
    font-size: 1rem;
    background-color: #8AA682; /* 設定綠色背景 */
    color: white; /* 文字顏色為白色 */
    min-width: 120px; /* 設定按鈕最小寬度 */
    padding: 0.7rem 2rem; /* 調整內邊距讓按鈕更長 */
}

.tabs-container .active-tab {
    background-color: #4A6741; /* 點擊後變色 */
    color: white;
    font-weight: bold; /* 讓選中的頁籤字體加粗 */
}

/* 讓搜尋框左右對齊 */
.search-container {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 20px;
}

.search-container label {
    margin-right: 10px;  /* 增加標籤與輸入框的距離 */
}

.search-container input {
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 4px;
    min-width: 150px;  /* 設定輸入框最小寬度，避免過短 */
}

.search-container button {
    padding: 0.5rem 1rem;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-left: 30px; /* 讓按鈕與輸入框之間留點間距 */
}

.search-container button:hover {
    background-color: #0056b3;
}

.reservation-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1rem;
}

.reservation-table th, .reservation-table td {
    padding: 0.8rem;
    border: 1px solid var(--border-color);
    text-align: left;
}

.reservation-table th {
    background-color: var(--hover-color);
    color: var(--text-primary);
}

.reservation-container {
    display: flex;
    gap: 20px; /* 讓左右區塊有間距 */
}

/* 左右框的樣式 */
.reservation-box, .tour-search-box {
    flex: 1;
    padding: 20px;
    border: 2px solid #ccc;
    border-radius: 10px;
    background-color: #f9f9f9;
}

/* 讓預約列表獨立顯示 */
.reservation-list {
    margin-top: 20px;
}

.edit-btn {
    padding: 5px 10px;
    background-color: #8AA682;
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
    margin-right: 12px; /* 與刪除按鈕的間距 */
    transition: background-color 0.3s ease;
}

.edit-btn:hover {
    background-color: #C2D5BB;
}

/* 讓總計靠右對齊 */
.total-label {
    font-weight: bold;
    text-align: right;
    background-color: #C2D5BB; /* 綠色背景 */
    color: #2C3E2D; /* 白色字 */
    padding: 10px;
}

/* 讓總計數字對齊 "預約人數" 欄位 */
.total-value {
    font-weight: bold;
    text-align: right;
    background-color: #f1f1f1;
    color: #dc3545;
    padding: 10px;
}

/* Modal 背景 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
}

/* Modal 內容 */
.modal-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    min-width: 600px;
    text-align: center;
}

.modal-content table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
}

.modal-content th, .modal-content td {
    border: 1px solid #ddd;
    padding: 10px;
}

.modal-content button {
    margin-top: 15px;
    padding: 10px 20px;
    background: #007bff;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 4px;
}

.clear-btn {
background-color: #dc3545;
color: white;
margin-left: 10px;
padding: 8px 15px;
border: none;
border-radius: 5px;
font-size: 14px;
}

.clear-btn:hover {
    background-color: #a71d2a;
}

/* 正序倒序按鈕 */
.sort-btn {
    margin-left: auto; /* 讓按鈕推到最右邊 */
    padding: 6px 10px;
    background-color: #8AA682;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1rem;
}

.sort-btn:hover {
    background-color: #4A6741;
}

/* 分頁美化 */
.pagination {
    margin-top: 10px;
    text-align: center;
}

.pagination button {
    padding: 5px 10px;
    margin: 0 5px;
    background-color: #4A6741; /* 主要顏色 */
    color: white;
    border: none;
    cursor: pointer;
}

.pagination button:disabled {
    background-color: #D8E0D5; /* 變灰，表示不可點擊 */
}

</style>