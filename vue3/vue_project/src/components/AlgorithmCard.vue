<script>
export default {
  name: 'AddressStep',
  props: {
    step: { type: Object, default: null },
    alg: { type: String, required: true } // 'FIFO', 'LRU', 'LFU'
  },
  computed: {
    // 算法小写前缀
    prefix() {
      return this.alg.toLowerCase();
    },
    // 逻辑地址
    logicAddress() {
      return this.step?.[this.prefix + 'Step'][0] || '-';
    },
    // TLB命中
    hitTLB() {
      const tlb = this.step?.[this.prefix + 'TLB'] || [];
      return tlb.includes(this.logicAddress);
    },
    // 缺页
    pageFault() {
      const memStep = this.step?.[this.prefix + 'Step'] || [];
      return !memStep.includes(this.logicAddress);
    },
    // 替换页框
    replacedIndex() {
      if (!this.pageFault) return null;
      const memStep = this.step?.[this.prefix + 'Step'] || [];
      // 找到第一个 null 被替换
      return memStep.findIndex(v => v === null);
    },
    // 总耗时
    time() {
      const cfg = this.step?.config || {};
      if (this.hitTLB) return cfg.visitTLB ?? 10;
      if (this.pageFault) return cfg.handleLosepage ?? 2000;
      return cfg.visitMemory ?? 100;
    }
  }
}
</script>

<template>
  <div class="address-step" v-if="step">
    <h4>{{ alg }} 当前访问</h4>
    <ul>
      <li><strong>逻辑地址：</strong>{{ logicAddress }}</li>
      <li>
        <strong>TLB命中：</strong>
        <span :class="hitTLB ? 'hit' : 'miss'">{{ hitTLB ? '命中' : '未命中' }}</span>
      </li>
      <li>
        <strong>缺页：</strong>
        <span :class="pageFault ? 'miss' : 'hit'">{{ pageFault ? '缺页' : '无缺页' }}</span>
      </li>
      <li><strong>替换页框：</strong>{{ replacedIndex !== null ? replacedIndex : '-' }}</li>
      <li><strong>总耗时：</strong>{{ time }} ns</li>
    </ul>
  </div>
</template>

<style scoped>
.address-step {
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 6px;
  margin-top: 10px;
  background-color: #fff8dc;
}
.address-step ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.address-step li {
  margin-bottom: 4px;
}
.hit {
  color: green;
  font-weight: bold;
}
.miss {
  color: red;
  font-weight: bold;
}
</style>
