<template>
  <div class="main-wrapper">
    <div @click="sendChatMessage" v-show="false">发送消息</div>
    <van-cell-group style="background: transparent">


      <taskList></taskList>


    </van-cell-group>
  </div>
</template>

<script>
import "vant/es/toast/style";
import taskList from "./sidebar.vue";
export default {
  components: { taskList },
  data() {
    return {
      activeType: "todo",
    };
  },
  created() { },

  methods: {
    sendChatMessage() {
      wx.invoke("sendChatMessage", {
        msgtype: "text",
        text: {
          content: "你好",
        },
      });
    }
  },
  computed: {},
};
</script>

<style lang="less" scoped>
.main-wrapper {
  font-family: "PingFang SC";
  background: #f5f7fa; //#D8D8D8;
  color: #333333;
  margin-bottom: 100px;

  :deep(.van-tabs__line) {
    width: 35%;
  }

  :deep(.van-tab--active) {
    color: #679bff;
  }

  :deep(.van-nav-bar__content) {
    background: #679bff;

    .van-nav-bar__title,
    .van-icon {
      color: #fff;
    }
  }
}
</style>
