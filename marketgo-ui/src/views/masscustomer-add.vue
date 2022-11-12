<template>
  <div class="masscustomer-add">
    <main-head title="新建群发客户" :back="true"></main-head>
    <div class="base-content">
      <div class="content-title">基础信息</div>
      <el-form :model="baseForm" :rules="baseRules" ref="baseForm" label-width="100px" class="demo-baseForm">
        <el-form-item label="群发名称：" prop="name">
          <el-input v-model="baseForm.name"
                    maxlength="20"
                    show-word-limit
                    style="width: 550px"
                    @blur="checkName()"
                    placeholder="请填写，群发名字仅内部可见"
          ></el-input>
        </el-form-item>

        <el-form-item label="群发员工：" prop="members">
<!--          <i class="el-icon-circle-plus-outline"></i>-->
          <div style="width: 606px">
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


<!--          <el-button
              size="mini"
              icon="el-icon-plus"
              circle
              @click="showFdata()"
          ></el-button>-->
        </el-form-item>
        <el-form-item label="选择客户：" prop="radioXz">
          <div class="style-tab-radio">
            <div class="style-item">
              <el-radio v-model="baseForm.radioXz" label="1">全部客户</el-radio>
            </div>
            <div class="style-item">
              <el-radio v-model="baseForm.radioXz" label="2">指定客户</el-radio>
            </div>
          </div>
          <condition ref="condition" v-show="baseForm.radioXz == 2"></condition>
<!--          <div @click="$refs.condition.getData()">huoqu</div>-->
        </el-form-item>
        <el-form-item label="排除客户：" prop="radioPc">
<!--          <el-radio disabled v-model="baseForm.radioPc" label="1">否</el-radio>
          <el-radio disabled v-model="baseForm.radioPc" label="2">是</el-radio>-->
          <div class="style-tab-radio">
            <div class="style-item">
              <el-radio v-model="baseForm.radioPc" label="1">否</el-radio>
            </div>
            <div class="style-item">
              <el-radio v-model="baseForm.radioPc" label="2">是</el-radio>
            </div>
          </div>
          <condition ref="conditionPc" v-show="baseForm.radioPc == 2"></condition>
        </el-form-item>

        <el-form-item>
          <div>
            <div class="inline num-text">预计发送客户数</div>
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
              <!--          <el-radio disabled v-model="setForm.radioFj" label="1">图片</el-radio>
                        <el-radio disabled v-model="setForm.radioFj" label="2">网页</el-radio>
                        <el-radio disabled v-model="setForm.radioFj" label="3">小程序</el-radio>-->

              <EnclosureList ref="EnclosureList" :dataIn="EnclosureListIn" :limit="9" :callback="enclosureListCallback" :type="'SINGLE'"></EnclosureList>
              <!--          <div @click="$refs.EnclosureList.getData()">获取附件数据</div>-->
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
  </div>
</template>

<script>
import CustomMessageInput from "@/components/CustomMessageInput.vue";
import EnclosureList from "@/components/EnclosureList.vue";
import condition from "@/components/condition.vue";
import SelectStaff from "@/components/SelectStaff.vue";
import PreviewPhone from "@/components/PreviewPhone.vue";

