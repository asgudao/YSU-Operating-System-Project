<script>//实验记录页
import { getAllPages, getTestNum, deletePage } from '@/api/paging.js'
import { pagingStore } from '@/store/pagingStore.js'
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'

export default {
  name: 'HistoryView',
  setup() {
    const pages = ref([])
    const router = useRouter()

    // 获取所有实验记录
    const fetchPages = async () => {
      try {
        const res = await getAllPages()
        if (res.code === 200) {
          pages.value = res.data
        } else {
          alert('获取实验记录失败: ' + res.msg)
        }
      } catch (err) {
        alert('接口调用错误: ' + err)
      }
    }

    // 加载某条实验记录到 store 并跳转 RunView
    const loadExperiment = async (id) => {
      try {
        const res = await getTestNum(id)
        if (res.code === 200) {
          pagingStore.setExperiment(res.data)
          router.push('/run')
        } else {
          alert('加载失败: ' + res.msg)
        }
      } catch (err) {
        alert('接口调用错误: ' + err)
      }
    }

    // 删除实验记录
    const deleteExperiment = async (id) => {
      if (!confirm('确定删除该实验吗？')) return
      try {
        const res = await deletePage(id)
        if (res.code === 200) {
          alert('删除成功')
          fetchPages()
        } else {
          alert('删除失败: ' + res.msg)
        }
      } catch (err) {
        alert('接口调用错误: ' + err)
      }
    }

    onMounted(() => {
      fetchPages()
    })

    return {
      pages,
      loadExperiment,
      deleteExperiment
    }
  }
}
</script>

<template>
  <div class="history-view">
    <h2>实验记录</h2>

    <div v-if="!pages.length">
      <p>暂无实验记录</p>
    </div>

    <table v-else>
      <thead>
      <tr>
        <th>ID</th>
        <th>页框数</th>
        <th>是否使用TLB</th>
        <th>TLB容量</th>
        <th>逻辑地址序列</th>
        <th>创建时间</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="page in pages" :key="page.id">
        <td>{{ page.id }}</td>
        <td>{{ page.pageNum }}</td>
        <td>{{ page.useTLB ? '是' : '否' }}</td>
        <td>{{ page.TLBNum }}</td>
        <td>{{ page.inputNum }}</td>
        <td>{{ page.createTime }}</td>
        <td>
          <button @click="loadExperiment(page.id)">加载</button>
          <button @click="deleteExperiment(page.id)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.history-view {
  padding: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 6px 10px;
  border: 1px solid #ccc;
  text-align: center;
}
button {
  margin: 0 4px;
  padding: 2px 6px;
}
</style>