<template>
    <div ref="lineChartRef" class="w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'

const props = defineProps({
    value: {
        type: Object,
        default: null
    }
})

const lineChartRef = ref(null)
let myChart = null

function initLineChat() {
    if (!lineChartRef.value) return

    if (!myChart) {
        myChart = echarts.init(lineChartRef.value)
    }

    const pvDates = props.value?.pvDates || []
    const pvCounts = props.value?.pvCounts || []

    const option = {
        grid: { top: 20, right: 20, bottom: 30, left: 40 },
        xAxis: {
            type: 'category',
            data: pvDates
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: pvCounts,
                type: 'line'
            }
        ]
    }

    myChart.setOption(option)
}

const handleResize = () => myChart && myChart.resize()

watch(() => props.value, () => nextTick(initLineChat), { deep: true })

onMounted(() => {
    nextTick(initLineChat)
    window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    if (myChart) {
        myChart.dispose()
        myChart = null
    }
})
</script>
