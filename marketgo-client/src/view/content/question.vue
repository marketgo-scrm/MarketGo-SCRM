<template>
  <div class="question-wrapper">
    <template v-if="!searchVal && list && list.length > 0">
      <van-cell-group style="background: transparent">
        <div class="search">
          <van-search
            v-model="searchVal"
            shape="round"
            placeholder="请输入搜索关键词"
            background="#f8f8f8"
            style="width: 100%"
            @search="getList"
            @clear="getList"
          />
        </div>
      </van-cell-group>

      <van-cell-group class="list">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text=""
          @load="getList"
          ><van-row class="row">
            <van-col span="12" class="title">问题</van-col>
            <van-col span="12" class="title">关键词</van-col>
          </van-row>
          <van-row class="row" v-for="item in list" :key="item.id">
            <van-col span="12" class="cell">{{ item.title }}</van-col>
            <van-col span="12" class="cell"
              ><span>{{
                item.keywords && item.keywords[0] ? item.keywords[0] : "-"
              }}</span>
              <span
                v-if="item.keywords && item.keywords.length > 1"
                class="all"
                @click="showAll(item)"
                >全部</span
              >
            </van-col>
          </van-row>
        </van-list>
        <van-popup v-model:show="tagsShow" round>
          <div class="keywords-wrapper">
            <div class="title">全部关键词</div>
            <div>
              <span class="keyword">{{ keywords.join("、") }}</span>
            </div>
            <div style="margin-top: 15px">
              <van-button type="primary" block @click="tagsShow = false"
                >确定</van-button
              >
            </div>
          </div>
        </van-popup>
      </van-cell-group>
    </template>
    <listEmpty v-else></listEmpty>
  </div>
</template>

<script>
import "vant/es/toast/style";
import { wecom } from "../../api/wecom";
import qs from "qs";
import listEmpty from "../components/listEmpty.vue";
export default {
  components: { listEmpty },
  data() {
    return {
      list: [],
      query: "",
      searchVal: "",
      loading: false,
      finished: false,
      tagsShow: false,
      keywords: [],
    };
  },
  created() {
    const query = this.$route.query;
    this.query = Object.assign(qs.parse(query), {
      page_num: 1,
      page_size: 9999,
    });
    this.getList()
  },
  methods: {
    getList() {
      const params = Object.assign({ keyword: this.searchVal }, this.query);
      wecom.questionList(params).then((res) => {
        this.list = res.data.list || [];
        this.loading = false;
        this.finished = true;
      });
    },
    showAll(item) {
      this.keywords = item.keywords || [];
      this.tagsShow = true;
    },
  },
  computed: {},
};
</script>

<style lang="less" scoped>
.question-wrapper {
  font-family: "PingFang SC";
  background: #f5f7fa; //#D8D8D8;
  color: #333333;
  margin-bottom: 100px;
  position: relative;
  .search {
    display: flex;
    align-items: center;
    :deep(.van-search__content) {
      background: #fff;
    }
  }

  .list {
    .row {
      padding: 0 20px;
      background: #fff;
      border-bottom: 1px solid #e7e7e7;
      line-height: 40px;
      color: #999;
      .title {
        font-size: 14px;
      }
      .cell {
        color: #333;
        .all {
          float: right;
          color: #679bff;
          cursor: pointer;
        }
      }
    }
  }
  .keywords-wrapper {
    padding: 20px 15px;
    min-width: 50vh;
    .title {
      font-weight: 600;
      margin-bottom: 15px;
    }
    .keyword {
      margin-bottom: 10px;
    }
  }
}
</style>
