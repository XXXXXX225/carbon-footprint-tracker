<template>
  <div class="carbon-chart">
    <div class="chart-header" v-if="title">
      <h3>{{ title }}</h3>
      <div class="chart-actions" v-if="showActions">
        <el-button size="small" @click="handleExport('png')">
          <el-icon><Download /></el-icon>
          导出图片
        </el-button>
        <el-button size="small" @click="handleExport('svg')">
          <el-icon><Document /></el-icon>
          导出SVG
        </el-button>
      </div>
    </div>
    <div ref="chartRef" class="chart-container" :style="{ height: height + 'px' }"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, defineProps, defineEmits } from 'vue'
import * as echarts from 'echarts'
import { Download, Document } from '@element-plus/icons-vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'line', // line, pie, bar, gauge
    validator: (value: string) => ['line', 'pie', 'bar', 'gauge'].includes(value)
  },
  data: {
    type: Object,
    default: () => ({})
  },
  height: {
    type: Number,
    default: 300
  },
  showActions: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['drillDown'])

const chartRef = ref<HTMLElement>()
const chart = ref<echarts.ECharts>()

const initChart = () => {
  if (chartRef.value) {
    chart.value = echarts.init(chartRef.value)
    updateChart()
    bindEvents()
  }
}

const bindEvents = () => {
  if (!chart.value) return
  
  chart.value.on('click', (params) => {
    emit('drillDown', params)
  })
}

const updateChart = () => {
  if (!chart.value) return
  
  let option: echarts.EChartsOption = {}
  
  switch (props.type) {
    case 'gauge':
      option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c}%'
        },
        series: [
          {
            name: props.title || '目标完成度',
            type: 'gauge',
            center: ['50%', '75%'],
            radius: '80%',
            startAngle: 180,
            endAngle: 0,
            min: 0,
            max: 100,
            splitNumber: 10,
            axisLine: {
              lineStyle: {
                width: 30,
                color: [
                  [0.3, '#F44336'],
                  [0.7, '#FFC107'],
                  [1, '#4CAF50']
                ]
              }
            },
            pointer: {
              length: '60%',
              width: 6,
              offsetCenter: [0, '-5%'],
              itemStyle: {
                color: 'auto'
              }
            },
            axisTick: {
              length: 12,
              lineStyle: {
                color: 'auto',
                width: 2
              }
            },
            splitLine: {
              length: 20,
              lineStyle: {
                color: 'auto',
                width: 5
              }
            },
            axisLabel: {
              color: '#464646',
              fontSize: 16,
              distance: -40,
              padding: [120, 0, 0, 0],
              formatter: function (value: number) {
                if (value === 0 || value === 100) {
                  return value + '%';
                }
                return '';
              }
            },
            title: {
              offsetCenter: [0, '40%'],
              fontSize: 16
            },
            detail: {
              fontSize: 30,
              offsetCenter: [0, '20%'],
              valueAnimation: true,
              formatter: function (value: number) {
                return Math.round(value) + '%';
              },
              color: 'auto'
            },
            data: [
              {
                value: props.data.value || 0,
                name: props.data.name || '完成度'
              }
            ]
          }
        ]
      }
      break
    case 'pie':
      option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'horizontal',
          bottom: 0,
          data: props.data.legend || []
        },
        series: [
          {
            name: props.title || '碳足迹',
            type: 'pie',
            radius: ['45%', '65%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: props.data.series || []
          }
        ]
      }
      break
    case 'line':
      option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: props.data.legend || [],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: props.data.xAxis || []
        },
        yAxis: {
          type: 'value',
          name: 'kg CO₂e'
        },
        series: props.data.series || []
      }
      break
    case 'bar':
      option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: props.data.legend || [],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: props.data.xAxis || []
        },
        yAxis: {
          type: 'value',
          name: 'kg CO₂e'
        },
        series: props.data.series || []
      }
      break
  }

  // Add ECharts ARIA accessability Support
  option.aria = { enabled: true, decal: { show: true } }
  
  chart.value.setOption(option)
}

const handleResize = () => {
  chart.value?.resize()
}

const handleExport = (type: 'png' | 'svg') => {
  if (!chart.value) return
  
  const url = chart.value.getDataURL({
    type: type,
    pixelRatio: 2,
    backgroundColor: '#fff'
  })
  
  const link = document.createElement('a')
  link.href = url
  link.download = `${props.title || 'chart'}.${type}`
  link.click()
}

watch(
  () => props.data,
  () => {
    updateChart()
  },
  { deep: true }
)

watch(
  () => props.type,
  () => {
    updateChart()
  }
)

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart.value?.dispose()
})
</script>

<style scoped>
.carbon-chart {
  width: 100%;
}

.chart-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header h3 {
  margin: 0;
  color: #4CAF50;
  font-size: 16px;
}

.chart-actions {
  display: flex;
  gap: 10px;
}

.chart-container {
  width: 100%;
}
</style>
