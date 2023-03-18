import { createRouter, createWebHistory } from 'vue-router'

const routes= [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    // route level code-splitting
    // this generates a separate chunk (About.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/active',
    name: 'active',
    component: () => import('@/views/active.vue')
  },
  {
    path: '/forget',
    name: 'forget',
    component: () => import('@/views/Forget.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout/Layout.vue'),
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/Layout/Home/Home.vue')
      },
      {
        path: 'userInfo',
        name: 'userControl',
        component: () => import('@/views/Layout/UserControl/UserControl.vue')
      }
    ]
  },
  {
    path: '/meeting/:meetId',
    name: 'meeting',
    component: () => import('@/views/Meeting.vue'),
  },
  {
    path: '/test',
    name: 'test',
    component: () => import('@/views/Test.vue')
  },
  {
    path: '/testroutpush',
    name: 'testr',
    component: () => import('@/views/TestPush.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
