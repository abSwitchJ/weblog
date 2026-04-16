<template>
    <Header></Header>

    <!-- 主内容区域 -->
    <div class="np-index-page" :class="{ 'dark': isDark }">
        <div class="np-index-container">

            <!-- 文章卡片列表 -->
            <div v-for="(article, index) in articles" :key="index"
                 class="np-index-card"
                 @click="goArticleDetailPage(article.slug)">

                <!-- 文章标题 -->
                <h2 class="np-index-card-title">{{ article.title }}</h2>

                <!-- 日期 + 摘要同行 -->
                <p class="np-index-card-line2">
                    <span class="np-index-card-date">【{{ article.createDate }}】</span>
                    <span v-if="article.summary" class="np-index-card-summary">{{ article.summary }}</span>
                </p>

            </div>

            <!-- 分页 -->
            <nav v-if="pages > 1" class="np-index-pagination">
                <a @click="getArticles(current - 1)"
                   class="np-index-page-btn"
                   :class="{ 'hidden': current <= 1 }">
                    上一页
                </a>
                <a @click="getArticles(current + 1)"
                   class="np-index-page-btn"
                   :class="{ 'hidden': current >= pages }">
                    下一页
                </a>
            </nav>

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
import { ref } from 'vue'
import { getArticlePageList } from '@/api/frontend/article'
import { useRouter } from 'vue-router'
import { useDark } from '@vueuse/core'

const router = useRouter()
const isDark = useDark()

// 文章集合
const articles = ref([])
// 当前页码
const current = ref(1)
// 每页显示的文章数
const size = ref(10)
// 总文章数
const total = ref(0)
// 总共多少页
const pages = ref(0)

function getArticles(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    getArticlePageList({current: currentNo, size: size.value}).then((res) => {
        if (res.success) {
            articles.value = res.data
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    })
}
getArticles(current.value)

// 跳转文章详情页
const goArticleDetailPage = (slug) => {
    router.push('/article/' + slug + '.html')
}
</script>

<style scoped>
/* 页面背景 */
.np-index-page {
    background-color: #f7f7f4;
    padding: 30px 20px 40px;
    min-height: 60vh;
}

.np-index-page.dark {
    background-color: #111;
}

/* 居中容器 */
.np-index-container {
    max-width: 1000px;
    margin: 0 auto;
}

/* 文章卡片 */
.np-index-card {
    background: #fff;
    padding: 28px 36px;
    margin-bottom: 20px;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
    cursor: pointer;
    border-radius: 8px;

    transition: box-shadow 0.2s ease;
}

.np-index-card:hover {
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.12);
}

.np-index-page.dark .np-index-card {
    background: #1e1e1e;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.3);
}

.np-index-page.dark .np-index-card:hover {
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.5);
}

/* 卡片标题 */
.np-index-card-title {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
    color: #1a1a1a;
    font-size: 1.5em;
    font-weight: 700;
    line-height: 1.35;
    margin: 0 0 8px;
    letter-spacing: 0.5px;
}

.np-index-page.dark .np-index-card-title {
    color: #e8e8e8;
}

/* 第二行：日期 + 摘要 */
.np-index-card-line2 {
    margin: 0;
    padding: 0;
    text-indent: 0;
    font-size: 0.95em;
    line-height: 1.7;
}

/* 日期 */
.np-index-card-date {
    color: #999;
    font-size: 0.88em;
    letter-spacing: 0.5px;
    margin-left: -0.35em;
}

.np-index-page.dark .np-index-card-date {
    color: #777;
}

/* 摘要 */
.np-index-card-summary {
    color: #333;
}

.np-index-page.dark .np-index-card-summary {
    color: #9e9e9e;
}

/* 分页容器 */
.np-index-pagination {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}

/* 翻页胶囊按钮 */
.np-index-page-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background: #1a1a1a;
    color: #fff;
    font-size: 0.88em;
    padding: 8px 20px;
    border-radius: 999px;
    cursor: pointer;
    transition: background-color 0.2s;
    user-select: none;
}


.np-index-page-btn.hidden {
    visibility: hidden;      /* 隐藏内容但保留占位 */
    pointer-events: none;    /* 禁止点击 */
}

.np-index-page.dark .np-index-page-btn {
    background: #333;
}

.np-index-page.dark .np-index-page-btn:hover:not(.np-index-page-btn.hidden) {
    background: #444;
}

/* 按钮箭头图标 */
.np-btn-arrow {
    width: 10px;
    height: 10px;
}

/* 响应式 */
@media (max-width: 768px) {
    .np-index-page {
        padding: 16px 10px 30px;
    }

    .np-index-card {
        padding: 20px 20px;
    }

    .np-index-card-title {
        font-size: 1.25em;
    }
}
</style>
