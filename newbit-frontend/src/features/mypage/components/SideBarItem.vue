<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const props = defineProps({
  to: String,
  text: String,
})

const route = useRoute()
const isActive = computed(() => route.fullPath === props.to)
</script>

<template>
  <li>
    <router-link :to="to" :class="{ active: isActive }" class="item-link" exact-active-class="active">
      {{ text }}
    </router-link>
  </li>
</template>

<style scoped>
.item-link {
  display: block;
  padding: 12px 16px;
  border-radius: 5px;
  color: var(--newbittext);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
}

.item-link:hover {
  background-color: var(--newbitlight); /* hover 시 연한 파랑 */
}

.item-link.active {
  background-color: var(--newbitlight); /* 선택된 항목은 hover 색과 동일 */
  color: var(--newbitdark); /* 진한 파란 글자색 */
}
</style>