<template>
  <div class="chat-script-wrapper">
    <template v-if="!searchVal && list && list.length > 0">
      <van-cell-group style="background: transparent">
        <div class="search">
          <van-search
            v-model="searchVal"
            shape="round"
            placeholder="请输入搜索关键词"
            background="#f8f8f8"
            style="width: 90%"
            @search="getList"
            @clear="getList"
          />
          <svg
            @click="show = true"
            width="24"
            height="28"
            viewBox="0 0 36 40"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M23.7917 14.8777C23.6101 15.0645 23.5086 15.3148 23.5087 15.5754L23.5222 38.8712L12.514 35.1161L12.4819 15.5732C12.4815 15.3128 12.3795 15.0628 12.1976 14.8764L1.49167 3.90497C0.434475 2.82156 1.20209 1 2.71584 1H33.2354C34.7476 1 35.5159 2.81845 34.4618 3.90272L23.7917 14.8777ZM23.949 39.0167L23.9484 39.0165L23.949 39.0167ZM12.296 35.0417C12.2965 35.0419 12.2969 35.0421 12.2974 35.0422L12.296 35.0417Z"
              stroke="#999999"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </div>
      </van-cell-group>

      <van-cell-group class="list">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text=""
          @load="getList"
          ><van-row class="row">
            <van-col span="12" class="title">标题</van-col>
            <van-col span="12" class="title">标签</van-col>
          </van-row>
          <van-row class="row" v-for="item in list" :key="item.id"  @click="showDetail(item)">
            <van-col span="12" class="cell">{{ item.title }}</van-col>
            <van-col span="12" class="cell"
              ><van-tag
                round
                plain
                color="#E1E1E1"
                text-color="#333"
                size="large"
                v-if="item.tags[0]"
                >{{ item.tags[0] ? item.tags[0].name : "-" }}</van-tag
              ><span v-else>-</span>
              <span v-if="item.tags[0]" class="all" @click.stop="showAll(item)"
                >全部</span
              >
            </van-col>
          </van-row>
        </van-list>
        <van-popup v-model:show="tagsShow" round>
          <div class="tags-wrapper">
            <div class="title">全部标签</div>
            <div>
              <van-tag
                class="tag"
                round
                plain
                color="#E1E1E1"
                text-color="#333"
                size="large"
                v-for="tag of tags"
                :key="tag.id"
                >{{ tag.name }}</van-tag
              >
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
    <van-overlay :show="show" @click="show = false">
      <div class="overlay-wrapper" @click.stop>
        <div class="block">
          <van-nav-bar
            :left-arrow="true"
            @click-left="show = false"
            title="选择条件"
          />

          <div class="top">
            已选<span style="padding: 0 10px">{{
              selectCreatorIds.length
            }}</span
            >人<span style="padding-left: 20px"
              >标签<span style="padding: 0 10px">{{
                selectTagIds.length
              }}</span></span
            >
            <span class="clear" @click="clear">清空</span>
          </div>
          <van-divider />
          <van-row>
            <van-col span="12" class="hd-title">创建人</van-col>
            <van-col span="12" class="hd-title">标签</van-col>
          </van-row>
          <van-divider />
          <van-row style="padding: 0 20px; height: 100%">
            <van-col span="12" class="left-creator">
              <van-cell
                v-for="creator of creators"
                :key="creator.creatorId"
                @click="clickCreator(creator.creatorId)"
              >
                <template #title>
                  <div>
                    <span class="custom-title">{{ creator.creatorName }}</span>
                    <van-icon
                      class="check-icon"
                      name="success"
                      v-show="selectCreatorIds.indexOf(creator.creatorId) != -1"
                    />
                  </div>
                </template>
              </van-cell>
            </van-col>
            <van-col span="12" class="right-tag"
              ><van-tag
                round
                :color="
                  selectTagIds.indexOf(tag.uuid) != -1 ? '#F1F6FF' : '#F9F9F9'
                "
                :text-color="
                  selectTagIds.indexOf(tag.uuid) != -1 ? '#679BFF' : '#333'
                "
                size="large"
                v-for="tag of tags"
                :key="tag.id"
                @click="clickTag(tag.uuid)"
                >{{ tag.name }}</van-tag
              ></van-col
            >
          </van-row>
          <div class="bottom-btn">
            <van-button type="primary" style="width: 80%" @click="search"
              >确定</van-button
            >
          </div>
        </div>
      </div>
    </van-overlay>
    <materialDetail :params="material"></materialDetail>
  </div>
