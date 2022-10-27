<template>
  <el-drawer
    title="选择员工"
    :visible.sync="drawer"
    size="600px"
    :before-close="close"
  >
    <div class="staff-body">
      <div class="staff-select">
        <div class="staff-select-left">
          <el-tabs v-model="tabcurrent">
            <!-- 全部 all, 只组织 organizational, 只员工 staff -->

            <template v-if="type === 'all' || type === 'organizational'">
              <el-tab-pane label="按部门" name="0">
                <el-input
                  class="staff-select-left-input"
                  placeholder="搜索"
                  v-model="filterText"
                >
                  <template #prefix>
                    <i class="el-icon-search el-input__icon"> </i>
                  </template>
                </el-input>
                <el-checkbox-group
                  v-model="checkOrganizationalList"
                  @change="organizationalChange"
                >
                  <el-tree
                    :data="organizationalList"
                    :props="{ label: 'name' }"
                    node-key="id"
                    default-expand-all
                    :filter-node-method="filterNode"
                    :expand-on-click-node="false"
                    ref="tree"
                  >
                    <div class="custom-tree-node" slot-scope="{ node, data }">
                      <span>{{ data.name }}</span>
                      <el-checkbox :label="data.id">&nbsp;</el-checkbox>
                    </div>
                  </el-tree>
                </el-checkbox-group>
              </el-tab-pane>
            </template>
            <template v-if="type === 'all' || type === 'staff'">
              <el-tab-pane label="按员工" name="1">
                <el-input
                  class="staff-select-left-input"
                  placeholder="搜索"
                  v-model="memberParameter.member_name"
                  @keyup.enter.native="contactsMemberList()"
                />
                <el-checkbox-group
                  v-model="checkStaffList"
                  @change="staffChange"
                >
                  <div
                    class="selecton-item"
                    v-for="item in staffList"
                    :key="item.memberId"
                  >
                    <div class="selecton-item-info">
                      <img
                        class="selecton-item-info-img"
                        src="@/assets/avter.png"
                      />
                      <div class="selecton-item-info-name">
                        <div class="one-line">{{ item.memberName }}</div>
                      </div>
                    </div>
                    <div class="selecton-item-btn">
                      <el-checkbox :label="item.memberId">&nbsp;</el-checkbox>
                    </div>
                  </div>
                </el-checkbox-group>
                <div style="margin-top: 16px">
                  <el-pagination
                    style="text-align: center"
                    :current-page="memberParameter.page_num"
                    :page-size="memberParameter.page_size"
                    :total="total"
                    small
                    background
                    layout="total,  prev, pager, next"
                    :page-sizes="[10, 20, 30]"
                    @current-change="contactsMemberList"
                  />
                </div>
              </el-tab-pane>
            </template>
          </el-tabs>
        </div>
        <div class="staff-select-right">
          <div class="selecton-title">已选择</div>
          <template
            v-if="checkOrganizationalList.length || checkStaffList.length"
          >
            <template v-if="checkOrganizationalList.length">
              <div class="selecton-subtitle">部门</div>

              <div
                class="selecton-item"
                v-for="item in synchroOrganizationalList"
                :key="item.id"
              >
                <img class="selecton-item-img" src="@/assets/avter.png" />
                <span class="selecton-item-name">{{ item.name }}</span>
              </div>
            </template>
            <template v-if="synchroStaffList.length">
              <div class="selecton-subtitle">员工</div>
              <div
                class="selecton-item"
                v-for="item in synchroStaffList"
                :key="item.memberId"
              >
                <img class="selecton-item-img" src="@/assets/avter.png" />
                <span class="selecton-item-name">{{ item.memberName }}</span>
              </div>
            </template>
          </template>
          <el-empty :image="empty" description="暂未选择" v-else></el-empty>
        </div>
      </div>
    </div>
    <div class="footer-btn">
      <el-button
        size="small"
        style="padding-left: 30px; padding-right: 30px"
        round
        @click="close"
        >取 消</el-button
      >
      <el-button
        style="padding-left: 30px; padding-right: 30px"
        @click="confirm"
        round
        size="small"
        type="primary"
        >确 定</el-button
      >
    </div>
  </el-drawer>
</template>

