<template>
  <div class="task-center">
    <van-nav-bar title="全部任务" />
    <van-cell-group style="background: transparent">
      <van-dropdown-menu>
        <van-dropdown-item
          v-model="filter.statuses"
          :options="statusOptions"
          @change="getList"
        />
        <van-dropdown-item
          v-model="filter.task_types"
          :options="typeOptions"
          @change="getList"
        />
      </van-dropdown-menu>
    </van-cell-group>
    <van-cell-group class="list">
      <van-loading color="#1989fa" v-if="loading" />
      <van-cell
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
          <div style="margin-top: 10px">
            {{ task.planTime ? task.planTime : "-" }}
          </div>
          <div style="margin-top: 10px">
            {{ getTaskTypeName(task.taskType) }}
          </div>
        </template>
      </van-cell>
    </van-cell-group>
    <listEmpty v-if="list && list.length <= 0"></listEmpty>
  </div>
</template>

<script>
import "vant/es/toast/style";
import { wecom } from "../../api/wecom";
import qs from "qs";
import listEmpty from "../components/listEmpty.vue";
export default {
  components: {
    listEmpty,
  },
  data() {
    return {
      list: [],
      query: "",
      statusOptions: [
        { value: "", text: "全部状态" },
        { value: "UNSENT", text: "未完成" },
        { value: "SENT", text: "已完成" },
      ],
      typeOptions: [
        { value: "", text: "全部类型" },
        { value: "SINGLE", text: "客户任务" },
        { value: "GROUP", text: "客户群任务" },
        { value: "MOMENT", text: "朋友圈" },
      ],
      loading: false,
      filter: {
        task_types: "",
        statuses: "UNSENT",
      },
    };
  },
  created() {
    const query = this.$route.query;
    this.query = qs.parse(query);
    this.getList();
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
    getList() {
      this.loading = true;
      const query = { ...this.query, ...this.filter };
      if (localStorage.getItem("user")) {
        query.member_id = JSON.parse(localStorage.getItem("user")).userId;
      }

      wecom
        .taskList(query)
        .then((res) => {
          this.list = (res.data && res.data.list) || [];
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    toDetail(task) {
      if (task.scheduleType === "IMMEDIATE") {
        this.$router.push({
          name: "detail",
          query: Object.assign(
            {
              task_uuid: task.taskUuid,
              uuid: task.uuid,
              taskType:task.taskType,
              from: "list",
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
  :deep(.van-nav-bar__content) {
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

      :deep(.van-cell__right-icon) {
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
        position: absolute;
        right: 20px;
      }
    }
  }
}
</style>