</template>

<script>
import "vant/es/toast/style";
import { wecom } from "../../api/wecom";
import qs from "qs";
import listEmpty from "../components/listEmpty.vue";
import materialDetail from "./materialDetail.vue";
export default {
  components: { materialDetail, listEmpty },
  data() {
    return {
      show: false,
      list: [],
      query: "",
      searchVal: "",
      loading: false,
      finished: false,
      tagsShow: false,
      tags: [
        {
          id: 70,
          uuid: "5714d07fdf3b4e1ca89947f19e3e9dfe",
          name: "的答复",
          orders: 1,
        },
      ],
      creators: [],
      selectCreatorIds: [],
      selectTagIds: [],
      material: {
        visible: false,
        data: {},
      },
    };
  },
  created() {
    const query = this.$route.query;
    this.query = Object.assign(qs.parse(query), {
      page_num: 1,
      page_size: 9999,
    });
    wecom.chatScriptCreators(this.query).then((res) => {
      this.creators = res.data.creators || [];
    });
    wecom.tagList({ type: "CHAT_SCRIPT", ...this.query }).then((res) => {
      //   this.creators = res.data.creators || [];
    });
    this.search();
  },
  methods: {
    search() {
      this.show = false;
      this.getList();
    },
    getList() {
      const params = Object.assign({ title: this.searchVal }, this.query);
      if (this.selectCreatorIds.length !== 0) {
        params.creator_ids = this.selectCreatorIds.join(",");
      }
      if (this.selectTagIds.length !== 0) {
        params.tag_ids = this.selectTagIds.join(",");
      }
      wecom.chatScriptList(params).then((res) => {
        this.list = res.data.list || [];
        this.loading = false;
        this.finished = true;
      });
    },
    showAll(item) {
      this.tags = item.tags || [];
      this.tagsShow = true;
    },
    clickCreator(id) {
      const idx = this.selectCreatorIds.indexOf(id);
      console.log(idx);
      if (idx === -1) {
        this.selectCreatorIds.push(id);
      } else {
        this.selectCreatorIds.splice(idx, 1);
      }
    },
    clickTag(uuid) {
      const idx = this.selectTagIds.indexOf(uuid);
      if (idx === -1) {
        this.selectTagIds.push(uuid);
      } else {
        this.selectTagIds.splice(idx, 1);
      }
    },
    clear() {
      this.selectCreatorIds = [];
      this.selectTagIds = [];
    },
    async showDetail(item) {
      const res = await wecom.chatScriptDetail({ uuid: item.uuid });
      this.material.visible = true;
      this.material.data ={...res.data,...res.data.content[0]};
      this.material.descTxt='文本内容'
    },
  },
  computed: {},
};
</script>

<style lang="less" scoped>
.chat-script-wrapper {
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
  .tags-wrapper {
    padding: 20px 15px;
    min-width: 50vh;
    .title {
      font-weight: 600;
      margin-bottom: 15px;
    }
    .tag {
      margin-bottom: 10px;
    }
  }
  .overlay-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }

  .block {
    width: 100%;
    height: 100%;
    background-color: #fff;
    .top {
      position: relative;
      padding: 16px 20px 0;
      color: #999;
      .clear {
        color: #679bff;
        position: absolute;
        right: 20px;
      }
    }
    .hd-title {
      font-weight: 400px;
      padding-left: 20px;
    }
    .left-creator {
      position: relative;
      height: calc(100% - 250px);
      &::before {
        content: "";
        position: absolute;
        height: 200%;
        width: 1px;
        background: #ebedf0;
        right: 0;
        top: -16px;
      }
      :deep(.van-cell) {
        padding: 0;
        width: 99%;
      }
      .check-icon {
        float: right;
        margin-right: 20px;
        color: #0fee0f;
      }
    }
    .right-tag {
      padding-left: 20px;
    }
    .bottom-btn {
      position: fixed;
      bottom: 0;
      padding: 20px;
      background: #fff;
      box-shadow: 0px -4px 5px rgba(0, 0, 0, 0.08);
      width: 100%;
      text-align: center;
    }
  }
}
</style>
