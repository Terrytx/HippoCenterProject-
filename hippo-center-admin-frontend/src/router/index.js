import { createRouter, createWebHistory } from 'vue-router'
import useUserStore from '../stores/user'
import Home from '../views/Home.vue'

// router/index.js
const routes = [
    {
        path: '/',
        component: Home,
        meta: { requiresAuth: true },
        children: [
            {
                path: '',  // 空路徑
                redirect: '/login'  // 重定向到登入
            },
            {
                path: 'navigation',
                name: 'Navigation',
                component: () => import('../views/B3Tours/V1Navigation.vue'),
                meta: { requiresAuth: true }
            },
{
                path: 'event',
                name: 'Event',
                component: () => import('../views/V2Event.vue'),
                children: [
                    {
                        path: 'event-management',
                        name: 'EventManagement',
                        component: () => import('../views/B1Event/A1EventManagement.vue')
                    },
                    {
                        path: 'event-publish-status',
                        name: 'EventPublishStatus',
                        component: () => import('../views/B1Event/A1EventPublishStatus.vue')
                    },
                    {
                        path: 'ticket-management',
                        name: 'TicketManagement',
                        component: () => import('../views/B1Event/B1TicketManagement.vue')
                    },
                    {
                        path: 'dash-board',
                        name: 'DashBoard',
                        component: () => import('../views/B1Event/C1DashBoard.vue')
                    }
                ]
            },
            {
                path: 'venue',
                name: 'venue',
                component: () => import('../views/V3Venue.vue'),
                meta: { requiresAuth: true },
                children: [
                    {
                        path: 'v1-rental-venue',
                        name: 'RentalVenue',
                        component: () => import('../views/B5Venue/V1RentalVenue.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path: 'v2-rental',
                        name: 'RentalSchedule',
                        component: () => import('../views/B5Venue/V2Rental.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path: 'v3-rental',
                        name: 'RentalApplication',
                        component: () => import('../views/B5Venue/V3Rental.vue'),
                        meta: { requiresAuth: true }
                    },

                ]
            },
            {
                path: 'shop',
                name: 'shop',
                component: () => import('../views/V4Shop.vue'), // 這是「商店管理」的主頁
                meta: { requiresAuth: true },
                children: [
                    {
                        path: 'products',
                        name: 'AdminProducts',
                        component: () => import('../views/B2Shop/ShopProducts.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path: 'promotions',
                        name: 'AdminPromotions',
                        component: () => import('../views/B2Shop/ShopPromotions.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path: 'orders',
                        name: 'AdminOrders',
                        component: () => import('../views/B2Shop/ShopOrders.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path: 'carts',
                        name: 'AdminCarts',
                        component: () => import('../views/B2Shop/ShopCarts.vue'),
                        meta: { requiresAuth: true }
                    },
                    {
                        path: 'reports',
                        name: 'AdminReports',
                        component: () => import('../views/B2Shop/ShopReports.vue'),
                        meta: { requiresAuth: true }
                    }
                ]
            },
            {
                path: 'members',
                name: 'Members',
                component: () => import('../views/V5Members.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: 'news',
                name: 'News',
                component: () => import('../views/B3News/V6News.vue'),
                meta: { requiresAuth: true }
            },
            {
                path: 'faq',
                name: 'FAQ',
                component: () => import('../views/V7FAQ.vue'),
                meta: { requiresAuth: true }
            },]
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { requiresAuth: false }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})


// 路由守衛：檢查是否已登入
router.beforeEach((to, from, next) => {
    const userStore = useUserStore();

    if (to.meta.requiresAuth) {
        if (!userStore.isLogin) {
            console.log('用戶未登入，重定向到登入頁面');
            next({ path: '/login' });
        } else {
            console.log('用戶已登入，繼續導航');
            next();
        }
    } else {
        console.log('不需要身份驗證的路由，直接導航');
        next();
    }
});

export default router