<script setup>
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {useToast} from "vue-toastification";

const router = useRouter();
const route = useRoute();
const toast = useToast()
const coffeechatId = route.params.id;

const { coffeechat, requestTimes, isMentor } = defineProps({
  coffeechat: {
    type: Object,
    required: true
  },
  requestTimes: {
    type: Array,
    required: true
  },
  isMentor: {
    type: Boolean,
    default: false
  }
})

function formatTime(datetimeString) {
  return datetimeString.replace('T', ' ')
}

const selectedRequestTimeId = ref(null)

const approveRequest = () => {
  if (selectedRequestTimeId.value === null) {
    alert('시간을 선택해주세요.')
    return
  }

  // 실제 승인 로직 실행 (emit 또는 API 요청 등)
  console.log('승인된 requestTimeId:', selectedRequestTimeId.value)
}

function cancelRequest() {
  // todo : api 연결(멘토가 커피챗 취소)
  toast.info('취소되었습니다.')
  router.push(`/mypage/mentor/coffeechats/${coffeechatId}`);
}
</script>

<template>
  <div class="space-y-8">
    <div>
      <div class="text-heading3" >커피챗 진행 시간</div>
      <div class="ml-2 mt-1">{{ coffeechat.purchaseQuantity * 30 }} 분</div>
    </div>
    <div>
      <div class="text-heading3">커피챗 요청 시간</div>
      <ul class="ml-2 mt-1">
        <li
            v-for="requestTime in requestTimes"
            :key="requestTime.requestTimeId"
            class="p-2">
          <template v-if="isMentor">
            <input
                type="radio"
                name="requestTime"
                :value="requestTime.requestTimeId"
                v-model="selectedRequestTimeId"
                class="mr-2"
            />
          </template>
          {{ formatTime(requestTime.startTime) }} ~ {{formatTime(requestTime.endTime) }}
        </li>

      </ul>
    </div>
    <div>
      <div class="text-heading3">요청 메시지</div>
      <div class="ml-2 mt-1 min-h-20">{{ coffeechat.requestMessage }}</div>
    </div>
    <div class="flex flex-wrap gap-2 justify-end pb-10">
      <button v-if="isMentor"
              @click="approveRequest"
              class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button">
        승인
      </button>
      <button v-if="isMentor"
              @click="cancelRequest"
              class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitred)] text-[var(--newbitlight)]  text-button">
        취소
      </button>
    </div>

  </div>
</template>

<style scoped>

</style>