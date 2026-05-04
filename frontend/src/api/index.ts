// API鎺ュ彛灏佽

const BASE_URL = ''

// 閫氱敤璇锋眰鏂规硶
async function request<T>(url: string, options?: RequestInit): Promise<T> {
    const token = localStorage.getItem('token')

    console.log(`[API璇锋眰] URL: ${url}`)
    console.log(`[API璇锋眰] Token瀛樺湪: ${!!token}`)
    console.log(`[API璇锋眰] Token鍊? ${token ? token.substring(0, 20) + '...' : 'null'}`)
    console.log(`[API璇锋眰] localStorage鍐呭:`, {
        token: localStorage.getItem('token'),
        user: localStorage.getItem('user')
    })

    const headers: HeadersInit = {
        'Content-Type': 'application/json',
        ...options?.headers
    }

    // 瀵逛簬璁よ瘉鐩稿叧鐨勮姹傦紝涓嶆坊鍔燗uthorization token
    if (token && !url.startsWith('/auth/')) {
        headers['Authorization'] = `Bearer ${token}`
        console.log(`[API璇锋眰] 宸叉坊鍔燗uthorization澶? Bearer ${token.substring(0, 20)}...`)
    }

    console.log(`[API璇锋眰] 瀹屾暣headers:`, headers)
    console.log(`[API璇锋眰] 瀹屾暣URL: ${BASE_URL}${url}`)

    const response = await fetch(`${BASE_URL}${url}`, {
        ...options,
        headers
    })

    console.log(`[API鍝嶅簲] 鐘舵€佺爜: ${response.status}`)
    console.log(`[API鍝嶅簲] 鍝嶅簲澶?`, Object.fromEntries(response.headers.entries()))

    if (!response.ok) {
        try {
            const error = await response.json()
            console.error(`[API閿欒] 閿欒璇︽儏:`, error)
            if (error.message) {
                throw new Error(error.message)
            } else if (error.error) {
                throw new Error(error.error)
            } else {
                throw new Error('请求失败')
            }
        } catch (e) {
            throw new Error('请求失败: ' + response.statusText)
        }
    }

    try {
        const result = await response.json()
        console.log(`[API鍝嶅簲] 鏁版嵁:`, result)
        return (result.data || result) as T
    } catch (e) {
        throw new Error('鍝嶅簲鏁版嵁鏍煎紡閿欒')
    }
}

// 璁よ瘉鐩稿叧API
export const authApi = {
    // 鐧诲綍
    login: (username: string, password: string) => {
        return request<{
            token: string
            username: string
            name: string
            role: string
        }>('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        })
    },
    // 娉ㄥ唽
    register: (username: string, password: string, name: string, email: string) => {
        return request<{
            token: string
            username: string
            name: string
            role: string
        }>('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, name, email })
        })
    },
    // 鐧诲嚭
    logout: () => {
        return request<{ success: boolean }>('/api/auth/logout', {
            method: 'POST'
        })
    }
}

// 碳足迹相关API
export const carbonApi = {
    // 获取碳排放汇总
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
        }>(`/api/emission/summary?period=${period}`)
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
        }>>('/api/emission/transport')
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
        }>('/api/emission/transport', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(record)
        })
    },
    deleteTransportRecord: (id: number) => {
        return request<{ success: boolean }>(`/api/emission/transport/${id}`, {
            method: 'DELETE'
        })
    },
    clearTransportRecords: () => {
        return request<{ success: boolean }>('/api/emission/transport', {
            method: 'DELETE'
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
        }>>('/api/emission/diet')
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
        }>('/api/emission/diet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(record)
        })
    },
    deleteDietRecord: (id: number) => {
        return request<{ success: boolean }>(`/api/emission/diet/${id}`, {
            method: 'DELETE'
        })
    },
    clearDietRecords: () => {
        return request<{ success: boolean }>('/api/emission/diet', {
            method: 'DELETE'
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
        }>>('/api/emission/electricity')
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
        }>('/api/emission/electricity', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(record)
        })
    },
    deleteElectricityRecord: (id: number) => {
        return request<{ success: boolean }>(`/api/emission/electricity/${id}`, {
            method: 'DELETE'
        })
    },
    clearElectricityRecords: () => {
        return request<{ success: boolean }>('/api/emission/electricity', {
            method: 'DELETE'
        })
    },
    // 获取碳行动计划
    getActionPlans: () => {
        return request<Array<{
            id: number
            category: string
            title: string
            description: string
            impact: number
            difficulty: string
            cost: string
        }>>('/api/action-plans')
    }
}

