<template>
  <div>
    <main-head title="任务中心">
      <el-form class="head-form" inline size="small" label-width="0">
        <el-form-item>
          <el-button
              style="position: relative; top: -3px"
              type="primary"
              size="medium"
              icon="el-icon-circle-plus"
              round
              @click="toAdd"
          >
            新建任务
          </el-button>
        </el-form-item>
      </el-form>
    </main-head>
    <div class="search-content">
      <el-form
          :model="queryParams"
          size="mini"
          ref="queryForm"
          v-show="true"
          label-width="90px"
      >
        <el-row>
          <el-col :span="5">
            <el-form-item label="任务名称">
              <el-input
                  prefix-icon="el-icon-search"
                  v-model="queryParams.keyword"
                  placeholder="请输入"
                  clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="添加时间">
              <el-date-picker
                  style="width: 100%"
                  type="daterange"
                  v-model="queryParams.times"
                  align="right"
                  unlink-panels
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="yyyy-MM-dd"
                  :picker-options="pickerOptions"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="创建人">
<!--              <el-input
                  prefix-icon=""
                  v-model="queryParams.external_user"
                  placeholder="请选择创建人"
                  clearable
              />-->
              <el-select v-model="queryParams.create_user_ids" multiple placeholder="请选择创建人">
                <el-option
                    v-for="item in creators"
                    :key="item.creatorId"
                    :label="item.creatorName"
                    :value="item.creatorId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="5">
            <el-form-item label="任务状态">
              <el-select
                  style="width: 100%"
                  multiple
                  collapse-tags
                  v-model="queryParams.statuses"
                  placeholder="请选择"
                  clearable
              >
                <el-option
                    v-for="(dict, index) in statusesOption"
                    :key="index"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="任务类型">
              <el-select
                  style="width: 100%"
                  collapse-tags
                  v-model="queryParams.task_type"
                  placeholder="请选择"
                  clearable
              >
                <el-option
                    v-for="(dict, index) in task_typeOption"
                    :key="index"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="1" style="height: 10px;margin-right: 10px"> </el-col>
          <el-col :span="8">
            <el-form-item label-width="20px">
              <el-button
                  type="primary"
                  icon=""
                  size="mini"
                  round
                  @click="handleQuery"
              >筛选
              </el-button>
              <el-button
                  icon=""
                  round
                  size="mini"
                  @click="resetQuery"
              >重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="main-content">
      <el-table v-if="dataList.length" :data="dataList">
        <el-table-column width="186" label="任务名称" prop="name">
        </el-table-column>
        <el-table-column label="创建人" prop="creatorName" />
        <el-table-column label="开始时间" sortable prop="scheduleTime">
        </el-table-column>
        <el-table-column label="类型">
          <template slot-scope="scope">
            {{ getTaskTypeName(scope.row.taskType) }}
          </template>
        </el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            {{ getStatusName(scope.row.taskStatus) }}
          </template>
        </el-table-column>
        <el-table-column label="完成率" prop="completeRate" />
        <el-table-column label="操作"
                         align="right"
        >
          <template #default="{ row }">
            <el-button size="small" type="text" :disabled="!row.canRemind" @click="remind(row)">提醒发送</el-button>
