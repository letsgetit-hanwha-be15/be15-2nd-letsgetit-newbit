<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goLogin() {
  router.push('/login')
}
</script>


<template>
  <div
      class="w-full h-full rounded-lg p-6"
      style="background-color: var(--newbitlight-active)"
  >
    <h2 class="text-heading1 text-newbittext mb-6 leading-tight">
      로그인을 하여 다양한<br />
      서비스를 누려보세요!
    </h2>

    <div
        class="w-full max-w-[560px] h-[240px] rounded-lg flex flex-col items-center justify-center space-y-4"
        style="background-color: var(--newbitlightmode)"
    >
      <p class="text-heading2 text-newbittext text-center font-bold">
        다양한 템플릿과 멘토링<br />
        서비스를 이용해보세요!
      </p>

      <button
          @click="goLogin"
          class="nav-button"
      >
        LOGIN
      </button>
    </div>
  </div>
</template>

<style scoped>
.nav-button {
  background-color: var(--newbitnormal);
  color: white;
  border: none;
  padding: 8px 14px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.nav-button:hover {
  background-color: var(--newbitnormal-hover);
}
</style>