<template>
  <div class="material-wrapper">
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
            <van-col span="18" class="title">标题</van-col>
            <van-col span="6" class="title">类型</van-col>
          </van-row>
          <van-row
            class="row"
            v-for="item in list"
            :key="item.id"
            @click="showDetail(item)"
          >
            <van-col span="18" class="cell img">
              <img
                v-if="item.materialType === 'IMAGE' && item.imageContent"
                :src="'data:image/Jpeg;base64,' + item.imageContent"
                alt=""
              />
              <svg
                v-if="item.materialType === 'mini'"
                width="20"
                height="36"
                viewBox="0 0 59 72"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M0 3.38824C0 1.51696 1.51696 0 3.38824 0L44.8941 0L58.4471 14.4V68.6118C58.4471 70.483 56.9301 72 55.0588 72H3.38823C1.51696 72 0 70.483 0 68.6118V3.38824Z"
                  fill="#FF6CA4"
                />
                <path
                  d="M45.0869 0L58.4474 14.4H48.4751C46.6039 14.4 45.0869 12.883 45.0869 11.0118L45.0869 0Z"
                  fill="#E64784"
                />
                <path
                  opacity="0.8"
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M49.1301 43.6207C49.1301 54.6144 40.218 63.5266 29.2242 63.5266C18.2305 63.5266 9.31836 54.6144 9.31836 43.6207C9.31836 32.627 18.2305 23.7148 29.2242 23.7148C40.218 23.7148 49.1301 32.627 49.1301 43.6207ZM45.945 43.6222C45.945 52.8569 38.4588 60.3431 29.224 60.3431C19.9893 60.3431 12.5031 52.8569 12.5031 43.6222C12.5031 34.3875 19.9893 26.9012 29.224 26.9012C38.4588 26.9012 45.945 34.3875 45.945 43.6222ZM23.0902 41.706L24.0487 44.3896C24.0487 44.3896 21.7484 45.3481 21.7484 47.6483C21.7484 49.9485 23.6653 51.0987 25.1988 51.0987C26.924 51.0987 28.6492 49.7568 28.6492 47.6483C28.6492 46.7232 28.2802 45.7242 27.866 44.603C27.3361 43.1686 26.7323 41.534 26.7323 39.5975C26.7323 36.1471 29.6076 33.2718 33.0579 33.2718C36.5083 33.2718 39.3836 36.1471 39.3836 39.5975C39.3836 43.8146 35.1665 45.5398 35.1665 45.5398L34.5914 42.6645C34.5914 42.6645 36.5083 41.5143 36.5083 39.5975C36.5083 37.8723 34.9748 36.1471 33.0579 36.1471C31.1411 36.1471 29.6076 37.8723 29.6076 39.5975C29.6076 40.3505 29.9728 41.4322 30.3844 42.6513C30.9158 44.2252 31.5244 46.028 31.5244 47.6483C31.5244 51.482 28.2658 53.9739 25.1988 53.9739C22.1318 53.9739 18.8732 51.6737 18.8732 47.6483C18.8732 43.6229 23.0902 41.706 23.0902 41.706Z"
                  fill="white"
                />
              </svg>

              <svg
                v-if="item.materialType === 'LINK'"
                width="20"
                height="36"
                viewBox="0 0 59 72"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M0 3.38824C0 1.51696 1.51696 0 3.38824 0L44.8941 0L58.4471 14.4V68.6118C58.4471 70.483 56.9301 72 55.0588 72H3.38823C1.51696 72 0 70.483 0 68.6118V3.38824Z"
                  fill="#FF8A1D"
                />
                <path
                  d="M45.0869 0L58.4474 14.4H48.4751C46.6039 14.4 45.0869 12.883 45.0869 11.0118L45.0869 0Z"
                  fill="#E6770F"
                />
                <path
                  opacity="0.8"
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M15.2471 20.332L17.6505 28.8026H19.116L20.7574 22.4319H20.8043L22.434 28.8026H23.8877L26.3029 20.332H24.7553L23.1843 26.7739H23.1374L21.4843 20.332H20.0657L18.4126 26.7739H18.3657L16.7947 20.332H15.2471ZM27.1837 20.332V28.8026H33.4209V27.6163H28.5554V25.0537H32.9402V23.8674H28.5554V21.5184H33.2216V20.332H27.1837ZM34.7059 28.8026V20.332H38.5397C39.3721 20.332 40.0287 20.5337 40.4976 20.9371C40.9314 21.3167 41.1542 21.8268 41.1542 22.4675C41.1542 22.942 41.0369 23.3454 40.8142 23.6776C40.5914 23.986 40.2749 24.2114 39.8528 24.3775C40.4038 24.4843 40.8142 24.7097 41.0955 25.0537C41.3652 25.3859 41.5059 25.8486 41.5059 26.418C41.5059 27.2722 41.2128 27.901 40.6383 28.3044C40.1459 28.6365 39.4424 28.8026 38.5514 28.8026H34.7059ZM38.1997 21.4709H36.0776V23.8911H38.1762C38.7624 23.8911 39.1728 23.7843 39.419 23.5945C39.6535 23.3928 39.7824 23.0725 39.7824 22.6336C39.7824 22.2302 39.6535 21.9336 39.419 21.7557C39.1611 21.5658 38.7624 21.4709 38.1997 21.4709ZM38.3873 25.03H36.0776V27.6637H38.3521C38.868 27.6637 39.2783 27.5807 39.5714 27.4146C39.9466 27.201 40.1342 26.8689 40.1342 26.3943C40.1342 25.9079 39.9935 25.5639 39.7238 25.3503C39.4424 25.1368 38.9969 25.03 38.3873 25.03ZM16.8935 39.015C15.0486 40.86 15.0486 43.8512 16.8935 45.6961L23.7912 52.5938L26.7142 49.6708L25.013 47.9695L23.7912 49.1913L18.6257 44.0258C17.7031 43.1033 17.7032 41.6077 18.6257 40.6852L21.4868 37.8241C22.4093 36.9016 23.9048 36.9016 24.8274 37.8241L29.9929 42.9896L28.7711 44.2114L30.4724 45.9127L33.3954 42.9897L26.4977 36.092C24.6528 34.247 21.6615 34.247 19.8165 36.092L16.8935 39.015ZM41.3757 57.6511C43.2206 55.8061 43.2206 52.8149 41.3757 50.9699L34.478 44.0722L31.555 46.9952L33.2562 48.6965L34.478 47.4748L39.6435 52.6403C40.5661 53.5628 40.566 55.0584 39.6435 55.9809L36.7824 58.842C35.8599 59.7645 34.3644 59.7645 33.4418 58.842L28.2763 53.6765L29.4981 52.4547L27.7968 50.7534L24.8738 53.6764L31.7715 60.5741C33.6164 62.419 36.6077 62.4191 38.4527 60.5741L41.3757 57.6511ZM22.7859 44.0722L24.8738 41.9844L35.7307 52.8413L33.6428 54.9291L22.7859 44.0722Z"
                  fill="white"
                />
              </svg>

              <svg
                v-if="item.materialType === 'TEXT'"
                width="20"
                height="36"
                viewBox="0 0 59 72"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M0 3.38824C0 1.51696 1.51696 0 3.38824 0L44.8941 0L58.4471 14.4V68.6118C58.4471 70.483 56.9301 72 55.0588 72H3.38823C1.51696 72 0 70.483 0 68.6118V3.38824Z"
                  fill="#4598D9"
                />
                <path
                  d="M45.0869 0L58.4474 14.4H48.4751C46.6039 14.4 45.0869 12.883 45.0869 11.0118L45.0869 0Z"
                  fill="#217CC2"
                />
                <path
                  opacity="0.8"
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M18.6354 21.5184V20.332H25.2804V21.5184H22.6224V28.8026H21.2934V21.5184H18.6354ZM28.4068 24.425L25.692 20.332H27.3163L29.2246 23.3217L31.1329 20.332H32.7573L30.0197 24.425L32.939 28.8026H31.3147L29.2246 25.5283L27.1346 28.8026H25.5102L28.4068 24.425ZM33.1669 20.332V21.5184H35.8249V28.8026H37.1539V21.5184H39.8119V20.332H33.1669ZM15.2472 37.6967C15.2472 36.5272 16.1953 35.5791 17.3648 35.5791H47.8589C49.0285 35.5791 49.9766 36.5272 49.9766 37.6967C49.9766 38.8663 49.0285 39.8144 47.8589 39.8144H17.3648C16.1953 39.8144 15.2472 38.8663 15.2472 37.6967ZM10.5883 43.2026C9.41881 43.2026 8.4707 44.1507 8.4707 45.3203C8.4707 46.4898 9.41881 47.4379 10.5883 47.4379H47.8589C49.0285 47.4379 49.9766 46.4898 49.9766 45.3203C49.9766 44.1507 49.0285 43.2026 47.8589 43.2026H10.5883ZM8.4707 52.9438C8.4707 51.7743 9.41881 50.8261 10.5883 50.8261H47.8589C49.0285 50.8261 49.9766 51.7743 49.9766 52.9438C49.9766 54.1133 49.0285 55.0614 47.8589 55.0614H10.5883C9.41881 55.0614 8.4707 54.1133 8.4707 52.9438ZM10.5883 58.4497C9.41881 58.4497 8.4707 59.3978 8.4707 60.5673C8.4707 61.7369 9.41881 62.685 10.5883 62.685H47.8589C49.0285 62.685 49.9766 61.7369 49.9766 60.5673C49.9766 59.3978 49.0285 58.4497 47.8589 58.4497H10.5883Z"
                  fill="white"
                />
              </svg>

              {{ item.title }}</van-col
            >
            <van-col span="6" class="cell"
              >{{ getTypeText(item.materialType) }}
            </van-col>
          </van-row>
        </van-list>
      </van-cell-group>
      <materialDetail :params="material"></materialDetail>
    </template>
    <listEmpty v-else></listEmpty>
  </div>
