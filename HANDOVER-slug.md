# 文章 Slug 路由改造 — 交接文档

## 背景

将文章详情页 URL 从 `/article/:id` 改为 `/article/:slug.html`，slug 由文章标题生成（英文直接转，中文通过百度翻译 API 翻译为英文后转）。

---

## 已完成的改动

### 后端（全部完成）

| 文件 | 改动内容 |
|------|---------|
| `weblog-web/src/main/resources/db/init.sql` | `t_article` 表新增 `slug` 字段 + `uk_slug` 唯一索引 |
| `weblog-module-common/.../dos/ArticleDO.java` | 新增 `private String slug;` |
| `weblog-module-common/.../utils/BaiduTranslateUtil.java` | **新文件**，调用百度翻译 API |
| `weblog-module-common/.../utils/SlugUtil.java` | **新文件**，`generateSlug()` + `ensureUnique()` |
| `weblog-module-common/.../config/BaiduTranslateProperties.java` | **新文件**，读取 `baidu.translate.app-id` 和 `secret-key` |
| `application-dev.yml` / `application-prod.yml` | 新增 `baidu.translate` 配置段（需填入真实 appId） |
| `weblog-module-common/.../mapper/ArticleMapper.java` | 新增 `selectBySlug()`、`existsBySlug()`；`selectAllForArchive()` select 列表追加 slug |
| `weblog-module-admin/.../impl/AdminArticleImpl.java` | `publishArticle()` 插入前生成 slug；`updateArticle()` 标题变更时重新生成 slug |
| `weblog-module-admin/.../vo/article/FindArticlePageListRspVO.java` | 新增 `private String slug;` |
| `weblog-module-admin/.../vo/article/FindArticleDetailRspVO.java` | 新增 `private String slug;` |
| `weblog-web/.../vo/article/FindArticleDetailBySlugReqVO.java` | **新文件**，接收 `@NotBlank String slug` |
| `weblog-web/.../controller/ArticleController.java` | `findArticleDetail` 改为接收 `FindArticleDetailBySlugReqVO` |
| `weblog-web/.../service/ArticleService.java` | `findArticleDetail` 改名为 `findArticleDetailBySlug` |
| `weblog-web/.../service/impl/ArticleImpl.java` | 用 `articleMapper.selectBySlug(slug)` 替代 `selectById(id)` |
| `weblog-web/.../vo/article/FindIndexArticlePageListRspVO.java` | 新增 `private String slug;` |
| `weblog-web/.../vo/article/ArticleRspVO.java` | 新增 `private String articleSlug;`（上下篇） |
| `weblog-web/.../vo/FindCategoryOrTagArticlePageListRspVO.java` | 新增 `private String slug;` |
| `weblog-web/.../vo/archive/FindArchiveArticleRspVO.java` | 新增 `private String slug;` |
| `weblog-web/.../vo/search/SearchArticlePageListRspVO.java` | 新增 `private String slug;` |
| `weblog-web/.../convert/ArticleConvert.java` | `convertDO2ArticleVO` 新增 `@Mapping(target = "articleSlug", ...)` |
| `weblog-module-search/.../index/ArticleIndex.java` | 新增 `COLUMN_SLUG = "slug"` |
| `weblog-module-search/.../runner/InitLuceneIndexRunner.java` | 建索引时新增 `StringField(COLUMN_SLUG)` |
| `weblog-module-admin/.../subscriber/PublishArticleSubscriber.java` | 文档新增 slug 字段 |
| `weblog-module-admin/.../subscriber/UpdateArticleSubscriber.java` | 文档新增 slug 字段 |
| `weblog-web/.../service/impl/SearchImpl.java` | 搜索结果 VO 中读取并设置 slug |
| `weblog-web/.../runner/MigrateArticleSlugRunner.java` | **新文件**，启动时为 `slug=''` 的存量文章自动生成 slug |

### 前端（部分完成）

| 文件 | 改动内容 | 状态 |
|------|---------|------|
| `src/router/index.js` | `path` 改为 `/article/:slug([\\w-]+)\\.html` | 已完成 |
| `src/api/frontend/article.js` | `getArticleDetail(slug)` 发送 `{slug}` | 已完成 |
| `src/pages/frontend/article-detail.vue` | `route.params.slug`、`refreshArticleDetail(slug)`、watch 回调、上下篇导航改 `articleSlug + '.html'` | 已完成 |

