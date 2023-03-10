<template>
  <div class="detail">
    <van-nav-bar :left-arrow="!!from" @click-left="back" title="任务详情" />
    <van-cell-group style="background: transparent">
      <div class="top">
        <div>
          <span class="title">{{ detail.taskName ? detail.taskName : "" }}</span
          ><van-tag color=" #FF993C" plain class="status">未完成</van-tag>
        </div>
        <div class="date"><span>今天8:30前完成</span></div>
      </div>
      <div class="content">
        <div class="type-title">{{ "任务说明" }}</div>
        <div class="text">
          {{ (contentTxt && contentTxt[0].text.content) || "" }}
        </div>
        <div class="image" v-if="imgList">
          <img
            :src="'data:image/Jpeg;base64,' + imgList[0].image.imageContent"
            alt=""
          />
        </div>
      </div>
    </van-cell-group>
    <van-cell-group class="receiver">
      <van-cell is-link @click="toReceiver" size="large">
        <template #title>
          <span class="lbl">发送给:</span>
          <span class="list" v-if="receiverList"
            >{{ receiverList[0].name }}等{{ receiverList.length }}人</span
          >
        </template>
      </van-cell>
    </van-cell-group>
    <van-action-bar class="bottom-bar">
      <van-button type="primary" @click="finish">标记为完成</van-button>
    </van-action-bar>
  </div>
</template>

<script>
import { showSuccessToast } from "vant";
import "vant/es/toast/style";
import { welcom } from "../../api/welcom";
import { welcomPinia } from "../../pinia/";
export default {
  data() {
    return {
      from: "",
      detail: {},
      imgBase64:
        "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",
    };
  },
  created() {
    const query = this.$route.query;
    this.from = query.from;
    welcom.taskDetail(query).then((res) => {
      this.detail = res.data || {};
      this.detail.query = query;
      const pinia = welcomPinia();
      pinia.setDetail(this.detail);
    });
  },

  methods: {
    back() {
      this.$router.go(-1);
    },
    finish() {
      const query = { corp_id };
      const params = {
        externalUserId: "",
        memberId: this.detail.member_id,
        sentTIme: Date.now(),
        status: "SENT",
        taskUuid: this.detail.query.task_uuid,
        type: "MEMBER", // EXTERNAL_USER 客户
        uuid: this.detail.query.uuid,
      };
      welcom.changeStatus({ query, params }).then((res) => {
        if (res.code === 0) {
          showSuccessToast("成功");
        }
      });
    },
    toReceiver() {
      this.$router.push({
        name: "receiverList",
      });
    },
  },
  computed: {
    receiverList() {
      const users =
        (this.detail.taskList &&
          this.detail.taskList[0] &&
          this.detail.taskList[0].externalUserId) ||
        false;
      return users;
    },
    contentTxt() {
      const txt =
        (this.detail.attachments &&
          this.detail.attachments.filter((item) => item.msgType === "text")) ||
        [];

      return txt.length > 0 ? txt : false;
    },
    imgList() {
      const imgList =
        (this.detail.attachments &&
          this.detail.attachments.filter((item) => item.msgType === "image")) ||
        [];
      return imgList.length > 0 ? imgList : false;
    },
  },
};
</script>

<style lang="less" scoped>
.detail {
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

  .top {
    background: #fff;
    padding: 15px;

    .title {
      font-style: normal;
      font-weight: 600;
      color: #333333;
      display: inline-block;
      width: calc(100% - 50px);
    }

    .status {
      float: right;
      top: 10px;
    }

    .date {
      font-style: normal;
      font-weight: 400;
      line-height: 24px;
      color: #ff993c;
      padding-top: 30px;
    }
  }

  .content {
    margin-top: 20px;
    background: #fff;
    padding: 15px;

    .type-title {
      line-height: 100%;
      color: #333333;
    }

    .text {
      margin-top: 10px;
      padding: 10px;
      background: #f5f7fa;
      min-height: 60px;
    }

    .image {
      margin-top: 27px;
    }
  }

  .receiver {
    .lbl {
      color: #999999;
    }

    .list {
      color: #333333;
      padding-left: 5px;
    }
  }

  .bottom-bar {
    justify-content: center;
    margin: 0 15px 40px 15px;

    button {
      width: 100%;
      background: #679bff;
    }
  }
}
</style>
