<template>
  <div class="min-h-screen bg-gray-100 p-4">
    <div class="max-w-4xl mx-auto">
      <transition name="fade" mode="out-in">
        <!-- Gender Selection Step -->
        <div v-if="step === 'gender'" class="text-center" key="gender">
          <h1 class="text-3xl font-bold mb-8">성별을 선택해주세요</h1>
          <div class="grid grid-cols-3 gap-4 max-w-2xl mx-auto">
            <button
              v-for="gender in ['male', 'female', 'non-binary']"
              :key="gender"
              @click="handleGenderSelect(gender)"
              class="flex flex-col items-center p-6 rounded-xl bg-white shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105 active:scale-95"
              :class="{
                'bg-blue-50': gender === 'male',
                'bg-pink-50': gender === 'female',
                'bg-purple-50': gender === 'non-binary',
              }"
            >
              <div
                class="w-20 h-20 rounded-full flex items-center justify-center mb-4"
                :class="{
                  'bg-blue-500': gender === 'male',
                  'bg-pink-500': gender === 'female',
                  'bg-purple-500': gender === 'non-binary',
                }"
              >
                <component
                  :is="
                    gender === 'male'
                      ? UserIcon
                      : gender === 'female'
                      ? UserGroupIcon
                      : SparklesIcon
                  "
                  class="w-10 h-10 text-white"
                />
              </div>
              <span class="font-medium text-lg">
                {{
                  gender === "male"
                    ? "남성"
                    : gender === "female"
                    ? "여성"
                    : "Non-Binary"
                }}
              </span>
            </button>
          </div>
        </div>

        <!-- Color Selection Step -->
        <div v-else-if="step === 'colors'" class="text-center" key="colors">
          <h1 class="text-3xl font-bold mb-8">
            좋아하는 색상을 순서대로 선택해주세요
          </h1>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <ColorCard
              v-for="color in availableColors"
              :key="color.id"
              :color="color.hex"
              :color-name="color.name"
              :is-selected="selectedColors.includes(color.id)"
              @select="() => handleColorSelect(color.id)"
            />
          </div>

          <!-- Selected Colors Display -->
          <div v-if="selectedColors.length > 0" class="mt-8">
            <p class="text-sm mb-2">선택한 색상 (클릭시 취소):</p>
            <div class="flex flex-wrap gap-2 justify-center">
              <div
                v-for="(colorId, index) in selectedColors"
                :key="colorId"
                class="relative group"
              >
                <div
                  class="w-8 h-8 rounded-md relative"
                  :style="{
                    backgroundColor: colors.find((c) => c.id === colorId)?.hex,
                  }"
                >
                  <span
                    class="absolute -top-1 -right-1 bg-white text-black text-xs w-4 h-4 flex items-center justify-center rounded-full"
                  >
                    {{ index + 1 }}
                  </span>
                  <button
                    @click="removeColor(colorId)"
                    class="absolute -top-2 -right-2 w-4 h-4 bg-red-500 text-white rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200"
                  >
                    <XMarkIcon class="w-3 h-3" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Result Step -->
        <div v-else class="text-center" key="result">
          <h1 class="text-3xl font-bold mb-8">당신의 성격 분석 결과</h1>
          <div
            class="bg-white p-8 rounded-xl shadow-lg transform transition-all duration-500 hover:scale-105"
          >
            <div class="flex justify-center mb-6">
              <div
                class="w-16 h-16 rounded-full bg-gradient-to-r from-purple-500 to-blue-500 flex items-center justify-center"
              >
                <SparklesIcon class="w-8 h-8 text-white" />
              </div>
            </div>
            <p class="text-lg mb-6">{{ resultTypes[resultIndex].summary }}</p>
            <div
              v-for="(section, index) in resultTypes[resultIndex].sections"
              :key="index"
              class="mb-6 p-4 rounded-lg bg-gray-50"
            >
              <div class="flex items-center gap-2 mb-2">
                <component
                  :is="
                    index === 0
                      ? ChartBarIcon
                      : index === 1
                      ? ExclamationTriangleIcon
                      : LightBulbIcon
                  "
                  class="w-5 h-5 text-purple-500"
                />
                <h2 class="text-xl font-semibold">{{ section.title }}</h2>
              </div>
              <p class="text-gray-700">{{ section.content }}</p>
            </div>
          </div>
          <button
            @click="resetQuiz"
            class="mt-8 px-6 py-3 bg-gradient-to-r from-purple-500 to-blue-500 text-white rounded-xl hover:from-purple-600 hover:to-blue-600 transition-all duration-300 hover:scale-105 active:scale-95 flex items-center gap-2 mx-auto"
          >
            <ArrowPathIcon class="w-5 h-5" />
            다시 시작하기
          </button>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import ColorCard from "./ColorCard.vue";
