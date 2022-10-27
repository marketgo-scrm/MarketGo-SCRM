<template>
  <div class="contout">
    <div class="tops">
      <h2>权限管理</h2>
      <!-- <span>添加成员，并设置角色与权限</span> -->
    </div>
    <div class="left">
      <el-scrollbar style="height: 100%">
        <div class="leftin">
          <div class="titles">角色管理</div>
          <div class="adds" @click="handleAdd">
            <i class="el-icon-circle-plus-outline"></i>
            添加角色
          </div>
          <div
            class="listone"
            @click="charole(item)"
            :class="{ active: nowid === item.roleUuid }"
            v-for="item in options"
            :key="item.roleUuid"
          >
            {{ item.desc }} ({{item.count}})
          </div>
        </div>
      </el-scrollbar>
    </div>
    <div class="right">
      <el-scrollbar style="height: 100%">
        <div class="searchtop">
          <h3>{{ desc }}</h3>
        </div>
        <el-tabs v-model="activeName">
          <el-tab-pane label="成员列表" name="1">
            <div class="search">
              <!-- <el-row type="flex"  justify="end"> -->
              <!-- <el-col :span="10"> -->
              <el-input
                @change="getList"
                size="small"
                placeholder="请输入成员名称"
                prefix-icon="el-icon-search"
                v-model="queryParams.searchMemberName"
                @keyup.enter.native="getList"
              >
              </el-input>
              <!-- </el-col> -->
              <!-- <el-col :span="6" :offset='1'> -->
              <el-button size="small"  plain round @click='chrole'>批量更换角色</el-button>
              <el-button size="small"  plain round>添加管理员</el-button>
              <!-- </el-col> -->
              <!-- </el-row> -->
            </div>
            <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="65" align='center' />
              <el-table-column prop="date" label="成员">
                <template slot-scope="scope">
                  <div class="user">
                    <img :src="scope.row.thumbAvatar?scope.row.thumbAvatar:require('../assets/avter.png')" alt="" />
                    <div class="useecont">
                      <span>{{ scope.row.memberName }} </span>
                    <!-- <span class='nums'>共6个客户</span> -->
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                prop="departmentName"
                label="所属部门"
              >
              </el-table-column>
              <el-table-column prop="mainDepartmentName" label="所管部门">
              </el-table-column>
              <el-table-column prop="action" label="操作" width="120px">
                <template slot-scope="scope">
                  <el-button
                    size="small"
                    type="text"
                    @click="handleUpdate(scope.row)"
                    >更改角色</el-button
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
          </el-tab-pane>
          <el-tab-pane label="权限范围" name="2">
            <div class='powerlist' v-for='item in powerlist' :key='item.id'>
                <div class='menu'>
                  <span>{{item.title}}</span>
                    <el-switch
                    v-model="item.status"
                    active-color="#13ce66"
                    inactive-color="#DADADA">
                  </el-switch>
                </div>
                <template v-if='item.children.length>0'>
                <div class='menytab'>
                  <div class='funs'>
                    功能
                  </div>
                  <div class='jurisdiction'>
                    权限
                  </div>
                </div>
                <div class='menulist' v-for='val in item.children' :key='val.id'>
                   <div class='menufuns'>
                     <el-checkbox v-model="val.status"></el-checkbox><span>
                      {{val.title}}
                     </span>
                  </div>
                  <div class='menujurisdiction'>
                   <div class='btnlist' v-for='obj in val.children' :key='obj.id'>
                       <el-checkbox v-model="obj.status">{{val.name}}</el-checkbox>
                   </div>
                  </div>
                </div>
                </template>
            </div>
            <div style='text-align:right'>
              <el-button type='primary' round size='mini' @click='edits'>修改</el-button>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-scrollbar>
    </div>
    <el-dialog
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      title="角色"
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
        <el-form-item v-if='choselist.length==0' label="角色名称" prop="roleDesc">
          <el-input
            v-model="form.roleDesc"
            placeholder="请输入角色名称"
          ></el-input>
        </el-form-item>
        <el-form-item v-else label="角色设置" prop="targetRoleUuid">
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
        <el-button
          type="primary"
          size="small"
          round
          class='btns'
          :loading="saveloading"
          @click="submitForm"
          >确 定</el-button
        >
        <el-button @click="cancel" round class='btns' size="small">关 闭</el-button>
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
        label: "departmentName",
      },
      url: {
        list: "mktgo/wecom/user/role/list/query",
        add: "mktgo/wecom/user/role/add",
        edit:'mktgo/wecom/user/role/authorization'
      },
      options: [],
      tableData: [],
      defaultfrom: {},
      desc: "",
      choserole: [],
      rules: {
        roleDesc: [
          { required: true, message: "请选择角色", trigger: "change" },
        ],
        targetRoleUuid: [
          { required: true, message: "请选择角色", trigger: "change" },
        ]
      },
      memberId: "",
      activeName: "1",
      powerlist:[],
      choselist:[]
    };
  },
  async created() {
    this.defaultfrom = {
      corpId: this.$store.state.corpId,
      projectUuid: this.$store.state.projectUuid,
      tenantUuid: this.$store.state.tenantUuid,
    };
    await this.getrole();
    this.getList();
    this.getpower()
  },
  mounted() {},
  methods: {
    handleUpdate(val){
      this.choselist=[]
      this.$set(this.form,"targetRoleUuid",this.nowid)
      this.choselist.push(val)
      this.open=true
    },
     handleSelectionChange(val) {
            this.datalistselection = val
             this.choselist=val
        },
        chrole(){
          if(this.datalistselection.length==0){
            return this.$message.error('请选择用户')
          }
          this.open=true
        },
         cancel() {
            this.form = { ...this.defaultfrom }
            this.editdata = {}
            this.saveloading = false
            this.open = false
            this.choselist=[]
        },
    submitForm() {
            this.$refs.form.validate(async vaild => {
                if (vaild) {
                    this.saveloading = true
                    let str = ''
                    let methods = ''
                    let params = {
                        ...this.form
                    }
                    let msg = ''
                    if (this.choselist.length>0) {
                      let changeRoleUserInfos=[]
                      this.choselist.map(item=>{
                        changeRoleUserInfos.push({
                          memberId:item.memberId,
                          targetRoleUuid:this.form.targetRoleUuid
                        })
                      })
                      params.changeRoleUserInfos=changeRoleUserInfos
                        //编辑
                        msg = '编辑成功'
                        str = this.url.edit
                        methods = 'post'
                    } else {
                        str = this.url.add
                        methods = 'post'
                        msg = '增加成功'
                    }
                    let data = await this.$http[methods](str, params)
                    if (data.code === 0) {
                      this.getrole()
                        this.getList()
                        this.cancel()
                        this.$message.success(msg)
                    }
                    this.saveloading = false
                    //this.getList()
                }
            })
        },
   async edits(){
    let params={
      permissions:this.powerlist,
      ...this.defaultfrom,
        roleUuid: this.nowid
    }
 let data=await this.$http.post('mktgo/wecom/user/permissions/authorization',params)
 if(data.code===0){
  this.$message.success('修改成功')
  this.getpower()
 }
    },
    async getrole() {
      let params = {
        ...this.defaultfrom,
      };
      let data = await this.$http.post("mktgo/wecom/user/role/list", params);
      if (data.code === 0) {
        this.options = data.data.infos;
        this.desc = data.data.infos[0].desc;
        this.nowid = data.data.infos[0].roleUuid;
      }
    },
    charole(val) {
      this.nowid = val.roleUuid;
      this.desc = val.desc;
      this.getList();
       this.getpower()
    },
    async getpower(){
      let params={
        ...this.defaultfrom,
        roleUuid: this.nowid
      }
      let data=await this.$http.post('mktgo/wecom/user/permissions/list/query',params)
      if(data.code===0){
        this.powerlist=data.data
      }
    },
    async getList() {
      let params = {
        ...this.defaultfrom,
        ...this.queryParams,
        roleUuid: this.nowid,
      };
      let data = await this.$http.post(
        this.url.list,
        params
      );
      if (data.code === 0) {
        this.tableData = data.data.infos;
      }
    },
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
.leftin {
  width: 100%;
  padding: 22px;
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
.searchtop {
  display: flex;
  justify-content: space-between;
  margin-bottom:4px;
}
.searchtop h3 {
  font-size: 14px;
  line-height: 28px;
}
.search {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  font-size: 12px;
  color: #999;
  padding-right:4px;
  box-sizing: border-box;
}
.texts {
  line-height: 28px;
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
  justify-content: center;
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
  color: #67c23a;
}
.errors {
  color: #e6a23c;
}
.listone {
  width: 100%;
  height: 30px;
  line-height: 30px;
  text-indent: 20px;
  font-size: 12px;
  color: #333;
  cursor: pointer;
}
.active {
  font-weight: 600;
  background: #eaf1ff;
}
::v-deep(.search .el-input) {
  width: 200px;
  margin-right: 10px;
}
.titles {
  font-weight: 600;
  font-size: 14px;
  line-height: 20px;
  color: #333333;
}
.powerlist{
  width:100%;
  border: 1px solid #E5EEF4;
  border-bottom: 0;
  box-sizing: border-box;
  font-size:12px;
  color:#333;
  margin-bottom:20px;
}
.menu{
  width:100%;
  height:40px;
  background: #F4F9FD;
  line-height:40px;
color: #252D39;
font-weight: 600;
text-indent:16px;
}
.menu span{
  margin-right:10px;
}
.menytab{
  width:100%;
  height:40px;
  display:flex;
}
.menulist{
  width:100%;
  border-bottom: 1px solid #E5EEF4;
  display:flex;
}
.funs{
  width:30%;
  line-height:40px;
  text-align: center;
  border-right: 1px solid #E5EEF4;
}
.jurisdiction{
  width:70%;
  line-height:40px;
  text-align: center;
}
.menufuns{
  width:30%;
  border-right: 1px solid #E5EEF4;
  display:flex;
  align-items: center;
  height:40px;
  padding-left:10px;
  box-sizing: border-box;
}
.menufuns span{
  margin-left:10px;
}
.menujurisdiction{
  width:70%;
  text-align: center;
  padding:20px 0 0 0;
  box-sizing: border-box;
}
::v-deep(.el-checkbox__label){
  font-size:12px;
}
::v-deep(.btnlist .el-checkbox){
  margin-bottom:20px;
}
::v-deep(.el-button--default){
  border-color: #679BFF;
  color: #679BFF;
}
.nums{
  color:#999;
}
::v-deep(.el-dialog__body){
  padding:1px 20px 12px 20px;
}
::v-deep(.dialog__footer) {
  padding:0 20px 20px;
}
.btns{
  width:96px;
}
</style>