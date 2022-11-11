<template>
  <div class="masscustomer-add">
    <main-head title="新建群发朋友圈" :back="true"></main-head>
    <div class="base-content">
      <div class="content-title">基础信息</div>
      <el-form :model="baseForm" :rules="baseRules" ref="baseForm" label-width="100px" class="demo-baseForm">
        <el-form-item label="任务名称：" prop="name">
          <el-input v-model="baseForm.name"
                    maxlength="20"
                    show-word-limit
                    style="width: 550px"
                    @blur="checkName()"
                    placeholder="请填写，群发名字仅内部可见"
          ></el-input>
        </el-form-item>

<!--        <el-form-item label="群发员工：" prop="members">
&lt;!&ndash;          <i class="el-icon-circle-plus-outline"></i>&ndash;&gt;
          <el-button
              size="mini"
              icon="el-icon-plus"
              circle
          ></el-button>
        </el-form-item>-->
        <el-form-item label="发送范围：" prop="radioXz">
          <div class="style-tab-radio">
            <div class="style-item">
              <el-radio v-model="baseForm.radioXz" label="1">全部客户</el-radio>
            </div>
            <div class="style-item">
              <el-radio v-model="baseForm.radioXz" label="2">按条件筛选客户</el-radio>
            </div>
          </div>
          <div class="choiceKh" style="padding-top: 5px;padding-bottom: 10px" v-show="baseForm.radioXz == 2">
            <el-form-item label="添加员工：" prop="">
              <!--          <i class="el-icon-circle-plus-outline"></i>-->
              <div style="width: 510px">
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
              </div>
            </el-form-item>
            <el-form-item label="客户标签：" prop="">
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


          </div>


<!--          <el-radio disabled v-model="baseForm.radioXz" label="1">全部客户</el-radio>
          <el-radio disabled v-model="baseForm.radioXz" label="2">按条件筛选客户</el-radio>-->
        </el-form-item>
<!--        <el-form-item label="排除客户：" prop="radioPc">
          <el-radio disabled v-model="baseForm.radioPc" label="1">否</el-radio>
          <el-radio disabled v-model="baseForm.radioPc" label="2">是</el-radio>
        </el-form-item>-->

        <el-form-item>
          <div>
            <div class="inline num-text">预计可见微信客户数（未去重）</div>
            <i class="el-icon-refresh num-refresh"
              :class="first || !needReBase ? '' : ' active'"
              @click="first || !needReBase ? '' : submitBaseForm()"
            >&nbsp;刷新数据</i>
          </div>
          <el-button type="text" @click="submitBaseForm()" v-show="first">点击查看</el-button>
          <div class="num" v-show="!first">