import {
  UserIcon,
  UserGroupIcon,
  SparklesIcon,
  ChartBarIcon,
  ExclamationTriangleIcon,
  LightBulbIcon,
  ArrowPathIcon,
  XMarkIcon,
} from "@heroicons/vue/24/outline";

const colors = [
  { id: 1, hex: "#FF5252", name: "빨강" },
  { id: 2, hex: "#FF9800", name: "주황" },
  { id: 3, hex: "#FFEB3B", name: "노랑" },
  { id: 4, hex: "#4CAF50", name: "초록" },
  { id: 5, hex: "#2196F3", name: "파랑" },
  { id: 6, hex: "#9C27B0", name: "보라" },
  { id: 7, hex: "#795548", name: "갈색" },
  { id: 8, hex: "#212121", name: "검정" },
];

const resultTypes = [
  {
    summary:
      "당신은 실용적인 특성을 가진 타고난 리더입니다. 이론보다는 사실을 다루는 것을 선호하고, 사람들과 프로젝트를 조직하여 일을 완수하는 것을 즐깁니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "현재 약간 게으른 편이며 노력을 많이 하지 않으려고 합니다. 안정적인 뿌리를 내리고 평화롭고 사랑스러운 파트너가 필요합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "삶의 좋은 것들과 감각적인 것들을 즐기지만 비판적일 수 있습니다. 신중하고 조심스러우며 자신이 조종당하거나 속지 않는다고 믿어야 합니다.",
      },
      {
        title: "억제된 특성",
        content:
          "자신감이 낮지만 그것이 갈등을 회피하는 이유라는 것을 인정하지 못합니다. 스트레스가 많고 긴장된 상황으로 인해 정서적으로 지쳐 있습니다.",
      },
    ],
  },
  {
    summary:
      "당신은 감성적 연결을 중요시하는 창의적이고 상상력이 풍부한 성격을 가지고 있습니다. 가능성을 탐색하는 것을 즐기며 세부사항보다는 큰 그림에 초점을 맞추는 경향이 있습니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "주변 환경에 매우 민감하고 세심한 관찰자입니다. 다른 사람들의 감정과 필요에 맞춰 자신을 조정하려고 노력합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "과도한 책임감과 완벽주의 성향이 스트레스의 주요 원인입니다. 모든 상황을 통제하려고 하며, 예측할 수 없는 변화에 불안감을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "내면의 감정을 표현하는 데 어려움을 겪고 있으며, 자신의 진정한 욕구를 억제하는 경향이 있습니다.",
      },
    ],
  },
  {
    summary:
      "당신은 객관적인 추론을 선호하는 분석적이고 논리적인 사람입니다. 복잡한 문제를 해결하는 것을 즐기며 설득력 있는 증거가 제시될 때까지 회의적인 경향이 있습니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "새로운 경험과 자극을 찾고 있으며, 일상적인 패턴에 지루함을 느끼고 있습니다. 더 많은 모험과 다양성을 원합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "자유와 독립성이 제한되는 상황이 주요 스트레스 원인입니다. 의무와 책임으로 인해 자신의 열정을 추구할 시간이 부족하다고 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "충동적인 본성을 억제하려고 노력하고 있으며, 때로는 이로 인해 내적 긴장이 발생합니다.",
      },
    ],
  },
  {
    summary:
      "당신은 사람과 사회적 상호작용에 중점을 둔 외향적이고 활기찬 성격입니다. 적응력이 뛰어나고 열정적이며 다른 사람들을 동기부여하는 데 능숙합니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "정서적으로 안정된 환경을 원하며 갈등과 불확실성을 피하려고 합니다. 조화로운 관계를 중요하게 생각합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "변화와 불안정성이 주요 스트레스 원인입니다. 미래에 대한 불확실성과 예측할 수 없는 상황에 불안을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "자신의 불안을 숨기고 강한 모습을 보여주려고 노력합니다. 타인에게 의존하고 싶은 욕구를 억제하며 독립적으로 보이려고 합니다.",
      },
    ],
  },
  {
    summary:
      "당신은 감정을 강렬하게 경험하는 민감하고 깊은 사고를 하는 성격입니다. 풍부한 내면세계와 강한 직관력을 가지고 있으며, 창의적이고 공감 능력이 뛰어납니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "높은 기준과 이상을 가지고 있으며, 자신과 타인에게 많은 것을 기대합니다. 완벽주의 성향이 있습니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "자신이 설정한 높은 기준에 도달하지 못할 때 스트레스를 받습니다. 자기비판적이며 실수에 대해 매우 엄격합니다.",
      },
      {
        title: "억제된 특성",
        content:
          "실패에 대한 두려움으로 인해 새로운 도전을 시도하는 것을 망설입니다. 감정적 표현을 억제하고 이성적으로 보이려고 노력합니다.",
      },
    ],
  },
  {
    summary:
      "당신은 실용적인 접근 방식을 가진 체계적이고 세부사항을 중시하는 성격입니다. 전통, 충성심, 그리고 일을 '올바른 방식'으로 하는 것을 중요시합니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "깊은 연결과 의미 있는 관계를 추구합니다. 표면적인 상호작용보다 진정성 있는 교류를 선호합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "진정한 이해와 공감을 받지 못하는 상황이 스트레스의 주요 원인입니다. 깊은 연결 없이는 고립감을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "자신의 감정적 깊이를 타인에게 보여주는 것을 두려워합니다. 거절에 대한 두려움으로 인해 자신을 완전히 개방하지 않습니다.",
      },
    ],
  },
  {
    summary:
      "당신은 새로운 아이디어와 경험을 좋아하는 타고난 낙관주의자입니다. 독립적이고 적응력이 뛰어나며, 최종 결정을 내리기보다는 선택의 여지를 두는 것을 선호합니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "독립적이고 자율적인 생활 방식을 추구합니다. 자신의 결정을 스스로 내리고 자유롭게 행동하기를 원합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "자신의 자유와 독립성이 제한될 때 스트레스를 받습니다. 타인의 기대나 사회적 규범에 맞추는 것이 부담스럽습니다.",
      },
      {
        title: "억제된 특성",
        content:
          "타인과의 정서적 유대감에 대한 욕구를 억제합니다. 취약성을 보이는 것을 두려워하여 감정적 방어벽을 세웁니다.",
      },
    ],
  },
  {
    summary:
      "당신은 명확한 원칙과 강한 직업 윤리를 가진 조직적이고 체계적인 성격입니다. 책임감을 중요시하고 안전과 전통을 가치있게 여깁니다.",
    sections: [
      {
        title: "현재 상황",
        content:
          "안정성과 일관성을 중요시합니다. 명확한 구조와 질서 있는 환경에서 가장 편안함을 느낍니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "예측할 수 없는 변화와 혼란이 주요 스트레스 원인입니다. 통제력을 잃는 것에 불안을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "모험과 새로운 경험에 대한 내면의 욕구를 억제합니다. 변화에 대한 두려움으로 인해 안전지대를 벗어나기 어렵습니다.",
      },
    ],
  },
];

