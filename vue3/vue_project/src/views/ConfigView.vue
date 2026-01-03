<script>//参数配置页
import { startPaging } from '@/apis/paging.js'
import { pagingStore } from '@/store/pagingStore.js'
import { useRouter } from 'vue-router'

export default {
  setup() {
    const router = useRouter()

    const form = {
      pageNum: 3,
      useTLB: 1,
      TLBNum: 4,
      visitMemory: 100,
      visitTLB: 10,
      handleLosepage: 2000,
      inputNum: '0E8A,0AF4,0577,0DBA,12C5,07EA,0C58,1496'
    }

    const startExperiment = async () => {
      try {
        const res = await startPaging(form)
        if (res.code === 200) {
          alert('实验启动成功！')
          pagingStore.setExperiment(res.data)   // 保存实验结果
          router.push('/run')                   // 跳转到运行页
        } else {
          alert('启动失败: ' + res.msg)
        }
      } catch (error) {
        alert('接口调用错误: ' + error)
      }
    }

    return { form, startExperiment }
  }
}
</script>

<template>
  <div class="config-view">
    <h2>分页管理系统参数配置</h2>

    <form @submit.prevent="startExperiment">

      <div>
        <label>驻留页框数:</label>
        <input type="number" v-model.number="form.pageNum" min="1">
      </div>

      <div>
        <label>是否使用TLB:</label>
        <select v-model.number="form.useTLB">
          <option :value="1">是</option>
          <option :value="0">否</option>
        </select>
      </div>

      <div>
        <label>TLB容量:</label>
        <input type="number" v-model.number="form.TLBNum" min="1">
      </div>

      <div>
        <label>内存访问时间(ns):</label>
        <input type="number" v-model.number="form.visitMemory">
      </div>

      <div>
        <label>TLB访问时间(ns):</label>
        <input type="number" v-model.number="form.visitTLB">
      </div>

      <div>
        <label>缺页处理时间(ns):</label>
        <input type="number" v-model.number="form.handleLosepage">
      </div>

      <div>
        <label>逻辑地址序列(逗号分隔):</label>
        <textarea v-model="form.inputNum" rows="3" cols="50"></textarea>
      </div>

      <button type="submit">开始实验</button>

    </form>
  </div>
</template>

<style scoped>
.config-view {
  padding: 20px;
}
.config-view form div {
  margin-bottom: 10px;
}
</style>