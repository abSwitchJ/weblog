<template>
    <Header></Header>

    <!-- 报纸页面背景 -->
    <div class="np-page" :class="{ 'dark': isDark }">
        <div class="np-container">

            <!-- 加载指示器：数据未到达前显示 -->
            <div v-if="loading" class="np-loading">
                <span class="np-spinner"></span>
            </div>

            <template v-else-if="article && article.title">
            <!-- 报纸报头区域 -->
            <header class="np-masthead">

                <!-- 文章大标题 -->
                <h1 class="np-title">{{ article.title }}</h1>

                <!-- 发布信息行 -->
                <div class="np-byline">
                    <span class="np-byline-left">
                        <span>{{ article.createDate }}</span>
                        <span class="np-sep">&nbsp;·&nbsp;</span>
                        <a class="np-cat-link" @click="goCategoryArticleListPage(article.categoryId, article.categoryName)">
                            {{ article.categoryName }}
                        </a>
                    </span>
                    <span class="np-byline-right">
                        <span>{{ article.totalWords }} {{ t('article.words') }}</span>
                        <span class="np-sep">&nbsp;·&nbsp;</span>
                        <span>{{ article.readTime }}</span>
                    </span>
                </div>

                <!-- 双线分隔 -->
                <div class="np-hrule-double"></div>


            </header>

            <!-- 主内容区：有目录时右侧浮动，无目录时正文占满 -->
            <div class="np-main" :class="{ 'no-toc': !hasToc }">

                <!-- 右侧：文章目录（可折叠，float:right，需放在正文前以便绕流） -->
                <aside v-if="hasToc" class="np-sidebar">
                    <div class="np-toc-card">
                        <div class="np-toc-header" @click="tocCollapsed = !tocCollapsed">
                            <span>{{ t('article.toc') }}</span>
                            <svg class="np-toc-arrow" :class="{ 'collapsed': tocCollapsed }"
                                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
                            </svg>
                        </div>
                        <div v-show="!tocCollapsed" class="np-toc-body">
                            <Toc :titles="article.toc"></Toc>
                        </div>
                    </div>
                </aside>

                <!-- 左侧：文章正文（自然绕流于浮动侧栏） -->
                <main class="np-article">
                    <div :class="{ 'dark': isDark }">
                        <div ref="articleContentRef" class="article-content" v-viewer v-html="article.content"></div>
                    </div>
                </main>
                <div class="np-prevnext">
                    <!-- 上下篇导航 -->
                    <div class="basis-1/2">
                        <a v-if="article.preArticle"
                            @click="router.push('/article/' + article.preArticle.articleSlug + '.html')"
                            class="cursor-pointer flex flex-col h-full p-4 mr-3 text-base font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:border-sky-500 hover:bg-gray-100 hover:text-gray-700 dark:bg-[#1e1e1e] dark:border-gray-700 dark:text-gray-400 dark:hover:bg-[#1a1a1a] dark:hover:text-white">
                            <div>
                                <svg class="inline w-3.5 h-3.5 mr-2 mb-1" aria-hidden="true"
                                    xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                        stroke-width="2" d="M13 5H1m0 0 4 4M1 5l4-4"></path>
                                </svg>
                                {{ t('article.prevArticle') }}
                            </div>
                            <div>{{ article.preArticle.articleTitle }}</div>
                        </a>
                    </div>

                    <div class="basis-1/2">
                        <a v-if="article.nextArticle"
                            @click="router.push('/article/' + article.nextArticle.articleSlug + '.html')"
                            class="cursor-pointer flex flex-col h-full text-right p-4 text-base font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:border-sky-500 hover:bg-gray-100 hover:text-gray-700 dark:bg-[#1e1e1e] dark:border-gray-700 dark:text-gray-400 dark:hover:bg-[#1a1a1a] dark:hover:text-white">
                            <div>
                                {{ t('article.nextArticle') }}
                                <svg class="inline w-3.5 h-3.5 ml-2 mb-1" aria-hidden="true"
                                    xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                        stroke-width="2" d="M1 5h12m0 0L9 1m4 4L9 9"></path>
                                </svg>
                            </div>
                            <div>{{ article.nextArticle.articleTitle }}</div>
                        </a>
                    </div>
                </div>
            </div>
            </template>

        </div>

        <div v-if="!loading && article && article.title" class="giscus-container" :class="{ 'dark': isDark }">
            <!-- 评论区（报纸组件外） -->
            <div class="np-comments">
                <Giscus
                    v-bind="config"
                    mapping="pathname"
                    strict="0"
                    reactions-enabled="1"
                    emit-metadata="1"
                    input-position="top"
                    :theme="giscusTheme"
                    :lang="localeStore.locale === 'en' ? 'en' : 'zh-CN'"
                    loading="lazy"
                    crossorigin="anonymous"
                />
            </div>
        </div>
    </div>


    <!-- 返回顶部 -->
    <ScrollToTopButton></ScrollToTopButton>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue'
