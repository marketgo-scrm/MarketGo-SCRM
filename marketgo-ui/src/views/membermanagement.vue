<template>
  <div class="contout">
    <div class="tops">
      <h2>成员管理</h2>
      <!-- <span>添加成员，并设置角色与权限</span> -->
    </div>
    <div class="left">
      <el-scrollbar style="height: 100%">
        <div class='leftin'>
            <el-input v-model="searchkey"  prefix-icon="el-icon-search" placeholder="可搜索部门、成员标签" @change="chakey" size="small"></el-input>
        <div class="adds" @click="addmember">
          <i class="el-icon-circle-plus-outline"></i>
          添加成员
        </div>
        <el-tree
          ref="tree"
          default-expand-all
          highlight-current
          :expand-on-click-node="false"
          :data="treedata"
          node-key="id"
          :props="defaultProps"
          @node-click="handleNodeClick"
          :filter-node-method="filterNode"
        >
          <span slot-scope="{ node, data }">
            <span class="treelabel">
              <!-- <i class="el-icon-bank-card iconcor"></i> -->
              <template v-if='!node.isLeaf&&node.expanded'>
                 <img src="../assets/fileopen.png" alt="">
              </template>
              <template v-else>
                   <img src="../assets/file.png" alt="">
              </template>
              {{ data.name }}</span
            >
          </span>
        </el-tree>
        </div>
      </el-scrollbar>
    </div>
    <div class="right">
      <el-scrollbar style="height: 100%">
        <div class="searchtop">
          <h3>{{ this.chosenode.name }}</h3>
          <div class="search">
            <el-row>
              <!-- <el-col :span="1"> -->
                <span class="texts">角色：</span>
              <!-- </el-col> -->
              <el-col :span="10">
                <el-select
                  v-model="queryParams.roleCode"
                  style="width: 100%"
                  size="small"
                  @change="getList"
                  placeholder="请选择"
                >
                  <el-option label="全部" value=""> </el-option>
                  <el-option
                    v-for="item in options"
                    :key="item.code"
                    :label="item.desc"
                    :value="item.code"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="10" :offset="1">
                <el-input
                  @change="getList"
                  size="small"
                  placeholder="请输入内容"
                  prefix-icon="el-icon-search"
                  v-model="queryParams.searchMemberName"
                  @keyup.enter.native="getList"
                >
                </el-input>
              </el-col>
            </el-row>
          </div>
        </div>
        <el-table :data="tableData" style="width: 100%">
          <el-table-column prop="date" label="成员列表" width="180">
            <template slot-scope="scope">
              <div class="user">
                <img :src="scope.row.thumbAvatar?scope.row.thumbAvatar:require('../assets/avter.png')" alt="" />
                <div class="useecont">
                  <span>{{ scope.row.memberName }} </span>
                 <span class='usernum'>共6个客户</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="departmentName" label="所属部门" width="180">
          </el-table-column>
          <el-table-column prop="leaderDepartmentName" label="所管部门">
          </el-table-column>
          <el-table-column prop="roleDesc" label="角色">
            <!-- <template slot-scope="scope">
              {{ scope.row.roleDescs ? scope.row.roleDescs.join(",") : "-" }}
            </template> -->
          </el-table-column>
          <el-table-column prop="address" label="授权状态" width="230">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.authStatus"
                active-color="#6ad9a4"
                @change="userAuthStatus(scope.row)"
                inactive-color="#C0C4CC" />
              <span v-if="scope.row.authStatus" class="finish">
                已授权
              </span>
              <span v-else class="errors">
                未授权（请配置手机号）
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="mobile" label="手机号" width="140">
          </el-table-column>
          <el-table-column fixed="right" prop="action" label="操作" width="180">
            <template slot-scope="scope">
              <el-button size="small" type="text" @click="setpower(scope.row)"
                >管理权限</el-button
              >
              <el-divider direction="vertical"></el-divider>
              <el-button size="small" type="text" @click="mobileOpen(scope.row)"
                >设置手机号</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.page_num"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.page_size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </el-scrollbar>
    </div>
    <el-drawer title="添加成员" size="620px" :visible.sync="drawer">
      <div class="drawerout">
        <p class="diawtext">
          1、进入 <a target='_blank' href='https://work.weixin.qq.com/wework_admin/loginpage_wx?from=myhome'>企业微信</a> ，找到【管理工具】-【成员加入】
        </p>
        <img src="../assets/userstep1.png" alt="" />
        <p class="diawtext">
          2、打开成员加入，管理员可通过以下三种方式让成员加入企业
        </p>
        <img src="../assets/userstep2.png" alt="" />
      </div>
      <div class="demo-drawer__footer">
        <el-button size="small" @click="cancle" round>取 消</el-button>
        <el-button type="primary" size="small" round @click="cancle"
          >我知道了</el-button
        >
      </div>
    </el-drawer>
    <el-dialog :close-on-click-modal='false'  :close-on-press-escape='false' 
      title="设置手机号"
      @close="mobileCancel"
      :visible.sync="mobileVisible"
      width="358px"
      append-to-body
       :modal='false' ref=""
    >
    <el-input size="small" v-model="systemUserMessageRequest.mobile" placeholder="填写手机号"></el-input>
      <div slot="footer" class="dialog-footer">
        <el-button @click="mobileCancel"  class='btns' round size="small">取 消</el-button>
         <el-button
          type="primary"
          size="small"
          round
          class='btns'
          @click="userSave"
          >确 定</el-button
        >
      </div>
    </el-dialog>
    <el-dialog :close-on-click-modal='false'  :close-on-press-escape='false' 
      title="权限设置"
      @close="cancel"
      :visible.sync="open"
      width="358px"
      append-to-body
       :modal='false'
    >
      <el-form
        ref="form"
        :model="form"
        size="small"
        v-if="open"
        :disabled="read"
        :rules="rules"
        label-width="74px"
        hide-required-asterisk
      >
            <el-form-item label="权限设置" prop="targetRoleUuid">
              <el-select
                  v-model="form.targetRoleUuid"
                  style="width: 100%"
                  placeholder="请选择角色"
                >
                  <el-option
                    v-for="item in options"
                    :key="item.roleUuid"
                    :label="item.desc"
                    :value="item.roleUuid"
                  >
                  </el-option>
                </el-select>
            </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel"  class='btns' round size="small">取 消</el-button>
         <el-button
          type="primary"
          size="small"
          round
          class='btns'
          :loading="saveloading"
          @click="submitForm"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import operation from "../mixins/operation";
