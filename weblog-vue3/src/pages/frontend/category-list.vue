<template>
    <Header></Header>

    <div class="np-archive-page" :class="{ 'dark': isDark }">
        <div class="np-archive-container">

            <div v-for="category in categories" :key="category.id"
                 :id="'cat-' + category.id" class="np-cat-row">

                <div class="np-cat-name-wrap">
                    <h2 class="np-cat-name" @click="scrollToCat(category.id)">{{ category.name }}</h2>
                    <span class="np-cat-badge">{{ category.articlesTotal }}</span>
                </div>

                <div class="np-cat-cards">
                    <div v-for="(article, idx) in getArticles(category.id)" :key="idx"
                         class="np-index-card"
                         @click="goArticleDetailPage(article.slug)">
                        <h3 class="np-index-card-title">{{ article.title }}</h3>
                        <p class="np-index-card-line2">
                            <span class="np-index-card-date">【{{ article.createDate }}】</span>
                            <span v-if="article.summary" class="np-index-card-summary">{{ article.summary }}</span>
                        </p>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <ScrollToTopButton></ScrollToTopButton>
    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue'
import { getCategoryList, getCategoryArticlePageList } from '@/api/frontend/category'
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDark } from '@vueuse/core'
import { useLocaleStore } from '@/stores/locale'

const router = useRouter()
const isDark = useDark()
const localeStore = useLocaleStore()

const categories = ref([])
const articlesMap = reactive({})

function loadCategories() {
    getCategoryList({}).then((res) => {
        if (!res.success) return
        categories.value = res.data
        categories.value.forEach((cat) => {
            if (!cat.articlesTotal || cat.articlesTotal <= 0) {
                articlesMap[cat.id] = []
                return
            }
            getCategoryArticlePageList({
                current: 1,
                size: cat.articlesTotal,
                name: cat.name,
            }).then((r) => {
                if (r.success) articlesMap[cat.id] = r.data || []
            })
        })
    })
}
loadCategories()

// 语言切换时重新加载
watch(() => localeStore.locale, loadCategories)

function getArticles(id) {
    return articlesMap[id] || []
}

function scrollToCat(id) {
    document.getElementById('cat-' + id)?.scrollIntoView({ behavior: 'instant' })
}

const goArticleDetailPage = (slug) => {
    router.push('/article/' + slug + '.html')
}
</script>

<style scoped>
.np-archive-page {
    background-color: #f7f7f4;
    padding: 30px 20px 40px;
    min-height: 60vh;
}

.np-archive-page.dark {
    background-color: #111;
}

.np-archive-container {
    max-width: 600px;
    margin: 0 auto;
}

/* 每个分类：并排布局 */
.np-cat-row {
    display: flex;
    align-items: flex-start;
    padding-top: 28px;
    scroll-margin-top: 80px;
}

.np-cat-row:first-child {
    padding-top: 0;
}

/* 分类名 + 角标 */
.np-cat-name-wrap {
    position: relative;
    flex-shrink: 0;
    padding-right: 18px;
}

.np-cat-name {
    margin: 0;
    color: #1a1a1a;
    font-size: 1.5em;
    font-weight: 700;
    line-height: 1.2;
    cursor: pointer;
    letter-spacing: 0.5px;
    transition: color 0.15s;
}

.np-cat-name:hover {
    color: #555;
}

.np-archive-page.dark .np-cat-name {
    color: #e8e8e8;
}

.np-archive-page.dark .np-cat-name:hover {
    color: #fff;
}

/* 右上角角标 */
.np-cat-badge {
    position: absolute;
    top: -6px;
    left: 100%;
    margin-left: -2px;
    background: #1a1a1a;
    color: #fff;
    font-size: 0.62em;
    font-weight: 600;
    line-height: 1.2;
    padding: 2px 7px;
    border-radius: 999px;
    min-width: 18px;
    text-align: center;
    white-space: nowrap;
}

.np-archive-page.dark .np-cat-badge {
    background: #333;
    color: #e8e8e8;
}

/* 卡片列 */
.np-cat-cards {
    flex: 1;
    max-width: 500px;
    margin-left: 24px;
}

/* 文章卡片 */
.np-index-card {
    background: #fff;
    padding: 20px 26px;
    margin-bottom: 14px;
    border-radius: 8px;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
    cursor: pointer;
    transition: box-shadow 0.2s ease;
}

.np-index-card:first-child {
    margin-top: 0;
}

.np-index-card:hover {
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.12);
}

.np-archive-page.dark .np-index-card {
    background: #1e1e1e;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.3);
}

.np-archive-page.dark .np-index-card:hover {
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.5);
}

.np-index-card-title {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
    color: #1a1a1a;
    font-size: 1.2em;
    font-weight: 700;
    line-height: 1.35;
    margin: 0 0 6px;
    letter-spacing: 0.5px;
}

.np-archive-page.dark .np-index-card-title {
    color: #e8e8e8;
}

.np-index-card-line2 {
    margin: 0;
    padding: 0;
    text-indent: 0;
    font-size: 0.92em;
    line-height: 1.7;
}

.np-index-card-date {
    color: #999;
    font-size: 0.88em;
    letter-spacing: 0.5px;
    margin-left: -0.35em;
}

.np-archive-page.dark .np-index-card-date {
    color: #777;
}

.np-index-card-summary {
    color: #333;
}

.np-archive-page.dark .np-index-card-summary {
    color: #9e9e9e;
}

/* 响应式 */
@media (max-width: 640px) {
    .np-cat-row {
        flex-direction: column;
    }

    .np-cat-cards {
        margin-left: 0;
        margin-top: 12px;
        max-width: 100%;
    }

    .np-archive-page {
        padding: 16px 10px 30px;
    }

    .np-cat-name {
        font-size: 1.3em;
    }
}
</style>
