<template>
  <div class="ac-panel">
    <div class="filter-content">
      <!-- 漢堡選單 -->
      <div class="l-nav__menu">
        <button class="hamburger" type="button" 
                aria-label="開合選單" aria-haspopup="true" 
                :aria-expanded="isMenuOpen"
                @click="toggleMenu">
          ☰
        </button>
      </div>

      <!-- 搜尋功能 -->
      <div class="l-nav__function-wrap">
        <div class="l-nav__search-input-wrap">
          <form @submit.prevent="onSearchSubmit">
            <button class="search-close" type="button" aria-label="收合搜尋框" @click="clearSearch">
              <svg role="img" aria-label="收合搜尋框" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path fill-rule="evenodd" clip-rule="evenodd"
                  d="M8 14C11.3137 14 14 11.3137 14 8C14 4.68629 11.3137 2 8 2C4.68629 2 2 4.68629 2 8C2 11.3137 4.68629 14 8 14ZM4.96967 4.96967C5.26256 4.67678 5.73744 4.67678 6.03033 4.96967L8 6.93934L9.96967 4.96967C10.2626 4.67678 10.7374 4.67678 11.0303 4.96967C11.3232 5.26256 11.3232 5.73744 11.0303 6.03033L9.06066 8L11.0303 9.96967C11.3232 10.2626 11.3232 10.7374 11.0303 11.0303C10.7374 11.3232 10.2626 11.3232 9.96967 11.0303L8 9.06066L6.03033 11.0303C5.73744 11.3232 5.26256 11.3232 4.96967 11.0303C4.67678 10.7374 4.67678 10.2626 4.96967 9.96967L6.93934 8L4.96967 6.03033C4.67678 5.73744 4.67678 5.26256 4.96967 4.96967Z"
                  fill="black" />
              </svg>
            </button>
            <input v-model="searchQuery" type="text" class="search-input" placeholder="輸入搜尋關鍵字" aria-label="輸入搜尋關鍵字">
          </form>
        </div>
      </div>

      <!-- 篩選表單 -->
      <form @submit.prevent="onSubmit" v-if="isMenuOpen">
        <div class="k-select">
          <div class="select-wrap">
            <select v-model="filters.class" title="篩選類別">
              <option value="">全部類別</option>
              <option v-for="option in categories" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>

          <div class="select-wrap">
            <select v-model="filters.location" title="篩選地點">
              <option value="">活動地點</option>
              <option v-for="option in locations" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>

          <div class="select-wrap">
            <select v-model="filters.year" title="篩選年份">
              <option value="">年份</option>
              <option v-for="year in years" :key="year" :value="year">
                {{ year }}
              </option>
            </select>
          </div>

          <div class="select-wrap">
            <select v-model="filters.month" title="篩選月份">
              <option value="">月份</option>
              <option v-for="month in months" :key="month" :value="month">
                {{ month }}
              </option>
            </select>
          </div>
        </div>

        <!-- 按鈕 -->
        <div class="button-group">
          <button type="button" class="c-btn-line" @click="resetFilters">清除選項</button>
          <button type="submit" class="c-btn-line">篩選</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  setup() {
    const isMenuOpen = ref(false);
    const searchQuery = ref("");

    const categories = ref([
      { value: "16", label: "展覽" },
      { value: "17", label: "演唱會" },
      { value: "18", label: "活動及講座" }
    ]);

    const locations = ref([
      { value: "29", label: "表演廳" },
      { value: "30", label: "文化館" }
    ]);

    const years = ref([2021, 2024, 2025, 2026]);
    const months = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]);

    const filters = ref({
      class: "",
      location: "",
      year: "",
      month: "",
    });

    const toggleMenu = () => {
      isMenuOpen.value = !isMenuOpen.value;
    };

    const resetFilters = () => {
      filters.value = {
        class: "",
        location: "",
        year: "",
        month: "",
      };
    };

    const clearSearch = () => {
      searchQuery.value = "";
    };

    const onSearchSubmit = () => {
      console.log("搜尋關鍵字:", searchQuery.value);
    };

    const onSubmit = () => {
      console.log("提交篩選條件：", filters.value);
    };

    return {
      isMenuOpen,
      searchQuery,
      categories,
      locations,
      years,
      months,
      filters,
      toggleMenu,
      resetFilters,
      clearSearch,
      onSearchSubmit,
      onSubmit
    };
  },
};
</script>

<style>
.l-nav__menu {
  text-align: left;
}

.hamburger {
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
}

.l-nav__search-input-wrap {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.search-input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.button-group {
  display: flex;
  gap: 1rem;
}
</style>