export default {
  mixins: [operation],
  data() {
    return {
      isactivaneed: false,
      searchkey: "",
      defaultProps: {
        children: "children",
        label: "name",
      },
      treedata: [],
      options: [],
      tableData: [],
      drawer: false,
      defaultparam: {},
      chosenode: {},
      choserole:[],
      rules:{
        targetRoleUuid:[
          { required: true, message: "请选择角色", trigger: "change" },
        ]
      },
      memberId:'',
      // 设置手机号
      mobileVisible: false,
      systemUserMessageRequest: {
        memberId:"",
        mobile: ""
      }
    };
  },
  async created() {
    this.defaultparam = {
      corpId: this.$store.state.corpId,
      projectUuid: this.$store.state.projectUuid,
      tenantUuid: this.$store.state.tenantUuid,
    };
    this.getrole();
    await this.gettree();
    this.getList();
  },
  mounted() {},
  methods: {
    setpower(val){
       this.open = true
       this.$set(this.form,'targetRoleUuid',val.roleUuid)
       this.memberId=val.memberId
    },
     submitForm() {
            this.$refs.form.validate(async vaild => {
                if (vaild) {
                  let form=JSON.parse(JSON.stringify(this.form))
                  form.changeRoleUserInfos=[{
                    targetRoleUuid:this.form.targetRoleUuid,
                    memberId:this.memberId
                  }]
                    this.saveloading = true
                    let params = {
                        ...form,
                        ...this.defaultparam
                    }
                    let data = await this.$http.post('mktgo/wecom/user/role/authorization', params)
                    if (data.code === 0) {
                        this.getList()
                        this.cancel()
                        this.$message.success('设置成功')
                    }
                    this.saveloading = false
                }
            })
        },
    async getrole() {
      let params = {
        ...this.defaultparam,
      };
      let data = await this.$http.post("mktgo/wecom/user/role/list", params);
      if (data.code === 0) {
        this.options = data.data.infos;
      }
    },
    async getList() {
      let params = {
        ...this.defaultparam,
        departmentId: this.chosenode.id,
        ...this.queryParams,
      };
      let data = await this.$http.post(
        "mktgo/wecom/user/organizational/structure/query",
        params
      );
      if (data.code === 0) {
        this.tableData = data.data.members;
      }
    },
    async gettree() {
      let params = {
        ...this.defaultparam,
      };
      let data = await this.$http.post(
        "mktgo/wecom/user/organizational/structure",
        params
      );
      if (data.code === 0) {
        this.treedata = data.data.structures;
        if (this.treedata.length > 0) {
          this.$nextTick(() => {
            this.chosenode = this.treedata[0];
            this.$refs.tree.setCurrentNode(this.treedata[0]);
          });
        }
      }
    },
    addmember() {
      this.drawer = true;
    },
    cancle() {
      this.drawer = false;
    },
    chakey() {
      this.$refs.tree.filter(this.searchkey);
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    handleNodeClick(val,node) {
      console.log(val)
       console.log(node)
      this.chosenode = val;
      this.getList();
    },
    // 设置手机号
    mobileOpen(item) {
      this.mobileVisible = true
      this.systemUserMessageRequest = {
        memberId:item.memberId,
        mobile: item.mobile
      }
    },
    mobileCancel() {
      this.mobileVisible = false
      this.systemUserMessageRequest = {
        memberId: "",
        mobile: ""
      }
    },
    userSave() {
      if (!this.systemUserMessageRequest.mobile || this.systemUserMessageRequest.mobile === '') {
        this.$message.info('请填写手机号')
        return
      }
      this.$api
        .userSave(this.systemUserMessageRequest)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.$message.success('设置成功')
            this.mobileCancel()
            this.getList()
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // 修改授权状态
    userAuthStatus(item) {
      if (!item.mobile || item.mobile === '') {
        this.mobileOpen(item)
        item.authStatus = !item.authStatus
        console.log(item)
        return
      }
      this.$api
        .userAuthStatus({
          authStatus: item.authStatus,
          memberId: item.memberId,
          mobile: item.mobile
        })
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.$message.success('设置成功')
            this.mobileCancel()
            this.getList()
          } else {
            item.authStatus = !item.authStatus
          }
        })
        .catch((err) => {
          item.authStatus = !item.authStatus
          console.log(err);
        });
    }
  },
};
</script>
<style scoped>
.tops {
  width: 100%;
  height: 40px;
  display: flex;
  flex-direction: column;
  color: #999999;
  font-size: 12px;
}
.tops h2 {
  color: #333;
  font-size: 20px;
  margin-bottom: 10px;
}
.left {
  width: 270px;
  float: left;
  height: calc(100% - 70px);
  background: #fff;
  border-radius: 10px;
}
.leftin{
  width:100%;
 padding: 24px;
  box-sizing: border-box;
}
.right {
  width: calc(100% - 284px);
  float: right;
  height: calc(100% - 70px);
  background: #fff;
  padding: 22px;
  box-sizing: border-box;
  border-radius: 10px;
}
.adds {
  width: 100%;
  height: 32px;
  border-bottom: 1px solid #ececec;
  margin: 20px 0;
  display: flex;
  align-items: center;
  font-size: 12px;
  cursor: pointer;
}
.adds i {
  margin-right: 8px;
  color: #d1d9e8;
}
.treelabel {
  font-size: 12px;
}
.treelabel img{
  width:12px;
}
.searchtop {
  display: flex;
  justify-content: space-between;
  margin-bottom:16px;
  padding:0 2px;
  box-sizing: border-box;
}
.searchtop h3 {
  font-size: 14px;
  line-height: 28px;
}
.search {
  width: 60%;
  font-size: 12px;
  color: #999;
}
.texts {
  line-height: 32px;
  float:left;
  width:36px;
  margin-right:4px;
}
.user {
  width: 110px;
  height: 39px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.user img {
  width: 30px;
  height: 30px;
  border-radius: 15px;
}
.useecont {
  width: 70px;
  height: 30px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  line-height: normal;
}
.useecont span {
  font-weight: 600;
}
.drawerout {
  padding: 10px 24px;
  box-sizing: border-box;
}
.diawtext {
  width: 100%;
  height: 44px;
  line-height: 44px;
  background: #fef4d8;
  opacity: 0.8;
  border-radius: 4px;
  color: #e79334;
  margin-bottom: 18px;
  font-size: 12px;
  text-indent: 10px;
}
.diawtext a {
  color: #679bff;
}
.drawerout img {
  width: 100%;
  margin-bottom: 18px;
}
.demo-drawer__footer {
  text-align: right;
  padding: 0 10px;
  box-sizing: border-box;
  margin-bottom: 10px;
}
::v-deep(.el-scrollbar__view) {
  height: 100%;
}
::v-deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}
.finish {
  /* color: #67c23a; */
  font-size: 12px;

color: #333333;
}
.finish i{
  color: #67c23a;
}
.errors i{
  color: #e6a23c;
}
.errors {
  /* color: #e6a23c; */
  color: #333333;
  opacity: 0.5;
}
.iconcor{
  color:#9BB7FF;
}
::v-deep(.el-tree-node__content){
  height:32px;
}
::v-deep(.el-dialog__body){
  padding:1px 20px 12px 20px;
}
::v-deep(.el-dialog__body) {
  padding:0 20px 20px;
}
.btns{
  width:96px;
}
.usernum{
  color:#999;
}
</style>