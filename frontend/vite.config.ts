import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// The SPA is served by Spring Boot under /app/ (built into static/app); the
// dev server proxies API calls to the locally running Boot instance.
export default defineConfig({
  plugins: [vue()],
  base: '/app/',
  build: {
    outDir: '../src/main/resources/static/app',
    emptyOutDir: true,
  },
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/resources': 'http://localhost:8080',
    },
  },
});
