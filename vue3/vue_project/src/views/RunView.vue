<script>//算法运行页
import PageFrameView from '@/components/PageFrameView.vue'
import AddressStep from '@/components/AddressStep.vue'
import GanttLikeBar from '@/components/GanttLikeBar.vue'
import { pagingStore } from '@/store/pagingStore.js'
import { computed, ref, onUnmounted } from 'vue'

export default {
  name: 'RunView',
  components: { PageFrameView, AddressStep, GanttLikeBar },
  setup() {
    const experiment = computed(() => pagingStore.experiment)

    const steps = computed(() => experiment.value?.steps || [])

    const algorithms = computed(() => {
      if (!experiment.value) return []
      return [
        { name: 'FIFO', frames: experiment.value.FIFO.frames, tlb: experiment.value.FIFO.tlb },
        { name: 'LRU', frames: experiment.value.LRU.frames, tlb: experiment.value.LRU.tlb },
        { name: 'LFU', frames: experiment.value.LFU.frames, tlb: experiment.value.LFU.tlb }
      ]
    })

    const currentStep = ref(0)
    const playing = ref(false)
    let timer = null

    const playPause = () => {
      playing.value = !playing.value
      if (playing.value) {
        timer = setInterval(() => {
          if (currentStep.value < steps.value.length - 1) {
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
      if (currentStep.value < steps.value.length - 1) currentStep.value++
    }

    const prevStep = () => {
      if (currentStep.value > 0) currentStep.value--
    }

    onUnmounted(() => {
      if (timer) clearInterval(timer)
    })

    return {
      experiment,
      steps,
      algorithms,
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
    <div class="run-view">
      <h2>分页算法运行可视化</h2>

      <div v-if="!experiment">
        <p>请先在参数配置页启动实验。</p>
      </div>

      <div v-else class="algorithm-container">
        <div
            v-for="alg in algorithms"
            :key="alg.name"
            class="algorithm-card-wrapper"
        >
          <!-- 卡片标题 -->
          <h3>{{ alg.name }}</h3>

          <!-- 内存页框 -->
          <PageFrameView
              :frames="alg.frames"
              :highlight="steps[currentStep]"
              :title="alg.name + ' 内存页框'"
          />

          <!-- TLB -->
          <PageFrameView
              v-if="alg.tlb && alg.tlb.length"
              :frames="alg.tlb"
              :highlight="steps[currentStep]"
              :title="alg.name + ' TLB'"
              :isTLB="true"
          />

          <!-- 当前访问信息 -->
          <AddressStep :step="steps[currentStep]" />

          <!-- 甘特图 -->
          <GanttLikeBar :steps="steps" :title="alg.name + ' 访问耗时'" />

          <!-- 播放控制 -->
          <div class="controls">
            <button @click="prevStep">⏮</button>
            <button @click="playPause">{{ playing ? '⏸' : '▶' }}</button>
            <button @click="nextStep">⏭</button>
            <span>{{ currentStep + 1 }} / {{ steps.length }}</span>
          </div>
        </div>
      </div>
    </div>
</template>

<style scoped>
.run-view {
  padding: 20px;
}

.algorithm-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.algorithm-card-wrapper {
  flex: 1 1 300px;
  border: 1px solid #ccc;
  padding: 15px;
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