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

          // 使用 transformChangeToSteps 转换
          const experimentData = transformChangeToSteps(rawData, form)

          console.log('experimentData:', experimentData)
          // 保存到 pagingStore
          pagingStore.setExperiment(experimentData,{ ...form })

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

    // 跳转到历史测试查询页面
    const gotoHistory = () => {
      router.push('/testnum')
    }

    return { form, startExperiment, gotoHistory }
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

      <div class="button-group">
        <button type="submit">开始实验</button>
        <button type="button" @click="gotoHistory">历史测试查询</button>
      </div>

    </form>
  </div>
</template>

<style scoped>
    .config-view {
        padding: 20px;
        max-width: 1200px;
        margin: 0 auto;
    }
    
    .config-view h2 {
        margin-bottom: 20px;
        color: #333;
        font-size: 20px;
        text-align: center;
    }
    
    .config-view form {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 15px;
        background: white;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    }
    
    .config-view form div {
        display: flex;
        flex-direction: column;
        gap: 5px;
        margin-bottom: 0;
    }
    
    .config-view form div:nth-child(7) {
        grid-column: 1 / -1;
    }
    
    .config-view form label {
        font-size: 14px;
        font-weight: 500;
        color: #606266;
    }
    
    .config-view form input,
    .config-view form select,
    .config-view form textarea {
        padding: 8px 12px;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        font-size: 14px;
        transition: border-color 0.2s;
    }
    
    .config-view form input:focus,
    .config-view form select:focus,
    .config-view form textarea:focus {
        outline: none;
        border-color: #409eff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
    
    .config-view form textarea {
        resize: vertical;
        min-height: 80px;
    }
    
    .config-view form button {
        margin-right: 10px;
        padding: 10px 20px;
        background-color: #409eff;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.3s;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    
    .config-view form button:hover {
        background-color: #66b1ff;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
        transform: translateY(-1px);
    }
    
    .config-view form button[type="button"] {
        background-color: #909399;
    }
    
    .config-view form button[type="button"]:hover {
        background-color: #a6a9ad;
    }
    
    .button-group {
        grid-column: 1 / -1;
        display: flex;
        justify-content: center;
        gap: 15px;
        margin-top: 10px;
    }
</style>