<!--            <el-divider direction="vertical" v-if="row.canRemind"></el-divider>-->
<!--            <el-divider direction="vertical"></el-divider>-->
<!--            <el-button @click="openDetails(row)" size="small" type="text">详情</el-button>-->
            <el-divider direction="vertical"></el-divider>
            <el-button size="small" type="text" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-if="dataList.length"
          :current-page="page_num"
          :page-size="page_size"
          :total="total"
          background
          layout="total,  prev, pager, next, sizes, jumper"
          :page-sizes="[10, 20, 30]"
          @size-change="pageSizeChange"
          @current-change="searchData"
      />
      <el-empty :image="empty" description="暂无数据" v-else></el-empty>
    </div>

    <el-dialog title="选择您要创建的任务类型" :visible.sync="dialogFormVisible">
      <div class="typeBox">
        <div class="item " :class="khChick ? 'active' : ''"
             @click="khChick = true;khqChick = false;pyqChick = false">
          <div class="icon"></div>
          <div class="text " :class="khChick ? 'active' : ''">客户触达</div>
        </div>
        <div class="item" :class="khqChick ? 'active' : ''" style="margin-right: 0"
             @click="khChick = false;khqChick = true;pyqChick = false">
          <div class="icon khq"></div>
          <div class="text" :class="khqChick ? 'active' : ''">客户群触达</div>
        </div>
        <div class="item" :class="pyqChick ? 'active' : ''" style="margin-bottom: 0"
             @click="khChick = false;khqChick = false;pyqChick = true">
          <div class="icon pyq"></div>
          <div class="text" :class="pyqChick ? 'active' : ''">朋友圈任务</div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"
                   icon=""
                   size="mini"
                   round
        >取 消</el-button>
        <el-button type="primary" @click="addJump()"
                   icon=""
                   round
                   size="mini"
        >创 建</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: 'masscustomer',
  data() {
    return {
      page_num: 1,
      page_size: 20,
      total: 0,
      empty: require('@/assets/empty-images.png'),
      queryParams: {
        keyword: '',
        create_user_ids:[],
        times: ['',''],
        statuses: [],
        task_type: '',
      },
      pickerOptions: {
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      statusesOption: [
        /*{ label: "未开始", value: JSON.stringify(['UNSTART']) },
        { label: "进行中", value: JSON.stringify(['COMPUTING', 'COMPUTED', 'SENDING']) },
        { label: "已结束", value: JSON.stringify(['FINISHED']) },*/
        { label: "未开始", value: 'UNSTART' },
        { label: "进行中", value: 'COMPUTING,COMPUTED,SENDING' },
        { label: "已结束", value: 'FINISHED' },
      ],
      task_typeOption: [
        { label: "全部", value: '' },
        { label: "群发好友", value: 'SINGLE' },
        { label: "群发客户群", value: 'GROUP' },
        { label: "群发朋友圈", value: 'MOMENT' },
      ],
      dataList: [],
      creators: [],//创建人

      dialogFormVisible: false,
      formLabelWidth: '120px',
      khChick: true,
      khqChick: false,
      pyqChick: false,
    }
  },
  mounted() {
    this.searchData()
    this.getCreators()
  },
  watch: {
    'queryParams.task_type' : function () {
      this.getCreators()
    }
  },
  methods: {
    pageSizeChange(e) {
      this.page_size = Number(e);
      this.searchData(1)
    },
    del(row) {
      let _this = this
      this.$confirm('是否确定删除？', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(() => {
        console.log(row)
        this.$http.post(
            `mktgo/wecom/task_center/delete?project_id=${this.$store.state.projectUuid}&task_type=${row.task_type}&task_uuid=${row.uuid}`,
            {}).then(function (res) {
          console.log(res)
          if (res.code == 0) {
            _this.$message({
              message: '删除成功',
              type: 'success'
            });
          } else {
            _this.$message(res.message);
          }
        });
      }).catch(action => {
        console.log(action)
      });
    },
    remind(row) {
      let _this = this
      this.$http.post(
          `mktgo/wecom/task_center/remind?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=${row.task_type}&task_uuid=${row.uuid}`,
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
    getCreators() {
      let _this = this
      this.$http.get(
          // `mktgo/wecom/task_center/creators?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=SINGLE`,
          `mktgo/wecom/task_center/creators?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=${this.queryParams.task_type ? this.queryParams.task_type : 'SINGLE'}`,
          {}).then(function (res) {
        _this.creators = res.data.creators
      });
    },
    async searchData(page_num) {
      let params = {
        create_user_ids: '',
        start_time: '',
        end_time: '',
        keyword: '',
        page_num: 1,
        page_size: 100000,
        sort_key: '',
        sort_order: '',
        statuses: '',
        task_type: '',
      }

      /*let statusesStr = ''
      for (let i = 0; i < this.queryParams.statuses.length; i++) {
        statusesStr += this.queryParams.statuses.join(',')
      }*/

      let queryStr = ''
      for (let k in params) {
        // console.log("p",k)
        k == 'create_user_ids' ? queryStr += `&create_user_ids=${
          // this.queryParams.create_user_ids.length ? JSON.stringify(this.queryParams.create_user_ids):''
            this.queryParams.create_user_ids.join(',')
        }` : ''
        k == 'keyword' ? queryStr += `&keyword=${this.queryParams.keyword}` : ''
        k == 'start_time' ? queryStr += `&start_time=${this.queryParams.times[0]}` : ''
        k == 'end_time' ? queryStr += `&end_time=${this.queryParams.times[1]}` : ''
        k == 'statuses' ? queryStr += `&statuses=${this.queryParams.statuses.join(',')}` : ''
        k == 'task_type' ? queryStr += `&task_type=${this.queryParams.task_type}` : ''
      }
      queryStr += `&page_num=${page_num ? page_num : this.page_num}`
      queryStr += `&page_size=${this.page_size}`
      let data = await this.$http.get(
          `mktgo/wecom/task_center/list?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}`+queryStr,
          {});
      console.log(data)
      if (data.code == 0) {
        this.dataList = data.data.list
        this.total = data.data.totalCount
      }
    },
    getTaskTypeName(type) {
      if (type == 'SINGLE') {
        return '客户任务';
      } else if (type == 'GROUP') {
        return '客户群任务';
      } else {
        return '朋友圈任务'
      }
    },
    getStatusName(status) {
      // console.log(status)
      if (status == 'UNSTART') {
        return '未开始';
      } else if (status == 'FINISHED') {
        return '已结束';
      } else {
        return '进行中'
      }
      /*switch (status) {
        case 'UNSTART':
          return '未开始';
        case 'FINISHED':
          return '已结束';
        default :
          return '进行中'
      }*/
    },
    openDetails(row) {
      console.log(row)
      this.$router.push({
        path: '/index/masscustomer-detail',
        query: {
          uuid: row.id,
          task_uuid: row.uuid,
          canRemind: row.canRemind ? 1 : 0,
        },
      })
    },
    toAdd() {
      console.log(111)
      // this.$router.push('/index/masscustomer-add')
      this.dialogFormVisible = true
    },
    addJump() {
      if (this.khChick) {
        this.$router.push('/index/task-masscustomer-add')
      } else if (this.khqChick) {
        this.$router.push('/index/task-masscustomerbase-add')
      } else if (this.pyqChick) {
        this.$router.push('/index/task-sendgroupfriends-add')
      }
    },
    handleQuery() {
      // this.queryParams.page_num = 1
      console.log(this.queryParams)
      this.searchData()
    },
    resetQuery() {
      // this.queryParams = JSON.parse(JSON.stringify(this.delaultsearch))
      // this.queryParams.page_num = 1
      this.queryParams = {
        keyword: '',
        create_user_ids:[],
        times: ['',''],
        statuses: [],
        task_type: '',
      }
      // this.getList()
    }
  }
}
</script>
<style scoped lang="scss">
::v-deep(.el-dialog) {
  width: 664px;
  height: 308px;

  background: #FFFFFF;
  box-shadow: 0px 5px 28px rgba(0, 0, 0, 0.18);
  border-radius: 8px;
  .el-dialog__title {

    font-family: 'PingFang SC';
    font-style: normal;
    font-weight: 600;
    font-size: 14px;
    line-height: 16px;
    color: #252D39;
  }
  .el-dialog__footer {
    padding-top: 0;
  }
  .typeBox {
    .item {
      box-sizing: border-box;
      width: 300px;
      height: 67px;
      cursor: pointer;
      background: #FFFFFF;
      border: 1px solid #DFDFDF;
      border-radius: 8px;
      position: relative;
      display: inline-block;
      margin-right: 16px;
      margin-bottom: 16px;
      &.active {
        height: 65px;
        border: 2px solid #679BFF;
      }
      .icon {
        width: 40px;
        height: 51px;
        background: url("../assets/imgs/taskcenter/kh.png") no-repeat top;
        background-size: 100%;
        position: absolute;
        top: 16px;
        left: 23px;
        &.khq {
          background: url("../assets/imgs/taskcenter/khq.png") no-repeat top;
          background-size: 100%;
        }
        &.pyq {
          background: url("../assets/imgs/taskcenter/pyq.png") no-repeat top;
          background-size: 100%;
        }
      }
      .text {
        font-family: 'PingFang SC';
        font-style: normal;
        font-weight: 400;
        font-size: 12px;
        line-height: 66px;
        color: #252D39;
        padding-left: 80px;
        &.active {
          color: #679BFF;
        }
      }
    }
  }
}
::v-deep(.el-table th.el-table__cell) {
  height: 46px;
  line-height: 22px;
  padding: 0;
}
.el-table th.el-table__cell {
  height: 46px;
  line-height: 22px;
  padding: 0;
}
.search-content {
  background-color: white;
  padding-top: 24px;
  border-radius: 8px;
}
.main-content {
  margin-top: 12px;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
}
</style>