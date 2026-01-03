import request from "@/utils/request"

//分页查询信息(get请求)
const testPage = (params) => {
    return request({
        url: "/api/use/page",
        method: "get",
        //简化属性
        params
    })
}

//根据id删除
const testDeleteById = (params) => {
    return request({
        url: `/api/use/testNum/delete/${params.id}`,
        method: "get"
    })
}


export{
    testDeleteById,
    testPage
}