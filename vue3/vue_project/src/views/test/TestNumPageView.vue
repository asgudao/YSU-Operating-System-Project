<script setup>
import {ref,onMounted} from 'vue'
import {useRouter} from 'vue-router'
import { ElMessage } from 'element-plus'
import {testPage,testNumDeleteById, testSelectById, testStart} from '@/apis/paging'

const username = ref('')
const pageNo=ref(1)
const pageSize=ref(8)
const total=ref(0)
const router= useRouter()

const tableData = ref([])

//根据行索引返回样式名称
const tableRowClassName = ({row,rowIndex}) => {
  if (rowIndex%4 === 0) {
    return 'warning-row'
  } else if (rowIndex%4 === 2) {
    return 'success-row'
  }else{
    return ''
  }
}

const searchTestPage= async (pageNo,pageSize,username)=>{
    try {
        const data = {
            pageNo,
            pageSize,
            username
        }
        console.log('发送请求:', data)
        const result  = await testPage(data)
        console.log('API响应:', result)
        
        // 根据后端 JsonResult 格式解析响应
        if (result && result.data) {
            // 检查后端返回状态
            if (result.data.state === 0) {
                // 成功状态，解析数据
                let rawData = []
                let rawTotal = 0
                
                if (result.data.data && result.data.data.list && typeof result.data.data.total !== 'undefined') {
                    // 格式为 {data: {state: 0, message: '', data: {list: [...], total: ...}}}
                    rawData = result.data.data.list
                    rawTotal = result.data.data.total
                } else if (Array.isArray(result.data.data)) {
                    // 格式为 {data: {state: 0, message: '', data: [...]}}
                    rawData = result.data.data
                    rawTotal = result.data.data.length
                } else {
                    console.error('数据格式错误:', result.data.data)
                    ElMessage.error('数据格式错误')
                    return
                }
                
                // 数据字段映射，解决后端字段名与前端预期不一致的问题
                tableData.value = rawData.map(item => {
                    // 处理字段名映射
                    return {
                        ...item,
                        // 确保 useTLB 字段存在
                        useTLB: item.useTLB || item.use_TLB || 0,
                        // 确保 handleLosePage 字段存在，处理拼写差异
                        handleLosePage: item.handleLosePage || item.handleLosepage || 0,
                        // 确保 TLBNum 字段存在
                        TLBNum: item.TLBNum || item.tlbNum || 0
                    }
                })
                
                total.value = rawTotal
                console.log('数据已加载:', tableData.value.length, '条记录')
            } else {
                // 失败状态
                console.error('后端返回错误:', result.data.message)
                ElMessage.error(result.data.message || '获取数据失败')
            }
        } else {
            console.error('响应格式错误:', result)
            ElMessage.error('响应格式错误')
        }
    } catch (error) {
        console.error('获取测试列表失败:', error)
        if (error.response) {
            console.error('响应状态:', error.response.status)
            console.error('响应数据:', error.response.data)
            ElMessage.error(`请求失败: ${error.response.status} - ${JSON.stringify(error.response.data)}`)
        } else if (error.request) {
            console.error('请求未响应:', error.request)
            ElMessage.error('后端服务器未响应，请检查后端是否启动')
        } else {
            ElMessage.error(`请求错误: ${error.message}`)
        }
    }
}

onMounted(()=>{
    // 从localStorage中读取保存的pageSize
    const savedPageSize = localStorage.getItem('testPageSize'); 
    if (savedPageSize) {
        pageSize.value = parseInt(savedPageSize);
    }
    searchTestPage(pageNo.value,pageSize.value,username.value)
})

const handleSearch=()=>{
    searchTestPage(pageNo.value,pageSize.value,username.value)
}

//每页数量发生改变回调函数
const handleSizeChange=(value)=>{
    console.log(`pageSize=${value}`)
    // 将选择的pageSize保存到localStorage
    localStorage.setItem('testPageSize', value.toString());
    searchTestPage(pageNo.value,value,username.value)
}
//页码发生改变回调函数
const handleCurrentChange=(value)=>{
    console.log(`pageNo=${value}`)
    searchTestPage(value,pageSize.value,username.value)
}


//确认删除回调函数
const handleConfirmDelete= async (id)=>{
    try {
        console.log(`id=${id}`)
        //请求后端api做删除操作
        await testNumDeleteById({id})
        ElMessage.success("删除测试成功")
        //要重新查询
        searchTestPage(pageNo.value,pageSize.value,username.value)  
    } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
    }
}

// 确认重演回调函数
const handleConfirmReplay= async (id)=>{
    try {
        console.log(`id=${id}`)
        // 根据ID查询测试数据
        const testData = await testSelectById({id})
        console.log('获取到的测试数据:', testData)
        
        if (testData && testData.data) {
            // 调用启动接口开始测试
            await testStart(testData.data)
            ElMessage.success("测试已开始")
            // 可以跳转到运行页面
            router.push('/run')
        } else {
            ElMessage.error('获取测试数据失败')
        }
    } catch (error) {
        console.error('重演测试失败:', error)
        ElMessage.error('重演测试失败')
    }
}
</script>

