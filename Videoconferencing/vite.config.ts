import { fileURLToPath, URL } from 'node:url'
import basicSsl from '@vitejs/plugin-basic-ssl'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { readFileSync } from 'node:fs'
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    basicSsl()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    https: {
      // cert: readFileSync("./src/key/videotest.lgh.pem"),
      // key: readFileSync("./src/key/GTFO_CRACK.pem"),
      // ca: readFileSync("./src/key/CA.pem"),
      // pfx: readFileSync("./src/key/test.pfx"),
      // passphrase: '123456',
    },
    proxy: {

      '/api': {
        // secure:false,
        changeOrigin: true,
          // target: 'http://localhost:10100',
          // rewrite: (path) => path.replace(/^\/api/, '')

          // target: 'http://120.53.246.47:8080',
          // target: 'https://192.168.123.53/',
          target: {
            protocol:'https:',
            host:'videotest.lgh',
            port: 443,
            ca:  readFileSync('./src/key/CA.pem') as any,
          },
          rewrite: (path) => path.replace(/^\/api/, ''),
          // ssl: {
          //   // pfx: PFX,
          //   pfx: readFileSync("./src/key/test.pfx"),
          //   // passphrase: PFX_PASSPHRASE,
          //   passphrase: 123456,
          // }
          // configure(proxy) {
          //   console.log(proxy)
          //   // options.ssl = {
          //   //   pfx: readFileSync("./src/key/test.pfx"),
          //   //   passphrase: 123456,
          //   // }
          // },
      }
    }
  }
})
