<!-- components/BreadCrumb.vue -->
<template>
  <div class="breadcrumb">
    <template v-for="(item, index) in breadcrumbItems" :key="index">
      <router-link v-if="item.path" :to="item.path" class="breadcrumb-item">
        {{ item.text }}
      </router-link>
      <span
        v-else
        class="breadcrumb-item"
        :class="{ highlight: index === breadcrumbItems.length - 1 }"
      >
        {{ item.text }}
      </span>
      <span v-if="index < breadcrumbItems.length - 1" class="breadcrumb-separator"
        >›</span
      >
    </template>
  </div>
</template>

<script setup>
defineProps({
  breadcrumbItems: {
    type: Array,
    required: true,
    default: () => [],
  },
});
</script>

<style scoped>
.breadcrumb {
  padding: 1rem 0;
  font-size: 1rem;
  color: #666;
  margin-bottom: 2rem;
}

.breadcrumb-item {
  display: inline-block;
  text-decoration: none;
  color: #666;
}

.breadcrumb-item:hover {
  color: #ff6b00;
  cursor: pointer;
}

.breadcrumb-separator {
  margin: 0 0.5rem;
  color: #666;
}

.highlight {
  color: #ff6b00;
}
</style>
