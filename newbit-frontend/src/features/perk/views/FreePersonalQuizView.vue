<script setup>
import { ref, computed } from "vue";

// 퀴즈 데이터
const questions = [
  {
    id: 1,
    text: "오랜만에 주말이 생겼습니다. 당신은 어떻게 시간을 보내시겠습니까?",
    options: [
      {
        id: "a",
        text: "친구들과 만나 시끌벅적하게 놀러간다",
        score: { e: 2, i: 0 },
      },
      {
        id: "b",
        text: "집에서 혼자 좋아하는 취미활동에 몰두한다",
        score: { e: 0, i: 2 },
      },
      {
        id: "c",
        text: "가까운 친구 1~2명과 조용히 대화하며 시간을 보낸다",
        score: { e: 1, i: 1 },
      },
    ],
  },
  {
    id: 2,
    text: "중요한 결정을 내릴 때 당신은 주로 어떤 방식을 선호하나요?",
    options: [
      {
        id: "a",
        text: "논리적으로 분석하고 장단점을 따져본 후 결정한다",
        score: { t: 2, f: 0 },
      },
      {
        id: "b",
        text: "내 감정과 다른 사람들의 감정을 고려하여 결정한다",
        score: { t: 0, f: 2 },
      },
      {
        id: "c",
        text: "직관적으로 옳다고 느껴지는 선택을 한다",
        score: { t: 1, f: 1 },
      },
    ],
  },
  {
    id: 3,
    text: "새로운 업무나 프로젝트를 시작할 때 당신은 어떤 방식으로 접근하나요?",
    options: [
      {
        id: "a",
        text: "철저한 계획을 세우고 단계별로 진행한다",
        score: { j: 2, p: 0 },
      },
      {
        id: "b",
        text: "상황에 따라 유연하게 대처하며 즉흥적으로 해결한다",
        score: { j: 0, p: 2 },
      },
      {
        id: "c",
        text: "대략적인 계획을 세우되, 필요에 따라 조정한다",
        score: { j: 1, p: 1 },
      },
    ],
  },
  {
    id: 4,
    text: "새로운 아이디어에 흥미를 느끼나요, 아니면 실질적인 경험에 더 가치를 두나요?",
    options: [
      {
        id: "a",
        text: "미래의 가능성과 혁신적인 아이디어에 흥미를 느낀다",
        score: { n: 2, s: 0 },
      },
      {
        id: "b",
        text: "현실적이고 실질적인 경험과 사실에 가치를 둔다",
        score: { n: 0, s: 2 },
      },
      {
        id: "c",
        text: "둘 다 상황에 따라 중요하게 생각한다",
        score: { n: 1, s: 1 },
      },
    ],
  },
  {
    id: 5,
    text: "스트레스를 받을 때 어떻게 대처하시나요?",
    options: [
      {
        id: "a",
        text: "다른 사람들과 대화하고 감정을 나눈다",
        score: { e: 2, i: 0 },
      },
      {
        id: "b",
        text: "혼자만의 시간을 갖고 생각을 정리한다",
        score: { e: 0, i: 2 },
      },
      {
        id: "c",
        text: "운동이나 취미활동으로 기분전환을 한다",
        score: { e: 1, i: 1 },
      },
    ],
  },
];

