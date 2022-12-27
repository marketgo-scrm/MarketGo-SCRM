<template>
  <div class="cdp-list-wrapper">
    <div class="tops"><h2>数据接入</h2></div>
    <div class="content">
      <el-row class="row" :gutter="20" v-for="(row, idx) of list" :key="idx">
        <el-col :span="8" v-for="cdp of row" :key="cdp.cdpType">
          <div
            :class="['grid-content', { enable: cdp.switchStatus }]"
            @click="
              cdp.configStatus
                ? setCdpStatus(cdp.cdpType, !cdp.switchStatus)
                : ''
            "
          >
            <div class="icon">
              <img :src="require(`@/assets/cdp/${cdp.cdpType}.png`)" />
            </div>
            <div class="info">
              <div class="name">
                {{ cdp.cdpName }}
                <span class="action" @click.stop="setCdp(cdp)">{{
                  cdp.configStatus ? "设置" : "未设置"
                }}</span>
              </div>
              <div class="desc">{{ cdp.desc }}</div>
            </div>
            <div
              :class="[
                'fl-r',
                { enable: cdp.configStatus },
                { suc: cdp.switchStatus },
              ]"
              @click="
                cdp.configStatus
                  ? setCdpStatus(cdp.cdpType, !cdp.switchStatus)
                  : ''
              "
            >
              <div>
                <i
                  :class="cdp.switchStatus ? 'el-icon-success' : 'icon-circle'"
                ></i>
              </div>
              <div>
                <span>{{ cdp.switchStatus ? "已启用" : "未启用" }}</span>
              </div>
            </div>
          </div></el-col
        >
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: "cdpList",
  data() {
    return {
      list: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.$http
        .get(
          `/mktgo/wecom/cdp/list?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}`
        )
        .then((res) => {
          const sliceArr = (arr, split_len) => {
            const res = [];
            for (let i = 0; i < arr.length; i += split_len) {
              const chunk = arr.slice(i, i + split_len);
              res.push(chunk);
            }
            return res;
          };
          this.list = sliceArr(res.data?.cdpList || [], 3);
        });
    },
    setCdp(cdp) {
      this.$router.push({
        path: "/index/cdpsettings-set",
        query: {
          type: cdp.cdpType,
          name: cdp.cdpName,
        },
      });
    },
    setCdpStatus(cbdType, status) {
      let params = {};
      this.$http
        .post(
          `/mktgo/wecom/cdp/status?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}
          &cdp_type=${cbdType}&status=${status}`,
          params
        )
        .then((res) => {
          if (res.code == 0 && res.message == "success") {
            this.getList();
          } else {
            this.$message({
              message: res.message,
              type: "warning",
            });
          }
        })
        .catch((erro) => {
          this.$message({
            message: erro.message,
            type: "warning",
          });
        });
    },
  },
};
</script>

<style lang="scss" scoped>
.cdp-list-wrapper {
  height: calc(100vh - 98px);
  .tops {
    width: 100%;
    height: 40px;
    display: flex;
    flex-direction: column;
    color: #999999;
    font-size: 12px;
    h2 {
      color: #333;
      font-size: 20px;
      margin-bottom: 10px;
    }
  }
  .content {
    height: calc(100% - 70px);
    padding: 32px 64px 32px 28px;
    background: #fff;
    .row {
      &:not(:first-child) {
        margin-top: 16px;
      }
      .grid-content {
        box-sizing: border-box;
        width: 100%;
        height: 87px;
        background: #ffffff;
        border: 1px solid #dfdfdf;
        border-radius: 8px;
        padding: 20px 26px;
        display: flex;
        position: relative;
        &.enable {
          background: #f4f8ff;
          border: 2px solid #679bff;
        }
        &:hover {
          box-shadow: 0px 0px 10px 0px #679bff;
          cursor: pointer;
        }
        .icon {
          width: 42px;
          height: 42px;
          display: flex;
          align-items: center;
          img {
            width: 100%;
            max-height: 100%;
          }
        }
        .info {
          margin-left: 20px;
          .name {
            font-size: 14px;
            font-weight: 600;
            color: #333333;
            .action {
              font-weight: 400;
              font-size: 12px;
              color: #679bff;
              padding-left: 10px;
              cursor: pointer;
            }
          }
          .desc {
            font-weight: 400;
            font-size: 12px;
            color: #999999;
            padding-top: 10px;
          }
        }
        .fl-r {
          position: absolute;
          right: 28px;
          width: 36px;
          text-align: center;
          color: #999;
          cursor: not-allowed;
          &.suc {
            color: #67c23a;
          }
          span {
            font-size: 12px;
          }
          .icon-circle {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: none;
            display: inline-block;
            border: 1px solid #cacaca;
          }
          &.enable {
            cursor: pointer;
            &:hover {
              color: #679bff;
            }
          }
        }
      }
    }
  }
}
</style>
