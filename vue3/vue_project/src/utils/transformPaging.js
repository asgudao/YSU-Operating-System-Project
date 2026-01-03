/**
 * 将后端返回的 Change 对象转换成前端可用的实验结构
 *
 * 返回结构：
 * {
 *   steps: [
 *     {
 *       fifoStep: [],
 *       fifoTLB: [],
 *       lruStep: [],
 *       lruTLB: [],
 *       lfuStep: [],
 *       lfuTLB: [],
 *       logicAddress,
 *       hitTLB,
 *       pageFault,
 *       replacedIndex,
 *       time
 *     }
 *   ],
 *   FIFO: { frames, tlb },
 *   LRU:  { frames, tlb },
 *   LFU:  { frames, tlb }
 * }
 */
export function transformChangeToSteps(change, config = {}) {
    console.log('[transform] config =', config)
    console.log('[transform] inputNum =', config.inputNum)

    const {
        visitMemory = 100,
        visitTLB = 10,
        handleLosepage = 2000,
        inputNum = ''
    } = config

    const logicAddressList = inputNum
        .split(',')
        .map(v => v.trim())
        .filter(Boolean)

    const steps = []

    const FIFO = {
        frames: change.fifo_TableChange,
        tlb: change.fifo_TLBChange
    }
    const LRU = {
        frames: change.lru_TableChange,
        tlb: change.lru_TLBChange
    }
    const LFU = {
        frames: change.lfu_TableChange,
        tlb: change.lfu_TLBChange
    }

    const totalSteps = change.fifo_TableChange[0]?.length || 0

    for (let i = 0; i < totalSteps; i++) {
        /* ---------- 当前步的结果态 ---------- */
        const fifoStep = change.fifo_TableChange.map(row => row[i] ?? null)
        const fifoTLB  = change.fifo_TLBChange.map(row => row[i] ?? null)

        const lruStep  = change.lru_TableChange.map(row => row[i] ?? null)
        const lruTLB   = change.lru_TLBChange.map(row => row[i] ?? null)

        const lfuStep  = change.lfu_TableChange.map(row => row[i] ?? null)
        const lfuTLB   = change.lfu_TLBChange.map(row => row[i] ?? null)

        /* ---------- 上一步状态（用于判断） ---------- */
        const prevFifoFrames =
            i > 0 ? change.fifo_TableChange.map(row => row[i - 1] ?? null) : []

        const prevFifoTLB =
            i > 0 ? change.fifo_TLBChange.map(row => row[i - 1] ?? null) : []

        /* ---------- 本步新访问的逻辑地址 ---------- */
        const logicAddress = logicAddressList[i] ?? null


        /* ---------- 命中 / 缺页判断（关键） ---------- */
        const hitTLB =
            i > 0 && logicAddress !== null
                ? prevFifoTLB.includes(logicAddress)
                : false

        const pageFault =
            i > 0 && logicAddress !== null
                ? !prevFifoFrames.includes(logicAddress)
                : true

        /* ---------- 被替换页框索引 ---------- */
        let replacedIndex = null
        if (i > 0 && pageFault) {
            const currFrames = fifoStep
            for (let idx = 0; idx < currFrames.length; idx++) {
                if (prevFifoFrames[idx] !== currFrames[idx]) {
                    replacedIndex = idx
                    break
                }
            }
        }

        /* ---------- 耗时计算 ---------- */
        let time = 0
        if (hitTLB) {
            time = visitTLB
        } else if (pageFault) {
            time = handleLosepage
        } else {
            time = visitMemory
        }

        /* ---------- 汇总 ---------- */
        steps.push({
            fifoStep,
            fifoTLB,
            lruStep,
            lruTLB,
            lfuStep,
            lfuTLB,

            logicAddress,
            hitTLB,
            pageFault,
            replacedIndex,
            time
        })
    }

    return { steps, FIFO, LRU, LFU }
}
