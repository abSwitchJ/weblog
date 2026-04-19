<template>
    <header :class="['fixed top-0 left-0 right-0 z-10 transition-transform duration-300', headerVisible ? 'translate-y-0' : '-translate-y-full']">
        <nav class="bg-white dark:bg-[#111] border-b border-gray-200 dark:border-gray-700">
            <div class="max-w-[1030px] mx-auto px-4 py-4 flex items-center justify-between">
                <!-- 左侧导航 -->
                <div class="flex items-center gap-2 md:gap-5 text-sm nav-font">
                    <a @click="router.push('/')" class="cursor-pointer font-semibold text-[#1a1a1a] dark:text-white hover:opacity-70 text-lg md:mr-[-12px]">abSwitchJ</a>

                    <!-- 白天/黑夜切换 -->
                    <button @click="toggleDark()" class="flex items-center justify-center w-5 h-5 text-[#707070] dark:text-gray-300 hover:opacity-70" aria-label="切换深色模式">
                        <!-- 太阳图标（亮色模式显示） -->
                        <svg v-if="!isDark" class="w-[18px] h-[18px]" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                            <path d="M512.000213 733.353497c-122.06857 0-221.353283-99.284713-221.353283-221.353284S389.931643 290.64693 512.000213 290.64693 733.353497 389.931643 733.353497 512.000213 634.026117 733.353497 512.000213 733.353497z m0-357.373767A136.148482 136.148482 0 0 0 375.97973 512.000213 136.148482 136.148482 0 0 0 512.000213 648.020697 136.148482 136.148482 0 0 0 648.020697 512.000213 136.148482 136.148482 0 0 0 512.000213 375.97973zM554.666613 171.735673A42.154403 42.154403 0 0 1 512.000213 213.335413c-23.551853 0-42.6664-18.645217-42.6664-41.59974V41.603153A42.154403 42.154403 0 0 1 512.000213 0.003413c23.551853 0 42.6664 18.645217 42.6664 41.59974v130.13252zM554.666613 982.397273A42.154403 42.154403 0 0 1 512.000213 1023.997013c-23.594519 0-42.6664-18.687883-42.6664-41.59974v-130.175186A42.111737 42.111737 0 0 1 512.000213 810.665013c23.551853 0 42.6664 18.60255 42.6664 41.59974v130.13252zM171.735673 469.333813c22.954523 0 41.59974 19.114547 41.59974 42.6664 0 23.594519-18.645217 42.6664-41.59974 42.6664H41.603153A42.154403 42.154403 0 0 1 0.003413 512.000213c0-23.551853 18.645217-42.6664 41.59974-42.6664h130.13252zM982.397273 469.333813c22.954523 0 41.59974 19.114547 41.59974 42.6664 0 23.594519-18.687883 42.6664-41.59974 42.6664h-130.175186A42.111737 42.111737 0 0 1 810.665013 512.000213c0-23.551853 18.60255-42.6664 41.59974-42.6664h130.13252zM241.239239 722.430898a42.06907 42.06907 0 0 1 59.562294 0.767995 42.111737 42.111737 0 0 1 0.767996 59.562295l-92.031425 92.074091a42.154403 42.154403 0 0 1-59.562295-0.853328 42.154403 42.154403 0 0 1-0.767995-59.562294l92.031425-91.988759zM814.462323 149.207814a42.154403 42.154403 0 0 1 59.562294 0.767995 42.154403 42.154403 0 0 1 0.767996 59.562295l-92.031425 92.031425a42.06907 42.06907 0 0 1-59.562295-0.767996 42.111737 42.111737 0 0 1-0.810661-59.562294l92.074091-92.031425zM241.239239 301.526862a42.19707 42.19707 0 0 0 59.604961-0.725329 42.111737 42.111737 0 0 0 0.767995-59.562294L209.538104 149.122481a42.154403 42.154403 0 0 0-59.562295 0.853328 42.111737 42.111737 0 0 0-0.767995 59.562295l92.031425 91.988758zM814.462323 874.792613a42.111737 42.111737 0 0 0 59.562294-0.810662 42.154403 42.154403 0 0 0 0.767996-59.562294l-92.031425-92.031425a42.06907 42.06907 0 0 0-59.562295 0.767995 42.111737 42.111737 0 0 0-0.810661 59.562294l92.074091 92.074092z" fill="currentColor"></path>
                        </svg>
                        <!-- 月亮图标（暗色模式显示） -->
                        <svg v-else class="w-[18px] h-[18px]" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                            <path d="M576.389562 1024A522.303665 522.303665 0 0 1 238.759308 103.797568 513.161417 513.161417 0 0 1 425.472143 2.177965a52.251464 52.251464 0 0 1 55.556739 17.229621 52.251464 52.251464 0 0 1 4.782099 57.736813 466.465627 466.465627 0 0 0 4.219499 462.246128 470.122526 470.122526 0 0 0 425.114535 235.0261 52.251464 52.251464 0 0 1 48.80554 30.310069c9.142248 18.987746 5.977624 41.702716-8.087373 57.455513a520.264241 520.264241 0 0 1-379.47362 161.747466zM440.66234 51.827404c-61.885987 18.636121-119.20085 49.93074-168.217364 91.914756a470.122526 470.122526 0 1 0 644.528489 681.660082 522.303665 522.303665 0 0 1-471.880651-261.186995A518.646766 518.646766 0 0 1 440.52169 51.897729z" fill="currentColor"></path>
                        </svg>
                    </button>

                    <div class="hidden md:contents">
                        <a @click="router.push('/archive')" class="cursor-pointer text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white">Archive</a>
                        <a href="https://github.com/abSwitchJ" target="_blank" class="text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white">GitHub</a>
                        <a href="https://x.com/AbswitchJ" target="_blank" class="text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white">Twitter</a>
                        <a @click="router.push('/about')" class="cursor-pointer text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white">About</a>
                    </div>
                </div>

                <!-- 右侧功能 -->
                <div class="flex items-center gap-2 md:gap-4">
                    <!-- 搜索图标 -->
                    <button @click="openSearch" class="text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white" aria-label="搜索">
                        <svg class="w-[18px] h-[18px]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z" />
                        </svg>
                    </button>

                    <!-- 中英文切换 -->
                    <button @click="toggleLang" class="text-sm text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white font-medium nav-font">
                        {{ localeStore.locale === 'en' ? '中' : 'En' }}
                    </button>

                    <!-- 汉堡菜单（仅移动端） -->
                    <button @click="menuOpen = !menuOpen" class="md:hidden flex items-center justify-center text-[#707070] dark:text-gray-300 hover:text-[#1a1a1a] dark:hover:text-white" :aria-label="menuOpen ? '关闭菜单' : '打开菜单'" :aria-expanded="menuOpen">
                        <svg v-if="!menuOpen" class="w-[18px] h-[18px]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2 5h16M2 10h16M2 15h16" />
                        </svg>
                        <svg v-else class="w-[18px] h-[18px]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3l14 14M17 3L3 17" />
                        </svg>
                    </button>
                </div>
            </div>

            <!-- 移动端下拉菜单 -->
            <Transition name="mobile-menu">
                <div v-show="menuOpen" class="mobile-menu-wrapper md:hidden border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-[#111]">
                    <a @click="goAndCloseMenu('/archive')" class="mobile-menu-item block px-4 py-3 text-sm text-[#707070] dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-[#1a1a1a] hover:text-[#1a1a1a] dark:hover:text-white cursor-pointer nav-font">Archive</a>
                    <a href="https://github.com/abSwitchJ" target="_blank" @click="menuOpen = false" class="mobile-menu-item block px-4 py-3 text-sm text-[#707070] dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-[#1a1a1a] hover:text-[#1a1a1a] dark:hover:text-white nav-font">GitHub</a>
                    <a href="https://x.com/AbswitchJ" target="_blank" @click="menuOpen = false" class="mobile-menu-item block px-4 py-3 text-sm text-[#707070] dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-[#1a1a1a] hover:text-[#1a1a1a] dark:hover:text-white nav-font">Twitter</a>
                    <a @click="goAndCloseMenu('/about')" class="mobile-menu-item block px-4 py-3 text-sm text-[#707070] dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-[#1a1a1a] hover:text-[#1a1a1a] dark:hover:text-white cursor-pointer nav-font">About</a>
                </div>
            </Transition>
        </nav>
    </header>

    <!-- 占位元素，防止 fixed 导航栏遮挡内容 -->
    <div class="h-[57px]"></div>

    <!-- 搜索弹窗 -->
    <Teleport to="body">
        <div v-if="showSearchModal" class="search-overlay" @click.self="closeSearch">
            <div class="search-dialog" :class="{ 'dark': isDark }">
                <!-- 搜索输入区 -->
                <div class="search-input-row">
                    <div class="search-input-icon">
                        <svg v-if="searchLoading" class="search-spinner" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="#e5e7eb"/>
                            <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="#0ea5e9"/>
                        </svg>
                        <svg v-else class="search-icon-mag" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z" />
                        </svg>
                    </div>
                    <input ref="searchInputRef" v-model="searchWord" type="text"
                        class="search-input" :placeholder="t('search.placeholder')" @keydown.esc="closeSearch">
                    <button @click="closeSearch" class="search-close-btn" aria-label="关闭搜索">
                        <svg class="w-3.5 h-3.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                        </svg>
                    </button>
                </div>

                <div class="search-divider"></div>

                <!-- 提示行 -->
                <p v-if="!searchWord" class="search-hint">{{ t('search.hint') }}</p>

                <!-- 结果区域 -->
                <div v-if="searchWord" class="search-results">
                    <div v-if="searchArticles && searchArticles.length > 0">
                        <p class="search-result-count">{{ t('search.resultsFound', { n: total }) }}</p>

                        <div v-for="(article, index) in searchArticles" :key="index"
                            class="search-card" :class="{ 'search-card-border': index < searchArticles.length - 1 }"
                            @click="jumpToArticleDetailPage(article.slug)">
                            <h3 class="search-card-title" v-html="article.title"></h3>
                            <p class="search-card-line2">
                                <span class="search-card-date">【{{ article.createDate }}】</span>
                                <span v-if="article.summary" class="search-card-summary" v-html="article.summary"></span>
                            </p>
                            <p v-if="article.content" class="search-card-content" v-html="article.content"></p>
                        </div>

                        <!-- 翻页 -->
                        <div v-if="pages > 1" class="search-pagination">
                            <a v-if="current > 1" @click="prePage" class="search-page-btn">{{ t('pagination.prev') }}</a>
                            <span v-else></span>
                            <a v-if="current < pages" @click="nextPage" class="search-page-btn">{{ t('pagination.next') }}</a>
                        </div>
                    </div>

                    <div v-else-if="!searchLoading" class="search-empty">
                        <p>{{ t('search.noResults') }}</p>
                    </div>
                </div>

                <div class="search-divider"></div>

                <!-- 底部快捷键提示 -->
                <div class="search-footer">
                    <span class="search-kbd">/</span>
                    <span class="search-kbd-label">{{ t('search.toSearch') }}</span>
                    <span class="search-kbd">Esc</span>
                    <span class="search-kbd-label">{{ t('search.toClose') }}</span>
                </div>
            </div>
        </div>
    </Teleport>