// 성격 유형 결과
const personalityTypes = {
  ENFP: {
    title: "열정적인 활력소",
    description:
      "호기심이 많고 열정적인 성격으로, 새로운 가능성을 발견하는 것을 좋아합니다. 상상력이 풍부하고 창의적이며, 다른 사람들과의 연결을 중요시합니다.",
    strengths: ["창의적", "열정적", "사교적", "적응력이 높음"],
    weaknesses: ["집중력 부족", "계획성 부족", "스트레스에 약함"],
  },
  INFP: {
    title: "이상주의적 중재자",
    description:
      "조용하고 사려깊은 성격으로, 깊은 가치관과 이상을 추구합니다. 공감능력이 뛰어나고 창의적이며, 다른 사람들의 잠재력을 볼 수 있습니다.",
    strengths: ["창의적", "공감능력이 뛰어남", "이상주의적", "적응력 있음"],
    weaknesses: ["현실감각 부족", "자기비판적", "갈등 회피"],
  },
  ENTJ: {
    title: "대담한 지도자",
    description:
      "결단력 있고 논리적인 성격으로, 장기적인 목표를 설정하고 달성하기 위해 노력합니다. 리더십이 뛰어나고 효율성을 중요시합니다.",
    strengths: ["결단력", "효율성", "리더십", "논리적 사고"],
    weaknesses: ["비인간적일 수 있음", "참을성 부족", "완벽주의"],
  },
  INTJ: {
    title: "전략적 사색가",
    description:
      "독립적이고 분석적인 성격으로, 복잡한 문제를 해결하는 것을 좋아합니다. 자신의 아이디어와 지식에 자신감이 있으며, 장기적인 계획을 세웁니다.",
    strengths: ["독립적", "분석적", "전략적", "지식 습득 능력"],
    weaknesses: ["고집이 셈", "사회성 부족", "과도한 비판"],
  },
  ESTJ: {
    title: "실용적인 관리자",
    description:
      "체계적이고 실용적인 성격으로, 명확한 기준과 절차를 중요시합니다. 목표 지향적이고 책임감이 강하며, 효율적으로 업무를 처리합니다.",
    strengths: ["신뢰성", "리더십", "효율성", "실용성"],
    weaknesses: ["융통성 부족", "감정 표현 어려움", "변화에 저항"],
  },
  ISTJ: {
    title: "신중한 관리자",
    description:
      "체계적이고 책임감이 강한 성격으로, 정확성과 신뢰성을 중요시합니다. 체계적으로 정보를 관리하고 자신의 의무를 성실히 수행합니다.",
    strengths: ["체계적", "책임감", "집중력", "정확성"],
    weaknesses: ["융통성 부족", "변화에 저항", "감정 표현 어려움"],
  },
  ESFP: {
    title: "자유로운 영혼",
    description:
      "즉흥적이고 활발한 성격으로, 현재의 순간을 즐기는 것을 좋아합니다. 사교적이고 유머감각이 있으며, 다른 사람들을 즐겁게 해줍니다.",
    strengths: ["적응력", "열정적", "사교적", "실용적"],
    weaknesses: ["장기계획 어려움", "집중력 부족", "갈등 회피"],
  },
  ISFP: {
    title: "섬세한 예술가",
    description:
      "따뜻하고 감성적인 성격으로, 자신만의 독특한 스타일과 미적 감각을 가지고 있습니다. 현재의 순간을 소중히 여기며 자신의 가치를 중요시합니다.",
    strengths: ["창의적", "적응력", "충성심", "개인주의적"],
    weaknesses: ["갈등 회피", "장기계획 어려움", "과도한 독립성"],
  },
};

// 상태 관리
const currentQuestionIndex = ref(0);
const answers = ref([]);
const showResult = ref(false);
const scores = ref({ e: 0, i: 0, n: 0, s: 0, t: 0, f: 0, j: 0, p: 0 });

// 계산된 속성
const currentQuestion = computed(() => questions[currentQuestionIndex.value]);

const progress = computed(() =>
  Math.round((currentQuestionIndex.value / questions.length) * 100)
);

const personalityType = computed(() => {
  let type = "";
  type += scores.value.e > scores.value.i ? "E" : "I";
  type += scores.value.n > scores.value.s ? "N" : "S";
  type += scores.value.t > scores.value.f ? "T" : "F";
  type += scores.value.j > scores.value.p ? "J" : "P";

  // 기본 유형이 없으면 가장 가까운 유형 반환
  return personalityTypes[type] ? type : "ENFP";
});

const result = computed(() => personalityTypes[personalityType.value]);

// 메서드
function selectOption(option) {
  // 점수 업데이트
  for (const [trait, score] of Object.entries(option.score)) {
    scores.value[trait] += score;
  }

  // 답변 기록
  answers.value.push({
    questionId: currentQuestion.value.id,
    optionId: option.id,
  });

  // 다음 질문으로 이동 또는 결과 표시
  if (currentQuestionIndex.value < questions.length - 1) {
    currentQuestionIndex.value++;
  } else {
    showResult.value = true;
  }
}

