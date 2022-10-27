export default {
    data() {
        return {
            url: {
                list: '', //查询列表
                add: '', //新增
                edit: '', //编辑
                del: '', //删除
                batchdel: '', //批量删除
                exporturl: '' // 导出
            },
            ispagination: true, //是否分页
            isactivaneed: true, //初始化加载
            delaultsearch: {},//默认查询条件
            defaultfrom: {}, //默认表单条件
            queryParams: {
                page_num: 1,
                page_size: 20,
            },  //查询条件
            form: {},    //增加表单
            datalist: [],  //数据列表
            // page_num: 1,
            // page_size: 10,
            total: 0,
            loading: false, //表格loading
            datalistselection: [], //多选项
            open: false, //弹窗
            editdata: {},    //编辑数据
            read: false,  //是否禁止编辑
            saveloading: false,
            showSearch: true,  //显示查询条件
            order: '', //排序方式
            orderField: '', //排序字段
            idtext: 'id',  //主键id
            exportTitle: '',
            tablekey:'groupChats'
        }
    },
    mounted() {
        if (this.isactivaneed) {
            this.getList()
        }
        if (!this.exportTitle) {
            this.exportTitle = this.title
        }
    },
    methods: {
        handleExport() {
            this.download(this.url.exporturl, {
                ...this.queryParams
            }, `${this.exportTitle}.xlsx`)
        },
        // 排序
        dataListSortChangeHandle(data) {
            if (!data.order || !data.prop) {
                this.order = ''
                this.orderField = ''
                return false
            }
            this.order = data.order == 'descending' ? 'desc' : 'asc'    //.replace(/ending$/, '')
            this.orderField = data.prop//.replace(/([A-Z])/g, '_$1').toLowerCase()
            this.getList()
        },
        paginations(val) {
            this.queryParams.page_num = val.page
            this.queryParams.page_size = val.limit
            this.getList()
        },
        async getList() {
            let queryParams = JSON.parse(JSON.stringify(this.queryParams))
            let params = {
                ...this.delaultsearch,
                ...queryParams
            }
            this.loading = true
            let data = await this.$http.get(this.url.list, { params })
            if (data.code === 0) {
                if (this.ispagination) {
                    this.datalist = data.data[this.tablekey] 
                    this.total = data.data.totalCount
                } else {
                    this.datalist = data
                }
            }
            this.loading = false
        },
        setdate(val) {
            return val
        },
        submitForm() {
            this.$refs.form.validate(async vaild => {
                if (vaild) {
                    this.saveloading = true
                    let str = ''
                    let methods = ''
                    let params = {
                        ...this.form
                    }
                    let msg = ''
                    if (this.editdata[this.idtext]) {
                        //编辑
                        msg = '编辑成功'
                        str = this.url.edit
                        params[this.idtext] = this.editdata[this.idtext]
                        methods = 'put'
                    } else {
                        str = this.url.add
                        methods = 'post'
                        msg = '增加成功'
                    }
                    let data = await this.$http[methods](str, params)
                    if (data.code === 0) {
                        this.getList()
                        this.cancel()
                        this.$message.success(msg)
                    }
                    this.saveloading = false
                    //this.getList()
                }
            })
        },
        handleAdd() {
            this.title = "新增"
            this.open = true
            this.read = false
            this.form = { ...this.defaultfrom }
        },
        cancel() {
            this.form = { ...this.defaultfrom }
            this.editdata = {}
            this.saveloading = false
            this.open = false
        },
        handleUpdate(val, type = false) {
            this.read = type
            this.title = type ? '查看' : '修改'
            this.editdata = JSON.parse(JSON.stringify(val))
            this.open = true
            this.form = this.editdata
        },
        async handleDelete(val) {
            let str = ''
            if (!val[this.idtext]) {
                if (this.datalistselection.length === 0) {
                    return this.$message.error('请选择数据')
                }
                let arr = []
                this.datalistselection.map(item => {
                    arr.push(item[this.idtext])
                })
                str = arr.join(',')
            } else {
                str = val[this.idtext]
            }
            this.$confirm('是否确定删除？', '提示', {
                closeOnClickModal: false,
                closeOnPressEscape: false,
            }).then(() => {
                this.deleconfim(str)
            }).catch(() => {

            })
            //this.getList()
        },
        async deleconfim(val) {
            let data = await this.$http.delete(this.url.del + val)
            if (data.code === 0) {
                this.getList()
                this.$message.success('删除成功')
            }
        },
        //多选
        handleSelectionChange(val) {
            this.datalistselection = val
        },
        handlebetchDelete() {
            if (this.datalistselection.length === 0) {
                return this.$message.error('请选择数据')
            }
            this.$confirm('是否确定删除？', '提示', {
                closeOnClickModal: false,
                closeOnPressEscape: false,
            }).then(() => {
                let arr = []
                this.datalistselection.map(item => {
                    arr.push(item[this.idtext])
                })
                this.deleconfim(arr.join(','))
            }).catch(() => {

            })

        },
        clearchose() {
            this.$refs.tables.clearSelection()
            this.datalistselection = []
        },
        handleSizeChange(val) {
            this.page_num = 1
            this.queryParams.page_size = val
            this.getList()
        },
        handleCurrentChange(val) {
            this.queryParams.page_num = val
            this.getList()
        },
        handleQuery() {
            this.queryParams.page_num = 1
            this.getList()
        },
        resetQuery() {
            this.queryParams = JSON.parse(JSON.stringify(this.delaultsearch))
            this.queryParams.page_num = 1
            this.queryParams.page_size = 10
            this.getList()
        }
    }
}
