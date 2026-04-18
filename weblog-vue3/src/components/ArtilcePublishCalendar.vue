<template>
  <div ref="calendarRef" class="w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { format, subMonths } from 'date-fns'

const props = defineProps({
  value: {
    type: Object,
    default: () => ({})
  }
})

const currentDate = new Date()
const sixMonthsAgo = subMonths(currentDate, 6)
const startDate = format(sixMonthsAgo, 'yyyy-MM-dd')
const endDate = format(currentDate, 'yyyy-MM-dd')

const calendarRef = ref(null)
let myChart = null

function initCalendar() {
  if (!calendarRef.value) return

  if (!myChart) {
    myChart = echarts.init(calendarRef.value)
  }

  const containerWidth = calendarRef.value.clientWidth
  const containerHeight = calendarRef.value.clientHeight
  const availWidth = containerWidth - (60 + 10)
  const availHeight = containerHeight - (30 + 20)
  const weeks = 27
  const daysPerWeek = 7
  const sizeByWidth = Math.floor(availWidth / weeks)
  const sizeByHeight = Math.floor(availHeight / daysPerWeek)
  const squareSize = Math.max(8, Math.min(sizeByWidth, sizeByHeight))

  const map = props.value || {}
  const myData = Object.entries(map)
    .filter(([, count]) => count > 0)
    .map(([date, count]) => [date, count])

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (p) => `${p.data[0]}：${p.data[1]} 篇`
    },
    visualMap: { show: false, min: 1, max: 10 },
    calendar: {
      top: 30,
      left: 60,
      right: 10,
      bottom: 20,
      range: [startDate, endDate],
      cellSize: squareSize,
      itemStyle: {
        color: 'transparent',
        borderColor: '#e5e7eb',
        borderWidth: 0.5
      },
      splitLine: {
        lineStyle: { color: '#9ca3af', width: 1 }
      },
      yearLabel: { show: true, margin: 30 },
      dayLabel: { nameMap: 'ZH' },
      monthLabel: { nameMap: 'ZH' }
    },
    series: {
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: myData,
      emphasis: {
        itemStyle: {
          shadowBlur: 4,
          shadowColor: 'rgba(0,0,0,0.2)'
        }
      }
    },
    gradientColor: ['#9be9a8', '#40c463', '#30a14e', '#216e39']
  }

  myChart.setOption(option)
}

const handleResize = () => {
  if (!myChart) return
  myChart.resize()
  initCalendar()
}

watch(() => props.value, () => nextTick(initCalendar), { deep: true })

onMounted(() => {
  nextTick(initCalendar)
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
