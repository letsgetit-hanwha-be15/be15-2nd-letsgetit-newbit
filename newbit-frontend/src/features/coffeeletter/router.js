export const coffeeletterRoutes = [
  {
    path: "/coffeeletter/:id",
    name: "Coffeeletter",
    component: () =>
      import("@/features/coffeeletter/views/CoffeeLetterChatView.vue"),
  },
  {
    path: "/coffeeletters/chats",
    name: "CoffeeLetterChats",
    component: () =>
      import("@/features/coffeeletter/views/CoffeeLetterChatsView.vue"),
  },
];
