<template>
  <div class="condition">
    <div class="style-tab">
      <div class="style-item">
        <el-checkbox v-model="checked1" >客户标签</el-checkbox>
      </div>
      <div class="style-item">
        <el-checkbox v-model="checked2" >性别</el-checkbox>
      </div>
      <div class="style-item">
        <el-checkbox v-model="checked3" >添加时间</el-checkbox>
      </div>
      <div class="style-item">
        <el-checkbox v-model="checked4" >所在群聊</el-checkbox>
      </div>
    </div>

    <div class="content">
      <div class="left">
        <div @click="externalUsers.relation == 'AND' ? externalUsers.relation = 'OR' : externalUsers.relation = 'AND'">{{ externalUsers.relation == 'AND' ? '且' : '或' }}</div>
      </div>
      <div class="right">
        <el-form ref="baseForm" class="demo-baseForm" label-width="85px">
          <el-form-item label="客户标签：" v-show="checked1">
            <el-select v-model="value" placeholder="" style="width: 374px">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
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

          <el-form-item label="性别：" v-show="checked2" style="margin-top: 13px">
            <div class="style-tab-radio">
              <div class="style-item">
                <el-radio v-model="genders" label="1">男</el-radio>
              </div>
              <div class="style-item">
                <el-radio v-model="genders" label="2">女</el-radio>
              </div>
              <div class="style-item">
                <el-radio v-model="genders" label="0">未知</el-radio>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="添加时间：" v-show="checked3">
            <el-date-picker
                style="width: 374px"
                type="daterange"
                v-model="startTime"
                align="right"
                unlink-panels
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                :picker-options="pickerOptions"
            >
            </el-date-picker>

          </el-form-item>

          <el-form-item label="所在群聊：" v-show="checked4" style="margin-top: 15px;margin-bottom: 16px">
            <el-select v-model="valueGroup" multiple placeholder="请选择" style="width: 374px">
              <el-option
                  v-for="item in optionsGroup"
                  :key="item.groupChatId"
                  :label="item.groupChatName"
                  :value="item.groupChatId">
              </el-option>
            </el-select>

          </el-form-item>

        </el-form>
      </div>
    </div>
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
import ActionTag from "@/components/ActionTag.vue";
export default {
  name: "condition",
  components : {ActionTag},
  data() {
    return {
      formData: {
        tags: [],
      },
      checked1:true,
      checked2:false,
      checked3:false,
      checked4:false,
      options: [{
        value: 'OR',
        label: '满足任意一个标签的客户'
      }, {
        value: 'AND',
        label: '满足所有标签的客户'
      }],
      value: 'OR',
      genders: '1',
      startTime:'',
      pickerOptions: {
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      optionsGroup: [{
        count: 100,//企业的客户群成员总数
        createTime: 100,	//string客户群创建时间
        groupChatId: 100,	//string企业的客户群id
        groupChatName: 'string企业的客户群名称',	//string企业的客户群名称
        ownerId: 100,	//string企业的客户群群主ID
        ownerName: 100,	//string企业的客户群群主名称
    }],
      valueGroup: [],

      "externalUsers": {
        "addTimeSwitch": false,
        "genderSwitch": false,
        "corpTagSwitch": true,
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
        "relation": 'AND'/*"OR"*/,
        "startTime": null
      }
    }
  },
  created() {
    let _this = this
    this.$http.get(`mktgo/wecom/contacts/group_chat/list?corp_id=${this.$store.state.corpId}&page_num=1&page_size=10000&project_id=${this.$store.state.projectUuid}`,
        {}).then(function (res) {
      _this.optionsGroup = res.data?.groupChats || []
    })
  },
  methods: {
    getData() {
      this.externalUsers.addTimeSwitch = this.checked3
      this.externalUsers.genderSwitch = this.checked2
      this.externalUsers.corpTagSwitch = this.checked1
      this.externalUsers.groupChatsSwitch = this.checked4
      this.externalUsers.corpTags.relation = this.value
      this.externalUsers.corpTags.tags = []
      for (let i = 0; i < this.formData.tags.length; i++) {
        this.externalUsers.corpTags.tags.push({
          "name": this.formData.tags[i].name,
          "createTime": "",
          "deleted": false,
          "id": this.formData.tags[i].id,
          "order": this.formData.tags[i].order
        })
      }
      if (!this.externalUsers.corpTagSwitch || !this.formData.tags.length) {
        this.externalUsers.corpTags.tags = null
      }
      /*if (!this.externalUsers.corpTagSwitch) {
        this.externalUsers.corpTags = null
      }*/
      this.externalUsers.startTime = this.externalUsers.addTimeSwitch && this.startTime ? this.startTime[0] : null
      this.externalUsers.endTime = this.externalUsers.addTimeSwitch && this.startTime ? this.startTime[1] : null
      this.externalUsers.genders = this.externalUsers.genderSwitch ? [this.genders] : null
      this.externalUsers.groupChats = this.externalUsers.groupChatsSwitch ? [] : null
      if (this.externalUsers.groupChatsSwitch) {
        for (let i = 0; i < this.valueGroup.length; i++) {
          for (let o = 0; o < this.optionsGroup.length; o++) {
            if (this.valueGroup[i] == this.optionsGroup[o].groupChatId) {
              this.externalUsers.groupChats.push({
                chatId:	this.optionsGroup[o].groupChatId,
                cname: this.optionsGroup[o].groupChatName,
                deleted: false
              })
            }
          }
        }
      }

      return this.externalUsers
      // console.log(this.externalUsers)
      /*let obj = {}
      if () {

      }*/
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
    //flex: auto;
    width: 337px;
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
.condition {
  .style-tab {
    width: 609px;
    height: 30px;
    border: 1px solid rgba(228, 228, 228, 1);
    border-radius: 2px;

    .style-item {
      width: auto;
      height: 30px;
      border-right: 1px solid rgba(228, 228, 228, 1);
      display: inline-block;
      padding: 0 10px;

      .el-checkbox {
        line-height: 30px !important;
        vertical-align: top;
      }
    }
  }
  .content {
    width: 609px;
    vertical-align: top;
    min-height: 100px;
    border: 1px solid rgba(228, 228, 228, 1);
    border-top: 0;
    position: relative;
    .left {
     position: absolute;
     width: 10px;
     left: 30px;
     top: 33px;
     height: calc(100% - 66px);
     border: 1px solid #EAEAEA;
     border-right: 0;
     div {
       cursor: pointer;
       width: 24px;
       height: 24px;
       line-height: 24px;
       position: absolute;
       text-align: center;
       left: -12px;
       background-color: white;
       border: 1px solid #EAEAEA;
       font-family: 'PingFang SC';
       font-style: normal;
       font-weight: 400;
       font-size: 12px;
       /* identical to box height */
       color: #678FF4;
       top: calc(50% - 13px);
     }
   }
    .right {
      margin-left: 50px;
      padding-top: 15px;

      .style-tab-radio {
        width: 609px;
        height: 30px;
        //border: 1px solid rgba(228, 228, 228, 1);
        border-radius: 2px;
        margin-bottom: 25px;
        //margin-top: 13px;
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
    }
  }
}
</style>