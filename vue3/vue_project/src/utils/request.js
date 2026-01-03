import axios from 'axios'
import { ElMessageBox, ElMessage } from 'element-plus'
import qs from 'qs'

// axios创建实例对象(axios对象)
const service = axios.create({
    // 设置baseRUL
    // baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
    // axios不带cookie
    withCredentials: false, // send cookies when cross-domain requests
    // 设置header的Content-Type类型
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    // 主要用于序列化params（params 参数用在get请求上）
    paramsSerializer: params => {
        if (params) {
            // qs.stringify 的{arrayFormat: 'repeat'} 可以对数组转换格式处理成 ids=1&ids=2
            // ids=[11,12,13]  -> ids=11&ids=12&ids=13
            return qs.stringify(params, { arrayFormat: 'repeat' });
        }
    },
    /* 设置请求超时时间*/
    timeout: 30000, // request timeout
    // `transformRequest` 允许在向服务器发送前，修改请求数据
    // 只能用在 'PUT', 'POST' 和 'PATCH' 这几个请求方法
    transformRequest: (data, headers) => {
        if (headers['Content-Type']) {
            if ((headers['Content-Type']).indexOf('multipart/form-data') > -1) { // 上传文件处理
                headers['Content-Type'] = ""
                const formData = new FormData()
                for (let key in data) {
                    formData.append(key, data[key])
                }
                return formData
            } else if ((headers['Content-Type']).indexOf('application/json') > -1) { // 请求json数据格式处理
                return JSON.stringify(data)
            } else { // 其他都是按照 x-www-form-urlencoded数据格式处理
                // qs.stringify 的{arrayFormat: 'repeat'} 可以对数组转换格式处理成 ids=1&ids=2
                return qs.stringify(data, { arrayFormat: 'repeat' })
            }
        } else { // headers['Content-Type'] 不存在时，按照 x-www-form-urlencoded 数据格式处理
            // qs.stringify 的{arrayFormat: 'repeat'} 可以对数组转换格式处理成 ids=1&ids=2
            return qs.stringify(data, { arrayFormat: 'repeat' })
        }
    }
})

// 响应拦截器
service.interceptors.response.use(
    response => {
        console.log('响应拦截器收到响应:', response)
        
        // 直接返回响应数据，由业务代码决定如何解析
        return response
    },
    error => {
        console.error('响应拦截器错误:', error)
        let message = '请求失败'
        if (error.response) {
            // 服务器返回了错误状态码
            switch (error.response.status) {
                case 400:
                    message = '请求参数错误'
                    break
                case 401:
                    message = '未授权，请登录'
                    break
                case 403:
                    message = '禁止访问'
                    break
                case 404:
                    message = '请求地址不存在'
                    break
                case 500:
                    message = '服务器内部错误'
                    break
                default:
                    message = `请求失败 (${error.response.status})`
            }
            
            if (error.response.data && typeof error.response.data === 'string') {
                message = error.response.data
            } else if (error.response.data && error.response.data.message) {
                message = error.response.data.message
            }
        } else if (error.request) {
            // 请求发送了但没有收到响应
            message = '服务器无响应，请检查后端是否启动'
        } else {
            // 请求配置错误
            message = `请求错误: ${error.message}`
        }
        
        ElMessage.error(message)
        return Promise.reject(error)
    }
)

export default service
