<template>
  <div class="masscustomer-detail">
    <main-head title="群发客户详情" :back="true"></main-head>
    <div class="top-content">
      <el-form label-width="100px" class="demo-baseForm" size="mini">
        <el-form-item label="创建人：" prop="name">
          {{ resData.creatorName }}
        </el-form-item>
        <el-form-item label="发送方式：" prop="name">
          {{ resData.scheduleType == 'IMMEDIATE' ? '立即发送' : '定时发送' }}
        </el-form-item>
        <el-form-item label="创建时间：" prop="name">
          {{ resData.createTime }}
        </el-form-item>
        <el-form-item label="任务时间：" prop="name">
          {{ resData.repeatStartTime }}&nbsp;至&nbsp;{{ resData.repeatEndTime }}
        </el-form-item>
        <el-form-item label="发送范围：" prop="name">
          满足筛选条件的 {{ detailNum }} 名客户 <span style="color: #678FF4;cursor: pointer;" @mouseover="rqShow = true" @mouseout="rqShow = false">详情</span>
          <div v-if="rqShow" style="display: inline-block;position: relative">
            <div class="fjBox" style="padding: 0">
              <PreviewMemberDE :userGroupUuid="resData.userGroupUuid" :count="detailNum" :type="$route.query.taskType"></PreviewMemberDE>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="消息内容：" prop="name">
          {{ resData.msgContent[0].text.content }}...不包含附件展示 <span style="color: #678FF4;cursor: pointer;" @mouseover="fjShow = true" @mouseout="fjShow = false">预览消息</span>
          <div v-show="fjShow" style="display: inline-block;position: relative">
            <div class="fjBox">
              <PreviewPhone :list="resData.msgContent"></PreviewPhone>
            </div>
          </div>
        </el-form-item>
      </el-form>
<!--      <el-tabs
          @tab-click="typeChange()"
          v-model="tabsType"
      >
        <el-tab-pane label="员工统计" name="yg"></el-tab-pane>
        <el-tab-pane label="客户统计" name="kh"></el-tab-pane>
      </el-tabs>-->
