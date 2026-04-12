# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Layout

This is a monorepo with two sibling projects:
- `weblog-vue3/` — Vue 3 frontend (this directory)
- `weblog-springboot/` — Spring Boot 3 backend (sibling directory)

---

## Frontend (weblog-vue3)

### Commands

```bash
npm run dev       # Dev server at http://127.0.0.1:5173 (proxies /api → localhost:8080)
npm run build     # Production build to dist/
npm run preview   # Preview production build
```

No test runner is configured in this project.

### Architecture

**Entry**: `src/main.js` → mounts Vue app, registers plugins (Pinia, Router, Element Plus, etc.)

**Routing** (`src/router/index.js`): Hash-mode Vue Router v4. Two route trees:
- Public: `/`, `/archive/list`, `/category/list`, `/tag/list`, `/article/:id`, `/login`, etc.
- Admin: `/admin/*` — guarded by `src/permission.js` (requires JWT token in cookie)

**Global router guard** (`src/permission.js`): Shows NProgress, checks token for admin routes, and fetches blog settings on public route entry.

**HTTP layer** (`src/axios.js`): Axios instance with base URL `/api`. Injects `Authorization: Bearer {token}` header from cookie on every request. Handles 401 by clearing user state and reloading.

**State** (`src/stores/`): Pinia with `pinia-plugin-persistedstate`.
- `user.js` — userInfo, setUserInfo(), logout()
- `blogsettings.js` — blog config fetched once and cached
- `menu.js` — admin sidebar width state

**API layer** (`src/api/`): Split into `admin/` (authenticated) and `frontend/` (public). All calls use POST, even reads.

**Pages** (`src/pages/frontend/` and `src/pages/admin/`): File-per-page Vue SFCs. Admin pages use the shared layout at `src/layouts/admin/admin.vue`.

**Key dependencies**: Element Plus (UI), md-editor-v3 (Markdown editor/renderer), ECharts (dashboard charts), Giscus (article comments), Tailwind CSS, GSAP (animations), highlight.js, v-viewer (image lightbox).

### API Response Shape

```json
{ "success": true, "data": { ... }, "message": "" }
```

Errors surface via Element Plus `ElMessage` in `src/composables/util.js`.

---

## Backend (weblog-springboot)

### Commands

```bash
# From weblog-springboot/
mvn clean install                    # Build all modules
mvn spring-boot:run -pl weblog-web   # Run the application
mvn clean package                    # Build deployable JAR
```

Java 21 required. Maven mirrors are configured for Aliyun.

### Module Structure (dependency order)

1. **weblog-module-common** — Domain objects (DO), MyBatis Plus mappers, shared config, AOP (`@ApiOperationLog`), custom exceptions, utilities
2. **weblog-module-jwt** — Spring Security + JWT authentication (`JwtAuthenticationFilter`, bcrypt password encoding)
3. **weblog-module-search** — Lucene 8.11 full-text search with Chinese analyzer; indexes articles on startup via `InitLuceneIndexRunner`
4. **weblog-module-admin** — Admin REST controllers (`/admin/*` endpoints): articles, categories, tags, users, blog settings, file uploads (Minio), dashboard metrics
5. **weblog-web** — Application entry point; aggregates all modules; hosts public controllers (article list/detail, archive, category, tag, search, captcha, blog settings, statistics)

### Key Architectural Decisions

- **All endpoints use POST** (no REST verbs for data fetching)
- **URL prefix**: All backend endpoints are under `/api`; Vite proxy strips this in dev; production routes through the deployed server
- **Auth**: JWT bearer token, 24-hour expiry, issued by `POST /login`; admin endpoints secured via Spring Security `@PreAuthorize("hasRole('ROLE_ADMIN')")`
- **Markdown**: Stored as raw Markdown in `ArticleContentDO`; rendered to HTML server-side by CommonMark with extensions (tables, heading anchors, task lists, image attributes)
- **File storage**: Images uploaded via admin panel to Minio object storage
- **Captcha**: Tianai-Captcha behavior verification (slider/click) on the login page
- **API docs**: Knife4j available at `/doc.html` in development

### Data Model (weblog-module-common)

Core entities: `ArticleDO`, `ArticleContentDO`, `ArticleCategoryRelDO`, `ArticleTagRelDO`, `CategoryDO`, `TagDO`, `UserDO`, `BlogSettingsDO`

MapStruct converters (`*Convert.java`) handle DO ↔ VO mapping in the web and admin modules.
