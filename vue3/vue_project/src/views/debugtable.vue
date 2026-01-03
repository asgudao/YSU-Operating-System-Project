<template>
  <div class="debug-table">
    <h3>单步调试表格 - Step {{ currentStep + 1 }} / {{ steps.length }}</h3>
    <table>
      <thead>
      <tr>
        <th>算法</th>
        <th>逻辑地址</th>
        <th>TLB</th>
        <th>页框</th>
        <th>TLB命中</th>
        <th>缺页</th>
        <th>替换页框</th>
        <th>总耗时 (ns)</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="alg in algorithms" :key="alg.name">
        <td>{{ alg.name }}</td>
        <td>{{ stepData(alg.name).logicAddress }}</td>
        <td>{{ stepData(alg.name).tlb.join(', ') }}</td>
        <td>{{ stepData(alg.name).frames.join(', ') }}</td>
        <td :class="stepData(alg.name).hitTLB ? 'hit' : 'miss'">
          {{ stepData(alg.name).hitTLB ? '命中' : '未命中' }}
        </td>
        <td :class="stepData(alg.name).pageFault ? 'miss' : 'hit'">
          {{ stepData(alg.name).pageFault ? '缺页' : '无缺页' }}
        </td>
        <td>{{ stepData(alg.name).replacedIndex !== null ? stepData(alg.name).replacedIndex : '-' }}</td>
        <td>{{ stepData(alg.name).time }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: 'DebugTable',
  props: {
    steps: { type: Array, default: () => [] },
    algorithms: { type: Array, default: () => [] },
    currentStep: { type: Number, default: 0 },
  },
  methods: {
    stepData(algName) {
      if (!this.steps[this.currentStep]) return {
        logicAddress: '-', tlb: [], frames: [], hitTLB: false, pageFault: false, replacedIndex: null, time: 0
      };
      const step = this.steps[this.currentStep];
      const algLower = algName.toLowerCase();
      return {
        logicAddress: step.logicAddress || '-',
        tlb: step[algLower + 'TLB'] || [],
        frames: step[algLower + 'Step'] || [],
        hitTLB: step.hitTLB,
        pageFault: step.pageFault,
        replacedIndex: step.replacedIndex,
        time: step.time
      };
    }
  }
}
</script>

<style scoped>
.debug-table {
  margin-top: 20px;
  overflow-x: auto;
}
table {
  border-collapse: collapse;
  width: 100%;
}
th, td {
  border: 1px solid #ccc;
  padding: 6px 8px;
  text-align: center;
  font-family: monospace;
}
.hit { color: green; font-weight: bold; }
.miss { color: red; font-weight: bold; }
</style>
