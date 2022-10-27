import axios from 'axios'
import { Notification, Message } from 'element-ui'
import { tansParams } from "./common";
import router from '../router'
import global from '@/mixins/global.js'
import utils from '@/utils' // 自定义方法库
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: global.BASEURL,
  // 超时
  timeout: 10000
})
service.interceptors.request.use(config => {
   
    // const isToken = (config.headers || {}).isToken === false
    // if (getToken() && !isToken) {
      if(localStorage.getItem('token')){
        config.headers['header-api-token'] = localStorage.getItem('token')
      }
    config.params = {
      ...config.params
    }
    // get请求映射params参数
    if (config.method === 'get' && config.params) {
      let url = config.url + '?' + tansParams(config.params);
      url = url.slice(0, -1);
      config.params = {}
      config.url = url;
    }
    return config
  }, error => {
      Promise.reject(error)
  })
  
  // 响应拦截器
  service.interceptors.response.use(res => {
      // 未设置状态码则默认成功状态
      const code = res.data.code || 0;
      // 获取错误信息
      const msg = res.data.message
      // 二进制数据则直接返回
      if(res.request.responseType ===  'blob' || res.request.responseType ===  'arraybuffer'){
        return res.data
      }
      if (code === 401||code===1156) {
        // MessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
        //     confirmButtonText: '重新登录',
        //     cancelButtonText: '关闭',
        //     type: 'warning'
        //   }
        // ).then(() => {
        //     // location.href = '/index';
        //     router.push({
        //       path:'/login'
        //     })
        // }).catch(() => {});
        router.push({
          path:'/login'
        })
        utils.logout()
         Promise.reject('无效的会话，或者会话已过期，请重新登录。')
         return  res.data
      } else if (code === 500) {
        Message({
          message: msg,
          type: 'error'
        })
         Promise.reject(new Error(msg))
         return  res.data
      } else if (code !== 0 && (code < 10000 || code > 10006)) {
        Notification.error({
          title: msg
        })
         Promise.reject('error')
         return  res.data
      } else {
        return res.data
      }
    },
    error => {
      let { message } = error;
      if (message == "Network Error") {
        message = "后端接口连接异常";
      }
      else if (message.includes("timeout")) {
        message = "系统接口请求超时";
      }
      else if (message.includes("Request failed with status code")) {
        message = "系统接口" + message.substr(message.length - 3) + "异常";
      }
      Message({
        message: message,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error)
    }
  )
export default service