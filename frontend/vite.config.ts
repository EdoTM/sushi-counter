import { defineConfig } from "vite";
import preact from "@preact/preset-vite";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [preact()],
  base: process.env.VITE_HTML_BASE_URL || "/",
  server: {
    port: 80,
  },
});