<template>
    <div class="test-container">
        <h1>测试管理列表</h1>
        <div class="div1">
        <el-table
            :data="tableData"
            style="width: 100%"
            :row-class-name="tableRowClassName"
        >
            <el-table-column fixed="left" prop="id" label="编号" width="120" />
            <el-table-column prop="pageNum" label="页数" width="120" />
            <el-table-column prop="useTLB" label="是否使用快表" width="120" />
            <el-table-column prop="TLBNum" label="快表数据数" width="120" />
            <el-table-column prop="visitMemory" label="访问内存时间" width="120" />
            <el-table-column prop="visitTLB" label="访问快表时间" width="120" />
            <el-table-column prop="handleLosePage" label="处理缺页时间" width="120" />
            <el-table-column prop="inputNum" label="输入内容" width="180" />
            <el-table-column prop="createTime" label="创建时间" width="120" />
            <el-table-column fixed="right" label="Operations" width="200">
                <template #default="scope">
                    <el-button link type="success" size="small" @click="handleConfirmReplay(scope.row.id)">重演</el-button>
                    <el-popconfirm
                        confirm-button-text="确定"
                        cancel-button-text="取消"
                        icon="WarningFilled"
                        icon-color="#626AEF"
                        title="是否确认删除此测试?"
                        @confirm="handleConfirmDelete(scope.row.id)"
                    >
                        <template #reference>
                            <el-button link type="danger" size="small">删除</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <!-- 分页插件 -->
    <el-pagination
      v-model:current-page="pageNo"
      v-model:page-size="pageSize"
      :page-sizes="[2, 4, 6, 8]"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    </div>
</template>

<style scoped>
    /* 主容器样式 - 紧凑布局 */
    .test-container {
        padding: 15px;
        max-width: 1400px;
        margin: 0 auto;
        min-height: calc(100vh - 100px);
    }
    
    /* 标题样式 */
    h1 {
        font-size: 22px;
        color: #303133;
        margin-bottom: 15px;
        font-weight: 600;
        text-align: left;
        padding-left: 10px;
        border-left: 4px solid #409EFF;
    }
    
    /* 表格容器样式 */
    .div1 {
        margin-bottom: 15px;
        background: white;
        border-radius: 6px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        overflow: hidden;
    }
    
    /* 搜索输入框样式 */
    .searchInput {
        width: 35%; 
        margin-right: 20px;
        margin-bottom: 10px;
    }
    
    /* 表格样式穿透 */
    :deep(.el-table) {
        border: none;
        font-size: 14px;
    }
    
    :deep(.el-table__header-wrapper th) {
        background-color: #f8f9fa;
        font-weight: 600;
        color: #303133;
        border-bottom: 1px solid #e9ecef;
        padding: 10px;
    }
    
    :deep(.el-table__body-wrapper tr) {
        transition: all 0.2s;
    }
    
    :deep(.el-table__body-wrapper tr:hover) {
        background-color: #f1f3f4;
    }
    
    :deep(.el-table__body-wrapper td) {
        padding: 10px;
    }
    
    :deep(.el-table .warning-row) {
        --el-table-tr-bg-color: var(--el-color-warning-light-9);
    }
    
    :deep(.el-table .success-row) {
        --el-table-tr-bg-color: var(--el-color-success-light-9);
    }
    
    /* 分页组件样式 */
    :deep(.el-pagination) {
        display: flex;
        justify-content: flex-end;
        margin-top: 15px;
        font-size: 14px;
    }
    
    /* 按钮样式穿透 */
    :deep(.el-button) {
        transition: all 0.2s;
        font-size: 13px;
        padding: 5px 10px;
    }
    
    :deep(.el-button:hover) {
        transform: translateY(-1px);
    }
    
    /* 操作列按钮间距 */
    :deep(.el-table-column--fixed-right .el-button) {
        margin-right: 5px;
    }
    
    /* 气泡确认框样式 */
    :deep(.el-popconfirm__main) {
        font-size: 13px;
    }
    
    /* 响应式布局优化 */
    @media (max-width: 768px) {
        .test-container {
            padding: 10px;
        }
        
        h1 {
            font-size: 18px;
            margin-bottom: 10px;
        }
        
        .searchInput {
            width: 100%;
            margin-right: 0;
        }
        
        :deep(.el-table) {
            font-size: 12px;
        }
        
        :deep(.el-table__header-wrapper th) {
            padding: 8px;
        }
        
        :deep(.el-table__body-wrapper td) {
            padding: 8px;
        }
    }
</style>