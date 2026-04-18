import axios from "@/axios";

// 获取标签列表
export function getTagList(data) {
    return axios.post("/tag/list", data)
}

// 获取标签下文章列表（按名称）
export function getTagArticlePageList(data) {
    return axios.post("/tag/article/list/by-name", data)
}

// 按标签名解析出中英文名称对
export function resolveTagName(data) {
    return axios.post("/tag/name/resolve", data)
}
