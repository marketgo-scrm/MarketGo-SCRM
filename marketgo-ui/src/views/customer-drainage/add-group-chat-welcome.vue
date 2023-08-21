<template>
  <div class="masscustomer-add">
    <main-head :title="this.$route.query.uuid ? '编辑入群欢迎语' : '新建入群欢迎语'" :back="true"></main-head>
    <div class="base-content">
      <div class="content-title">基础信息</div>
      <el-form :model="baseForm" :rules="baseRules" ref="baseForm" label-width="100px" class="demo-baseForm">
        <el-form-item label="标题：" prop="name">
          <el-input v-model="baseForm.name" maxlength="20" show-word-limit style="width: 550px" @blur="checkName()"
            placeholder="请填写欢迎语标题"></el-input>
        </el-form-item>
        <el-form-item label="发送方式：" required>
          <div class="style-tab-radio">
            <div class="style-item">
              <el-radio v-model="baseForm.notifyType" label="1">所有群主</el-radio>
            </div>
            <div class="style-item">
              <el-radio v-model="baseForm.notifyType" label="2">指定群主</el-radio>
            </div>
            <div class="style-item">
              <el-radio v-model="baseForm.notifyType" label="0">不通知群主</el-radio>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="请选择群主:" v-show="baseForm.notifyType == 2">
          <el-select style="width: 20%" multiple collapse-tags v-model="baseForm.members" placeholder="请选择" clearable>
            <el-option v-for="(dict, index) in memberList" :key="index" :label="dict.memberName" :value="dict.memberId" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <div class="set-content">
      <el-row :gutter="50">
        <el-col :sm="18" :md="14" :xs="24">
          <div class="content-title">设置欢迎语内容</div>
          <el-form :model="setForm" :rules="setRules" ref="setForm" label-width="100px" class="demo-baseForm">
            <el-form-item label="文字消息：" prop="text">
              <div style="width: 100%">
                <CustomMessageInput :value="formData.welcomeContent.find((item) => {
                  return item.type === 'TEXT';
                })?.text?.content
                  " @change="descChange" />
              </div>
            </el-form-item>
            <el-form-item label="附件消息：" prop="radioFj">

              <EnclosureList ref="EnclosureList" :dataIn="EnclosureListIn" :limit="9" :callback="enclosureListCallback" :key="enclosureListKey"
                :type="'GROUP'"></EnclosureList>

            </el-form-item>
            <el-form-item>
              <el-button icon="" round size="mini" style="width: 92px;height: 32px;" @click="resetForm">取 消
              </el-button>
              <el-button type="primary" icon="" size="mini" round style="width: 116px;height: 32px;" @click="submitForm">
                {{ this.$route.query.uuid ? '保存' : '创建'
                }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :sm="6" :md="10" :xs="24">
          <div class="preview">
            <PreviewPhone :list="formData.welcomeContent"></PreviewPhone>
          </div>

        </el-col>
      </el-row>

    </div>

    <ActionTag ref="actiontagRef" @change="(e) => {
      formData.tags = e;
    }
      " />
    <!-- 添加员工
    <SelectStaff ref="selectstaffRef" @change="usersChange" /> -->
  </div>
</template>
    
