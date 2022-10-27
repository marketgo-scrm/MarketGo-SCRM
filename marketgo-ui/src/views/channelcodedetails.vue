<template>
  <div>
    <main-head back title="活码统计"></main-head>
    <el-card class="box-card">
      活码名称：
      {{$route.query.name}}
    </el-card>
    <el-card class="box-card customer-statistics">
      <div class="customer-statistics-title">
        客户统计
        <span class="customer-statistics-title-time"
          >更新时间：{{ summary.lastStatisticsTime }}</span
        >
      </div>
      <div class="customer-statistics-data">
        <el-row :gutter="20">
          <el-col :md="4" :sm="6" :xs="12">
            <div class="customer-statistics-data-item">
              <div class="customer-statistics-data-item-title">
                今日新增客户数
              </div>
              <div class="customer-statistics-data-item-number">
                {{ summary.dailyIncreasedExtUserCount || 0 }}
              </div>
            </div>
          </el-col>
          <el-col :sm="4" :xs="12">
            <div class="customer-statistics-data-item">
              <div class="customer-statistics-data-item-title">
                今日流失客户数
              </div>
              <div class="customer-statistics-data-item-number">
                {{ summary.dailyDecreaseExtUserCount || 0 }}
              </div>
            </div>
          </el-col>
          <el-col :sm="4" :xs="12">
            <div class="customer-statistics-data-item">
              <div class="customer-statistics-data-item-title">客户总数</div>
              <div class="customer-statistics-data-item-number">
                {{ summary.totalExtUserCount || 0 }}
              </div>
            </div>
          </el-col>
          <el-col :sm="4" :xs="12">
            <div class="customer-statistics-data-item">
              <div class="customer-statistics-data-item-title">
                流失客户总数
              </div>
              <div class="customer-statistics-data-item-number">
                {{ summary.totalDecreaseExtUserCount || 0 }}
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    <div class="card-box">
      <!-- MEMBER 按照员工； DATE 日期 -->
      <el-tabs
        @tab-click="liveCodeStatisticsList()"
        v-model="parameter.statistics_type"
      >
        <el-tab-pane label="按日期" name="DATE"></el-tab-pane>
        <el-tab-pane label="按员工" name="MEMBER"></el-tab-pane>
      </el-tabs>
      <el-form label-width="0" @submit.prevent.native>
        <el-row style="padding: 0 24px" :gutter="14">
          <!-- <el-col :span="4" :offset="12">
            <el-form-item>
              <el-select
                style="width: 100%"
                size="small"
                v-model="value"
                placeholder="请选择"
              >
                <el-option label="item.label" value="item.value"> </el-option>
              </el-select>
            </el-form-item>
          </el-col> -->
          <el-col :span="8" :offset="16">
            <el-form-item>
              <el-date-picker
                style="width: 100%"
                v-model="times"
                type="daterange"
                align="right"
                unlink-panels
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :picker-options="pickerOptions"
                @change="dateChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-table :data="statisticsList">
        <template v-if="parameter.statistics_type === 'DATE'">
          <el-table-column label="日期" prop="addExtUserDate" />
        </template>
        <template v-if="parameter.statistics_type === 'MEMBER'">
          <el-table-column label="员工名称" prop="memberName" />
        </template>
        <el-table-column label="客户总数" prop="extUserCount" />
        <el-table-column label="新增客户数" prop="increasedExtUserCount" />
        <el-table-column label="流失客户数" prop="decreaseExtUserCount" />
      </el-table>
      <el-pagination
        :current-page="parameter.page_num"
        :page-size="parameter.page_size"
        :total="total"
        background
        layout="total,  prev, pager, next, sizes, jumper"
        :page-sizes="[10, 20, 30]"
        @size-change="pageSizeChange"
        @current-change="liveCodeStatisticsList"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "channelcodedetails",
  data() {
    let createTime = this.$route.query.createTime
    return {
      times: [],
      // 活码数据详情汇总
      summary: {
        dailyDecreaseExtUserCount: 0,
        dailyIncreasedExtUserCount: 0,
        lastStatisticsTime: "string",
        totalDecreaseExtUserCount: 0,
        totalExtUserCount: 0,
      },
      // 活码数据统计详情
      parameter: {
        channel_uuid: this.$route.query.uuid,
        page_num: 1,
        page_size: 20,
        statistics_type: "DATE",
        start_time: '',
        end_time: '',
      },
      total: 0,
      statisticsList: [],
      pickerOptions: {
        disabledDate(time) {
          
          console.log(77,time.getTime(),new Date(createTime).getTime())
          // return time.getTime() < Date.now() - 24 * 60 * 60 * 1000;
          return time.getTime() < new Date(createTime).getTime()|| time.getTime() > new Date().getTime()
        },
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
    };
  },
  created() {
    if (this.$route.query.uuid) {
      this.liveCodeStatisticsSummary();
      this.liveCodeStatisticsList();
    } else {
      this.$message("数据错误");
      setTimeout(() => {
        this.$router.go(-1);
      }, 1000);
    }
  },
  methods: {
    getDay(day) {
      var time = new Date().getTime() - day * 24 * 60 * 60 * 1000;
      var tragetTime = new Date(time);
      tragetTime =
        tragetTime.getFullYear() +
        "-" +
        (Number(tragetTime.getMonth() + 1) > 9
          ? tragetTime.getMonth() + 1
          : "0" + (tragetTime.getMonth() + 1)) +
        "-" +
        (Number(tragetTime.getDate()) > 9
          ? tragetTime.getDate()
          : "0" + tragetTime.getDate());
      return tragetTime;
    },
    dateChange() {
      this.parameter.start_time = this.times[0];
      this.parameter.end_time = this.times[1];
      this.liveCodeStatisticsList();
    },
    // 调整页数
    pageSizeChange(e) {
      console.log(e);
      this.parameter.page_size = Number(e);
      this.liveCodeStatisticsList();
    },
    // 活码数据统计详情
    liveCodeStatisticsList(page) {
      this.parameter.page_num = page || 1;
      let data = {
        channel_uuid: this.$route.query.uuid,
        page_num: this.parameter.page_num,
        page_size: this.parameter.page_size,
        statistics_type: this.parameter.statistics_type,
      };
      if (this.parameter.start_time) {
        data.start_time = this.parameter.start_time;
      }
      if (this.parameter.end_time) {
        data.end_time = this.parameter.end_time;
      }
      this.$api
        .liveCodeStatisticsList(data)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.statisticsList = res.data.detail;
            this.total = res.data.total;
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // 活码数据详情汇总
    liveCodeStatisticsSummary() {
      this.$api
        .liveCodeStatisticsSummary({
          channel_uuid: this.$route.query.uuid,
        })
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.summary = res.data;
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep(.el-form-item) {
  margin-bottom: 10px;
}
::v-deep(.el-tabs) {
  padding-top: 10px;
  .el-tabs__nav {
    margin-left: 24px;
  }
}
.card-box {
  margin-top: 12px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.customer-statistics {
  margin-top: 12px;
  .customer-statistics-title {
    color: #333333;
    font-family: PingFang SC;
    font-weight: 600;
    font-size: 14px;
    .customer-statistics-title-time {
      color: #999999;
      font-family: PingFang SC;
      font-size: 12px;
      margin-left: 12px;
    }
  }
  .customer-statistics-data {
    margin-top: 24px;
    .customer-statistics-data-item {
      .customer-statistics-data-item-title {
        color: #999999;
        font-family: PingFang SC;
        font-size: 12px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .customer-statistics-data-item-number {
        margin-top: 12px;
        color: #333333;
        font-family: Helvetica;
        font-size: 32px;
        line-height: 32px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
}
</style>