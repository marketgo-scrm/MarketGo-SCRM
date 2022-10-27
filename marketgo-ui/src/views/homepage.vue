<template>
  <div>
    <main-head title="欢迎回到控制台！"></main-head>
    <el-row :gutter="12">
      <el-col :sm="24" :md="12" :lg="12">
        <div class="data-info">
          <img class="data-info-img" src="@/assets/user-icon.png">
          <div class="data-info-content">
            <div class="data-info-content-title">
              <div class="data-info-content-title-text">客户统计</div>
              <div class="data-info-content-title-time">
                更新时间：2022-07-21 09:30:00
              </div>
            </div>
            <div class="data-info-content-data">
              <div class="data-info-content-data-item">
                <div class="data-info-content-data-item-text">
                  客户总数
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    content="截止到当前时间的客户总数，注意这里不包含流失、删除客户关系数量。"
                  >
                    <i class="el-icon-question" slot="reference"></i>
                  </el-popover>
                </div>
                <div class="data-info-content-data-item-number">
                  {{statisticData.externalUserCount || 0}}
                  <span class="data-info-content-data-item-number-syb">人</span>
                </div>
              </div>
              <div class="data-info-content-data-item">
                <div class="data-info-content-data-item-text">
                  今日新增
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    content="今日内的新增客户数量，这里的新增是指等于添加好友的流水时间，则是有效新增，并且是去重的。
"
                  >
                    <i class="el-icon-question" slot="reference"></i>
                  </el-popover>
                </div>
                <div class="data-info-content-data-item-number">
                  {{statisticData.todayNewExternalUserCount || 0}}
                  <span class="data-info-content-data-item-number-syb">人</span>
                </div>
              </div>
              <div class="data-info-content-data-item">
                <div class="data-info-content-data-item-text">
                  今日流失
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    content="今日内的流失客户数量，这里的流失是指等于删除好友/被删除的流水时间，则是有效流失，并且是去重的。"
                  >
                    <i class="el-icon-question" slot="reference"></i>
                  </el-popover>
                </div>
                <div class="data-info-content-data-item-number">
                  {{statisticData.todayLossExternalUserCount || 0}}
                  <span class="data-info-content-data-item-number-syb">人</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :sm="24" :md="12">
        <div class="data-info">
          <img class="data-info-img" src="@/assets/group-icon.png">
          <div class="data-info-content">
            <div class="data-info-content-title">
              <div class="data-info-content-title-text">客户群统计</div>
              <div class="data-info-content-title-time">
                更新时间：2022-07-21 09:30:00
              </div>
            </div>
            <div class="data-info-content-data">
              <div class="data-info-content-data-item">
                <div class="data-info-content-data-item-text">
                  客户群总数
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    content="截止到当前时间的客户群总数。"
                  >
                    <i class="el-icon-question" slot="reference"></i>
                  </el-popover>
                </div>
                <div class="data-info-content-data-item-number">
                  {{statisticData.groupChatsCount || 0}}
                  <span class="data-info-content-data-item-number-syb">人</span>
                </div>
              </div>
              <div class="data-info-content-data-item">
                <div class="data-info-content-data-item-text">
                  今日入群人数
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    content="今日内的新入群的群成员数量。"
                  >
                    <i class="el-icon-question" slot="reference"></i>
                  </el-popover>
                </div>
                <div class="data-info-content-data-item-number">
                  {{statisticData.todayJoinGroupCount || 0}}
                  <span class="data-info-content-data-item-number-syb">人</span>
                </div>
              </div>
              <div class="data-info-content-data-item">
                <div class="data-info-content-data-item-text">
                  今日退群人数
                  <el-popover
                    placement="top-start"
                    title="标题"
                    width="200"
                    trigger="hover"
                    content="今日内的退群客户数量。"
                  >
                    <i class="el-icon-question" slot="reference"></i>
                  </el-popover>
                </div>
                <div class="data-info-content-data-item-number">
                  {{statisticData.todayQuitGroupCount || 0}}
                  <span class="data-info-content-data-item-number-syb">人</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="box-card">
      <el-form label-width="0" @submit.prevent.native>
        <el-row :gutter="10">
          <el-col :sm="12" :md="8">
            <el-form-item>
              <div class="box-card-title">客户统计图</div>
            </el-form-item>
          </el-col>
          <el-col :sm="12" :md="4">
            <el-form-item>
              <el-select
                size="small"
                @change="contactsStatisticDetail"
                v-model="params.date_type"
                placeholder="请选择"
              >
                <el-option label="按日" value="day"> </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :sm="24" :md="8">
            <el-form-item>
              <el-date-picker
                  style="width: 100%;border-radius: 40px;"
                  type="daterange"
                  v-model="times"
                  size="small"
                  align="right"
                  unlink-panels
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="yyyy-MM-dd"
                  :picker-options="pickerOptions"
                  @change="dateChange"
                >
                </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :sm="12" :md="4">
            <el-form-item>
              <el-select
                size="small"
                @change="contactsStatisticDetail"
                v-model="params.holiday_switch"
                placeholder="请选择"
              >
                <el-option label="包含节假日" value="1"> </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!-- metric_type: 'total', //统计的类型：total 总客户数 add 新增客户数 delete 流失客户数 -->
      <div>
        <el-radio-group
          class="data-tab"
          v-model="params.metric_type"
          size="small"
          @change="contactsStatisticDetail"
        >
          <el-radio-button label="total">总客户数</el-radio-button>
          <el-radio-button label="add">新增客户数</el-radio-button>
          <el-radio-button label="delete">流失客户数</el-radio-button>
        </el-radio-group>
      </div>
      <!-- 柱状图 -->
      <div id="myChart" style="height: 450px; width: 100%"></div>
    </el-card>
  </div>
