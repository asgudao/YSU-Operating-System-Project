<script>
import PageFrameView from '@/components/PageFrameView.vue'
import AddressStep from '@/components/AddressStep.vue'
import GanttLikeBar from '@/components/GanttLikeBar.vue'
import ConfigView from '@/views/ConfigView.vue'
import { pagingStore } from '@/store/pagingStore.js'
import { computed, ref, onUnmounted, watch } from 'vue'
import { transformChangeToSteps } from '@/utils/transformPaging.js'

export default {
  name: 'RunView',
  components: { PageFrameView, AddressStep, GanttLikeBar, ConfigView },
  setup() {
    const experiment = ref(null)        // 本地存放转换后的实验结果
    const currentStep = ref(0)
    const playing = ref(false)
    let timer = null

    // 监听 pagingStore.experiment，当实验启动成功时，将后端 Change 数据转换
    watch(
        () => pagingStore.experiment,
        (val) => {
          experiment.value = val
          currentStep.value = 0
        },
        { immediate: true }
    )

    const steps = computed(() => experiment.value?.steps || [])

    const algorithms = computed(() => {
      if (!experiment.value) return []
      return [
        { name: 'FIFO', frames: experiment.value.FIFO.frames, tlb: experiment.value.FIFO.tlb },
        { name: 'LRU',  frames: experiment.value.LRU.frames,  tlb: experiment.value.LRU.tlb },
        { name: 'LFU',  frames: experiment.value.LFU.frames,  tlb: experiment.value.LFU.tlb }
      ]
    })

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
    <div class="main-container">
      <!-- 左侧动画区 -->
      <div class="animation-column">
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
            <h3>{{ alg.name }}</h3>

            <PageFrameView
                :frames="alg.frames"
                :highlight="steps[currentStep]"
                :title="alg.name + ' 内存页框'"
            />

            <PageFrameView
                v-if="alg.tlb && alg.tlb.length"
                :frames="alg.tlb"
                :highlight="steps[currentStep]"
                :title="alg.name + ' TLB'"
                :isTLB="true"
            />

            <AddressStep :step="steps[currentStep]" />
            <GanttLikeBar :steps="steps" :title="alg.name + ' 访问耗时'" />

            <div class="controls">
              <button @click="prevStep">⏮</button>
              <button @click="playPause">{{ playing ? '⏸' : '▶' }}</button>
              <button @click="nextStep">⏭</button>
              <span>{{ currentStep + 1 }} / {{ steps.length }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧配置区 -->
      <div class="config-column">
        <ConfigView />
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-container {
  display: flex;
  gap: 20px;
}
.animation-column {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.config-column {
  flex: 1;
}
.algorithm-card-wrapper {
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
