import qs from "qs";
import { jsSdk } from "../api/wecom";

const query = qs.parse(location.search.substring(1));
const corp_id = query.corp_id;
const agent_id = query.agent_id;
const apiList = [
    "getContext",
    "sendChatMessage",
    "openEnterpriseChat",
    "openExistedChatWithMsg",
    "getCurExternalContact",
    "getCurExternalChat",
    "shareToExternalMoments",
    "shareToExternalContact",
]


export const initSDK = (sucfun) => {


    //type:agent, all, corp
    jsSdk
        .config({
            query: { agent_id, corp_id },
            params: {
                url: location.href.split("#")[0],
                type: "corp",
            },
        })
        .then((res) => {
            let data = res.data;
            wx.config({
                beta: true, // 必须这么写，否则wx.invoke调用形式的jsapi会有问题
                debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: corp_id, // 必填，企业微信的corpID，必须是本企业的corpID，不允许跨企业使用
                timestamp: data.corpSignature.timestamp, // 必填，生成签名的时间戳
                nonceStr: data.corpSignature.nonceStr, // 必填，生成签名的随机串
                signature: data.corpSignature.signature, // 必填，签名，见 附录-JS-SDK使用权限签名算法
                jsApiList: apiList, // 必填，需要使用的JS接口列表，凡是要调用的接口都需要传进来
            });

            wx.ready(async () => {
                await agentConfig().then(res => {
                    //设置应用入口
                    wx.invoke("getContext", {}, function (res) {
                        let entry = null
                        if (res.err_msg == "getContext:ok") {
                            entry = res.entry; //返回进入H5页面的入口类型，目前有normal、contact_profile、single_chat_tools、group_chat_tools、chat_attachment
                        }
                        localStorage.setItem("entry", entry);
                        if (typeof sucfun === 'function') {
                            sucfun()
                        }
                    });
                })
            });
        });
}
export const agentConfig = () => {

    return new Promise((resolve, reject) => {
        jsSdk
            .config({
                query: { agent_id, corp_id },
                params: {
                    url: location.href.split("#")[0],
                    type: "agent",
                },
            })
            .then((res) => {
                let data = res.data;
                wx.agentConfig({
                    corpid: corp_id, // 必填，企业微信的corpid，必须与当前登录的企业一致
                    agentid: agent_id, // 必填，企业微信的应用id （e.g. 1000247）
                    timestamp: data.agentSignature.timestamp, // 必填，生成签名的时间戳
                    nonceStr: data.agentSignature.nonceStr, // 必填，生成签名的随机串
                    signature: data.agentSignature.signature, // 必填，签名，见附录-JS-SDK使用权限签名算法
                    jsApiList: apiList,
                    success: function (res) {
                        resolve(res)
                    },
                    fail: function (res) {
                        alert(JSON.stringify(res));
                        if (res.errMsg.indexOf("function not exist") > -1) {
                            alert("版本过低请升级");
                        }
                        reject(err)
                    },
                });
            }).catch(err => {
                reject(err)
            })
    })
}