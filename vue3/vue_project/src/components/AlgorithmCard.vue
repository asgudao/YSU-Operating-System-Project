<script>//算法单卡片
import PageFrameView from './PageFrameView.vue'
import AddressStep from './AddressStep.vue'
import { ref, watch, onUnmounted } from 'vue'

export default {
  name: 'AlgorithmCard',
  components: { PageFrameView, AddressStep },
  props: {
    title: { type: String, default: '' },
    frames: { type: Array, required: true },      // 二维数组 [step][pageFrameIndex]
    tlbFrames: { type: Array, default: () => [] },// 可选 TLB frames
    steps: { type: Array, required: true }       // 每一步的访问信息
  },
  setup(props) {
    const currentStep = ref(0)
    const playing = ref(false)
    let timer = null

    const playPause = () => {
      playing.value = !playing.value
      if (playing.value) {
        timer = setInterval(() => {
          if (currentStep.value < props.steps.length - 1) {
            currentStep.value++
          } else {
            clearInterval(timer)
            playing.value = false
          }
        }, 800)
      } else {
        clearInterval(timer)
      }
    }

    const nextStep = () => {
      if (currentStep.value < props.steps.length - 1) currentStep.value++
    }

    const prevStep = () => {
      if (currentStep.value > 0) currentStep.value--
    }

    // 清理定时器
    onUnmounted(() => {
      if (timer) clearInterval(timer)
    })

    return {
      currentStep,
      playing,
      playPause,
      nextStep,
      prevStep
    }
  }
}
</script>

<template>
  <div class="algorithm-card">
    <h3>{{ title }}</h3>

    <!-- 内存页框 -->
    <PageFrameView
        :frames="frames[currentStep]"
        :highlight="steps[currentStep]"
        :title="title + ' 内存页框'"
    />

    <!-- TLB -->
    <PageFrameView
        v-if="tlbFrames && tlbFrames.length"
        :frames="tlbFrames[currentStep]"
        :highlight="steps[currentStep]"
        :title="title + ' TLB'"
        :isTLB="true"
    />

    <!-- 当前访问信息 -->
    <AddressStep :step="steps[currentStep]" />

    <!-- 播放控制按钮 -->
    <div class="controls">
      <button @click="prevStep">⏮</button>
      <button @click="playPause">{{ playing ? '⏸' : '▶' }}</button>
      <button @click="nextStep">⏭</button>
      <span>{{ currentStep + 1 }} / {{ steps.length }}</span>
    </div>
  </div>
</template>

<style scoped>
.algorithm-card {
  border: 1px solid #ccc;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 6px;
  background-color: #fafafa;
}
.controls {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.controls button {
  padding: 5px 10px;
}
</style>