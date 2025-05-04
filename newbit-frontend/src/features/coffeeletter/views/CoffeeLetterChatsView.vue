<template>
  <div class="coffeeletter-container">
    <!-- 왼쪽: 채팅방 목록 -->
    <ChatRoomList
      :selected-room-id="selectedRoomId"
      @select-room="selectRoom"
    />

    <!-- 오른쪽: 채팅 내용 -->
    <Chat :room-id="selectedRoomId" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import ChatRoomList from "@/features/coffeeletter/components/ChatRoomList.vue";
import Chat from "@/features/coffeeletter/components/Chat.vue";

const route = useRoute();
const router = useRouter();

const selectedRoomId = ref(null);

onMounted(() => {
  console.log("현재 라우트 쿼리:", route.query);
  if (route.query.roomId) {
    console.log(
      "아 대체 왜 안 돼? URL에서 가져온 roomId: ",
      route.query.roomId
    );
    selectedRoomId.value = route.query.roomId;
  }
});

watch(
  () => route.query.roomId,
  (newRoomId) => {
    console.log("쿼리 파라미터 변경:", newRoomId);
    if (newRoomId) {
      selectedRoomId.value = newRoomId;
    }
  }
);

watch(selectedRoomId, (newId) => {
  console.log("선택된 채팅방 ID 변경:", newId);
});

const selectRoom = (roomId) => {
  console.log("채팅방 선택:", roomId);
  selectedRoomId.value = roomId;

  router.replace({
    query: { roomId: roomId },
  });
};
</script>

<style scoped>
.coffeeletter-container {
  display: flex;
  height: calc(100vh - 110px);
  width: 100%;
  background-color: #f5f7fa;
  margin: 0;
  overflow: hidden;
  position: relative;
  z-index: 1;
}
</style>
