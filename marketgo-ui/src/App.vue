<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "App",
  components: {},
  data() {
    return {};
  },
  created() {
    document.documentElement.style.setProperty("--el-color-primary", "#6E94F5");
    if (sessionStorage.getItem("store")) {
      this.$store.replaceState(
        Object.assign(
          {},
          this.$store.state,
          JSON.parse(sessionStorage.getItem("store"))
        )
      );
      sessionStorage.removeItem("store");
    }
    window.addEventListener("beforeunload", () => {
      sessionStorage.setItem("store", JSON.stringify(this.$store.state));
    });
  },
};
</script>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
}
.one-line {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.two-line {
  text-overflow: -o-ellipsis-lastline;
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.three-line {
  text-overflow: -o-ellipsis-lastline;
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
}
.inline {
  display: inline-block;
}
.content-title {
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 600;
  font-size: 14px;
  line-height: 100%;
  color: #333333;
  margin-left: 12px;
  margin-bottom: 16px;
}
.el-message-box__wrapper {
  .el-message-box {
    padding: 0 20px 20px;
    .el-message-box__headerbtn {
      display: none;
    }
    .el-message-box__message {
      color: #000000;
      font-family: PingFang SC;
      font-size: 14px;
      line-height: 20px;
      display: flex;
      align-items: center;
      &::before{
        content: '!';
        background-color: #FE8106;
        width: 46px;
        height: 46px;
        border-radius: 50%;
        font-size: 32px;
        line-height: 46px;
        text-align: center;
        flex: none;
        color: #fff;
        font-weight: bold;
        margin-right: 16px;
      }
    }
    .el-message-box__btns{
      text-align: center;
      padding-top: 24px;
      .el-button{
        border-radius: 40px;
        padding-left: 32px;
        padding-right: 32px;
      }
      .el-button--default{
        border-color: #f6f6f6;
        background-color: #f6f6f6;
      }
      .el-button--primary {
        background-color: #679BFF;
      }
    }
  }
}
</style>
