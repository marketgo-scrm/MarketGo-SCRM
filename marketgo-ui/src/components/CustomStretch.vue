<template>
  <div
    class="custom-stretch"
    :style="{ '--height': height + 'px' }"
    :class="{ on: stretch && standard }"
  >
    <div class="custom-stretch-box" :class="{ stretch: stretch && standard }">
      <slot></slot>
    </div>
    <template v-if="standard">
      <el-button
        @click="stretch = false"
        v-if="stretch"
        size="mini"
        type="text"
        :style="{marginTop:marginTop +'px'}"
      >
        展开
        <i class="el-icon-arrow-down" />
      </el-button>
      <el-button
      :style="{marginTop:marginTop +'px'}"
        @click="stretch = true"
        v-if="!stretch"
        size="mini"
        type="text"
      >
        收起
        <i class="el-icon-arrow-up" />
      </el-button>
    </template>
  </div>
</template>

<script>
export default {
  name: "CustomStretch",
  props: {
    // 收缩后高度
    height: {
      type: Number,
      default: 28,
    },
    // 达成条件
    standard: {
      type: Boolean,
      default: false,
    },
    marginTop: {
      type: Number,
      default: 0,
    }
  },
  data() {
    return {
      stretch: true,
    };
  },
  methods: {
    goback() {
      this.$router.go(-1);
    },

  },
};
</script>
<style lang="scss" scoped>
.custom-stretch {
  display: flex;
  align-items: baseline;
  overflow: hidden;
  flex-wrap: wrap;
  &.on {
    flex-wrap: inherit;
    height: var(--height);
  }
  .custom-stretch-box {
    display: contents;
    flex-wrap: wrap;
    &.stretch {
      display: flex;
    }
  }
  .el-button {
    flex: none;
  }
  &.stretch {
    align-items: center;
  }
}
</style>