<!--      <div class="cardBox">
        <el-row v-show="tabsType == 'yg'">
          <el-col :span="8">
            <div class="card">
              <div class="title">预计执行员工</div>
              <div class="text">
                {{ cardData.memberTotalCount ? cardData.memberTotalCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="card">
              <div class="title">未执行员工</div>
              <div class="text">
                {{ cardData.nonSendCount ? cardData.nonSendCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="card">
              <div class="title">已执行员工</div>
              <div class="text">
                {{ cardData.sendCount ? cardData.sendCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-row v-show="tabsType == 'kh'">
          <el-col :span="5">
            <div class="card">
              <div class="title">预计送达客户</div>
              <div class="text">
                {{ cardData.externalUserTotalCount ? cardData.externalUserTotalCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
          <el-col :span="5">
            <div class="card">
              <div class="title">未送达客户</div>
              <div class="text">
                {{ cardData.nonDeliveredCount ? cardData.nonDeliveredCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
          <el-col :span="5">
            <div class="card">
              <div class="title">已送达客户</div>
              <div class="text">
                {{ cardData.deliveredCount ? cardData.deliveredCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
          <el-col :span="5">
            <div class="card">
              <div class="title">送达失败客户</div>
              <div class="text">
                {{ cardData.unfriendCount ? cardData.unfriendCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
          <el-col :span="5">
            <div class="card">
              <div class="title">接收达上限客户</div>
              <div class="text">
                {{ cardData.receiveLimitCount ? cardData.receiveLimitCount : 0}}<span>&nbsp;&nbsp;人</span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>-->
    </div>

    <div class="foot-content" style="margin-top: -6px">
      <div class="cardBox">
        <el-row v-show="tabsType == 'yg'">
          <el-col :span="6">
            <div class="card">
              <div class="title">预计任务发送次数</div>
              <div class="text">
                {{ cardData.totalSendCount ? cardData.totalSendCount : 0}}<span>&nbsp;&nbsp;次</span>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="card">
              <div class="title">已发送次数</div>
              <div class="text">
                {{ cardData.sentCount ? cardData.sentCount : 0}}<span>&nbsp;&nbsp;次</span>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="card">
              <div class="title">未发送次数</div>
              <div class="text">
                {{ cardData.unsentCount ? cardData.unsentCount : 0}}<span>&nbsp;&nbsp;次</span>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="card">
              <div class="title">发送失败次数</div>
              <div class="text">
                {{ cardData.failedCount ? cardData.failedCount : 0}}<span>&nbsp;&nbsp;次</span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>


    <div class="foot-content">
<!--      <el-row >
        <el-col :span="4" style="padding-left: 20px">
          <el-button
              type="primary"
              icon=""
              size="mini"
              round
              :disabled="$route.query.canRemind == 0"
              @click="remind()"
          >提醒员工发送
          </el-button>
        </el-col>
        <el-col :span="16">&nbsp;</el-col>
        <el-col :span="4" style="padding-right: 20px">
          <el-form
              size="mini"
              label-width="0px"
          >
            <el-form-item label="">
              <el-input
                  prefix-icon="el-icon-search"
                  v-model="cName"
                  placeholder="请输入员工姓名"
                  @blur="getMembers()"
                  @keyup.enter.native="getMembers()"
                  clearable
              />
            </el-form-item>
          </el-form>

        </el-col>
      </el-row>-->
      <div>
        <el-table :data="ygfootList" v-if="ygfootList.length">
          <el-table-column  label="日期" prop="planTime"></el-table-column>
          <el-table-column  label="状态" prop="status"></el-table-column>
          <el-table-column  label="完成率" prop="completeRate"></el-table-column>
          <el-table-column  label="预计执行员工（人）" prop="memberCount"></el-table-column>
          <el-table-column  label="预计送达客户（人）" prop="externalUserCount"></el-table-column>
          <el-table-column  label="操作" align="right">
            <template #default="{ row }">
              <el-button @click="openDetails(row)" size="small" type="text">详情</el-button>
            </template>
          </el-table-column>

        </el-table>
        <el-pagination
            v-if="ygfootList.length"
            :current-page="page_num"
            :page-size="page_size"
            :total="total"
            background
            layout="total,  prev, pager, next, sizes, jumper"
            :page-sizes="[10, 20, 30]"
            @size-change="pageSizeChange"
            @current-change="getMembers"
        />
        <el-empty :image="empty" description="暂无数据" v-else></el-empty>
      </div>
<!--      <div v-if="tabsType == 'yg'">
        <el-tabs
            @tab-click="ygtabsTypeChange()"
            v-model="ygtabsType"
        >
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="未执行" name="wzx"></el-tab-pane>
          <el-tab-pane label="已执行" name="yzx"></el-tab-pane>
        </el-tabs>
        <el-table :data="ygfootList" v-if="ygfootList.length">
          <el-table-column  label="员工" prop="memberName">
          </el-table-column>
          <el-table-column  :label="ygtabsType == 'wzx' ? `预计送达客户`: '送达客户'" prop="externalUserCount">
&lt;!&ndash;            <template slot-scope="scope">
              {{ ygtabsType == 'wzx' ? scope.memberTotalCount : scope.sendCount }}
            </template>&ndash;&gt;
          </el-table-column>
        </el-table>
        <el-pagination
            v-if="ygfootList.length"
            :current-page="page_num"
            :page-size="page_size"
            :total="total"
            background
            layout="total,  prev, pager, next, sizes, jumper"
            :page-sizes="[10, 20, 30]"
            @size-change="pageSizeChange"
            @current-change="getMembers"
        />
        <el-empty :image="empty" description="暂无数据" v-else></el-empty>
      </div>
      <div v-else-if="tabsType == 'kh'">
        <el-tabs
            @tab-click="khtabsTypeChange()"
            v-model="khtabsType"
        >
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="未送达" name="A"></el-tab-pane>
          <el-tab-pane label="已送达" name="B"></el-tab-pane>
          <el-tab-pane label="接收达上限" name="C"></el-tab-pane>
          <el-tab-pane label="送达失败" name="D"></el-tab-pane>
        </el-tabs>
        <el-table :data="khfootList" v-if="khfootList.length">
          <el-table-column  label="员工" prop="memberName">
          </el-table-column>
          <el-table-column  :label="khtableCalName" prop="externalUserCount">
          </el-table-column>
        </el-table>
        <el-pagination
            v-if="khfootList.length"
            :current-page="page_num"
            :page-size="page_size"
            :total="total"
            background
            layout="total,  prev, pager, next, sizes, jumper"
            :page-sizes="[10, 20, 30]"
            @size-change="pageSizeChange"
            @current-change="getMembers"
        />
        <el-empty :image="empty" description="暂无数据" v-else></el-empty>
      </div>-->
    </div>
  </div>
</template>

<script>
import PreviewPhone from "@/components/PreviewPhoneDE.vue";
import PreviewMemberDE from "@/components/PreviewMemberDE.vue";
export default {
  name: "masscustomer-detail",
  components: {PreviewPhone,PreviewMemberDE},
  data() {
    return {
      page_num: 1,
      page_size: 20,
      total: 0,
      empty: require('@/assets/empty-images.png'),
      resData: {
        "agentId": "string",
        "canRemind": true,
        "corpId": "string",
        "creatorId": "string",
        "creatorName": "string",
        "description": "string",
        "id": 0,
        "msgContent": [
          {
            "file": {
              "fileName": "string",
              "size": 0,
              "title": "string",
              "type": "EXCEL",
              "uuid": "string"
            },
            "image": {
              "thumbnailContent": "string",
              "uuid": "string"
            },
            "link": {
              "desc": "string",
              "thumbnailContent": "string",
              "title": "string",
              "url": "string",
              "uuid": "string"
            },
            "miniProgram": {
              "appId": "string",
              "page": "string",
              "thumbnailContent": "string",
              "title": "string",
              "uuid": "string"
            },
            "text": {
              "content": "string"
            },
            "type": "FILE",
            "video": {
              "thumbnailContent": "string",
              "title": "string",
              "uuid": "string"
            }
          }
        ],
        "name": "string",
        "scheduleTime": "string",
        "scheduleType": "string",
        "taskStatus": "string",
        "userGroupUuid": "string",
        "uuid": "string"
      },
      cardData:{},
      tabsType: 'yg',
      cName: '',
      ygtabsType: 'all',
      ygfootList:[],
      khtabsType: 'all',
      khtableCalName: '送达客户',
      khfootList:[],
      detailNum: 0,
      //附件预览
      fjShow: false,
      //人群预览
      rqShow: false,
      // welcomeContent: [],
    }
  },
  mounted() {
    this.init()
  },
  watch: {
    'tabsType': function f(n,o) {
      console.log(n,o)
      if (n == 'yg') {
        this.ygtabsType = 'all'
      } else {
        this.khtabsType = 'all'
      }
      this.getStatistic()
      this.getMembers()
    },
    'ygtabsType': function f() {
      this.getMembers()
    },
    'khtabsType': function f(n,o) {
      console.log(n,o)
      if (n == 'all') {
        this.khtableCalName = '送达客户';
      } else if (n == 'A') {
        this.khtableCalName = '未送达客户';
      } else if (n == 'B') {
        this.khtableCalName = '已送达客户';
      } else if (n == 'C') {
        this.khtableCalName = '接收达上限客户';
      } else if (n == 'D') {
        this.khtableCalName = '送达失败客户';
      }
      this.getMembers()

    }
  },
  methods: {
    openDetails(row) {
      let url = '/index/task-masscustomer-detail'
      if (this.$route.query.taskType == 'GROUP') {
        url = '/index/task-masscustomerbase-detail'
      }
      if (this.$route.query.taskType == 'MOMENT') {
        url = '/index/task-sendgroupfriends-detail'
      }
      this.$router.push({
        path: url,
        query: {
          // uuid: row.id,
          uuid: this.$route.query.uuid,
          // task_uuid: row.uuid,
          task_uuid: this.$route.query.task_uuid,
          plan_date: row.planTime,
          canRemind: row.canRemind ? 1 : 0,
        },
      })
    },
    pageSizeChange(e) {
      this.page_size = Number(e);
      this.getMembers(1)
    },
    async init() {
      let data = await this.$http.get(
          `mktgo/wecom/task_center/detail?task_id=${this.$route.query.uuid}&project_id=${this.$store.state.projectUuid}`,
          {});
      console.log(data)
      this.resData = data.data
      await this.getStatistic()
      await this.getMembers()

      /*let data1 = await this.$http.get(
          `mktgo/wecom/task_center/statistic?metrics_type=EXTERNAL_USER&project_id=${this.$store.state.projectUuid}&task_uuid=${this.$route.query.task_uuid}`,
          {});
      if (data1.data && data1.data.externalUserDetail) {
        this.detailNum = data1.data.externalUserDetail.externalUserTotalCount
      }*/

      let userGroupUuid = this.resData.userGroupUuid
      let _this = this
      this.$http.get(
          `mktgo/wecom/user_group/query?group_uuid=${userGroupUuid}&corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=SINGLE`,
          {}).then(function (res) {
        console.log(res)
        _this.detailNum = res.data.externalUserCount
      })

    },
    //mktgo/wecom/task_center/statistic
    async getStatistic() {
      /*let params = {
        metrics_type: this.tabsType == 'yg' ? 'MEMBER' : 'EXTERNAL_USER',//统计指标类型 MEMBER 员工； EXTERNAL_USER 客户, COMMENTS 互动统计
        project_id: this.$store.state.projectUuid,
        task_uuid: this.$route.query.task_uuid
      }*/
      let data = await this.$http.get(
          `mktgo/wecom/task_center/statistic?metrics_type=RATE&project_id=${this.$store.state.projectUuid}&task_uuid=${this.$route.query.task_uuid}`,
          {});
      console.log(data)
      if (data.data && data.data.memberDetail) {
        this.cardData = data.data.memberDetail
      } else if (data.data && data.data.externalUserDetail) {
        this.cardData = data.data.externalUserDetail
      } else if (data.data && data.data.statisticDetail) {
        this.cardData = data.data.statisticDetail
      }
    },
    //mktgo/wecom/task_center/members 获取群发任务统计的员工列表
    async getMembers(page_num) {
      /*let params = {
        metrics_type: this.tabsType == 'yg' ? 'MEMBER' : 'EXTERNAL_USER',//统计指标类型 MEMBER 员工； EXTERNAL_USER 客户, COMMENTS 互动统计
        project_id: this.$store.state.projectUuid,
        task_uuid: this.$route.query.task_uuid
      }*/
      let data = await this.$http.get(
          `mktgo/wecom/task_center/members?corp_id=${this.$store.state.corpId}&keyword=${this.cName}&metrics_type=RATE&page_num=${page_num ? page_num : this.page_num}&page_size=${this.page_size}&project_id=${this.$store.state.projectUuid}&status=${this.getParStatus()}&task_type=${this.$route.query.taskType}&task_uuid=${this.$route.query.task_uuid}`,
          {});
      console.log(data)
      if (data.data && data.data.members) {
        this.ygfootList = data.data.members
        this.khfootList = data.data.members
      } else if (data.data && data.data.externalUserDetail) {
        this.cardData = data.data.externalUserDetail
      } else if (data.data && data.data.dayDetails) {
        this.ygfootList = data.data.dayDetails
      }
      this.total = data.data.count
    },
    typeChange() {
      /*this.getStatistic()
      this.getMembers()*/
    },
    ygtabsTypeChange() {
      // this.getMembers()
    },
    khtabsTypeChange() {
      // this.getMembers()
    },
    remind() {
      let _this = this
      this.$http.post(
          `mktgo/wecom/task_center/remind?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=SINGLE&task_uuid=${this.$route.query.task_uuid}`,
          {}).then(function (res) {
            console.log(res)
        if (res.code == 0) {
          _this.$message({
            message: '提醒成功',
            type: 'success'
          });
        }
      });
    },
    getParStatus() {
      //员工维度(UNSENT 未执行; SENT 已执行;)
      // 客户维度(UNDELIVERED 未送达客户; DELIVERED 已送达客户; UNFRIEND 送达失败的客户; EXCEED_LIMIT 结束达上限； COMMENT 评论； LIKE 点赞)

      if (this.tabsType == 'yg') {
        if (this.ygtabsType == 'all') {
          return ''
        } else if (this.ygtabsType == 'wzx') {
          return 'UNSENT'
        } else {
          return 'SENT'
        }
      } else {
        if (this.khtabsType == 'all') {
          return ''
        } else if (this.khtabsType == 'A') {
          return 'UNDELIVERED'
        } else if (this.khtabsType == 'B') {
          return 'DELIVERED'
        } else if (this.khtabsType == 'C') {
          return 'EXCEED_LIMIT'
        } else if (this.khtabsType == 'D') {
          return 'UNFRIEND'
        } else {
          return ''
        }
      }
    }
  }
}
</script>

<style scoped lang="scss">
.fjBox {
  position: absolute;
  z-index: 999;
  top: -25px;
  left: 15px;
  padding: 11px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
}
::v-deep(.el-tabs) {
  .el-tabs__nav {
    margin-left: 24px;
  }
  .el-tabs__header {
    margin: 0 0 0px;
  }
}
::v-deep(.el-col-5) {
  width: calc(20% - 0px);
}
.masscustomer-detail {
  .top-content {
    background-color: white;
    padding-top: 18px;
    border-radius: 8px;
  }
  .cardBox {
    width: calc(100% - 24px);
    padding-left: 24px;
    padding-top: 20px;
    padding-bottom: 24px;
    position: relative;
    .card {
      width: calc(100% - 24px);
      height: 91px;
      background: #F8FAFF;
      border-radius: 4px;
      .title {
        font-family: 'PingFang SC';
        font-style: normal;
        font-weight: 400;
        font-size: 12px;
        line-height: 16px;
        color: #999999;
        margin: 0 0 0 20px;
        padding-top: 16px;
      }
      .text {
        font-family: 'Helvetica';
        font-style: normal;
        font-weight: 400;
        font-size: 32px;
        line-height: 32px;
        color: #333333;
        margin-top: 11px;
        margin-left: 20px;
        span {
          font-weight: 400;
          font-size: 14px;
          line-height: 16px;
        }
      }
    }
  }
  .foot-content {
    margin-top: 12px;
    background-color: white;
    padding-top: 18px;
    border-radius: 8px;

  }
}
</style>