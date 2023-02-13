<template>
  <div>
    <main-head
      back
      :title="formData.id ? '编辑渠道活码' : '新建渠道活码'"
    ></main-head>
    <el-form
      ref="ruleForm"
      size="small"
      :model="formData"
      :rules="rules"
      label-width="100px"
      @submit.prevent.native
    >
      <el-card class="box-card">
        <el-row :gutter="20">
          <el-col :sm="18" :md="14" :xs="24">
            <div class="custom-title">基础信息</div>
            <el-form-item label="二维码名称：" prop="name">
              <el-input
                v-model="formData.name"
                @blur="liveCodeCheckName()"
                maxlength="30"
              ></el-input>
            </el-form-item>
            <el-form-item label="在线类型：" prop="onlineType">
              <el-radio v-model="formData.onlineType" :label="1" border
                >全天在线</el-radio
              >
            </el-form-item>
            <el-form-item required label="添加员工：">
              <div class="add-custom">
                <div class="add-custom-btn">
                  <el-button
                    @click="
                      $refs.selectstaffRef.open(
                        formData?.members?.departments,
                        formData?.members?.users
                      )
                    "
                    size="mini"
                    icon="el-icon-plus"
                    circle
                  ></el-button>
                </div>
                <div class="add-custom-tag">
                  <CustomStretch
                    :height="28"
                    :standard="
                      formData?.members?.departments.length +
                        formData?.members?.users.length >
                      3
                    "
                  >
                    <el-tag
                      type="info"
                      effect="plain"
                      size="small"
                      closable
                      :disable-transitions="false"
                      @close="deleteDepartments(index)"
                      v-for="(tag, index) in formData?.members?.departments"
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
                      effect="plain"
                      size="small"
                      closable
                      :disable-transitions="false"
                      @close="deleteUsers(index)"
                      v-for="(tag, index) in formData?.members?.users"
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
              <custom-alert
                text="同一个二维码可选择多位员工进行接待，客户扫码后随机分配一名员工如果选择的是部门，若部门有新入职的员工，则自动添加为使用员工"
              />
              <span style="color: #F56C6C;font-size:12px;" v-if="formData?.members?.departments.length + formData?.members?.users.length <= 0">
                请添加员工
              </span>
            </el-form-item>
            <el-form-item label="添加客户上限：">
              <el-switch
                v-model="formData.addExtUserLimitStatus"
                active-color="#81DD9B"
                @change="extUserLimitStatusChange"
              >
              </el-switch>
              <custom-alert
                text="开启后，员工从此活码添加的客户达到每日上线后，将自动下线当日不再接待该活码新客户"
              />
              <template v-if="formData.addExtUserLimitStatus">
                <div style="margin-top: 12px; font-size: 12px">
                  <el-checkbox
                    :indeterminate="isIndeterminate"
                    v-model="checkAll"
                    @change="handleCheckAllChange"
                    :disabled="!formData.addExtUserLimit.length"
                  >
                    <span style="font-size: 12px">全部页全选</span>
                  </el-checkbox>
                  已选择 {{ addExtUserCeilings.length }} 个员工 批量操作：
                  <el-button
                    :disabled="!addExtUserCeilings.length"
                    @click="extUserCeilingOpen()"
                    type="text"
                  >
                    设置上限
                  </el-button>
                </div>
                <el-checkbox-group
                  v-model="addExtUserCeilings"
                  v-if="formData.addExtUserLimit.length"
                  @change="handleCheckedCitiesChange"
                >
                  <div
                    class="custom-table"
                    style="border-bottom: 0; border-radius: 4px 4px 0 0"
                  >
                    <div class="custom-table-colgouup">
                      <div class="custom-table-col" style="width: 54%"></div>
                      <div class="custom-table-col" style="width: 46%"></div>
                    </div>
                    <div class="custom-table-tr">
                      <div class="custom-table-th">员工</div>
                      <div class="custom-table-th">每日添加客户上线</div>
                    </div>
                  </div>
                  <div
                    style="
                      overflow-y: auto;
                      border-top: 0;
                      max-height: 400px;
                      border-radius: 0 0 4px 4px;
                      border: 1px solid #e0e0e0;
                    "
                  >
                    <div class="custom-table" style="border: 0">
                      <div
                        class="custom-table-tr"
                        v-for="(item, index) in formData.addExtUserLimit"
                        :key="index"
                      >
                        <div class="custom-table-td">
                          <el-checkbox :label="item.memberId">
                            <div style="display: grid">
                              <div class="user-info">
                                <img
                                  class="user-info-img"
                                  src="@/assets/avter.png"
                                />
                                <div class="user-info-name">
                                  <div class="one-line">
                                    {{ item.memberName }}
                                  </div>
                                </div>
                              </div>
                            </div>
                          </el-checkbox>
                        </div>
                        <div class="custom-table-td">
                          <CustomNumber
                            :max="999"
                            :min="1"
                            placeholder="请输入内容"
                            :value="item.addExtUserCeiling"
                            @change="ceilingChange($event, index)"
                            size="mini"
                          ></CustomNumber>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-checkbox-group>
                <el-empty
                  :image="empty"
                  description="暂未选择"
                  v-else
                ></el-empty>
              </template>
            </el-form-item>
            <el-form-item label="备用员工：">
              <div class="add-custom">
                <div class="add-custom-btn">
                  <el-button
                    @click="
                      $refs.backupStaffRef.open(
                        formData.backupMembers.departments,
                        formData.backupMembers.users
                      )
                    "
                    size="mini"
                    icon="el-icon-plus"
                    circle
                  ></el-button>
                </div>
                <div class="add-custom-tag">
                  <CustomStretch
                    :height="28"
                    :standard="
                      formData.backupMembers.departments.length +
                        formData.backupMembers.users.length >
                      3
                    "
                  >
                    <el-tag
                      type="info"
                      effect="plain"
                      size="small"
                      closable
                      :disable-transitions="false"
                      @close="
                        formData?.backupMembers?.departments.splice(index, 1)
                      "
                      v-for="(tag, index) in formData?.backupMembers
                        ?.departments"
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
                      effect="plain"
                      size="small"
                      closable
                      :disable-transitions="false"
                      @close="formData?.backupMembers?.users.splice(index, 1)"
                      v-for="(tag, index) in formData?.backupMembers?.users"
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
              <custom-alert
                text="当员工都已达到添加上限或非上线时间，将分配客户给备用员工"
              />
            </el-form-item>
            <el-form-item label="客户标签：">
              <div class="add-custom">
                <div class="add-custom-btn">
                  <el-button
                    @click="$refs.actiontagRef.open(formData.tags)"
                    size="mini"
                    icon="el-icon-plus"
                    circle
                  ></el-button>
                </div>
                <div class="add-custom-tag">
                  <CustomStretch
                    :height="28"
                    :standard="formData.tags.length > 3"
                  >
                    <el-tag
                      type="info"
                      effect="plain"
                      size="small"
                      closable
                      :disable-transitions="false"
                      @close="formData.tags.splice(index, 1)"
                      v-for="(tag, index) in formData.tags"
                      :key="tag.id"
                    >
                      <span class="add-custom-tag-txt">{{ tag.name }}</span>
                    </el-tag>
                  </CustomStretch>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="box-card">
        <el-row :gutter="50">
          <el-col :sm="18" :md="14" :xs="24">
            <div class="custom-title">设置欢迎语</div>
            <el-form-item label="类型：">
              <!-- // 开启的欢迎语类型： ALL(-1,"全部"),
              // CLOSED(0,"不开启欢迎语"),
              // CHANNEL(1,"渠道欢迎语"),
              // USER(2,"员工欢迎语"); -->
              <el-radio-group v-model="formData.welcomeType">
                <el-radio label="USER" border>渠道欢迎语</el-radio>
                <el-radio label="CHANNEL" border>默认欢迎语</el-radio>
                <el-radio label="CLOSED" border>不发送欢迎语</el-radio>
              </el-radio-group>
              <custom-alert
                text="默认欢迎语：将发送员工已设置的欢迎语，若所选员工未设置欢迎语，则不会发送欢迎语"
              />
            </el-form-item>
            <div v-show="formData.welcomeType === 'USER'">
              <el-form-item label="文字消息：">
                <CustomMessageInput
                  :value="
                    formData.welcomeContent.find((item) => {
                      return item.type === 'TEXT';
                    })?.text?.content
                  "
                  @change="descChange"
                />
              </el-form-item>
              <el-form-item label="附件消息：">
                <!-- {{EnclosureListIn}} -->
                <template v-if="$route.query.uuid">
                  <template v-if="formData.id">
                    <EnclosureList
                      ref="EnclosureList"
                      :dataIn="EnclosureListIn"
                      :callback="enclosureListCallback"
                      :type="'LIVE_CODE'"
                    ></EnclosureList>
                  </template>
                </template>
                <template v-else>
                  <EnclosureList
                    ref="EnclosureList"
                    :dataIn="EnclosureListIn"
                    :callback="enclosureListCallback"
                    :type="'LIVE_CODE'"
                  ></EnclosureList>
                </template>
                <!-- <div @click="$refs.EnclosureList.getData()">获取附件数据</div> -->
              </el-form-item>
            </div>
          </el-col>
          <el-col
            :sm="6"
            :md="10"
            :xs="24"
            v-show="formData.welcomeType === 'USER'"
          >
            <PreviewPhone :list="formData.welcomeContent"></PreviewPhone>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="box-card">
        <el-row :gutter="20">
          <el-col :sm="18" :md="14" :xs="24">
            <div class="custom-title">功能设置</div>
            <el-form-item label="自动通过好友：">
              <el-switch v-model="formData.skipVerify" active-color="#81DD9B">
              </el-switch>
              <custom-alert
                text="开启后，客户添加该企业微信时无需验证，将会自动添加成功"
              />
            </el-form-item>
            <el-form-item label="二维码预览：">
              <div class="qrcode-img">
                <img class="qrcode-img-code" src="@/assets/slqrcode.png" />
                <img
                  class="qrcode-img-logo"
                  :src="`data:image/png;base64,${formData.logoMedia?.imageContent.replace(
                    /\\r\\n/g,
                    ''
                  )}`"
                  v-if="formData?.logoMedia?.imageContent"
                />
              </div>

              <div>
                <el-upload
                  action
                  :before-upload="giftImageUpdata"
                  accept="image/*"
                >
                  <el-button type="text" size="mini"
                    >更换二维码中间头像</el-button
                  >
                </el-upload>
              </div>
              <div
                style="
                  color: #999999;
                  font-family: PingFang SC;
                  font-size: 12px;
                "
              >
                此二维码只是样式预览效果，请勿直接使用
              </div>
            </el-form-item>
            <el-form-item>
              <el-button
                @click="$router.go(-1)"
                style="padding-left: 30px; padding-right: 30px"
                round
                >取消</el-button
              >
              <el-button
                style="padding-left: 30px; padding-right: 30px"
                round
                type="primary"
                @click="onSubmit"
              >
                <template v-if="formData.id">保存</template>
                <template v-else>创建活码</template>
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
    </el-form>
    <!-- 添加员工 -->
    <SelectStaff ref="selectstaffRef" @change="usersChange" />
    <!-- 备用员工 -->
    <SelectStaff ref="backupStaffRef" @change="backupMembersChange" />
    <!-- 设置客户上线 -->
    <el-dialog
      title="批量添加客户上线"
      :visible.sync="extUserCeilingVisible"
      width="300px"
      :before-close="extUserCeilingClose"
    >
      <el-form size="small" @submit.prevent.native>
        <el-form-item required label="每日添加客户上线：">
          <CustomNumber
            :max="999"
            :min="1"
            placeholder="请输入内容"
            :value="addExtUserCeiling"
            @change="
              (e) => {
                addExtUserCeiling = e;
              }
            "
            size="mini"
          ></CustomNumber>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="small" @click="extUserCeilingClose" round
          >取 消</el-button
        >
        <el-button
          @click="extUserCeilingConfirm"
          size="small"
          round
          type="primary"
          >确 定</el-button
        >
      </template>
    </el-dialog>
    <ActionTag
      ref="actiontagRef"
      @change="
        (e) => {
          formData.tags = e;
        }
      "
    />
  </div>
</template>
<script>
import CustomMessageInput from "@/components/CustomMessageInput.vue";
import SelectStaff from "@/components/SelectStaff.vue";
import ActionTag from "@/components/ActionTag.vue";
import EnclosureList from "@/components/EnclosureList.vue";
import PreviewPhone from "@/components/PreviewPhone.vue";
export default {
  components: {
    CustomMessageInput,
    SelectStaff,
    ActionTag,
    EnclosureList,
    PreviewPhone,
  },
  data() {
    return {
      empty: require("@/assets/empty-images.png"),
      // 员工添加外部联系人的上限明细
      addExtUserCeilings: [],
      extUserCeilingVisible: false,
      addExtUserCeiling: 1,
      checkAll: false,
      isIndeterminate: false,
      formData: {
        // 员工添加外部联系人的上限明细
        addExtUserLimit: [
          // {
          //   addExtUserCeiling: 100,
          //   inUseStatus: "string",
          //   memberId: "string",
          //   memberName: "string",
          //   thumbAvatar: "string",
          // },
        ],
        // 添加员工上限的状态开关
        addExtUserLimitStatus: false,
        // 备用员工
        backupMembers: {
          // 部门的列表
          departments: [
            //   {
            //     id: 0,
            //     name: "string",
            //   },
          ],
          // 是否是全部员工
          isAll: false,
          // 员工列表
          users: [
            // {
            //   memberName: "string",
            //   memberId: "string",
            // },
          ],
        },

        // 添加员工
        members: {
          // 部门的列表
          departments: [
            // {
            //   id: 0,
            //   name: "string",
            // },
          ],
          // 是否是全部员工
          isAll: true,
          // 员工列表
          users: [
            // {
            //   name: "string",
            //   userId: "string",
            // },
          ],
        },
        // 二维码名称
        name: "",
        // 员工在线状态：1 全天在线
        onlineType: 1,
        // 是否开启验证
        skipVerify: true,
        // 客户标签
        tags: [
          // {
          //   cname: "string",
          //   createTime: "string",
          //   deleted: true,
          //   id: "string",
          //   order: 0,
          // },
        ],
        // 欢迎语列表明细
        welcomeContent: [
          {
            text: {
              content: "",
            },
            type: "TEXT",
          },
          // {
          //   image: {
          //     thumbnailContent: "",
          //     uuid: "",
          //   },
          // },
          // {
          //   file: {
          //     fileName: "string",
          //     size: 0,
          //     title: "string",
          //     type: "EXCEL",
          //     uuid: "string",
          //   },
          //   image: {
          //     thumbnailContent: "string",
          //     uuid: "string",
          //   },
          //   link: {
          //     desc: "string",
          //     thumbnailContent: "string",
          //     title: "string",
          //     url: "string",
          //     uuid: "string",
          //   },
          //   miniProgram: {
          //     appId: "string",
          //     page: "string",
          //     thumbnailContent: "string",
          //     title: "string",
          //     uuid: "string",
          //   },
          //   miniprogram: {
          //     appId: "string",
          //     page: "string",
          //     thumbnailContent: "string",
          //     title: "string",
          //     uuid: "string",
          //   },
          //   text: {
          //     content: "string",
          //   },
          //   type: "FILE",
          //   video: {
          //     thumbnailContent: "string",
          //     title: "string",
          //     uuid: "string",
          //   },
          // },
        ],
        // 二维码logo
        logoMedia: null,
        desc: "321",
        // 开启的欢迎语类型： ALL(-1,"全部"),
        // CLOSED(0,"不开启欢迎语"),
        // CHANNEL(1,"渠道欢迎语"),
        // USER(2,"员工欢迎语");
        welcomeType: "USER",
      },
      checkList: [],
      // 校验
      rules: {
        name: [
          { required: true, message: "请输入二维码名称", trigger: "blur" },
        ],
        onlineType: [
          { required: true, message: "请选择群发员工状态", trigger: "blur" },
        ],
      },
    };
  },
  computed: {
    EnclosureListIn() {
      const list = JSON.parse(JSON.stringify(this.formData.welcomeContent));
      return list.filter((item) => {
        return item.type !== "TEXT";
      });
    },
  },
  created() {
    if (this.$route.query.uuid) {
      this.openDetails();
    }
  },
  methods: {
    extUserLimitStatusChange(e) {
      console.log(e);
      if (!e) {
        this.formData.addExtUserLimit = [];
        this.addExtUserCeilings = []
      }
    },
    enclosureListCallback(data) {
      console.log(990, data);
      const list = JSON.parse(JSON.stringify(this.formData.welcomeContent));
      let textData = list.find((item) => {
        return item.type === "TEXT";
      });
      data.unshift(textData);
      console.log(990, data);
      this.formData.welcomeContent = data;
    },
    // logo上传
    giftImageUpdata(file) {
      this.$utils
        .uploadImg(file, "LOGO", "LIVE_CODE")
        .then((res) => {
          console.log(res);
          this.formData.logoMedia = res;
          // form.loginId = res.data.fullPath;
          this.$message.success("上传成功");
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // 查看详情
    openDetails() {
      this.$api
        .liveCodeDetail({
          channel_uuid: this.$route.query.uuid,
        })
        .then((res) => {
          console.log(1111, JSON.stringify( res));
          if (res.code === 0) {
            this.formData = {
              id: res.data.id ? res.data.id : "",
              uuid: res.data.uuid ? res.data.uuid : "",
              // 员工添加外部联系人的上限明细
              addExtUserLimit: res.data.addExtUserLimit
                ? res.data.addExtUserLimit
                : [],
              // 添加员工上限的状态开关
              addExtUserLimitStatus:
                res.data.addExtUserLimitStatus === false ||
                res.data.addExtUserLimitStatus === true
                  ? res.data.addExtUserLimitStatus
                  : false,
              // 备用员工
              backupMembers: {
                // 部门的列表
                departments: res.data.backupMembers?.departments
                  ? res.data.backupMembers?.departments
                  : [],
                // 是否是全部员工
                isAll: !!res.data.backupMembers?.isAll,
                // 员工列表
                users: res.data.backupMembers?.users
                  ? res.data.backupMembers?.users
                  : [],
              },
              // 二维码logo
              logoMedia: res.data?.logoMedia?.imageContent
                ? res.data.logoMedia
                : null,
              // 添加员工
              members: {
                // 部门的列表
                departments: res.data.members?.departments
                  ? res.data.members?.departments
                  : [],
                // 是否是全部员工
                isAll: !!res.data.members?.isAll,
                // 员工列表
                users: res.data.members?.users ? res.data.members?.users : [],
              },
              // 二维码名称
              name: res.data.name ? res.data.name : "",
              // 员工在线状态：1 全天在线
              onlineType: res.data.onlineType ? res.data.onlineType : 1,
              // 是否开启验证
              skipVerify: !!res.data.skipVerify,
              // 客户标签
              tags: res.data.tags ? res.data.tags : [],
              // 欢迎语列表明细
              welcomeContent: res.data.welcomeContent
                ? res.data.welcomeContent
                : [
                    {
                      text: {
                        content: "",
                      },
                      type: "TEXT",
                    },
                  ],
              // desc: res.data.desc ? res.data.desc : '',
              // 开启的欢迎语类型： ALL(-1,"全部"),
              // CLOSED(0,"不开启欢迎语"),
              // CHANNEL(1,"渠道欢迎语"),
              // USER(2,"员工欢迎语");
              welcomeType: res.data.welcomeType ? res.data.welcomeType : "USER",
            };
          } else {
            this.$message(res.message);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // 同步欢迎语
    descChange(e) {
      // this.$set(this.formData, "desc", e);
      console.log(8888, e);
      this.formData.welcomeContent.find((item) => {
        if (item.type === "TEXT") {
          item.text.content = e;
        }
      });

      console.log(
        8888,
        this.formData.welcomeContent.find((item) => {
          return item.type === "TEXT";
        })
      );
    },
    deleteUsers(index) {
      for (let i = 0; i < this.formData.addExtUserLimit.length; i++) {
        if (!this.formData.addExtUserLimit[i]?.id) {
          if (
            this.formData.addExtUserLimit[i].memberId ===
            this.formData.members.users[index].memberId
          ) {
            this.formData.addExtUserLimit.splice(i, 1);
            i--;
          }
        }
      }
      this.formData.members.users.splice(index, 1);
      this.checkAll = false;
    },
    deleteDepartments(index) {
      for (let i = 0; i < this.formData.addExtUserLimit.length; i++) {
        if (this.formData.addExtUserLimit[i]?.id) {
          if (
            this.formData.addExtUserLimit[i]?.id ===
            this.formData.members.departments[index]?.id
          ) {
            this.formData.addExtUserLimit.splice(i, 1);
            i--;
          }
        }
      }
      this.formData.members.departments.splice(index, 1);
      this.checkAll = false;
    },
    extUserCeilingConfirm() {
      for (let item of this.formData.addExtUserLimit) {
        if (this.addExtUserCeilings.includes(item.memberId)) {
          item.addExtUserCeiling = this.addExtUserCeiling;
          console.log(item);
        }
      }
      this.extUserCeilingVisible = false;
      console.log(this.formData.addExtUserLimit);
    },
    handleCheckAllChange(val) {
      this.addExtUserCeilings = val
        ? this.formData.addExtUserLimit.map((item) => item.memberId)
        : [];
      this.isIndeterminate = false;
    },
    handleCheckedCitiesChange(value) {
      let checkedCount = value.length;
      this.checkAll =
        checkedCount ===
        this.formData.addExtUserLimit.map((item) => item.memberId).length;
      this.isIndeterminate =
        checkedCount > 0 &&
        checkedCount <
          this.formData.addExtUserLimit.map((item) => item.memberId).length;
    },
    extUserCeilingOpen() {
      if (this.formData.addExtUserLimit.length) {
        this.extUserCeilingVisible = true;
      } else {
        this.$message("请添加员工");
      }
    },
    extUserCeilingClose() {
      this.extUserCeilingVisible = false;
    },
    ceilingChange(e, index) {
      this.$set(this.formData.addExtUserLimit[index], "addExtUserCeiling", e);
      console.log(this.formData.addExtUserLimit[index]);
      this.$forceUpdate();
    },
    removal(list, list2) {
      list.find((members) => {
        if (!list2.map((val) => val.memberId).includes(members.memberId)) {
          list2.push(members);
        }
      });
      return list2;
    },
    backupMembersChange(departments = [], users = []) {
      this.formData.backupMembers.departments = departments;
      this.formData.backupMembers.users = users;
    },
    usersChange(departments = [], users = []) {
      this.formData.members.departments = departments;
      this.formData.members.users = users;
      let extArr = this.removal(users, this.formData.addExtUserLimit);
      extArr.find((item) => {
        if (!item.addExtUserCeiling) {
          item.addExtUserCeiling = 1;
        }
        if (!item.inUseStatus && item.inUseStatus !== false) {
          item.inUseStatus = true;
        }
        if (!item.thumbAvatar && item.thumbAvatar !== "") {
          item.thumbAvatar = "";
        }
      });
      this.formData.addExtUserLimit = extArr;
      if (departments.length) {
        for (let item of departments) {
          this.$api
            .contactsMemberList({
              page_num: 1,
              page_size: 1000,
              department_id: item.id,
            })
            .then((res) => {
              console.log(res);
              if (res.code === 0) {
                res.data.members.find((departmentsUser) => {
                  departmentsUser.id = item.id;
                  departmentsUser.addExtUserCeiling = 1;
                  departmentsUser.inUseStatus = true;
                  departmentsUser.thumbAvatar = "";
                });
                this.formData.addExtUserLimit = this.removal(
                  res.data.members,
                  this.formData.addExtUserLimit
                );
              }
            })
            .catch((err) => {
              console.log(err);
            });
        }
      }
    },
    ruleCheck() {
      let bool = true;
      if (this.formData.name === "" || !this.formData.name) {
        bool = false;
        this.$message("请输入二维码名称");
        return;
      }
      if (
        !this.formData.members.departments.length &&
        !this.formData.members.users.length
      ) {
        this.$message("请选择员工");
        bool = false;
        return;
      }
      return bool;
    },
    liveCodeCheckName() {
      if (!this.formData.name) {
        return;
      }
      let data = {
        channel_name: this.formData.name,
      };
      if (this.formData.id) {
        data.channel_id = this.formData.id;
      }
      this.$api
        .liveCodeCheckName(data)
        .then((res) => {
          console.log(res);
          if (res.code !== 0) {
            this.$message.error(res.message);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    onSubmit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if (this.ruleCheck()) {
            console.log(333222);
            let data = {
              channel_name: this.formData.name,
            };
            if (this.formData.id) {
              data.channel_id = this.formData.id;
            }
            this.$api
              .liveCodeCheckName(data)
              .then((res) => {
                console.log(res);
                if (res.code === 0) {
                  this.liveCodeCreate();
                } else {
                  this.$message.error(res.message);
                }
              })
              .catch((err) => {
                console.log(err);
              });
          }
        }
      });
    },
    liveCodeCreate() {
      console.log(JSON.stringify(this.formData))
      this.$api
        .liveCodeCreate(this.formData)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.$message.success("保存成功");
            setTimeout(() => {
              this.$router.go(-1);
            }, 1000);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    handleClose(tag) {
      console.log(tag);
    },
  },
};
</script>
<style lang="scss" scoped>
.custom-title {
  height: 14px;
  color: #333333;
  font-family: PingFang SC;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 16px;
}
.add-custom {
  display: flex;
  align-items: baseline;
  .add-custom-btn {
    flex: none;
    margin-right: 12px;
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
}
.user-info {
  display: flex;
  align-items: center;
  position: relative;
  .user-info-img {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    vertical-align: middle;
    margin-right: 12px;
  }
  .user-info-name {
    vertical-align: middle;
    display: grid;
  }
}
.box-card {
  margin-bottom: 12px;
  ::v-deep(.el-form-item--small) {
    >.el-form-item__label {
      text-align: left;
    }
  }
  .el-radio-group {
    .el-radio {
      margin-right: 0;
    }
  }
}
.custom-table {
  width: 100%;
  font-size: 12px;
  display: table;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  box-sizing: border-box;
  background: #ffffff;
  .custom-table-colgouup {
    display: table-column-group;
    .custom-table-col {
      display: table-column;
    }
  }
  .custom-table-tr {
    display: table-row;
    .custom-table-th {
      display: table-cell;
      background: #fafafa;
      box-sizing: border-box;
      padding: 2px 16px;
      color: #999999;
    }
    .custom-table-td {
      display: table-cell;
      border-top: 1px solid #e0e0e0;
      box-sizing: border-box;
      padding: 6px 16px;
      ::v-deep(.el-checkbox){
        .el-checkbox__label{
          vertical-align: middle;
        }
      }
    }
  }
}
.qrcode-img {
  width: 128px;
  height: 128px;
  position: relative;
  display: flex;
  align-items: center;
  .qrcode-img-code {
    width: 128px;
    height: 128px;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 0;
  }
  .qrcode-img-logo {
    width: 30px;
    height: 30px;
    position: relative;
    z-index: 1;
    margin: auto;
  }
}
</style>