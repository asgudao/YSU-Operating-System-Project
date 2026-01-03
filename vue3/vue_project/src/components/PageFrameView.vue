<script>//内存页框可视化
export default {
  name: "PageFrameView",
  props: {
    frames: {        // 二维数组，当前算法每步的页框状态
      type: Array,
      required: true
    },
    highlight: {     // 当前步骤信息
      type: Object,
      default: () => ({})
    },
    isTLB: {        // 是否是TLB显示
      type: Boolean,
      default: false
    },
    title: {        // 标题
      type: String,
      default: ""
    }
  },
  methods: {
    cellClass(cell, colIndex) {
      const classes = ["cell"]
      // 高亮命中
      if (cell === this.highlight.pageNo && this.highlight.hitTLB) {
        classes.push("hit")
      }
      // 高亮缺页
      if (this.highlight.pageFault && this.highlight.replacedIndex === colIndex) {
        classes.push("miss")
      }
      if (this.isTLB) classes.push("tlb")
      return classes
    }
  }
}
</script>

<template>
  <div class="frame-container">
    <h4>{{ title }}</h4>
    <div v-for="(row, rowIndex) in frames" :key="rowIndex" class="frame-row">
      <div
          v-for="(cell, colIndex) in row"
          :key="colIndex"
          :class="cellClass(cell, colIndex)"
      >
        {{ cell || '-' }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.frame-container {
  margin: 10px 0;
}
.frame-row {
  display: flex;
  margin-bottom: 5px;
}
.cell {
  width: 40px;
  height: 40px;
  border: 1px solid #333;
  text-align: center;
  line-height: 40px;
  margin-right: 5px;
  border-radius: 4px;
  background-color: #f5f5f5;
}
.hit {
  background-color: #8f8; /* 命中 */
}
.miss {
  background-color: #f88; /* 缺页/替换 */
}
.tlb {
  border-color: #08f; /* TLB边框专色 */
}
</style>