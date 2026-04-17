# 前台中英文切换功能 — 交接文档

## 项目概述

为博客前台实现完整的中英文切换功能。切换英文时，前台所有内容（UI 文字 + 后端动态内容）都变为英文。

---

## 已完成的工作

### Phase 1：前端静态 UI 翻译 ✅

所有前台组件中的硬编码中文已替换为 `vue-i18n` 的 `$t()` 调用，生产构建验证通过。

**新建文件（4 个）：**

| 文件 | 说明 |
|------|------|
| `src/i18n/zh.js` | 中文 locale，约 30 个 key |
| `src/i18n/en.js` | 英文 locale，与 zh.js 结构对应 |
| `src/i18n/index.js` | createI18n 配置，legacy: false，fallbackLocale: 'zh' |
| `src/stores/locale.js` | Pinia store，浏览器语言检测 + 持久化 |

**修改文件（12 个）：**

| 文件 | 改动 |
|------|------|
| `src/main.js` | 添加 `app.use(i18n)` |
| `src/App.vue` | Element Plus locale 动态切换，watch 同步 i18n.locale |
| `Header.vue` | localeStore 替换 isEnglish ref，6+ 处中文 → `t()` |
| `UserInfoCard.vue` | 8 处中文 → `$t()` |
| `CategoryListCard.vue` | 分类 → `$t('category.title')` |
| `TagListCard.vue` | 标签 → `$t('tag.title')` |
| `Toc.vue` | 文章目录 → `$t('article.toc')` |
| `index.vue` | 上一页/下一页 → `t()` |
| `category-article-list.vue` | 空状态 + 分页 → `t()` |
| `tag-list.vue` | 标签 → `$t('tag.title')` |
| `tag-article-list.vue` | 标签 + 空状态 → `$t()` |
| `article-detail.vue` | 目录/上下篇/字数 → `t()`，Giscus lang 动态切换 |
| `404.vue` | 错误信息 + 按钮 → `$t()` |

---

## 未完成的工作

### Phase 2：后端预翻译基础设施（Task #3，未开始）

**核心思路**：文章发布/更新时，异步调用百度翻译 API 预翻译标题、摘要、正文，结果缓存到 `t_translation_cache` 表。前端切换英文时直接查询缓存，零等待。

**待创建：**

1. **数据库表** `t_translation_cache`

