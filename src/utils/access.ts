export type AppRole = 'INDIVIDUAL' | 'ENTERPRISE' | 'ADMIN'

export type RouteScope = 'public' | 'user' | 'operator' | 'admin'

export interface MenuItemConfig {
    path: string
    label: string
    scope: Exclude<RouteScope, 'public'>
}

export interface MenuSectionConfig {
    title: string
    items: MenuItemConfig[]
}

export interface TopNavItemConfig {
    command: string
    label: string
    scope: Exclude<RouteScope, 'public'>
}

const userMenuItems: MenuItemConfig[] = [
    { path: '/dashboard', label: '仪表盘', scope: 'user' },
    { path: '/emissions', label: '碳排放计算', scope: 'user' },
    { path: '/report', label: '报表展示', scope: 'user' },
    { path: '/ai-analysis', label: 'AI分析预测', scope: 'user' },
    { path: '/recommendations', label: '减排建议', scope: 'user' },
    { path: '/points', label: '减碳积分', scope: 'user' }
]

const operatorMenuItems: MenuItemConfig[] = [
    { path: '/dashboard-screen', label: '运营大屏', scope: 'operator' }
]

const adminMenuItems: MenuItemConfig[] = [
    { path: '/admin', label: '管理员后台', scope: 'admin' }
]

const topNavItems: TopNavItemConfig[] = [
    { command: 'dashboard', label: '仪表盘', scope: 'user' },
    { command: 'dashboard-screen', label: '运营大屏', scope: 'operator' },
    { command: 'profile', label: '个人中心', scope: 'user' },
    { command: 'points', label: '减碳积分', scope: 'user' },
    { command: 'admin', label: '管理员后台', scope: 'admin' }
]

const routeScopes: Array<{ scope: Exclude<RouteScope, 'public'>; paths: string[] }> = [
    {
        scope: 'user',
        paths: ['/dashboard', '/emissions', '/report', '/ai-analysis', '/recommendations', '/points', '/profile']
    },
    {
        scope: 'operator',
        paths: ['/dashboard-screen']
    },
    {
        scope: 'admin',
        paths: ['/admin']
    }
]

export function normalizeRole(role?: string | null): AppRole | '' {
    if (role === 'INDIVIDUAL' || role === 'ENTERPRISE' || role === 'ADMIN') {
        return role
    }

    return ''
}

export function getLandingRoute(role?: string | null): string {
    const normalizedRole = normalizeRole(role)

    if (normalizedRole === 'ADMIN') {
        return '/dashboard'
    }

    if (normalizedRole === 'ENTERPRISE') {
        return '/dashboard-screen'
    }

    return '/dashboard'
}

export function getSidebarSections(role?: string | null): MenuSectionConfig[] {
    const normalizedRole = normalizeRole(role)
    const sections: MenuSectionConfig[] = []

    const userItems = userMenuItems.filter(item => item.scope === 'user')
    const operatorItems = operatorMenuItems.filter(item => item.scope === 'operator' && (normalizedRole === 'ENTERPRISE' || normalizedRole === 'ADMIN'))
    const adminItems = adminMenuItems.filter(item => item.scope === 'admin' && normalizedRole === 'ADMIN')

    sections.push({ title: '普通用户', items: userItems })

    if (operatorItems.length > 0) {
        sections.push({ title: '运营视图', items: operatorItems })
    }

    if (adminItems.length > 0) {
        sections.push({ title: '管理员', items: adminItems })
    }

    return sections
}

export function getTopNavItems(role?: string | null): TopNavItemConfig[] {
    const normalizedRole = normalizeRole(role)

    return topNavItems.filter(item => {
        if (item.scope === 'user') {
            return true
        }

        if (item.scope === 'operator') {
            return normalizedRole === 'ENTERPRISE' || normalizedRole === 'ADMIN'
        }

        if (item.scope === 'admin') {
            return normalizedRole === 'ADMIN'
        }

        return false
    })
}

export function canAccessPath(role?: string | null, path?: string): boolean {
    if (!path) {
        return false
    }

    const normalizedRole = normalizeRole(role)
    if (!normalizedRole) {
        return false
    }

    if (normalizedRole === 'ADMIN') {
        return true
    }

    if (normalizedRole === 'ENTERPRISE') {
        return routeScopes.some(item => item.scope === 'user' && item.paths.includes(path)) || path === '/dashboard-screen'
    }

    return routeScopes.some(item => item.scope === 'user' && item.paths.includes(path))
}