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

---

## 前台文章详情页设计规范

**文件**：`src/pages/frontend/article-detail.vue`

章详情页时的路由从 /article/:id 改为 /article/:slug.html。数据库表可以加一个slug字段。文章slug(来自文 
章标题)。

生成 slug 的方式：英文标题：直接转小写 + 空格用“-”代替，中文标题：翻译为英文再转小写 + 空格用“-”代替

文章详情页采用**复古报纸排版风格**：Georgia 衬线字体、米黄色页面背景、白色报纸容器（最大宽度 1100px）、`2fr 1fr` 两列 grid（文章正文 + 右侧 Toc 目录）、双线分隔报头、两端对齐正文排版。移动端折叠为单列。具体 CSS 规格（颜色值、字号、边框粗细）参见组件文件内的 `<style>` 部分。

Giscus 评论区位于报纸容器外部独立渲染，自定义样式在 `public/css/comment.css` 中定义。

深色模式（`useDark`）全程支持，highlight.js 代码高亮、代码复制按钮、`v-viewer` 图片灯箱保留。

现在由于文章详情页的slug改动，导致识别不出上一页和下一页了，按创建日期进行判断上一页（比当前文章晚发布的）和下一页（比当前文章晚发布的）

## 前台index页设计规范

**文件**：`src/pages/frontend/index.vue`

首页采用卡片式文章列表、 居中单列布局：米黄色\#f7f7f4页面背景、每一篇文章都是一个 独立卡片（最大宽度 1100px、高度适应显示的标题和摘要、白色背景）每个卡片内部是：

标题（  color: #1a1a1a;font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;）

日期（格式：yyyy-MM-dd，灰色小号字体，由【】包围）摘要（正文黑色）

标题日期正摘要对齐，日期后面接摘要

每页含十个卡片，卡片间间隔10px，不需要显示页码

当没有上一页或下一页时不需要出现上一页或下一页的组件

下一页组件胶囊状黑色背景置于页尾，、字体为白色，和卡片式文章列表右对齐

上一页组件胶囊状黑色背景置于页尾，、字体为白色，和卡片式文章列表左对齐

## 前台归档页设计规范

**文件**：`src/pages/frontend/archive.vue`

archive采用时间节点居中单列纵向布局：米黄色\#f7f7f4页面背景

（color: #1a1a1a;font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;）

下面这个设计的最大宽度为 600px

```
yyyy
mm  
    dd 文章标题
```

所有文章都要全部显示在归档页，不可分页

## 前台页脚

**文件**：`src\layouts\frontend\components\Footer.vue`

