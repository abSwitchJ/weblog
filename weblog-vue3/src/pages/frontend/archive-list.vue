<template>
    <Header></Header>

    <div class="np-archive-page" :class="{ 'dark': isDark }">
        <div class="np-archive-container">

            <div v-for="yearGroup in archives" :key="yearGroup.year" class="np-archive-year-group">
                <div class="np-archive-title-wrap">
                    <h1 class="np-archive-year" :id="'year-' + yearGroup.year" @click="scrollTo('year-' + yearGroup.year)">
                        {{ yearGroup.year }}
                    </h1>
                    <span class="np-archive-badge">{{ yearGroup.articles.length }}</span>
                </div>

                <div v-for="(monthArticles, month) in groupByMonth(yearGroup.articles)" :key="month"
                     class="np-archive-month-group">
                    <div class="np-archive-title-wrap">
                        <h2 class="np-archive-month" :id="'month-' + yearGroup.year + '-' + month" @click="scrollTo('month-' + yearGroup.year + '-' + month)">
                            {{ month }}
                        </h2>
                        <span class="np-archive-badge">{{ monthArticles.length }}</span>
                    </div>

                    <div v-for="dayGroup in groupByDay(monthArticles)" :key="dayGroup.day"
                         class="np-archive-day-group">
                        <div class="np-archive-title-wrap np-archive-day-wrap">
                            <h6 class="np-archive-day">{{ dayGroup.day }}</h6>
                            <span class="np-archive-badge np-archive-badge-sm">{{ dayGroup.articles.length }}</span>
                        </div>
                        <div class="np-archive-titles">
                            <p v-for="article in dayGroup.articles" :key="article.id"
                               class="np-archive-title"
                               @click="goArticleDetailPage(article.slug)">{{ article.title }}</p>
                        </div>
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
import { getArchiveList } from '@/api/frontend/archive'
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDark } from '@vueuse/core'
import { useLocaleStore } from '@/stores/locale'

const router = useRouter()
const isDark = useDark()
const localeStore = useLocaleStore()

const archives = ref([])

function loadArchives() {
    getArchiveList().then((res) => {
        if (res.success) {
            archives.value = res.data
        }
    })
}
loadArchives()

// 语言切换时重新加载
watch(() => localeStore.locale, loadArchives)

function groupByMonth(articles) {
    const map = {}
    for (const article of articles) {
        const month = article.createDate.substring(5, 7)
        if (!map[month]) map[month] = []
        map[month].push(article)
    }
    const sorted = {}
    Object.keys(map).sort((a, b) => b.localeCompare(a)).forEach(k => sorted[k] = map[k])
    return sorted
}

// 按天分组，返回按天倒序的数组（避免 JS 对象整数 key 升序迭代的坑）
function groupByDay(articles) {
    const map = {}
    for (const article of articles) {
        const day = article.createDate.substring(8, 10)
        if (!map[day]) map[day] = []
        map[day].push(article)
    }
    return Object.keys(map)
        .sort((a, b) => b.localeCompare(a))
        .map(day => ({ day, articles: map[day] }))
}


const goArticleDetailPage = (slug) => {
    router.push('/article/' + slug + '.html')
}

const scrollTo = (id) => {
    document.getElementById(id)?.scrollIntoView({ behavior: 'instant' })
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

/* 年份：左对齐，无缩进 */
.np-archive-year {
    color: #1a1a1a;
    font-size: 2em;
    font-weight: 700;
    margin: 0 0 8px;
    padding-top: 24px;
    letter-spacing: 1px;
    cursor: pointer;
}

.np-archive-year-group:first-child .np-archive-year {
    padding-top: 0;
}

.np-archive-page.dark .np-archive-year {
    color: #e8e8e8;
}

/* 月份：左对齐，无缩进 */
.np-archive-month {
    color: #333;
    font-size: 1.1em;
    font-weight: 600;
    margin: 14px 0 6px;
    cursor: pointer;
}

.np-archive-page.dark .np-archive-month {
    color: #bbb;
}

/* 日+标题行：flex 布局，日左边缩进对齐年份右边（约 4em） */
.np-archive-day-group {
    display: flex;
    align-items: flex-start;
    padding-left: 6em;
    line-height: 2;
}

/* 日所在的 wrap：flex 行中的固定宽度项 */
.np-archive-day-wrap {
    min-width: 1em;
    flex-shrink: 0;
}

/* 日：去除 h6 默认 margin/加粗，保持灰色小字 */
.np-archive-day {
    margin: 0;
    color: #999;
    font-size: 0.95em;
    font-weight: normal;
}

.np-archive-page.dark .np-archive-day {
    color: #666;
}

/* 标题容器：tab 间距 */
.np-archive-titles {
    padding-left: 2em;
}

.np-archive-title {
    margin: 0;
    color: #1a1a1a;
    font-size: 1em;
    line-height: 2;
    cursor: pointer;
    transition: color 0.15s;
}

.np-archive-title:hover {
    text-decoration: underline;
    color: #555;
}

.np-archive-page.dark .np-archive-title {
    color: #ccc;
}

.np-archive-page.dark .np-archive-title:hover {
    color: #fff;
}

/* 标题 + 角标包裹：撑到内容宽度，为 badge 提供定位上下文 */
.np-archive-title-wrap {
    position: relative;
    display: inline-block;
}

/* 右上角角标 */
.np-archive-badge {
    position: absolute;
    top: -4px;
    left: 100%;
    margin-left: -2px;
    background: #1a1a1a;
    color: #fff;
    font-size: 0.5em;
    font-weight: 600;
    line-height: 1.2;
    padding: 1px 5px;
    border-radius: 999px;
    min-width: 14px;
    text-align: center;
    white-space: nowrap;
}

.np-archive-page.dark .np-archive-badge {
    background: #333;
    color: #e8e8e8;
}

/* day 的角标更小 */
.np-archive-badge-sm {
    font-size: 0.6em;
    padding: 0 4px;
    min-width: 12px;
    top: -3px;
}

@media (max-width: 768px) {
    .np-archive-page {
        padding: 16px 10px 30px;
    }

    .np-archive-year {
        font-size: 1.6em;
    }

    .np-archive-month {
        font-size: 1em;
    }

    .np-archive-day-group {
        padding-left: 2.5em;
    }
}
</style>