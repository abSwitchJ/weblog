<template>
    <div v-if="titles && titles.length > 0" class="toc-wrapper" :class="[isDark ? 'dark' : '']">
        <ul class="toc-list">
            <li v-for="(item, idx) in titles" :key="item.id"
                :class="['toc-item', { 'toc-item-active': item.id === activeId, 'toc-item-last': idx === titles.length - 1 }]">
                <span class="toc-link" @click="scrollToAnchor(item.id)">{{ item.text }}</span>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { useDark } from '@vueuse/core'

const isDark = useDark()

const props = defineProps({
    titles: {
        type: Array,
        default: () => []
    }
})

const activeId = ref('')

function scrollToAnchor(id) {
    const el = document.getElementById(id)
    if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
}

function handleScroll() {
    if (!props.titles || props.titles.length === 0) return
    // 以距顶部 100px 为激活阈值
    const threshold = 120
    let current = ''
    for (const item of props.titles) {
        const el = document.getElementById(item.id)
        if (!el) continue
        const rect = el.getBoundingClientRect()
        if (rect.top <= threshold) {
            current = item.id
        } else {
            break
        }
    }
    activeId.value = current
}

onMounted(() => {
    window.addEventListener('scroll', handleScroll, { passive: true })
    nextTick(handleScroll)
})

onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleScroll)
})

watch(() => props.titles, () => {
    nextTick(handleScroll)
}, { deep: true })
</script>

<style scoped>
.toc-wrapper {
    width: 100%;
    font-size: 0.9em;
    line-height: 1.5;
}

.toc-list {
    list-style: none;
    margin: 0;
    padding: 0;
}

.toc-item {
    border-bottom: 1px solid #e5e5e5;
}

.toc-item-last {
    border-bottom: none;
}

.toc-wrapper.dark .toc-item {
    border-bottom-color: #3a3a3a;
}

.toc-wrapper.dark .toc-item-last {
    border-bottom: none;
}

.toc-link {
    display: block;
    padding: 10px 16px;
    color: #555;
    cursor: pointer;
    transition: color 0.2s, background-color 0.2s;
    word-break: break-word;
    white-space: normal;
}

.toc-link:hover {
    color: #1a1a1a;
    background-color: #f0ede8;
}

.toc-wrapper.dark .toc-link {
    color: #aaa;
}

.toc-wrapper.dark .toc-link:hover {
    color: #fff;
    background-color: #333;
}

.toc-item-active .toc-link {
    color: #1a1a1a;
    font-weight: 600;
    border-left: 2px solid #1a1a1a;
    padding-left: 14px;
}

.toc-wrapper.dark .toc-item-active .toc-link {
    color: #fff;
    border-left-color: #fff;
}
</style>
