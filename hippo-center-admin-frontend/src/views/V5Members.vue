<template>
  <div class="container">
    <!-- 會員管理頁面標題 -->
    <div class="dashboard-header">
      <h2>會員管理</h2>
      <div class="header-actions">
        <div class="search-bar">
          <input v-model="searchKeyword" type="text" placeholder="搜尋...">
          <button @click="searchMembers">
            <i class="fas fa-search"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- 會員列表包裹容器 -->
    <div class="member-table-container">
      <button class="admin" @click="addAdmin(members)">新增管理員</button>
      <table class="member-table">
        <thead>
          <tr>
            <th @click="sortTable('memberId')">會員ID <i :class="getSortIcon('memberId')"></i></th>
            <th>帳號 </th>
            <th>姓名 </th>
            <th>電話 </th>
            <th>生日 </th>
            <th @click="sortTable('address')">地址 <i :class="getSortIcon('address')"></i></th>
            <th @click="sortTable('memberType')">使用權限 <i :class="getSortIcon('memberType')"></i></th>
            <th @click="sortTable('createDate')">註冊日期 <i :class="getSortIcon('createDate')"></i></th>
            <th @click="sortTable('modifyDate')">修改日期 <i :class="getSortIcon('modifyDate')"></i></th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="member in members" :key="member.memberId">
            <td>{{ member.memberId }}</td>
            <td>{{ member.account }}</td>
            <td>{{ member.name }}</td>
            <td>{{ member.phone || "未填寫" }}</td>
            <td>{{ member.birthday || "未填寫" }}</td>
            <td>{{ member.address || "未填寫" }}</td>
            <td>{{ member.memberType === 'admin' ? '管理員' : '一般使用者' }}</td>
            <td>{{ member.createDate }}</td>
            <td>{{ member.modifyDate }}</td>
            <td>
              <button @click="editMember(member)" class="edit-btn">編輯</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分頁控制 -->
    <div class="pagination">
      <button @click="changePage(currentPage - 1)" :disabled="currentPage <= 0">上一頁</button>
      <span>頁數: {{ currentPage + 1 }} / {{ totalPages }}</span>
      <button @click="changePage(currentPage + 1)" :disabled="currentPage >= totalPages - 1">下一頁</button>
    </div>

    <!-- 編輯會員彈出視窗 -->
    <div v-if="isEditing" class="edit-modal">
      <div class="modal-content">
        <h2>編輯會員</h2>
        <form @submit.prevent="updateMember">

          <label for="account">帳號</label>
          <input v-model="currentMember.account" type="text" id="account" required />

          <label for="name">姓名</label>
          <input v-model="currentMember.name" type="text" id="name" required />

          <label for="phone">電話</label>
          <input v-model="currentMember.phone" type="text" id="phone" />

          <label for="birthday">生日</label>
          <input v-model="currentMember.birthday" type="date" id="birthday" />

          <label for="address">地址</label>
          <input v-model="currentMember.address" type="text" id="address" />

          <label for="memberType">使用權限</label>
          <select v-model="currentMember.memberType" id="memberType" required>
            <option value="admin">管理員</option>
            <option value="general">一般使用者</option>
          </select>
          <!-- 按鈕容器 -->
          <div class="button-container">
            <button @click="cancelEdit" type="button" class="cancel-btn">取消</button>
            <button type="submit" class="save-btn">儲存變更</button>
          </div>
        </form>
      </div>
    </div>

        <!-- 新增管理員彈出視窗 -->
        <div v-if="isAdding" class="edit-modal">
      <div class="modal-content">
        <h2>新增管理員</h2>
        <form @submit.prevent="sureAddAdmin">

          <label for="account">帳號</label>
          <input v-model="currentMember.account" type="text" id="account" required />

          <label for="password">密碼</label>
          <input v-model="currentMember.password" type="password" id="password" required />

          <label for="twoPassword">確認密碼</label>
          <input v-model="currentMember.twoPassword" type="password" id="twoPassword" required />

          <label for="name">姓名</label>
          <input v-model="currentMember.name" type="text" id="name" required />

          <label for="phone">電話</label>
          <input v-model="currentMember.phone" type="text" id="phone" required />

          <label for="birthday">生日</label>
          <input v-model="currentMember.birthday" type="date" id="birthday" required />

          <label for="address">地址</label>
          <input v-model="currentMember.address" type="text" id="address" required />

          <label for="memberType">使用權限</label>
          <select v-model="currentMember.memberType" id="memberType" required>
            <option value="admin">管理員</option>
          </select>
          <!-- 按鈕容器 -->
          <div class="button-container">
            <button @click="cancelAdd" type="button" class="cancel-btn">取消</button>
            <button type="submit" class="save-btn">確認新增</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import axiosapi from '../plugins/axios.js';
import Swal from "sweetalert2";

const loading = ref(true);
const members = ref([]);
const totalPages = ref(0);
const currentPage = ref(0); // 當前頁數
const searchKeyword = ref(""); // 使用局部變數來存儲搜尋關鍵字
const isEditing = ref(false);
const isAdding = ref(false)
const currentMember = ref(null);
const sortOrder = ref({ field: "", direction: 1 });

