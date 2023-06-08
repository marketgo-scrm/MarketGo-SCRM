<template>
  <van-overlay :show="params.visible" @click="hiddenOverlay">
    <div class="overlay-wrapper" @click.stop>
      <div class="block">
        <van-nav-bar
          :left-arrow="true"
          @click-left="hiddenOverlay"
          title="详情"
        />

        <div class="top">
          <div class="title">{{ params.data.title }}</div>
          <div class="info">
            <span>创建时间：</span><span>{{ params.data.createTime }}</span
            ><span class="creator">创建人：</span
            ><span>{{ params.data.creatorName }}</span>
          </div>
        </div>
        <div class="divider-bg"></div>
        <div class="content">
          <div class="title">{{params.descTxt?params.descTxt:'发送内容'}}</div>
          <div class="text-wrapper" v-if="params.data.type === 'TEXT'">
            <div class="txt">
              {{ params.data.text.content||"" }}
            </div>
          </div>
          <div
            class="image-wrapper"
            v-if="params.data.type === 'IMAGE'"
          >
            <div class="imgs">
              <img
                :src="'data:image/Jpeg;base64,' + params.data.image.imageContent"
                alt=""
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </van-overlay>
</template>

<script>
export default {
  name: "materialDetail",
  props: {
    params: {
      type: Object,
      default: () => {},
    },
  },
  methods: {
    hiddenOverlay() {
      this.params.visible = false;
      this.$emit("hidden", false);
    },
  },
};
</script>

<style lang="less" scoped>
.overlay-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  .block {
    width: 100%;
    height: 100%;
    background-color: #fff;
    .top {
      background: #fff;
      padding: 15px 15px 0;

      .title {
        font-style: normal;
        font-weight: 600;
        color: #333333;
        display: inline-block;
        width: calc(100% - 80px);
        font-size: 18px;
      }
      .info {
        margin-top: 15px;
        color: #999;
        .creator {
          padding-left: 10px;
        }
      }
    }
    .divider-bg {
      height: 15px;
      background: #f5f7fa;
      margin-top: 15px;
    }
    .content {
      background: #fff;
      padding: 15px;
      .title {
        font-family: "PingFang SC";
        font-style: normal;
        font-weight: 600;
        // font-size: 24px;
        line-height: 100%;
        letter-spacing: 0.075em;
        color: #333333;
        margin-bottom: 15px;
      }
      .text-wrapper {
        margin-top: 10px;

        .txt {
          padding: 10px;
          background: #f5f7fa;
          min-height: 60px;
        }
      }

      .image-wrapper {
        margin-top: 10px;
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
  }
}
</style>
