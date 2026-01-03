// 实验状态存储
import { reactive } from 'vue'

export const pagingStore = reactive({
    experiment: null,   // 保存算法运行结果（已经转换成 steps + FIFO/LRU/LFU）
    config: {
        useTLB: true,
        visitMemory: 100,
        visitTLB: 10,
        handleLosepage: 2000
    },       // 保存实验配置参数，用于前端时间计算
    currentStep: 0,     // 当前播放步骤


    // 设置实验结果和配置
    setExperiment(data, cfg) {
        this.experiment = data
        this.config = cfg || null
        this.currentStep = 0
    },

    // 设置当前步骤
    setStep(index) {
        this.currentStep = index
    },

    // 重置实验
    reset() {
        this.experiment = null
        this.config = null
        this.currentStep = 0
    }
})
