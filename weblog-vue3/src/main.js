import '@/assets/main.css'
import 'animate.css';
import 'nprogress/nprogress.css'

import { createApp } from 'vue'
// 引入全局状态管理 Pinia
import pinia from '@/stores'
import App from '@/App.vue'
// 导入路由
import router from '@/router'
// 导入全局路由守卫
import '@/permission'
// 导入 i18n
import i18n from '@/i18n'
// 导入 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 导入 element-plus 暗黑 css
import 'element-plus/theme-chalk/dark/css-vars.css'
// 导入后台主题覆盖（需在 element-plus 暗色主题之后加载，保证覆盖生效）
import '@/assets/admin-theme.css'
// 图片点击放大
import 'viewerjs/dist/viewer.css'
import VueViewer from 'v-viewer'

const app = createApp(App)

// 应用路由
app.use(router)
// 应用 Pinia
app.use(pinia)
// 应用 i18n
app.use(i18n)

// 引入图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(VueViewer)

app.mount('#app')
