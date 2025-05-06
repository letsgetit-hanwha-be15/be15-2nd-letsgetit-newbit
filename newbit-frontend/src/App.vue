<script setup>
import Footer from "@/components/common/Footer.vue";
import {useNotificationStore} from "@/features/stores/notification.js";
import {useAuthStore} from "@/features/stores/auth.js";
import {onMounted} from "vue";

const auth = useAuthStore();
const notification = useNotificationStore();

onMounted(() => {
  if (auth.userId) {
    notification.initSSE(auth.userId);
  }
});
</script>

<template>
  <RouterView />
  <Footer/>
</template>

<style scoped>

</style>
