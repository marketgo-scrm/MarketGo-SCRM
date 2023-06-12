<template>
  <div class="receiver-wrapper">
    <van-nav-bar left-arrow @click-left="back" title="客户列表" />
    <van-search
      v-model="query"
      shape="round"
      background="#F5F7FA"
      placeholder="请输入搜索关键词"
      @clear="query = ''"
    />
    <van-cell-group>
      <van-cell
        class="total-title"
        :title="`共${users.length}位客户`"
        value=""
      />
    </van-cell-group>
    <van-index-bar>
      <van-index-anchor index="A">{{ " " }}</van-index-anchor>
      <van-cell
        value="内容"
        v-for="(user, idx) of list"
        :key="idx"
        class="user"
      >
        <!-- 使用 title 插槽来自定义标题 -->
        <template #title>
          <img :src="user.avatar" alt="" />
          <span class="title-name">{{ user.name }}</span>
          <span :class="['title-weixin', { qywx: user.type === 2 }]"
            >@{{ user.type === 1 ? "微信" : user.corpName || "企业微信" }}</span
          >
          <!-- <span class="title-sex">{{
            user.gender ? (user.gender === 1 ? "男" : "女") : "未知"
          }}</span> -->
        </template>
        <template #value>
          <van-button plain type="primary" size="mini" @click="sendToUser(user)" :class="user.status==='DELIVERED'?'DELIVERED':''"
            >{{user.status==='DELIVERED'?'再次发送':'立即发送'}}</van-button
          >
        </template>
      </van-cell>
    </van-index-bar>
  </div>
</template>

<script>
import { wecomPinia } from "../../pinia/";
export default {
  data() {
    return {
      users: [],
      query: "",
      pinia: null,
      msgContent: {}
    };
  },
  created() {
    this.pinia = wecomPinia();
    this.users =
      (this.pinia.detail.taskList &&
        this.pinia.detail.taskList[0] &&
        this.pinia.detail.taskList[0].externalUserId) ||
      [];

    //构建消息体
    this.msgContent = (
      (this.pinia.detail.attachments && this.pinia.detail.attachments) ||
      []
    ).map((msg) => {
      let itemContent={}
      itemContent.msgtype = msg.type;
      if (msg.type === "text") {
        itemContent.text={content:msg.text.content}
      }
      if (msg.type === "image") {
          itemContent.image = { mediaid: msg.image.mediaId };
      }
      return itemContent;
    });
  },
  computed: {
    list() {
      return this.users.filter((item) => {
        if (this.query) {
          return item.name.indexOf(this.query) !== -1;
        } else {
          return true;
        }
      });
    },
  },
  methods: {
    back() {
      this.$router.go(-1);
    },
    sendToUser(user) {
      let _self = this;
      wx.invoke("getContext", {}, function (res) {
        if (res.err_msg == "getContext:ok") {
          const entry = res.entry; //返回进入H5页面的入口类型，目前有normal、contact_profile、single_chat_tools、group_chat_tools、chat_attachment
          // const shareTicket = res.shareTicket; //可用于调用getShareInfo接口
          if (entry === "single_chat_tools") {
            _self.msgContent.forEach((content) => {
              wx.invoke("sendChatMessage", {
                msgtype: "text",
                enterChat: true,
                ...content,
              });
            });
          }
          if (entry === "normal") {
            wx.openEnterpriseChat({
              externalUserIds: user.externalUserId,
              success: (res) => {
                let chatId = res.chatId;
                // wx.invoke("sendChatMessage", {
                //   msgtype: "text",
                //   enterChat: true,
                //   ..._self.msgContent,
                // });
                // wx.invoke(
                //   "openExistedChatWithMsg",
                //   {
                //     chatId: chatId,
                //     msg: {
                //       msgtype: "text",
                //       text: {
                //         content: "title1",
                //       },
                //     },
                //   },
                //   function (res) {
                //     if (res.err_msg == "openExistedChatWithMsg:ok") {
                //       alert(JSON.stringify(res));
                //     }
                //   }
                // );
              },
              fail: (errObj) => {
                alert(JSON.stringify(errObj));
              },
            });
          }
        } else {
          //错误处理
        }
      });
    }
  },
};
</script>

<style lang="less" scoped>
.receiver-wrapper {
  :deep(.van-nav-bar__content) {
    background: #679bff;

    .van-nav-bar__title,
    .van-icon {
      color: #fff;
    }

    //
  }

  :deep(.van-search__content) {
    background: #fff;
  }

  .total-title {
    color: #999;
  }

  .user {
    img {
      width: 36px;
    }

    :deep(.van-cell__title) {
      display: flex;
      align-items: center;
      flex: auto;

      .title-name {
        display: inline-block;
        font-size: 16px;
        font-weight: bold;
        color: #333;
        min-width: 66px;
        padding-left: 10px;
      }

      .title-weixin {
        color: #57be6a;
        vertical-align: top;
        display: inline-block;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: 362px;
        padding-right: 12px;

        &.qywx {
          color: #ffb84d;
        }
      }

      .title-sex {
        padding-left: 12px;
        border-left: 1px solid #d8d8d8;
        color: #333;
        white-space: nowrap;
      }
    }

    :deep(.van-cell__value) {
      flex: auto;
      min-width: 70px;
      margin-right: 10px;
    }
    .DELIVERED{
      color: #999;
      border-color: #C7C7C7;
    }
  }
}
</style>
