<template>
  <div class="PreviewMemberDE">
    <div class="title">满足以下条件中的 <span>{{/*count*/resData.externalUserCount}}</span>个{{ type == 'SINGLE' ? '客户' : (type == 'GROUP' ? '客户群' : '客户') }}</div>
    <div class="content" v-if="type == 'SINGLE'">
      <div class="left" v-show="!externalUsers.isAll">
        <div>{{ externalUsers.relation == 'AND' ? '且' : '或' }}</div>
      </div>
      <div class="right" v-show="!externalUsers.isAll">
        <el-form ref="baseForm" class="demo-baseForm" label-width="85px">
          <el-form-item label="客户标签：" v-if="externalUsers.corpTagSwitch">
            <section>{{externalUsers.corpTags.relation == 'OR'?'满足任意一个标签的客户':'满足所有标签的客户'}}</section>
            <div class="add-custom-1" v-if="externalUsers.corpTags.tags&&externalUsers.corpTags.tags.length">
              <div v-for="(v,i) in externalUsers.corpTags.tags" :key="i">{{ v.name }}</div>
<!--              <div>每日数据观察</div>-->
            </div>
          </el-form-item>

          <el-form-item label="性别：" style="margin-top: 13px" v-if="externalUsers.genderSwitch">
            <section class="  ">
              {{externalUsers.genders[0] == 1 ? '男' : (externalUsers.genders[0] == 2 ? '女' : '未知')}}
            </section>
          </el-form-item>

          <el-form-item label="添加时间：" v-if="externalUsers.addTimeSwitch">
            <section>{{ externalUsers.startTime }}  <span>至</span> {{ externalUsers.endTime }}</section>

          </el-form-item>

          <el-form-item label="所在群聊：" style="margin-top: 15px;margin-bottom: 16px" v-if="externalUsers.groupChatsSwitch">
            <section v-for="(v,i) in externalUsers.groupChats" :key="i">{{ (i == 0 ? '' : ',') + v.cname }}</section>
          </el-form-item>

        </el-form>
      </div>
      <div class="right" v-show="externalUsers.isAll"><section>全部</section></div>
    </div>
    <div class="content" v-if="type == 'GROUP'" style="height: auto">
      <div class="right" style="margin-left: 20px;margin-top: 15px;margin-bottom: 15px">
        <el-form ref="baseForm" class="demo-baseForm" label-width="85px">
          <el-form-item label="选择的群主：" >
            <div class="add-custom">
<!--              <div v-for="(v,i) in externalUsers.corpTags.tags" :key="i">{{ v.cname }}</div>-->
              <!--              <div>每日数据观察</div>-->
              <div class="add-custom-tag">
                <el-tag
                    type="info"
                    effect="plain"
                    size="small"
                    :closable="false"
                    :disable-transitions="false"
                    v-for="(tag) in formData?.members?.departments"
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
                    :closable="false"
                    :disable-transitions="false"
                    v-for="(tag) in formData?.members?.users"
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
              </div>

            </div>
          </el-form-item>

        </el-form>
      </div>

    </div>
    <div class="content" v-if="type == 'MOMENT'" style="height: auto">
      <div class="right" v-if="externalUsers.isAll" style="margin-left: 20px;margin-top: 15px;margin-bottom: 15px"><section>全部</section></div>
      <div class="right" v-else style="margin-left: 20px;margin-top: 15px;margin-bottom: 15px">
        <el-form ref="baseForm" class="demo-baseForm" label-width="85px">
          <el-form-item label="员工：" >
            <div class="add-custom">
<!--              <div v-for="(v,i) in externalUsers.corpTags.tags" :key="i">{{ v.cname }}</div>-->
              <!--              <div>每日数据观察</div>-->
              <div class="add-custom-tag">
                <el-tag
                    type="info"
                    effect="plain"
                    size="small"
                    :closable="false"
                    :disable-transitions="false"
                    v-for="(tag) in formData?.members?.departments"
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
                    :closable="false"
                    :disable-transitions="false"
                    v-for="(tag) in formData?.members?.users"
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
              </div>

            </div>
          </el-form-item>
          <el-form-item label="客户标签：">
