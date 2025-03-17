<template>
  <div v-if="isVisible" class="modal-container">
    <div class="modal-content">
      <div class="modal-header">
        <h5>{{ title }}</h5>
        <button class="close-btn" @click="closeModal">×</button>
      </div>
      <div class="modal-body">
        <p>{{ message }}</p>
      </div>
      <div class="modal-footer">
        <button class="confirm-btn" @click="closeModal">確定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineExpose, ref } from "vue";

// 接收 props
const props = defineProps({
  title: String,
  message: String,
});

// 控制彈窗顯示狀態
const isVisible = ref(false);

// 顯示彈窗
const show = () => {
  isVisible.value = true;
};

// 關閉彈窗
const closeModal = () => {
  isVisible.value = false;
};

// 暴露方法
defineExpose({ show });
</script>

<style scoped>
.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  margin-bottom: 20px;
}

.confirm-btn {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.confirm-btn:hover {
  background-color: #0056b3;
}
</style>