const selectedGender = ref(null);
const step = ref("gender");
const selectedColors = ref([]);
const availableColors = ref(colors);
const resultIndex = ref(0);

const handleGenderSelect = (gender) => {
  selectedGender.value = gender;
  step.value = "colors";
};

const handleColorSelect = (colorId) => {
  selectedColors.value.push(colorId);
  availableColors.value = availableColors.value.filter(
    (color) => color.id !== colorId
  );

  if (selectedColors.value.length === colors.length) {
    const firstChoice = selectedColors.value[0];
    let baseIndex = (firstChoice - 1) % resultTypes.length;

    if (selectedGender.value === "male") {
      baseIndex = (baseIndex + 1) % resultTypes.length;
    } else if (selectedGender.value === "female") {
      baseIndex = (baseIndex + 2) % resultTypes.length;
    }

    resultIndex.value = baseIndex;
    step.value = "result";
  }
};

const resetQuiz = () => {
  selectedGender.value = null;
  step.value = "gender";
  selectedColors.value = [];
  availableColors.value = colors;
};

const removeColor = (colorId) => {
  selectedColors.value = selectedColors.value.filter((id) => id !== colorId);
  availableColors.value = [
    ...availableColors.value,
    colors.find((c) => c.id === colorId),
  ].sort((a, b) => a.id - b.id);
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
