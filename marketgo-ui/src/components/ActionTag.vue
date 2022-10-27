<template>
  <div>
    <el-drawer
      title="添加标签"
      :visible.sync="drawer"
      size="600px"
      :before-close="close"
    >
      <div class="tag-body">
        <template v-if="tagList.length">
          <el-input
          @keyup.enter.native="corpTagsList"
          v-model="parameter.keyword"
          placeholder="请输入要查找的标签"
          class="tag-select-input"
        >
          <template #prefix>
            <i class="el-icon-search el-input__icon"> </i>
          </template>
        </el-input>
        <div class="tag-term" v-for="(tags, index) in tagList" :key="index">
          <div class="tag-term-title">
            <div class="tag-term-title-text">{{ tags.groupName }}</div>
            <div class="tag-term-title-btn">
              <el-button
                @click="tagGroupOpen(tags)"
                size="small"
                type="text"
                v-if="type === 'edit' || type === 'all'"
                >编辑</el-button
              >
            </div>
          </div>
          <div class="tag-term-list">
            <el-button
              :class="{ on: selectedIds.includes(item.id) }"
              @click="selectTag(item)"
              v-for="(item, key) in tags.tags"
              :disabled="type === 'edit'"
              :key="key"
              size="mini"
              round
            >
              {{ item.name }}
            </el-button>
          </div>
        </div>
        </template>
        <el-empty :image="empty" description="暂未选择" v-else></el-empty>
      </div>
      <div class="footer-btn">
        <div>
          <el-button
            @click="tagGroupOpen()"
            type="text"
            v-if="type === 'edit' || type === 'all'"
            >+ 创建标签组</el-button
          >
        </div>
        <div>
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
      </div>
    </el-drawer>
    <el-dialog
      :title="`${tagGroup.groupId ? '编辑' : '添加'}标签组`"
      :visible.sync="tagGroupVisible"
      width="500px"
      :before-close="tagGroupClose"
    >
      <div style="overflow-y: auto; max-height: 400px">
        <el-form
          ref="ruleForm"
          :model="tagGroup"
          :rules="rules"
          size="small"
          label-width="110px"
          @submit.prevent.native
        >
          <el-form-item label="标签组名称：" prop="groupName">
            <el-input
              v-model="tagGroup.groupName"
              placeholder="请输入标签组名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="标签：" prop="tagNameList">
            <div
              class="tag-item"
              v-for="(tag, index) in tagGroup.tagNameList"
              :key="index"
            >
              <div class="tag-item-input">
                <el-input
                  v-model="tag.name"
                  placeholder="请输入标签名"
                ></el-input>
              </div>
              <div class="tag-item-btn">
                <i @click="tagsDelete(tag, index)" class="el-icon-close"></i>
              </div>
            </div>
            <el-button @click="addTag()" type="text">+ 添加标签</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div
        slot="footer"
        style="
          display: flex;
          align-items: center;
          justify-content: space-between;
        "
      >
        <div>
          <el-button
            v-if="tagGroup.groupId"
            style="color: #f00"
            @click="tagGroupDelete(tagGroup)"
            type="text"
            >删除标签组</el-button
          >
        </div>
        <div>
          <el-button size="small" round @click="tagGroupClose">取 消</el-button>
          <el-button size="small" round type="primary" @click="tagGroupConfirm"
            >确 定</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "ActionTag",
  props: {
    // 标题
    text: {
      type: String,
      default: "",
    },
    // 都可以 all, 只能选择 select, 编辑 edit
    type: {
      type: String,
      default: "all",
    },
  },
  data() {
    return {
      empty: require('@/assets/empty-images.png'),
      parameter: {
        keyword: "",
        sync_state: "async",
      },
      drawer: false,
      tagList: [],
      selectedList: [],
      // 标签组
      tagGroupVisible: false,
      tagGroup: {
        groupId: "",
        groupName: "",
        tagNameList: [],
      },
      tagGroupBack: {
        groupId: "",
        groupName: "",
        tagNameList: [],
      },
      rules: {
        groupName: [
          { required: true, message: "请输入标签组名称", trigger: "blur" },
          {
            min: 1,
            max: 11,
            message: "标签组名称长度在 1 到 15 个字符",
            trigger: "blur",
          },
        ],
        tagNameList: [
          { required: true, message: "请添加标签", trigger: "blur" },
        ],
      },
    };
  },
  computed: {
    // 已选的id数组
    selectedIds() {
      let arr = [];
      if (this.selectedList.length) {
        arr = this.selectedList.map((item) => item.id);
      }
      return arr;
    },
    tagNameNoList() {
      return this.tagGroup.tagNameList.filter((v, i, arr) => {
        console.log(v, i, arr);
        return (
          !this.tagGroupBack.tagNameList
            .map((item) => item.name)
            .includes(v.name) && v.id
        );
      });
    },
  },
  watch: {
    tagList(data) {
      let arr = []
        .concat(...data.map((item) => item.tags))
        .map((item) => item.id);
      for (let i = 0; i < this.selectedList.length; i++) {
        if (!arr.includes(this.selectedList[i].id)) {
          this.selectedList.splice(i, 1);
          i--;
        }
      }
    },
  },
  methods: {
    // 删除标签组or 标签
    tagsDelete(tag, index) {
      if (tag.id) {
        this.corpTagsDelete([tag.id], "tag", index);
      } else {
        this.tagGroup.tagNameList.splice(index, 1);
      }
    },
    tagGroupDelete(group) {
      this.corpTagsDelete([group.groupId], "taggroup");
    },
    // 删除企业标签
    corpTagsDelete(arr, type, index) {
      let text = "";
      let data = {};
      if (type === "taggroup") {
        text = "组";
        data.groupId = arr;
      } else {
        data.tagId = arr;
      }
      this.$confirm(`确定是否删除标签${text}？`)
        .then(() => {
          this.$api
            .corpTagsDelete(data)
            .then((res) => {
              if (res.code === 0) {
                console.log(res);
                if (type === "taggroup") {
                  this.tagGroupClose();
                }
                if (type === "tag") {
                  this.tagGroup.tagNameList.splice(index, 1);
                  this.tagGroupBack.tagNameList.splice(index, 1);
                }
                this.corpTagsList();
              } else {
                this.$message.error(res.message);
              }
            })
            .catch((err) => {
              console.log(err);
            });
        })
        .catch(() => {});
    },
    // 标记企业标签
    corpTagsMark() {
      this.$api
        .corpTagsMark()
        .then((res) => {
          if (res.code === 0) {
            console.log(res);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    // 确认保存标签组
    tagGroupConfirm() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          let bool = true;
          for (let item of this.tagGroup.tagNameList) {
            if (!item.name || item.name === "") {
              bool = false;
              break;
            }
          }
          if (!bool) {
            this.$message.info("请填写标签名称");
            return;
          }
          if (this.tagGroup.groupId) {
            this.corpTagsEdit();
          }
          this.corpTagsAdd();
        }
      });
    },
    // 添加企业标签
    corpTagsAdd() {
      let arr = this.tagGroup.tagNameList.filter((item) => {
        return item.id === "";
      });
      if (arr.length) {
        this.$api
          .corpTagsAdd({
            groupId: this.tagGroup.groupId,
            groupName: this.tagGroup.groupName,
            tagNameList: arr.map((item) => item.name),
          })
          .then((res) => {
            if (res.code === 0) {
              console.log(res);
              this.corpTagsList('sync');
              this.tagGroupClose();
            } else {
              this.$message.error(res.message);
            }
          })
          .catch((err) => {
            console.log(err);
          });
      }
    },
    // 编辑企业标签
    corpTagsEdit() {
      let data = {
        groups: [],
        tags: [],
      };
      let bool = false;
      if (this.tagGroup.groupName !== this.tagGroupBack.groupName) {
        data.groups = [
          {
            id: this.tagGroup.groupId,
            name: this.tagGroup.groupName,
          },
        ];
        bool = true;
      }
      if (this.tagNameNoList.length) {
        data.tags = this.tagNameNoList;
        bool = true;
      }
      if (bool) {
        this.$api
          .corpTagsEdit(data)
          .then((res) => {
            if (res.code === 0) {
              console.log(res);
              this.corpTagsList('sync');
              this.tagGroupClose();
            } else {
              this.$message.error(res.message);
            }
          })
          .catch((err) => {
            console.log(err);
          });
      } else {
        if (
          !this.tagGroup.tagNameList.filter((item) => {
            return item.id === "";
          }).length
        ) {
          this.$message.error("标签组无修改");
        }
      }
    },
    // 添加标签
    addTag() {
      this.tagGroup.tagNameList.push({
        id: "",
        name: "",
      });
    },
    // 打开标签组弹框
    tagGroupOpen(item) {
      console.log(item);
      if (item) {
        this.tagGroup = {
          groupId: item.groupId,
          groupName: item.groupName,
          tagNameList: item.tags,
        };
        this.tagGroupBack = {
          groupId: JSON.parse(JSON.stringify(item)).groupId,
          groupName: JSON.parse(JSON.stringify(item)).groupName,
          tagNameList: JSON.parse(JSON.stringify(item)).tags,
        };
      }
      this.tagGroupVisible = true;
    },
    // 关闭标签组弹框
    tagGroupClose() {
      this.tagGroupVisible = false;
      this.tagGroup = {
        groupId: "",
        groupName: "",
        tagNameList: [],
      };
      this.tagGroupBack = {
        groupId: "",
        groupName: "",
        tagNameList: [],
      };
      this.$refs.ruleForm.clearValidate();
    },
    // 打开组件 回填已选数组
    open(list = []) {
      this.selectedList = list;
      this.corpTagsList();
    },
    // 确认
    confirm() {
      this.$emit("change", this.selectedList);
      this.close();
    },
    // 关闭重置
    close() {
      this.drawer = false;
      this.tagList = [];
      this.selectedList = [];
    },
    // 选择标签
    selectTag(item) {
      if (this.type === "edit") {
        return;
      }
      if (this.selectedIds.includes(item.id)) {
        for (let i = 0; i < this.selectedList.length; i++) {
          if (this.selectedList[i].id === item.id) {
            this.selectedList.splice(i, 1);
            break;
          }
        }
      } else {
        this.selectedList.push(item);
      }
    },
    // 标签列表
    corpTagsList(state = "async") {
      this.parameter.sync_state = state
      this.$api
        .corpTagsList(this.parameter)
        .then((res) => {
          console.log(res);
          if (res.code === 0) {
            this.tagList = res.data.corpTagGroups;
            this.drawer = true;
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
::v-deep(.el-drawer) {
  .el-drawer__header {
    margin-bottom: 20px;
  }
  .tag-body {
    height: calc(100% - 90px);
    padding: 0 24px;
    overflow-y: auto;
    .tag-select-input {
      .el-input__inner {
        border-radius: 40px;
      }
    }
    .tag-term {
      margin-top: 20px;
      .tag-term-title {
        display: flex;
        align-items: center;
        justify-content: space-between;
        .tag-term-title-text {
          color: #333333;
          font-family: PingFang SC;
          font-size: 12px;
        }
        .tag-term-title-btn {
        }
      }
      .tag-term-list {
        .el-button {
          margin: 10px 10px 0 0;
          &.on {
            background-color: #409eff;
            border: #409eff 1px solid;
            color: #fff;
          }
        }
      }
    }
  }
  .footer-btn {
    padding: 24px 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
.tag-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  .tag-item-input {
    flex: auto;
  }
  .tag-item-btn {
    flex: none;
    margin-left: 12px;
    i {
      font-weight: bold;
      color: #999;
      cursor: pointer;
    }
  }
}
</style>