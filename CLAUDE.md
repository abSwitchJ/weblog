# CLAUDE.md

本文件为 Claude Code 在该仓库工作时提供指导。所有交流使用中文。

## 仓库结构

Monorepo，两个子项目：
- `weblog-vue3/` — Vue 3 前端
- `weblog-springboot/` — Spring Boot 3 后端（Java 21，Maven 阿里云镜像）

---

## 前端（weblog-vue3）

### 命令

```bash
npm run dev       # http://127.0.0.1:5173，/api 代理到 localhost:8080
npm run build     # 输出到 dist/
npm run preview
```

未配置测试运行器。

### Vite 配置要点

- `@` 别名 → `src/`
- Element Plus 通过 `unplugin-auto-import` + `unplugin-vue-components` 自动导入，无需手动 import
- ECharts 通过 `vite-plugin-cdn-import` 从 bootcdn 加载，构建时排除。**不要 `import echarts`**，直接用全局 `echarts`
- `__GISCUS_CONFIG__` 在 `vite.config.js` 的 `define` 中定义（repo、repoId、category、categoryId）
- 生产构建用 `vite-plugin-compression` 对 >1KB 的 JS/CSS 启用 gzip

### 环境变量

- `.env.development`：`VITE_APP_BASE_API='/api'`（Vite 代理 `localhost:8080`，去掉 `/api` 前缀）
- `.env.production`：`VITE_APP_BASE_API='https://www.abswitchj.com/api'`
- `src/axios.js` 用该变量作为 Axios `baseURL`

### 架构

- **入口**：`src/main.js` 挂载应用并注册 Pinia、Router、Element Plus 等
- **路由**（`src/router/index.js`）：Hash 模式 Vue Router v4，两棵树：公开路由（`/`、`/archive/list`、`/category/list`、`/tag/list`、`/article/:id`、`/login`）与 `/admin/*`（需 JWT）
- **守卫**（`src/permission.js`）：NProgress 进度条；检查管理路由 token；公开路由每次切换都调 `getBlogSettings()`（非一次性缓存）；`afterEach` 拼接 `{meta.title} - Weblog`，每个路由必须定义 `meta.title`
- **HTTP**（`src/axios.js`）：从 cookie 注入 `Authorization: Bearer {token}`；401 时清用户状态并 reload
- **状态**（`src/stores/`，Pinia + `pinia-plugin-persistedstate`）：
  - `user.js` — userInfo、setUserInfo()、logout()
  - `blogsettings.js` — 博客配置
  - `menu.js` — 后台侧边栏宽度
- **Composables**（`src/composables/`）：
  - `cookie.js` — `getToken/setToken/removeToken`（cookie key 为 `Authorization`）、`getTabList/setTabList`
  - `util.js` — `showMessage`（ElMessage）、`showModel`（ElMessageBox 确认）、`showPageLoading/hidePageLoading`（nprogress）
  - `useTabList.js` — 后台标签页 hook，始终保留 `/admin/index` 仪表盘标签
- **API**（`src/api/`）：`admin/`（需认证）、`frontend/`（公开）。**所有调用均 POST**，包括读
- **布局**：
  - 后台（`src/pages/admin/`）：由 `src/layouts/admin/admin.vue` 统一包裹，含 AdminMenu、AdminHeader、AdminFooter、AdminTagList；KeepAlive 最多 10 实例
  - 前台（`src/pages/frontend/`）：无统一包裹，各页手动引入 `src/layouts/frontend/components/` 下组件
    - 全部页面：Header、Footer
    - 列表页（index、archive-list、category-list、tag-list、category-article-list、tag-article-list）额外：UserInfoCard、CategoryListCard、TagListCard、ScrollToTopButton
    - article-detail：ScrollToTopButton、Toc
- **深色模式**：VueUse `useDark()` + Tailwind class 策略，切 `<html>.dark`；Element Plus 暗色通过 `main.js` 引入 `element-plus/theme-chalk/dark/css-vars.css`
- **主要依赖**：Element Plus、md-editor-v3、ECharts、Giscus、Tailwind CSS、GSAP、highlight.js、v-viewer

### API 响应

```json
{ "success": true, "data": { ... }, "message": "" }
```

错误用 `showMessage()` 展示。

### 注意事项

- 登录状态仅依赖 cookie token 存在性（`getToken()`），无响应式认证状态；token 过期只在 401 时清除并刷新
- 天爱验证码 SDK 由 `<script>` 从后端 `/tac/js/tac.min.js` 加载（非 npm 包），组件在 `src/components/Captcha.vue`
- 分页参数（`current`、`size`、`total`）在各列表页重复实现，无共享分页组件

---

## 后端（weblog-springboot）

### 命令

```bash
# 在 weblog-springboot/ 下
mvn clean install
mvn spring-boot:run -pl weblog-web
mvn clean package
```

### 模块（依赖顺序）

1. **weblog-module-common** — DO、MyBatis Plus Mapper、共享配置、AOP（`@ApiOperationLog`）、自定义异常、工具类
2. **weblog-module-jwt** — Spring Security + JWT（`JwtAuthenticationFilter`、bcrypt）
3. **weblog-module-search** — Lucene 8.11 中文分词全文搜索；`InitLuceneIndexRunner` 启动时建索引
4. **weblog-module-admin** — `/admin/*` 控制器：文章、分类、标签、用户、博客设置、文件上传（Minio）、Dashboard 指标
5. **weblog-web** — 入口；聚合所有模块；公开控制器（文章列表/详情、归档、分类、标签、搜索、验证码、博客设置、统计）

### 架构决策

- **所有接口均 POST**（读也不用 REST 动词）
- **URL 前缀 `/api`**：开发环境由 Vite 代理去除；生产由部署服务器路由
- **认证**：JWT Bearer，24 小时有效，`POST /login` 签发；管理接口 `@PreAuthorize("hasRole('ROLE_ADMIN')")`
- **Markdown**：原文存 `ArticleContentDO`；CommonMark 服务端渲染 HTML（表格、标题锚点、任务列表、图片属性扩展）
- **文件**：后台上传图片至 Minio
- **验证码**：登录页用天爱验证码（Tianai-Captcha）行为验证
- **API 文档**：开发环境 Knife4j 在 `/doc.html`

### 数据模型

核心实体：`ArticleDO`、`ArticleContentDO`、`ArticleCategoryRelDO`、`ArticleTagRelDO`、`CategoryDO`、`TagDO`、`UserDO`、`BlogSettingsDO`。

MapStruct `*Convert.java` 负责 DO ↔ VO 映射（web 与 admin 模块）。
