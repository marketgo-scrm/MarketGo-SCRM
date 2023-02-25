<template>
  <div class="myout">
    <div class="mypro">
      <div class="logon">
        <img src="../assets/imgs/Union.png" alt="" />
        MarketGo
      </div>
      <div class="listout">
        <h3>我的项目</h3>
        <p>选择一个项目，查看并开启它的管理页面</p>
        <div class="listin">

          <div class="create" v-if= "this.canCreate" @click="showDialog()">

            <img src="../assets/create_progect.png" alt="" />
            <span>添加一个新项目</span>
          </div>
          <div class="list" v-for="item in projects" :key="item.projectUuid">
            <h4>{{ item.projectName }}</h4>
            <p>
              {{ item.desc }}
            </p>
            <span>类型：{{ item.type }}</span>
            <span>状态：{{ item.status == 'publish' ? '已发布' : "未发布" }}</span>
            <div class="timeout">
              <span>创建时间：{{ item.createTime }}</span>
              <el-button type="primary" round size="small" :loading='loading' @click="gopro(item)">进入项目</el-button>
            </div>
          </div>
          <div class="list vips">
            <h4>升级至VIP尊享版</h4>
            <p>可以创建更多类型的项目</p>
            <p>当前版本最多创建2个</p>
            <div class="btns">立即升级</div>
          </div>
        </div>
      </div>
    </div>
    <div class="imgbom">
      <div class='imgin'>
        <div class="imgs">
          <p>帮助文档</p>
          <img src="../assets/word.png" alt="" />
        </div>
        <div>
          <p>账号授权</p>
          <img src="../assets/power.png" alt="" />
        </div>
      </div>
    </div>
    <el-dialog title="创建项目" :visible.sync="dialogVisible" width="400px" :before-close="handleClose">

      <el-input placeholder="请填写项目名称" style="margin-top: -10px;" v-model="prejectData.name" @blur="checkProjectName()"
        maxlength="30"></el-input>
      <el-input placeholder="请填写项目描述" type="textarea" style="margin-top: 20px;" :rows="8"
        v-model="prejectData.desc"></el-input>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelCreated" class='btns_created' round size="small">取 消</el-button>
        <el-button type="primary" size="small" round class='btns_created' @click="createProject()">确 定</el-button>
      </div>

    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      projects: [],
      loading: false,
      // 显示创建项目的弹框
      dialogVisible: false,
      // 创建项目所需要的数据
      prejectData: {
        desc: "",
        name: "",
      },
      // 是否有权限创建新的项目
      canCreate:false
    };
  },
  mounted() {
    this.getlist();
  },
  methods: {
    showDialog() {
      // alert(3)
      this.dialogVisible = true
    },
    // 校验项目名称是否存在
    checkProjectName() {
      this.$http.get(
        `/mktgo/wecom/project/check_name?name=${this.prejectData.name}`,
        {}).then(function (res) {
          console.log(res);
          if (res.code !== 0) {
            this.$message.error(res.message);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    //新建一个项目
    createProject() {
      if (!this.prejectData.name || this.prejectData.name.trim().length == 0) {
        this.$message.info('请填写项目名称')
        return;
      }
      if (!this.prejectData.desc || this.prejectData.desc.trim().length == 0) {
        this.$message.info('请填写项目描述')
        return;
      }
      let _this = this
      this.$http.post(
        `mktgo/wecom/project/create?name=${this.prejectData.name}&desc=${this.prejectData.desc}`,
        {}).then(function (res) {
          console.log(res)
          if (res.code == 0) {
            _this.$message({
              message: '项目创建成功',
              type: 'success'
            });
            this.dialogVisible = false
            this.getlist();
          }
          else {
            this.$message.error(res.message);
          }
        });
    },
    // 取消创建
    cancelCreated() {
      this.dialogVisible = false
    },
    // 获取项目列表
    async getlist() {
      let data = await this.$http.post("mktgo/wecom/project/list", {});
      if (data.code === 0) {
        this.projects = data.data.projects;
        this.canCreate  = data.data.canCreate || false;
        this.$store.commit('SET_TENANTUUID', data.data.tenantUuid)
        this.$store.commit('SET_PROJECT', data.data.projects)
      }
    },
    async gopro(val) {
      this.$store.commit("SET_PROID", val.projectUuid);
      this.loading = true
      let data = await this.$http.get(
        `mktgo/wecom/corp/config?project_id=${val.projectUuid}`
      );
      if (data.code === 0) {
        if (data.data) {
          this.$store.commit("SET_CORPID", data.data.configs[0].corp.corpId);
          this.$router.push({
            path: "/index",
          });
        } else {
          this.$router.push({
            path: "/accountauthorization",
          });
        }
      }
      this.loading = false
    },
  },
};
</script>
<style scoped>
.myout {
  width: 100%;
  height: 100vh;
  min-width: 1125px;
  background: #f5f8ff;
}

.mypro {
  width: 100%;
  min-height: 300px;
  background: url("../assets/imgs/peoback.png") no-repeat;
  background-size: 100%;
  position: relative;
}

.logon {
  width: 100%;
  height: 70px;
  display: flex;
  align-items: center;
  color: #fff;
  padding: 0 24px;
  box-sizing: border-box;
}

.logon img {
  width: 26px;
  margin-right: 12px;
}

.listout {
  width: 100%;
  padding: 0 80px;
  /* position: absolute;
  top: 130px;
  left: 0; */
  box-sizing: border-box;
  margin-top: 40px;
}

.imgbom {
  /* position:fixed;
    left:0;
    bottom:20px; */
  width: 100%;
  padding: 0 80px;
  box-sizing: border-box;
  margin-top: 70px;
  /* height:18vh */

  /* justify-content: center; */
}

.imgin {
  width: 90%;
  /* margin:0 auto; */
  display: flex;
}

.imgbom p {
  position: absolute;
  left: 20px;
  top: 20px;
  font-weight: 600;
  font-size: 14px;
  line-height: 20px;


  color: #999999;
}

.imgin div {
  position: relative;
  width: 75%;
  /* height:190px; */
}

.imgbom .imgs {
  width: 24%;
  margin-right: 1%;
  /* float: left; */
}

.imgbom img {
  width: 100%;
  /* height:100%; */
}

.listout>h3 {
  font-weight: 600;
  font-size: 24px;
  line-height: 34px;
  color: #ffffff;
}

.listout>p {
  font-weight: 400;
  font-size: 12px;
  line-height: 17px;
  color: #ffffff;
  margin: 16px 0;
}

.listin {
  width: 90%;
  /* margin:0 auto; */
  display: flex;
  flex-wrap: wrap;
  /* justify-content: center; */
}

.create {
  justify-content: center;
  align-items: center;


  width: 24%;
  height: 190px;
  background: #fff;
  display: flex;
  flex-direction: column;
  /* justify-content: space-between; */
  padding: 12px 20px;
  box-sizing: border-box;
  font-size: 12px;
  border-radius: 10px;
  box-shadow: 0px 5px 24px rgba(0, 0, 0, 0.07);
  margin-right: 1%;
  margin-bottom: 20px;
}

.create img {
  width: 36px;
  height: 36px;
}

.create span {
  margin-top: 17px;
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 12px;
  line-height: 17px;
  color: #6E94F5;
}

.list {
  width: 24%;
  height: 190px;
  background: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 12px 20px;
  box-sizing: border-box;
  font-size: 12px;
  border-radius: 10px;
  box-shadow: 0px 5px 24px rgba(0, 0, 0, 0.07);
  margin-right: 1%;
  margin-bottom: 20px;
}

.timeout {
  display: flex;
  justify-content: space-between;
}

.vips {
  background: linear-gradient(158.17deg, #ffa654 11.03%, #eb55e5 118.34%);
  box-shadow: 0px 5px 24px rgba(148, 7, 142, 0.19);
  color: #fff;
}

.vips h4 {
  font-weight: 600;
  font-size: 30px;
  color: #ffffff;
}

.btns {
  width: 152px;
  height: 40px;
  background: linear-gradient(180deg, #ffde30 0%, #ffb628 100%);
  border-radius: 44px;
  font-weight: 600;
  font-size: 16px;
  line-height: 40px;
  text-align: center;
  color: #ffffff;
}

.btns_created {
  width: 96px;
}</style>