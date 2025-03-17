<template>
<div class="news-admin">
    <h1>
        消息管理
        <button class="add-news-btn" @click="openNewsModal">新增消息</button>
    </h1>

    <table>
    <thead>
        <tr>
        <th>消息ID</th>
        <th>消息標題</th>
        <th>消息類別</th>
        <th>狀態</th>
        <th>發布日期</th>
        <th>內容</th>
        <th>創建時間</th>
        <th>更新時間</th> 
        <th>操作</th>
        </tr>
    </thead>
    <tbody>
        <tr v-for="news in paginatedNews" :key="news.newsId">
            <td>{{ news.newsId }}</td>
            <td class="title-cell">{{ news.newsTitle }}</td>
            <td>{{ news.newsCategoryName }}</td>
            <td>
                <!-- 如果發布日期大於今天，狀態顯示「尚未上架」 -->
                {{ new Date(news.publishDate) > new Date() ? "尚未上架" : news.newsStatus }}
            </td>
            <td>{{ formatDate(news.publishDate) }}</td>
            <td class="content-cell" :title="news.newsContent">
                {{ news.newsContent }}
            </td>
            <td>{{ formatDateTime(news.createdTime) }}</td>
            <td>{{ formatDateTime(news.updatedTime) }}</td>
            <td class="action-cell">
                <button @click="editNews(news)" class="edit-btn">修改</button>
                <button @click="deleteNews(news.newsId)" class="delete-btn">刪除</button>
            </td>
        </tr>
    </tbody>
    </table>

    <div class="pagination">
    <button @click="prevPage" :disabled="currentPage === 1">上一頁</button>
    <span>第 {{ currentPage }} 頁，共 {{ totalPages }} 頁</span>
    <button @click="nextPage" :disabled="currentPage >= totalPages">下一頁</button>
    </div>

        <!-- 新增消息的彈出視窗 -->
    <div v-if="showNewsModal" class="modal">
        <div class="modal-content">
        <h2>新增消息</h2>
        <label>標題：
            <input v-model="newNews.newsTitle" type="text" class="input-field" />
        </label>
        <label>內容：
            <textarea v-model="newNews.newsContent" class="textarea-field"></textarea>
        </label>
        <label>類別：
            <select v-model="newNews.categoryId" class="select-field">
            <option v-for="category in categories" :key="category.newsCategoryId" :value="Number(category.newsCategoryId)">
                {{ category.newsCategoryName }}
            </option>
            </select>
        </label>
        <label>狀態：
            <select v-model="newNews.newsStatus" class="select-field">
            <option value="上架">上架</option>
            <option value="下架">下架</option>
            </select>
        </label>
        <label>發布日期：
            <input v-model="newNews.publishDate" type="date" class="input-field" />
        </label>

        <div class="modal-actions">
            <button @click="saveNews" class="confirm-btn">提交</button>
            <button @click="closeNewsModal" class="cancel-btn">取消</button>
        </div>
        </div>
    </div>
</div>
</template>

<script>
import axiosapi from '@/plugins/axios.js';
import Swal from "sweetalert2";

