<template>
  <div class="preview-phone">
    <img class="preview-phone-header" src="@/assets/phone-header.png" />
    <div class="preview-phone-content">
      <div
        class="preview-phone-content-item"
        v-for="(item, index) in list"
        :key="index"
      >
        <div class="preview-phone-content-item-avatar">
          <i class="el-icon-s-custom"></i>
        </div>
        <div class="preview-phone-content-item-info">
          <!-- IMAGE,MINIPROGRAM 小程序,LINK 网页, TEXT 文本 -->
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
      </div>
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
    </div>
    <img class="preview-phone-footer" src="@/assets/phone-footer.png" />
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
    return {};
  },
  computed: {},
  created() {},
  methods: {},
};
</script>
<style lang="scss" scoped>
.preview-phone {
  width: 272px;
  .preview-phone-header {
    width: 100%;
    display: block;
  }
  .preview-phone-content {
    height: auto;
    min-height: 240px;
    background-color: #ebedf0;
    padding: 16px 30px 16px 12px;
    box-sizing: border-box;
    overflow: hidden;
    overflow-y: auto;
    .preview-phone-content-item {
      display: flex;
      margin-bottom: 16px;
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
        flex: auto;
        padding: 4px 8px;
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