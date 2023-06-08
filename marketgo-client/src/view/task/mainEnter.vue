<template>
  <div class="main-wrapper">
    <taskList v-if="tabActive === 'task'"></taskList>
  </div>
</template>

<script>
import "vant/es/toast/style";
import taskList from "./list.vue";
export default {
  components: { taskList },
  data() {
    return {
      tabActive: "task",
      activeType: "chat",
    };
  },
  created() { },

  methods: {
    test() {
      wx.checkJsApi({
        jsApiList: ['shareToExternalMoments'], // 需要检测的JS接口列表
        success: function (res) {
          alert(JSON.stringify(res))
        }
      });
      try {
        wx.invoke(
          "shareToExternalMoments",
          {
            text: {
              content: "dddddd", // 文本内容
            },
          },
          function (res) {
            console.log(res, "shareToExternalMoments");
            if (res.err_msg == "shareToExternalMoments:ok") {
            }
            alert(JSON.stringify(res)+'res')
          }
        );
      } catch (_) {
        alert('catch'+JSON.stringify(_) + 'catch')
      }
    },
    group() {
      wx.invoke(
        "shareToExternalContact",
        {
          text: {
            content: "群发到客户", // 文本内容
          },
        },
        function (res) {
          console.log(res, "shareToExternalContact");
          if (res.err_msg == "shareToExternalContact:ok") {
          }
        }
      );
    },
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

  :deep(.van-overlay) {
    z-index: 2;
  }
}
</style>
