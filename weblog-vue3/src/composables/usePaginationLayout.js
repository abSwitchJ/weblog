import { computed } from 'vue'
import { useMediaQuery } from '@vueuse/core'

export function usePaginationLayout() {
    const isSmallScreen = useMediaQuery('(max-width: 640px)')

    const layout = computed(() =>
        isSmallScreen.value
            ? 'prev, pager, next'
            : 'total, sizes, prev, pager, next, jumper'
    )
    const small = computed(() => isSmallScreen.value)

    return { layout, small }
}