```sql
CREATE TABLE `t_translation_cache` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `source_hash` varchar(64) NOT NULL COMMENT 'SHA-256 of source text',
  `source_lang` varchar(10) NOT NULL DEFAULT 'zh',
  `target_lang` varchar(10) NOT NULL DEFAULT 'en',
  `source_text` mediumtext NOT NULL,
  `translated_text` mediumtext NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hash_lang` (`source_hash`, `source_lang`, `target_lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

2. **DO + Mapper**（weblog-module-common）
   - `TranslationCacheDO.java` — `@TableName("t_translation_cache")`
   - `TranslationCacheMapper.java` — `extends BaseMapper<TranslationCacheDO>`

3. **翻译工具改造** — `BaiduTranslateUtil.java`
   - 新增 `translate(String query, String from, String to, String appId, String secretKey)`
   - HTTP GET → POST（避免长文本 URL 限制）
   - 返回所有 `trans_result` 条目拼接
   - 保留原 `translateToEnglish` 向后兼容

4. **翻译服务**（weblog-web）
   - `TranslationService.java` 接口：translateAndCache / translateBatch / getTranslation
   - `TranslationImpl.java` 核心逻辑：
     - 计算 SHA-256 hash → 查缓存 → 命中返回 → 未命中调 API
     - 长文本按 `\n\n` 分段，跳过代码块，每段间隔 1s（1 QPS 限速）
     - 翻译 Markdown 源文本，翻译完用 `MarkdownHelper` 渲染 HTML
     - 存入缓存表

5. **查询接口**（weblog-web）
   - `TranslationController.java` — `POST /translation/batch`
   - `TranslateBatchReqVO.java` — texts[], from, to
   - `TranslateBatchRspVO.java` — translatedTexts[]

6. **发布时触发预翻译**
   - 修改 `AdminArticleImpl.java`，slug 生成后异步翻译标题/摘要/正文

7. **分类/标签触发**
   - 修改 `AdminCategoryServiceImpl.java`、`AdminTagServiceImpl.java`

8. **启动预翻译 Runner**
   - `PreTranslateRunner.java`，参考 `MigrateArticleSlugRunner.java` 模式

### Phase 3：前端动态内容语言切换（Task #4，未开始，依赖 Phase 2）

**待创建：**

1. `src/api/frontend/translation.js` — 调用 `/translation/batch` 接口
2. `src/composables/useTranslation.js` — 翻译 composable
   - `useTranslatedText(sourceRef)` → 返回 `{ translated }`
   - `useTranslatedList(listRef, fields)` → 返回翻译后的列表
   - locale='zh' 返回原文，locale='en' 查询后端缓存
   - 前端内存 Map 缓存（会话级，避免重复请求）
   - 缓存未命中 → fallback 显示原中文

**待集成页面：**

| 页面 | 翻译内容 |
|------|---------|
| `index.vue` | 文章标题 + 摘要（批量） |
| `article-detail.vue` | 标题 + 正文 HTML + 上/下篇标题 |
| `category-article-list.vue` | 分类名 + 文章标题/摘要 |
| `tag-article-list.vue` | 标签名 + 文章标题/摘要 |
| `tag-list.vue` | 标签名列表 |
| `category-list.vue` | 分类名 + 文章标题/摘要 |
| `archive-list.vue` | 文章标题 |
| `CategoryListCard.vue` | 侧边栏分类名 |
| `TagListCard.vue` | 侧边栏标签名 |
| Header.vue 搜索结果 | 搜索结果标题/摘要 |

---

## 关键设计决策

| 决策 | 选择 | 原因 |
|------|------|------|
| 翻译方式 | 预翻译（发布时翻译） | 切换语言零等待，用户体验最佳 |
| 翻译范围 | 标题 + 摘要 + 正文 + 分类名 + 标签名 | 用户明确要求正文也翻译 |
| 翻译服务位置 | weblog-web 模块 | 用户明确指定 |
| 默认语言 | 跟随浏览器语言 | 用户选择 |
| API 限制 | 百度翻译免费版 1 QPS，5 万字符/月 | 已有配置，复用 |

---

## 关键后端文件路径

- `weblog-module-common/.../utils/BaiduTranslateUtil.java` — 现有翻译工具类
- `weblog-module-admin/.../service/impl/AdminArticleImpl.java` — 文章发布/更新逻辑
- `weblog-web/.../markdown/MarkdownHelper.java` — Markdown → HTML 渲染
- `weblog-web/.../runner/MigrateArticleSlugRunner.java` — Runner 模式参考
- `weblog-web/.../WebSecurityConfig.java` — 已 `.anyRequest().permitAll()`，无需修改

## 缓存策略

| 层 | 方式 | 特点 |
|----|------|------|
| 后端 DB | `t_translation_cache`，SHA-256 查找 | 永久缓存，发布时写入，查询时必定命中 |
| 前端内存 | composable 中的 Map | 会话级，避免重复 HTTP 请求 |

## 错误处理

- 预翻译时百度 API 失败 → 日志告警，不影响发布，用户切英文时显示原中文
- 查询缓存未命中（极端情况）→ 返回原中文
- 免费额度耗尽 → 日志告警，新文章暂不翻译

## 验证方式

1. `npm run dev` 启动前端，点击 Header 中英文按钮，确认 UI 文字瞬间切换
2. 刷新页面，确认语言选择已持久化
3. 英文浏览器首次访问，确认默认英文
4. 后端启动后，发布一篇文章，检查 `t_translation_cache` 表确认自动写入翻译
5. 前台切换英文，确认文章标题/摘要/正文/分类名/标签名瞬间变为英文
6. 切回中文，确认立即恢复原文

## 详细计划文件

完整实现计划在：`C:\Users\--\.claude\plans\functional-crunching-wozniak.md`
