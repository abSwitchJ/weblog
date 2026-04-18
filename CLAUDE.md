# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在该仓库中工作时提供指导。

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 仓库结构

这是一个包含两个子项目的 monorepo：
- `weblog-vue3/` — Vue 3 前端（当前目录）
- `weblog-springboot/` — Spring Boot 3 后端（同级目录）

---

## 前端（weblog-vue3）

### 常用命令

```bash
npm run dev       # 开发服务器，地址 http://127.0.0.1:5173（/api 代理到 localhost:8080）
npm run build     # 生产构建，输出到 dist/
npm run preview   # 预览生产构建
```

本项目未配置测试运行器。

### Vite 配置要点

- `@` 别名指向 `src/`
- Element Plus 通过 `unplugin-auto-import` 和 `unplugin-vue-components` 自动导入，组件无需手动 import
- ECharts 通过 `vite-plugin-cdn-import` 从 CDN（bootcdn）加载，在构建时排除打包——不要在代码中 `import echarts`，直接使用全局变量 `echarts`
- `__GISCUS_CONFIG__` 在 `vite.config.js` 的 `define` 中全局定义，包含 Giscus 仓库配置（repo、repoId、category、categoryId）
- 生产构建通过 `vite-plugin-compression` 启用 gzip 压缩（JS/CSS 文件 >1KB）

### 环境变量

- `.env.development`：`VITE_APP_BASE_API='/api'`（Vite 代理到 `localhost:8080`，去掉 `/api` 前缀后转发）
- `.env.production`：`VITE_APP_BASE_API='https://www.abswitchj.com/api'`
- `src/axios.js` 中通过 `import.meta.env.VITE_APP_BASE_API` 读取作为 Axios 的 `baseURL`

### 架构概览

**入口**：`src/main.js` → 挂载 Vue 应用，注册插件（Pinia、Router、Element Plus 等）

**路由**（`src/router/index.js`）：Hash 模式 Vue Router v4，包含两棵路由树：
- 公开路由：`/`、`/archive/list`、`/category/list`、`/tag/list`、`/article/:id`、`/login` 等
- 管理路由：`/admin/*` — 由 `src/permission.js` 守卫（需要 cookie 中的 JWT token）

**全局路由守卫**（`src/permission.js`）：显示 NProgress，检查管理路由的 token，并在公开路由进入时获取博客设置。

**HTTP 层**（`src/axios.js`）：Axios 实例，base URL 为 `/api`。每次请求从 cookie 中注入 `Authorization: Bearer {token}` 请求头，遇到 401 时清除用户状态并重新加载。

**状态管理**（`src/stores/`）：Pinia + `pinia-plugin-persistedstate`。
- `user.js` — userInfo、setUserInfo()、logout()
- `blogsettings.js` — 博客配置，获取一次后缓存
- `menu.js` — 管理后台侧边栏宽度状态

**Composables**（`src/composables/`）：
- `cookie.js` — `getToken()` / `setToken()` / `removeToken()` 管理 JWT cookie（key 为 `Authorization`）；`getTabList()` / `setTabList()` 管理后台标签页 cookie
- `util.js` — `showMessage()` 封装 ElMessage；`showModel()` 封装 ElMessageBox 确认弹窗；`showPageLoading()` / `hidePageLoading()` 封装 nprogress
- `useTabList.js` — 后台标签页导航 hook，管理标签页的打开、关闭、切换，始终保留 `/admin/index` 仪表盘标签

**API 层**（`src/api/`）：分为 `admin/`（需认证）和 `frontend/`（公开）。所有调用均使用 POST，包括读取操作。

**页面布局**：
- **后台页面**（`src/pages/admin/`）：嵌套在 `/admin` 路由下，由 `src/layouts/admin/admin.vue` 统一包裹，包含 AdminMenu、AdminHeader、AdminFooter、AdminTagList 四个子组件，KeepAlive 最多缓存 10 个页面实例
- **前台页面**（`src/pages/frontend/`）：无统一布局包裹组件，各页面手动引入 `src/layouts/frontend/components/` 下的布局组件：
  - 所有前台页面引入 Header、Footer
  - 列表类页面（index、archive-list、category-list、tag-list、category-article-list、tag-article-list）额外引入 UserInfoCard、CategoryListCard、TagListCard、ScrollToTopButton
  - article-detail 引入 ScrollToTopButton、Toc（目录组件）

