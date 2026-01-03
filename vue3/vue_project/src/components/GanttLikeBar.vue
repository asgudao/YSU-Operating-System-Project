<script>
export default {
  name: 'GanttLikeBar',
  props: {
    steps: {
      type: Array,
      required: true
    },
    title: { type: String, default: '' }
  },
  methods: {
    barStyle(time) {
      if (!this.steps || this.steps.length === 0) return {}
      const maxTime = Math.max(...this.steps.map(s => s.time))
      // 保证最小高度 2px
      const height = maxTime ? Math.max((time / maxTime) * 200, 2) : 2
      return {
        height: height + 'px',
        backgroundColor: '#4caf50',
        minHeight: '2px'
      }
    }
  }
}
</script>

<template>
  <div class="gantt-bar">
    <h4>{{ title }}</h4>
    <div class="bar-container">
      <div
          v-for="(step, index) in steps"
          :key="index"
          class="bar"
          :style="barStyle(step.time)"
          :title="`逻辑地址: ${step.logicAddress}, 时间: ${step.time} ns`"
      >
        {{ step.time }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.gantt-bar {
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
}
.bar {
  width: 30px;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  color: #fff;
  font-size: 10px;
  border-radius: 3px 3px 0 0;
  min-height: 2px; /* 防止高度为 0 */
  background-color: red;
}
</style>
