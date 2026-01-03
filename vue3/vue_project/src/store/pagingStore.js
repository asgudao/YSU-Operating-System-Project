//实验状态存储
import { reactive } from 'vue'

export const pagingStore = reactive({
    experiment: null,   // 保存算法运行结果
    currentStep: 0,     // 当前播放步骤
    setExperiment(data) {
        this.experiment = data
        this.currentStep = 0
    },
    setStep(index) {
        this.currentStep = index
    },
    reset() {
        this.experiment = null
        this.currentStep = 0
    }
})
