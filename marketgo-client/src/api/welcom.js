import xhr from './axiosConfig'
import qs from 'qs'

export const welcom={
    taskDetail:(query={})=>{
      return  xhr.get(`mktgo/client/wecom/task_center/detail?${qs.stringify(query)}`)
    }

}