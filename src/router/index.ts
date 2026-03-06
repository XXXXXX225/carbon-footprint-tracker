import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/transport',
    name: 'Transport',
    component: () => import('../views/Transport.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/diet',
    name: 'Diet',
    component: () => import('../views/Diet.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/electricity',
    name: 'Electricity',
    component: () => import('../views/Electricity.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/report',
    name: 'Report',
    component: () => import('../views/Report.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/recommendations',
    name: 'Recommendations',
    component: () => import('../views/Recommendations.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/points',
    name: 'Points',
    component: () => import('../views/Points.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token') !== null
  const userStr = localStorage.getItem('user')
  let userRole = ''
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      userRole = user.role || ''
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }

  if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
    next({ name: 'Login' })
  } else if (to.matched.some(record => record.meta.requiresAdmin) && userRole !== 'ADMIN') {
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default router