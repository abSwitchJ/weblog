import axios from "@/axios";

// 登录接口
// 增加 captchaId 参数, 默认为空字符串
export function login(username, password, captchaId = '') {
    return axios.post("/login", {username, password, captchaId})
}

// 获取登录用户信息
export function getUserInfo() {
    return axios.post("/admin/user/info")
}

// 修改用户密码
export function updateAdminPassword(data) {
    return axios.post("/admin/password/update", data)
}