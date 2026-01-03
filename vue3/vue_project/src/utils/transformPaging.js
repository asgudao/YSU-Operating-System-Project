/**
 * 将后端返回的 Change 对象转换成前端可用的实验结构
 * 返回值格式：
 * {
 *   steps: [{ fifoStep: [], fifoTLB: [], lruStep: [], lruTLB: [], lfuStep: [], lfuTLB: [] }, ...],
 *   FIFO: { frames: [...], tlb: [...] },
 *   LRU: { frames: [...], tlb: [...] },
 *   LFU: { frames: [...], tlb: [...] }
 * }
 */
export function transformChangeToSteps(change) {
    const steps = []

    const FIFO = { frames: change.fifo_TableChange, tlb: change.fifo_TLBChange }
    const LRU  = { frames: change.lru_TableChange, tlb: change.lru_TLBChange }
    const LFU  = { frames: change.lfu_TableChange, tlb: change.lfu_TLBChange }

    // 计算动画总步数（假设列数一致）
    const totalSteps = change.fifo_TableChange[0]?.length || 0

    for (let i = 0; i < totalSteps; i++) {
        steps.push({
            fifoStep: change.fifo_TableChange.map(row => row[i] || null),
            fifoTLB: change.fifo_TLBChange.map(row => row[i] || null),
            lruStep: change.lru_TableChange.map(row => row[i] || null),
            lruTLB: change.lru_TLBChange.map(row => row[i] || null),
            lfuStep: change.lfu_TableChange.map(row => row[i] || null),
            lfuTLB: change.lfu_TLBChange.map(row => row[i] || null)
        })
    }

    return { steps, FIFO, LRU, LFU }
}
