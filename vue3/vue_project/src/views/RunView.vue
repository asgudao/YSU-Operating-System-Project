<script>
import PageFrameView from '@/components/PageFrameView.vue'
import AddressStep from '@/components/AddressStep.vue'
import GanttLikeBar from '@/components/GanttLikeBar.vue'
import ConfigView from '@/views/ConfigView.vue'
import { pagingStore } from '@/store/pagingStore.js'
import { computed, ref, onUnmounted, watch } from 'vue'

export default {
  name: 'RunView',
  computed: {
    pagingStore() {
      return pagingStore
    }
  },
  components: { PageFrameView, AddressStep, GanttLikeBar, ConfigView },
  setup() {
    const experiment = ref(null)        // 转换后的实验结果
    const currentStep = ref(0)
    const playing = ref(false)
    let timer = null

    // 从 store 获取配置参数
    const config = computed(() => pagingStore.config || {})

    // 计算每步的耗时数组，用于 GanttLikeBar
    function computeStepTimes(step, config, algName) {
      if (!step || !config || !algName) return []

      const algStep = step[algName.toLowerCase() + 'Step'] || []
      const algTLB  = step[algName.toLowerCase() + 'TLB'] || []
      const times = []

      for (let i = 0; i < algStep.length; i++) {
        const addr = algStep[i]
        let time = 0

        if (addr == null) {
          time = config.handleLosepage ?? 2000  // 这里可以设置缺页处理时间默认值
          times.push({ time, logicAddress: '-' })
          continue
        }

        if (config.useTLB) {
          if (algTLB[i] === addr) {
            time += config.visitTLB ?? 10
          } else {
            time += (config.visitTLB ?? 10) + (config.visitMemory ?? 100)
          }
        } else {
          time += config.visitMemory ?? 100
        }

        times.push({ time, logicAddress: addr })
      }

      return times
    }



    // 监听 pagingStore.experiment

    watch(
        () => pagingStore.experiment,
        (val) => {
          if (val) {
            console.log('steps computed:', val)
            if (val[currentStep.value]) {
              console.log('currentStep:', currentStep.value, 'step at currentStep:', val[currentStep.value])
            }
            experiment.value = val   // 已经是转换后的数据
            currentStep.value = 0
          } else {
            experiment.value = null
            currentStep.value = 0
          }
        },
        { immediate: true }
    )

    const steps = computed(() => experiment.value?.steps || [])

    watch(() => steps.value, (val) => {
      console.log('steps.value changed:', val)
    })

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
      prevStep,
      computeStepTimes,
      config
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

            <!-- 单步访问 -->
            <AddressStep
                v-if="steps[currentStep]"
                :step="{
                logicAddress: steps[currentStep][alg.name.toLowerCase() + 'Step'][0],
                pageNo: steps[currentStep][alg.name.toLowerCase() + 'Step'][1],
                offset: steps[currentStep][alg.name.toLowerCase() + 'Step'][2],
                hitTLB: steps[currentStep][alg.name.toLowerCase() + 'TLB'][0] === steps[currentStep][alg.name.toLowerCase() + 'Step'][0],
                pageFault: !steps[currentStep][alg.name.toLowerCase() + 'Step'][0],
                replacedIndex: null,
                time: computeStepTimes(steps[currentStep], config, alg.name)[0].time
              }"
            />

            <GanttLikeBar
                v-if="steps[currentStep]"
                :steps="steps[currentStep] ? computeStepTimes(steps[currentStep], config, alg.name) : []"
                :title="alg.name + ' 访问耗时'"
            />


            <!-- 控制按钮 -->
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
