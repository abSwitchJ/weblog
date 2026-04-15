<template>
    <!-- 日历热点图容器 -->
  <div id="calendar" class="overflow-x-auto w-full h-60" style="min-width: 1000px;"></div>
</template>

<script setup>
import * as echarts from 'echarts';
import { watch, onMounted, onUnmounted } from 'vue';
import { format, subMonths } from 'date-fns';

const props = defineProps({
  value: {
    type: Object,
    default: () => ({})
  }
});

let myChart = null;

const currentDate = new Date();
const sixMonthsAgo = subMonths(currentDate, 6);
const startDate = format(sixMonthsAgo, 'yyyy-MM-dd');
const endDate = format(currentDate, 'yyyy-MM-dd');

function renderChart() {
  const chartDom = document.getElementById('calendar');
  if (!chartDom) return;

  // 转换数据
  const map = props.value;
  const myData = Object.entries(map).map(([date, count]) => [date, count]);

  if (!myChart) {
    myChart = echarts.init(chartDom);
  }

  const option = {
    visualMap: {
      show: false,
      min: 0,
      max: 10
    },
    calendar: {
      range: [startDate, endDate],
    },
    series: {
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: myData
    },
    gradientColor: ['#fff', '#40c463', '#30a14e', '#216e39']
  };

  myChart.setOption(option);
}

watch(() => props.value, () => renderChart(), { immediate: true, deep: true });

// 监听窗口大小变化，自适应调整图表尺寸
const handleResize = () => myChart?.resize();
window.addEventListener('resize', handleResize);
onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  myChart?.dispose();
});
</script>