**深色模式**：使用 VueUse 的 `useDark()` composable，配合 Tailwind 的 class 策略，切换 `<html>` 元素上的 `dark` class。Element Plus 暗色主题通过 `main.js` 中引入 `element-plus/theme-chalk/dark/css-vars.css` 实现。

**主要依赖**：Element Plus（UI）、md-editor-v3（Markdown 编辑器/渲染器）、ECharts（Dashboard 图表）、Giscus（文章评论）、Tailwind CSS、GSAP（动画）、highlight.js、v-viewer（图片灯箱）。

### API 响应结构

```json
{ "success": true, "data": { ... }, "message": "" }
```

错误通过 `src/composables/util.js` 的 `showMessage()` 展示（封装 Element Plus ElMessage）。

### 关键注意事项

- 博客设置在**每次**非管理端路由切换时都会调用 `getBlogSettings()` 重新请求（在 `src/permission.js` 的 `beforeEach` 守卫中），非一次性缓存
- 登录状态仅依赖 cookie 中 token 的存在与否（通过 `getToken()` 判断），无响应式认证状态，token 过期只在收到 401 响应时才被清除并刷新页面
- 每个路由必须定义 `meta.title`，`permission.js` 的 `afterEach` 将其拼接为 `{meta.title} - Weblog` 作为页面标题
- 天爱验证码 SDK 通过 `<script>` 标签从后端 `/tac/js/tac.min.js` 加载，不是 npm 包，相关组件为 `src/components/Captcha.vue`
- 分页参数模式（`current`、`size`、`total`）在各列表页面重复实现，无共享分页组件

---

## 后端（weblog-springboot）

### 常用命令

```bash
# 在 weblog-springboot/ 目录下执行
mvn clean install                    # 构建所有模块
mvn spring-boot:run -pl weblog-web   # 启动应用
mvn clean package                    # 打包可部署的 JAR
```

需要 Java 21。Maven 镜像已配置为阿里云。

### 模块结构（依赖顺序）

1. **weblog-module-common** — 领域对象（DO）、MyBatis Plus Mapper、共享配置、AOP（`@ApiOperationLog`）、自定义异常、工具类
2. **weblog-module-jwt** — Spring Security + JWT 认证（`JwtAuthenticationFilter`、bcrypt 密码加密）
3. **weblog-module-search** — Lucene 8.11 全文搜索，使用中文分词器；通过 `InitLuceneIndexRunner` 在启动时建立文章索引
4. **weblog-module-admin** — 管理端 REST 控制器（`/admin/*` 接口）：文章、分类、标签、用户、博客设置、文件上传（Minio）、Dashboard 指标
5. **weblog-web** — 应用入口；聚合所有模块；托管公开控制器（文章列表/详情、归档、分类、标签、搜索、验证码、博客设置、统计）

### 关键架构决策

- **所有接口均使用 POST**（读取数据也不使用 REST 动词）
- **URL 前缀**：所有后端接口均在 `/api` 下；开发环境由 Vite 代理去除该前缀；生产环境通过已部署服务器路由
- **认证**：JWT Bearer Token，有效期 24 小时，由 `POST /login` 签发；管理接口通过 Spring Security `@PreAuthorize("hasRole('ROLE_ADMIN')")` 保护
- **Markdown**：原始 Markdown 存储在 `ArticleContentDO` 中；由 CommonMark（含表格、标题锚点、任务列表、图片属性扩展）在服务端渲染为 HTML
- **文件存储**：通过管理面板上传图片至 Minio 对象存储
- **验证码**：登录页使用天爱验证码（Tianai-Captcha）行为验证（滑动/点击）
- **API 文档**：开发环境下 Knife4j 可在 `/doc.html` 访问

### 数据模型（weblog-module-common）

核心实体：`ArticleDO`、`ArticleContentDO`、`ArticleCategoryRelDO`、`ArticleTagRelDO`、`CategoryDO`、`TagDO`、`UserDO`、`BlogSettingsDO`

MapStruct 转换器（`*Convert.java`）负责 web 模块和 admin 模块中的 DO ↔ VO 映射。