import Toc from '@/layouts/frontend/components/Toc.vue'
import { getArticleDetail } from '@/api/frontend/article'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ref, computed, watch, onBeforeUnmount, nextTick, reactive } from 'vue'
import hljs from 'highlight.js'
import 'highlight.js/styles/tokyo-night-dark.css'

import { useDark } from '@vueuse/core'
import Giscus from '@giscus/vue';
import { useLocaleStore } from '@/stores/locale'
// @ts-ignore
const config = __GISCUS_CONFIG__;
// 是否是暗黑模式
const isDark = useDark()
const { t } = useI18n()
const localeStore = useLocaleStore()

const route = useRoute()
const router = useRouter()
// 路由传递过来的文章 slug
console.log(route.params.slug)

// 侧边栏目录折叠状态
const tocCollapsed = ref(false)

// Giscus 主题：开发环境用内置 light（http 无法加载自定义 CSS），生产环境用自定义 CSS
const giscusTheme = import.meta.env.DEV
  ? 'light'
  : `${window.location.origin}/css/comment.css`

// 文章数据
const article = ref({})

// 加载状态
const loading = ref(true)

// 是否存在目录
const hasToc = computed(() => Array.isArray(article.value?.toc) && article.value.toc.length > 0)

// 获取文章详情
function refreshArticleDetail(slug) {
    loading.value = true
    getArticleDetail(slug).then((res) => {
        // 该文章不存在(错误码为 20010)
        if (!res.success && res.errorCode == '20010') {
            // 手动跳转 404 页面
            loading.value = false
            router.push({ name: 'NotFound' })
            return
        }

        article.value = res.data
        if (res.data && res.data.title) {
            document.title = res.data.title
        }
        loading.value = false

        nextTick(() => {
            // 获取所有 pre code 节点
            let highlight = document.querySelectorAll('pre code')
            // 循环高亮
            highlight.forEach((block) => {
                hljs.highlightElement(block)
            })

            // 获取所有的 pre 节点
            let preElements = document.querySelectorAll('pre')
            preElements.forEach(preElement => {
                // 找到第一个 code 元素
                let firstCode = preElement.querySelector('code');
                if (firstCode) {
                    let copyCodeBtn = '<button class="hidden copy-code-btn flex items-center justify-center"><div class="copy-icon"></div></button>'
                    firstCode.insertAdjacentHTML('beforebegin', copyCodeBtn);

                    // 获取刚插入的按钮
                    let copyBtn = firstCode.previousSibling;
                    copyBtn.addEventListener('click', () => {
                        // 添加 copied 样式
                        copyBtn.classList.add('copied');
                        copyToClipboard(preElement.textContent)
                        // 1.5 秒后移除 copied 样式
                        setTimeout(() => {
                            copyBtn.classList.remove('copied');
                        }, 1500);
                    });
                }

                // 添加事件监听器
                preElement.addEventListener('mouseenter', handleMouseEnter);
                preElement.addEventListener('mouseleave', handleMouseLeave);
            })
        })
    }).catch(() => {
        loading.value = false
    })
}
refreshArticleDetail(route.params.slug)

// 跳转分类文章列表页
const goCategoryArticleListPage = (id, name) => {
    router.push('/category/' + encodeURIComponent(name))
}

// 跳转标签文章列表页
const goTagArticleListPage = (id, name) => {
    // 跳转时通过 query 携带参数（标签 ID、标签名称）
    router.push({ path: '/tag/article/list', query: { id, name } })
}

// 监听路由
watch(route, (newRoute, oldRoute) => {
    // 重新渲染文章详情
    refreshArticleDetail(newRoute.params.slug)
})

// 语言切换时重新加载
watch(() => localeStore.locale, () => refreshArticleDetail(route.params.slug))

// 复制内容到剪切板
function copyToClipboard(text) {
    const textarea = document.createElement('textarea');
    textarea.value = text;
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
}