export default {
data() {
    return {
    newsList: [],
    categories: [],
    currentPage: 1,
    perPage: 10,
    showNewsModal: false, // 控制新增消息視窗
        newNews: { // 存放新增的消息資料
            newsTitle: "",
            newsContent: "",
            categoryId: null,
            newsStatus: "上架",
            publishDate: new Date().toISOString().split('T')[0], // 預設今天
            createdTime: null,
            updatedTime: null,
        }
    };
},
computed: {
    paginatedNews() {
        const start = (this.currentPage - 1) * this.perPage;
        return this.newsList.slice(start, start + this.perPage).map(news => ({
            ...news,
            newsCategoryName: this.getCategoryName(news.categoryId) // 在這裡動態匹配
        }));
    },
    totalPages() {
    return Math.ceil(this.newsList.length / this.perPage);
    }
},
methods: {
    async deleteNews(newsId) {
    Swal.fire({
        title: '確定要刪除？',
        text: '此操作將永久刪除此消息！',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: "#4A6741",
        cancelButtonColor: '#d33',
        confirmButtonText: '是的，刪除！',
        cancelButtonText: '取消'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                await axiosapi.delete(`/admin/news/${newsId}`);
                Swal.fire({
                    icon: 'success',
                    title: '成功',
                    text: '消息刪除成功！',
                    confirmButtonColor: "#4A6741",
                    confirmButtonText: '確定'
                });
                this.fetchNews(); // 重新獲取消息列表
            } catch (error) {
                console.error("刪除消息時發生錯誤：", error);
                Swal.fire({
                    icon: 'error',
                    title: '錯誤',
                    text: '刪除消息失敗！',
                    confirmButtonColor: "#4A6741",
                    confirmButtonText: '確定'
                });
            }
        }
    });
},
    async fetchNews() {
        try {
            const response = await axiosapi.get('/admin/news', {
                headers: { 'Accept': 'application/json' }
            });

            console.log("獲取消息資料 (API 回應)：", response.data);

            if (!Array.isArray(response.data)) {
                console.error("API 回應的不是陣列，而是：", response.data);
                this.newsList = [];
                return;
            }

            const today = new Date().toISOString().split("T")[0];

            this.newsList = response.data.map(news => {
                let status = news.newsStatus; // 預設狀態

            // 如果發布日期在今天之後，狀態應顯示「尚未上架」
            if (news.publishDate && news.publishDate > today) {
                status = "尚未上架";
            }

            return{
                ...news,
                categoryId: news.categoryId ?? null,  
                newsCategoryName: this.getCategoryName(news.categoryId ?? null),  
                createdTime: news.createdTime ? this.formatDateTime(news.createdTime) : "-", // ✅ 如果為 null，顯示 "-"
                updatedTime: news.updatedTime ? this.formatDateTime(news.updatedTime) : "-", // ✅ 如果為 null，顯示 "-"
                publishDate: news.publishDate ? this.formatDate(news.publishDate) : "-"
            };
        });

            console.log("最終處理後的消息列表：", this.newsList);
        } catch (error) {
            console.error("獲取消息時發生錯誤：", error);
        }
},
    async fetchCategories() {
        try {
            const response = await axiosapi.get('/admin/news-categories', {
                headers: { 'Accept': 'application/json' }
            });

            console.log("獲取類別資料 (API 回應)：", response.data);

            // 轉換成簡單類別陣列，只存 `newsCategoryId` 和 `newsCategoryName`
            this.categories = response.data.map(category => ({
                newsCategoryId: category.newsCategoryId,
                newsCategoryName: category.newsCategoryName
            }));

        } catch (error) {
            console.error("獲取類別時發生錯誤：", error);
            this.categories = [];
        }
},
    async saveNews() {
        if (!this.newNews.categoryId) {
            Swal.fire({
            icon: 'warning',
            title: '警告',
            text: '請選擇消息類別！',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
            return;
        }

        try {
            const postData = { 
                ...this.newNews, 
                category: { newsCategoryId: this.newNews.categoryId }
            };

            if (this.newNews.newsId) {
                //  **修改消息時才更新 `updatedTime`**
                postData.updatedTime = new Date().toISOString();

                await axiosapi.put(`/admin/news/${this.newNews.newsId}`, postData, {
                    headers: { 'Content-Type': 'application/json' }
                });
                Swal.fire({
                icon: 'success',
                title: '成功',
                text: '消息修改成功！',
                confirmButtonColor: "#4A6741",
                confirmButtonText: '確定'
            });
            } else {
                // **新增時不要設定 `updatedTime`**
                postData.createdTime = new Date().toISOString();
                postData.updatedTime = null; // 避免新增時帶入更新時間

                await axiosapi.post('/admin/news', postData, {
                    headers: { 'Content-Type': 'application/json' }
                });
                Swal.fire({
                icon: 'success',
                title: '成功',
                text: '消息新增成功！',
                confirmButtonColor: "#4A6741",
                confirmButtonText: '確定'
            });
            }

            this.closeNewsModal();
            this.fetchNews(); // 重新獲取消息列表，確保 UI 更新
        } catch (error) {
            console.error("儲存消息時發生錯誤：", error);
            Swal.fire({
            icon: 'error',
            title: '錯誤',
            text: '操作失敗！',
            confirmButtonColor: "#4A6741",
            confirmButtonText: '確定'
        });
    }
},
    formatDateTime(datetime) {
            if (!datetime || datetime === "null" || datetime === "Invalid Date") return "-"; // ✅ 確保顯示 "-"
            
            const dateObj = new Date(datetime);
            return isNaN(dateObj.getTime()) ? "-" : dateObj.toLocaleString("zh-TW", { hour12: false });
},
    getCategoryName(categoryId) {
        if (!this.categories || !Array.isArray(this.categories)) {
            console.warn("categories 未初始化，回傳 '未分類'");
            return '未分類';
        }

        if (categoryId === null || categoryId === undefined) {
            console.warn("消息的 categoryId 是 null 或 undefined，回傳 '未分類'");
            return '未分類';
        }

        // 確保 categoryId 是數字來匹配
        const category = this.categories.find(cat => Number(cat.newsCategoryId) === Number(categoryId));
        return category ? category.newsCategoryName : '未分類';
},
    editNews(news) {
        this.newNews = { ...news };  // 先複製消息資料
        this.newNews.categoryId = news.categoryId ?? null; // 確保 categoryId 存在
        this.showNewsModal = true;   // 顯示修改視窗
},
    openNewsModal() {
        this.showNewsModal = true;
},
    closeNewsModal() {
        this.showNewsModal = false;
        this.newNews = {
            newsTitle: "",
            newsContent: "",
            categoryId: null,
            newsStatus: "上架",
            publishDate: new Date().toISOString().split('T')[0]
        };
},
    formatDate(date) {
    return date ? new Date(date).toISOString().split('T')[0] : '';
},
    prevPage() {
    if (this.currentPage > 1) {
        this.currentPage--;
    }
},
    nextPage() {
    if (this.currentPage < this.totalPages) {
        this.currentPage++;
    }
}
},
mounted() {
    this.fetchNews();
    this.fetchCategories();
}
};
</script>