---

## 未完成的改动

### 前端：6 处文章跳转函数需要把 `article.id` 改为 `article.slug`

每处需要改 2 个地方：模板中的 `@click` 调用参数 + script 中的函数体。

#### 1. `src/pages/frontend/index.vue`

**模板（约第 11 行）：**
```
// 改前
@click="goArticleDetailPage(article.id)"
// 改后
@click="goArticleDetailPage(article.slug)"
```

**函数（约第 85 行）：**
```js
// 改前
const goArticleDetailPage = (id) => {
    router.push('/article/' + id)
}
// 改后
const goArticleDetailPage = (slug) => {
    router.push('/article/' + slug + '.html')
}
```

#### 2. `src/pages/frontend/archive-list.vue`

**模板（第 23 行）：**
```
// 改前
@click="goArticleDetailPage(article.id)"
// 改后
@click="goArticleDetailPage(article.slug)"
```

**函数（第 82 行）：**
```js
// 改前
const goArticleDetailPage = (id) => {
    router.push('/article/' + id)
}
// 改后
const goArticleDetailPage = (slug) => {
    router.push('/article/' + slug + '.html')
}
```

#### 3. `src/pages/frontend/category-article-list.vue`

**模板（第 60 行）：**
```
// 改前
@click="goArticleDetailPage(article.id)"
// 改后
@click="goArticleDetailPage(article.slug)"
```

**函数（第 379 行）：**
```js
// 同上，id → slug，路径加 .html
```

#### 4. `src/pages/frontend/tag-article-list.vue`

**模板（第 55 行）：**
```
// 改前
@click="goArticleDetailPage(article.id)"
// 改后
@click="goArticleDetailPage(article.slug)"
```

**函数（第 372 行）：**
```js
// 同上
```

#### 5. `src/layouts/frontend/components/Header.vue`（搜索结果跳转）

**模板（第 87 行）：**
```
// 改前
@click="jumpToArticleDetailPage(article.id)"
// 改后
@click="jumpToArticleDetailPage(article.slug)"
```

**函数（第 235 行）：**
```js
// 改前
const jumpToArticleDetailPage = (id) => {
    closeSearch()
    router.push('/article/' + id)
}
// 改后
const jumpToArticleDetailPage = (slug) => {
    closeSearch()
    router.push('/article/' + slug + '.html')
}
```

#### 6. `src/pages/admin/article-list.vue`（管理后台预览按钮）

**模板（第 60 行）：**
```
// 改前
@click="goArticleDetailPage(scope.row.id)"
// 改后
@click="goArticleDetailPage(scope.row.slug)"
```

**函数（第 568 行）：**
```js
// 同上，id → slug，路径加 .html
```

---

## 部署前必做

1. **数据库执行 DDL**（生产环境手动执行，init.sql 仅供参考）：
   ```sql
   ALTER TABLE t_article ADD COLUMN slug VARCHAR(200) NOT NULL DEFAULT '' COMMENT '文章 slug' AFTER title;
   CREATE UNIQUE INDEX uk_slug ON t_article(slug);
   ```

2. **填入百度翻译 API 真实凭证**：`application-dev.yml` 和 `application-prod.yml` 中的 `baidu.translate.app-id` 和 `secret-key`

3. **首次启动后**，`MigrateArticleSlugRunner` 会自动为所有 `slug=''` 的存量文章生成 slug（有 1.1 秒/篇的延迟，避免百度 API 限流）。迁移完成后该 Runner 不会重复执行（因为不再有 `slug=''` 的文章）。

4. **重建 Lucene 索引**：删除 `lucene-index/article/` 目录下的旧索引文件，重启后端让 `InitLuceneIndexRunner` 重新创建包含 slug 字段的索引。

---

## 验证清单

- [ ] 首页文章卡片点击 → URL 为 `/article/xxx.html`
- [ ] 文章详情页上下篇导航正常跳转
- [ ] 归档页文章链接正常
- [ ] 分类文章列表页文章链接正常
- [ ] 标签文章列表页文章链接正常
- [ ] Header 搜索结果点击跳转正常
- [ ] 管理后台文章列表"预览"按钮正常
- [ ] 新发布文章自动生成 slug
- [ ] 修改文章标题后 slug 自动更新