export default {
  name: "masscustomer-add",
  components: {EnclosureList,condition,SelectStaff,CustomMessageInput,PreviewPhone},
  data() {
    return {
      checkAll: false,
      formData: {
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
        members: '',
        radioXz: '1',
        radioPc: '1',
      },
      baseRules: {
        name: [
          { required: true, message: '请输入群发名称', trigger: 'blur' },
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
            "corpTagSwitch": true,
            "corpTags": {
              "relation": "OR",
              "tags": [
                {
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
                }
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

      EnclosureListIn: [
        /*{
          image: {
            imageContent:'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',
            mediaUuid:"a4596190b48b41e58c62d612ffa21ec7",
          }
        },
        {
          'link': {
            title: 'this.arr[i].typeLink.title',
            desc: 'this.arr[i].typeLink.des',
            url: 'this.arr[i].typeLink.url',
            imageContent:'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',
            mediaUuid:'a4596190b48b41e58c62d612ffa21ec7',
          }
        },
        {
          'miniProgram': {
            title: 'this.arr[i].miniApp.title',
            appId: 'this.arr[i].miniApp.appid',
            page: 'this.arr[i].miniApp.path',
            imageContent:'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',
            mediaUuid:'a4596190b48b41e58c62d612ffa21ec7',
          }
        }*/
      ],
      fjList: [],

      needcxjs: true,
      conditionStr: '',
      conditionPcStr: '',
      conditionStr1: '',
      conditionPcStr1: '',
    }
  },
  watch: {
    'baseForm.radioXz': function (n) {
      this.needcxjs = true
      if (n == 2) {
        if (!this.$refs.condition.checked1 && !this.$refs.condition.checked2 && !this.$refs.condition.checked3 && !this.$refs.condition.checked4) {
          this.$refs.condition.checked1 = true
        }
      }
    },
    'baseForm.radioPc': function (n) {
      this.needcxjs = true
      if (n == 2) {
        if (!this.$refs.conditionPc.checked1 && !this.$refs.conditionPc.checked2 && !this.$refs.conditionPc.checked3 && !this.$refs.conditionPc.checked4) {
          this.$refs.conditionPc.checked1 = true
        }
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

          if (this.baseForm.radioXz == 1) {
            this.$refs.condition.checked1 = false
            this.$refs.condition.externalUsers.isAll = true
          }
          this.postDataBase.weComUserGroupRule.externalUsers = this.$refs.condition.getData()
          this.conditionStr = JSON.stringify(this.postDataBase.weComUserGroupRule.externalUsers)

          if (this.baseForm.radioPc == 1) {
            this.postDataBase.weComUserGroupRule.excludeSwitch = false
            this.postDataBase.weComUserGroupRule.excludeExternalUsers = null
          } else {
            this.postDataBase.weComUserGroupRule.excludeSwitch = true
            this.postDataBase.weComUserGroupRule.excludeExternalUsers = this.$refs.conditionPc.getData()
            this.conditionPcStr = JSON.stringify(this.postDataBase.weComUserGroupRule.excludeExternalUsers)
          }

          this.postDataBase.weComUserGroupRule.members.departments = this.formData.members.departments
          this.postDataBase.weComUserGroupRule.members.users = this.formData.members.users

          let params = /*{
            corp_id: this.$store.state.corpId,//企业的企微ID
            project_id: this.$store.state.projectUuid,//企微项目uuid
            request_id: (Date.parse(new Date())).toString(),
            task_type: 'SINGLE',//企微群发类型; SINGLE 群发好友; GROUP 群发客户群; MOMENT 群发朋友圈
            audienceRules: this.postDataBase
          }*/ this.postDataBase
          let data = await this.$http.post(`mktgo/wecom/user_group/estimate?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&request_id=${this.base_request_id}&task_type=SINGLE`,
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
      this.conditionStr1 = JSON.stringify(this.$refs.condition.getData())
      this.conditionPcStr1 = JSON.stringify(this.$refs.conditionPc.getData())
      if (this.baseForm.radioXz != 1) {
        if (this.conditionStr1 != this.conditionStr) {
          this.needcxjs = true
        }
      }
      if (this.baseForm.radioPc != 1) {
        if (this.conditionPcStr1 != this.conditionPcStr) {
          this.needcxjs = true
        }
      }
      /*if (this.conditionStr1 != this.conditionStr || this.conditionPcStr1 != this.conditionPcStr) {
        this.needcxjs = true
      }*/
      /*console.log('conditionStr1'+this.conditionStr1)
      console.log('conditionStr'+this.conditionStr)
      console.log('conditionPcStr1'+this.conditionPcStr1)
      console.log('conditionPcStr'+this.conditionPcStr)*/

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

              let data = await this.$http.post(`mktgo/wecom/mass_task/save?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=SINGLE`,
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
      let data = await this.$http.get(`mktgo/wecom/mass_task/check_name?project_id=${this.$store.state.projectUuid}&task_name=${this.baseForm.name}&task_type=SINGLE`,
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
    width: calc(100% - 24px);
    position: relative;
    .preview {
      //position: absolute;
      //left: 780px;
      //top: 52px;
    }
  }
}
</style>