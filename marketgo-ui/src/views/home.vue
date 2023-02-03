<template>
  <div class="app-main">
    <div class="top">
      <div class="logon">
        <img src="../assets/imgs/Union.png" alt="" />
        MarketGo
      </div>
      <div class="navbar">
        <div class="nav">
          <div class="listone project">
            {{ this.$store.state.project[0]?.projectName }}
            <i class="el-icon-caret-bottom"> </i>
            <div class="menus">
              <div
                class="menulist"
                @click="gohome"
                v-for="(item, index) in $store.state.project"
                :key="index"
              >
                {{ item.projectName }}
              </div>
            </div>
          </div>
          <div class="listone" v-for="(item, index) in list" :key="index" @click="jumpUrl(item)">
            {{ item.name }}
          </div>
        </div>
        <div class="navbar-operation">
          <div class="vips" @click="jumpUrl({name:'升级'})">
            <span>
              升级至VIP尊享版
          </span>
            <img src="../assets/imgs/Group.png" alt="" />
          </div>
          <div class="navbar-operation-userinfo">
            <UserInfo></UserInfo>
          </div>
        </div>
      </div>
    </div>
    <div class="contlet">
      <div class="user">
        <img src="../assets/imgs/user.png" alt="" />
        <div class="usertext" @click="shows">
          <span class="title">{{ chosedata.corp.corpName }}</span>
          <span class="curs"
            >切换授权账号 <i class="el-icon-arrow-right"></i
          ></span>
        </div>
        <div class=""></div>
      </div>
      <div class="menuout">
        <el-scrollbar style="height: 100%"
          ><el-menu
            :default-active="defaultactive"
            class="el-menu-vertical-demo"
            @select="handleSelect"
            background-color="transparent"
            text-color="#fff"
            active-text-color="#fff"
          >
            <template v-for="item in constantRoutes">
              <template v-if="item.children">
                <el-submenu :index="item.name" :key="item.name">
                  <template slot="title">{{ item.title }}</template>
                  <el-menu-item
                    :index="val.name"
                    v-for="val in item.children"
                    :key="val.name"
                  >
                    {{ val.title }}
                  </el-menu-item>
                </el-submenu>
              </template>
              <template v-else>
                <el-menu-item :key="item.name" :index="item.name">
                  <span slot="title">{{ item.title }}</span>
                </el-menu-item>
              </template>
            </template>
          </el-menu>
        </el-scrollbar>
      </div>
    </div>
    <div class="conrrit">
      <el-scrollbar style="height: 100%">
        <div class="contin">
          <router-view></router-view>
        </div>
      </el-scrollbar>
    </div>
    <el-drawer
      title="切换授权企业微信账号"
      :visible.sync="drawer"
      direction="ltr"
      size="350px"
    >
      <div class="commonout">
        <div class="common" v-for="(item, index) in datalist" :key="index">
          <div>
            <span>{{ item.corp.corpName }}</span>
            <el-button size="mini" type="text" @click="goset(item)"
              >配置</el-button
            >
          </div>
          <span>当前账号</span>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
