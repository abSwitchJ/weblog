
<template>
   <!-- 设置语言为中文 -->
   <el-config-provider :locale="locale">
      <router-view></router-view>
   </el-config-provider>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import en from 'element-plus/dist/locale/en.mjs'
import { useLocaleStore } from '@/stores/locale'

const localeStore = useLocaleStore()
const { locale: i18nLocale } = useI18n()

const locale = computed(() => localeStore.locale === 'en' ? en : zhCn)

watch(() => localeStore.locale, (val) => {
  i18nLocale.value = val
}, { immediate: true })
</script>

<style>
/* 自定义顶部加载 Loading 颜色 */
#nprogress .bar {
   background: #409eff!important;
}

/* 暗黑模式 body 背景色 */
.dark body {
    background-color: #111;
}
</style>