// 載入會員資料
async function loadMembers(page = 0) {
  try {
    const response = await axiosapi.get("/page/members", {
      params: { page: page, size: 12 },  // 分頁查詢
    });
    console.log(`一開始的查詢data: ${response.data}`); // 打印回傳的數據
    if (response.data.list) {
      members.value = response.data.list;
      totalPages.value = response.data.totalPages;  // 設置總頁數
      currentPage.value = response.data.currentPage;  // 設置當前頁數
    } else {
      console.error("無法獲取會員資料。");
    }
  } catch (error) {
    console.error("載入會員資料失敗", error);
  } finally {
    loading.value = false;
  }
}

// 改變頁數
const changePage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    loadMembers(newPage);  // 重新加載該頁的會員資料
  }
};

// 搜尋會員
const searchMembers = async () => {
  try {
    const response = await axiosapi.get("/admin/member/search", {
      params: { keyword: searchKeyword.value },
    });
    members.value = response.data;
  } catch (error) {
    console.error("Error searching members:", error);
  }
};

// 排序邏輯
const sortTable = async (field) => {
  console.log(`field${field}`);
  if (sortOrder.value.field === field) {
    sortOrder.value.direction = -sortOrder.value.direction; //降序 -1
  } else {
    sortOrder.value.field = field;
    sortOrder.value.direction = 1;  // 設置為升序 1
  }
    try {
      const response = await axiosapi.get("/page/members", {
        params: { sortBy: field, direction: sortOrder.value.direction },  // 分頁查詢
      });
      console.log(`排序data: ${response.data}`); // 打印回傳的數據
      if (response.data.list) {
        members.value = response.data.list;
        totalPages.value = response.data.totalPages;  // 設置總頁數
        currentPage.value = response.data.currentPage;  // 設置當前頁數
      } else {
        console.error("無法獲取會員資料。");
      }
    } catch (error) {
      console.error("載入會員資料失敗", error);
    } finally {
      loading.value = false;
    }
};

// 排序圖示
const getSortIcon = (field) => {
  if (sortOrder.value.field === field) {
    return sortOrder.value.direction === 1 ? "fas fa-sort-up" : "fas fa-sort-down";
  }
  return "fas fa-sort";  // 默認排序圖示
};

// 排序邏輯
// const sortTable = (field) => {
//   if (sortOrder.value.field === field) {
//     sortOrder.value.direction = -sortOrder.value.direction;
//   } else {
//     sortOrder.value.field = field;
//     sortOrder.value.direction = 1;  // 設置為升序
//   }

//   // 根據排序的欄位和方向進行排序
//   members.value.sort((a, b) => {
//     // 處理 null 或 undefined，將其視為較小的值（升序時排在前面）
//     const aValue = a[field] === null || a[field] === undefined ? -Infinity : a[field];
//     const bValue = b[field] === null || b[field] === undefined ? -Infinity : b[field];

//     // 如果是字串（例如 "未填寫"），我們可以將它們視為最小值或最大值
//     if (typeof aValue === 'string' && aValue === "未填寫") {
//       return sortOrder.value.direction === 1 ? -1 : 1;  // "未填寫" 排在前或後
//     }
//     if (typeof bValue === 'string' && bValue === "未填寫") {
//       return sortOrder.value.direction === 1 ? 1 : -1;
//     }

//     // 標準的數字和字串比較
//     if (aValue < bValue) return -1 * sortOrder.value.direction;
//     if (aValue > bValue) return 1 * sortOrder.value.direction;
//     return 0;
//   });
// };



// 編輯會員
const editMember = (member) => {
  isEditing.value = true;
  currentMember.value = { ...member };
};

// 取消編輯
const cancelEdit = () => {
  isEditing.value = false;
  currentMember.value = null;
};

// 新增管理員
const addAdmin = (member) => {
  isAdding.value = true;
  currentMember.value = { account: '', name: '', phone: '', birthday: '', address: '', memberType: 'admin', };
};

// 取消編輯
const cancelAdd = () => {
  isAdding.value = false;
  currentMember.value = null;
};

// 更新會員
const updateMember = async () => {
  try {
    const response = await axiosapi.post("/admin/member/update", currentMember.value);
    if (response.data.success) {
      Swal.fire({
      icon: 'success',
      title: '更新成功',
      text: '更新成功，請確認。',
      confirmButtonColor: '#4A6741',  // 設定按鈕顏色
    });
      loadMembers();
      cancelEdit();
    } else {
      Swal.fire({
      icon: 'error',
      title: '更新失敗',
      text: '更新失敗，請稍後再試。',
      confirmButtonColor: '#4A6741',  // 設定按鈕顏色
    });
    }
  } catch (error) {
    console.error("更新會員失敗", error);
  }
};