</template>

<script>
import "vant/es/toast/style";
import { wecom } from "../../api/wecom";
import qs from "qs";
import materialDetail from "./materialDetail.vue";
import listEmpty from "../components/listEmpty.vue";
export default {
  components: {
    materialDetail,
    listEmpty,
  },
  data() {
    return {
      list: [],
      query: "",
      searchVal: "",
      loading: false,
      finished: false,
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
    this.getList()
  },
  methods: {
    getList() {
      const params = Object.assign({ title: this.searchVal }, this.query);
      wecom.materialList(params).then((res) => {
        this.list = res.data.list || [];
        this.loading = false;
        this.finished = true;
      });
    },
    getTypeText(type) {
      const typeMap = {
        IMAGE: "图片",
        LINK: "网页",
        TEXT: "文本",
        minin: "小程序",
      };
      return typeMap[type];
    },
    async showDetail(item) {
      const res = await wecom.materialDetail({ material_uuid: item.uuid });
      this.material.visible = true;
      this.material.data = res.data;
    },
  },
  computed: {},
};
</script>

<style lang="less" scoped>
.material-wrapper {
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
        &.img {
          display: flex;
          align-items: center;
          img {
            height: 36px;
            margin-right: 10px;
          }
          svg {
            margin-right: 10px;
          }
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
}
</style>
