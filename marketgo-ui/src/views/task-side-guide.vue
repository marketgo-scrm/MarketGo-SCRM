<template>
    <div class="callback-wrapper">

        <div class="tops">
            <main-head title="如何待办任务侧边栏?" :back="true"></main-head>
        </div>
        <div class="content">
            <div class="title">配置链接</div>
            <div class="section-one">
                <el-form :model="callbackConfig" label-width="60px" label-position="left">
                    <el-form-item label="配置链接">
                        <el-input readonly v-model="callbackConfig.sidebarUrl"></el-input>
                        <el-button style="margin-left: 20px" type="primary" size="small" round class="tag"
                            @click="copy(callbackConfig.sidebarUrl)">复制</el-button>
                    </el-form-item>

                </el-form>
            </div>
            <div v-show="dicts" class="guide-wrapper">
                <div class="dictlist" v-for="(item, index) in dicts" :key="index + 'a'">
                    <span>
                        <span v-html='item.dist'></span>
                    </span>
                    <img v-for="(val, idx) in item.imglist" :key="idx + 'b'" :src="val" alt="" />
                </div>
            </div>
            <div>
                <el-button type="primary" class="finish" icon="" size="mini" round @click="finish">完成
                </el-button>
            </div>

        </div>
    </div>
</template>

<script>
import Clipboard from "clipboard";
export default {
    name: "callback",
    data() {
        return {
            callbackConfig: {},
            dicts: [
                {
                    dist: "1、登录<a target='_blank' class='acls' href='https://work.weixin.qq.com/wework_admin/loginpage_wx?from=myhome'>企业微信管理后台</a>，点击【客户与上下游-聊天工具】进入【聊天工具栏管理】",
                    imglist: [require("../assets/taskStep/1.png")],
                },
                {
                    dist: "2、在聊天工具栏管理页，点击【配置应用页面】",
                    imglist: [
                        require("../assets/taskStep/2.png")
                    ],
                },
                {
                    dist: "3、填写【页面名称】-待办任务，选择页面内容【自定义】，粘贴配置链接，点击【确定】",
                    imglist: [require("../assets/taskStep/3.png")],
                },
                {
                    dist: "4、在聊天工具栏管理页，可以通过拖拽配置新创建的聊天工具栏应用顺序，例如把待办任务放到第1位",
                    imglist: [require("../assets/taskStep/4.png")],
                },
                {
                    dist: "5、完成配置后，【待办任务】工具即可在聊天工具栏使用",
                    imglist: [],
                },
            ],
        };
    },
    created() { },
    mounted() {
        this.getForwardConfig()
    },
    methods: {
        copy(data) {
            let clipboard = new Clipboard(".tag", {
                text: function () {
                    return data;
                },
            });
            clipboard.on("success", () => {
                this.$message({
                    message: "复制成功",
                    showClose: true,
                    type: "success",
                });
                // 释放内存
                clipboard.destroy();
            });
            clipboard.on("error", () => {
                this.$message({ message: "复制失败,", showClose: true, type: "error" });
                clipboard.destroy();
            });
        },
        finish() {
            history.go(-1);
        },


        getForwardConfig() {
            this.$http
                .get(
                    `/mktgo/wecom/corp/sidebar/config?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}`,
                    {}
                )
                .then((res) => {
                    this.callbackConfig = res.data;
                });
        },

    },
};
</script>

<style lang="scss" scoped>
.callback-wrapper {
    .tops {
        width: 100%;
        height: 40px;
        display: flex;
        flex-direction: column;
        font-size: 12px;
    }

    .content {
        height: calc(100% - 70px);
        background: #fff;
        border-radius: 10px;
        padding: 5px 20px 58px;

        .title {
            font-weight: 600;
            font-size: 14px;
            line-height: 20px;
            color: #333333;
            // background-color: aqua;
            padding: 16px 0;
        }

        .section-one {
            width: 720px;
            padding: 25px 0px 1px 25px;
            background-color: #f8f6f8;
            box-sizing: border-box;

            ::v-deep(.el-input) {
                width: 530px;
            }
        }
    }

    .guide-wrapper {
        padding: 32px 0 32px 0px;
    }

    .dictlist {
        width: 720px;
    }

    .dictlist>span {
        display: inline-block;
        font-weight: 600;
        font-size: 12px;
        margin-bottom: 16px;
    }

    .dictlist img {
        width: 720px;
        margin-bottom: 16px;
    }
    .finish {
        width: 96px;
        font-size: 12px;
    }
}
</style>
