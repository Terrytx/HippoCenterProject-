<template>
  <h3>租借流程</h3> 
  <div class="container">
      <div class="circle stage1">提出申請</div>
      <div class="arrow"></div>
      <div class="circle stage2">確認檔期</div>
      <div class="arrow"></div>
      <div class="circle stage3">簽約</div>
      <div class="arrow"></div>
      <div class="circle stage4">繳款</div>
      <div class="arrow"></div>
      <div class="circle stage5">使用場地</div>
  </div>    
</template>
  
<script setup>
</script>
  
<style scoped>
.container {
  height: 30vh;
  min-height: 20vh;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  background: white;
  min-width: 900px;
  width: 90%;
  position: relative;
  font-family: NotoSansTC-VariableFont;
  box-sizing: border-box;
  flex-wrap: nowrap;
  gap: 15px;
}

h3 {
  font-size: 35px;
}

.circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 20px;
  line-height: 1.5;
  color: #333;
  font-weight: 550;
  position: relative;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.circle:hover {
  transform: translateY(-2px);
}

.arrow {
  flex-grow: 1;
  height: 2px;
  background: #666;
  margin: 0 10px;
  position: relative;
}

.arrow::after {
  content: '';
  position: absolute;
  right: 0;
  top: -4px;
  width: 0;
  height: 0;
  border-left: 8px solid #666;
  border-top: 5px solid transparent;
  border-bottom: 5px solid transparent;
}

/* 更新為簡約的灰階色調 */
.stage1 { background-color: #FAFAFA; }
.stage2 { background-color: #F5F5F5; }
.stage3 { background-color: #EEEEEE; }
.stage4 { background-color: #E0E0E0; }
.stage5 { background-color: #BDBDBD; }
</style>