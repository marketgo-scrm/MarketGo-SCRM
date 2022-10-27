<template>
  <div class="custom-number-box">
    <div class="custom-number">
      <el-input
        type="number"
        :max="max"
        :min="min"
        placeholder=""
        :size="size"
        v-model="val"
        @input="verification"
      ></el-input>
      <div class="custom-number-operate">
        <div class="custom-number-operate-add" @click="add()">
          <i class="el-icon-arrow-up"></i>
        </div>
        <div class="custom-number-operate-reduce" @click="reduce()">
          <i class="el-icon-arrow-down"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CustomNumber",
  emits: ["change"],
  props: {
    // 值
    value: {
      type: Number,
      default: 0,
    },
    // 最大值
    max: {
      type: Number,
      default: 0,
    },
    // 最小值
    min: {
      type: Number,
      default: 999,
    },
    // 框型号
    size: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      val: "",
    };
  },
  watch: {
    value(data) {
      this.val = data;
    },
  },
  mounted() {
    this.val = this.value
  },
  methods: {
    verification(num) {
      this.val = Number(num);
      this.$emit("change", this.val);
      if (Number(this.val) < Number(this.min)) {
        this.val = this.min;
        this.$emit("change", this.min);
      }
      if (Number(this.val) > Number(this.max)) {
        this.val = this.max;
        this.$emit("change", this.max);
      }
    },
    add() {
      this.verification(Number(this.val) + 1);
    },
    reduce() {
      this.verification(Number(this.val) - 1);
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep(input::-webkit-inner-spin-button) {
  -webkit-appearance: none !important;
  margin: 0;
  -moz-appearance: textfield;
}
::v-deep(input[type="number"]) {
  -moz-appearance: textfield;
}
.custom-number-box {
  display: inline-block;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  box-sizing: border-box;
  .custom-number {
    display: flex;
    ::v-deep(.el-input) {
      border-right: 1px solid #e0e0e0;
      line-height: initial;
      .el-input__inner {
        border: 0;
        width: 100px;
      }
    }
    .custom-number-operate {
      line-height: 0;
      .custom-number-operate-add,
      .custom-number-operate-reduce {
        height: 50%;
        cursor: pointer;
        color: #999999;
        padding: 0 6px;
        &:active {
          background: #679bff;
          color: #fff;
        }
      }
      .custom-number-operate-add {
        border-bottom: 1px solid #e0e0e0;
      }
    }
  }
}
</style>