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

export{
    startPaging,
    getAllPages,
    getTestNum,
    deletePage
}
