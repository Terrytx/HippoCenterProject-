<template>
  <el-card class="tickets-card">
    <template #header>
      <div class="card-header">
        <h1>票券管理系統</h1>
      </div>
    </template>

    <el-table
      :data="filteredTickets"
      style="width: 100%"
      v-loading="loading"
      border
      fit
      empty-text="沒有數據"
    >
      <el-table-column prop="ticketId" label="票券 ID" min-width="90" />
      <el-table-column prop="ticketAmount" label="購買數量" min-width="90" />
      <el-table-column prop="memberName" label="持有人" min-width="100">
        <template #default="scope">
          <span
            class="member-name-link"
            @click="handleMemberClick(scope.row, scope.column, $event)"
          >
            {{ scope.row.memberName }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="eventName" label="活動名稱" min-width="180" />
      <el-table-column prop="eventDate" label="活動日期" min-width="180" />
      <el-table-column prop="venueName" label="場地" min-width="120" />
  
      <el-table-column prop="ticketStatus" label="狀態" min-width="90" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.ticketStatus === 'VALID' ? 'success' : 'info'">
            {{ scope.row.ticketStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="150" align="center">
        <template #default="scope">
          <el-button-group class="operation-buttons">
            <el-button size="small" type="primary" @click="handleUpdate(scope.row)">
              更新狀態
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">
              刪除票券
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 會員票券對話框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="`${selectedMemberName} 的所有票券`"
      width="80%"
    >
      <el-table
        :data="currentMemberTickets"
        border
        fit
        empty-text="沒有數據"
      >
        <el-table-column prop="ticketId" label="票券 ID" min-width="90" />
        <el-table-column prop="ticketAmount" label="購買數量" min-width="90" />
        <el-table-column prop="eventName" label="活動名稱" min-width="180" />
        <el-table-column prop="eventDate" label="活動日期" min-width="180" />
        <el-table-column prop="venueName" label="場地" min-width="120" />
        <el-table-column prop="ticketStatus" label="狀態" min-width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.ticketStatus === 'VALID' ? 'success' : 'info'">
              {{ scope.row.ticketStatus }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-card>
</template>

<script setup>
import Swal from 'sweetalert2';
import { ref, computed, onMounted } from "vue";
import 'element-plus/dist/index.css'
import axiosapi from "@/plugins/axios.js";
import {
  ElTable,
  ElTableColumn,
  ElCard,
  ElDialog,
} from "element-plus";

const rawTickets = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const currentMemberTickets = ref([]);
const selectedMemberName = ref("");

// 查詢票券請求
const fetchTickets = async () => {
  try {
    const response = await axiosapi.get('/api/tickets/all');
    rawTickets.value = response.data;
  } catch (error) {
    console.error("獲取票券數據失敗:", error);
    Swal.fire({
      icon: 'error',
      title: '獲取票券數據失敗'
    });
  } finally {
    loading.value = false;
    try {
      console.log("API 返回的原始數據:", response.data);
    } catch (err) {
      console.log("API 返回的原始數據: undefined");
    }
    console.log("fetchTickets finally, loading =", loading.value);
  }
};

// 根據會員ID獲取票券
const fetchMemberTickets = async (memberId) => {
  try {
    loading.value = true;
    console.log("調用 fetchMemberTickets，memberId:", memberId);

    const response = await axiosapi.get(`/api/tickets/member/${memberId}`);
    currentMemberTickets.value = response.data.map((ticket) => ({
      ticketId: ticket.ticketId,
      ticketAmount: ticket.ticketAmount,
      eventName: ticket.eventId?.eventName,
      eventDate: `${ticket.eventId?.eventStartDate} ~ ${ticket.eventId?.eventEndDate}`,
      venueName: ticket.eventId?.venue?.venueName,
      ticketStatus: ticket.ticketStatus,
    }));
    dialogVisible.value = true;
  } catch (error) {
    console.error("獲取會員票券失敗:", error);
    Swal.fire({
      icon: 'error',
      title: '獲取會員票券失敗'
    });
  } finally {
    loading.value = false;
    console.log("fetchTickets finally, loading =", loading.value);
  }
};

// 處理會員名稱點擊
const handleMemberClick = (row, column, event) => {
  // 直接從原始數據中獲取會員信息
  const originalTicket = rawTickets.value.find((t) => t.ticketId === row.ticketId);
  console.log("找到的原始票據:", originalTicket);

  if (!originalTicket?.memberId?.memberId) {
    console.warn("無法找到有效的會員ID");
    Swal.fire({
      icon: 'warning',
      title: '無法獲取會員ID'
    });
    return;
  }

  const memberId = originalTicket.memberId.memberId;
  console.log("獲取到的會員ID:", memberId);

  selectedMemberName.value = originalTicket.memberId.name;
  fetchMemberTickets(memberId);
};

// 更新票券
const handleUpdate = async (row) => {
  try {
    // 使用 SweetAlert2 讓使用者選擇新狀態
    const { value: newStatus } = await Swal.fire({
      title: "請選擇票券新狀態",
      input: 'select',
      inputOptions: {
        VALID: '有效',
        USED: '已使用',
        REFUNDED: '已退款'
      },
      inputPlaceholder: '選擇新狀態',
      showCancelButton: true,
      confirmButtonText: '更新',
      cancelButtonText: '取消',
      didOpen: () => {
        const swalContainer = Swal.getPopup().parentElement;
        if (swalContainer) {
          swalContainer.style.zIndex = '3000';
        }
      }
    });
    if (!newStatus) {
      return;
    }
    // 準備只更新允許修改的欄位
    const payload = {
      ticketAmount: row.ticketAmount, // 保持原數量
      ticketStatus: newStatus  // 依照選擇更新狀態
    };
    const response = await axiosapi.put(`/api/admin/tickets/${row.ticketId}`, payload);
    if (response.status === 200) {
      Swal.fire({
        icon: 'success',
        title: '更新成功',
        showConfirmButton: false,
        timer: 1500
      });
      await fetchTickets();
    }
  } catch (error) {
    console.error("更新票券失敗:", error);
    Swal.fire({
      icon: 'error',
      title: error.response && error.response.data === "票券無效" ? "票券無效" : "更新票券失敗"
    });
  }
};

const handleDelete = async (row) => {
  try {
    const swalResult = await Swal.fire({
      title: "確定要刪除此票券嗎？此操作不可恢復",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "確定",
      cancelButtonText: "取消",
      didOpen: () => {
        const swalContainer = Swal.getPopup().parentElement;
        if (swalContainer) {
          swalContainer.style.zIndex = '3000';
        }
      }
    });
    if (!swalResult.isConfirmed) {
      return;
    }

    const response = await axiosapi.delete(`/api/admin/tickets/${row.ticketId}`);
    if (response.status === 204) {
      Swal.fire({
        icon: 'success',
        title: '刪除成功',
        showConfirmButton: false,
        timer: 1500
      });
      await fetchTickets();
    }
  } catch (error) {
    if (error && error.message && error.message.toLowerCase().includes("cancel")) {
      return;
    }
    console.error("刪除票券失敗:", error);
    Swal.fire({
      icon: 'error',
      title: '刪除票券失敗'
    });
  }
};

// 只取需要的欄位
const filteredTickets = computed(() => {
  return rawTickets.value.map((ticket) => {
    console.log("處理票券:", ticket.ticketId, "會員信息:", ticket.memberId);
    return {
      ticketId: ticket.ticketId,
      ticketAmount: ticket.ticketAmount,
      memberName: ticket.memberId?.name || "未知",
      eventName: ticket.eventId?.eventName,
      eventDate: `${ticket.eventId?.eventStartDate} ~ ${ticket.eventId?.eventEndDate}`,
      venueName: ticket.eventId?.venue?.venueName,
      qrCode: ticket.qrCode,
      ticketStatus: ticket.ticketStatus,
    };
  });
});

// 組件載入時取得數據
onMounted(fetchTickets);
</script>

<style scoped>
.tickets-card {
  margin: 0px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.operation-buttons {
  display: inline-flex;
  gap: 8px;
}

.member-name-link {
  color: var(--el-color-primary);
  cursor: pointer;
}

.member-name-link:hover {
  text-decoration: underline;
}

:deep(.el-table) {
  --el-table-border-color: var(--el-border-color-lighter);
  --el-table-border: 1px solid var(--el-table-border-color);
  --el-table-text-color: var(--el-text-color-regular);
  --el-table-header-text-color: var(--el-text-color-secondary);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
  --el-table-current-row-bg-color: var(--el-color-primary-light-9);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-fixed-box-shadow: var(--el-box-shadow-light);
  --el-table-bg-color: var(--el-bg-color);
  --el-table-tr-bg-color: var(--el-bg-color);
  --el-table-expanded-cell-bg-color: var(--el-fill-color-blank);
  margin-bottom: 20px;
}

/* 利用 ::v-deep 來影響 Element Plus 的表格 */
::v-deep .el-table th,
::v-deep .el-table td {
  text-align: center;
}

/* 其他樣式 */
.tickets-card {
  margin: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.member-name-link {
  color: var(--el-color-primary);
  cursor: pointer;
}
.member-name-link:hover {
  text-decoration: underline;
}
.operation-buttons {
  display: inline-flex;
  gap: 8px;
}
.operation-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px; /* 調整按鈕間距 */
}
</style>
