<template>
    <div>
        <main-head title="入群欢迎语">
            <el-form class="head-form" inline size="small" label-width="0">
                <el-form-item>
                    <el-input style="width: 248px" placeholder="输入标题回车搜索" v-model="queryParams.keyword"
                        class="input-with-select" @keyup.enter.native="searchData(1)">
                        <template #prefix>
                            <i class="el-icon-search el-input__icon"> </i>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button style="position: relative; top: -3px" type="primary" size="medium" icon="el-icon-circle-plus"
                        round @click="toAdd">
                        创建入群欢迎语
                    </el-button>
                </el-form-item>
            </el-form>
        </main-head>
        <div class="main-content">
            <el-table v-if="dataList.length" :data="dataList">
                <el-table-column  label="标题" prop="name">
                </el-table-column>
                <el-table-column label="欢迎语内容" width="419">
                    <template #default="{ row }">
                        <span style="width:419px;white-space:nowrap;text-overflow:ellipsis;overflow:hidden; "  v-html="row.welcomeText"> </span>

                    </template>
                </el-table-column>
                <el-table-column label="创建时间" sortable  prop="createTime"/> 
                <el-table-column label="创建人" prop="creatorName" />

                <el-table-column label="操作" align="right">
                    <template #default="{ row }">
                        <el-button @click="editWelcome(row)" size="small" type="text">编辑</el-button>
                        <el-divider direction="vertical"></el-divider>
                        <el-button size="small" type="text" @click="delteWelcome(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-if="dataList.length" :current-page="page_num" :page-size="page_size" :total="total" background
                layout="total,  prev, pager, next, sizes, jumper" :page-sizes="[10, 20, 30]" @size-change="pageSizeChange"
                @current-change="searchData" />
            <el-empty :image="empty" description="暂无数据" v-else></el-empty>
        </div>
    </div>
</template>
<script>
export default {
    name: 'masscustomer',
    components: {},
    data() {
        return {
            page_num: 1,
            page_size: 20,
            total: 0,
            empty: require('@/assets/empty-images.png'),
            queryParams: {
                keyword: '',
            },
            dataList: [],
        }
    },
    mounted() {
        this.searchData()
    },
    methods: {

        // 删除欢迎语
        delteWelcome(row) {
            let _this = this
            this.$confirm('是否删除该欢迎语？', '确认信息', {
                distinguishCancelAndClose: true,
                confirmButtonText: '删除',
                cancelButtonText: '取消'
            }).then(() => {
                console.log(row)
                this.$http.post(
                    `mktgo/wecom/welcome/group_chat/delete?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&uuid=${row.uuid}`,
                    {}).then(function (res) {
                        console.log(res)
                        if (res.code == 0) {
                            _this.$message({
                                message: '删除成功',
                                type: 'success'
                            });
                            _this.searchData()
                        } else {
                            _this.$message(res.message);
                        }
                    });
            }).catch(action => {
                console.log(action)
            });
        },

        async searchData(page_num) {
            let params = {
                keyword: '',
                page_num: 1,
                page_size: 20,

            }
            let queryStr = ''
            for (let k in params) {
                k == 'keyword' ? queryStr += `&keyword=${this.queryParams.keyword}` : ''
            }
            queryStr += `&page_num=${page_num ? page_num : this.page_num}`
            queryStr += `&page_size=${this.page_size}`
            let res = await this.$http.get(
                `mktgo/wecom/welcome/group_chat/list?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}` + queryStr,
                {});
            if (res.code == 0) {
                this.dataList = res.data.list || []
                for (let item of this.dataList) {
                    let users = []
                    if (item.members?.departments) {
                        for (let user of item.members?.departments) {
                            users.push(user.name)
                        }
                    }
                    if (item.members?.users) {
                        for (let user of item.members?.users) {
                            users.push(user.memberName)
                        }
                        item.users = users.join(',')
                    }
                    let textContent = item.welcomeContent.find((itemIn) => {
                        return itemIn.type === "TEXT";
                    });
                    item.welcomeText = textContent.text.content.replace(/%NICKNAME%/g, '<span style=color:#409EFF;>客户昵称</span>')

                }
                this.total = res.data.totalCount
            }
        },
        editWelcome(row) {
            console.log(row)
            this.$router.push({
                path: '/index/add_group_chat_welcome',
                query: {
                    uuid: row.uuid,
                    id: row.id,
                },
            })
        },
        toAdd() {
            console.log(111)
            this.$router.push('/index/add_group_chat_welcome')
        },
        handleQuery() {
            // this.queryParams.page_num = 1
            console.log(this.queryParams)
            this.searchData(1)
        },
        resetQuery() {
            this.queryParams = {
                keyword: '',
            }
            this.searchData(1)
        },
        // 调整页数
        pageSizeChange(e) {
            console.log(e);
            this.page_size = Number(e);
            this.searchData();
        },
    }
}
</script>
<style scoped lang="scss">
.dialogClass .el-dialog__body {
    // padding: 20px;
    padding-top: 0px;
    padding-bottom: 0px;
    padding-left: 0px;
    padding-right: 0px;
    color: #606266;
    font-size: 14px;
    // max-height: 358px;
}

.dialogClass .el-dialog__header {
    // padding: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
    padding-left: 8px;
    padding-right: 8px;
    color: #606266;
    font-size: 14px;
}

.el-dialog-header-box {
    height: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .dialog-title-text {
        margin-left: 8px;
    }

    .righgt_edit {
        margin-right: 8px;
        width: 10px;
        height: 10px;
    }
}

.tagInput {
    width: 90%;
    height: 32px;
    border: 0;
    font-size: 12px;
    outline: none;
    // background-color: rgba(255, 255, 255, 0);
    // color: white;
    margin: 0 10px;
    // border-radius: 5px;
}

.search {

    width: 93%;
    height: 32px;
    border: 1px solid #DDDDDD;
    border-radius: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    margin-left: 8px;
    margin-right: -8px;
}

.menus {
    display: block;
    // height: 200px;
    margin-left: auto;
}

.menulist {
    height: 30px;
    text-indent: 10px;
    background: white;
    font-size: 12px;
    line-height: 30px;
    color: #333333;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-left: 10px;
    margin-right: 10px;
}

.menulist span {
    display: flex;
    align-items: center;
}

.menulist span img {
    width: 10px;
    height: 10px;
}

.menulist:hover {
    background: #EFF3FD;
    margin-left: -20;
}

.tag_edit {
    width: 24px;
    height: 24px;
}

::v-deep(.el-table th.el-table__cell) {
    height: 46px;
    line-height: 22px;
    padding: 0;
}

.el-table th.el-table__cell {
    height: 46px;
    line-height: 22px;
    padding: 0;
}

.search-content {
    background-color: white;
    padding-top: 24px;
    border-radius: 8px;
}

.main-content {
    margin-top: 12px;
    border-radius: 8px;
    overflow: hidden;
    background-color: white;
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

::v-deep(.el-table__body) {
    width: 100%;
    // 使表格兼容safari，不错位
    // table-layout: fixed !important;
}
</style>