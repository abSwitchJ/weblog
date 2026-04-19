<template>
    <div>
        <!-- 表头分页查询条件， shadow="never" 指定 card 卡片组件没有阴影 -->
        <el-card shadow="never" class="mb-5">
            <!-- flex 布局，允许换行；中窄屏下按钮组占整行两端对齐 -->
            <div class="flex flex-wrap items-center gap-y-3">
                <div class="flex items-center w-full sm:w-auto sm:mr-5">
                    <el-text class="whitespace-nowrap">分类名称</el-text>
                    <div class="ml-3 flex-1 sm:flex-none sm:w-52"><el-input v-model="searchCategoryName" placeholder="请输入（模糊查询）" /></div>
                </div>

                <div class="flex items-center w-full sm:w-auto sm:mr-5">
                    <el-text class="whitespace-nowrap">创建日期</el-text>
                    <div class="ml-3 flex-1 min-w-0 sm:flex-none">
                        <!-- 日期选择组件（区间选择） -->
                        <el-date-picker v-model="pickDate" type="daterange" range-separator="至" start-placeholder="开始时间"
                            end-placeholder="结束时间" size="default" :shortcuts="shortcuts" @change="datepickerChange"
                            class="!w-full sm:!w-auto" />
                    </div>
                </div>

                <div class="flex items-center w-full lg:w-auto justify-between lg:justify-start">
                    <el-button type="primary" :icon="Search" @click="getTableData">查询</el-button>
                    <el-button class="ml-3" :icon="RefreshRight" @click="reset">重置</el-button>
                </div>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- 新增按钮 -->
            <div class="mb-5">
                <el-button type="primary" @click="addCategoryBtnClick">
                    <el-icon class="mr-1">
                        <Plus />
                    </el-icon>
                    新增</el-button>
            </div>

            <!-- 分页列表 -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="name" label="分类名称" min-width="360" />
                <el-table-column prop="articlesTotal" label="文章数" min-width="180" />
                <el-table-column prop="createTime" label="创建时间" min-width="180">
                    <template #default="scope">
                        <div class="flex flex-col md:flex-row md:gap-1">
                            <span>{{ scope.row.createTime?.split(' ')[0] }}</span>
                            <span>{{ scope.row.createTime?.split(' ')[1] }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="180">
                    <template #default="scope">
                    <el-button size="small" @click="editCategoryBtnClick(scope.row)">
                        <el-icon class="mr-1">
                            <Edit />
                        </el-icon>
                        编辑
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteCategorySubmit(scope.row)">
                        <el-icon class="mr-1">
                            <Delete />
                        </el-icon>
                        删除
                    </el-button>
                </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                :small="paginationSmall" :background="true" :layout="paginationLayout"
                :total="total" @size-change="handleSizeChange" @current-change="getTableData" />
            </div>

        </el-card>

    <!-- 添加/编辑分类 -->
    <FormDialog ref="formDialogRef" :title="editId ? '编辑文章分类' : '添加文章分类'" destroyOnClose @submit="onSubmit">
        <el-form ref="formRef" :rules="rules" :model="form">
                    <el-form-item label="分类名称" prop="name" label-width="80px" size="large">
                        <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" show-word-limit clearable/>
                    </el-form-item>
                </el-form>
    </FormDialog>

    </div>
</template>

<script setup>
import { Search, RefreshRight, Edit } from '@element-plus/icons-vue'
import { ref, reactive } from 'vue'
import { getCategoryPageList, addCategory, updateCategory, deleteCategory } from '@/api/admin/category'
import moment from 'moment'
import { showMessage, showModel } from '@/composables/util'
import { usePaginationLayout } from '@/composables/usePaginationLayout'
import FormDialog from '@/components/FormDialog.vue'

const { layout: paginationLayout, small: paginationSmall } = usePaginationLayout()

// 分页查询的分类名称
const searchCategoryName = ref('')
// 日期
const pickDate = ref('')

// 查询条件：开始结束时间
const startDate = reactive({})
const endDate = reactive({})

// 监听日期组件改变事件，并将开始结束时间设置到变量中
const datepickerChange = (e) => {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')

    console.log('开始时间：' + startDate.value + ', 结束时间：' + endDate.value)
}

const shortcuts = [
    {
        text: '最近一周',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        },
    },
    {
        text: '最近一个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        },
    },
    {
        text: '最近三个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        },
    },
]

// 表格加载 Loading
const tableLoading = ref(false)
// 表格数据
const tableData = ref([])
// 当前页码，给了一个默认值 1
const current = ref(1)
// 总数据量，给了个默认值 0
const total = ref(0)
// 每页显示的数据量，给了个默认值 10
const size = ref(10)


// 获取分页数据
function getTableData() {
    // 显示表格 loading
    tableLoading.value = true
    // 调用后台分页接口，并传入所需参数
    
    getCategoryPageList({current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, name: searchCategoryName.value})
    .then((res) => {
        if (res.success == true) {
        
            tableData.value = res.data
            current.value = res.current
            size.value = res.size
            total.value = res.total
        }
    })
    .finally(() => tableLoading.value = false) // 隐藏表格 loading
}
getTableData()

// 每页展示数量变更事件
const handleSizeChange = (chooseSize) => {
    console.log('选择的页码' + chooseSize)
    size.value = chooseSize
    getTableData()
}

// 重置查询条件
const reset = () => {
    searchCategoryName.value = ''
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
}

// 对话框是否显示
const formDialogRef = ref(null)

// 当前编辑分类 ID，为 null 表示新增模式
const editId = ref(null)

// 新增分类按钮点击事件
const addCategoryBtnClick = () => {
    editId.value = null
    form.name = ''
    formDialogRef.value.open()
}

// 编辑分类按钮点击事件
const editCategoryBtnClick = (row) => {
    editId.value = row.id
    form.name = row.name
    formDialogRef.value.open()
}


// 表单引用
const formRef = ref(null)

// 添加文章分类表单对象
const form = reactive({
    name: ''
})

// 规则校验
const rules = {
    name: [
        {
            required: true,
            message: '分类名称不能为空',
            trigger: 'blur',
        },
        { min: 1, max: 20, message: '分类名称字数要求大于 1 个字符，小于 20 个字符', trigger: 'blur' },
    ]
}

const onSubmit = () => {
    // 先验证 form 表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }

        const isEdit = editId.value !== null
        const request = isEdit
            ? updateCategory({ id: editId.value, name: form.name })
            : addCategory(form)

        // 显示提交按钮 loading
        formDialogRef.value.showBtnLoading()
        request.then((res) => {
            if (res.success == true) {
                showMessage(isEdit ? '修改成功' : '添加成功')
                // 重置表单状态
                form.name = ''
                editId.value = null
                // 隐藏对话框
                formDialogRef.value.close()
                // 重新请求分页接口，渲染数据
                getTableData()
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading()) // 隐藏提交按钮 loading

    })
}

// 删除分类
const deleteCategorySubmit = (row) => {
    console.log(row)
    showModel('是否确定要删除该分类？').then(() => {
        deleteCategory(row.id).then((res) => {
            if (res.success == true) {
                showMessage('删除成功')
                // 重新请求分页接口，渲染数据
                getTableData()
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
            }
        })
    }).catch(() => {
        console.log('取消了')
    })
}

</script>