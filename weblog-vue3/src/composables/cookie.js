import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()

// ============================== Token 令牌 ==============================

// 存储在 Cookie 中的 Token 的 key
const TOKEN_KEY = 'Authorization'

// 与后端 JWT 有效期保持一致：24 小时
const TOKEN_TTL_MS = 24 * 60 * 60 * 1000

// 获取 Token 值
export function getToken() {
    return cookie.get(TOKEN_KEY)
}

// 设置 Token 到 Cookie 中
export function setToken(token) {
    return cookie.set(TOKEN_KEY, token, {
        path: '/',
        expires: new Date(Date.now() + TOKEN_TTL_MS),
    })
}

// 删除 Token
export function removeToken() {
    return cookie.remove(TOKEN_KEY, { path: '/' })
}