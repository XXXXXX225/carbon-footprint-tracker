import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { getLandingRoute, normalizeRole } from '../utils/access'

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
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/transport',
    name: 'Transport',
    component: () => import('../views/Transport.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/diet',
    name: 'Diet',
    component: () => import('../views/Diet.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/electricity',
    name: 'Electricity',
    component: () => import('../views/Electricity.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/report',
    name: 'Report',
    component: () => import('../views/Report.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/ai-analysis',
    name: 'AIAnalysis',
    component: () => import('../views/AIAnalysis.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/recommendations',
    name: 'Recommendations',
    component: () => import('../views/Recommendations.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/points',
    name: 'Points',
    component: () => import('../views/Points.vue'),
    meta: { requiresAuth: true, roles: ['INDIVIDUAL', 'ENTERPRISE', 'ADMIN'] }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/news',
    name: 'News',
    component: () => import('../views/News.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/news/:id',
    name: 'NewsDetail',
    component: () => import('../views/NewsDetail.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard-screen',
    name: 'DashboardScreen',
    component: () => import('../views/DashboardScreen.vue'),
    meta: { requiresAuth: true, roles: ['ENTERPRISE', 'ADMIN'] }
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
      userRole = normalizeRole(user.role) || ''
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }

  if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
    next({ name: 'Login' })
  } else if (to.matched.some(record => Array.isArray(record.meta.roles) && record.meta.roles.length > 0) && !to.matched.every(record => {
    const roles = record.meta.roles as string[] | undefined
    return !roles || roles.includes(userRole)
  })) {
    next({ path: getLandingRoute(userRole) })
  } else {
    next()
  }
})

export default router