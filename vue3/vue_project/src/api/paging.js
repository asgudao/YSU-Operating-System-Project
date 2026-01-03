import axios from 'axios'

const request = axios.create({
    baseURL: '/api', // 会被 Vite 代理到 http://localhost:8081
    timeout: 5000
})

// 启动实验
export const startPaging = (data) => request.post('/start', data)

// 获取所有实验记录
export const getAllPages = () => request.get('/page/select/all')

// 根据 ID 获取实验数据
export const getTestNum = (id) => request.get(`/testNum/select/${id}`)

// 删除实验
export const deletePage = (id) => request.get(`/page/delete/${id}`)
