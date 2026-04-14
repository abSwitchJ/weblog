# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在该仓库中工作时提供指导。

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

**API 层**（`src/api/`）：分为 `admin/`（需认证）和 `frontend/`（公开）。所有调用均使用 POST，包括读取操作。

**页面**（`src/pages/frontend/` 和 `src/pages/admin/`）：每个页面一个 Vue SFC 文件。管理页面使用 `src/layouts/admin/admin.vue` 共享布局。

**主要依赖**：Element Plus（UI）、md-editor-v3（Markdown 编辑器/渲染器）、ECharts（Dashboard 图表）、Giscus（文章评论）、Tailwind CSS、GSAP（动画）、highlight.js、v-viewer（图片灯箱）。

### API 响应结构

```json
{ "success": true, "data": { ... }, "message": "" }
```

错误通过 `src/composables/util.js` 中的 Element Plus `ElMessage` 展示。

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

---

## 前台文章详情页设计规范

**文件**：`src/pages/frontend/article-detail.vue`

文章详情页采用**复古报纸排版风格**，具体规则如下：

### 布局结构
- 页面背景：米黄色 `#f5f5f0`（深色模式 `#111`）
- 报纸容器：白色背景 + `box-shadow`，最大宽度 `1100px`，居中显示
- 主内容区：`2fr 1fr` 两列 grid（文章正文 + 右侧目录）
- 移动端：两列折叠为单列

### 报头（Masthead）
- 文章标题：居中，Georgia/衬线字体，2.6em，letter-spacing
- 双线分隔：`border-top: 3px double #1a1a1a`
- 发布信息行（byline）：斜体，格式为 `日期 · 分类   `左对齐， ` 字数 · 阅读时长 `右对齐

### 文章内容排版
- 正文段落：`text-align: justify`（两端对齐）+ Georgia 衬线字体
- `h2` 标题：全大写 + `text-transform: uppercase` + 2px 粗实线底边
- `h3` 标题：衬线粗体，无下划线
- `blockquote`：浅米黄背景 `#f8f7f4` + 4px 左实线边框

### 侧边栏
- **文章目录（`Toc` 组件）**，可折叠
- `Toc` 使用 `::v-deep` 覆盖背景色以匹配报纸风格（浅米黄 `#faf9f7`）

### 上下篇导航

- 在文章内容结尾下方

### Giscus 评论区

**样式文件**：`public/css/comment.css`

位于上下篇导航下方、报纸容器组件之外，独立渲染。样式分四个区域：

- **顶部**：隐藏原生标题栏，用 `::after` 伪元素将 reaction 计数替换为”欢迎一起交流~”欢迎语
- **评论列表**：字号 15px，行高 1.8，时间线细线
- **输入框**：带边框圆角外框，Tab 选中态有黑色底线，去掉 focus 光晕
- **按钮**：纯黑背景，无阴影无边框，圆角

整体配色：白色背景，浅灰边框（`#eee`），黑色主色调（`#222`），字体与正文一致。

### 保留元素
- `Header`、`Footer`、`ScrollToTopButton` 组件不变
- highlight.js 代码高亮、代码复制按钮、`v-viewer` 图片灯箱保留
- 深色模式（`useDark`）全程支持