<script>
import CustomMessageInput from "@/components/CustomMessageInput.vue";
import EnclosureList from "@/components/EnclosureList.vue";
import ActionTag from "@/components/ActionTag.vue";
// import SelectStaff from "@/components/SelectStaff.vue";
import PreviewPhone from "@/components/PreviewPhone.vue";
export default {
  name: "masscustomer-add",
  components: { EnclosureList, CustomMessageInput, PreviewPhone, ActionTag },
  data() {
    return {
      memberList: [],
      detailData: {},
      materialIds: [],
      enclosureListKey:1,
      formData: {
        welcomeContent: [
          {
            text: {
              content: "",
            },
            type: "TEXT",
          },
        ],
      },

      baseForm: {
        notifyType: '1',
        name: '',
        members: [],
      },
      baseRules: {
        name: [
          { required: true, message: '请填写欢迎语标题', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
      },
      setForm: {

        text: '',
      },
      setRules: {
        text: [
          { required: true, message: '请输入文字消息', trigger: 'blur' },
          { min: 1, max: 800, message: '长度在 1 到 800 个字符', trigger: 'blur' }
        ],
      },


      EnclosureListIn: [],
      fjList: [],
    }
  },
  mounted() {
    if (this.$route.query.uuid) {
      this.openDetails()
    }
    if (this.$route.query.materialIds) {
      this.materialIds = this.$route.query.materialIds
      this.getMaterialInfo(this.$route.query.materialIds)
    }
    this.getMemberList()
  },
  methods: {


    getMemberList() {

      let _this = this
      this.$http.get(`mktgo/wecom/contacts/group_chat/list?corp_id=${this.$store.state.corpId}&page_num=1&page_size=10000&project_id=${this.$store.state.projectUuid}`,
        {}).then(function (res) {

          //_this.memberList = res.data?.groupChats || []
          _this.memberList = []
          for (let item of res.data?.groupChats) {

            let member1 = _this.memberList.find((itemIn) => {
              return item.ownerId === itemIn.memberId;
            });
            if (!member1) {
              let member = {
                memberId: item.ownerId,
                memberName: item.ownerName
              }
              _this.memberList.push(member)

            }
          }
        })
    },

    // 查看详情
    openDetails() {
      this.$api
        .groupWelcomeDetail({
          uuid: this.$route.query.uuid,
        })
        .then((res) => {
          console.log(1111, JSON.stringify(res));
          if (res.code === 0) {
            this.detailData = res.data
            this.baseForm.name = res.data.name

            if (res.data.members) {
              for (let item of res.data.members.users) {
                this.baseForm.members.push(item.memberId)
              }
            }
            this.baseForm.notifyType = res.data.notifyType.toString()
            this.formData.welcomeContent = res.data.welcomeContent
            let array = res.data.welcomeContent.filter((item) => {
              return item.type !== "TEXT";
            });
            let textContent = res.data.welcomeContent.find((itemIn) => {
              return itemIn.type === "TEXT";
            });
            this.setForm.text = textContent.text.content

            if (array.length > 0) {
              this.EnclosureListIn =  array
              this.enclosureListKey++;
             // this.$refs.EnclosureList.getsync(this.EnclosureListIn, true);
            }

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
      this.setForm.text = e
      console.log(
        88880000,
        this.formData.welcomeContent.find((item) => {
          return item.type === "TEXT";
        })
      );
    },

    async submitForm() {
      let hasName = await this.checkName()
      if (hasName.code == '1004') {
        return false
      }

      await this.$refs['baseForm'].validate((valid) => {
        if (valid) {

          this.$refs['setForm'].validate(async (valid) => {
            if (valid) {

              let tmpMemberList = []
              console.log('baseForm.members:' + JSON.stringify(this.baseForm.members))
              console.log('memberList:' + JSON.stringify(this.memberList))

              for (let memberId of this.baseForm.members) {

                let member = this.memberList.find((item) => {

                  return memberId === item.memberId;
                });

                tmpMemberList.push(member)

              }
              let membersObj = {
                departments: null,
                users: tmpMemberList
              }
              if (this.setForm.notifyType == 2 && this.baseForm.members?.length == 0) {
                this.$message('至少需要选择一位群主');
              }

              let params = {
                "welcomeContent": [
                ],
                creatorId: this.$store.state.user.userName,
                creatorName: this.$store.state.user.userName,
                id: this.detailData.id,
                name: this.baseForm.name,
                uuid: this.detailData.uuid,
                notifyType: this.baseForm.notifyType,

              }
              //  alert(JSON.stringify(membersObj))

              if (this.baseForm.notifyType == 2) {
                params.members = membersObj
              }
              this.$refs.EnclosureList.getData()
              //  console.log(this.fjList)
              for (let i = 0; i < this.fjList.length; i++) {
                params.welcomeContent.push(this.fjList[i])
              }

              console.log(JSON.stringify(params))
              let data = await this.$http.post(`mktgo/wecom/welcome/group_chat/save?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}`,
                params);
              console.log(data)
              if (data.code == 0 && data.message == 'success') {
                history.back()
              }
            } else {
              console.log('error submit!!---');
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
      if (this.detailData?.uuid && this.detailData?.name === this.baseForm.name) {
        return true
      }

      let data = await this.$http.get(`mktgo/wecom/welcome/group_chat/check_name?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&name=${this.baseForm.name}`,
        {});
      console.log(data)
      return data
    },
    enclosureListCallback(data) {
    //  alert(33555555)
      console.log(data)
      this.fjList = data
      const list = JSON.parse(JSON.stringify(this.formData.welcomeContent))
      let textData = list.find((item) => {
        return item.type === "TEXT";
      });
      data.unshift(textData);
      this.formData.welcomeContent = data;
      // let otherData = data.find((item) => {
      //   return item.type != "TEXT";
      // });

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

    ::v-deep(.el-radio__label) {
      font-size: 12px !important;
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