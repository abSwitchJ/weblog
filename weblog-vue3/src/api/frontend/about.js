import axios from "@/axios";

// 获取关于我页面 HTML
export function getAboutDetail() {
    return axios.post("/blog/settings/about")
}
