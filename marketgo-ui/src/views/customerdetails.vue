<template>
  <div class="content">
    <div class="tops">
      <div class='iconout' @click="backs">
         <i class="el-icon-arrow-left"></i>
      </div>
      <div class="title">
        <h3>客户详情</h3>
        <!-- <span>xxxxxx</span> -->
      </div>
    </div>
    <div class="users">
      <img src="../assets/imgs/user.png" alt="" />
      <div class="names">
        <span>{{this.querys.memberName}}</span>
        <span class="wx" v-if='this.querys.type==2'>@微信</span>
      </div>
    </div>
    <div class="tabs">
      <div class="remark">
        <span> 员工信息备注 </span>
        下方是员工给客户的标签和备注，可以切换查看不同员工的备注和标签
      </div>
      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane
          :label="item.memberName"
          v-for="item in labellist"
          :key="item.memberId"
          :name="item.memberId"
        ></el-tab-pane>
      </el-tabs>
      <div class="chats">
        <div class="name">
          <img :src="chose.avatar?chose.avatar:require('../assets/avter.png')" alt="" />
          <span>{{chose.memberName}}</span>
        </div>
        <!-- <div>
          最近沟通时间：
          <span>2022-07-28 09:27:54</span>
        </div>
        <div>聊天记录</div> -->
      </div>
      <div class='remaname'>{{chose.memberName}}给TA的备注</div>
      <el-descriptions
        title=""
        :column="2"
        border
        size='mini'
      >
        <el-descriptions-item>
          <template slot="label">
            备注
          </template>
          {{chose.remark}}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            手机号
          </template>
          {{chose.mobile}}
        </el-descriptions-item>
        <el-descriptions-item span='2'>
          <template slot="label">
            性别
          </template>
          {{chose.gender==1?'男':'女'}}
        </el-descriptions-item>
        <el-descriptions-item :span='2'>
          <template slot="label">
            标签
          </template>
          <div class='listout'>
             <div class='listone' v-for='item in chose.tags' :key='item.tagId'>
                    {{item.tagName}}
                  </div>
          </div>
          <!-- <el-tag size="small" type='info' color='#fff' v-for='item in chose.tags' :key='item.tagId'>{{item.tagName}}</el-tag> -->
        </el-descriptions-item>
        <el-descriptions-item :span='2'>
          <template slot="label">
            描述
          </template>
          {{chose.description}}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      activeName: "",
      labellist: [],
      querys:{},
      chose:{}
    };
  },
  mounted() {
    this.querys=this.$route.query
    this.getuser()
  },
  methods: {
    handleClick() {
        this.chose=this.labellist.filter(item=>{
          return item.memberId==this.activeName
        })[0]
      },
    async getuser(){
      let params={
        external_user:this.querys.id,
        page_num:1,
        page_size:5000,
        corp_id :this.$store.state.corpId,
    project_id : this.$store.state.projectUuid
      }
      let data=await this.$http.get('mktgo/wecom/contacts/external_user/detail',{params})
      if(data.code===0){
        this.labellist=data.data.members
        if(this.labellist.length>0){
          this.activeName=this.labellist[0].memberId
          this.chose=this.labellist[0]
        }
      }
    },
    backs() {
      this.$router.go(-1);
    },
  },
};
</script>
<style scoped>
.tops {
  width: 100%;
  height: 64px;
  display: flex;
  align-items: center;
  margin-top:-24px;
}
.tops i {
  cursor: pointer;
}
.title {
  height: 40px;
  display: flex;
  font-size: 12px;
  margin-left: 20px;
  flex-direction: column;
  justify-content: center;
}
.title h3 {
  font-size: 18px;
}
.users {
  width: 100%;
  height: 66px;
  background: #fff;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-sizing: border-box;
  border-radius: 8px;
}
.users img {
  width: 44px;
  height: 44px;
  border-radius: 22px;
  margin-right: 20px;
}
.names {
  display: flex;
  height: 44px;
  padding: 2px 0;
  box-sizing: border-box;
  justify-content: center;
  flex-direction: column;
  font-size: 12px;
  color: #444;
}
.wx {
  margin-top:10px;
  color: #28ba36;
}
.tabs {
  width: 100%;
  padding: 0 20px 20px;
  box-sizing: border-box;
  border-radius: 8px;
  background: #fff;
}
::v-deep(.el-tabs__item) {
  border: 0 !important;
}
::v-deep(.el-tabs__nav) {
  border: 0 !important;
}
::v-deep(.el-tabs__header) {
  border: 0 !important;
}
.remark {
  width: 100%;
  height: 60px;
  line-height: 60px;
  font-size: 12px;
  color: #999999;
}
.remark span {
  font-weight: 600;
  color: #333;
}
.chats {
  width: 100%;
  height: 46px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
  padding: 0 12px;
  box-sizing: border-box;
  margin: 20px 0 10px 0;
  border: 1px solid #e5eef4;
}
.chats img {
  width: 22px;
  height: 22px;
  border-radius: 11px;
  margin-right: 12px;
}
.name {
  color: #678ff4;
  display: flex;
  align-items: center;
}
.remaname {
  width: 100%;
  height: 30px;
  line-height: 30px;
  font-weight: 600;
  font-size: 12px;

  color: #333333;
}
::v-deep(.el-tag){
  margin-right:10px;
}
::v-deep(.is-active){
  border-bottom:2px solid #678FF4!important;
  color:#678FF4;
}
::v-deep(.el-tabs__nav-wrap:after) {
    content: ""!important;
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 2px;
    background-color: #e4e7ed;
    z-index: 1;
}
.listout{
  width:100%;

}
 .listone{
  float:left;
  padding:2px 16px!important;
  box-sizing: border-box;
  border: 1px solid #E1E1E1;
border-radius: 31px;
margin-right:6px;
font-size:12px;
line-height:normal;
}
</style>