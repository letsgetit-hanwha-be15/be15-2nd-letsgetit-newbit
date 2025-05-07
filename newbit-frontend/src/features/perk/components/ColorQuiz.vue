<template>
  <div
    class="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 p-4 sm:p-8"
  >
    <div class="max-w-5xl mx-auto">
      <transition name="fade" mode="out-in">
        <!-- 성별 선택 단계 -->
        <div v-if="step === 'gender'" class="text-center" key="gender">
          <div class="mb-12">
            <h1
              class="text-4xl font-bold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600"
            >
              성별을 선택해주세요
            </h1>
            <p class="text-gray-600 max-w-xl mx-auto">
              여러분의 성별에 따라 더 정확한 성격 분석 결과를 제공해 드립니다.
            </p>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-3 gap-6 max-w-3xl mx-auto">
            <button
              v-for="gender in [
                { id: 'male', label: '남성', icon: UserIcon, color: 'blue' },
                {
                  id: 'female',
                  label: '여성',
                  icon: UserGroupIcon,
                  color: 'rose',
                },
                {
                  id: 'non-binary',
                  label: '논바이너리',
                  icon: SparklesIcon,
                  color: 'purple',
                },
              ]"
              :key="gender.id"
              @click="handleGenderSelect(gender.id)"
              class="flex flex-col items-center p-8 rounded-2xl bg-white shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105 active:scale-95"
            >
              <div
                class="w-24 h-24 rounded-full flex items-center justify-center mb-5 transition-all"
                :class="{
                  'bg-blue-500': gender.color === 'blue',
                  'bg-rose-500': gender.color === 'rose',
                  'bg-purple-500': gender.color === 'purple',
                }"
              >
                <component :is="gender.icon" class="w-12 h-12 text-white" />
              </div>
              <span class="font-semibold text-xl tracking-wide">{{
                gender.label
              }}</span>
            </button>
          </div>
        </div>

        <!-- 색상 선택 단계 -->
        <div v-else-if="step === 'colors'" class="text-center" key="colors">
          <div class="mb-12">
            <h1
              class="text-4xl font-bold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600"
            >
              좋아하는 색상을 선택해주세요
            </h1>
            <p class="text-gray-600 max-w-xl mx-auto">
              가장 선호하는 순서대로 색상을 선택하시면, 여러분의 성격과 성향을
              분석해 드립니다.
            </p>
          </div>

          <!-- 진행 상황 표시 -->
          <div v-if="selectedColors.length > 0" class="mb-8 max-w-md mx-auto">
            <div class="flex justify-between text-sm text-gray-600 mb-2">
              <span
                >{{ selectedColors.length }} / {{ colors.length }} 선택됨</span
              >
              <span
                >완료까지 {{ colors.length - selectedColors.length }}개
                남음</span
              >
            </div>
            <div class="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
              <div
                class="h-full bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full transition-all duration-500"
                :style="`width: ${
                  (selectedColors.length / colors.length) * 100
                }%`"
              ></div>
            </div>
          </div>

          <!-- 색상 그리드 -->
          <div
            class="grid grid-cols-2 md:grid-cols-4 gap-6 max-w-4xl mx-auto mb-10"
          >
            <ColorCard
              v-for="color in availableColors"
              :key="color.id"
              :color="color.hex"
              :color-name="color.name"
              :is-selected="selectedColors.includes(color.id)"
              @select="() => handleColorSelect(color.id)"
            />
          </div>

          <!-- 선택된 색상 표시 -->
          <div
            v-if="selectedColors.length > 0"
            class="bg-white rounded-2xl p-6 shadow-lg max-w-3xl mx-auto"
          >
            <h2 class="font-semibold text-lg mb-4">
              선택한 색상 (클릭하여 취소 가능)
            </h2>
            <div class="flex flex-wrap justify-center gap-3">
              <div
                v-for="(colorId, index) in selectedColors"
                :key="colorId"
                class="relative group"
              >
                <div
                  class="w-12 h-12 rounded-lg transition-all duration-300 transform group-hover:scale-110"
                  :style="{
                    backgroundColor: colors.find((c) => c.id === colorId)?.hex,
                    boxShadow: `0 4px 6px -1px ${
                      colors.find((c) => c.id === colorId)?.hex
                    }40`,
                  }"
                >
                  <span
                    class="absolute -top-2 -right-2 bg-white text-gray-800 text-xs w-5 h-5 flex items-center justify-center rounded-full shadow-md font-semibold"
                  >
                    {{ index + 1 }}
                  </span>
                  <button
                    @click="removeColor(colorId)"
                    class="absolute -top-2 -right-2 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center shadow-lg opacity-0 group-hover:opacity-100 transition-all duration-300 transform scale-90 group-hover:scale-100"
                  >
                    <XMarkIcon class="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 결과 단계 -->
        <div v-else class="text-center" key="result">
          <div class="mb-12">
            <h1
              class="text-4xl font-bold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600"
            >
              당신의 성격 분석 결과
            </h1>
            <p class="text-gray-600 max-w-xl mx-auto">
              색상 선호도를 기반으로 분석한, 여러분의 성격과 심리적 특성을
              살펴보세요.
            </p>
          </div>

          <div
            class="bg-white p-10 rounded-2xl shadow-xl max-w-3xl mx-auto mb-10 transition-all duration-500 hover:shadow-2xl transform hover:scale-[1.02]"
          >
            <div class="flex justify-center mb-8">
              <div
                class="w-20 h-20 rounded-full bg-gradient-to-r from-purple-500 to-blue-500 flex items-center justify-center"
              >
                <SparklesIcon class="w-10 h-10 text-white" />
              </div>
            </div>

            <h2 class="text-2xl font-bold mb-6 text-gray-800">
              {{ resultTypes[resultIndex].summary }}
            </h2>

            <div
              v-for="(section, index) in resultTypes[resultIndex].sections"
              :key="index"
              class="mb-6 p-6 rounded-xl transition-all duration-300 hover:shadow-md"
              :class="{
                'bg-blue-50': index === 0,
                'bg-amber-50': index === 1,
                'bg-indigo-50': index === 2,
              }"
            >
              <div class="flex items-center gap-3 mb-3">
                <div
                  class="w-10 h-10 rounded-full flex items-center justify-center"
                  :class="{
                    'bg-blue-100 text-blue-600': index === 0,
                    'bg-amber-100 text-amber-600': index === 1,
                    'bg-indigo-100 text-indigo-600': index === 2,
                  }"
                >
                  <component
                    :is="
                      index === 0
                        ? ChartBarIcon
                        : index === 1
                        ? ExclamationTriangleIcon
                        : LightBulbIcon
                    "
                    class="w-6 h-6"
                  />
                </div>
                <h3 class="text-xl font-semibold">{{ section.title }}</h3>
              </div>
              <p class="text-gray-700 leading-relaxed">{{ section.content }}</p>
            </div>
          </div>

          <button
            @click="resetQuiz"
            class="px-8 py-4 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-xl hover:from-blue-600 hover:to-indigo-700 transition-all duration-300 transform hover:scale-105 active:scale-95 shadow-lg hover:shadow-xl flex items-center gap-2 mx-auto font-medium"
          >
            <ArrowPathIcon class="w-5 h-5" />
            다시 테스트하기
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
    summary: "당신은 실용적인 특성을 가진 타고난 리더입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "현재 약간 게으른 편이며 노력을 많이 하지 않으려고 합니다. 안정적인 뿌리를 내리고 평화롭고 사랑스러운 파트너가 필요합니다. 다른 사람들이 당신의 가치를 알아주길 원합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "삶의 좋은 것들과 감각적인 것들을 즐기지만 비판적일 수 있습니다. 신중하고 조심스러우며 자신이 조종당하거나 속지 않는다고 믿어야 합니다. 불확실성과 갑작스러운 변화에 불안을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "자신감이 낮지만 그것이 갈등을 회피하는 이유라는 것을 인정하지 못합니다. 스트레스가 많고 긴장된 상황으로 인해 정서적으로 지쳐 있습니다. 더 개방적이고 자유롭게 표현하고 싶은 욕구가 있습니다.",
      },
    ],
  },
  {
    summary:
      "당신은 감성적 연결을 중요시하는 창의적이고 상상력이 풍부한 성격입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "주변 환경에 매우 민감하고 세심한 관찰자입니다. 다른 사람들의 감정과 필요에 맞춰 자신을 조정하려고 노력합니다. 조화와 균형을 중요시하며 갈등을 피하려 합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "과도한 책임감과 완벽주의 성향이 스트레스의 주요 원인입니다. 모든 상황을 통제하려고 하며, 예측할 수 없는 변화에 불안감을 느낍니다. 타인의 기대를 충족시키지 못하는 것에 두려움을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "내면의 감정을 표현하는 데 어려움을 겪고 있으며, 자신의 진정한 욕구를 억제하는 경향이 있습니다. 더 자유롭고 모험적인 측면을 드러내지 못하고 있습니다.",
      },
    ],
  },
  {
    summary: "당신은 객관적인 추론을 선호하는 분석적이고 논리적인 사람입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "새로운 경험과 자극을 찾고 있으며, 일상적인 패턴에 지루함을 느끼고 있습니다. 더 많은 모험과 다양성을 원합니다. 지적인 도전과 성장 기회를 추구합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "자유와 독립성이 제한되는 상황이 주요 스트레스 원인입니다. 의무와 책임으로 인해 자신의 열정을 추구할 시간이 부족하다고 느낍니다. 비효율적인 시스템이나 비논리적인 결정에 좌절감을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "충동적인 본성을 억제하려고 노력하고 있으며, 때로는 이로 인해 내적 긴장이 발생합니다. 감정적 측면을 억누르고 지나치게 이성적으로 행동하려는 경향이 있습니다.",
      },
    ],
  },
  {
    summary:
      "당신은 사람과 사회적 상호작용에 중점을 둔 외향적이고 활기찬 성격입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "정서적으로 안정된 환경을 원하며 갈등과 불확실성을 피하려고 합니다. 조화로운 관계를 중요하게 생각합니다. 주변 사람들에게 영감을 주고 에너지를 불어넣는 역할을 합니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "변화와 불안정성이 주요 스트레스 원인입니다. 미래에 대한 불확실성과 예측할 수 없는 상황에 불안을 느낍니다. 사회적 고립이나 주변 사람들의 부정적인 감정에 쉽게 영향을 받습니다.",
      },
      {
        title: "억제된 특성",
        content:
          "자신의 불안을 숨기고 강한 모습을 보여주려고 노력합니다. 타인에게 의존하고 싶은 욕구를 억제하며 독립적으로 보이려고 합니다. 때로는 내면의 평온함을 찾기 위한 혼자만의 시간이 필요합니다.",
      },
    ],
  },
  {
    summary:
      "당신은 감정을 강렬하게 경험하는 민감하고 깊은 사고를 하는 성격입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "높은 기준과 이상을 가지고 있으며, 자신과 타인에게 많은 것을 기대합니다. 완벽주의 성향이 있습니다. 깊이 있는 의미와 목적을 추구하는 경향이 있습니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "자신이 설정한 높은 기준에 도달하지 못할 때 스트레스를 받습니다. 자기비판적이며 실수에 대해 매우 엄격합니다. 피상적인 관계나 의미 없는 대화에 답답함을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "실패에 대한 두려움으로 인해 새로운 도전을 시도하는 것을 망설입니다. 감정적 표현을 억제하고 이성적으로 보이려고 노력합니다. 자신의 진정한 생각과 느낌을 표현하지 못해 답답함을 느낍니다.",
      },
    ],
  },
  {
    summary:
      "당신은 실용적인 접근 방식을 가진 체계적이고 세부사항을 중시하는 성격입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "깊은 연결과 의미 있는 관계를 추구합니다. 표면적인 상호작용보다 진정성 있는 교류를 선호합니다. 신뢰와 안정성을 바탕으로 한 환경에서 가장 편안함을 느낍니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "진정한 이해와 공감을 받지 못하는 상황이 스트레스의 주요 원인입니다. 깊은 연결 없이는 고립감을 느낍니다. 변화가 빠르고 예측할 수 없는 환경에서 불안감을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "자신의 감정적 깊이를 타인에게 보여주는 것을 두려워합니다. 거절에 대한 두려움으로 인해 자신을 완전히 개방하지 않습니다. 더 자유롭고 즉흥적인 측면을 발현하고 싶은 욕구가 있습니다.",
      },
    ],
  },
  {
    summary: "당신은 새로운 아이디어와 경험을 좋아하는 타고난 낙관주의자입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "독립적이고 자율적인 생활 방식을 추구합니다. 자신의 결정을 스스로 내리고 자유롭게 행동하기를 원합니다. 새로운 가능성을 탐색하고 다양한 관점을 접하는 것을 즐깁니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "자신의 자유와 독립성이 제한될 때 스트레스를 받습니다. 타인의 기대나 사회적 규범에 맞추는 것이 부담스럽습니다. 일상적인 루틴이나 지루한 세부사항에 집중해야 할 때 어려움을 느낍니다.",
      },
      {
        title: "억제된 특성",
        content:
          "타인과의 정서적 유대감에 대한 욕구를 억제합니다. 취약성을 보이는 것을 두려워하여 감정적 방어벽을 세웁니다. 때로는 더 안정적이고 체계적인 접근 방식이 필요함을 인정하기 어려워합니다.",
      },
    ],
  },
  {
    summary:
      "당신은 명확한 원칙과 강한 직업 윤리를 가진 조직적이고 체계적인 성격입니다",
    sections: [
      {
        title: "현재 상황",
        content:
          "안정성과 일관성을 중요시합니다. 명확한 구조와 질서 있는 환경에서 가장 편안함을 느낍니다. 책임감이 강하고 자신의 의무를 성실히 수행하는 특성이 있습니다.",
      },
      {
        title: "스트레스 원인",
        content:
          "예측할 수 없는 변화와 혼란이 주요 스트레스 원인입니다. 통제력을 잃는 것에 불안을 느낍니다. 구체적인 지침이나 명확한 기대가 없는 상황에서 어려움을 겪습니다.",
      },
      {
        title: "억제된 특성",
        content:
          "모험과 새로운 경험에 대한 내면의 욕구를 억제합니다. 변화에 대한 두려움으로 인해 안전지대를 벗어나기 어렵습니다. 때로는 더 유연하고 즉흥적인 접근 방식을 취하고 싶은 욕구가 있습니다.",
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
  // 애니메이션을 위한 클래스 추가 로직이 필요하다면 여기에 구현
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
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 부드러운 배경 전환 */
.bg-gradient-to-br {
  background-size: 400% 400%;
  animation: gradientFlow 15s ease infinite;
}

@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>