const handleMouseEnter = (event) => {
    // 鼠标移入，显示按钮
    let copyBtn = event.target.querySelector('button');
    if (copyBtn) {
        copyBtn.classList.remove('hidden');
        copyBtn.classList.add('block');
    }
}

const handleMouseLeave = (event) => {
    // 鼠标移出，隐藏按钮
    let copyBtn = event.target.querySelector('button');
    if (copyBtn) {
        copyBtn.classList.add('hidden');
    }
}
</script>

<style scoped>
/* ===========================
   报纸页面级布局样式
   =========================== */

.np-page {
    background-color: #f7f7f4;
    padding: 30px 20px 10px;
    min-height: 60vh;
}

.np-page.dark {
    background-color: #111;
}

.np-container {
    max-width: 1000px;
    margin: 0 auto 30px;
    background: #fff;
    padding: 30px 60px 30px;
    box-shadow: 0 0 25px rgba(0, 0, 0, 0.12);
}
.giscus-container {
    max-width: 1000px;
    margin: 0 auto 30px;
    background-color: #fff;
    padding: 14px 20px 10px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}
.giscus-container.dark {
    background-color: #111;
}
.comment-welcome {
    text-align: center;
    font-size: 22px;
    font-weight: 600;
    color: #222;
    margin: 0 0 20px;
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", serif;
    letter-spacing: 0.5px;
}
.prevnext-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px 40px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.12);
}

.np-page.dark .np-container {
    background: #1e1e1e;
    box-shadow: 0 0 25px rgba(0, 0, 0, 0.5);
}

/* 报头区域 */
.np-masthead {
    margin-bottom: 10px;
}

/* 标签栏目标注 */
.np-section-label {
    text-align: center;
    margin-bottom: 14px;
    font-size: 0.82em;
    letter-spacing: 2.5px;
    text-transform: uppercase;
    color: #888;
}

.np-page.dark .np-section-label {
    color: #777;
}

.np-tag {
    cursor: pointer;
    margin: 0 8px;
    color: #666;
    transition: color 0.2s;
}

.np-tag:hover {
    color: #1a1a1a;
    text-decoration: underline;
}

.np-page.dark .np-tag {
    color: #888;
}

.np-page.dark .np-tag:hover {
    color: #ddd;
}

/* 文章大标题 */
.np-title {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
    font-size: 2.6em;
    font-weight: 700;
    text-align: center;
    line-height: 1.25;
    letter-spacing: 1px;
    color: #1a1a1a;
    margin: 0 0 18px;
}

.np-page.dark .np-title {
    color: #e8e8e8;
}

/* 双线分隔 */
.np-hrule-double {
    border: none;
    border-top: 3px double #1a1a1a;
    margin: 10px 0;
}

.np-page.dark .np-hrule-double {
    border-top-color: #555;
}

/* 发布信息行（左右分栏） */
.np-byline {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.88em;
    color: #777;
    margin-bottom: 10px;
    font-style: italic;
}

.np-page.dark .np-byline {
    color: #aaa;
}

.np-byline-left,
.np-byline-right {
    display: inline-flex;
    align-items: center;
}

.np-sep {
    margin: 0 2px;
    color: #bbb;
}

.np-cat-link {
    cursor: pointer;
    color: #555;
    transition: color 0.2s;
}

.np-cat-link:hover {
    color: #1a1a1a;
    text-decoration: underline;
}

.np-page.dark .np-cat-link {
    color: #aaa;
}

.np-page.dark .np-cat-link:hover {
    color: #eee;
}

/* 主内容布局：block + float 绕流（sidebar 浮右，正文绕流） */
.np-main {
    display: block;
    padding-top: 10px;
}

/* clearfix：撑开父容器高度，sticky 的包含块才有足够垂直范围 */
.np-main::after {
    content: "";
    display: table;
    clear: both;
}

/* 浮动侧栏：目录卡片 */
.np-sidebar {
    float: right;
    width: 300px;
    margin-left: 40px;
    margin-bottom: 20px;
}

.np-page.dark .np-main {
    border-top-color: #3a3a3a;
}


/* 加载指示器 */
.np-loading {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 320px;
}

.np-spinner {
    width: 36px;
    height: 36px;
    border: 3px solid rgba(26, 26, 26, 0.15);
    border-top-color: #1a1a1a;
    border-radius: 50%;
    animation: np-spin 0.8s linear infinite;
}

