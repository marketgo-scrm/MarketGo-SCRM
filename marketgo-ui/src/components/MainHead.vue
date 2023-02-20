<template>
  <div class="main-title">
    <div class="main-title-text">
      <el-button
        v-if="back"
        @click="goback()"
        size="mini"
        icon="el-icon-arrow-left"
        circle
      ></el-button>
      <span>{{ title }}</span>

      <el-button type="text" class="sub-title" v-if="subtitle" @click="send">{{ subtitle }}</el-button>
    </div>
    <div class="main-title-operation">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "UserInfo",
  props: {
    // 标题
    title: {
      type: String,
      default: "",
    },
      // 子标题
      subtitle: {
      type: String,
      default: "",
    },
    back: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {};
  },
  computed: {},
  created() {},
  methods: {
    goback() {
      history.go(-1);
    },
    send() {
        this.$emit('ListenSubtitle', '');
      }
  },
};
</script>
<style lang="scss" scoped>
.main-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: -10px;
  margin-bottom: 14px;
  height: 32px;
  line-height: 32px;
  .main-title-text {
    font-size: 20px;
    font-weight: bold;
    display: flex;
    align-items: center;
    .el-button {
      flex: none;
      margin-right: 6px;
    }
    .sub-title {
      margin-left: 10px;
       margin-top:3px;
       font-size: 12px;
       font-weight:normal;
    }
  }
  .main-title-operation {
    height: 32px;
  }
}
</style>