</template>
<script>
export default {
  data() {
    return {
      // 时间选择
      times: [this.getDay(6),this.getDay(0)],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > new Date().getTime()
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
      // 统计列表
      statisticList: [],
      params: {
        date_type: "day",
        end_time: this.getDay(0),
        start_time: this.getDay(6),
        holiday_switch: "1",
        metric_type: "total", //统计的类型：total 总客户数 add 新增客户数 delete 流失客户数
      },
      statisticData: {
        externalUserCount: 0,
        groupChatsCount: 0,
        todayJoinGroupCount: 0,
        todayLossExternalUserCount: 0,
        todayNewExternalUserCount: 0,
        todayQuitGroupCount: 0,
      },
    };
  },
  mounted() {
    this.drawLine();
    this.contactsStatisticDetail();
    this.contactsStatistic();
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
      this.params.start_time = this.times[0];
      this.params.end_time = this.times[1];
      this.contactsStatisticDetail();
    },
    // 统计
    contactsStatistic() {
      this.$api
        .contactsStatistic()
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.statisticData = res.data;
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // 统计详情
    contactsStatisticDetail() {
      this.$api
        .contactsStatisticDetail(this.params)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.statisticList = res.data.detail;
            this.drawLine();
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // metric_type: 'total', //统计的类型：total 总客户数 add 新增客户数 delete 流失客户数
    metricFilter(type) {
      let text = ''
      switch(type) {
        case 'total':
          text = '总客户数'
          break;
        case 'add':
          text = '新增客户数'
          break;
        case 'delete':
          text = '流失客户数'
          break;
      }
      return text
    },
    drawLine() {
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById("myChart"));
      let option = {
        title: {
          text: "",
        },
        tooltip: {
          trigger: "axis",
        },
        // legend: {
        //   data: ["Email", "Union Ads", "Video Ads", "Direct", "Search Engine"],
        // },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        toolbox: {
          feature: {
            saveAsImage: {},
          },
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: this.statisticList.map((item) => item.todayTime),
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: this.metricFilter(this.params.metric_type),
            type: "line",
            stack: "Total",
            data: this.statisticList.map((item) => item.todayCount),
          },
        ],
      };
      console.log(333);
      // 绘制图表
      myChart.setOption(option);
      window.onresize = myChart.resize;
    },
  },
};
</script>
<style lang="scss" scoped>
.data-info {
  padding: 16px 22px;
  color: #fff;
  border-radius: 8px;
  background: #435ea0;
  display: flex;
  margin-bottom: 10px;
  .data-info-img {
    width: 75px;
    height: 75px;
    flex: none;
    margin-right: 26px;
    line-height: 75px;
    position: relative;
    top: 10px;
  }
  .data-info-content {
    flex: auto;
    font-family: PingFang SC;
    .data-info-content-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .data-info-content-title-text {
        font-weight: 600;
        font-size: 14px;
      }
      .data-info-content-title-time {
        opacity: 0.7;
        font-size: 12px;
      }
    }
    .data-info-content-data {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      .data-info-content-data-item {
        margin-top: 16px;
        .data-info-content-data-item-text {
          opacity: 0.7;
          font-size: 12px;
          margin-bottom: 6px;
          i {
            cursor: pointer;
          }
        }
        .data-info-content-data-item-number {
          font-family: Helvetica;
          font-size: 32px;
          line-height: 32px;
          .data-info-content-data-item-number-syb {
            font-size: 14px;
            line-height: 32px;
          }
        }
      }
    }
  }
}
::v-deep(.data-tab) {
  margin: 24px auto 0;
  display: table;
  .el-radio-button {
    &:first-child {
      .el-radio-button__inner {
        border-radius: 40px 0 0 40px;
      }
    }
    &:last-child {
      .el-radio-button__inner {
        border-radius: 0 40px 40px 0;
      }
    }
  }
}
::v-deep(.box-card) {
  margin-top: 20px;
  .box-card-title {
    font-weight: 600;
    font-size: 14px;
  }
  .el-select {
    width: 100%;
  }
  .el-input {
    width: 100%;
    .el-input__inner {
      border-radius: 40px;
    }
  }
}
</style>