<script>
/*组件使用
  返回参数为 部门列表，员工列表
  <SelectStaff
    ref="selectstaffRef"
    @change="
      (departments,users) => {
        formData.members.departments = departments;
        formData.members.users = users;
      }
    "
  />
  组件调用
  <el-button
  @click="$refs.selectstaffRef.open(部门列表（可不传）, 用户列表（可不传）)"
  >按钮</el-button>
*/
export default {
  name: "SelectStaff",
  props: {
    // 全部 all, 只组织 organizational, 只员工 staff
    type: {
      type: String,
      default: "all",
    },
  },
  data() {
    return {
      empty: require('@/assets/empty-images.png'),
      tabcurrent: "0",
      drawer: false,
      // 部门
      filterText: "",
      parameter: {
        corpId: this.$store.state.corpId,
        projectUuid: this.$store.state.projectUuid,
        tenantUuid: this.$store.state.tenantUuid,
      },
      // 选择部门
      organizationalList: [],
      checkOrganizationalList: [],
      synchroOrganizationalList: [],
      // 员工
      memberParameter: {
        corp_id: this.$store.state.corpId,
        page_num: 1,
        page_size: 15,
        project_id: this.$store.state.projectUuid,
        member_name: "",
      },
      total: 0,
      // 选择员工
      staffList: [],
      checkStaffList: [],
      synchroStaffList: [],
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  methods: {
    // 打开组件 回填部门数组，员工数组
    open(organizationals = [], staffs = []) {
      if (this.type === "all") {
        if (staffs.length) {
          this.synchroStaffList = staffs;
          this.checkStaffList = staffs.map((item) => item.memberId);
        }
        if (organizationals.length) {
          this.synchroOrganizationalList = organizationals;
          this.checkOrganizationalList = organizationals.map((item) => item.id);
        }
        this.userOrganizationalStructure();
        this.contactsMemberList();
      }

      if (this.type === "organizational") {
        if (organizationals.length) {
          this.synchroOrganizationalList = organizationals;
          this.checkOrganizationalList = organizationals.map((item) => item.id);
        }
        this.userOrganizationalStructure();
        this.tabcurrent = "0";
      }
      // 选择员工的时候默认也是第一个参数
      if (this.type === "staff") {
        if (organizationals.length) {
          this.synchroStaffList = organizationals;
          this.checkStaffList = organizationals.map((item) => item.memberId);
        }
        this.contactsMemberList();
        this.tabcurrent = "1";
      }
    },
    // 关闭重置
    close() {
      this.drawer = false;
      this.tabcurrent = "0";
      this.memberParameter = {
        page_num: 1,
        page_size: 15,
        member_name: "",
      };
      this.checkOrganizationalList = [];
      this.checkStaffList = [];
      this.organizationalList = [];
      this.staffList = [];
      this.synchroOrganizationalList = [];
      this.synchroStaffList = [];
    },
    // 确认
    confirm() {
      if (this.type === "all") {
        this.$emit(
          "change",
          this.synchroOrganizationalList,
          this.synchroStaffList
        );
      }
      if (this.type === "organizational") {
        this.$emit(
          "change",
          this.synchroOrganizationalList
        );
      }
      if (this.type === "staff") {
        this.$emit(
          "change",
          this.synchroStaffList
        );
      }
      this.close();
    },
    // 员工列表
    contactsMemberList(page) {
      this.memberParameter.page_num = page || 1;
      this.$api
        .contactsMemberList(this.memberParameter)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.staffList = res.data.members;
            this.total = res.data.totalCount;
            this.drawer = true;
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    staffChange() {
      let list = JSON.parse(JSON.stringify(this.synchroStaffList));
      this.synchroStaffList = Object.assign(list, this.staffList).filter(
        (item) => this.checkStaffList.includes(item.memberId)
      );
    },
    // 树转数组
    treeToArr(data) {
      const result = [];
      data.forEach((item) => {
        const loop = (data) => {
          result.push({
            id: data.id,
            name: data.name,
            parentId: data.parentId,
          });
          let child = data.children;
          if (child) {
            for (let i = 0; i < child.length; i++) {
              loop(child[i]);
            }
          }
        };
        loop(item);
      });
      return result;
    },
    // 组织架构
    userOrganizationalStructure() {
      this.$api
        .userOrganizationalStructure(this.parameter)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.organizationalList = res.data.structures;
            this.drawer = true;
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    organizationalChange() {
      console.log(this.treeToArr(this.organizationalList));
      let list = JSON.parse(JSON.stringify(this.synchroOrganizationalList));
      this.synchroOrganizationalList = Object.assign(
        list,
        this.treeToArr(this.organizationalList)
      ).filter((item) => this.checkOrganizationalList.includes(item.id));
    },
    // 部门筛选
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep(.el-drawer) {
  .el-drawer__header {
    margin-bottom: 10px;
  }
}

.staff-body {
  height: calc(100% - 80px);
  overflow-y: auto;
  .staff-select {
    height: 100%;
    display: flex;
    ::v-deep(.staff-select-left) {
      padding: 0 15px;
      border-right: #ececec 1px solid;
      box-sizing: border-box;
      width: 50%;
      .staff-select-left-input {
        width: 100%;
        .el-input__inner {
          border-radius: 40px;
        }
      }
      .selecton-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 14px;
        .selecton-item-info {
          display: flex;
          align-items: center;
          .selecton-item-info-img {
            width: 20px;
            height: 20px;
            border-radius: 50%;
            margin-right: 12px;
          }
          .selecton-item-info-name {
            font-size: 12px;
            display: grid;
          }
        }
        .selecton-item-btn {
        }
      }
      .el-tree {
        margin-top: 10px;
        .el-tree-node {
          font-size: 12px;
          .el-tree-node__content {
            height: 34px;
            .el-checkbox {
              position: absolute;
              right: 0;
            }
          }
        }
      }
    }
    .staff-select-right {
      padding: 0 15px;
      box-sizing: border-box;
      width: 50%;
      .selecton-title {
        color: #333333;
        font-family: PingFang SC;
        font-weight: 500;
        font-size: 12px;
      }
      .selecton-subtitle {
        color: #999999;
        font-family: PingFang SC;
        font-size: 12px;
        margin-top: 20px;
      }
      .selecton-item {
        margin-top: 12px;
        .selecton-item-img {
          width: 14px;
          height: 14px;
          vertical-align: middle;
          margin-right: 8px;
        }
        .selecton-item-name {
          vertical-align: middle;
          color: #333333;
          font-family: PingFang SC;
          font-size: 12px;
        }
      }
    }
  }
}

.footer-btn {
  text-align: right;
  padding: 24px 16px;
}
</style>