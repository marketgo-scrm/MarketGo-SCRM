<template>
  <div class="sidebar_wrapper">
    <!-- <van-nav-bar title="代办任务" /> -->
    <van-cell-group class="info" v-if="false">
      <img :src="'data:image/Jpeg;base64,' + imgBase64" alt="" /><span class="desc">{{ "name" }}有关的代办任务</span>
    </van-cell-group>
    <template v-if="list && list.length > 0">
      <van-cell-group style="background: transparent" v-for="task of list" :key="task.uuid" class="task-wrapper">
        <div class="top">
          <div style="display: flex; justify-content: space-between">
            <span class="title">{{ task.taskName ? task.taskName : "" }}</span><span @click="finish(task)"
              class="status">标记为完成</span>
          </div>
          <div class="date" v-if="task.planTime">
            <span>{{ task.planTime + " " }}前完成</span>
          </div>
        </div>
        <div class="content">
          <template v-for="(attach, idx) of task.attachments" :key="idx">
            <div class="text-wrapper" v-if="attach.type.toLocaleUpperCase() === 'TEXT'">
              <div class="txt">
                {{ (attach && attach.text.content) || "" }}
              </div>
              <div class="txt-btns">
                <van-button plain type="primary" size="mini" @click="send(attach)" v-if="false">编辑后发送</van-button>
                <van-button plain type="primary" size="mini" @click="send(attach)">直接发送</van-button>
              </div>
            </div>
            <div class="image-wrapper" v-if="attach.type.toLocaleUpperCase() === 'IMAGE'">
              <div class="imgs">
                <img :src="'data:image/Jpeg;base64,' + attach.image.imageContent" alt="" />
              </div>
              <div class="btns">
                <van-button :disabled="!attach.image.mediaId" plain type="primary" size="mini"
                  @click="send(attach)">直接发送</van-button>
              </div>
            </div>
          </template>
        </div>
      </van-cell-group>
    </template>
    <listEmpty v-else></listEmpty>
  </div>
</template>

<script>
import { showSuccessToast } from "vant";
import "vant/es/toast/style";
import { wecom } from "../../api/wecom";
import listEmpty from "../components/listEmpty.vue";
import moment from 'moment'
export default {
  name: "sidebar",
  components: { listEmpty },
  data() {
    return {
      imgBase64:
        "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",
      list: [],
      queryExt: {
        group_chat_id: '',
        externalUserId: "",
        memberId: "",
      },
      jsApiReady: false,
      query: {},
      entry: null
    };
  },
  created() {
    this.entry = localStorage.getItem("entry");//入口

    this.query = this.$route.query;
    if (localStorage.getItem("user")) {
      this.query.member_id = JSON.parse(localStorage.getItem("user")).userId;
      this.queryExt.memberId = this.query.member_id
    }
    this.getCurExternalContact()
  },

  methods: {
    getCurExternalContact(sucFun) {
      try {
        if (this.entry === 'single_chat_tools') {
          wx.invoke("getCurExternalContact", {}, (res) => {
            if (res.err_msg == "getCurExternalContact:ok") {
              this.queryExt.externalUserId = res.userId; //返回当前外部联系人userId
              this.getDetail()
            } else {
              //错误处理
              alert(JSON.stringify(res))
            }
          });
        }
        if (this.entry === 'group_chat_tools') {
          wx.invoke('getCurExternalChat', {
          }, (res) => {
            if (res.err_msg == "getCurExternalChat:ok") {
              this.queryExt.group_chat_id = res.chatId; //返回当前外部群的群聊ID
              this.getGroupDetail()
            } else {
              //错误处理
              alert(JSON.stringify(res))
            }
          });
        }
      } catch (_) {

      }
    },
    getDetail() {
      const params = { ...this.query, external_user_id: this.queryExt.externalUserId }
      wecom.sidebarDetail(params).then((res) => {
        this.list = res.data || [];
      });
    },
    getGroupDetail() {
      const params = { ...this.query, group_chat_id: this.queryExt.group_chat_id }
      wecom.sidebarGroupDetail(params).then((res) => {
        this.list = res.data || [];
      });
    },
    send(attach) {
      let msgObject = {};
      msgObject.msgtype = attach.type.toLocaleLowerCase();
      if (msgObject.msgtype === "text") {
        msgObject.text = { content: attach.text.content };
      }
      if (msgObject.msgtype === "image") {
        msgObject.image = { mediaid: attach.image.mediaId };
      }
      console.log(msgObject);
      wx.invoke("sendChatMessage", {
        msgtype: "text",
        enterChat: true,
        ...msgObject,
      });
    },
    finish(task) {
      const query = { corp_id: task.corpId };
      const params = {
        externalUserId: "", //sdk单独获取
        memberId: "",
        sentTime: moment(new Date()).format('YYYY-MM-DD HH:mm:ss'),
        status: "DELIVERED",
        taskUuid: task.taskUuid,
        type: "EXTERNAL_USER", // EXTERNAL_USER 客户
        uuid: task.uuid,
        ...this.queryExt
      };
      wecom.changeStatus({ query, params }).then((res) => {
        if (res.code === 0) {
          showSuccessToast("成功");
        }
      });
    },
  },
  computed: {},
};
</script>

<style lang="less" scoped>
.sidebar_wrapper {
  font-family: "PingFang SC";
  background: #f5f7fa; //#D8D8D8;
  color: #333333;
  margin-bottom: 100px;
  position: relative;

  :deep(.van-nav-bar__content) {
    background: #679bff;

    .van-nav-bar__title {
      color: #fff;
    }
  }

  .info {
    background: #fff;
    padding: 15px;
    margin-bottom: 20px;
    display: flex;

    .desc {
      font-style: normal;
      font-weight: 600;
      color: #333333;
      display: inline-block;
      padding-left: 10px;
    }

    img {
      width: 36px;
    }
  }

  .top {
    background: #fff;
    padding: 15px 15px 0;

    .title {
      font-style: normal;
      font-weight: 600;
      color: #333333;
      display: inline-block;
      width: calc(100% - 80px);
    }

    .status {
      color: #679bff;
      font-style: normal;
      font-weight: 400;
      font-size: 14px;
      cursor: pointer;
    }

    .date {
      font-style: normal;
      font-weight: 400;
      line-height: 24px;
      color: #ff993c;
      padding-top: 20px;
      font-size: 14px;
    }
  }

  .task-wrapper {
    &:nth-child(n + 3) {
      margin-top: 10px;
    }

    .content {
      background: #fff;
      padding: 15px;

      .text-wrapper {
        margin-top: 10px;

        .txt {
          padding: 10px;
          background: #f5f7fa;
          min-height: 60px;
        }

        .txt-btns {
          margin-top: 10px;
          text-align: right;

          button {
            &:not(:first-child) {
              margin-left: 20px;
            }
          }
        }
      }

      .image-wrapper {
        margin-top: 10px;
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
  }
}
</style>
