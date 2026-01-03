import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
import ConfigView from '@/views/ConfigView.vue'
import RunView from '@/views/RunView.vue'
import HistoryView from '@/views/HistoryView.vue'

const routes = [
  {
    path: '/',
    redirect: '/config' // 默认跳转到参数配置页
  },
  {
    path: '/config',
    name: 'Config',
    component: ConfigView
  },
  {
    path: '/run',
    name: 'Run',
    component: RunView
  },
  {
    path: '/history',
    name: 'History',
    component: HistoryView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
