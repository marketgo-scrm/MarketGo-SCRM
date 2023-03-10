<template>
  <div class="task-center">
    <van-nav-bar title="全部任务" />
    <van-cell-group style="background: transparent">
      <van-dropdown-menu>
        <van-dropdown-item v-model="filter.statuses" :options="statusOptions" />
        <van-dropdown-item v-model="filter.task_types" :options="typeOptions" />
      </van-dropdown-menu>
    </van-cell-group>
    <van-cell-group class="list">
      <van-cell
        is-link
        @click="toDetail(task)"
        size="large"
        class="card"
        v-for="task of list"
        :key="task.id"
      >
        <template #title>
          <span class="lbl">{{ task.name }}</span>
          <van-tag plain type="primary" class="tag">{{
            getScheduleTypeName(task.scheduleType)
          }}</van-tag>
        </template>
        <template #label>
          <div style="margin-top: 10px">{{ task.planTime }}</div>
          <div style="margin-top: 10px">
            {{ getTaskTypeName(task.taskType) }}
          </div>
        </template>
      </van-cell>
    </van-cell-group>
  </div>
</template>

<script>
import "vant/es/toast/style";
import { welcom } from "../../api/welcom";
import qs from "qs";
export default {
  data() {
    return {
      list: [],
      query: "",
      statusOptions: [
        { value: "", text: "全部状态" },
        { value: "todo", text: "未完成" },
      ],
      typeOptions: [
        { value: "", text: "全部类型" },
        { value: "SINGLE", text: "客户任务" },
        { value: "GROUP", text: "客户群任务" },
        { value: "MOMENT", text: "朋友圈" },
      ],
      filter: {
        task_types: "",
        statuses: "",
      },
    };
  },
  created() {
    const query = this.$route.query;
    this.query = qs.parse(query);
    welcom.taskList(query).then((res) => {
      this.list = res.data.list || [];
    });
  },

  methods: {
    getScheduleTypeName(type) {
      const map = {
        IMMEDIATE: "单次",
        REPEAT_TIME: "定时重复",
      };
      return map[type];
    },
    getTaskTypeName(type) {
      let find = this.typeOptions.find((item) => item.value === type);
      return find ? find.text : type;
    },
    toDetail(task) {
      if (task.scheduleType === "IMMEDIATE") {
        this.$router.push({
          name: "detail",
          query: Object.assign(
            {
              task_uuid: task.taskUuid,
              uuid: task.uuid,
              from:'list'
            },
            this.query
          ),
        });
      } else {
        this.$router.push({
          name: "subList",
          query: Object.assign(
            {
              task_uuid: task.taskUuid,
            },
            this.query
          ),
        });
      }
    },
  },
  computed: {},
};
</script>

<style lang="less" scoped>
.task-center {
  font-family: "PingFang SC";
  background: #f5f7fa; //#D8D8D8;
  color: #333333;
  margin-bottom: 100px;

  /deep/.van-nav-bar__content {
    background: #679bff;

    .van-nav-bar__title {
      color: #fff;
    }
  }

  .list {
    margin-top: 20px;
    background: transparent;

    .card {
      &:not(:first-child) {
        margin-top: 10px;
      }

      /deep/.van-cell__right-icon {
        position: absolute;
        right: 10px;
        top: 50%;
        transform: translateY(-50%);
      }
      .lbl {
        font-weight: bold;
      }

      .tag {
        margin-left: 20px;
        color: #999;
        padding: 2px 4px;
      }
    }
  }
}
</style>
