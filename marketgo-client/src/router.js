import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router';
import qs from 'qs'
const routes = [
  {
    path: '/',
    redirect:`/wecom/app`,
  },
  {
    name: 'notFound',
    path: '/:path(.*)+',
    redirect: '/wecom/app',
  },
  {
    name: 'wecom',
    path: '/wecom',
    meta: {
      title: '任务列表',
    },
    redirect: '/wecom/app',
    children: [
      {
        name: 'mainEnter',
        path: 'app',
        component: () => import('./view/task/mainEnter.vue'),
        meta: {
          title: '应用',
        },
      },
      {
        name: 'list',
        path: 'list',
        component: () => import('./view/task/list.vue'),
        meta: {
          title: '任务列表',
        },
      },
      {
        name: 'subList',
        path: 'subList',
        component: () => import('./view/task/subList.vue'),
        meta: {
          title: '重复任务',
        },
      },
      {
        name: 'detail',
        path: 'detail',
        component: () => import('./view/task/detail.vue'),
        meta: {
          title: '任务详情',
        },
      }, {
        name: 'receiverList',
        path: 'receiverList',
        component: () => import('./view/task/mainSiderbar.vue'),
        meta: {
          title: '接受客户列表',
        },
      },
      {
        name: 'sidebar',
        path: 'sidebar',
        component: () => import('./view/task/sidebar.vue'),
        meta: {
          title: '代办任务详情',
        },
      },
    ]
  },
];

const router = createRouter({
  routes,
  history: createWebHistory(import.meta.env.BASE_URL)
});

router.beforeEach((to, from, next) => {
  const title = to.meta && to.meta.title;
  if (title) {
    document.title = ' '//title;
  }
  if (to.query.corp_id && to.query.agent_id) {
    next()
  } else {
    if (from.query.corp_id && from.query.agent_id) {
      let toQuery = qs.parse(to.query)
      toQuery.corp_id = from.query.corp_id
      toQuery.agent_id = from.query.agent_id
      next({
        path: to.path,
        query: toQuery
      }
      )
    } else {
      next()
    }

  }
});

export { router };
