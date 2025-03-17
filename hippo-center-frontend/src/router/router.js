import { createRouter, createWebHistory } from 'vue-router';
import useUserStore from '@/stores/user.js';
import Header from '@/components/footers/Header.vue';
import Footer from '@/components/footers/Footer.vue';
import Login from '@/views/b4member/login/Login.vue';
import register from '@/views/b4member/login/Register.vue';
import person from '@/views/b4member/person/person.vue';
import Policy from '@/views/b4member/login/policy.vue';
import Terms from '@/views/b4member/login/Terms.vue';
import tour from '../views/b4member/tour/tour.vue';
import Orders from '../views/b4member/order/Orders.vue';
import ticket from '../views/b4member/ticket/ticket.vue';
import product from '../views/b4member/mylove/product.vue';
import event from '../views/b4member/mylove/event.vue';
import SentEmail from '../views/b4member/login/SentEmail.vue';
import Reset from '../views/b4member/login/Reset.vue';
import ProductDetail from '@/views/b2shopcart/Product/ProductDetail/ProductDetail.vue';
import ShopCart from '@/views/b2shopcart/Product/ProductList.vue';

const routes = [
    { path: '/header', component: Header },
    { path: '/footer', component: Footer },
    { path: '/secure/login', component: Login },
    { path: '/secure/register', component: register },
    { path: '/secure/sent', component: SentEmail },
    {
        path: '/secure/person',
        component: person,
        meta: { requiresAuth: true }  // 需要驗證的頁面
    },
    {
        path: '/secure/tour',
        component: tour,
        meta: { requiresAuth: true }  // 需要驗證的頁面
    },
    {
        path: '/secure/Orders',
        component: Orders,
        meta: { requiresAuth: true }  // 需要驗證的頁面
    },
    {
        path: '/secure/ticket',
        component: ticket,
        meta: { requiresAuth: true }  // 需要驗證的頁面
    },
    {
        path: '/secure/mylove/p',
        component: product,
        meta: { requiresAuth: true }  // 需要驗證的頁面
    },
    {
        path: '/secure/mylove/e',
        component: event,
        meta: { requiresAuth: true }  // 需要驗證的頁面
    },
    {
        path: "/user/changePassword/:uuid", // 這是用戶點擊郵件中的連結後的網址
        name: "Reset", // 給路由命名
        component: Reset, // 對應的組件
        props: (route) => ({
            uuid: route.params.uuid, // 使用 params 中的 uuid 參數
        }),
    },
    { path: '/secure/policy', component: Policy },
    { path: '/secure/terms', component: Terms },
    //首頁
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/Home.vue')
    },
    //節目相關路由
    {//event使用的路由
        path: '/event',
        name: 'Event',
        component: () => import('@/views/b1event/Event.vue'),
    },

    //節目總覽相關路由
    {
        path: '/event/allevent',
        name: 'AllEvent',
        component: () => import('@/views/b1event//event/AllEvent.vue'),
    },

    //節目回顧相關路由
    {
        path: '/event/eventreview',
        name: 'EventReview',
        component: () => import('@/views/b1event/event/EventReview.vue'),
    },
    //節目預告相關路由
    {
        path: '/event/eventupcoming',
        name: 'EventUpcoming',
        component: () => import('@/views/b1event/event/EventUpcoming.vue'),
    },
    //節目資訊相關路由
    {
        path: '/event/eventdetail/:id',
        name: 'EventDetail',
        component: () => import('@/views/b1event/event/EventDetail/EventDetail.vue'),
        props: true
    },
    {   // 場地服務相關路由 
        path: '/venue',
        name: 'Venue',
        component: () => import('@/views/b5venue/Venue.vue'),
        children: [{  // 新增子路由
            path: 'booking',
            name: 'VenueBooking',
            component: () => import('@/views/b5venue/venue/B06Booking.vue')
        }]
    },
    // {// 觀眾服務相關路由
    //     path: '/venue/service',
    //     name: 'VenueService',
    //     component: () => import('@/views/b5venue/Service.vue')
    // },

    // 計畫行程的路由
    {
        path: '/b3visit',
        name: 'Plan',
        component: () => import('@/views/b3visit/plan.vue')
    },

    // 導覽的路由
    {
        path: '/b3tours',
        name: 'Calendar',
        component: () => import('@/views/b3tours/calendar.vue')
    },


    // 最新消息的路由
    {
        path: '/b3news',
        name: 'NewsMain',
        component: () => import('@/views/b3news/newsMain.vue'),
        redirect: '/b3news/newsoverview', // 路由重新導向
        children: [
            {
                path: 'newsoverview', //子路由
                name: 'NewsOverview',
                component: () => import('@/views/b3news/newsOverview.vue')
            },
            {
                path: 'news',
                name: 'News',
                component: () => import('@/views/b3news/news.vue')
            },
            {
                path: 'exhibition',
                name: 'NewsExhibition',
                component: () => import('@/views/b3news/newsExhibition.vue')
            },
            {
                path: 'venue',
                name: 'NewsVenue',
                component: () => import('@/views/b3news/newsVenue.vue')
            },
            {
                path: 'rental',
                name: 'NewsRental',
                component: () => import('@/views/b3news/newsRental.vue')
            },
            {
                path: 'tender',
                name: 'NewsTender',
                component: () => import('@/views/b3news/newsTender.vue')
            },
            {
                path: 'other',
                name: 'NewsOther',
                component: () => import('@/views/b3news/newsOther.vue')
            }
        ]

    },
    //購物車相關路由 //首頁
    { path: '/shopcart', name: 'ShopCart', component: ShopCart },
    //購物車相關路由 //商品詳細頁面
    { path: '/product/:id', name: 'ProductDetail', component: ProductDetail },
    //購物車相關路由 //購物車
    {
        path: '/cart',
        name: 'Cart',
        component: () => import('@/views/b2shopcart/Check/AddToCart.vue')
    },

    //購物車相關路由 //訂單確認                                                 
    // { path: '/venue/introduction', name: 'Orders', component: () => import('@/views/b4member/order/Orders.vue') },
    // { path: '/venue/introduction/:orderId', name: 'OrderDetail', component: () => import('@/views/b4member/order/OrderDetail.vue') },


];

const router = createRouter({
    history: createWebHistory(),
    routes,
    //換頁面回到頂端
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition;
        } else {
            return { top: 0 };
        }
    },
});


// 路由守衛：檢查是否已登入
router.beforeEach((to, from, next) => {
    const userStore = useUserStore();

    if (to.meta.requiresAuth) {
        if (!userStore.isLogin) {
            console.log('用戶未登入，重定向到登入頁面');
            // 保存當前頁面路徑
            localStorage.setItem('redirectTo', to.fullPath);
            next({ path: '/secure/login' });
        } else {
            console.log('用戶已登入，繼續導航');
            next();
        }
    } else {
        console.log('不需要身份驗證的路由，直接導航');
        next();
    }
});

export default router;
