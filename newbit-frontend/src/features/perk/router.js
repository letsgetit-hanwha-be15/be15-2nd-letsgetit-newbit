export const perkRoutes = [
  {
    path: "/perks",
    name: "PerkList",
    component: () => import("@/features/perk/views/PerkListView.vue"),
  },
  {
    path: "/perks/quiz",
    name: "PersonalQuiz",
    component: () => import("@/features/perk/views/PersonalQuizView.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/perks/quiz-free",
    name: "FreePersonalQuiz",
    component: () => import("@/features/perk/views/FreePersonalQuizView.vue"),
  },
];
