<template>
  <div class="content">
     <div class="tops">
      <div class='iconout' @click="backs">
         <i class="el-icon-arrow-left"></i>
      </div>
          <div class="title">
        <h3>客户群列表</h3>
        <!-- <span>xxxxxx</span> -->
      </div>
    </div>
    <div class='conttop'>
        <div class='name'>
            <img src="../assets/group.png"  alt="">
            <div class='basic'>
                <h3>{{detail.groupChatName}}</h3>
                <p>
                   创建时间：
                   <span>{{detail.createTime}}</span> 
                </p>
                <p>
                   群主：
                   <span>{{detail.ownerName}}</span> 
                </p>
                <p>
                   群公告：
                   <span>{{detail.notice}}</span> 
                </p>
            </div>
        </div>
         <div class='statistics'>
            <div class='titletext'>
                <h3>{{detail.groupChatName}}</h3>
                <span>
                    更新时间：{{detail.createTime}}
                </span>
            </div>
            <div class='statisnum'>
                <div class='nums'>
                    <span>群人数</span>
                    <p>
                        <span class='num'>{{detail.statistic.totalCount}}</span>
                        人
                    </p>
                </div>
                 <div class='nums'>
                    <span>今日新增</span>
                    <p>
                        <span class='num'>{{detail.statistic.todayAddCount}}</span>
                        人
                    </p>
                </div>
                 <div class='nums'>
                    <span>今日退群</span>
                    <p>
                        <span class='num'>{{detail.statistic.todayExitCount}}</span>
                        人
                    </p>
                </div>
            </div>
         </div>
    </div>
     <div class='tabouts'>
        <el-button size='mini' round class='btns'>导出客户明细</el-button>
     <el-table
      v-loading="loading"
      :data="datalist"
    >
      <el-table-column label="群成员"  show-overflow-tooltip prop="name">
         <template slot-scope="scope">
        <div class='user'>
            <img v-if='scope.row.avatar' :src="scope.row.avatar" alt="">
            <img v-else src="../assets/avter.png" alt="">
            {{scope.row.name}}
            <span v-if='scope.row.type===2'>@微信</span>
        </div>
      </template>
      </el-table-column>
      <el-table-column
        label="关系"
        
        prop="type"
      >
      <template slot-scope="scope">
        {{scope.row.type==1?'企业成员':'外部联系人'}}
      </template>
      </el-table-column>
      <el-table-column
        label="进群时间"
        
        prop="joinTime"
      />
      <el-table-column
        label="进群方式"
        
        prop="joinScene"
      >
      <template slot-scope="scope">
        {{scope.row.joinScene==1?'由群成员邀请入群（直接邀请入群）':scope.row.joinScene==2?'由群成员邀请入群（通过邀请链接入群）':'通过扫描群二维码入群'}}
      </template>
      </el-table-column>
    </el-table>
     </div>
    <el-pagination
        background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="queryParams.page_num"
      :page-sizes="[10, 20, 30, 50]"
      :page-size="queryParams.page_size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
  </div>
</template>
<script>
import operation from "../mixins/operation";
export default {
  mixins: [operation],
  data() {
    return {
      groupChatId: "",
      url: {
        list: "mktgo/wecom/contacts/group_chat/detail",
        del: "",
        add: "",
        edit: "",
        exporturl: "",
      },
      tablekey:'groupChatMembers',
      detail:{
        statistic:{}
      }
    };
  },
  created() {
    this.groupChatId = this.$route.query.group_chat_id;
    this.delaultsearch = {
      corp_id: this.$store.state.corpId,
      group_chat_id: this.groupChatId,
    };
    this.getdetail()
  },
  methods: {
    backs() {
      this.$router.go(-1);
    },
    async getdetail(){
        let params={
            ...this.delaultsearch
        }
        let data=await this.$http.get('mktgo/wecom/contacts/group_chat/statistic',{params})
        if(data.code===0){
            this.detail=data.data
        }
    }
  },
};
</script>
<style scoped>

.tops {
  width: 100%;
  height: 60px;
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
.conttop{
    width:100%;
    height:150px;
    display:flex;
    justify-content: space-between;
    margin-bottom:20px;
}
.conttop>div{
    width:49.4%;
    height:100%;
    background:#fff;
      padding:10px 20px;
    box-sizing: border-box;
}
.name{
    display:flex;
    border-radius: 8px;
}
.name img{
    width:44px;
    height:44px;
    margin-right:18px;
}
.basic{
    display:flex;
    height:100%;
    flex-direction: column;
    justify-content: space-between;
    font-size:12px;
}
.basic p{
    color:#999;
}
.basic p span{
    color:#333;
}
.titletext{
    width:100%;
    height:40px;
    display:flex;
    justify-content: space-between;
    font-size:12px;
    align-items: center;
    color:#333;
}
.titletext span{
    color:#999999;
}
.statisnum{
     width:100%;
    height:110px;
    display:flex;
    justify-content:space-between;
    align-items: center;
}
.nums{
    width:33%;
    font-size:12px;
    display:flex;
    flex-direction: column;
}
.nums>span{
    color:#999;
}
.num{
    font-weight: 600;
font-size: 36px;
 color:#333;
}
.btns{
    margin-bottom:10px;
}
.user{
    display:flex;
    height:30px;
    align-items: center;
}
.user img{
    width:30px;
    height:30px;
    border-radius: 15px;
    margin-right:10px;
}
.tabouts{
    background:#fff;
    padding:10px;
    box-sizing: border-box;
}
.user span{
    color:#28BA36;
     margin-left:10px;
}
</style>