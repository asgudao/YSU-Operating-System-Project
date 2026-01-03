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
/* 主容器样式 */
.run-view {
    padding: 20px;
    max-width: 1400px;
    margin: 0 auto;
}

/* 标题样式 */
.run-view h2 {
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
    font-weight: 600;
    text-align: center;
}

.run-view h3 {
    font-size: 18px;
    color: #606266;
    margin-bottom: 15px;
    font-weight: 500;
    border-bottom: 1px solid #ebeef5;
    padding-bottom: 8px;
}

/* 主布局容器 */
.main-container {
    display: flex;
    gap: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

/* 动画列样式 */
.animation-column {
    flex: 3;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    overflow-y: auto;
    max-height: calc(100vh - 120px);
}

/* 算法卡片容器 */
.algorithm-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
}

/* 算法卡片样式 */
.algorithm-card-wrapper {
    background: #fafafa;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    padding: 15px;
    transition: all 0.3s;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.algorithm-card-wrapper:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-color: #409eff;
}

/* 配置列样式 */
.config-column {
    flex: 1;
    background: #f5f7fa;
    padding: 20px;
    border-left: 1px solid #ebeef5;
    overflow-y: auto;
    max-height: calc(100vh - 120px);
}

/* 控制按钮样式 */
.controls {
    margin-top: 15px;
    display: flex;
    align-items: center;
    gap: 10px;
    justify-content: center;
    background: white;
    padding: 10px;
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.controls button {
    padding: 8px 16px;
    background: #409eff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.controls button:hover {
    background: #66b1ff;
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.controls span {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
    min-width: 80px;
    text-align: center;
}

/* 空状态样式 */
.run-view p {
    text-align: center;
    color: #909399;
    font-size: 16px;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 滚动条样式 */
.animation-column::-webkit-scrollbar,
.config-column::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

.animation-column::-webkit-scrollbar-track,
.config-column::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
}

.animation-column::-webkit-scrollbar-thumb,
.config-column::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 4px;
}

.animation-column::-webkit-scrollbar-thumb:hover,
.config-column::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}
</style>
