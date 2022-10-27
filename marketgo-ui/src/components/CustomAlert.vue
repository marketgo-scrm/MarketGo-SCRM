<template>
  <div class="custom-alert" :style="{background: background,color: color}" v-if="text">
    {{text}}
  </div>
</template>

<script>
export default {
  name: "CustomAlert",
  props: {
    // 标题
    text: {
      type: String,
      default: "",
    },
    // 背景
    background: {
      type: String,
      default: "#f2f9ff",
    },
    // 字色
    color: {
      type: String,
      default: "#8b9fbd",
    },
  },
  data() {
    return {};
  },
  methods: {
    goback() {
      this.$router.go(-1);
    },
  },
};
</script>
<style lang="scss" scoped>
.custom-alert {
  padding: 8px;
  font-family: PingFang SC;
  font-size: 12px;
  line-height: 24px;
  margin-top: 10px;
}
</style>