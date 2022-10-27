import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import store from './store'
import 'babel-polyfill'
import request from '@/utils/request'
import utils from '@/utils' // 自定义方法库
import global from '@/mixins/global.js'

import '@/assets/index.css'
import api from '@/api' // 接口配置
//  引入Echarts
import * as echarts from 'echarts'

// 引入全局头部标题组件
import MainHead from "@/components/MainHead.vue";
Vue.component('MainHead', MainHead)
// 引入全局自定义提示组件
import CustomAlert from "@/components/CustomAlert.vue";
Vue.component('CustomAlert', CustomAlert)
// 引入全局自定义数字输入组件
import CustomNumber from "@/components/CustomNumber.vue";
Vue.component('CustomNumber', CustomNumber)
// 引入全局自定义数字输入组件
import CustomStretch from "@/components/CustomStretch.vue";
Vue.component('CustomStretch', CustomStretch)

Vue.prototype.$utils = utils
Vue.prototype.$http = request
Vue.prototype.$api = api
Vue.prototype.$echarts = echarts
Vue.prototype.$global = global
Vue.use(ElementUI)
Vue.config.productionTip = false
new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
