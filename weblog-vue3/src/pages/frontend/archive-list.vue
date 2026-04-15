<template>
    <Header></Header>

    <div class="np-archive-page" :class="{ 'dark': isDark }">
        <div class="np-archive-container">

            <div v-for="yearGroup in archives" :key="yearGroup.year" class="np-archive-year-group">
                <h2 class="np-archive-year" :id="'year-' + yearGroup.year" @click="scrollTo('year-' + yearGroup.year)">
                    {{ yearGroup.year }}
                </h2>

                <div v-for="(monthArticles, month) in groupByMonth(yearGroup.articles)" :key="month"
                     class="np-archive-month-group">
                    <h3 class="np-archive-month" :id="'month-' + yearGroup.year + '-' + month" @click="scrollTo('month-' + yearGroup.year + '-' + month)">
                        {{ month }}
                    </h3>

                    <div v-for="(dayArticles, day) in groupByDay(monthArticles)" :key="day"
                         class="np-archive-day-group">
                        <span class="np-archive-day">{{ day }}</span>
                        <div class="np-archive-titles">
                            <div v-for="article in dayArticles" :key="article.id" class="np-archive-item">
                                <a class="np-archive-title" @click="goArticleDetailPage(article.id)">{{ article.title }}</a>
                            </div>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDark } from '@vueuse/core'

const router = useRouter()
const isDark = useDark()

const archives = ref([])

getArchiveList().then((res) => {
    if (res.success) {
        archives.value = res.data
    }
})

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

// 按天分组，返回按天倒序的对象
function groupByDay(articles) {
    const map = {}
    for (const article of articles) {
        const day = article.createDate.substring(8, 10)
        if (!map[day]) map[day] = []
        map[day].push(article)
    }
    const sorted = {}
    Object.keys(map).sort((a, b) => b.localeCompare(a)).forEach(k => sorted[k] = map[k])
    return sorted
}

const goArticleDetailPage = (id) => {
    router.push('/article/' + id)
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

/* 日：固定宽度，每行左对齐 */
.np-archive-day {
    min-width: 1em;
    flex-shrink: 0;
    color: #999;
    font-size: 0.95em;
}

.np-archive-page.dark .np-archive-day {
    color: #666;
}

/* 标题容器：tab 间距 */
.np-archive-titles {
    padding-left: 2em;
}

/* 同一天多篇文章各占一行，标题左对齐 */
.np-archive-item {
    line-height: 2;
}

.np-archive-title {
    color: #1a1a1a;
    font-size: 1em;
    cursor: pointer;
    text-decoration: none;
    transition: color 0.15s;
    padding-left: 0em;  /* 调大/调小 */
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