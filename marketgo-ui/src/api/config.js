import axios from 'axios'
import utils from '@/utils' // 自定义js方法
import global from '@/mixins/global.js'
/**
 * @development 开发环境、
 * @production 生产环境
 */
// 请求超时时间
axios.defaults.timeout = 10000
// 引入全局url，定义在全局变量
axios.defaults.baseURL = global.BASEURL
// 封装请求拦截
axios.interceptors.request.use(
  config => {
    config.headers = {
      'Content-Type': 'application/json'
    }
    if(localStorage.getItem('token')){
      config.headers['header-api-token'] = localStorage.getItem('token')
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)
// 封装响应拦截，判断token是否过期
axios.interceptors.response.use(
  response => {
    let data = {}
    if (response.data.code === 1156) {
      utils.logout()
      data = Promise.reject(response.data)
    } else {
      data = Promise.resolve(response.data)
    }
    return data
  },
  (error) => {
    if (error && error.response) {
      console.log(error.response)
      switch (error.response.status) {
        case 400:
          console.log('错误请求')
          break
        case 401:
          console.log('未授权，请重新登录')
          utils.logout()
          break
        case 403:
          console.log('拒绝访问')
          break
        case 404:
          console.log('请求错误,未找到该资源')
          break
        case 405:
          console.log('请求方法未允许')
          break
        case 408:
          console.log('请求超时')
          break
        case 500:
          console.log('服务器端出错')
          break
        case 501:
          console.log('网络未实现')
          break
        case 502:
          console.log('网络错误')
          break
        case 503:
          console.log('服务不可用')
          break
        case 504:
          console.log('网络超时')
          break
        case 505:
          console.log('http版本不支持该请求')
          break
        default:
          console.log('连接错误$ {error.response.status}')
      }
    } else {
      console.log('连接到服务器失败')
    }
    return Promise.resolve(error.response)
  }
)

export default axios
