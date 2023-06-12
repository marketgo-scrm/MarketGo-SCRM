import vue from "@vitejs/plugin-vue";
import Components from "unplugin-vue-components/vite";
import { VantResolver } from "unplugin-vue-components/resolvers";
const { resolve } = require('path');
const alias='/marketgo/'
export default {
  base: alias,
  publicDir: resolve(__dirname,'./public'),
  server: {
    host: '0.0.0.0',
  },
  plugins: [
    vue(),
    Components({
      resolvers: [VantResolver()],
    }),
  ],
  build:{
    outDir: `dist${alias}`,
  }
};