.np-page.dark .np-spinner {
    border-color: rgba(232, 232, 232, 0.2);
    border-top-color: #e8e8e8;
}

@keyframes np-spin {
    to { transform: rotate(360deg); }
}

/* 锚点跳转时避开固定 Header */
.article-content :is(h1, h2, h3, h4, h5, h6) {
    scroll-margin-top: 5.5rem;
}

/* 上下篇导航（清除浮动，始终从新行开始） */
.np-prevnext {
    clear: both;
    display: flex;
    flex-direction: row;
    max-width: 1000px;
    margin: 0;
    padding: 0;
}

/* 评论区（报纸组件外，独立居中区块） */
.np-comments {
    max-width: 1000px;
    margin: 32px auto 0;
    padding: 0 20px 40px;
}

/* 侧边栏：可折叠目录卡片 */
.np-toc-card {
    background-color: #faf9f7;
    border: 1px solid #ccc;
    border-radius: 0px;
    position: sticky;
    top: 5.5rem;
}

.np-page.dark .np-toc-card {
    background-color: #2a2a2a;
    border-color: #444;
}

.np-toc-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    font-weight: 700;
    font-size: 0.9em;
    letter-spacing: 0.5px;
    text-transform: uppercase;
    color: #716d6d;
    cursor: pointer;
    border-bottom: 0px solid #fff;
    user-select: none;
}

.np-toc-header:hover {
    background-color: #f0ede8;
}

.np-page.dark .np-toc-header {
    color: #faf9f7;
    border-bottom-color: #555;
}

.np-page.dark .np-toc-header:hover {
    background-color: #333;
}

.np-toc-arrow {
    width: 12px;
    height: 16px;
    transition: transform 0.25s ease;
    color: #666;
    flex-shrink: 0;
}

.np-toc-arrow.collapsed {
    transform: rotate(-90deg);
}

.np-page.dark .np-toc-arrow {
    color: #aaa;
}

.np-toc-body {
    padding: 4px 0;
}

/* 去掉 Toc 自身的卡片外壳，由 np-toc-card 统一管理 */
::v-deep(.np-sidebar .sticky) {
    position: static;
    background: transparent;
    border: none;
    box-shadow: none;
    border-radius: 0;
    padding: 0;
    margin: 0;
}

.np-page.dark ::v-deep(.np-sidebar .sticky) {
    background: transparent;
    border: none;
}

/* 响应式 */
@media (max-width: 768px) {
    .np-sidebar {
        float: none;
        width: 100%;
        margin: 0 0 20px;
    }

    .np-container {
        padding: 24px 20px;
    }

    .np-title {
        font-size: 1.8em;
    }

    ::v-deep(.article-content h1) {
        font-size: 1.8em;
    }
}

/* ===========================
   文章内容样式（报纸排版）
   =========================== */

/* h1, h2, h3, h4, h5, h6 基础字族 */
::v-deep(.article-content h1,
.article-content h2,
.article-content h3,
.article-content h4,
.article-content h5,
.article-content h6) {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", serif;
    line-height: 1.4;
    color: #1a1a1a;
}

/* h1 与报头标题同字族，左对齐，紧随双线之下 */
::v-deep(.article-content h1) {
    font-size: 2.6em;
    font-weight: 700;
    text-align: left;
    line-height: 1.25;
    letter-spacing: 1px;
    color: #1a1a1a;
    margin: 10px 0 14px;
}

.np-page.dark ::v-deep(.article-content h1) {
    color: #e8e8e8;
}

/* h2 报纸栏目标题风格（flow-root 让其成为 BFC，不让 border-bottom 延伸到浮动 TOC 下方） */
::v-deep(.article-content h2) {
    display: flow-root;
    font-size: 1.35em;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-top: 10px;
    margin-bottom: 14px;
    padding-bottom: 8px;
    border-bottom: 2px solid #1a1a1a;
    color: #1a1a1a;
}

::v-deep(.dark .article-content h2) {
    color: #e0e0e0;
    border-bottom: 2px solid #555;
}

/* h3 副标题 */
::v-deep(.article-content h3) {
    font-size: 1.15em;
    font-weight: 700;
    margin-top: 10px;
    margin-bottom: 12px;
    color: #1a1a1a;
}

::v-deep(.dark .article-content h3) {
    color: #ddd;
}

