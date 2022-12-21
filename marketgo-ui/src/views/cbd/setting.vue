<template>
  <div class="cdp-setting-wrapper">
    <main-head :title="`${cbdName}数据接入`" :back="true"></main-head>
    <div class="content">
      <el-form
        :model="formData"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        label-position="left"
      >
        <el-form-item label="API Secret" prop="apiSecret">
          <el-input v-model="formData.apiSecret"></el-input>
        </el-form-item>
        <el-form-item label="系统部署地址" prop="apiUrl">
          <el-input v-model="formData.apiUrl"></el-input>
        </el-form-item>
        <el-form-item label="数据上报地址" prop="dataUrl">
          <el-input v-model="formData.dataUrl"></el-input>
        </el-form-item>
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="formData.projectName"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button size="small" style="width: 162px" round @click="unbind"
            >解除绑定</el-button
          >
          <el-button
            size="small"
            :icon="
              isTest
                ? testFlag 
                  ? 'el-icon-success'
                  : 'el-icon-warning'
                : ''
            "
            :loading="loading"
            style="width: 162px;margin-left:8px;padding: 9px 0px;"
            round
            @click="testbind('ruleForm')"
            >{{              isTest
                ? testFlag 
                  ? '测试成功'
                  : '测试失败，请重新测试！'
                : '绑定测试'}}</el-button
          >
          <el-button
            size="small"
            style="width: 100px;margin-left:8px;"
            type="primary"
            round
            @click="bind('ruleForm')"
            >绑 定</el-button
          >
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "cdpSetting",
  data: () => {
    return {
      cbdName: "",
      cbdType: "",
      formData: {
        apiSecret: "",
        apiUrl: "",
        appKey: "",
        dataUrl: "",
        projectName: "",
      },
      rules: {
        apiSecret: [{ required: true, message: "请输入", trigger: "blur" }],
        apiUrl: [
          { required: true, message: "请输入", trigger: "blur" },
          {
            type: "url",
            required: true,
            message: "请输入正确的地址",
            trigger: "blur",
          },
        ],
        dataUrl: [
          { required: true, message: "请输入", trigger: "blur" },
          {
            type: "url",
            required: true,
            message: "请输入正确的地址",
            trigger: "blur",
          },
        ],
        projectName: [{ required: true, message: "请输入", trigger: "blur" }],
      },
      loading: false,
      isTest: false,
      testFlag: false,
    };
  },
  created() {
    if (this.$route.query.type) {
      this.cbdType = this.$route.query.type;
      this.getDetail(this.$route.query.type);
    }
    this.cbdName = this.$route.query?.name;
  },
  methods: {
    getDetail(cdp_type) {
      this.$http
        .get(
          `/mktgo/wecom/cdp/query?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}
          &cdp_type=${cdp_type}`
        )
        .then((res) => {
          this.formData = res.data;
        });
    },
    bind(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let params = {
            ...this.formData,
          };
          this.$http
            .post(
              `/mktgo/wecom/cdp/save?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}
          &cdp_type=${this.cbdType}`,
              params
            )
            .then((res) => {
              if (res.code == 0 && res.message == "success") {
                this.$message({
                  message: "绑定成功",
                  type: "success",
                });
              } else {
                this.$message({
                  message: res.message,
                  type: "warning",
                });
              }
            })
            .catch((erro) => {
              this.$message({
                message: erro.message,
                type: "warning",
              });
            });
        } else {
          return false;
        }
      });
    },
    unbind() {
      this.loading = true;
      this.$http
        .post(
          `/mktgo/wecom/cdp/delete?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}
          &cdp_type=${this.cbdType}`
        )
        .then((res) => {
          this.loading = false;
          if (res.code == 0 && res.message == "success") {
            this.formData = {
              apiSecret: "",
              apiUrl: "",
              appKey: "",
              dataUrl: "",
              projectName: "",
            };
            this.$message({
              message: "解绑成功",
              type: "success",
            });
          } else {
            this.$message({
              message: res.message,
              type: "warning",
            });
          }
        })
        .catch((erro) => {
          this.loading = false;
          this.$message({
            message: erro.message,
            type: "warning",
          });
        });
    },
    testbind(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let params = {
            ...this.formData,
          };
          this.loading = true;
          this.isTest = true;
          this.$http
            .post(
              `/mktgo/wecom/cdp/test?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}
          &cdp_type=${this.cbdType}`,
              params
            )
            .then((res) => {
              this.loading = false;
              if (res.code == 0 && res.message == "success") {
                this.testFlag = true;
                this.$message({
                  message: "测试成功",
                  type: "success",
                });
              } else {
                this.testFlag = false;
                this.$message({
                  message: res.message,
                  type: "warning",
                });
              }
            })
            .catch((erro) => {
              this.loading = false;
              this.testFlag = false;
              this.$message({
                message: erro.message,
                type: "warning",
              });
            });
        } else {
          return false;
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.cdp-setting-wrapper {
  height: calc(100vh - 98px);
  .content {
    height: calc(100% - 70px);
    padding: 32px 64px 32px 28px;
    background: #fff;
    ::v-deep(.el-form-item__content) {
      width: 440px;
    }
    ::v-deep(.el-icon-success) {
      font-size: 14px;
      color: #67c23a;
    }
    ::v-deep(.el-icon-warning) {
      font-size: 14px;
      color: #e6a23c;
    }
  }
}
</style>
