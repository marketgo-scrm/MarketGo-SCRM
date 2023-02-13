<template>
  <div>
    <main-head title="渠道活码">
      <el-form
        class="head-form"
        inline
        size="small"
        label-width="0"
        @submit.prevent.native
      >
        <el-form-item>
          <el-input
            style="width: 248px"
            placeholder="输入活码名称回车搜索"
            v-model="parameter.keyword"
            class="input-with-select"
            @keyup.enter.native="liveCodeList"
          >
            <template #prefix>
              <i class="el-icon-search el-input__icon"> </i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            style="position: relative; top: -3px"
            type="primary"
            size="medium"
            icon="el-icon-circle-plus"
            round
            @click="$router.push('/index/addchannelcode')"
          >
            新建渠道活码
          </el-button>
        </el-form-item>
      </el-form>
    </main-head>
    <div class="mian-content">
      <template v-if="chanelCodeList.length">
        <el-table :data="chanelCodeList">
        <el-table-column width="186" label="二维码">
          <template #default="{ row }">
            <div class="chanel-code">
              <el-image
                class="chanel-code-img"
                :src="row.qrCode"
                :preview-src-list="[row.qrCode]"
                fit="cover"
              ></el-image>
              <div class="chanel-code-title">
                <div class="two-line">{{ row.name }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="添加客户数" prop="addExtCount" width="120" />
        <el-table-column label="客户标签">
          <template #default="{ row }">
            <div class="add-custom-tag">
              <CustomStretch :height="28" :standard="row?.tags?.length > 3">
                <el-tag
                  type="info"
                  size="small"
                  v-for="tag in row.tags"
                  :key="tag.id"
                >
                  <span class="add-custom-tag-txt">{{ tag.name }}</span>
                </el-tag>
              </CustomStretch>
            </div>
          </template>
        </el-table-column>

        <el-table-column width="260" label="使用员工">
          <template #default="{ row }">
            <div class="add-custom-tag">
              <CustomStretch
                :height="28"
                :standard="
                  row?.members?.departments?.length +
                    row?.members?.users?.length >
                  3
                "
              >
                <el-tag
                  type="info"
                  size="small"
                  v-for="tag in row.members.departments"
                  :key="tag.id"
                >
                  <img class="add-custom-tag-img" src="@/assets/avter.png" />
                  <span class="add-custom-tag-txt">{{ tag.name }}</span>
                </el-tag>
                <el-tag
                  type="info"
                  size="small"
                  v-for="tag in row.members.users"
                  :key="tag.memberId"
                >
                  <img class="add-custom-tag-img" src="@/assets/avter.png" />
                  <span class="add-custom-tag-txt">{{ tag.memberName }}</span>
                </el-tag>
              </CustomStretch>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="200" label="备用员工" >
          <template #default="{ row }">
            <div class="add-custom-tag">
              <CustomStretch
                :height="28"
                :standard="
                  row?.backupMembers?.departments?.length +
                    row?.members?.users?.length >
                  3
                "
              >
                <el-tag
                  type="info"
                  size="small"
                  v-for="tag in row.backupMembers.departments"
                  :key="tag.id"
                >
                  <img class="add-custom-tag-img" src="@/assets/avter.png" />
                  <span class="add-custom-tag-txt">{{ tag.name }}</span>
                </el-tag>
                <el-tag
                  type="info"
                  size="small"
                  v-for="tag in row.backupMembers.users"
                  :key="tag.memberId"
                >
                  <img class="add-custom-tag-img" src="@/assets/avter.png" />
                  <span class="add-custom-tag-txt">{{ tag.memberName }}</span>
                </el-tag>
              </CustomStretch>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column
          label="操作"
          width="210"
          prop="addExtCount"
          align="right"
        >
          <template #default="{ row }">
            <el-button @click="exportScore(row)" size="small" type="text"
              >下载</el-button
            >
            <el-divider direction="vertical"></el-divider>
            <el-button @click="openDetails(row)" size="small" type="text"
              >详情</el-button
            >
            <el-divider direction="vertical"></el-divider>
            <el-button
              @click="
                $router.push({
                  path: '/index/channelcodedetails',
                  query: {
                    uuid: row.uuid,
                    createTime: row.createTime,
                    name: row.name,
                  },
                })
              "
              size="small"
              type="text"
              >数据</el-button
            >
            <el-divider direction="vertical"></el-divider>
            <el-button @click="liveCodeDelete(row)" size="small" type="text">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="parameter.page_num"
        :page-size="parameter.page_size"
        :total="total"
        background
        layout="total,  prev, pager, next, sizes, jumper"
        :page-sizes="[10, 20, 30]"
        @size-change="pageSizeChange"
        @current-change="liveCodeList"
      />
      </template>
      <el-empty :image="empty" description="暂无数据" v-else></el-empty>
    </div>
    <!-- 查看详情 -->
    <el-dialog
      title="活码详情"
      :visible.sync="codeVisible"
      width="700px"
      :before-close="codeClose"
    >
      <div class="code-details" v-if="details.id">
        <div class="code-details-left">
          <el-image
            class="code-details-left-codeimg"
            :src="details.qrCode"
            :preview-src-list="[details.qrCode]"
            fit="cover"
          ></el-image>
          <div class="code-details-left-title two-line">
            {{ details.name }}
          </div>
          <div style="margin-top: 16px">
            <el-button
              @click="exportScore(details)"
              style="width: 100%"
              round
              size="small"
              type="primary"
              >下载活码</el-button
            >
          </div>
          <div style="margin-top: 16px">
            <el-button
              @click="
                $router.push({
                  path: '/index/addchannelcode',
                  query: {
                    uuid: details.uuid,
                  },
                })
              "
              size="small"
              style="width: 100%"
              round
              >修 改</el-button
            >
          </div>
        </div>
        <div class="code-details-right">
          <el-descriptions title="基本信息" :column="1">
            <el-descriptions-item label="创建时间">{{
              details.createTime
            }}</el-descriptions-item>
            <el-descriptions-item label="使用员工">
              <div class="add-custom-tag">
                <CustomStretch
                  :height="28"
                  :standard="
                    details?.members?.departments?.length +
                      details?.members?.users?.length >
                    3
                  "
                >
                  <el-tag
                    type="info"
                    size="small"
                    v-for="tag in details.members.departments"
                    :key="tag.id"
                  >
                    <img class="add-custom-tag-img" src="@/assets/avter.png" />
                    <span class="add-custom-tag-txt">{{ tag.name }}</span>
                  </el-tag>
                  <el-tag
                    type="info"
                    size="small"
                    v-for="tag in details.members.users"
                    :key="tag.memberId"
                  >
                    <img class="add-custom-tag-img" src="@/assets/avter.png" />
                    <span class="add-custom-tag-txt">{{ tag.memberName }}</span>
                  </el-tag>
                </CustomStretch>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="备用成员">
              <div class="add-custom">
                <div class="add-custom-tag">
                  <CustomStretch
                    :height="28"
                    :standard="
                      details.backupMembers?.departments?.length +
                        details.backupMembers?.users?.length >
                      3
                    "
                  >
                    <el-tag
                      type="info"
                      size="small"
                      v-for="tag in details.backupMembers?.departments"
                      :key="tag.id"
                    >
                      <img
                        class="add-custom-tag-img"
                        src="@/assets/avter.png"
                      />
                      <span class="add-custom-tag-txt">{{ tag.name }}</span>
                    </el-tag>
                    <el-tag
                      type="info"
                      size="small"
                      v-for="tag in details.backupMembers?.users"
                      :key="tag.memberId"
                    >
                      <img
                        class="add-custom-tag-img"
                        src="@/assets/avter.png"
                      />
                      <span class="add-custom-tag-txt">{{
                        tag.memberName
                      }}</span>
                    </el-tag>
                  </CustomStretch>
                </div>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="自动通过好友">
              <template v-if="details.skipVerify"> 已开启 </template>
              <template v-else> 已关闭 </template>
            </el-descriptions-item>
            <el-descriptions-item label="渠道码欢迎语">
              <CustomStretch :height="28" :standard="true" :marginTop="-3">
                <template v-if="details.welcomeContent.find((item) => {
                  return item.type === 'TEXT'
                })?.text?.content">
                <div v-html="details.welcomeContent.find((item) => {
                  return item.type === 'TEXT'
                })?.text?.content.replace(/#{EXTERNAL_USER_NAME}#/g, '<span style=color:#409EFF;>客户昵称</span>')"></div>
              </template>
              </CustomStretch>
              
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import qs from "qs";
export default {
  name: "channelcode",
  data() {
    return {
      empty: require('@/assets/empty-images.png'),
      chanelCodeList: [],
      parameter: {
        page_num: 1,
        page_size: 20,
        keyword: "",
      },
      total: 0,
      // 查看详情
      codeVisible: false,
      details: {
        id: "",
        uuid: "",
        qrCode: "",
        name: "",
        addExtCount: 0,
        members: {
          // departments: [{ id: 2, name: "研发部" }],
          // users: [{ memberId: "WangWeiNing", memberName: "王伟宁" }],
        },
        backupMembers: {},
      },
    };
  },
  created() {
    this.liveCodeList();
  },
  methods: {
    liveCodeDelete(item) {
      this.$confirm('是否确定删除？', {
          distinguishCancelAndClose: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(() => {
            this.$api
              .liveCodeDelete({
                channel_uuid: item.uuid,
              })
              .then((res) => {
                console.log(res);
                if (res.code === 0) {
                  this.liveCodeList(this.parameter.page_num)
                  this.$message.success('删除成功');
                } else {
                  this.$message(res.message);
                }
              })
              .catch((err) => {
                console.log(err);
              });
          }).catch(action => {
            console.log(action)
          });
    },
    exportScore(item) {
      console.log(777, item);
      if (item.isUploadLogo) {
        const href = `${this.$global.BASEURL}mktgo/wecom/live_code/qrcode_download?${qs.stringify(
          { ...this.$store.getters?.parseId, ...{ channel_uuid: item.uuid } }
        )}`;
        console.log(href);
        const a = document.createElement("a");
        a.href = href;
        a.target = "_blank";
        a.download = item.name;
        a.click();
        a.remove();
      } else {
        this.downloadByBlob(item);
      }
    },
    downloadByBlob(item) {
      let image = new Image();
      image.setAttribute("crossOrigin", "anonymous");
      image.src = item.qrCode;
      image.onload = () => {
        let canvas = document.createElement("canvas");
        canvas.width = image.width;
        canvas.height = image.height;
        let ctx = canvas.getContext("2d");
        ctx.drawImage(image, 0, 0, image.width, image.height);
        canvas.toBlob((blob) => {
          let url = URL.createObjectURL(blob);
          this.download(url, item.name);
          // 用完释放URL对象
          URL.revokeObjectURL(url);
        });
      };
    },
    download(href, name) {
      const a = document.createElement("a");
      a.download = name;
      a.href = href;
      a.click();
      a.remove();
    },
    // 查看详情
    openDetails(item) {
      this.$api
        .liveCodeDetail({
          channel_uuid: item.uuid,
        })
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.details = res.data;
            this.codeVisible = true;
          } else {
            this.$message(res.message);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // 关闭详情
    codeClose() {
      this.codeVisible = false;
    },
    // 调整页数
    pageSizeChange(e) {
      console.log(e);
      this.parameter.page_size = Number(e);
      this.liveCodeList();
    },
    liveCodeList(page) {
      this.parameter.page_num = page || 1;
      console.log(this.parameter);
      this.$api
        .liveCodeList(this.parameter)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.chanelCodeList = res.data.infos || [];
            this.total = res.total;
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
};
</script>
<style lang="scss" scoped>
.head-form {
  .el-form-item {
    margin: 0 24px 0 0;
    &:last-child {
      margin-right: 0;
    }
  }
}
.mian-content {
  background: #ffffff;
  ::v-deep(.el-pagination) {
    padding: 10px;
  }
}
.code-details {
  display: flex;
  .code-details-left {
    width: 35%;
    box-sizing: border-box;
    padding: 0 40px;
    border-right: #d2d2d2 1px solid;
    .code-details-left-codeimg {
      width: 100%;
    }
    .code-details-left-title {
      text-align: center;
      color: #333333;
      font-family: PingFang SC;
      font-size: 12px;
      margin: 10px 0;
    }
  }
  .code-details-right {
    width: 65%;
    box-sizing: border-box;
    padding: 0 40px;
  }
}
.chanel-code {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .chanel-code-img {
    width: 40px;
    height: 40px;
    margin-right: 14px;
    flex: none;
  }
  .chanel-code-title {
    font-size: 12px;
    line-height: 162%;
    color: #333333;
    flex: auto;
    display: grid;
  }
}
.add-custom-tag {
  flex: auto;
  .el-tag {
    border-radius: 40px;
    margin-right: 6px;
    margin-bottom: 6px;
    line-height: 20px;
    .add-custom-tag-img {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      vertical-align: middle;
      margin-right: 5px;
    }
    .add-custom-tag-txt {
      vertical-align: middle;
    }
  }
}
</style>