// 確認新增管理員
const sureAddAdmin = async () => {
// 檢查密碼是否一致
if (currentMember.value.password !== currentMember.value.twoPassword) {
    // 顯示 SweetAlert2 錯誤訊息
    Swal.fire({
      icon: 'error',
      title: '密碼不一致',
      text: '請確認密碼和確認密碼相同。',
      confirmButtonColor: '#4A6741',  // 設定按鈕顏色
    });
    return;
  }

  try {
    const response = await axiosapi.post("/admin/register", currentMember.value);
    if (response.data.success) {
      // 顯示 SweetAlert2 錯誤訊息
      Swal.fire({
      icon: 'success',
      title: '新增管理員成功',
      text: '新增管理員成功，請確認。',
      confirmButtonColor: '#4A6741',  // 設定按鈕顏色
    });
      loadMembers();
      cancelAdd();
    } else {
    // 顯示 SweetAlert2 錯誤訊息
    Swal.fire({
      icon: 'error',
      title: '帳號已存在',
      text: '請確認帳號是否已被註冊過。',
      confirmButtonColor: '#4A6741',  // 設定按鈕顏色
    });
      console.error("註冊失敗:", response.data.message);
    }
  } catch (error) {
    console.error("註冊失敗", error);
  }
};
// 初始載入會員資料
loadMembers();

</script>


<style scoped>
/* 主體樣式 */
.dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.dashboard-header h2 {
    font-size: 1.5rem;
    color: var(--text-primary);
}

.header-actions {
    display: flex;
    gap: 1rem;
    align-items: center;
}

.search-bar {
    display: flex;
    gap: 0.5rem;
}

.search-bar input {
    padding: 0.5rem 1rem;
    border: 1px solid var(--border-color);
    border-radius: 0.5rem;
    min-width: 200px;
    background-color: white;
}

.search-bar button {
    padding: 0.5rem 1rem;
    background: var(--primary-color);
    color: white;
    border: none;
    border-radius: 0.5rem;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.search-bar button:hover {
    background-color: var(--secondary-color);
}

/* 查詢條件區域 */
.filter-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.search-bar input {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  min-width: 150px;
  background-color: white;
}


.search-btn:hover {
  background-color: var(--secondary-color);
}

.search-btn i {
  font-size: 1.2rem;
}

/* 會員表格容器 */
.member-table-container {
  overflow-y: auto;  /* 讓會員表格有滾動條 */
  padding-bottom: 3rem; /* 保證不會被固定分頁區域覆蓋 */
}

/* 會員表格樣式 */
.member-table {
  width: 100%;
  border-collapse: collapse;
  background-color: var(--background-color);
  table-layout: fixed;
}

.member-table th, .member-table td {
  padding: 12px;
  border: 1px solid var(--border-color);
  text-align: center;
  word-wrap: break-word;
}

.member-table th {
  background-color: var(--secondary-color);
  color: var(--text-primary);
  cursor: pointer;
}

.member-table th:nth-child(2), .member-table td:nth-child(2) {
  width: 210px;  /* 帳號 */
}
.member-table th:nth-child(6), .member-table td:nth-child(6) {
  width: 210px;  /* 地址 */
}


.member-table tr:nth-child(even) {
  background-color: var(--hover-color);
}

.member-table tr:hover {
  background-color: var(--secondary-color);
}

.edit-btn {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 5px;
}

.edit-btn:hover {
  background-color: var(--hover-color);
}

/* 分頁固定樣式 */
.pagination {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: var(--background-color);
  padding: 0.5rem 0;
  display: flex;
  justify-content: center;
  gap: 1rem;
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
  z-index: 10;
  padding: 1rem 0;
}

.pagination button {
  padding: 0.5rem 1rem;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination span {
  align-self: center;
  font-size: 1.1rem;
}

/* 編輯會員彈窗 */
.edit-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
}

.modal-content h2 {
  margin-bottom: 10px;
  color: var(--primary-color);
}

.modal-content input,
.modal-content select {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid var(--border-color);
  border-radius: 5px;
  background-color: white;
  font-size: 1rem;
}

/* 下拉選單 */
.modal-content select {
  -webkit-appearance: none;  /* 去除 Safari 的預設樣式 */
  -moz-appearance: none;     /* 去除 Firefox 的預設樣式 */
  appearance: none;          /* 去除其他瀏覽器的預設樣式 */
  padding-right: 30px;       /* 讓選項顯示得更完整，避免文字被截斷 */
  background-image: url('data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-chevron-down" viewBox="0 0 16 16%3E%3Cpath d="M1 4l7 7 7-7z"/%3E%3C/svg%3E'); /* 自定義下拉箭頭 */
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px 12px;
}

.save-btn, .cancel-btn {
  padding: 10px 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #888;
}

.save-btn:hover {
  background-color: var(--hover-color);
}

.cancel-btn:hover {
  background-color: #777;
}

/* 最底部按鈕區域 */
.modal-content .button-container {
  display: flex;
  justify-content: space-between; /* 使按鈕分佈於左右 */
  margin-top: 20px;
}

.admin {
  padding: 0.5rem 1rem;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  margin-bottom: 20px;
  margin-right: 20px;
}
</style>
