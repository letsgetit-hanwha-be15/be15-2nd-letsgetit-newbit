<template>
  <div
      ref="dropdownRef"
      class="dropdown-wrapper absolute right-[-120px] mt-2 w-96 bg-white border rounded shadow-md z-50">
    <div class="px-4 py-3 border-b font-semibold text-gray-800 flex justify-between items-center">
      <span>알림</span>
      <button class="text-xs text-gray-500 hover:underline">모두 읽음</button>
    </div>
    <div class="max-h-96 overflow-y-auto">
      <NotificationItem
          v-for="n in notifications.data"
          :key="n.notificationId"
          :notification="n"
      />
    </div>
  </div>
</template>

<script setup>
import NotificationItem from './NotificationItem.vue'
import {inject, onBeforeUnmount, onMounted, ref, watch} from 'vue'
import {useRouter} from "vue-router";


const props = defineProps({
  open: Boolean,
  dropdownId: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(["close"]);
const activeDropdown = inject("activeDropdown", ref(null));
const router = useRouter();


watch(activeDropdown, (newValue) => {
  if (newValue !== props.dropdownId && props.open) {
    close();
  }
});


const close = () => {
  emit("close");
  if (activeDropdown.value === props.dropdownId) {
    activeDropdown.value = null;
  }
};

const dropdownRef = ref(null)

function handleClickOutside(event) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    close()
  }
}

onMounted(() => {
  setTimeout(() => {
    window.addEventListener("click", handleClickOutside)
  }, 0)
})

onBeforeUnmount(() => {
  window.removeEventListener("click", handleClickOutside)
})

const notifications = ref({
  success: true,
  data: [
    {
      notificationId: 2,
      content: '[강한 사람이 되는 법] 칼럼이 좋아요를 5개 받았습니다.',
      typeName: '좋아요',
      serviceId: 1,
      isRead: false,
      createdAt: '2025-04-18T20:40:00',
    },
    {
      notificationId: 1,
      content: '[버텨야 할 때와 그만두어야 할 때를 구분하기] 게시글에 댓글이 달렸습니다.',
      typeName: '댓글',
      serviceId: 1,
      isRead: false,
      createdAt: '2025-04-18T20:40:00',
    }
  ],
  errorCode: null,
  message: null,
  timestamp: '2025-05-05T14:57:50.035556',
})
</script>