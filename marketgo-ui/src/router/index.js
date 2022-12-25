import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)
export const constantRoutes = [
    {
        path: '/login',
        name: 'login',
        component: (resolve) => require(['@/views/login'], resolve),
    },
    {
        path: '/',
        name: 'index',
        title: '首页',
        component: (resolve) => require(['@/views/index'], resolve),
    },
    {
        path: '/index',
        name: 'home',
        component: (resolve) => require(['@/views/home'], resolve),
        redirect: '/index/homepage',
        children: [
            {
                path: 'homepage',
                name: 'homepage',
                title: '客户群详情',
                component: (resolve) => require(['@/views/homepage'], resolve),
            },
            {
                path: 'groupdetails',
                name: 'groupdetails',
                title: '客户群详情',
                component: (resolve) => require(['@/views/groupdetails'], resolve),
            },
            {
                path: 'customerdetails',
                name: 'customerdetails',
                title: '客户群详情',
                component: (resolve) => require(['@/views/customerdetails'], resolve),
            },
            {
                path: 'channelcode',
                name: 'channelcode',
                title: '渠道活码',
                component: (resolve) => require(['@/views/channelcode'], resolve),
            },
            {
                path: 'channelcodedetails',
                name: 'channelcodedetails',
                title: '活码详情',
                component: (resolve) => require(['@/views/channelcodedetails'], resolve),
            },
            {
                path: 'addchannelcode',
                name: 'addchannelcode',
                title: '新建渠道活码',
                component: (resolve) => require(['@/views/addchannelcode'], resolve),
            },
            
            {
                path: 'storeactivecode',
                name: 'storeactivecode',
                title: '门店活码',
                component: (resolve) => require(['@/views/storeactivecode'], resolve),
            },
            {
                path: 'drainagelink',
                name: 'drainagelink',
                title: '引流链接',
                component: (resolve) => require(['@/views/drainagelink'], resolve),
            },
            {
                path: 'customerlist',
                name: 'customerlist',
                title: '客户列表',
                component: (resolve) => require(['@/views/customerlist'], resolve),
            },
            {
                path: 'customergrouplist',
                name: 'customergrouplist',
                title: '客户群列表',
                component: (resolve) => require(['@/views/customergrouplist'], resolve),
            },
            {
                path: 'masscustomer',
                name: 'masscustomer',
                title: '群发客户',
                component: (resolve) => require(['@/views/masscustomer'], resolve),
                /*redirect: '/index/masscustomer',
                children: [
                    {
                        path: 'masscustomer/masscustomer-add',
                        name: 'masscustomer-add',
                        title: '新建群发客户',
                        component: (resolve) => require(['@/views/masscustomer-add'], resolve),
                    }
                ]*/
            },
            {
                path: 'masscustomer-add',
                name: 'masscustomer-add',
                title: '新建群发客户',
                component: (resolve) => require(['@/views/masscustomer-add'], resolve),
            },
            {
                path: 'masscustomer-detail',
                name: 'masscustomer-detail',
                title: '群发客户详情',
                component: (resolve) => require(['@/views/masscustomer-detail'], resolve),
            },
            {
                path: 'masscustomerbase',
                name: 'masscustomerbase',
                title: '群发客户群',
                component: (resolve) => require(['@/views/masscustomerbase'], resolve),
            },
            {
                path: 'masscustomerbase-add',
                name: 'masscustomerbase-add',
                title: '新建群发客户群',
                component: (resolve) => require(['@/views/masscustomerbase-add'], resolve),
            },
            {
                path: 'masscustomerbase-detail',
                name: 'masscustomerbase-detail',
                title: '群发客户群详情',
                component: (resolve) => require(['@/views/masscustomerbase-detail'], resolve),
            },
            {
                path: 'sendgroupfriends',
                name: 'sendgroupfriends',
                title: '群发朋友圈',
                component: (resolve) => require(['@/views/sendgroupfriends'], resolve),
            },
            {
                path: 'sendgroupfriends-add',
                name: 'sendgroupfriends-add',
                title: '新建群发朋友圈',
                component: (resolve) => require(['@/views/sendgroupfriends-add'], resolve),
            },
            {
                path: 'sendgroupfriends-detail',
                name: 'sendgroupfriends-detail',
                title: '群发朋友圈详情',
                component: (resolve) => require(['@/views/sendgroupfriends-detail'], resolve),
            },
            {
                path: 'membermanagement',
                name: 'membermanagement',
                title: '成员管理',
                component: (resolve) => require(['@/views/membermanagement'], resolve),
            },
            {
                path: 'permissionmanagement',
                name: 'permissionmanagement',
                title: '权限管理',
                component: (resolve) => require(['@/views/permissionmanagement'], resolve),
            },
            {
                path: 'cdpsettings',
                name: 'cdpsettings',
                title: '数据接入',
                component: (resolve) => require(['@/views/cbd/list'], resolve),
            },
            {
                path: 'cdpsettings-set',
                name: 'cdpsettings-set',
                title: '数据接入配置',
                component: (resolve) => require(['@/views/cbd/setting'], resolve),
            }
        ]
    },
    {
        path: '/accountauthorization',
        name: 'accountauthorization',
        title: '账号授权',
        component: (resolve) => require(['@/views/accountauthorization'], resolve)
    }
]
export default new Router({
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes
})