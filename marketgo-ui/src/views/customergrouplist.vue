<template>
  <div>
    <div class="tops">
      <h2>客户群列表</h2>
      <!-- <span>XXXXXXXX</span> -->
    </div>
    <div class="content">
      <el-form
        :model="queryParams"
        size="small"
        ref="queryForm"
        :inline="true"
        v-show="showSearch"
        label-width="90px"
      >
        <el-form-item label="客户群" prop="group_chat">
          <el-input
            v-model="queryParams.group_chat"
            placeholder="请输入客户群"
            clearable
          />
        </el-form-item>
        <el-form-item label="群主员工" prop="members">
          <el-select
            v-model="queryParams.members"
            placeholder="请选择群主员工"
            clearable
          >
            <el-option
              v-for="dict in groupleader"
              :key="dict.ownerId"
              :label="dict.ownerName"
              :value="dict.ownerId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            round
            icon="el-icon-search"
            size="small"
            @click="handleQuery"
            >筛选</el-button
          >
          <el-button icon="el-icon-refresh" round size="small">导出</el-button>
          <el-button icon="el-icon-refresh" round size="small" @click="resetQuery"
            >重置</el-button
          >
        </el-form-item>
      </el-form>
      <el-table
        v-loading="loading"
        :data="datalist"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="65" align='center' />
        <el-table-column
          label="客户群名称"
          show-overflow-tooltip
          prop="groupChatName"
        />
        <el-table-column label="群人数" prop="count" />
        <el-table-column label="群主" prop="ownerName" />
        <el-table-column label="创建时间" prop="createTime" />
        <el-table-column
          label="操作"
          width="90px"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              size="small"
              type="text"
              @click="handleUpdate(scope.row)"
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
  </div>
</template>

<script>
import operation from "../mixins/operation";
export default {
  name: "customerlist",
  mixins: [operation],
  data() {
    return {
      title: "Banner",
      url: {
        list: "mktgo/wecom/contacts/group_chat/list",
        del: "",
        add: "",
        edit: "",
        exporturl: "",
      },
      idtext: "id",
      groupleader: [],
      corp_id: "",
      project_id: "",
    };
  },
  created() {
    this.corp_id = this.$store.state.corpId;
    this.project_id = this.$store.state.projectUuid;
    this.delaultsearch.corp_id = this.corp_id;
    this.delaultsearch.project_id = this.project_id;
    this.getleader();
  },
  methods: {
    async getleader() {
      let params = {
        corp_id: this.corp_id,
        project_id: this.project_id,
      };
      let data = await this.$http.get(
        "mktgo/wecom/contacts/group_chat/owners",
        {params}
      );
      if(data.code===0){
        this.groupleader=data.data.groupChatOwners
      }
    },
    handleUpdate(val) {
      this.$router.push({
        name: "groupdetails",
        query: {
          group_chat_id: val.groupChatId,
        },
      });
    },
  },
};
</script>
<style scoped>
.content {
  background: #fff;
padding: 24px 10px;
  box-sizing: border-box;
}
.tops {
  width: 100%;
  height: 40px;
  display: flex;
  /* flex-direction: center; */
  color: #999999;
  font-size: 12px;
}
.tops h2 {
  color: #333;
  font-size: 20px;
  margin-bottom: 10px;
}
</style>