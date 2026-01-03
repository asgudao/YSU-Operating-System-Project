<template>
<router-view></router-view>
</template>

<script>
export default {
}
</script>

<style>
/* 全局样式重置和基础设置 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html, body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif;
    font-size: 14px;
    color: #303133;
    background-color: #f0f2f5;
    line-height: 1.5;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    overflow-x: hidden;
}

/* 标题样式 */
h1, h2, h3, h4, h5, h6 {
    color: #303133;
    font-weight: 600;
    margin: 0;
}

/* 链接样式 */
a {
    color: #409eff;
    text-decoration: none;
    transition: color 0.3s;
}

a:hover {
    color: #66b1ff;
}

/* 表单元素基础样式 */
input, select, textarea, button {
    font-family: inherit;
    font-size: inherit;
    outline: none;
}

/* 禁用状态样式 */
:disabled {
    cursor: not-allowed;
    opacity: 0.6;
}

/* 页面过渡动画 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

/* 滚动条全局样式 */
::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

::-webkit-scrollbar-track {
    background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}
</style>