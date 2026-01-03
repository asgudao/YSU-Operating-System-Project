<script>
export default {
  name: 'GanttGlobalBar',
  props: {
    steps: {
      type: Array,
      required: true
    },
    title: { type: String, default: '全局甘特图' }
  },
  computed: {
    maxTime() {
      if (!this.steps || this.steps.length === 0) return 1
      return Math.max(...this.steps.map(s => s.time))
    }
  },
  methods: {
    barStyle(time) {
      const height = this.maxTime ? (time / this.maxTime) * 200 : 2
      return {
        height: Math.max(height, 2) + 'px',
        backgroundColor: '#2196f3',
        minHeight: '2px'
      }
    }
  }
}
</script>

<template>
  <div class="gantt-global-bar">
    <h4>{{ title }}</h4>
    <div class="bar-container">
      <div
          v-for="(step, index) in steps"
          :key="index"
          class="bar"
          :style="barStyle(step.time)"
          :title="`逻辑地址: ${step.logicAddress}, 耗时: ${step.time} ns`"
      >
        {{ step.time }}
      </div>
    </div>
    <div class="x-labels">
      <span v-for="(step, index) in steps" :key="index">
        {{ step.logicAddress }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.gantt-global-bar {
  margin: 15px 0;
}
.bar-container {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 220px;
  border-left: 1px solid #333;
  border-bottom: 1px solid #333;
  padding: 5px;
  overflow-x: auto;
}
.bar {
  width: 30px;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  color: #fff;
  font-size: 10px;
  border-radius: 3px 3px 0 0;
}
.x-labels {
  display: flex;
  gap: 4px;
  margin-top: 5px;
  font-size: 10px;
}
.x-labels span {
  width: 30px;
  text-align: center;
}
</style>
