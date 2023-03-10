<template>
  <div class="task-repeat-center">
    <van-nav-bar :left-arrow="true" @click-left="back" title="重复任务列表" />
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
          <van-tag
            plain
            type="primary"
            :class="['tag', { todo: task.taskStatus !== 'COMPUTED' }]"
            >{{ task.taskStatus === "COMPUTED" ? "已完成" : "未完成" }}</van-tag
          >
        </template>
        <template #label>
          <div
            style="margin-top: 10px"
            :class="{ todo: task.taskStatus !== 'COMPUTED' }"
          >
            {{ task.planTime }}
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
    welcom.taskSubList(query).then((res) => {
      this.list = res.data.list || [];
    });
  },

  methods: {
    back() {
      this.$router.go(-1);
    },
    toDetail(task) {
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
    },
  },
  computed: {},
};
</script>

<style lang="less" scoped>
.task-repeat-center {
  font-family: "PingFang SC";
  background: #f5f7fa; //#D8D8D8;
  color: #333333;
  margin-bottom: 100px;

  /deep/.van-nav-bar__content {
    background: #679bff;

    .van-nav-bar__title,
    .van-icon {
      color: #fff;
    }
  }
  .list {
    margin-top: 10px;
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
        color: #79cd6b;
        padding: 2px 4px;
        &.todo {
          color: #ff993c;
        }
      }
      .todo {
        color: #ff993c;
      }
    }
  }
}
</style>
