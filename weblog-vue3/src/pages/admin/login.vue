<template>
    <Header />

    <div class="login-page" :class="{ 'dark': isDark }">
        <div class="login-card">
            <h1 class="login-title">{{ t('login.title') }}</h1>

            <el-form class="login-form" ref="formRef" :rules="rules" :model="form" @submit.prevent>
                <el-form-item prop="username">
                    <el-input size="large" v-model="form.username" :placeholder="t('login.username')"
                        :prefix-icon="User" clearable />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input size="large" type="password" v-model="form.password" :placeholder="t('login.password')"
                        :prefix-icon="Lock" clearable show-password />
                </el-form-item>
                <el-form-item>
                    <button type="button" class="login-submit-btn" :disabled="loading" @click="onSubmit">
                        <span v-if="loading" class="login-spinner"></span>
                        {{ t('login.submit') }}
                    </button>
                </el-form-item>
            </el-form>

            <div v-show="showCaptcha" class="captcha-overlay">
                <Captcha
                    ref="captchaRef"
                    :visible="showCaptcha"
                    type="SLIDER"
                    @success="onCaptchaSuccess"
                    @fail="onCaptchaFail"
                    @close="showCaptcha = false"
                />
            </div>
        </div>
    </div>

    <Footer />
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/admin/user'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useDark } from '@vueuse/core'
import { showMessage } from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import Captcha from '@/components/Captcha.vue'

const { t } = useI18n()
const isDark = useDark()
const router = useRouter()
const userStore = useUserStore()

const captchaRef = ref(null)
const showCaptcha = ref(false)
const captchaId = ref('')

const form = reactive({
    username: '',
    password: ''
})

const loading = ref(false)
const formRef = ref(null)
const rules = {
    username: [{ required: true, message: t('login.usernameRequired'), trigger: 'blur' }],
    password: [{ required: true, message: t('login.passwordRequired'), trigger: 'blur' }]
}

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) return false
        if (showCaptcha.value) {
            captchaRef.value?.refresh()
        } else {
            showCaptcha.value = true
        }
    })
}

const onCaptchaSuccess = (id) => {
    captchaId.value = id
    showCaptcha.value = false
    doLogin()
}

const onCaptchaFail = () => {
    if (showCaptcha.value) {
        captchaRef.value?.refresh()
    }
}

const doLogin = () => {
    loading.value = true
    login(form.username, form.password, captchaId.value).then((res) => {
        if (res.success) {
            showMessage(t('login.success'))
            setToken(res.data.token)
            userStore.setUserInfo()
            router.push('/admin/index')
        } else {
            showMessage(res.message, 'error')
        }
    }).finally(() => {
        loading.value = false
    })
}

function onKeyUp(e) {
    if (e.key === 'Enter') onSubmit()
}

onMounted(() => {
    document.addEventListener('keyup', onKeyUp)
})

onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
})
</script>

<style scoped>
.login-page {
    background-color: #f7f7f4;
    padding: 60px 20px 80px;
    min-height: calc(100vh - 57px);
}

.login-page.dark {
    background-color: #111;
}

.login-card {
    position: relative;
    max-width: 900px;
    margin: 0 auto;
    background: #fff;
    padding: 40px 44px;
    border-radius: 12px;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
}

.login-page.dark .login-card {
    background: #1e1e1e;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.3);
}

.login-title {
    font-family: Georgia, 'Times New Roman', "Songti SC", "SimSun", "STSong", serif;
    color: #1a1a1a;
    font-size: 1.8em;
    font-weight: 700;
    letter-spacing: 0.5px;
    text-align: center;
    margin: 0 0 32px;
}

.login-page.dark .login-title {
    color: #e8e8e8;
}

.login-form {
    max-width: 380px;
    margin: 0 auto;
}

.login-submit-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    width: 100%;
    height: 40px;
    background: #1a1a1a;
    color: #fff;
    font-size: 0.95em;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    transition: background-color 0.2s;
    font-family: inherit;
}

.login-submit-btn:hover:not(:disabled) {
    background: #333;
}

.login-submit-btn:disabled {
    cursor: not-allowed;
    opacity: 0.7;
}

.login-page.dark .login-submit-btn {
    background: #333;
}

.login-page.dark .login-submit-btn:hover:not(:disabled) {
    background: #444;
}

.login-spinner {
    width: 14px;
    height: 14px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top-color: #fff;
    border-radius: 50%;
    animation: login-spin 0.8s linear infinite;
}

@keyframes login-spin {
    to { transform: rotate(360deg); }
}

.captcha-overlay {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 100;
}

@media (max-width: 640px) {
    .login-page {
        padding: 30px 12px 40px;
    }

    .login-card {
        padding: 28px 20px;
    }

    .login-title {
        font-size: 1.5em;
        margin-bottom: 24px;
    }
}
</style>