<!--            <span>11,453</span>-->
<!--            <span>{{ resDataBase.memberCount ? resDataBase.memberCount : '&#45;&#45;' }}</span>-->
            <span>{{ resDataBase.externalUserCount ? resDataBase.externalUserCount : (resDataBase.externalUserCount == 0 ? 0 : '--') }}</span>
            人
          </div>
        </el-form-item>
      </el-form>
    </div>

    <div class="set-content">
      <el-row :gutter="50">
        <el-col :sm="18" :md="14" :xs="24">
          <div class="content-title">设置群发消息</div>
          <el-form :model="setForm" :rules="setRules" ref="setForm" label-width="100px" class="demo-baseForm">
            <el-form-item label="文字消息：" prop="text">
              <!--          <el-input v-model="setForm.text"
                                  type="textarea"
                                  maxlength="600"
                                  show-word-limit
                                  style="width: 610px;"
                                  rows="6"
                                  placeholder="请填写文字消息"
                        ></el-input>-->
              <div style="width: 100%">
                <CustomMessageInput
                    :value="
                    formData.welcomeContent.find((item) => {
                      return item.type === 'TEXT';
                    })?.text?.content
                  "
                    @change="descChange"
                    :nameBtnShow="false"
                />
              </div>
            </el-form-item>
            <el-form-item label="附件消息：" prop="radioFj">
              <EnclosureList ref="EnclosureList" :dataIn="EnclosureListIn" :isPyq="true" :limit="1" :callback="enclosureListCallback" :type="'MOMENT'"></EnclosureList>


              <!--          <el-radio disabled v-model="setForm.radioFj" label="1">图片</el-radio>
                        <el-radio disabled v-model="setForm.radioFj" label="2">网页</el-radio>
                        <el-radio disabled v-model="setForm.radioFj" label="3">小程序</el-radio>-->
            </el-form-item>
            <el-form-item label="发送方式：" prop="radioFs">
              <div class="style-tab-radio">
                <div class="style-item">
                  <el-radio v-model="setForm.radioFs" label="1">立即发送</el-radio>
                </div>
                <div class="style-item">
                  <el-radio v-model="setForm.radioFs" label="2">定时发送</el-radio>
                </div>
              </div>
              <el-date-picker v-show="setForm.radioFs == 2"
                              v-model="postDataSet.weComMassTaskRequest.scheduleTime"
                              format="yyyy-MM-dd HH:mm:ss"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              type="datetime"
                              placeholder="选择日期">
              </el-date-picker>

              <!--          <el-radio disabled v-model="setForm.radioFs" label="1">立即发送</el-radio>
                        <el-radio disabled v-model="setForm.radioFs" label="2">定时发送</el-radio>-->
            </el-form-item>
            <el-form-item>
              <el-button
                  icon=""
                  round
                  size="mini"
                  style="width: 92px;height: 32px;"
                  @click="resetForm"
              >取 消
              </el-button>
              <el-button
                  type="primary"
                  icon=""
                  size="mini"
                  round
                  style="width: 116px;height: 32px;"
                  @click="submitForm"
              >创 建
              </el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col
            :sm="6"
            :md="10"
            :xs="24"
        >
          <div class="preview">
            <PreviewPhone :list="formData.welcomeContent"></PreviewPhone>
          </div>

        </el-col>
      </el-row>


    </div>

    <!-- 添加员工 -->
    <SelectStaff ref="selectstaffRef" @change="usersChange" />
    <ActionTag
        ref="actiontagRef"
        :type="'select'"
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
import EnclosureList from "@/components/EnclosureList.vue";
import SelectStaff from "@/components/SelectStaff.vue";
import ActionTag from "@/components/ActionTag.vue";
import PreviewPhone from "@/components/PreviewPhonePYQ.vue";
export default {
  name: "masscustomer-add",
  components: {EnclosureList,SelectStaff,CustomMessageInput,ActionTag,PreviewPhone},
  data() {
    return {
      checkAll: false,
      formData: {
        tags: [],
        welcomeContent: [
          {
            text: {
              content: "",
            },
            type: "TEXT",
          },
        ],
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
      },

      first: true,
      baseForm: {
        name: '',
        members: 'ALL',
        radioXz: '1',
        radioPc: '1',
      },
      baseRules: {
        name: [
          { required: true, message: '请输入任务名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        members: [
          { required: true, message: '请选择群发员工', trigger: 'change' }
        ],
        radioXz: [
          { required: true, message: '请选择客户', trigger: 'change' }
        ],
        radioPc: [
          { required: true, message: '请选择是否排除客户', trigger: 'change' }
        ],
      },
      setForm: {
        text: '',
        radioFj: '1',
        radioFs: '1',
      },
      setRules: {
        text: [
          { required: true, message: '请输入文字消息', trigger: 'blur' },
          { min: 1, max: 800, message: '长度在 1 到 800 个字符', trigger: 'blur' }
        ],
        radioFs: [
          { required: true, message: '请选择发送方式', trigger: 'change' }
        ],
      },
      postDataBase: {
        "cdpUserGroupRule": null,
        "offlineUserGroupRule": null,
        "relation": "OR",
        "userGroupType": "WECOM",
        "weComUserGroupRule": {
          "externalUsers": {
            "addTimeSwitch": false,
            "genderSwitch": false,
            "corpTagSwitch": false,
            "corpTags": {
              "relation": "OR",
              "tags": [
                /*{
                  "cname": "核心",
                  "createTime": "string",
                  "deleted": true,
                  "id": "etqPhANwAAL9z2s27j58nsVeDn7a4DNA",
                  "order": 0
                },
                {
                  "cname": "门头沟区",
                  "createTime": "string",
                  "deleted": true,
                  "id": "etqPhANwAA3Tver5WZTwPR7Vjbwkn2Yw",
                  "order": 0
                }*/
              ]
            },
            "endTime": null,
            "genders": null,
            "groupChatsSwitch": false,
            "groupChats": null,
            "isAll": false,
            "relation": "OR",
            "startTime": null
          },
          "excludeSwitch":false,
          "excludeExternalUsers": null,
          "members": {
            "departments": [
              /*{
                "id": 5,
                "name": "河北宛铮科技有限公司"
              }*/
            ],
            "isAll": false,
            "users": [
              /*{
                "name": "王伟宁",
                "userId": "WangWeiNing"
              }*/
            ]
          }
        }
      },
      base_request_id: '',
      needReBase: true,
      resDataBase: {},
      postDataSet: {
        corp_id: this.$store.state.corpId,//企业的企微ID
        project_id: this.$store.state.projectUuid,//企微项目uuid
        task_type: 'SINGLE',//企微群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈
        weComMassTaskRequest: {//创建群发任务
          "canRemind": false,//今天是否提醒发送
          "content": [
            {
              /*"file": {
                "fileName": "string",
                "size": 0,
                "title": "string",
                "type": "EXCEL",
                "uuid": "string"
              },
              "image": {
                "thumbnailContent": "string",
                "uuid": "string"
              },
              "link": {
                "desc": "string",
                "thumbnailContent": "string",
                "title": "string",
                "url": "string",
                "uuid": "string"
              },
              "miniProgram": {
                "appId": "string",
                "page": "string",
                "thumbnailContent": "string",
                "title": "string",
                "uuid": "string"
              },
              "miniprogram": {
                "appId": "string",
                "page": "string",
                "thumbnailContent": "string",
                "title": "string",
                "uuid": "string"
              },*/
              "text": {
                "content": "文本消息内容，最多4000个字节"//文本消息内容，最多4000个字节
              },
              "type": "TEXT",//推送内容类型:TEXT/IMAGE/VOICE/VIDEO/FILE/MINIPROGRAM/LINK
              /*"video": {
                "thumbnailContent": "string",
                "title": "string",
                "uuid": "string"
              }*/
            }
          ],
          "creatorId": "创建人id",//创建人id
          "creatorName": "创建人名称",//创建人名称
          "description": "",//任务描述
          "id": 0,//任务id
          "name": "任务名称",//任务名称
          "scheduleTime": "",//定时执行时间
          "scheduleType": "IMMEDIATE",//群发活动类型， IMMEDIATE 立即；FIXED_TIME 定时
          "taskStatus": "",//任务的执行状态 【UNSTART】未开始 【COMPUTING】人群计算中 【COMPUTE_FAILED】人群获取失败 【SENDING】发送中 【SEND_FAILED】发送失败 【FINISHED】执行完成
          "userGroupUuid": "人群的圈选条件的uuid",//人群的圈选条件的uuid
          "uuid": ""//任务uuid
        }
      },
      set_userGroupUuid: '',

      EnclosureListIn:[],
      fjList: [],

      needcxjs: true,
    }
  },
  watch: {
    'baseForm.radioXz': {
      deep: true,
      handler() {
        this.needcxjs = true
      }
    },
    'formData.tags': {
      deep: true,
      handler() {
        this.needcxjs = true
      }
    },
    'formData.members': {
      deep: true,
      handler() {
        this.needcxjs = true
      }
    }
  },
  methods: {
    // 同步欢迎语
    descChange(e) {
      // this.$set(this.formData, "desc", e);
      console.log(8888, e);
      this.formData.welcomeContent.find((item) => {
        if (item.type === "TEXT") {
          item.text.content = e;
        }
      });
      this.setForm.text = e
      console.log(
          88880000,
          this.formData.welcomeContent.find((item) => {
            return item.type === "TEXT";
          })
      );
    },
    showFdata() {
      console.log(this.formData)
    },
    removal(list, list2) {
      list.find((members) => {
        if (!list2.map((val) => val.memberId).includes(members.memberId)) {
          list2.push(members);
        }
      });
      return list2;
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

      if (departments.length || users.length) {
        this.baseForm.members = 'HAS'
      }
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

    submitBaseForm() {
      let _this = this
      if (this.needReBase) {
        this.base_request_id = (Date.parse(new Date())).toString()
      }
      this.needReBase = false
      this.$refs['baseForm'].validate(async (valid) => {
        if (valid || !valid) {
          // alert('submit!');
          this.first = false

          if (this.baseForm.radioXz == 2) {
            this.postDataBase.weComUserGroupRule.externalUsers.isAll = false
            this.postDataBase.weComUserGroupRule.members.isAll = false
            this.postDataBase.weComUserGroupRule.externalUsers.corpTagSwitch = true
            this.postDataBase.weComUserGroupRule.externalUsers.corpTags.tags = []
            for (let i = 0; i < this.formData.tags.length; i++) {
              this.postDataBase.weComUserGroupRule.externalUsers.corpTags.tags.push({
                "cname": this.formData.tags[i].name,
                "createTime": "",
                "deleted": false,
                "id": this.formData.tags[i].id,
                "order": this.formData.tags[i].order
              })
            }
            if (!this.formData.members.departments.length && !this.formData.members.users.length) {
              this.$message({
                message: '请选择添加员工',
                type: 'warning'
              });
              this.needReBase = true
              return false
            }
            // if (!this.formData.tags.length) {
            //   this.$message({
            //     message: '请选择客户标签',
            //     type: 'warning'
            //   });
            //   this.needReBase = true
            //   return false
            // }

            this.postDataBase.weComUserGroupRule.members.departments = this.formData.members.departments
            this.postDataBase.weComUserGroupRule.members.users = this.formData.members.users
          } else {
            this.postDataBase.weComUserGroupRule.externalUsers.isAll = true
            this.postDataBase.weComUserGroupRule.members.isAll = true
            this.postDataBase.weComUserGroupRule.externalUsers.corpTagSwitch = false
            this.postDataBase.weComUserGroupRule.externalUsers.corpTags.tags = []
            this.postDataBase.weComUserGroupRule.members.departments = []
            this.postDataBase.weComUserGroupRule.members.users = []
          }

          let params = /*{
            corp_id: this.$store.state.corpId,//企业的企微ID
            project_id: this.$store.state.projectUuid,//企微项目uuid
            request_id: (Date.parse(new Date())).toString(),
            task_type: 'SINGLE',//企微群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈
            audienceRules: this.postDataBase
          }*/ this.postDataBase
          let data = await this.$http.post(`mktgo/wecom/user_group/estimate?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&request_id=${this.base_request_id}&task_type=MOMENT`,
              params);
          console.log(data)

          this.needcxjs = false
          if (data.data) {
            if (data.data.status == "COMPUTING") {
              this.needReBase = false
              setTimeout(function () {
                _this.submitBaseForm()
              }, 2000)
            } else if (data.data.status == 'SUCCEED') {
              this.needReBase = true
              if (data.data.externalUserCount == 0 && data.data.memberCount == 0) {
                this.$message({
                  message: '请重新选择人群条件',
                  type: 'warning'
                });
              } else {
                this.$message({
                  message: '查询人群成功',
                  type: 'success'
                });
                this.resDataBase = data.data
                this.set_userGroupUuid = data.data.uuid
              }
            } else if (data.data.status == 'FAILED') {
              this.needReBase = true
              this.$message({
                message: '请重新选择人群条件',
                type: 'warning'
              });
            }
          }

        } else {
          console.log('error submit!!');
          this.first = true
          return false;
        }
      });
    },
    async submitForm() {
      let hasName = await this.checkName()
      if (hasName.code == '1004') {
        return false
      }
      if (!this.set_userGroupUuid || !this.resDataBase.externalUserCount) {
        this.$message.error('请选择有效的人群');
        return false
      }
      if (this.needcxjs) {
        this.$message.error('人群条件已修改，请选重新计算人群');
        return false
      }
      await this.$refs['baseForm'].validate((valid) => {
        if (valid) {
          // alert('submit!');

          this.$refs['setForm'].validate(async (valid) => {
            if (valid) {
              // alert('submit!');
              let params = {
                "canRemind": true,
                "content": [
                  /*{
                    "text": {
                      "content": /!*this.setForm.text*!/
                      this.formData.welcomeContent.find((item) => {
                        return item.type === "TEXT";
                      }).text.content
                    },
                    "type": "TEXT"
                  }*/
                ],
                "creatorId": this.$store.state.user.userName,
                "creatorName": this.$store.state.user.userName,
                "description": null,
                "id": null,
                "name": this.baseForm.name,
                "scheduleTime": this.setForm.radioFs == 1 ? null : this.postDataSet.weComMassTaskRequest.scheduleTime,
                "scheduleType": this.setForm.radioFs == 1 ? "IMMEDIATE" : 'FIXED_TIME',// IMMEDIATE 立即；FIXED_TIME 定时
                "taskStatus": null,
                "userGroupUuid": this.set_userGroupUuid/*"ca023c0d4b654812aff1504d0f16eadf"*/,
                "uuid": null
              }
              // console.log(this.$store.state)
              this.$refs.EnclosureList.getData()
              console.log(this.fjList)
              for (let i = 0; i < this.fjList.length; i++) {
                params.content.push(this.fjList[i])
              }

              // console.log(params)
              // return false

              let data = await this.$http.post(`mktgo/wecom/mass_task/save?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=MOMENT`,
                  params);
              console.log(data)
              if (data.code == 0 && data.message == 'success') {
                history.back()
              }
            } else {
              console.log('error submit!!');
              return false;
            }
          });

        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm() {
      history.back()
    },
    async checkName() {
      let data = await this.$http.get(`mktgo/wecom/mass_task/check_name?project_id=${this.$store.state.projectUuid}&task_name=${this.baseForm.name}&task_type=MOMENT`,
          {});
      console.log(data)
      return data
    },
    enclosureListCallback(data) {
      console.log(data)
      this.fjList = data
      const list = JSON.parse(JSON.stringify(this.formData.welcomeContent))
      let textData = list.find((item) => {
        return item.type === "TEXT";
      });
      data.unshift(textData);
      this.formData.welcomeContent = data;
    }
  }
}
</script>

<style scoped lang="scss">
.choiceKh {
  width: 606px;
  min-height: 50px;

  mix-blend-mode: normal;
  border: 1px solid #E0E0E0;
  border-radius: 2px;
}
.add-custom {
  display: flex;
  align-items: baseline;
  .add-custom-btn {
    flex: none;
  }
  .add-custom-tag {
    flex: auto;
    margin-left: 12px;
    .el-tag {
      border-radius: 40px;
      margin-right: 6px;
      margin-bottom: 6px;
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
.style-tab-radio {
  width: 609px;
  height: 30px;
  //border: 1px solid rgba(228, 228, 228, 1);
  border-radius: 2px;
  margin-bottom: 25px;
  .style-item {
    width: auto;
    height: 30px;
    border: 1px solid rgba(228, 228, 228, 1);
    display: inline-block;
    padding: 0 10px;
    margin-top: 5px;
    margin-right: 8px;
    .el-radio {
      line-height: 30px !important;
      vertical-align: top;
    }
  }
}
.masscustomer-add {
  .base-content {
    padding: 24px 12px;
    background-color: white;
    border-radius: 8px;
    .num-text {
      margin-right: 10px;
    }
    .num-refresh {
      color: #999999;
      opacity: 0.3;
      &.active {
        opacity: 1;
        cursor: pointer;
      }
    }
    .num {
      span {
        font-family: 'PingFang SC';
        font-style: normal;
        font-weight: 600;
        font-size: 20px;
        line-height: 28px;
        color: #678FF4;
      }
    }
  }
  .set-content {
    margin-top: 12px;
    padding: 24px 12px;
    background-color: white;
    border-radius: 8px;
    position: relative;
    .preview {
      //position: absolute;
      //left: 780px;
      //top: 52px;
    }
  }
}
</style>