import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLocaleStore = defineStore('locale', () => {
  const browserLang = navigator.language?.startsWith('en') ? 'en' : 'zh'
  const locale = ref(browserLang)

  function setLocale(lang) {
    locale.value = lang
  }

  function toggleLocale() {
    locale.value = locale.value === 'zh' ? 'en' : 'zh'
  }

  return { locale, setLocale, toggleLocale }
}, {
  persist: true
})
