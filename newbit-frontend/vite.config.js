import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    proxy: {
      "/api/v1": {
        target: "http://localhost:8000",
        changeOrigin: true,
        ws: true,
        secure: false,
      },
      "/api/v1/feature/ws": {
        target: "http://localhost:8000",
        changeOrigin: true,
        ws: true,
        secure: false,
      },
    },
  },
  define: {
    global: {},
  },
});
