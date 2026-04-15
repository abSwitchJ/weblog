<template>
  <!-- 与 PV 组件完全相同的容器样式：overflow-x-auto w-full h-60 -->
  <div id="calendar" class="overflow-x-auto w-full h-60"></div>
</template>

<script setup>
import * as echarts from 'echarts'
import { watch } from 'vue'
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

// 初始化日历热点图
function initCalendar() {
  const chartDom = document.getElementById('calendar')
  if (!chartDom) return

  // 参照 PV 组件：固定宽度初始化（但日历需要更宽，否则格子挤压，滚动条拉不到底）
  const myChart = echarts.init(chartDom, null, { width: 550 })

  // 转换数据
  const map = props.value
  const myData = Object.entries(map).map(([date, count]) => [date, count])

  const option = {
    visualMap: {
      show: false,
      min: 0,
      max: 10
    },
    calendar: {
      range: [startDate, endDate],
      cellSize: [16, 16] // 固定格子大小，让图表宽度 = 天数 × 16
    },
    series: {
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: myData
    },
    gradientColor: ['#fff', '#40c463', '#30a14e', '#216e39']
  }

  myChart.setOption(option)
}

// 监听数据变化（与 PV 组件一致，无 immediate，但 watch 会在数据首次赋值后触发）
watch(() => props.value, () => initCalendar())
</script>