import server from '@/api/config'
import qs from 'qs'
// 登陆
export const login = data => server.post('mktgo/wecom/user/login', qs.parse(data))
// 修改密码
export const changePassWord = data => server.post('mktgo/wecom/user/changePassWord', qs.parse(data))
