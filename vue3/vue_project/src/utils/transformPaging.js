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
// export function transformChangeToSteps(change) {
//     const steps = []
//
//     const FIFO = { frames: change.fifo_TableChange, tlb: change.fifo_TLBChange }
//     const LRU  = { frames: change.lru_TableChange, tlb: change.lru_TLBChange }
//     const LFU  = { frames: change.lfu_TableChange, tlb: change.lfu_TLBChange }
//
//     // 计算动画总步数（假设列数一致）
//     const totalSteps = change.fifo_TableChange[0]?.length || 0
//
//     for (let i = 0; i < totalSteps; i++) {
//         steps.push({
//             fifoStep: change.fifo_TableChange.map(row => row[i] || null),
//             fifoTLB: change.fifo_TLBChange.map(row => row[i] || null),
//             lruStep: change.lru_TableChange.map(row => row[i] || null),
//             lruTLB: change.lru_TLBChange.map(row => row[i] || null),
//             lfuStep: change.lfu_TableChange.map(row => row[i] || null),
//             lfuTLB: change.lfu_TLBChange.map(row => row[i] || null)
//         })
//     }
//
//     return { steps, FIFO, LRU, LFU }
// }
/**
 * 生成单步访问信息
 * @param {*} change 后端 Change 对象
 * @param {*} config 参数对象 { visitMemory, visitTLB, handleLosepage }
 * @returns steps 数组，每个元素是 { fifoStep: [], fifoTLB: [], ... , time, logicAddress, pageNo, offset, hitTLB, pageFault, replacedIndex }
 */
export function transformChangeToSteps(change, config = {}) {
    const { visitMemory = 100, visitTLB = 10, handleLosepage = 2000 } = config;
    const steps = [];

    const FIFO = { frames: change.fifo_TableChange, tlb: change.fifo_TLBChange };
    const LRU  = { frames: change.lru_TableChange,  tlb: change.lru_TLBChange };
    const LFU  = { frames: change.lfu_TableChange,  tlb: change.lfu_TLBChange };

    const totalSteps = change.fifo_TableChange[0]?.length || 0;

    for (let i = 0; i < totalSteps; i++) {
        // 生成每步访问信息
        const stepObj = {
            fifoStep: change.fifo_TableChange.map(row => row[i] || null),
            fifoTLB: change.fifo_TLBChange.map(row => row[i] || null),
            lruStep: change.lru_TableChange.map(row => row[i] || null),
            lruTLB: change.lru_TLBChange.map(row => row[i] || null),
            lfuStep: change.lfu_TableChange.map(row => row[i] || null),
            lfuTLB: change.lfu_TLBChange.map(row => row[i] || null),
        };

        // 单步访问详细信息（示例，只生成逻辑地址、耗时等）
        stepObj.logicAddress = change.fifo_TableChange[0][i] || null;  // 这里可改成每算法不同
        stepObj.pageNo = stepObj.logicAddress ? parseInt(stepObj.logicAddress, 16) % FIFO.frames.length : null;
        stepObj.offset = stepObj.logicAddress ? parseInt(stepObj.logicAddress, 16) % 256 : null;

        // 假设前端计算 TLB 命中和缺页
        stepObj.hitTLB = stepObj.fifoTLB.includes(stepObj.logicAddress);
        stepObj.pageFault = !stepObj.fifoStep.includes(stepObj.logicAddress);
        stepObj.replacedIndex = stepObj.pageFault ? 0 : null; // 这里可改成替换策略真实索引
        stepObj.time = stepObj.hitTLB ? visitTLB : stepObj.pageFault ? handleLosepage : visitMemory;

        steps.push(stepObj);
    }

    return { steps, FIFO, LRU, LFU };
}