</template>

<script setup>
import { onMounted, ref, onBeforeUnmount, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getArticleSearchPageList } from '@/api/frontend/search'
import { useDark, useToggle } from '@vueuse/core'
import { useLocaleStore } from '@/stores/locale'

const router = useRouter()
const { t } = useI18n()
const isDark = useDark()
const toggleDark = useToggle(isDark)
const localeStore = useLocaleStore()

// 搜索弹窗状态
const showSearchModal = ref(false)
const searchInputRef = ref(null)

// 移动端汉堡菜单状态
const menuOpen = ref(false)
const goAndCloseMenu = (path) => {
    menuOpen.value = false
    router.push(path)
}
watch(() => router.currentRoute.value.fullPath, () => {
    menuOpen.value = false
})

// 导航栏滚动隐藏/显示
const headerVisible = ref(true)
let lastScrollY = 0

const handleScroll = () => {
    const currentScrollY = window.scrollY
    if (currentScrollY > lastScrollY && currentScrollY > 60) {
        headerVisible.value = false
    } else {
        headerVisible.value = true
    }
    lastScrollY = currentScrollY
}

const openSearch = () => {
    showSearchModal.value = true
    nextTick(() => {
        searchInputRef.value?.focus()
    })
}

const closeSearch = () => {
    showSearchModal.value = false
}

