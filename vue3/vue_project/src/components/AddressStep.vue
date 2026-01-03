<script>//单步访问展示
export default {
  name: 'AddressStep',
  props: {
    step: {
      type: Object,
      default: null
    }
  }
}
</script>

<template>
  <div class="address-step" v-if="step">
    <h4>当前访问</h4>
    <ul>
      <li><strong>逻辑地址：</strong>{{ step.logicAddress }}</li>
      <li><strong>页号：</strong>{{ step.pageNo }}</li>
      <li><strong>页内偏移：</strong>{{ step.offset }}</li>
      <li>
        <strong>TLB命中：</strong>
        <span :class="step.hitTLB ? 'hit' : 'miss'">{{ step.hitTLB ? '命中' : '未命中' }}</span>
      </li>
      <li>
        <strong>缺页：</strong>
        <span :class="step.pageFault ? 'miss' : 'hit'">{{ step.pageFault ? '缺页' : '无缺页' }}</span>
      </li>
      <li><strong>替换页框：</strong>{{ step.replacedIndex !== undefined ? step.replacedIndex : '-' }}</li>
      <li><strong>耗时：</strong>{{ step.time }} ns</li>
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