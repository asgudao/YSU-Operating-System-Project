import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
import ConfigView from '@/views/ConfigView.vue'
import RunView from '@/views/RunView.vue'

const routes = [
  {
    path: '/',
    redirect: '/config' // 默认跳转到参数配置页
  },
  {
    path: '/config',
    name: 'Config',
    component: () => import('@/views/ConfigView.vue')
  },
  {
    path: '/run',
    name: 'Run',
    component: () => import('@/views/RunView.vue')
  },
  {
    path: '/testnum',
    name: 'TestNum',
    component: () => import('@/views/test/TestNumPageView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router