// import constantRoutes from "../router/menu";
import UserInfo from "@/components/UserInfo.vue";
import constants from '@/constants/constants.js'
export default {
  components: {
    UserInfo,
  },
  data() {
    return {
      constantRoutes: [],
      drawer: false,
      project_id: "",
      datalist: [],
      list: [
        // { name: "平台运营项目", path: "",type:1 },
        { name: "更新日志", path: "" },
        { name: "帮助文档", path: "" },
        { name: "价格", path: "" },
      ],
      defaultactive: "home",
      chosedata: {
        corp: {},
      },
    };
  },
  async mounted() {
    if (sessionStorage.getItem("defaultactive")) {
      this.defaultactive = sessionStorage.getItem("defaultactive");
    }
    this.project_id = this.$store.state.projectUuid;
    await this.gethttps();
    this.getmenu();
  },
  methods: {
    gohome() {
      sessionStorage.setItem("defaultactive", "home");
      this.defaultactive = "home";
      this.$router.push({
        name: "home",
      });
    },
    // 顶部导航栏 URL跳转
    jumpUrl(item) {
      if (item.name === '更新日志') {
        window.open(constants.UPDATE_LOG_URL, "_blank")
      }
      else if (item.name === '帮助文档') {
        window.open(constants.HELP_URL, "_blank")
      }
      else if (item.name === '价格') {
        window.open(constants.PRICE_URL, "_blank")
      }
      else if (item.name === '升级') {
        window.open(constants.PRICE_URL, "_blank")
      }
    },
    async getmenu() {
      let params = {
        corpId: this.$store.state.corpId,
        projectUuid: this.$store.state.projectUuid,
        tenantUuid: this.$store.state.tenantUuid,
      };
      let data = await this.$http.post("mktgo/wecom/user/role/info", params);
      if (data.code === 0) {
        let arr = [];
        arr = data.data.filter((item) => {
          return item.status;
        });
        arr.map((item) => {
          if (item.children.length > 0) {
            item.children = item.children.filter((val) => {
              return val.status;
            });
          } else {
            delete item.children;
          }
        });
        this.constantRoutes = arr;
      }
    },
    goset(item) {
      this.$router.push({
        name: "accountauthorization",
        params: {
          configure: item,
        },
      });
    },
    async gethttps() {
      let data = await this.$http.get(
        `mktgo/wecom/corp/config?project_id=${this.project_id}`
      );
      if (data.code === 0) {
        this.$store.commit("SET_CORPID", data.data.configs[0].corp.corpId);
        this.datalist = data.data.configs;
        this.chosedata = data.data.configs[0];
      }
    },
    shows() {
      this.drawer = true;
    },
    handleSelect(val) {
      sessionStorage.setItem("defaultactive", val);
      this.defaultactive = val;
      this.$router.push({
        name: val,
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.contlet {
  width: 206px;
  position: fixed;
  top: 50px;
  height: calc(100vh - 50px);
  background: #364066;
}
.el-drawer__wrapper{
  left: 206px;
}
.menuout {
  width: 100%;
  height: calc(100vh - 130px);
}
.conrrit {
  min-width: 700px;
  position: absolute;
  top: 50px;
  left: 206px;
  width: calc(100% - 206px);
  height: calc(100vh - 50px);
  background: #f0f2f6;
}
.contin {
  padding: 24px;
  width: 100%;
  /* height:100%; */
  box-sizing: border-box;
}
.user {
  width: 100%;
  height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 12px;
  border-bottom: 1px solid #535f80;
}
.user img {
  width: 46px;
  border-radius: 23px;
  margin-right: 5px;
}
.usertext {
  display: flex;
  height: 46px;
  flex-direction: column;
  justify-content: space-around;
}
.title {
  font-weight: 600;
  font-size: 14px;
}
::v-deep(.el-menu-item:hover),
::v-deep(.el-submenu__title:hover) {
  background: #4f618c !important;
}
::v-deep(.el-drawer__header) {
  font-size: 14px;
  font-weight: 600;
}
.commonout {
  width: 100%;
  padding: 0 24px;
  box-sizing: border-box;
}
.common {
  width: 100%;
  height: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #eff3fc;
  font-size: 12px;
  padding: 0 10px;
  box-sizing: border-box;
}
.common span {
  margin-right: 10px;
}
.top {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 50px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.07);
}
.logon {
  width: 206px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  float: left;
}
.logon img {
  width: 26px;
  margin-right: 12px;
}
.navbar {
  font-size: 12px;
  width: calc(100% - 206px);
  display: flex;
  height: 50px;
  justify-content: space-between;
  align-items: center;
  padding: 0 28px;
  box-sizing: border-box;
}
.nav {
  width: 400px;
  display: flex;
}
.listone {
  padding: 0 6px;
  height: 50px;
  line-height: 50px;
  margin-left: 20px;
  box-sizing: border-box;
  position: relative;
  cursor: pointer;
}
.navbar-operation {
  display: flex;
  align-items: center;
  .vips {
    width: 160px;
    height: 34px;
    border-radius: 17px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-image: linear-gradient(to right, #fd9d65, #ef65c9);
    font-size: 15px;
    color: #fff;
    img {
      width: 14px;
      margin-left: 6px;
    }
  }
  .navbar-operation-userinfo {
    margin-left: 20px;
  }
}

::v-deep(.el-scrollbar__view) {
  height: 100%;
}
::v-deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}
::v-deep(.el-submenu__title) {
  height: 40px;
  font-size: 12px;
  line-height: 40px;
}
::v-deep(.el-menu-item) {
  height: 40px;
  font-size: 12px;
  line-height: 40px;
}
::v-deep(.el-menu .el-menu) {
  background: #232f57 !important;
}
/* /deep/ .el-submenu__title,/deep/ .el-menu-item{
     margin-bottom:8px;
  } */
::v-deep(.el-menu .el-menu-item.is-active) {
  background: #4f618c !important;
  font-weight: 600;
}
::v-deep(.el-scrollbar__wrap) {
  margin-right: -18px !important;
}
.curs {
  cursor: pointer;
}
.project {
  position: relative;
}
.menus {
  width: 208px;
  padding: 8px 0;
  box-sizing: border-box;
  background: #fff;
  display: none;
  box-shadow: 0px 3px 9px rgba(0, 0, 0, 0.26);
  border-radius: 4px;
  position: absolute;
  top: 40px;
  left: 0;
  z-index: 55;
}
.project:hover .menus {
  display: block;
}
.menulist {
  width: 208px;
  height: 30px;
  text-indent: 10px;
  background: #eff3fd;
  font-size: 12px;
  line-height: 30px;
  color: #333333;
  cursor: pointer;
}
</style>