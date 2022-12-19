<template>
  <div class="callback-wrapper">
    <div class="tops">
      <h2>回调配置</h2>
    </div>
    <div class="content">
      <el-tabs v-model="activeName">
        <el-tab-pane label="通讯录" name="CONTACTS"></el-tab-pane>
        <el-tab-pane label="客户" name="EXTERNAL_USER "></el-tab-pane>
      </el-tabs>
      <div class="title">回调地址</div>
      <div class="section-one">
        <el-form :model="callbackConfig" ref="ruleForm" label-width="120px">
          <el-form-item label="URL" :required="true">
            <el-input readonly v-model="callbackConfig.callbackUrl"></el-input>
            <el-button
              style="margin-left: 20px"
              type="primary"
              size="small"
              round
              class="tag"
              @click="copy(callbackConfig.callbackUrl)"
              >复制</el-button
            >
          </el-form-item>
          <el-form-item label="Token" :required="true">
            <el-input readonly v-model="callbackConfig.token"></el-input
            ><el-button
              style="margin-left: 20px"
              type="primary"
              size="small"
              round
              class="tag"
              @click="copy(callbackConfig.token)"
              >复制</el-button
            >
          </el-form-item>
          <el-form-item label="EncodingAesKey" :required="true">
            <el-input
              readonly
              v-model="callbackConfig.encodingAesKey"
            ></el-input
            ><el-button
              style="margin-left: 20px"
              type="primary"
              size="small"
              round
              class="tag"
              @click="copy(callbackConfig.encodingAesKey)"
              >复制</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <div class="title">转发地址</div>
      <div>
        <el-form :model="forwardServer" ref="cbform" label-width="0px">
          <el-form-item
            label=""
            :required="true"
            v-for="(item, idx) of forwardServer.list"
            :key="idx + item"
            :prop="'list.' + idx"
            :rules="{
              required: true,
              message: '不能为空',
              trigger: 'blur',
            }"
          >
            <el-input
              :ref="idx"
              style="width: 554px"
              :readonly="!forwardServerState[idx]"
              v-model="forwardServer.list[idx]"
            ></el-input>
            <template v-if="!forwardServerState[idx]">
              <el-button
                style="margin-left: 5px"
                type="text"
                size="small"
                round
                @click="edit(idx)"
                >编辑</el-button
              >|
              <el-button
                style="margin-left: 0px"
                type="text"
                size="small"
                round
                @click="del(idx)"
                >删除</el-button
              >
            </template>
            <el-button
              style="margin-left: 20px"
              type="primary"
              size="small"
              round
              @click="save(idx)"
              v-show="forwardServerState[idx]"
              >保存</el-button
            >
          </el-form-item>
        </el-form>
        <div>
          <el-button
            type="text"
            icon="el-icon-plus"
            @click="add"
            :disabled="disableAdd"
            >添加</el-button
          >
        </div>
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
      activeName: "CONTACTS",
      callbackConfig: {},
      forwardServer: { list: [] },
      forwardServerState: [],
    };
  },
  created() {},
  computed: {
    disableAdd() {
      return this.forwardServerState.some((item) => item);
    },
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
    add() {
      this.forwardServer.list.push("");
      this.forwardServerState.push(true);
    },
    del(idx) {
      this.forwardServer.list.splice(idx, 1);
      this.forwardServerState.splice(idx, 1);
    },
    edit(idx) {
      this.$set(this.forwardServerState, idx, true);
      this.$nextTick(() => {
        this.$refs[idx][0].focus();
      });
    },
    save(idx) {
      const params = {
        forwardServer: this.forwardServer.list,
      };
      this.$http
        .post(
          `/mktgo/wecom/corp/forward/save?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&config_type=${this.activeName}`,
          params
        )
        .then((res) => {
          if (res.code === 0) {
            this.$set(this.forwardServerState, idx, false);
            this.$message({
              message: "保存成功",
              type: "success",
            });
          } else {
            this.$message.error(res.message);
          }
        });
    },
    getCallback() {
      this.$http
        .get(
          `/mktgo/wecom/corp/callback/config?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&config_type=${this.activeName}`,
          {}
        )
        .then((res) => {
          this.callbackConfig = res.data;
        });
    },
    getForwardConfig() {
      this.$http
        .get(
          `/mktgo/wecom/corp/forward/config?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&config_type=${this.activeName}`,
          {}
        )
        .then((res) => {
          this.forwardServer.list = res.data.forwardServer;
          this.forwardServerState = new Array(
            this.forwardServer.list.length
          ).fill(false);
        });
    },
  },
  watch: {
    activeName: {
      immediate: true,
      handler: function () {
        this.getCallback();
        this.getForwardConfig();
      },
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
    color: #999999;
    font-size: 12px;
    h2 {
      color: #333;
      font-size: 20px;
      margin-bottom: 10px;
    }
  }
  .content {
    height: calc(100% - 70px);
    background: #fff;
    border-radius: 10px;
    padding: 30px 20px;
    .title {
      font-weight: 600;
      font-size: 14px;
      line-height: 20px;
      color: #333333;
      padding: 15px 0;
    }
    .section-one {
      width: 720px;
      padding: 30px 0 15px 0;
      background-color: #e6e6e6;
      ::v-deep(.el-input) {
        width: 430px;
      }
    }
  }
}
</style>
