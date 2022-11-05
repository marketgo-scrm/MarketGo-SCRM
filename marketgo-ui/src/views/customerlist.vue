<template>
  <div>
    <div class="tops">
      <h2>客户列表</h2>
      <!-- <span>XXXXXXXX</span> -->
    </div>
    <div class="content">
      <el-form
        :model="queryParams"
        size="small"
        ref="queryForm"
        inline
        v-show="showSearch"
        label-width="90px"
      >
        <!-- <el-row>
        <el-col :span='8'>    -->
        <el-form-item label="添加时间">
          <el-date-picker
            style="width: 100%"
            type="daterange"
            v-model="queryParams.times"
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
        <!-- </el-col>
        <el-col :span='8'> -->
        <el-form-item label="客户" prop="external_user">
          <el-input
            prefix-icon="el-icon-search"
            v-model="queryParams.keyword"
            placeholder="请输入客户名称"
            clearable
          />
        </el-form-item>
        <!-- </el-col>
        <el-col :span='8'> -->
        <el-form-item label="性别" prop="gender">
          <el-select
            style="width: 100%"
            multiple
            collapse-tags
            v-model="queryParams.gender"
            placeholder="请选择性别"
            clearable
          >
            <el-option
              v-for="(dict, index) in sexlist"
              :key="index"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <!-- </el-col>
        <el-col :span='8'> -->
        <el-form-item label="所属员工" prop="member_ids">
          <el-input
            @click.native="$refs.selectstaffRef.open(queryParams.member_ids)"
            clearable
            placeholder="请选择所属员工"
            suffix-icon="el-icon-plus"
            readonly
            :value="
              queryParams.member_ids.map((item) => item.memberName).join(',')
            "
          >
          </el-input>
          <!-- <el-select style='width:100%'
           multiple
    collapse-tags
            v-model="queryParams.member_ids"
            placeholder="请选择所属员工"
            clearable
          >
            <el-option
              v-for="(dict, index) in userlist"
              :key="index"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select> -->
        </el-form-item>
        <!-- </el-col>
        <el-col :span='8'> -->
        <el-form-item label="标签">
          <el-input
            @click.native="$refs.actiontagRef.open(queryParams.tags)"
            type="text"
            clearable
            placeholder="请选择标签"
            suffix-icon="el-icon-plus"
            readonly
            :value="queryParams.tags.map((item) => item.name).join(',')"
          >
          </el-input>
        </el-form-item>
        <!-- </el-col>
        <el-col :span='8'> -->
        <el-form-item label="添加渠道 " prop="channels">
          <el-select
            style="width: 100%"
            multiple
            collapse-tags
            v-model="queryParams.channels"
            placeholder="请选择添加渠道 "
            clearable
          >
            <el-option
              v-for="(dict, index) in jdlist"
              :key="index"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <!-- </el-col>
        <el-col :span='8'> -->
        <el-form-item label="流失状态 " prop="statuses ">
          <el-select
            style="width: 100%"
            multiple
            collapse-tags
            v-model="queryParams.statuses"
            placeholder="请选择流失状态 "
            clearable
          >
            <el-option
              v-for="(dict, index) in dictlist"
              :key="index"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <!-- </el-col>
        <el-col :span='8'> -->
        <el-form-item label="所在群聊" prop="group_chats">
          <el-select
            style="width: 100%"
            multiple
            collapse-tags
            v-model="queryParams.group_chats"
            placeholder="请选择所在群聊"
            clearable
          >
            <el-option
              v-for="dict in chatlist"
              :key="dict.groupChatId"
              :label="dict.groupChatName"
              :value="dict.groupChatId"
            />
          </el-select>
        </el-form-item>
        <!-- </el-col>
        <el-col :span='8'> -->

        <!-- </el-col>
      </el-row> -->
      </el-form>
      <div class="btns">
        <el-button
          type="primary"
          icon="el-icon-search"
          size="small"
          round
          @click="handleQuery"
          >筛选</el-button
        >
        <el-button icon="el-icon-refresh" round size="small">导出</el-button>
        <el-button icon="el-icon-refresh" round size="small" @click="resetData"
          >重置</el-button
        >
      </div>
      <div style="margin-bottom:24px;">
        <el-button
          size="small"
          style="margin-right: 24px"
          :disabled="!externalUsers?.length && !isAll"
          round
          @click="$refs.actiontagEditRef.open()"
          >批量添加标签</el-button
        >
        <el-checkbox
          v-model="isAll"
          size="mini"
          round
          label="选择全部客户"
          @change="isAllChange"
        ></el-checkbox>
      </div>
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="datalist"
        @select-all="handleSelection"
        @selection-change="handleSelection"
      >
        <el-table-column type="selection" width="65" align="center" />
        <el-table-column
          label="客户"
          show-overflow-tooltip
          prop="externalUserName"
        >
          <template slot-scope="scope">
            <div class="user">
              <img
                :src="
                  scope.row.avatar
                    ? scope.row.avatar
                    : require('../assets/avter.png')
                "
                alt=""
              />
              <div class="useecont">
                <span
                  >{{ scope.row.externalUserName }}

                  <span class="wx" v-if="scope.row.type == 2">@微信</span>
                </span>
                <span class="nickname"> 昵称：</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属员工" prop="memberName" />
        <el-table-column label="标签" prop="tags" width="380">
          <template slot-scope="scope">
            <div class="tablist">
              <template v-if="scope.row.tags.length > 2">
                <div
                  class="listone"
                  v-for="item in scope.row.tags.filter((val, e) => {
                    return e < 3;
                  })"
                  :key="item.tagId"
                >
                  {{ item.tagName }}
                </div>
                <el-tooltip
                  placement="bottom"
                  effect="light"
                  popper-class="popers"
                >
                  <div slot="content" class="listout">
                    <div
                      class="listone listoneher"
                      v-for="item in scope.row.tags"
                      :key="item.tagId"
                    >
                      {{ item.tagName }}
                    </div>
                  </div>
                  <el-button size="mini" type="text">全部</el-button>
                </el-tooltip>
              </template>
              <template v-else>
                <div
                  class="listone"
                  v-for="item in scope.row.tags"
                  :key="item.tagId"
                >
                  {{ item.tagName }}
                </div>
              </template>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="所在群聊"
          prop="groupChats"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            {{ text(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="90px"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button size="small" type="text" @click="godetail(scope.row)"
              >详情</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.page_num"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="queryParams.page_size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </div>
    <div class="tagout"></div>
    <!-- 选择标签 -->
    <ActionTag type="select" ref="actiontagRef" @change="tagChange" />
    <!-- 批量添加标签 -->
    <ActionTag type="all" ref="actiontagEditRef" @change="corpTagsMark" />
    <!-- 选择员工 -->
    <SelectStaff type="staff" ref="selectstaffRef" @change="usersChange" />
  </div>
</template>

<script>
import operation from "../mixins/operation";
import ActionTag from "@/components/ActionTag.vue";
import SelectStaff from "@/components/SelectStaff.vue";
export default {
  name: "customerlist",
  components: {
    ActionTag,
    SelectStaff,
  },
  mixins: [operation],
  data() {
    return {
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > new Date().getTime();
        },
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
      title: "Banner",
      idtext: "id",
      // 表单校验
      rules: {
        bannerName: [
          {
            required: true,
            message: "请输入轮播图名称",
            trigger: ["blur", "change"],
          },
          { max: 30, message: "不能大于30个字符", trigger: ["blur", "change"] },
        ],
        bannerPosition: [
          { required: true, message: "请输入轮播图位置", trigger: "blur" },
        ],
        bannerUrl: [{ required: true, message: "请上传图片", trigger: "blur" }],
        isShow: [
          { required: true, message: "请选择是否显示", trigger: "blur" },
        ],
        bannerSort: [
          { required: true, message: "请输入排序", trigger: "blur" },
        ],
      },
      dictlist: [
        // {label:'全部',value:''},
        { label: "流失", value: "0" },
        { label: "好友", value: "1" },
      ],
      tablekey: "externalUsers",
      sexlist: [
        // {label:'全部',value:''},
        { label: "男", value: "1" },
        { label: "女", value: "2" },
        { label: "未知", value: "0" },
      ],
      jdlist: [
        //  {label:'全部',value:''},
        { label: "未知来源", value: "0" },
        { label: "未知来源", value: "1" },
        { label: "搜索手机号", value: "2" },
        { label: "名片分享", value: "3" },
        { label: "群聊", value: "4" },
        { label: "手机通讯录", value: "5" },
        { label: "微信联系人", value: "6" },
        // {label:'',value:'7'},
        { label: "安装第三方应用时自动添加的客服人员", value: "8" },
        { label: "搜索邮箱", value: "9" },
        { label: "视频号添加", value: "10" },
        { label: "通过日程参与人添加", value: "11" },
        { label: "通过会议参与人添加", value: "12" },
        { label: "添加微信好友对应的企业微信", value: "13" },
        { label: "通过智慧硬件专属客服添加", value: "14" },
        { label: "内部成员共享", value: "201" },
        { label: "管理员/负责人分配", value: "202" },
      ],
      userlist: [],
      chatlist: {},
      pagedata: {
        pagedata: 5000,
        page_num: 1,
      },
      queryParams: {
        page_num: 1,
        page_size: 20,
        keyword: "",
        gender: [],
        channels: [],
        group_chats: [],
        member_ids: [],
        statuses: [],
        tags: [],
      },
      externalUsers: [],
      isAll: false,
    };
  },
  created() {
    this.corp_id = this.$store.state.corpId;
    this.project_id = this.$store.state.projectUuid;
    this.delaultsearch.corp_id = this.corp_id;
    this.delaultsearch.project_id = this.project_id;
    this.getuserlist();
    this.getchatlist();
  },
  methods: {
    // 选择全部客户
    isAllChange(e) {
      console.log(e);
      if (e === true) {
        this.externalUsers;
        this.$refs.tableRef.clearSelection();
      }
    },
    // 选择客户
    handleSelection(data) {
      console.log(data);
      let newarr = data.map((x) => {
        return {
          externalUserId: x.externalUserId,
          memberId: x.memberId,
        };
      });
      newarr = JSON.parse(JSON.stringify(newarr)); // 生成数组
      console.log(newarr);
      this.externalUsers = newarr;
      if (this.externalUsers.length) {
        this.isAll = false;
      }
    },
    // 重置
    resetData() {
      this.queryParams = {
        page_num: 1,
        page_size: 20,
        keyword: "",
        gender: [],
        group_chats: [],
        member_ids: [],
        statuses: [],
        tags: [],
      };
      this.getList();
    },
    async getchatlist() {
      let params = {
        ...this.delaultsearch,
        ...this.pagedata,
      };
      let data = await this.$http.get("mktgo/wecom/contacts/group_chat/list", {
        params,
      });
      if (data.code === 0) {
        this.chatlist = data.data.groupChats;
      }
    },
    // 批量添加企业标签
    corpTagsMark(tags) {
      let params = {
        // channels: [],
        duplicate: true,
        // isAll: true,
        // endTime: "",
        // externalUser: "",
        // genders: [],
        // groupChats: [],
        // memberIds: [],
        // startTime: "",
        // statuses: [],
        // markCorpTags
        tags: tags.map((item) => item.id),
      };
      if (this.isAll) {
        params.isAll = this.isAll;
      } else {
        if (this.externalUsers.length) {
          params.externalUsers = this.externalUsers;
        }
      }
      if (!this.externalUsers.length) {
        if (this.queryParams.times) {
          params.start_time = this.queryParams.times[0];
          params.end_time = this.queryParams.times[1];
        }
        if (this.queryParams.channels.length) {
          params.channels = this.queryParams.channels;
        }
        if (this.queryParams.keyword) {
          params.keyword = this.queryParams.keyword;
        }
        if (this.queryParams.group_chats.length) {
          params.groupChats = this.queryParams.group_chats;
        }
        if (this.queryParams.gender.length) {
          params.genders = this.queryParams.gender;
        }
        if (this.queryParams.member_ids.length) {
          params.memberIds = this.queryParams.member_ids.map(
            (item) => item.memberId
          );
        }
        if (this.queryParams.statuses.length) {
          params.statuses = this.queryParams.statuses;
        }
        if (this.queryParams.tags.length) {
          params.markCorpTags = this.queryParams.tags.map((item) => item.id);
        }
      }

      this.$api
        .corpTagsMark(params)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            console.log(3);
            this.$message("设置成功");
          } else {
            this.$message(res.message);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    async getuserlist() {
      let params = {
        ...this.delaultsearch,
      };
      let data = await this.$http.get("mktgo/wecom/corp_tags/list", {
        params,
      });
      if (data.code === 0) {
        this.userlist = data.data.corpTagGroups;
      }
    },
    text(val) {
      if (!val.groupChats) {
        return "";
      }
      let arr = [];
      val.groupChats.map((item) => {
        arr.push(item.groupChatName);
      });
      return arr.join("、");
    },
    // 选择员工
    usersChange(e) {
      console.log(e);
      this.queryParams.member_ids = e;
    },
    // 选择标签
    tagChange(e) {
      this.queryParams.tags = e;
    },
    getList() {
      let queryParams = JSON.parse(JSON.stringify(this.queryParams));
      queryParams.tags = queryParams.tags.map((item) => item.id).join(",");
      queryParams.member_ids = queryParams.member_ids
        .map((item) => item.memberId)
        .join(",");
      queryParams.gender = queryParams.gender.join(",");
      queryParams.channels = queryParams.channels.join(",");
      queryParams.group_chats = queryParams.group_chats.join(",");
      queryParams.statuses = queryParams.statuses.join(",");
      let params = {
        ...this.delaultsearch,
        ...queryParams,
      };
      if (this.queryParams.times) {
        params.start_time = this.queryParams.times[0];
        params.end_time = this.queryParams.times[1];
        delete params.times;
      }

      this.loading = true;
      console.log(999, params);
      this.$api
        .externalUserList(params)
        .then((res) => {
          console.log(res);
          this.loading = false;
          if (res.code === 0) {
            if (this.ispagination) {
              this.datalist = res.data[this.tablekey];
              this.total = res.data.totalCount;
            } else {
              this.datalist = res;
            }
          }
        })
        .catch((err) => {
          this.loading = false;
          console.log(err);
        });
    },
    godetail(val) {
      this.$router.push({
        name: "customerdetails",
        query: {
          id: val.externalUserId,
          remark: val.remark,
          type: val.type,
        },
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.el-table {
  ::v-deep(.el-table__cell) {
    padding: 0;
  }
}
.content {
  background: #fff;
  padding: 24px 10px;
  box-sizing: border-box;
}
.tops {
  width: 100%;
  height: 40px;
  display: flex;
  /* justify-content: center; */
  flex-direction: column;
  color: #999999;
  font-size: 12px;
}
.tops h2 {
  color: #333;
  font-size: 20px;
  margin-bottom: 10px;
}
.user {
  width: 110px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.user img {
  width: 30px;
  height: 30px;
  border-radius: 15px;
}
.useecont {
  width: 70px;
  height: 30px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  line-height: normal;
}
.useecont span {
  font-weight: 600;
}
.tablist {
  display: flex;
  align-items: center;
  font-size: 12px;
  flex-wrap: wrap;
}
.listout {
  width: 360px;
  display: flex;
  flex-wrap: wrap;
}
.listone {
  padding: 2px 16px !important;
  box-sizing: border-box;
  border: 1px solid #e1e1e1;
  border-radius: 31px;
  margin-right: 6px;
  font-size: 12px;
  line-height: normal;
}
.listoneher {
  margin-bottom: 12px;
}
.wx {
  color: #28ba36;
}
.nickname {
  color: #999;
  font-weight: 400 !important;
}
::v-deep(.el-input) {
  width: 222px;
}
::v-deep(.el-range-editor.el-input__inner) {
  width: 444px !important;
}
.btns {
  margin: 16px 0;
}
</style>
