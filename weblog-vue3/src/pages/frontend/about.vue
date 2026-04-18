<template>
    <Header></Header>

    <div class="np-about-page" :class="{ 'dark': isDark }">
        <div class="np-about-card">
            <div class="np-about-content prose" v-html="aboutHtml"></div>
        </div>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import { ref, watch } from 'vue'
import { useDark } from '@vueuse/core'
import { getAboutDetail } from '@/api/frontend/about'
import { useLocaleStore } from '@/stores/locale'

const isDark = useDark()
const localeStore = useLocaleStore()

const aboutHtml = ref('')

function loadAbout() {
    getAboutDetail().then(res => {
        if (res.success) {
            aboutHtml.value = res.data?.aboutHtml || ''
        }
    })
}
loadAbout()

watch(() => localeStore.locale, loadAbout)
</script>

<style scoped>
.np-about-page {
    background-color: #f7f7f4;
    padding: 40px 20px 60px;
    min-height: 70vh;
}

.np-about-page.dark {
    background-color: #111;
}

.np-about-card {
    max-width: 900px;
    margin: 0 auto;
    background: #fff;
    padding: 40px 44px;
    border-radius: 12px;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
}

.np-about-page.dark .np-about-card {
    background: #1e1e1e;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.3);
}

.np-about-content {
    color: #333;
    font-size: 0.95em;
    line-height: 1.8;
    word-break: break-word;
}

.np-about-page.dark .np-about-content {
    color: #c9c9c9;
}

/* 标题（关于我 / 关注我）使用与首页卡片标题一致的衬线字体 */
.np-about-content :deep(h1),
.np-about-content :deep(h2),
.np-about-content :deep(h3) {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
    color: #1a1a1a;
    font-weight: 700;
    line-height: 1.35;
    margin: 28px 0 14px;
}

.np-about-page.dark .np-about-content :deep(h1),
.np-about-page.dark .np-about-content :deep(h2),
.np-about-page.dark .np-about-content :deep(h3) {
    color: #e8e8e8;
}

.np-about-content :deep(h1) { font-size: 1.7em; }
.np-about-content :deep(h2) { font-size: 1.4em; }
.np-about-content :deep(h3) { font-size: 1.15em; }

.np-about-content :deep(h1:first-child),
.np-about-content :deep(h2:first-child),
.np-about-content :deep(h3:first-child) {
    margin-top: 0;
}

.np-about-content :deep(p) {
    margin: 10px 0;
}

.np-about-content :deep(ul),
.np-about-content :deep(ol) {
    padding-left: 1.4em;
    margin: 10px 0;
}

.np-about-content :deep(li) {
    margin: 6px 0;
}

.np-about-content :deep(ul) {
    list-style: disc;
}

.np-about-content :deep(ol) {
    list-style: decimal;
}

.np-about-content :deep(a) {
    color: inherit;
    text-decoration: none;
    border-bottom: 1px dotted #bbb;
    padding-bottom: 1px;
    transition: border-bottom-color 0.15s ease, border-bottom-style 0.15s ease;
}

.np-about-content :deep(a:hover) {
    border-bottom: 1px solid #666;
}

.np-about-page.dark .np-about-content :deep(a) {
    border-bottom-color: #555;
}

.np-about-page.dark .np-about-content :deep(a:hover) {
    border-bottom: 1px solid #999;
}

.np-about-content :deep(img) {
    max-width: 200px;
    height: auto;
    display: block;
    margin: 8px 0;
    border-radius: 4px;
}

.np-about-content :deep(code) {
    background: #f3f3f3;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 0.9em;
}

.np-about-page.dark .np-about-content :deep(code) {
    background: #2a2a2a;
}

@media (max-width: 640px) {
    .np-about-card {
        padding: 28px 22px;
        margin: 0 auto;
    }
    .np-about-page {
        padding: 20px 12px 40px;
    }
}
</style>
