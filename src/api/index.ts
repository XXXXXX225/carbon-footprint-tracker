// API接口封装

const BASE_URL = '/api'

// 通用请求方法
async function request<T>(url: string, options?: RequestInit): Promise<T> {
    const token = localStorage.getItem('token')

    const headers: HeadersInit = {
        'Content-Type': 'application/json',
        ...options?.headers
    }

    // 对于认证相关的请求，不添加Authorization token
    if (token && !url.startsWith('/auth/')) {
        headers['Authorization'] = `Bearer ${token}`
    }

    const response = await fetch(`${BASE_URL}${url}`, {
        ...options,
        headers
    })

    if (!response.ok) {
        try {
            const error = await response.json()
            if (error.message) {
                throw new Error(error.message)
            } else if (error.error) {
                throw new Error(error.error)
            } else {
                throw new Error('请求失败')
            }
        } catch (e) {
            throw new Error('请求失败：' + response.statusText)
        }
    }

    try {
        const result = await response.json()
        return (result.data || result) as T
    } catch (e) {
        throw new Error('响应数据格式错误')
    }
}

// 认证相关API
export const authApi = {
    // 登录
    login: (username: string, password: string) => {
        return request<{
            token: string
            username: string
            name: string
            role: string
        }>('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        })
    },
    // 注册
    register: (username: string, password: string, name: string, email: string) => {
        return request<{
            token: string
            username: string
            name: string
            role: string
        }>('/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, name, email })
        })
    },
    // 登出
    logout: () => {
        return request<{ success: boolean }>('/auth/logout', {
            method: 'POST'
        })
    }
}

// 碳足迹相关API
export const carbonApi = {
    // 获取排放汇总
    getSummary: (period: string = 'month') => {
        return request<{
            userId: string
            period: string
            totalEmission: number
            transportEmission: number
            dietEmission: number
            electricityEmission: number
            averageDailyEmission: number
            recordCount: number
        }>(`/emission/summary?period=${period}`)
    },
    // 获取交通排放记录
    getTransportRecords: () => {
        return request<Array<{
            id: number
            userId: number
            transportType: number
            distance: number
            fuelType: string
            fuelConsumption: number
            emissionAmount: number
            emissionDate: string
            description: string
            createdAt: string
        }>>('/emission/transport')
    },
    // 添加交通排放记录
    addTransportRecord: (record: {
        transportType: number
        distance: number
        fuelType?: string
        fuelConsumption?: number
        emissionDate: string
        description?: string
    }) => {
        return request<{
            id: number
            userId: number
            transportType: number
            distance: number
            fuelType: string
            fuelConsumption: number
            emissionAmount: number
            emissionDate: string
            description: string
            createdAt: string
        }>('/emission/transport', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(record)
        })
    },
    // 获取饮食排放记录
    getDietRecords: () => {
        return request<Array<{
            id: number
            userId: number
            foodType: number
            specificFood: string
            amount: number
            cookingMethod: string
            emissionAmount: number
            emissionDate: string
            description: string
            createdAt: string
        }>>('/emission/diet')
    },
    // 添加饮食排放记录
    addDietRecord: (record: {
        foodType: number
        specificFood: string
        amount: number
        cookingMethod?: string
        emissionDate: string
        description?: string
    }) => {
        return request<{
            id: number
            userId: number
            foodType: number
            specificFood: string
            amount: number
            cookingMethod: string
            emissionAmount: number
            emissionDate: string
            description: string
            createdAt: string
        }>('/emission/diet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(record)
        })
    },
    // 获取用电排放记录
    getElectricityRecords: () => {
        return request<Array<{
            id: number
            userId: number
            deviceType: string
            power: number
            usageTime: number
            usageDays: number
            electricityAmount: number
            emissionAmount: number
            emissionDate: string
            description: string
            createdAt: string
        }>>('/emission/electricity')
    },
    // 添加用电排放记录
    addElectricityRecord: (record: {
        deviceType: string
        power: number
        usageTime: number
        usageDays: number
        emissionDate: string
        description?: string
    }) => {
        return request<{
            id: number
            userId: number
            deviceType: string
            power: number
            usageTime: number
            usageDays: number
            electricityAmount: number
            emissionAmount: number
            emissionDate: string
            description: string
            createdAt: string
        }>('/emission/electricity', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(record)
        })
    },
    // 获取减排建议
    getRecommendations: () => {
        return request<Array<{
            id: number
            category: string
            title: string
            description: string
            impact: number
            difficulty: string
            cost: string
        }>>('/recommendations')
    }
}

// 用户相关API
export const userApi = {
    // 获取用户信息
    getUserInfo: () => {
        return request<{
            id: string
            name: string
            email: string
        }>('/user/info')
    },
    // 更新用户信息
    updateUserInfo: (userInfo: {
        name: string
        email: string
    }) => {
        return request<{
            id: string
            name: string
            email: string
        }>('/user/info', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userInfo)
        })
    }
}

// 管理员相关API
export const adminApi = {
    // 获取所有用户列表
    getAllUsers: () => {
        return request<Array<{
            id: number
            username: string
            name: string
            email: string
            role: string
            totalPoints: number
            createdAt: string
            updatedAt: string
        }>>('/admin/users')
    },
    // 获取用户注册统计
    getUserStats: () => {
        return request<{
            totalUsers: number
            todayNewUsers: number
            weekNewUsers: number
            monthNewUsers: number
            roleDistribution: Record<string, number>
            dailyRegistrationTrend: Record<string, number>
        }>('/admin/users/stats')
    },
    // 编辑用户信息
    updateUser: (id: number, userData: {
        name?: string
        email?: string
        role?: string
        totalPoints?: number
    }) => {
        return request<{
            id: number
            username: string
            name: string
            email: string
            role: string
            totalPoints: number
            createdAt: string
            updatedAt: string
        }>(`/admin/users/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
    },
    // 删除用户
    deleteUser: (id: number) => {
        return request<{ success: boolean }>(`/admin/users/${id}`, {
            method: 'DELETE'
        })
    }
}