不需要页脚组件，把页脚的内容© 2026 [abSwitchJ](https://www.abswitchj.com) · 备案号：[赣ICP备2026006527号]([ICP/IP地址/域名信息备案管理系统](https://beian.miit.gov.cn/#/Integrated/index))

放到最底部居中，米黄色\#f7f7f4页面背景

## 前台导航栏

**文件**：`src\layouts\frontend\components\Header.vue`

水平flex布局：白色背景#fff，最大宽度为 1100px，两端对齐

左侧分别是：abSwitchJ：跳转主页、Archive：跳转归档页：https://abswitchj.com/archive、GitHub：跳转https://github.com/abSwitchJ、Twitter：跳转https://x.com/AbswitchJ、About：跳转https://abswitchj.com/about（自我介绍页面）

abSwitchJ右侧有一个切换白天（太阳）和黑夜（月亮）的按钮

太阳

```
<svg t="1776267358068" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5535" width="200" height="200"><path d="M512.000213 733.353497c-122.06857 0-221.353283-99.284713-221.353283-221.353284S389.931643 290.64693 512.000213 290.64693 733.353497 389.931643 733.353497 512.000213 634.026117 733.353497 512.000213 733.353497z m0-357.373767A136.148482 136.148482 0 0 0 375.97973 512.000213 136.148482 136.148482 0 0 0 512.000213 648.020697 136.148482 136.148482 0 0 0 648.020697 512.000213 136.148482 136.148482 0 0 0 512.000213 375.97973zM554.666613 171.735673A42.154403 42.154403 0 0 1 512.000213 213.335413c-23.551853 0-42.6664-18.645217-42.6664-41.59974V41.603153A42.154403 42.154403 0 0 1 512.000213 0.003413c23.551853 0 42.6664 18.645217 42.6664 41.59974v130.13252zM554.666613 982.397273A42.154403 42.154403 0 0 1 512.000213 1023.997013c-23.594519 0-42.6664-18.687883-42.6664-41.59974v-130.175186A42.111737 42.111737 0 0 1 512.000213 810.665013c23.551853 0 42.6664 18.60255 42.6664 41.59974v130.13252zM171.735673 469.333813c22.954523 0 41.59974 19.114547 41.59974 42.6664 0 23.594519-18.645217 42.6664-41.59974 42.6664H41.603153A42.154403 42.154403 0 0 1 0.003413 512.000213c0-23.551853 18.645217-42.6664 41.59974-42.6664h130.13252zM982.397273 469.333813c22.954523 0 41.59974 19.114547 41.59974 42.6664 0 23.594519-18.687883 42.6664-41.59974 42.6664h-130.175186A42.111737 42.111737 0 0 1 810.665013 512.000213c0-23.551853 18.60255-42.6664 41.59974-42.6664h130.13252zM241.239239 722.430898a42.06907 42.06907 0 0 1 59.562294 0.767995 42.111737 42.111737 0 0 1 0.767996 59.562295l-92.031425 92.074091a42.154403 42.154403 0 0 1-59.562295-0.853328 42.154403 42.154403 0 0 1-0.767995-59.562294l92.031425-91.988759zM814.462323 149.207814a42.154403 42.154403 0 0 1 59.562294 0.767995 42.154403 42.154403 0 0 1 0.767996 59.562295l-92.031425 92.031425a42.06907 42.06907 0 0 1-59.562295-0.767996 42.111737 42.111737 0 0 1-0.810661-59.562294l92.074091-92.031425zM241.239239 301.526862a42.19707 42.19707 0 0 0 59.604961-0.725329 42.111737 42.111737 0 0 0 0.767995-59.562294L209.538104 149.122481a42.154403 42.154403 0 0 0-59.562295 0.853328 42.111737 42.111737 0 0 0-0.767995 59.562295l92.031425 91.988758zM814.462323 874.792613a42.111737 42.111737 0 0 0 59.562294-0.810662 42.154403 42.154403 0 0 0 0.767996-59.562294l-92.031425-92.031425a42.06907 42.06907 0 0 0-59.562295 0.767995 42.111737 42.111737 0 0 0-0.810661 59.562294l92.074091 92.074092z" fill="#707070" p-id="5536"></path></svg>
```

月亮

```
<svg t="1776267549052" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6693" width="200" height="200"><path d="M576.389562 1024A522.303665 522.303665 0 0 1 238.759308 103.797568 513.161417 513.161417 0 0 1 425.472143 2.177965a52.251464 52.251464 0 0 1 55.556739 17.229621 52.251464 52.251464 0 0 1 4.782099 57.736813 466.465627 466.465627 0 0 0 4.219499 462.246128 470.122526 470.122526 0 0 0 425.114535 235.0261 52.251464 52.251464 0 0 1 48.80554 30.310069c9.142248 18.987746 5.977624 41.702716-8.087373 57.455513a520.264241 520.264241 0 0 1-379.47362 161.747466zM440.66234 51.827404c-61.885987 18.636121-119.20085 49.93074-168.217364 91.914756a470.122526 470.122526 0 1 0 644.528489 681.660082 522.303665 522.303665 0 0 1-471.880651-261.186995A518.646766 518.646766 0 0 1 440.52169 51.897729z" p-id="6694" fill="#707070"></path></svg>
```

右侧是分别是：搜索符号（一个放大镜）、中英文转换按钮（当前页面为中文时显示En/当前为英文时显示中）

向下滚动时导航栏自动收起（隐藏），向上滚动时自动出现（展开）。

搜索时

搜索出来的文章结构要和index页的卡片式文章一样，搜索要基于标题、摘要、正文、日期（关键词高亮）

搜索框（黑色主题时#1e1e1e）类似于这个![image-20260416101959315](C:\Users\--\AppData\Roaming\Typora\typora-user-images\image-20260416101959315.png)

生成 slug 的方式：英文标题：直接转小写 + 空格用“-”代替，中文标题：翻译为英文再转小写 + 空格用“-”代替

## 前台分类页

路由：https://abswitchj.com/category/分类名

卡片列表最上方是分类名与卡片左对齐（字体为默认，颜色黑色，大小需要美观）

采用卡片式文章列表（效果与index页的一样）

## 前台分类列表页

路由：https://abswitchj.com/category/list

米黄色背景、居中单列（max-width 600px）与archive页一致，每个分类（左对齐）单独一行（不需要被包裹），右上角黑色胶囊角标显示文章总数。分类换行显示文章卡片（Georgia 衬线标题、【日期】+  摘要）（max-width 500px）

## 前台中英文切换待开发

在切换成英文时，要求整个前台所有都要切换成英文

## 前台router标题待开发

通过路由 meta 或 API 数据，动态设置 `<title>` 标签，从而实现文章标题、分类名、自动出现在浏览器标签栏。

## 前台about页待开发

路由：https://abswitchj.com/about

是一个自我介绍的页面米黄色背景

居中白色卡片放置自我介绍内容（max-width 600px）：

开头是“关于我”文字（字体为默认，颜色黑色，大小需要美观）

然后分点介绍

分点介绍后是“关注我”文字形状和“关于我“一致

- [Github](https://github.com/abSwitchJ)
- [Twitter](https://twitter.com/abSwitchJ)
- 微信

类似于下面这种页面

![image-20260416211605539](C:\Users\--\AppData\Roaming\Typora\typora-user-images\image-20260416211605539.png)

## 404页面待开发

重构"C:\Users\--\Desktop\tw93.github.io\404.html"页面，集成到我的项目中，检查所有代码以确保不报错

## 当前问题待修复

- 前台分类页：路由：http://127.0.0.1:5173/category/分类名
  - 当分类名不存在时不报错
  - 当还未查询出文章时率先显示的是当前分类下还没有数据

- 文章详情页：路由：https://www.abswitchj.com/article/test.html

  - 详情页的目录条目需要在后端和数据库中创建和修复
    - 每个目录条目用浅灰色细线分隔
    - 每个目录条目需要能跳转到文章的对应位置
    - 修复前端和后端代码，确保项目能正常运行

  - 文章详情页，当内容标题日期还未查询出来时，确先显示了页面结构导致不美观（如下图）

    ![image-20260416215038685](C:\Users\--\AppData\Roaming\Typora\typora-user-images\image-20260416215038685.png)

- 当页面内容不够长时页脚与下面分层，也导致页脚内容没有置底