<style scoped>
.news-admin {
padding: 20px;
background-color: #F5F7F4; /* 背景顏色 */
color: #2C3E2D; /* 主要文字顏色 */
}

h1 {
display: flex;
justify-content: space-between;
align-items: center;
color: #2C3E2D; /* 主要文字顏色 */
}

.add-news-btn {
padding: 8px 15px;
background-color: #007bff; /* 主要顏色 */
color: white;
border: none;
border-radius: 5px;
font-size: 14px;
cursor: pointer;
transition: background 0.3s;
}

.add-news-btn:hover {
background-color: #C2D5BB; /* 懸停顏色 */
}

/* 設定表格欄位大小 */
th:nth-child(1), td:nth-child(1) { width: 7%; min-width: 50px; } /* 消息ID */
th:nth-child(2), td:nth-child(2) { width: 35%; } /* 消息標題 */
th:nth-child(3), td:nth-child(3) { width: 8%; min-width: 100px; } /* 消息類別 */
th:nth-child(4), td:nth-child(4) { width: 5%; min-width: 80px; } /* 狀態 */
th:nth-child(5), td:nth-child(5) { width: 9%; min-width: 120px; } /* 發布日期 */
th:nth-child(6), td:nth-child(6) { width: 38%; } /* 內容 */
th:nth-child(7), td:nth-child(7) { width: 10%; min-width: 120px; } /* 創建時間 */
th:nth-child(8), td:nth-child(8) { width: 10%; min-width: 120px; } /* 更新時間 */
th:nth-child(9), td:nth-child(9) { width: 10%; min-width: 120px; text-align: center; }

/* 表格樣式 */
table {
width: 100%;
max-width: 100%; /* 確保不會超過 */
table-layout: fixed;
border-collapse: collapse;
background-color: white;
border: 1px solid #D8E0D5;
margin-top: 10px;
}

th, td {
border: 1px solid #D8E0D5 !important; /* 邊框顏色 */
padding: 12px;
text-align: left;
}

th {
background-color: #4A6741; /* 主要顏色 */
color: white;
}

/* 限制「消息標題」欄位寬度 */
th:nth-child(2), td:nth-child(2) {
width: 250px;
max-width: 250px;
min-width: 250px;
}

.title-cell, .content-cell {
overflow: hidden;
white-space: normal;
word-wrap: break-word; /* 讓長單字自動斷行 */
}

/* 確保「操作」欄位沒有外框 */
td.action-cell {
    justify-content: center; /* 讓它們置中 */
    align-items: center;
}

/* 操作欄位 */
.action-cell {
    
flex-wrap: nowrap;
justify-content: center;
align-items: center;
white-space: nowrap;
}

/* 按鈕樣式 */
.edit-btn, .delete-btn {
padding: 5px 10px;
border: none;
cursor: pointer;
border-radius: 5px;
font-size: 14px;
transition: background 0.3s;
}

.edit-btn {
background-color: #8AA682; /* 次要顏色 */
color: white;
margin-right: 4px;
}

.edit-btn:hover {
background-color: #C2D5BB; /* 懸停顏色 */
}

.delete-btn {
background-color: #dc3545;
color: white;
}

.delete-btn:hover {
background-color: #a71d2a;
}

/* 分頁按鈕 */
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
font-size: 14px;
border-radius: 5px;
}

.pagination button:disabled {
background-color: #D8E0D5; /* 邊框顏色 */
}

/* Modal 彈出視窗 */
.modal {
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
background: #F5F7F4; /* 背景顏色 */
padding: 20px;
border-radius: 8px;
box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
width: 400px;
}

.modal h2 {
color: #2C3E2D; /* 主要文字顏色 */
margin-bottom: 15px;
}

.input-field, .select-field, .textarea-field {
width: 100%;
padding: 8px;
margin-top: 5px;
border: 1px solid #D8E0D5; /* 邊框顏色 */
border-radius: 5px;
}

.textarea-field {
height: 100px;
}

.modal-actions {
display: flex;
justify-content: space-between;
margin-top: 15px;
}

.confirm-btn {
background-color: #4A6741; /* 主要顏色 */
color: white;
padding: 8px 15px;
border: none;
border-radius: 5px;
cursor: pointer;
}

.cancel-btn {
background-color: #dc3545;
color: white;
padding: 8px 15px;
border: none;
border-radius: 5px;
cursor: pointer;
}

.confirm-btn:hover, .cancel-btn:hover {
opacity: 0.8;
}

</style>
