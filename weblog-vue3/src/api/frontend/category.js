import axios from "@/axios";

// 获取分类列表
export function getCategoryList(data) {
    return axios.post("/category/list", data)
}

// 获取分类-文章列表
export function getCategoryArticlePageList(data) {
    return axios.post("/category/article/list/by-name", data)
}

// 按分类名解析出中英文名称对
export function resolveCategoryName(data) {
    return axios.post("/category/name/resolve", data)
}


