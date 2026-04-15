import axios from "@/axios";

// 获取文章归档数据
export function getArchiveList() {
    return axios.post("/archive/list")
}
