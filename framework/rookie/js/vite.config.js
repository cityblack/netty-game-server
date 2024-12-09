import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import legacy from '@vitejs/plugin-legacy'

export default defineConfig({
  plugins: [
    vue(),
    legacy({
      targets: ['defaults'],
      additionalLegacyPolyfills: ['regenerator-runtime/runtime'],
      modernPolyfills: true
    })
  ],
  server: {
    proxy: {
      '/ws': {
        target: 'ws://localhost:8081',
        ws: true
      }
    }
  },
  build: {
    target: 'esnext',
    minify: 'terser'
  },
  esbuild: {
    target: 'esnext'
  }
})
