import xhr from "./axiosConfig";
import qs from "qs";
const handleQuery = (query) => {
  delete query.code
  delete query.state
}
export const wecom = {
  taskDetail: (query = {}) => {
    handleQuery(query)
    return xhr.get(
      `mktgo/client/wecom/task_center/detail?${qs.stringify(query)}`
    );
  },
  changeStatus: ({ query = {}, params = {} }) => {
    handleQuery(query)
    return xhr.post(
      `mktgo/client/wecom/task_center/status?${qs.stringify(query)}`,
      params
    );
  },
  sidebarDetail: (query = {}) => {
    handleQuery(query)
    return xhr.get(`mktgo/client/wecom/sidebar/detail?${qs.stringify(query)}`);
  },
  sidebarGroupDetail: (query = {}) => {
    handleQuery(query)
    return xhr.get(`mktgo/client/wecom/sidebar/group_chat/detail?${qs.stringify(query)}`);
  },
  taskList: (query = {}) => {
    handleQuery(query)
    return xhr.get(
      `/mktgo/client/wecom/task_center/list?${qs.stringify(query)}`
    );
  },
  taskSubList: (query = {}) => {
    handleQuery(query)
    return xhr.get(
      `/mktgo/client/wecom/task_center/sub_list?${qs.stringify(query)}`
    );
  },
  verifyCode: ({ query = {}, params = {} }) => {
    return xhr.post(
      `/mktgo/client/wecom/verify?${qs.stringify(query)}`,
      params
    );
  }
};

export const jsSdk = {
  config: ({ query = {}, params = {} }) => {
    return xhr.post(
      `mktgo/client/wecom/sdk_config?${qs.stringify(query)}`,
      params
    );
  },
};

