<template>
  <div id="app">
    <router-view />
  </div>
</template>
<script>
import { jsSdk } from "./api/welcom";
// import wx from 'weixin-js-sdk'
export default {
  created() {
    //type:agent, all, corp
    const corp_id = this.$route.query.corp_id || "wwa67b5f2bf5754641";
    const agent_id = this.$route.query.agent_id || "1000007";

    jsSdk
      .config({
        query: { agent_id, corp_id },
        params: { url: location.origin, type: "corp" },
      })
      .then((res) => {
        debugger;
        console.log(wx);
        let data = res.data;
        wx.agentConfig({
          debug: true,
          corpid: corp_id, // 必填，企业微信的corpid，必须与当前登录的企业一致
          agentid: agent_id, // 必填，企业微信的应用id （e.g. 1000247）
          timestamp: data.timestamp, // 必填，生成签名的时间戳
          nonceStr: data.nonceStr, // 必填，生成签名的随机串
          signature: data.signature, // 必填，签名，见附录-JS-SDK使用权限签名算法
          jsApiList: ["selectExternalContact"], //必填，传入需要使用的接口名称
          success: function (res) {
            // 回调
            console.log("agentConfig", res);
            alert("success");
          },
          fail: function (res) {
            if (res.errMsg.indexOf("function not exist") > -1) {
              alert("版本过低请升级");
            }
          },
        });
        // wx.ready(function (res) {
        //   console.log('ready', res)
        // });
        // wx.error(function (res) {
        //   alert("微信验证失败");
        // });
      });
  },
};
</script>
<style>
body {
  font-size: 16px;
  background-color: #f8f8f8;
  -webkit-font-smoothing: antialiased;
}
</style>
