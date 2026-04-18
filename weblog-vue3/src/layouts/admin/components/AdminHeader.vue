<template>
    <el-affix :offset="0">
        <header
            class="bg-white dark:bg-[#111] border-b border-gray-200 dark:border-gray-700 h-[76px] flex">
            <div class="w-full max-w-[1200px] mx-auto px-4 flex items-center">
            <!-- 横向菜单 -->
            <nav class="flex items-center gap-1">
                <a v-for="item in menus" :key="item.path" @click="router.push(item.path)"
                    class="cursor-pointer px-3 py-2 text-base rounded transition-colors"
                    :class="isActive(item.path)
                        ? 'text-[#1a1a1a] dark:text-white font-semibold'
                        : 'text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white'">
                    {{ item.name }}
                </a>
            </nav>

            <!-- 右侧工具区 -->
            <div class="ml-auto flex items-center gap-1">
                <!-- 主题切换 -->
                <el-tooltip effect="dark" :content="isDark ? '切换浅色' : '切换深色'" placement="bottom">
                    <button @click="toggleDark()"
                        class="w-[40px] h-[40px] rounded cursor-pointer flex items-center justify-center text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                        aria-label="切换深色模式">
                        <svg v-if="!isDark" class="w-[18px] h-[18px]" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
                            <path d="M512.000213 733.353497c-122.06857 0-221.353283-99.284713-221.353283-221.353284S389.931643 290.64693 512.000213 290.64693 733.353497 389.931643 733.353497 512.000213 634.026117 733.353497 512.000213 733.353497z m0-357.373767A136.148482 136.148482 0 0 0 375.97973 512.000213 136.148482 136.148482 0 0 0 512.000213 648.020697 136.148482 136.148482 0 0 0 648.020697 512.000213 136.148482 136.148482 0 0 0 512.000213 375.97973zM554.666613 171.735673A42.154403 42.154403 0 0 1 512.000213 213.335413c-23.551853 0-42.6664-18.645217-42.6664-41.59974V41.603153A42.154403 42.154403 0 0 1 512.000213 0.003413c23.551853 0 42.6664 18.645217 42.6664 41.59974v130.13252zM554.666613 982.397273A42.154403 42.154403 0 0 1 512.000213 1023.997013c-23.594519 0-42.6664-18.687883-42.6664-41.59974v-130.175186A42.111737 42.111737 0 0 1 512.000213 810.665013c23.551853 0 42.6664 18.60255 42.6664 41.59974v130.13252zM171.735673 469.333813c22.954523 0 41.59974 19.114547 41.59974 42.6664 0 23.594519-18.645217 42.6664-41.59974 42.6664H41.603153A42.154403 42.154403 0 0 1 0.003413 512.000213c0-23.551853 18.645217-42.6664 41.59974-42.6664h130.13252zM982.397273 469.333813c22.954523 0 41.59974 19.114547 41.59974 42.6664 0 23.594519-18.687883 42.6664-41.59974 42.6664h-130.175186A42.111737 42.111737 0 0 1 810.665013 512.000213c0-23.551853 18.60255-42.6664 41.59974-42.6664h130.13252zM241.239239 722.430898a42.06907 42.06907 0 0 1 59.562294 0.767995 42.111737 42.111737 0 0 1 0.767996 59.562295l-92.031425 92.074091a42.154403 42.154403 0 0 1-59.562295-0.853328 42.154403 42.154403 0 0 1-0.767995-59.562294l92.031425-91.988759zM814.462323 149.207814a42.154403 42.154403 0 0 1 59.562294 0.767995 42.154403 42.154403 0 0 1 0.767996 59.562295l-92.031425 92.031425a42.06907 42.06907 0 0 1-59.562295-0.767996 42.111737 42.111737 0 0 1-0.810661-59.562294l92.074091-92.031425zM241.239239 301.526862a42.19707 42.19707 0 0 0 59.604961-0.725329 42.111737 42.111737 0 0 0 0.767995-59.562294L209.538104 149.122481a42.154403 42.154403 0 0 0-59.562295 0.853328 42.111737 42.111737 0 0 0-0.767995 59.562295l92.031425 91.988758zM814.462323 874.792613a42.111737 42.111737 0 0 0 59.562294-0.810662 42.154403 42.154403 0 0 0 0.767996-59.562294l-92.031425-92.031425a42.06907 42.06907 0 0 0-59.562295 0.767995 42.111737 42.111737 0 0 0-0.810661 59.562294l92.074091 92.074092z" fill="currentColor"></path>
                        </svg>
                        <svg v-else class="w-[18px] h-[18px]" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
                            <path d="M576.389562 1024A522.303665 522.303665 0 0 1 238.759308 103.797568 513.161417 513.161417 0 0 1 425.472143 2.177965a52.251464 52.251464 0 0 1 55.556739 17.229621 52.251464 52.251464 0 0 1 4.782099 57.736813 466.465627 466.465627 0 0 0 4.219499 462.246128 470.122526 470.122526 0 0 0 425.114535 235.0261 52.251464 52.251464 0 0 1 48.80554 30.310069c9.142248 18.987746 5.977624 41.702716-8.087373 57.455513a520.264241 520.264241 0 0 1-379.47362 161.747466zM440.66234 51.827404c-61.885987 18.636121-119.20085 49.93074-168.217364 91.914756a470.122526 470.122526 0 1 0 644.528489 681.660082 522.303665 522.303665 0 0 1-471.880651-261.186995A518.646766 518.646766 0 0 1 440.52169 51.897729z" fill="currentColor"></path>
                        </svg>
                    </button>
                </el-tooltip>

                <!-- 刷新 -->
                <el-tooltip effect="dark" content="刷新" placement="bottom">
                    <button @click="handleRefresh"
                        class="w-[40px] h-[40px] rounded cursor-pointer flex items-center justify-center text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                        <el-icon><Refresh /></el-icon>
                    </button>
                </el-tooltip>

                <!-- 跳转前台首页 -->
                <el-tooltip effect="dark" content="跳转前台" placement="bottom">
                    <button @click="router.push('/')"
                        class="w-[40px] h-[40px] rounded cursor-pointer flex items-center justify-center text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                        <el-icon><Monitor /></el-icon>
                    </button>
                </el-tooltip>

                <!-- 全屏 -->
                <el-tooltip effect="dark" content="全屏" placement="bottom">
                    <button @click="toggle"
                        class="w-[40px] h-[40px] rounded cursor-pointer flex items-center justify-center text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors mr-1">
                        <el-icon>
                            <FullScreen v-if="!isFullscreen" />
                            <Aim v-else />
                        </el-icon>
                    </button>
                </el-tooltip>

                <!-- 用户下拉 -->
                <el-dropdown class="flex items-center justify-center" @command="handleCommand">
                    <span class="flex items-center justify-center text-[#707070] dark:text-gray-300 text-xs cursor-pointer">
                        <el-avatar class="mr-2" :size="28"
                            src="https://img.abswitchj.com/abswitchj/IMG_20260327_160706.jpg" />
                        {{ userStore.userInfo.username }}
                        <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="updatePassword">修改密码</el-dropdown-item>
                            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
            </div>
        </header>

        <!-- 修改密码 -->
        <FormDialog ref="formDialogRef" title="修改密码" destroyOnClose @submit="onSubmit">
            <el-form ref="formRef" :rules="rules" :model="form">
                <el-form-item label="用户名" prop="username" label-width="120px" size="large">
                    <el-input v-model="form.username" placeholder="请输入用户名" clearable disabled />
                </el-form-item>
                <el-form-item label="新密码" prop="password" label-width="120px" size="large">
                    <el-input type="password" v-model="form.password" placeholder="请输入新密码" clearable show-password />
                </el-form-item>
                <el-form-item label="确认新密码" prop="rePassword" label-width="120px" size="large">
                    <el-input type="password" v-model="form.rePassword" placeholder="请确认新密码" clearable show-password />
                </el-form-item>
            </el-form>
        </FormDialog>
    </el-affix>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useFullscreen, useDark, useToggle } from '@vueuse/core'
