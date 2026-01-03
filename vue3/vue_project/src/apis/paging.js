import request from "@/utils/request"

// 启动实验
const startPaging = (data) => {
    return request({
        url: '/api/use/start',
        method: 'post',
        data,
        headers: {
            'Content-Type': 'application/json'
        }
    })
}

// 获取所有实验记录
const getAllPages = () => {
    return request({
        url: '/api/page/select/all',
        method: 'get'
    })
}

// 根据 ID 获取实验数据
const getTestNum = (id) => {
    return request({
        url: `/api/testNum/select/${id}`,
        method: 'get'
    })
}

// 删除实验
const deletePage = (id) => {
    return request({
        url: `/api/page/delete/${id}`,
        method: 'get'
    })
}

const testPage = (params) => {
    return new Promise((resolve, reject) => {
        request({
            url: '/api/use/page',
            method: 'get',
            params: {
                pageNo: params.pageNo,
                pageSize: params.pageSize,
                username: params.username || ''
            }
        }).then(response => {
            console.log('testPage API 完整响应:', response)
            resolve(response)
        }).catch(error => {
            console.error('testPage API 请求失败:', error)
            reject(error)
        })
    })
}
const testNumDeleteById=(params)=>{
    return request({
        url: `/use/testNum/delete/${params.id}`,
        method: 'get',
    })
}
const testPaging = (data) => {
    return request({
        url: '/use/start',
        method: 'post',
        data,
        headers: {
            'Content-Type': 'application/json'
        }
    })
}

// 根据ID查询单个测试数据
const testSelectById = (params) => {
    return request({
        url: `/use/testNum/select/${params.id}`,
        method: 'get'
    })
}

// 开始测试（重演功能）
const testStart = (data) => {
    return request({
        url: '/use/start',
        method: 'post',
        data,
        headers: {
            'Content-Type': 'application/json'
        }
    })
}

export{
    startPaging,
    getAllPages,
    getTestNum,
    deletePage,
    testPage,
    testNumDeleteById,
    testPaging,
    testSelectById,
    testStart
}