const handleKeyDown = (event) => {
    if (event.ctrlKey && event.key === 'k') {
        event.preventDefault()
        openSearch()
        return
    }
    if (event.key === '/' && !showSearchModal.value) {
        const tag = document.activeElement?.tagName
        if (tag === 'INPUT' || tag === 'TEXTAREA' || document.activeElement?.isContentEditable) return
        event.preventDefault()
        openSearch()
        return
    }
    if (event.key === 'Escape') {
        if (showSearchModal.value) closeSearch()
        if (menuOpen.value) menuOpen.value = false
    }
}

onMounted(() => {
    window.addEventListener('keydown', handleKeyDown)
    window.addEventListener('scroll', handleScroll, { passive: true })
})

onBeforeUnmount(() => {
    window.removeEventListener('keydown', handleKeyDown)
    window.removeEventListener('scroll', handleScroll)
})

// 中英文切换
const toggleLang = () => {
    localeStore.toggleLocale()
}

// 文章搜索
const searchArticles = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)
const pages = ref(0)
const searchWord = ref('')
const searchLoading = ref(false)

watch(searchWord, (newText, oldText) => {
    if (newText && newText !== oldText) {
        current.value = 1
        renderSearchArticles({ current: 1, size: size.value, word: newText })
    } else if (newText == '') {
        searchArticles.value = []
    }
})

