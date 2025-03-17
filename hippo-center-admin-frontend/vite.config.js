import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 9173 // 改成你想要的 port
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  }
})
