<template>
  <div class="content">
    <div class="contin">
      <img src="../assets/imgs/bagrod.png" alt="" />
      <div class="loginout">
        <div class="logon">
          <img src="../assets/imgs/Union.png" alt="" />
          <div class="logontext">
            登录
            <span>MarketGo</span>
          </div>
        </div>
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="ruleForm"
          size="small"
          label-width="0"
        >
          <el-form-item label="" prop="userName">
            <el-input @keyup.enter.native="login" v-model="ruleForm.userName" clearable>
              <template slot="prepend">账号</template>
            </el-input>
          </el-form-item>
          <el-form-item label="" prop="passWord">
            <el-input @keyup.enter.native="login" type="password" v-model="ruleForm.passWord" clearable>
              <template slot="prepend">密码</template>
            </el-input>
          </el-form-item>
        </el-form>
        <div class="forget">
          <el-button type="text" size="mini">忘记密码，请联系管理员</el-button>
        </div>
        <div class="btns" @click="login">登录</div>
        <div class="texts">
          <el-button type="text" size="mini"
            >没有账号，请联系管理员开通</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      ruleForm: {},
      rules: {
        userName: [{ required: true, message: "请输入账号", trigger: "blur" }],
        passWord: [{ required: true, message: "请输入密码", trigger: "blur" }],
      },
    };
  },
  methods: {
    login() {
      this.$refs.ruleForm.validate(async (valid) => {
        if (valid) {
          this.$api
            .login(this.ruleForm)
            .then((res) => {
              console.log(res);
              if (res.code === 0) {
                this.$store.commit("SET_USER", res.data);
                localStorage.setItem("token", res.data.token);
                sessionStorage.setItem("defaultactive", "home");
                this.$router.push({
                  name: "index",
                });
              } else {
                this.$message.error(res.message)
              }
            })
            .catch((err) => {
              console.log(err);
            });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
  },
};
</script>
<style scoped>
.content {
  width: 100%;
  height: 100vh;
  background: #d4e0ff;
  /* background:url('../assets/imgs/bagrod.png') no-repeat; */
  /* background-size: 100% 100%; */
}
.contin {
  width: 80%;
  height: 100%;
  margin: 0 auto;
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.contin > img {
  width: 42%;
}
.loginout {
  /* position: absolute; */
  padding: 40px 20px;
  box-sizing: border-box;
  background: #fff;
  width: 380px;
  height: 450px;
  /* right: 260px;
top: 50%;
margin-top:-225px; */
}
.logon {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.logon img {
  height: 40px;
  margin-right: 12px;
}
.logontext {
  height: 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  font-size: 12px;
}
.logontext span {
  font-size: 23px;
  font-weight: 600;
}
::v-deep(.el-input-group__prepend) {
  background: #fff !important;
  border-radius: 2px 0 0 2px;
}
::v-deep(.el-input-group > .el-input__inner) {
  border-radius: 0 2px 2px 0;
}
.forget {
  text-align: right;
  margin-bottom: 80px;
}
.texts {
  text-align: center;
}
.btns {
  width: 100%;
  height: 46px;
  background: #435ea0;
  color: #fff;
  text-align: center;
  line-height: 46px;
  border-radius: 23px;
  margin-bottom: 32px;
  cursor: pointer;
}
::v-deep(.el-form-item) {
  margin-bottom: 24px;
}
</style>