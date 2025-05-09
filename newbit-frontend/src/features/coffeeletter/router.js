export const coffeeletterRoutes = [
  {
    path: "/coffeeletter/:id",
    name: "Coffeeletter",
    component: () =>
      import("@/features/coffeeletter/views/CoffeeLetterChatView.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/coffeeletters/chats",
    name: "CoffeeLetterChats",
    component: () =>
      import("@/features/coffeeletter/views/CoffeeLetterChatsView.vue"),
    meta: { requiresAuth: true },
  },
];