import { updateAdminPassword } from '@/api/admin/user'
import { showMessage, showModel } from '@/composables/util'
import { useRouter, useRoute } from 'vue-router'
import FormDialog from '@/components/FormDialog.vue'

const router = useRouter()
const route = useRoute()

// 深色模式
const isDark = useDark()
const toggleDark = useToggle(isDark)

// 全屏
const { isFullscreen, toggle } = useFullscreen()

const userStore = useUserStore()

// 顶部菜单
const menus = [
    { name: '仪表盘', path: '/admin/index' },
    { name: '文章管理', path: '/admin/article/list' },
    { name: '分类管理', path: '/admin/category/list' },
    { name: '标签管理', path: '/admin/tag/list' },
    { name: '博客设置', path: '/admin/blog/settings' },
]

// 菜单激活判断：当前路由以菜单路径开头
const isActive = (path) => {
    if (path === '/admin/index') return route.path === path
    const base = path.replace(/\/list$|\/settings$/, '')
    return route.path === path || route.path.startsWith(base + '/')
}

const handleRefresh = () => location.reload()

const formDialogRef = ref(null)

const handleCommand = (command) => {
    if (command == 'updatePassword') {
        formDialogRef.value.open()
    } else if (command == 'logout') {
        logout()
    }
}

function logout() {
    showModel('是否确认要退出登录？').then(() => {
        userStore.logout()
        showMessage('退出登录成功！')
        router.push('/login')
    })
}

const formRef = ref(null)

const form = reactive({
    username: userStore.userInfo.username || '',
    password: '',
    rePassword: ''
})

watch(() => userStore.userInfo.username, (newValue) => {
    form.username = newValue
})

const rules = {
    username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
    rePassword: [{ required: true, message: '确认密码不能为空', trigger: 'blur' }]
}

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) return false

        if (form.password != form.rePassword) {
            showMessage('两次密码输入不一致，请检查！', 'warning')
            return
        }

        formDialogRef.value.showBtnLoading()
        updateAdminPassword(form).then((res) => {
            if (res.success == true) {
                showMessage('密码重置成功，请重新登录！')
                userStore.logout()
                formDialogRef.value.close()
                router.push('/login')
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => formDialogRef.value.closeBtnLoading())
    })
}
</script>

