import router from '@/router'
import store from '@/store'
import axios from 'axios'
import qs from 'qs'
export default {
  // 退出登录
  logout() {
    console.log('退出登录')
    sessionStorage.clear()
    localStorage.clear()
    store.commit('SET_USER', {});
    store.commit('SET_PROID', '');
    store.commit('SET_CORPID', '');
    store.commit('SET_TENANTUUID', '');
    store.commit('SET_PROJECT', []);
    router.push('/login');
  },
  // 获取url参数
  getUrlKey(name) {
    const reg = new RegExp(`(^|&)${name}=([^&]*)(&|$)`, 'i')
    const r = window.location.search.substr(1).match(reg)
    if (r !== null) {
      return unescape(r[2])
    }
    return null
  },
  // 获取路径.后缀
  getSuffix(filename) {
    const pos = filename.lastIndexOf('.')
    let suffix = ''
    if (pos !== -1) {
      suffix = filename.substring(pos)
    }
    return suffix
  },
  // 上传图片、视频
  // 素材类型media_type IMAGE,VIDEO,FILE,MINIPROGRAM 小程序,LINK 网页,VOICE, LOGO
  // 任务类型task_type; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈; , LIVE_CODE活码
  uploadImg(file, media_type, task_type) {
    console.log(file)
    return new Promise((resolve, reject) => {
      const fileData = new FormData();
      fileData.append('file', file, file.name);
      const token = localStorage.getItem('token')
      axios
        .post(`/mktgo/wecom/media/upload?${qs.stringify({...store.getters?.parseId,...{
          task_type: task_type,
          media_type: media_type
        }})}`, fileData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            'header-api-token': token
          }
        })
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            resolve(res.data);
          } else {
            reject(res);
          }
        })
        .catch((err) => {
          console.log(err);
          reject(err);
        });
    });
  },
}
