import xhr from './axiosConfig'
import qs from 'qs'

export const welcom = {
  taskDetail: (query = {}) => {
    return xhr.get(`mktgo/client/wecom/task_center/detail?${qs.stringify(query)}`)
  },
  changeStatus: ({query = {}, params = {}}) => {
    return xhr.post(`mktgo/client/wecom/task_center/status?${qs.stringify(query)}`, params)
  },
  sidebarDetail: (query = {}) => {
    return xhr.get(`mktgo/client/wecom/sidebar/detail?${qs.stringify(query)}`)
  },
  taskList: (query = {}) => {
    return xhr.get(`/mktgo/client/wecom/task_center/list?${qs.stringify(query)}`)
  },
  taskSubList: (query = {}) => {
    return xhr.get(`/mktgo/client/wecom/task_center/sub_list?${qs.stringify(query)}`)
  }

}

export const jsSdk = {
  config: ({query = {}, params = {}}) => {
    return xhr.post(`mktgo/client/wecom/sdk_config?${qs.stringify(query)}`, params)
  },
}