<!--            <section>{{externalUsers.corpTags.relation == 'OR'?'满足任意一个标签的客户':'满足所有标签的客户'}}</section>-->
            <div class="add-custom-1">
              <div v-for="(v,i) in externalUsers.corpTags.tags" :key="i">{{ v.cname }}</div>
              <!--              <div>每日数据观察</div>-->
            </div>
          </el-form-item>
        </el-form>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: "PreviewMemberDE",
  props: {
    type: String,
    userGroupUuid: String,
    count: Number,
  },
  data() {
    return {
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
      resData: {
        "uuid": "a6d42ec5e1e848a883583663c99b5855",
        "memberCount": 6,
        "externalUserCount": 0,
        "userGroup": {
          "userGroupType": "WECOM",
          "weComUserGroupRule": {
            "members": {"isAll": false, "users": [{"memberId": "ZiYan", "memberName": "王思远"}]},
            "externalUsers": {
              "isAll": false,
              "relation": "AND",
              "corpTagSwitch": true,
              "addTimeSwitch": true,
              "groupChatsSwitch": true,
              "genderSwitch": true,
              "genders": [2],
              "corpTags": {
                "relation": "OR",
                "tags": [{
                  "id": "etqPhANwAA2882RnimvCmdv8AO2l_rlQ",
                  "order": 0,
                  "deleted": false
                }, {"id": "etqPhANwAADAweTAnyp6Rzk10WLp-UJw", "order": 0, "deleted": false}]
              },
              "groupChats": [{"chatId": "wrqPhANwAAL89W3Zz_jKE-c8q1Iq8GfA", "cname": "测试群2", "deleted": false}],
              "startTime": "2022-09-29",
              "endTime": "2022-09-30"
            },
            "excludeSwitch": false
          }
        }
      },
      "externalUsers": {
        "isAll": false,
        "relation": "AND",
        "corpTagSwitch": true,
        "addTimeSwitch": true,
        "groupChatsSwitch": true,
        "genderSwitch": true,
        "genders": [2],
        "corpTags": {
          "relation": "OR",
          "tags": [{
            "id": "etqPhANwAA2882RnimvCmdv8AO2l_rlQ",
            "order": 0,
            "deleted": false
          }, {"id": "etqPhANwAADAweTAnyp6Rzk10WLp-UJw", "order": 0, "deleted": false}]
        },
        "groupChats": [{"chatId": "wrqPhANwAAL89W3Zz_jKE-c8q1Iq8GfA", "cname": "测试群2", "deleted": false}],
        "startTime": "2022-09-29",
        "endTime": "2022-09-30"
      }
    };
  },
  computed: {},
  created() {},
  mounted() {
    // console.log(this.count)
    // console.log(this.userGroupUuid)
    let _this = this
    this.$http.get(
        `mktgo/wecom/user_group/query?group_uuid=${this.userGroupUuid}&corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&task_type=${this.type}`,
        {}).then(function (res) {
      console.log(res)
      _this.resData = res.data
      _this.externalUsers = _this.resData.userGroup.weComUserGroupRule.externalUsers
      _this.formData.members = _this.resData.userGroup.weComUserGroupRule.members
    })
  },
  methods: {},
};
</script>
<style lang="scss" scoped>

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
section {
  display: inline-block;
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 12px;
  line-height: 25px;
  color: #333333;
  span {
    color: #999999;
  }
}
.PreviewMemberDE {
  width: 530px;
  //height: 252px;
  .title {
    width: 100%;
    padding-left: 18px;
    height: 36px;
    line-height: 36px;
    border-bottom: 1px solid #E4E4E4;
    font-family: 'PingFang SC';
    font-style: normal;
    font-weight: 400;
    font-size: 12px;
    color: #999999;
    span {
      color: rgba(51, 51, 51, 1);
    }
  }
  .content {
    //width: 609px;
    vertical-align: top;
    min-height: 100px;
    //border: 1px solid rgba(228, 228, 228, 1);
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
        width: 24px;
        //height: 24px;
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
      .el-form-item {
        margin-top: 0!important;
      }
      .add-custom-1 {
        div {
          display: inline-block;
          box-sizing: border-box;
          padding: 0 10px;
          height: 24px;
          line-height: 24px;
          margin-right: 8px;
          background: #FFFFFF;
          border: 1px solid #E1E1E1;
          border-radius: 31px;

          font-family: 'PingFang SC';
          font-style: normal;
          font-weight: 400;
          font-size: 12px;
          /* identical to box height */
          color: #333333;
        }
      }
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