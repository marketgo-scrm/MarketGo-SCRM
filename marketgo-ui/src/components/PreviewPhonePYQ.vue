<template>
  <div class="preview-phone">
<!--    <img class="preview-phone-header" src="@/assets/phone-header.png" />-->
<!--    <div class="preview-phone-content">-->
<!--      <div
        class="preview-phone-content-item"
        v-for="(item, index) in list"
        :key="index"
      >
        <div class="preview-phone-content-item-avatar">
          <i class="el-icon-s-custom"></i>
        </div>
        <div class="preview-phone-content-item-info">
          &lt;!&ndash; IMAGE,MINIPROGRAM 小程序,LINK 网页, TEXT 文本 &ndash;&gt;
          <div
            class="preview-phone-content-item-info-text"
            v-if="item.type === 'TEXT'"
          >
            <template v-if="item?.text?.content">
              <div
                v-html="
                  item?.text?.content?.replace(
                    /#{EXTERNAL_USER_NAME}#/g,
                    '<span style=color:#409EFF;>客户昵称</span>'
                  )
                "
              ></div>
            </template>
            <template v-else>请输入欢迎语</template>
          </div>
          <div
            class="preview-phone-content-item-info-program"
            v-if="item.type === 'IMAGE'"
          >
            <el-image
              class="preview-phone-content-item-info-program-img"
              :src="`data:image/png;base64,${item?.image?.imageContent.replace(
                /\\r\\n/g,
                ''
              )}`"
              v-if="item?.image?.imageContent"
            >
            </el-image>
            <el-image
              class="preview-phone-content-item-info-program-img"
              v-else
            >
              <div class="picture-outline" slot="error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </div>
          <div
            class="preview-phone-content-item-info-link"
            v-if="item.type === 'LINK'"
          >
            <div class="preview-phone-content-item-info-link-title">
              <div class="one-line">
                <template v-if="item?.link?.title">
                  {{ item?.link?.title }}
                </template>
                <template v-else> 请填写标题 </template>
              </div>
            </div>
            <div class="preview-phone-content-item-info-link-content">
              <div class="preview-phone-content-item-info-link-content-deoc">
                <div class="two-line">
                  <template v-if="item?.link?.desc">
                    {{ item?.link?.desc }}
                  </template>
                  <template v-else> 请输入内容 </template>
                </div>
              </div>
              <el-image
                class="preview-phone-content-item-info-link-content-img"
                :src="`data:image/png;base64,${item?.link?.imageContent.replace(
                  /\\r\\n/g,
                  ''
                )}`"
                v-if="item?.link?.imageContent"
              >
              </el-image>
              <el-image
                class="preview-phone-content-item-info-link-content-img"
                v-else
              >
                <div class="picture-outline" slot="error">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>
          </div>
          <div
            class="preview-phone-content-item-info-program"
            v-if="item.type === 'MINIPROGRAM'"
          >
            <div class="preview-phone-content-item-info-program-title">
              <div class="one-line">
                <template v-if="item?.miniProgram?.title">
                  {{ item?.miniProgram?.title }}
                </template>
                <template v-else> 请填写标题 </template>
              </div>
            </div>
            <el-image
              class="preview-phone-content-item-info-program-img"
              :src="`data:image/png;base64,${item?.miniProgram?.imageContent.replace(
                /\\r\\n/g,
                ''
              )}`"
              v-if="item?.miniProgram?.imageContent"
            >
            </el-image>
            <el-image
              class="preview-phone-content-item-info-program-img"
              v-else
            >
              <div class="picture-outline" slot="error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div class="preview-phone-content-item-info-program-sign">
              <img
                class="preview-phone-content-item-info-program-sign-icon"
                src="@/assets/program-icon.png"
              />小程序
            </div>
          </div>
        </div>
      </div>-->
      <!-- <div class="preview-phone-content-item">
        <div class="preview-phone-content-item-avatar">
          <i class="el-icon-s-custom"></i>
        </div>
        <div class="preview-phone-content-item-info">
          <div class="preview-phone-content-item-info-text">
            纷纷i哪能佛诶我
          </div>
        </div>
      </div>
      <div class="preview-phone-content-item">
        <div class="preview-phone-content-item-avatar">
          <i class="el-icon-s-custom"></i>
        </div>
        <div class="preview-phone-content-item-info">
          <div class="preview-phone-content-item-info-program">
            <div class="preview-phone-content-item-info-program-title">
              <div class="one-line">请填写标题</div>
            </div>
            <el-image class="preview-phone-content-item-info-program-img">
              <div class="picture-outline" slot="error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div class="preview-phone-content-item-info-program-sign">
              <img
                class="preview-phone-content-item-info-program-sign-icon"
                src="@/assets/program-icon.png"
              />纷纷i哪能佛诶我
            </div>
          </div>
        </div>
      </div>
      <div class="preview-phone-content-item">
        <div class="preview-phone-content-item-avatar">
          <i class="el-icon-s-custom"></i>
        </div>
        <div class="preview-phone-content-item-info">
          <div class="preview-phone-content-item-info-program">
            <el-image class="preview-phone-content-item-info-program-img">
              <div class="picture-outline" slot="error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </div>
        </div>
      </div>
      <div class="preview-phone-content-item">
        <div class="preview-phone-content-item-avatar">
          <i class="el-icon-s-custom"></i>
        </div>
        <div class="preview-phone-content-item-info">
          <div class="preview-phone-content-item-info-link">
            <div class="preview-phone-content-item-info-link-title">
              <div class="one-line">请填写标题</div>
            </div>
            <div class="preview-phone-content-item-info-link-content">
              <div class="preview-phone-content-item-info-link-content-deoc">
                <div class="two-line">
                  请输入内容
                </div>
              </div>
              <el-image
                class="preview-phone-content-item-info-link-content-img"
              >
                <div class="picture-outline" slot="error">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>
          </div>
        </div>
      </div> -->
<!--    </div>-->
<!--    <img class="preview-phone-footer" src="@/assets/phone-footer.png" />-->
    <div
        class="preview-phone-content-item"
        v-for="(item, index) in list"
        :key="index"
    >
      <div style="padding-top: 212px;padding-left: 59px;
font-size: 12px;
line-height: 100%;width: 168px;word-wrap: break-word" v-if="item?.text">
        <div v-if="item.type === 'TEXT'">
          <template v-if="item?.text?.content">
            <div style="white-space: pre-wrap;"
                v-html="
                  item?.text?.content?.replace(
                    /#{EXTERNAL_USER_NAME}#/g,
                    '<span style=color:#409EFF;>客户昵称</span>'
                  )
                "
            ></div>
          </template>
          <template v-else>请输入文字消息</template>
        </div>
      </div>


    </div>
    <div class="imgBox" v-if="list[1]?.image">
      <div v-for="(v,k) in list" :key="k" style="display: inline-block">
        <el-image
            class="preview-phone-content-item-info-program-img img"
            :src="`data:image/png;base64,${v?.image?.imageContent.replace(
                /\\r\\n/g,
                ''
              )}`"
            v-if="v?.image?.imageContent"
        >
        </el-image>
      </div>
    </div>
    <div class="preview-phone-content">
      <div
          class="preview-phone-content-item"
          v-for="(item, index) in list"
          :key="index"
      >
        <div class="preview-phone-content-item-info">
          <!-- IMAGE,MINIPROGRAM 小程序,LINK 网页, TEXT 文本 -->
          <div
              class="preview-phone-content-item-info-link"
              v-if="item.type === 'LINK'"
          >
            <el-image
                class="preview-phone-content-item-info-link-content-img"
                :src="`data:image/png;base64,${item?.link?.imageContent.replace(
                  /\\r\\n/g,
                  ''
                )}`"
                v-if="item?.link?.imageContent"
            >
            </el-image>
            <el-image
                class="preview-phone-content-item-info-link-content-img"
                v-else
            >
              <div class="picture-outline" slot="error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div class="preview-phone-content-item-info-link-title">
              <div class="one-line">
                <template v-if="item?.link?.title">
                  {{ item?.link?.title }}
                </template>
                <template v-else> 请填写标题 </template>
              </div>
            </div>
            <div class="preview-phone-content-item-info-link-content">
              <div class="preview-phone-content-item-info-link-content-deoc">
                <div class="two-line">
                  <template v-if="item?.link?.desc">
                    {{ item?.link?.desc }}
                  </template>
                  <template v-else> 请输入内容 </template>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>


  </div>
</template>

<script>
export default {
  name: "PreviewPhone",
  props: {
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      imgList: []
    };
  },
  computed: {},
  created() {
    for (let i in this.list) {
      if (this.list[i].type === 'IMAGE') {
        this.imgList.push(this.list[i])
      }
    }
  },
  updated() {
    console.log(this.list)
    /*this.imgList = []
    for (let i in this.list) {
      if (this.list[i].type === 'IMAGE') {
        this.imgList.push(this.list[i])
      }
    }*/
  },
  watch: {
    list(data) {
      this.imgList = []
      for (let i in data) {
        if (data[i].type === 'IMAGE') {
          this.imgList.push(data[i])
        }
      }
    },
  },
  methods: {},
};
</script>
<style lang="scss" scoped>
.preview-phone {
  width: 272px;
  height: 487px;
  background: url("../assets/imgs/img.png") no-repeat;
  background-position: -1px -1px;
  background-size: 272px;
  .imgBox {
    width: 200px;
    height: auto;
    margin-top: 12px;
    margin-left: 59px;
    .img {
      width: 60px;
      height: 60px;
      margin-right: 3px;
      margin-bottom: 3px;
    }
  }
  .preview-phone-header {
    width: 100%;
    display: block;
  }
  .preview-phone-content {
    //height: 380px;
    //background-color: #ebedf0;
    //padding: 16px 30px 16px 12px;
    width: 240px;
    padding-left: 59px;
    box-sizing: border-box;
    overflow: hidden;
    overflow-y: auto;
    .preview-phone-content-item {
      //display: flex;
      //margin-bottom: 16px;
      height: auto;
      .preview-phone-content-item-avatar {
        width: 24px;
        height: 24px;
        line-height: 24px;
        border-radius: 4px;
        background-color: #dcdcdc;
        flex: none;
        margin-right: 10px;
        text-align: center;
        color: #fff;
        .el-icon-s-custom {
        }
      }
      .preview-phone-content-item-info {
        background-color: #fff;
        border-radius: 4px;
        //flex: auto;
        //padding: 4px 8px;
        .preview-phone-content-item-info-text {
          color: #333333;
          font-family: PingFang SC;
          font-size: 12px;
          line-height: 20px;
        }
        .preview-phone-content-item-info-program {
          .preview-phone-content-item-info-program-title {
            color: #333333;
            font-family: PingFang SC;
            font-weight: 300;
            font-size: 14px;
            display: grid;
          }
          .preview-phone-content-item-info-program-img {
            margin: 6px 0;
            width: 100%;
            height: 144px;
            display: block;
            background-color: #f4f4f4;
            ::v-deep(.picture-outline) {
              width: 100%;
              height: 100%;
              display: flex;
              align-items: center;
              .el-icon-picture-outline {
                display: block;
                margin: auto;
                color: #bfbfca;
              }
            }
          }
          .preview-phone-content-item-info-program-sign {
            color: #9ba3b4;
            font-weight: 400;
            font-size: 12px;
            .preview-phone-content-item-info-program-sign-icon {
              width: 9px;
              height: 9px;
              margin-right: 4px;
            }
          }
        }
        .preview-phone-content-item-info-link {
          padding-top: 10px;
          padding-bottom: 4px;
          .preview-phone-content-item-info-link-title {
            color: #333333;
            font-family: PingFang SC;
            font-weight: 300;
            font-size: 14px;
            display: grid;
          }
          .preview-phone-content-item-info-link-content {
            margin-top: 2px;
            display: flex;
            justify-content: space-between;
            .preview-phone-content-item-info-link-content-deoc {
              font-size: 12px;
              color: #9ba3b4;
              flex: auto;
            }
            .preview-phone-content-item-info-link-content-img {
              width: 38px;
              height: 38px;
              display: block;
              background-color: #f4f4f4;
              flex: none;
              margin-left: 10px;
              ::v-deep(.picture-outline) {
                width: 100%;
                height: 100%;
                display: flex;
                align-items: center;
                .el-icon-picture-outline {
                  display: block;
                  margin: auto;
                  color: #bfbfca;
                }
              }
            }
          }

          .preview-phone-content-item-info-link-content-img {
            width: 140px;
            //height: 140px;
            height: auto;
            display: block;
            background-color: #f4f4f4;
            flex: none;
            //margin-left: 10px;
            ::v-deep(.picture-outline) {
              width: 100%;
              height: 100%;
              display: flex;
              align-items: center;
              .el-icon-picture-outline {
                font-size: 50px;
                display: block;
                margin: auto;
                color: #bfbfca;
              }
            }
          }
        }
      }
    }
  }
  .preview-phone-footer {
    width: 100%;
    display: block;
  }
}
</style>