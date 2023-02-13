<template>
  <div>
    <main-head title="群发客户">
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
            新建群发客户
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
            <el-divider direction="vertical"></el-divider>
            <el-button @click="openDetails(row)" size="small" type="text">详情</el-button>
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
      dataList: [],
      creators: [],//创建人
    }
  },
  mounted() {
    this.searchData()
    this.getCreators()
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
            `mktgo/wecom/mass_task/delete?project_id=${this.$store.state.projectUuid}&task_type=SINGLE&task_uuid=${row.uuid}`,
            {}).then(function (res) {
          console.log(res)
          if (res.code == 0) {
            _this.$message({
              message: '删除成功',
              type: 'success'
            });
            _this.searchData()
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
          `mktgo/wecom/mass_task/remind?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=SINGLE&task_uuid=${row.uuid}`,
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
          `mktgo/wecom/mass_task/creators?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=SINGLE`,
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
      }
      queryStr += `&page_num=${page_num ? page_num : this.page_num}`
      queryStr += `&page_size=${this.page_size}`
      let data = await this.$http.get(
          `mktgo/wecom/mass_task/list?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=SINGLE`+queryStr,
          {});
      console.log(data)
      if (data.code == 0) {
        this.dataList = data.data.list
        this.total = data.data.totalCount
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
      this.$router.push('/index/masscustomer-add')
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
      }
      // this.getList()
    }
  }
}
</script>
<style scoped lang="scss">
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