::v-deep(.article-content h4) {
    font-size: 1.05em;
    margin-top: 10px;
    margin-bottom: 10px;
    font-weight: 600;
    color: #222;
}

::v-deep(.dark .article-content h4) {
    color: rgb(226 232 240 / 1);
}

::v-deep(.article-content h5) {
    font-size: 1em;
    margin-top: 10px;
    margin-bottom: 8px;
    font-weight: 600;
    color: #222;
}

::v-deep(.dark .article-content h5) {
    color: rgb(226 232 240 / 1);
}

::v-deep(.article-content h6) {
    font-size: 1em;
    margin-top: 10px;
    margin-bottom: 8px;
    font-weight: 600;
    color: #444;
}

::v-deep(.dark .article-content h6) {
    color: rgb(226 232 240 / 1);
}

/* 段落：两端对齐，衬线字体 */
::v-deep(.article-content p) {
    text-align: justify;
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", serif;
    font-size: 1em;
    line-height: 1.9;
    letter-spacing: 0.2px;
    color: #333;
    margin: 0 0 18px;
    word-break: normal;
    word-wrap: break-word;
}

::v-deep(.dark .article-content p) {
    color: #9e9e9e;
}

/* blockquote 报纸引语风格 */
::v-deep(.article-content blockquote) {
    background: #f8f7f4;
    border-left: 4px solid #1a1a1a;
    padding: 16px 20px;
    font-style: italic;
    color: #555;
    font-size: 1em;
    margin-bottom: 20px;
    quotes: none;
}

::v-deep(.dark .article-content blockquote) {
    background-color: rgb(31 41 55 / 1);
    border-left: 4px solid #555;
    color: #888;
    margin-bottom: 20px;
    padding: 16px 20px;
}

/* 设置 blockquote 中最后一个 p 标签的 margin-bottom 为 0 */
::v-deep(.article-content blockquote p:last-child) {
    margin-bottom: 0;
}

/* 斜体样式 */
::v-deep(.article-content em) {
    color: #c849ff;
}

/* 超链接样式：中性灰文字 + 灰色虚线底边，hover 变灰色实线 */
::v-deep(.article-content a) {
    color: inherit;
    text-decoration: none;
    border-bottom: 1px dotted #bbb;
    padding-bottom: 1px;
    transition: border-bottom-color 0.15s ease, border-bottom-style 0.15s ease;
}

::v-deep(.article-content a:hover) {
    border-bottom: 1px solid #666;
}

::v-deep(.dark .article-content a) {
    border-bottom-color: #555;
}

::v-deep(.dark .article-content a:hover) {
    border-bottom: 1px solid #999;
}

/* ul 样式 */
::v-deep(.article-content ul) {
    padding-left: 2rem;
}

::v-deep(.dark .article-content ul) {
    padding-left: 2rem;
    color: #9e9e9e;
}

::v-deep(.article-content > ul) {
    margin-bottom: 20px;
}

::v-deep(.article-content ul li) {
    list-style-type: disc;
    padding-top: 5px;
    padding-bottom: 5px;
    font-size: 1em;
    font-family: Georgia, 'Times New Roman', "Songti SC", serif;
}

::v-deep(.article-content ul li p) {
    margin-bottom: 0 !important;
}

::v-deep(.article-content ul ul li) {
    list-style-type: square;
}

/* ol 样式 */
::v-deep(.article-content ol) {
    list-style-type: decimal;
    padding-left: 2rem;
}

::v-deep(.dark .article-content ol) {
    color: #9e9e9e;
}

/* 图片样式 */
::v-deep(.article-content img) {
    max-width: 100%;
    overflow: hidden;
    display: block;
    margin: 0 auto;
    border-radius: 4px;
}

::v-deep(.article-content img:hover,
img:focus) {
    box-shadow: 2px 2px 10px 0 rgba(0, 0, 0, .15);
}

/* 图片描述文字 */
::v-deep(.image-caption) {
    min-width: 20%;
    max-width: 80%;
    min-height: 43px;
    display: block;
    padding: 10px;
    margin: 0 auto;
    font-size: 13px;
    color: #999;
    text-align: center;
}

/* code 样式 */
::v-deep(.article-content code:not(pre code)) {
    padding: 2px 4px;
    margin: 0 2px;
    font-size: 95% !important;
    border-radius: 4px;
    color: rgb(41, 128, 185);
    background-color: rgba(27, 31, 35, 0.05);
    font-family: Operator Mono, Consolas, Monaco, Menlo, monospace;
}

