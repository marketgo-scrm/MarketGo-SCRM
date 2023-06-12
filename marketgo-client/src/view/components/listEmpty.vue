<template>
  <div class="list-empty">
    <img :src="url" />
    <div class="text">暂无数据</div>
  </div>
</template>

<script>
import empty from "../../imgs/empty-images.png";
export default {
    data(){
        return {
            url:empty
        }
    }
};
</script>

<style lang="less" scoped>
.list-empty {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  margin-top: 30px;
  img{
    width: 215px;
  }
  .text {
    color: #999;
    text-align: center;
  }
}
</style>