// 语言切换时重新搜索（弹窗打开且有搜索词时）
watch(() => localeStore.locale, () => {
    if (showSearchModal.value && searchWord.value) {
        renderSearchArticles({ current: current.value, size: size.value, word: searchWord.value })
    }
})

function renderSearchArticles(data) {
    searchLoading.value = true
    getArticleSearchPageList(data).then(res => {
        if (res.success) {
            searchArticles.value = res.data
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    }).finally(() => searchLoading.value = false)
}

const nextPage = () => {
    renderSearchArticles({ current: current.value + 1, size: size.value, word: searchWord.value })
}

const prePage = () => {
    renderSearchArticles({ current: current.value - 1, size: size.value, word: searchWord.value })
}

const jumpToArticleDetailPage = (slug) => {
    closeSearch()
    router.push('/article/' + slug + '.html')
}
</script>

<style scoped>
.nav-font {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
}

/* ========== 移动端汉堡下拉菜单 ========== */
.mobile-menu-item {
    transition: background-color 0.15s, color 0.15s;
}

.mobile-menu-item + .mobile-menu-item {
    border-top: 1px solid #f0f0f0;
}

.dark .mobile-menu-item + .mobile-menu-item {
    border-top-color: #222;
}

/* Transition: 菜单滑入/滑出 */
.mobile-menu-enter-active,
.mobile-menu-leave-active {
    transition: max-height 0.22s ease, opacity 0.22s ease;
    overflow: hidden;
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
    max-height: 0;
    opacity: 0;
}

.mobile-menu-enter-to,
.mobile-menu-leave-from {
    max-height: 320px;
    opacity: 1;
}

/* ========== 搜索弹窗 ========== */
.search-overlay {
    position: fixed;
    inset: 0;
    z-index: 50;
    display: flex;
    align-items: flex-start;
    justify-content: center;
    padding-top: 12vh;
    background: rgba(0, 0, 0, 0.45);
}

.search-dialog {
    width: 100%;
    max-width: 640px;
    margin: 0 16px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
    overflow: hidden;
}

.search-dialog.dark {
    background: #1e1e1e;
    box-shadow: 0 8px 40px rgba(0, 0, 0, 0.5);
}

/* 输入区 */
.search-input-row {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    gap: 12px;
}

.search-input-icon {
    flex-shrink: 0;
    color: #999;
    display: flex;
    align-items: center;
}

.search-dialog.dark .search-input-icon {
    color: #666;
}

.search-icon-mag {
    width: 18px;
    height: 18px;
}

.search-spinner {
    width: 18px;
    height: 18px;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.search-input {
    flex: 1;
    border: none;
    outline: none;
    background: transparent;
    font-size: 15px;
    color: #1a1a1a;
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
}

.search-input::placeholder {
    color: #bbb;
}

.search-dialog.dark .search-input {
    color: #e8e8e8;
}

.search-dialog.dark .search-input::placeholder {
    color: #555;
}

.search-close-btn {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border-radius: 6px;
    color: #999;
    transition: background 0.15s, color 0.15s;
}

.search-close-btn:hover {
    background: #f0f0f0;
    color: #333;
}

.search-dialog.dark .search-close-btn {
    color: #666;
}

.search-dialog.dark .search-close-btn:hover {
    background: #333;
    color: #ccc;
}

/* 分割线 */
.search-divider {
    height: 1px;
    background: #eee;
}

.search-dialog.dark .search-divider {
    background: #333;
}

/* 提示行 */
.search-hint {
    padding: 24px 16px;
    color: #bbb;
    font-size: 14px;
    text-align: center;
}

.search-dialog.dark .search-hint {
    color: #555;
}

/* 结果区域 */
.search-results {
    max-height: 55vh;
    overflow-y: auto;
    padding: 12px 16px;
}

.search-result-count {
    font-size: 13px;
    color: #999;
    margin-bottom: 10px;
}

.search-dialog.dark .search-result-count {
    color: #666;
}

/* 搜索结果卡片 */
.search-card {
    background: #fff;
    padding: 16px 20px;
    cursor: pointer;
    transition: background-color 0.15s ease;
}

.search-card:hover {
    background: #f5f5f5;
}

.search-card-border {
    border-bottom: 1px solid #eee;
}

.search-dialog.dark .search-card {
    background: #1e1e1e;
}

.search-dialog.dark .search-card:hover {
    background: #2a2a2a;
}

.search-dialog.dark .search-card-border {
    border-bottom-color: #333;
}

.search-card-title {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
    color: #1a1a1a;
    font-size: 1.3em;
    font-weight: 700;
    line-height: 1.35;
    margin: 0 0 6px;
}

.search-dialog.dark .search-card-title {
    color: #e8e8e8;
}

.search-card-line2 {
    margin: 0;
    font-size: 0.9em;
    line-height: 1.65;
}

.search-card-date {
    color: #999;
    font-size: 0.88em;
    letter-spacing: 0.5px;
}

.search-dialog.dark .search-card-date {
    color: #777;
}

.search-card-summary {
    color: #555;
}

.search-dialog.dark .search-card-summary {
    color: #9e9e9e;
}

/* 正文片段 */
.search-card-content {
    margin: 6px 0 0;
    font-size: 0.85em;
    line-height: 1.6;
    color: #777;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.search-dialog.dark .search-card-content {
    color: #666;
}

/* 空结果 */
.search-empty {
    padding: 30px 0;
    text-align: center;
    color: #999;
    font-size: 14px;
}

.search-dialog.dark .search-empty {
    color: #666;
}

/* 翻页 */
.search-pagination {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}

.search-page-btn {
    display: inline-flex;
    align-items: center;
    background: #1a1a1a;
    color: #fff;
    font-size: 0.82em;
    padding: 6px 16px;
    border-radius: 999px;
    cursor: pointer;
    transition: background-color 0.2s;
    user-select: none;
}

.search-page-btn:hover {
    background: #333;
}

.search-dialog.dark .search-page-btn {
    background: #444;
}

.search-dialog.dark .search-page-btn:hover {
    background: #555;
}

/* 底部快捷键提示 */
.search-footer {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 16px;
    font-size: 12px;
    color: #bbb;
}

.search-dialog.dark .search-footer {
    color: #555;
}

.search-kbd {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 22px;
    height: 20px;
    padding: 0 5px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 11px;
    color: #999;
    background: #fafafa;
}

.search-dialog.dark .search-kbd {
    border-color: #444;
    color: #666;
    background: #2a2a2a;
}

.search-kbd-label {
    margin-right: 10px;
}

/* 响应式 */
@media (max-width: 640px) {
    .search-overlay {
        padding-top: 5vh;
    }

    .search-dialog {
        margin: 0 8px;
    }

    .search-results {
        max-height: 60vh;
    }

    .search-card {
        padding: 12px 14px;
    }

    .search-card-title {
        font-size: 1.05em;
    }
}
</style>
