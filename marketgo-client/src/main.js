import { createApp } from "vue";
import App from "./App.vue";
import { router } from "./router";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import qs from "qs";
import { wecom } from "./api/wecom";
import { initSDK } from './utils/jsSDK'

const query = qs.parse(location.search.substring(1));
const corp_id = query.corp_id;
const agent_id = query.agent_id;
const code = query.code;

const initApp = () => {
  const pinia = createPinia();
  pinia.use(piniaPluginPersistedstate);
  createApp(App).use(router).use(pinia).mount("#app");
};

if (oAuth2 && !code) {
  const location = window.location;
  let REDIRECT_URI = encodeURIComponent(`${location.href}`);
  let STATE = new Date().getTime();
  window.location = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corp_id}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=snsapi_base&state=${STATE}&agentid=${agent_id}#wechat_redirect`;
} else {
  if (query.code) {
    wecom
      .verifyCode({
        query: { corp_id, agent_id },
        params: { code },
      })
      .then((res) => {    
        delete query.code;
        delete query.state;
        history.replaceState(
          null,
          null,
          `${location.origin}${location.pathname}?${qs.stringify(query)}`
        );
        if (res.code === 0) {
          localStorage.setItem("user", JSON.stringify(res.data));
          //初始化
          initSDK(initApp)
        } else {
          //验证code失败
          window.location.reload();
        }
      })
      .catch((_) => {
        console.log(_)
        initApp();
      });
  } else {
    //不走企业微信
    initApp();
  }
}