::v-deep(.dark .article-content code:not(pre code)) {
    padding: 2px 4px;
    margin: 0 2px;
    font-size: .85em;
    border-radius: 5px;
    color: #abb2bf;
    background: #333;
    font-family: Operator Mono, Consolas, Monaco, Menlo, monospace;
}

::v-deep(code) {
    font-size: 98%;
}

/* pre 样式 */
::v-deep(pre) {
    margin-bottom: 20px;
    padding-top: 30px;
    background: #21252b;
    border-radius: 6px;
    position: relative;
}

::v-deep(pre code.hljs) {
    padding: 0.7rem 1rem;
    border-bottom-left-radius: 6px;
    border-bottom-right-radius: 6px;
}

::v-deep(pre:before) {
    background: #fc625d;
    border-radius: 50%;
    box-shadow: 20px 0 #fdbc40, 40px 0 #35cd4b;
    content: ' ';
    height: 10px;
    margin-top: -19px;
    margin-left: 10px;
    position: absolute;
    width: 10px;
}

/* 表格样式 */
::v-deep(table) {
    margin-bottom: 20px;
    width: 100%;
}

::v-deep(table tr) {
    background-color: #fff;
    border-top: 1px solid #c6cbd1;
}

::v-deep(table th) {
    padding: 6px 13px;
    border: 1px solid #dfe2e5;
}

::v-deep(table td) {
    padding: 6px 13px;
    border: 1px solid #dfe2e5;
}

::v-deep(table tr:nth-child(2n)) {
    background-color: #f6f8fa;
}

::v-deep(.dark table tr) {
    background-color: rgb(31 41 55 / 1);
}

::v-deep(.dark table) {
    color: #9e9e9e;
}

::v-deep(.dark table th) {
    border: 1px solid #394048;
}

::v-deep(.dark table td) {
    border: 1px solid #394048;
}

::v-deep(.dark table tr:nth-child(2n)) {
    background-color: rgb(21 41 55 / 1);
}

/* hr 横线 */
::v-deep(hr) {
    margin-bottom: 20px;
}

::v-deep(.dark hr) {
    --tw-border-opacity: 1;
    border-color: rgb(55 65 81 / var(--tw-border-opacity));
}

/* 复制代码按钮 */
::v-deep(.copy-code-btn) {
    border-width: 0;
    cursor: pointer;
    position: absolute;
    top: 0.5em;
    right: 0.5em;
    z-index: 5;
    width: 2.5rem;
    height: 2.5rem;
    padding: 0;
    border-radius: 0.5rem;
    opacity: 0;
    transition: opacity .4s;
    opacity: 1;
}

::v-deep(.copy-code-btn:hover) {
    background: #2f3542;
}

::v-deep(.copy-icon) {
    --copy-icon: url("data:image/svg+xml;utf8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' height='20' width='20' stroke='rgba(128,128,128,1)' stroke-width='2'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' d='M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2'/%3E%3C/svg%3E");
    background: currentcolor;
    -webkit-mask-image: var(--copy-icon);
    mask-image: var(--copy-icon);
    -webkit-mask-position: 50%;
    mask-position: 50%;
    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;
    -webkit-mask-size: 1em;
    mask-size: 1em;
    width: 1.25rem;
    height: 1.25rem;
    padding: 0.625rem;
    color: #9e9e9e;
    font-size: 1.25rem;
}

::v-deep(.copied) {
    display: flex;
    background: #2f3542;
}

::v-deep(.copied:after) {
    content: "已复制";
    position: absolute;
    top: 0;
    right: calc(100% + .25rem);
    display: block;
    height: 2.5rem;
    padding: .625rem;
    border-radius: .5rem;
    background: #2f3542;
    color: #9e9e9e;
    font-weight: 500;
    line-height: 1.25rem;
    white-space: nowrap;
    font-size: 14px;
    font-family: -apple-system, BlinkMacSystemFont, PingFang SC, Hiragino Sans GB, Microsoft Yahei, Arial, sans-serif;
}

::v-deep(.copied .copy-icon) {
    --copied-icon: url("data:image/svg+xml;utf8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' height='20' width='20' stroke='rgba(128,128,128,1)' stroke-width='2'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' d='M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2m-6 9 2 2 4-4'/%3E%3C/svg%3E");
    -webkit-mask-image: var(--copied-icon);
    mask-image: var(--copied-icon);
}
</style>