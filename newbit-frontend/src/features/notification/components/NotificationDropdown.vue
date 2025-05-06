<script setup>
import { inject, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useNotificationStore } from '@/features/stores/notification'
import NotificationItem from './NotificationItem.vue'

const props = defineProps({
  open: Boolean,
  dropdownId: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['close']);
const activeDropdown = inject('activeDropdown', ref(null));
const dropdownRef = ref(null);

const store = useNotificationStore();

watch(activeDropdown, (newValue) => {
  if (newValue !== props.dropdownId && props.open) {
    close();
  }
});

const close = () => {
  emit('close');
  if (activeDropdown.value === props.dropdownId) {
    activeDropdown.value = null;
  }
};

// 외부 클릭 감지
function handleClickOutside(event) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    close();
  }
}

onMounted(() => {
  setTimeout(() => {
    window.addEventListener('click', handleClickOutside);
  }, 0);
});

onBeforeUnmount(() => {
  window.removeEventListener('click', handleClickOutside);
});

// 모두 읽음 처리
const handleMarkAllAsRead = async () => {
  try {
    await store.markAllRead();

    store.notifications = store.notifications.map(n => ({
      ...n,
      isRead: true
    }));

    store.hasNew = false;
  } catch (e) {
    console.error('알림 읽음 처리 실패', e);
  };
};
</script>

<template>
  <div
      ref="dropdownRef"
      class="dropdown-wrapper absolute right-[-120px] mt-2 w-96 bg-white border rounded shadow-md z-50"
  >
    <div class="px-4 py-3 border-b font-semibold text-gray-800 flex justify-between items-center">
      <span>알림</span>
      <button
          class="text-xs text-gray-500 hover:underline"
          @click="handleMarkAllAsRead"
      >
        모두 읽음
      </button>
    </div>

    <div class="max-h-96 overflow-y-auto">
      <NotificationItem
          v-for="n in store.notifications.filter(n => !n.isRead)"
          :key="n.notificationId"
          :notification="n"
      />
      <div v-if="store.notifications.length === 0" class="text-center text-sm text-gray-400 p-4">
        새로운 알림이 없습니다.
      </div>
    </div>
  </div>
</template>