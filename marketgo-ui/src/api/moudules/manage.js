import server from '@/api/config'
import qs from 'qs'
import store from '@/store'
// 渠道活码列表
export const liveCodeList = data => server.get(`mktgo/wecom/live_code/list?${qs.stringify({...store.getters?.parseId,...data})}`)
// 组织架构
export const userOrganizationalStructure = data => server.post(`mktgo/wecom/user/organizational/structure`, qs.parse(data))
// 根据部门名称查询人员
export const userOrganizationalStructureQuery = data => server.post(`mktgo/wecom/user/organizational/structure/query?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))
// 员工列表
export const contactsMemberList = data => server.get(`mktgo/wecom/contacts/member/list?${qs.stringify({...store.getters?.parseId,...data})}`)
// 创建活码
export const liveCodeCreate = data => server.post(`mktgo/wecom/live_code/create?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))
// 活码删除
export const liveCodeDelete = data => server.post(`mktgo/wecom/live_code/delete?${qs.stringify({...store.getters?.parseId,...data})}`)

// 渠道活码检查名称是否存在
export const liveCodeCheckName = data => server.get(`mktgo/wecom/live_code/check_name?${qs.stringify({...store.getters?.parseId,...data})}`)
// 活码详情
export const liveCodeDetail = data => server.get(`mktgo/wecom/live_code/detail?${qs.stringify({...store.getters?.parseId,...data})}`)
// 活码数据统计详情
export const liveCodeStatisticsList = data => server.get(`mktgo/wecom/live_code/statistics/list?${qs.stringify({...store.getters?.parseId,...data})}`)
// 活码数据详情汇总
export const liveCodeStatisticsSummary = data => server.get(`mktgo/wecom/live_code/statistics/summary?${qs.stringify({...store.getters?.parseId,...data})}`)
// 下载活码
export const liveCodeQrcodeDownload = data => server.get(`mktgo/wecom/live_code/qrcode_download?${qs.stringify({...store.getters?.parseId,...data})}`)
// 获取企业标签信息
export const corpTagsList = data => server.get(`mktgo/wecom/corp_tags/list?${qs.stringify({...store.getters?.parseId,...data})}`)
// 添加企业标签
export const corpTagsAdd = data => server.post(`mktgo/wecom/corp_tags/add?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))
// 删除企业标签
export const corpTagsDelete = data => server.post(`mktgo/wecom/corp_tags/delete?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))
// 编辑企业标签
export const corpTagsEdit = data => server.post(`mktgo/wecom/corp_tags/edit?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))
// 标记企业标签
export const corpTagsMark = data => server.post(`mktgo/wecom/corp_tags/mark?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))
// 获取客户统计详情
export const contactsStatisticDetail = data => server.get(`mktgo/wecom/contacts/statistic/detail?${qs.stringify({...store.getters?.parseId,...data})}`)
// 获取客户统计
export const contactsStatistic = () => server.get(`mktgo/wecom/contacts/statistic?${qs.stringify(store.getters?.parseId)}`)
// 客户列表
export const externalUserList = data => server.get(`mktgo/wecom/contacts/external_user/list?${qs.stringify({...store.getters?.parseId,...data})}`)
// 添加或者更新员工手机号
export const userSave = data => server.post(`mktgo/wecom/user/save?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))
// 修改授权状态
export const userAuthStatus = data => server.post(`mktgo/wecom/user/auth_status?${qs.stringify(store.getters?.parseId)}`, qs.parse(data))

