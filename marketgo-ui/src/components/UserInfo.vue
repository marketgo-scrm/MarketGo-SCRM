<template>
  <div v-if="user.userName">
    <el-popover placement="top-start" width="216" trigger="hover">
      <div class="usermenu">
        <div class="usermenu-info">
          <el-avatar slot="reference" :size="32" :src="avatar"></el-avatar>
          <div class="usermenu-info-content">
            <div class="usermenu-info-content-name">
              {{ user.userName }}
            </div>
            <div
              class="usermenu-info-content-identity"
              v-if="user.userName === 'admin'"
            >
              超级管理员
            </div>
          </div>
        </div>
        <div class="usermenu-list">
          <div class="usermenu-list-item" @click="dialogVisible = true">
            修改密码
          </div>
          <div class="usermenu-list-item" @click="$utils.logout()">
            退出账号
          </div>
        </div>
      </div>
      <el-avatar slot="reference" :size="32" :src="avatar"></el-avatar>
    </el-popover>
    <el-dialog
      title="修改密码"
      :visible.sync="dialogVisible"
      width="400px"
      :before-close="handleClose"
    >
      <el-form ref="ruleForm" :model="passData" :rules="rules" label-width="80px" @submit.prevent.native>
        <el-form-item label="旧密码" prop="passWord">
          <el-input v-model="passData.passWord"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="changePassWord">
          <el-input v-model="passData.changePassWord"></el-input>
        </el-form-item>
        <el-form-item label="确定密码" prop="recChangePassWord">
          <el-input v-model="passData.recChangePassWord"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="changePassWord()"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import avatar from "@/assets/avter.png";
import { mapState } from "vuex";

export default {
  name: "UserInfo",
  props: {
    variables: {
      type: Array,
      default: () => [],
    },
    // 行业列表
    dashboardId: {
      type: String,
      default: "",
    },
    // 路由path
    path: {
      type: String,
      default: "",
    },
  },
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'));
      } else if (value.length > 11 || value.length < 6) {
        callback(new Error('长度在 6 到 11 个字符'));
      } else if (value !== this.passData.changePassWord) {
        callback(new Error('两次输入新密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      // 默认头像
      avatar,
      // 修改密码
      passData: {
        changePassWord: "",
        passWord: "",
        recChangePassWord: "",
        userName: "",
      },
      dialogVisible: false,
      // 修改密码校验
      rules: {
        passWord: [
          { required: true, message: '请输入旧密码', trigger: 'blur' },
          { min: 6, max: 11, message: '长度在 6 到 11 个字符', trigger: 'blur' }
        ],
        changePassWord: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 11, message: '长度在 6 到 11 个字符', trigger: 'blur' }
        ],
        recChangePassWord: [
          { validator: validatePass, trigger: 'blur' }
        ]
      },
    };
  },
  computed: {
    ...mapState({
      user: (state) => state.user,
    }),
  },
  created() {},
  methods: {
    // 修改密码
    changePassWord() {
      this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            this.passData.userName = this.user.userName
            this.$api
            .changePassWord(this.passData)
            .then((res) => {
              console.log(res);
              if (res.code === 0) {
                this.$message.success('修改成功')
                this.handleClose()
              } else {
                this.$message.error(res.message)
              }
            })
            .catch((err) => {
              console.log(err);
            });
          } else {
            console.log('error submit!!');
            return false;
          }
        });
    },
    // 清除密码文本
    handleClose() {
      this.passData = {
        changePassWord: "",
        passWord: "",
        recChangePassWord: "",
        userName: "",
      };
      this.dialogVisible = false
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep(.el-popper) {
  padding: 0 !important;
}
.usermenu {
  .usermenu-info {
    display: flex;
    align-items: center;
    border-bottom: #ebebeb 1px solid;
    margin: 0 -12px;
    padding: 0 17px 12px;
    .el-avatar {
      margin-right: 17px;
    }
    .usermenu-info-content {
      .usermenu-info-content-name {
        font-size: 14px;
        font-weight: bold;
      }
      .usermenu-info-content-identity {
        font-size: 12px;
        color: #999999;
      }
    }
  }
  .usermenu-list {
    margin: 8px -12px 0;
    .usermenu-list-item {
      padding: 8px 18px;
      cursor: pointer;
      &:hover {
        background: #eaf1ff;
      }
    }
  }
}
</style>