import { watch } from 'vue'
import router from '@/router/index'
import { getToken } from '@/composables/cookie'
import { showMessage } from '@/composables/util'
import { showPageLoading, hidePageLoading } from '@/composables/util'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { useLocaleStore } from '@/stores/locale'
import pinia from '@/stores'
import i18n from '@/i18n'

const SITE_NAME = 'abSwitchJ'

// 根据路由 meta 计算并应用浏览器标签标题
// - titleKey === 'home'：标题为品牌名
// - 其他 titleKey：标题为 i18n 对应文案 + | abSwitchJ
// - 无 titleKey（动态页）：由页面自行设置 document.title
// - 后台路由沿用 meta.title 中文硬编码
function applyTitle(route) {
    const { titleKey, title } = route.meta || {}

    if (titleKey === 'home') {
        document.title = SITE_NAME
        return
    }
    if (titleKey) {
        document.title = `${i18n.global.t(`title.${titleKey}`)} | ${SITE_NAME}`
        return
    }
    if (title) {
        document.title = title
    }
}

// 全局路由前置守卫
router.beforeEach((to, from, next) => {
    console.log('==> 全局路由前置守卫')

    // 展示页面加载 Loading
    showPageLoading()

    let token = getToken()

    if (!token && to.path.startsWith('/admin')) {
        // 若用户想访问后台（以 /admin 为前缀的路由）
        // 未登录，则强制跳转登录页
        showMessage('请先登录', 'warning')
        next({ path: '/login' })
    } else if (token && to.path == '/login') {
        // 若用户已经登录，且重复访问登录页
        showMessage('请勿重复登录', 'warning')
        // 跳转后台首页
        next({ path: '/admin/index' })
    } else if (!to.path.startsWith('/admin')) {
        // 如果访问的非 /admin 前缀路由
        // 引入博客设置 store
        let blogSettingsStore = useBlogSettingsStore()
        // 获取博客设置信息并保存到全局状态中
        blogSettingsStore.getBlogSettings()
        next()
    } else {
        next()
    }
})

// 全局路由后置守卫
router.afterEach((to, from) => {
    applyTitle(to)

    // 隐藏页面加载 Loading
    hidePageLoading()
})

// 语言切换时刷新当前路由的静态标题（动态页由页面自身 watch locale 刷新）
const localeStore = useLocaleStore(pinia)
// 初始同步 i18n 语言，避免首次加载时 locale 与持久化的 store 不一致
i18n.global.locale.value = localeStore.locale
watch(() => localeStore.locale, (val) => {
    // 先同步 i18n 语言，确保 t() 取到新语言
    i18n.global.locale.value = val
    applyTitle(router.currentRoute.value)
})