// 鐢ㄦ埛鐩稿叧API
export const userApi = {
    // 鑾峰彇鐢ㄦ埛淇℃伅
    getUserInfo: () => {
        return request<{
            id: string
            name: string
            email: string
        }>('/api/user/info')
    },
    // 鏇存柊鐢ㄦ埛淇℃伅
    updateUserInfo: (userInfo: {
        name: string
        email: string
    }) => {
        return request<{
            id: string
            name: string
            email: string
        }>('/api/user/info', {
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
        }>>('/api/admin/users')
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
        }>('/api/admin/users/stats')
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
        }>(`/api/admin/users/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
    },
    // 删除用户
    deleteUser: (id: number) => {
        return request<{ success: boolean }>(`/api/admin/users/${id}`, {
            method: 'DELETE'
        })
    }
}

// AI预测相关API
export const predictionApi = {
    // 预测下月碳排放
    getNextMonthPrediction: () => {
        return request<{
            userId: number
            predictionDate: string
            predictedEmission: number
            confidence: number
            trend: string
            dailyPredictions: Array<{
                date: string
                predictedEmission: number
                lowerBound: number
                upperBound: number
            }>
            monthlyPredictions: Array<{
                month: string
                predictedEmission: number
                trend: number
            }>
            suggestion: {
                category: string
                suggestion: string
                potentialReduction: number
                priority: number
            } | null
        }>('/api/prediction/next-month')
    },
    // 获取预测历史
    getHistory: () => {
        return request<Array<{
            id: number
            targetMonth: string
            predictionDate: string
            predictedEmission: number
            confidence: number
            trend: string
            actualEmission: number | null
            absoluteError: number | null
            errorRate: number | null
            status: string
            createdAt: string
            updatedAt: string
        }>>('/api/prediction/history')
    }
}

// AI分析相关API
export const aiAnalysisApi = {
    getAnalysis: () => {
        return request<{
            model: string
            generatedAt: string
            headline: string
            summary: string
            riskLevel: 'LOW' | 'MEDIUM' | 'HIGH'
            confidence: number
            insights: Array<{
                title: string
                text: string
            }>
            recommendations: string[]
            nextActions: string[]
            source: string
        }>('/api/ai/analysis')
    }
}

// 积分相关API
export const pointsApi = {
    getTotalPoints: () => {
        return request<number>('/api/points/total')
    },
    getPointsRecords: () => {
        return request<Array<{
            id: number
            userId: number
            pointsChange: number
            totalPoints: number
            emissionReduced: number
            reason: string
            createdAt: string
        }>>('/api/points/records')
    },
    calculatePoints: (emissionReduced: number, reason: string) => {
        return request<number>(`/api/points/calculate?emissionReduced=${emissionReduced}&reason=${encodeURIComponent(reason)}`, {
            method: 'POST'
        })
    },
    redeemPoints: (data: { pointsSpent: number, reason: string }) => {
        return request<{ remainingPoints: number }>('/api/points/redeem', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
    }
}

// 数据大屏相关API
export const dashboardApi = {
    // 获取大屏数据
    getDashboardData: (range: string = 'month') => {
        return request<{
            overview: {
                totalUsers: number
                totalEmission: number
                totalReduction: number
                totalPoints: number
                avgDailyEmission: number
                activeUsersToday: number
            }
            emissionTrends: Array<{
                date: string
                emission: number
                transportEmission: number
                dietEmission: number
                electricityEmission: number
            }>
            categoryDistribution: Array<{
                category: string
                value: number
                percentage: number
            }>
            topUsers: Array<{
                userId: number
                username: string
                totalPoints: number
                totalReduction: number
                rank: number
            }>
            regionalStats: Array<{
                region: string
                userCount: number
                totalEmission: number
                avgEmission: number
            }>
            realTimeActivities: Array<{
                time: string
                username: string
                activity: string
                emission: number
                type: string
            }>
        }>(`/api/dashboard/data?range=${range}`)
    }
}

// 目标相关API
export interface ReductionGoal {
    id: number
    userId: number
    targetPercentage: number
    baselineEmission: number
    targetEmission: number
    currentEmission: number | null
    startDate: string
    endDate: string
    status: 'ACTIVE' | 'COMPLETED' | 'FAILED' | 'CANCELLED'
    progress: number
    createdAt: string
    updatedAt: string
}

export const goalApi = {
    // 创建减排目标
    createGoal: (targetPercentage: number, endDate: string) => {
        return request<ReductionGoal>(`/api/goals?targetPercentage=${targetPercentage}&endDate=${endDate}`, {
            method: 'POST'
        })
    },
    // 更新目标进度
    updateProgress: (goalId: number) => {
        return request<{ success: boolean }>(`/api/goals/${goalId}/progress`, {
            method: 'PUT'
        })
    },
    // 获取用户目标列表
    getUserGoals: () => {
        return request<ReductionGoal[]>('/api/goals')
    },
    // 获取活跃目标
    getActiveGoal: () => {
        return request<ReductionGoal>('/api/goals/active')
    },
    // 取消目标
    cancelGoal: (goalId: number) => {
        return request<{ success: boolean }>(`/api/goals/${goalId}`, {
            method: 'DELETE'
        })
    }
}

// 建议相关API
export const recommendationApi = {
    // 从AI建议添加行动
    addAiTask: (data: { content: string }) =>
        request('/api/recommendations/add-from-ai', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }),
    // 更新用户建议状态
    updateUserRecommendation: (data: { id: number, status: string, completedAt?: string }) =>
        request('/api/recommendations/update', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }),
}
