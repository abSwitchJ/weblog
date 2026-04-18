<template>
    <!-- 后台根容器：纵向 flex 布局；admin-root class 供全局样式作用域使用 -->
    <div class="admin-root flex flex-col min-h-screen">
        <!-- 顶部导航栏（含菜单 + 工具区） -->
        <AdminHeader />

        <!-- 主内容区域 -->
        <div class="admin-main flex-1">
            <router-view v-slot="{ Component }">
                <Transition name="fade">
                    <KeepAlive :max="10">
                        <component :is="Component"></component>
                    </KeepAlive>
                </Transition>
            </router-view>
        </div>

        <!-- 页脚 -->
        <AdminFooter />
    </div>
</template>

<script setup>
import AdminFooter from './components/AdminFooter.vue';
import AdminHeader from './components/AdminHeader.vue';
</script>

<style scoped>
/* 内容区域过渡动画：淡入淡出效果 */
.fade-enter-from {
    opacity: 0;
}

.fade-enter-to {
    opacity: 1;
}

.fade-leave-from {
    opacity: 1;
}

.fade-leave-to {
    opacity: 0;
}

.fade-leave-active {
    transition: all 0.3s;
}

.fade-enter-active {
    transition: all 0.3s;
    transition-delay: 0.3s;
}
</style>
