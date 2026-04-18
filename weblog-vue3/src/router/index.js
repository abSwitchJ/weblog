import Index from '@/pages/frontend/index.vue'
import ArchiveList from '@/pages/frontend/archive-list.vue'
import CategoryList from '@/pages/frontend/category-list.vue'
import CategoryArticleList from '@/pages/frontend/category-article-list.vue'
import TagList from '@/pages/frontend/tag-list.vue'
import TagArticleList from '@/pages/frontend/tag-article-list.vue'
import ArticleDetail from '@/pages/frontend/article-detail.vue'
import About from '@/pages/frontend/about.vue'
import NotFound from '@/pages/frontend/404.vue'
import Login from '@/pages/admin/login.vue'
import AdminIndex from '@/pages/admin/index.vue'
import AdminArticleList from '@/pages/admin/article-list.vue'
import AdminCategoryList from '@/pages/admin/category-list.vue'
import AdminTagList from '@/pages/admin/tag-list.vue'
import AdminBlogSettings from '@/pages/admin/blog-settings.vue'
import { createRouter, createWebHistory } from 'vue-router'
import Admin from '@/layouts/admin/admin.vue'

// 统一在这里声明所有路由
const routes = [
    {
        path: '/', // 路由地址，首页
        component: Index, // 对应组件
        meta: {
            titleKey: 'home'
        }
    },
    {
        path: '/archive', // 归档页
        component: ArchiveList,
        meta: {
            titleKey: 'archive'
        }
    },
    {
        path: '/category/list', // 分类列表页
        component: CategoryList,
        meta: {
            titleKey: 'categoryList'
        }
    },
    {
        path: '/category/:name', // 分类文章页
        component: CategoryArticleList,
        meta: {}
    },
    {
        path: '/tag/list', // 标签列表页
        component: TagList,
        meta: {
            titleKey: 'tagList'
        }
    },
    {
        path: '/tag/:name', // 标签文章列表页
        component: TagArticleList,
        meta: {}
    },
    {
        path: '/article/:slug([\\w-]+).html', // 文章详情页
        component: ArticleDetail,
        meta: {}
    },
    {
        path: '/about', // 关于我
        component: About,
        meta: {
            titleKey: 'about'
        }
    },
    {
        path: '/login', // 登录页
        component: Login,
        meta: {
            titleKey: 'login'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound,
        meta: {
            titleKey: 'notFound'
        }
    },
    {
        path: "/admin", // 后台首页
        component: Admin,
        // 使用到 admin.vue 布局的，都需要放置在其子路由下面
        children: [
            {
                path: "/admin/index",
                component: AdminIndex,
                meta: {
                    title: '仪表盘'
                }
            },
            {
                path: "/admin/article/list",
                component: AdminArticleList,
                meta: {
                    title: '文章管理'
                }
            },
            {
                path: "/admin/category/list",
                component: AdminCategoryList,
                meta: {
                    title: '分类管理'
                }
            },
            {
                path: "/admin/tag/list",
                component: AdminTagList,
                meta: {
                    title: '标签管理'
                }
            },
            {
                path: "/admin/blog/settings",
                component: AdminBlogSettings,
                meta: {
                    title: '博客设置'
                }
            },
        ]
        
    }
]

// 创建路由
const router = createRouter({
    // 指定路由的历史管理方式，hash 模式指的是 URL 的路径是通过 hash 符号（#）进行标识
    history: createWebHistory(),
    // routes: routes 的缩写
    routes, 
    // 每次切换路后，页面滚动到顶部
    scrollBehavior() {
        return { top: 0 }
    }
})

// 暴露出去
export default router

