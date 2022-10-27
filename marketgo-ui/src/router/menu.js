const constantRoutes = [
    {
        path: '/index',
        name: 'home',
        title: '首页',
    },
    {
        path: 'channelcode',
        name: 'channelcode',
        title: '渠道活码',

    },
    {
        path: 'storeactivecode',
        name: 'storeactivecode',
        title: '门店活码',

    },
    {
        path: 'drainagelink',
        name: 'drainagelink',
        title: '引流链接',

    },
    {
        path: 'management',
        name: 'management',
        title: '客户管理',

        children: [
            {
                path: 'customerlist',
                name: 'customerlist',
                title: '客户列表',

            },
            {
                path: 'customergrouplist',
                name: 'customergrouplist',
                title: '客户群列表',
            },
        ],
    },
    {
        path: 'marketingplan',
        name: 'marketingplan',
        title: '营销计划',
        children: [
            {
                path: 'masscustomer',
                name: 'masscustomer',
                title: '群发客户'
            },
            {
                path: 'masscustomerbase',
                name: 'masscustomerbase',
                title: '群发客户群'
            },
            {
                path: 'sendgroupfriends',
                name: 'sendgroupfriends',
                title: '群发朋友圈'
            },
        ],
    },
    {
        path: 'settings',
        name: 'settings',
        title: '系统设置',
        children: [
            {
                path: 'membermanagement',
                name: 'membermanagement',
                title: '成员管理',
            },
            {
                path: 'permissionmanagement',
                name: 'permissionmanagement',
                title: '权限管理',
            }
        ]
    },
    // {
    //     path: 'accountauthorization',
    //     name: 'accountauthorization',
    //     title: '账号授权'
    // },
]
export default constantRoutes