function restartQuiz() {
  currentQuestionIndex.value = 0;
  answers.value = [];
  showResult.value = false;
  scores.value = { e: 0, i: 0, n: 0, s: 0, t: 0, f: 0, j: 0, p: 0 };
}
</script>

<template>
  <div
    class="min-h-screen bg-gradient-to-b from-indigo-50 to-blue-50 py-12 px-4"
  >
    <div class="max-w-3xl mx-auto">
      <!-- 질문 영역 -->
      <div v-if="!showResult" class="bg-white rounded-2xl shadow-lg p-8 mb-8">
        <!-- 진행 상태 -->
        <div class="mb-8">
          <div class="flex justify-between text-sm mb-1">
            <span>진행 상황</span>
            <span>{{ currentQuestionIndex + 1 }} / {{ questions.length }}</span>
          </div>
          <div class="w-full h-2 bg-gray-200 rounded-full">
            <div
              class="h-full bg-gradient-to-r from-indigo-500 to-blue-500 rounded-full transition-all duration-500"
              :style="`width: ${progress}%`"
            ></div>
          </div>
        </div>

        <!-- 질문 -->
        <div class="mb-8 text-center">
          <h2 class="text-2xl font-bold mb-2">
            질문 {{ currentQuestionIndex + 1 }}
          </h2>
          <p class="text-lg text-gray-700">{{ currentQuestion.text }}</p>
        </div>

        <!-- 답변 선택지 -->
        <div class="space-y-4">
          <button
            v-for="option in currentQuestion.options"
            :key="option.id"
            @click="selectOption(option)"
            class="w-full px-6 py-4 rounded-xl border-2 border-gray-200 hover:border-indigo-500 hover:bg-indigo-50 transition-all text-left flex items-center"
          >
            <span class="flex-grow">{{ option.text }}</span>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 text-gray-400"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                clip-rule="evenodd"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- 결과 영역 -->
      <div v-else class="bg-white rounded-2xl shadow-lg p-8 animate-fadeIn">
        <div class="text-center mb-12">
          <div class="inline-block p-4 rounded-full bg-indigo-100 mb-4">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-12 w-12 text-indigo-600"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3zM6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z"
              />
            </svg>
          </div>
          <h2 class="text-3xl font-bold mb-2">당신의 성격 유형은</h2>
          <div class="flex justify-center items-center gap-2 mb-4">
            <span
              class="text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-blue-600"
            >
              {{ personalityType }}
            </span>
            <span class="text-xl font-semibold text-gray-600">
              {{ result.title }}
            </span>
          </div>
          <p class="text-gray-600 mb-8 max-w-xl mx-auto">
            {{ result.description }}
          </p>
        </div>

        <!-- 특징 및 강점 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div class="bg-indigo-50 rounded-xl p-6">
            <h3 class="text-lg font-semibold mb-3 text-indigo-700">강점</h3>
            <ul class="space-y-2">
              <li
                v-for="(strength, index) in result.strengths"
                :key="index"
                class="flex items-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 text-green-500 mr-2"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fill-rule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                    clip-rule="evenodd"
                  />
                </svg>
                {{ strength }}
              </li>
            </ul>
          </div>

          <div class="bg-blue-50 rounded-xl p-6">
            <h3 class="text-lg font-semibold mb-3 text-blue-700">약점</h3>
            <ul class="space-y-2">
              <li
                v-for="(weakness, index) in result.weaknesses"
                :key="index"
                class="flex items-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 text-red-500 mr-2"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fill-rule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                    clip-rule="evenodd"
                  />
                </svg>
                {{ weakness }}
              </li>
            </ul>
          </div>
        </div>

        <!-- 다시 하기 버튼 -->
        <div class="text-center">
          <button
            @click="restartQuiz"
            class="px-8 py-3 bg-gradient-to-r from-indigo-500 to-blue-500 text-white font-medium rounded-xl hover:from-indigo-600 hover:to-blue-600 transition-colors"
          >
            다시 테스트하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.5s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
