<template>
  <span>{{ formattedValue }}</span>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'

const props = defineProps({
  value: {
    type: Number,
    required: true
  },
  duration: {
    type: Number,
    default: 1000
  },
  decimals: {
    type: Number,
    default: 0
  }
})

const displayValue = ref(props.value)
let animationFrame: number | null = null

const animate = (start: number, end: number) => {
  if (start === end) return
  
  const startTime = performance.now()
  const tick = (currentTime: number) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / props.duration, 1)

    // Easing function (easeOutQuart)
    const easeOut = 1 - Math.pow(1 - progress, 4)
    displayValue.value = start + (end - start) * easeOut

    if (progress < 1) {
      animationFrame = requestAnimationFrame(tick)
    } else {
      displayValue.value = end
    }
  }
  
  if (animationFrame) cancelAnimationFrame(animationFrame)
  animationFrame = requestAnimationFrame(tick)
}

watch(() => props.value, (newVal, oldVal) => {
  animate(oldVal || 0, newVal)
})

onMounted(() => {
  animate(0, props.value)
})

const formattedValue = computed(() => {
  return displayValue.value.toLocaleString('zh-CN', {
    minimumFractionDigits: props.decimals,
    maximumFractionDigits: props.decimals
  })
})
</script>
