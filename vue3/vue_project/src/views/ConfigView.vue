<script>
// 参数配置页
import { startPaging } from '@/apis/paging.js'
import { pagingStore } from '@/store/pagingStore.js'
import { useRouter } from 'vue-router'
import { transformChangeToSteps } from '@/utils/transformPaging.js'

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
        if (res && res.data?.state === 0) {
          alert('实验启动成功！')

          const rawData = res.data.data

          // 转换为前端动画步骤并保存到 pagingStore
          // pagingStore.setExperiment({
          //   FIFO: { frames: rawData.fifo_TableChange, tlb: rawData.fifo_TLBChange },
          //   LRU: { frames: rawData.lru_TableChange, tlb: rawData.lru_TLBChange },
          //   LFU: { frames: rawData.lfu_TableChange, tlb: rawData.lfu_TLBChange },
          //   steps: transformChangeToSteps(rawData).steps
          // })

          // 使用 transformChangeToSteps 转换
          const experimentData = transformChangeToSteps(rawData)

          // 保存到 pagingStore
          pagingStore.setExperiment(experimentData)

          router.push('/run')  // 跳转到运行页
        } else {
          const msg = res?.data?.message || JSON.stringify(res)
          alert('启动失败: ' + msg)
          console.error('启动失败响应:', res)
        }
      } catch (error) {
        if (error.response) {
          console.error('后端响应错误:', error.response)
          alert(
              `接口调用失败！状态码: ${error.response.status}\n` +
              `响应数据: ${JSON.stringify(error.response.data)}`
          )
        } else if (error.request) {
          console.error('请求未收到响应:', error.request)
          alert('请求未收到响应，请检查后端是否启动以及 CORS 配置')
        } else {
          console.error('请求设置错误:', error.message)
          alert('请求错误: ' + error.message)
        }
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
        <select v-model.number="form.useTLB" min="0">
          <option :value="1">是</option>
          <option :value="0">否</option>
        </select>
      </div>

      <div>
        <label>TLB容量:</label>
        <input type="number" v-model.number="form.TLBNum" min="0">
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