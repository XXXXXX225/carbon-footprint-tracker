import { defineStore } from 'pinia'

interface CarbonFootprint {
  transport: number
  diet: number
  electricity: number
  total: number
}

interface EmissionRecord {
  id: string
  type: 'transport' | 'diet' | 'electricity'
  value: number
  date: string
  description: string
}

export const useCarbonStore = defineStore('carbon', {
  state: () => ({
    footprint: {
      transport: 0,
      diet: 0,
      electricity: 0,
      total: 0
    } as CarbonFootprint,
    records: [] as EmissionRecord[],
    reductionGoal: 20, // 20% 减排目标
    user: {
      id: '',
      name: '',
      role: ''
    }
  }),
  getters: {
    totalFootprint: (state) => {
      return state.footprint.total
    },
    monthlyRecords: (state) => {
      return state.records.filter(record => {
        const recordDate = new Date(record.date)
        const now = new Date()
        return recordDate.getMonth() === now.getMonth() && recordDate.getFullYear() === now.getFullYear()
      })
    }
  },
  actions: {
    updateFootprint(type: keyof Omit<CarbonFootprint, 'total'>, value: number) {
      this.footprint[type] = value
      this.calculateTotal()
    },
    calculateTotal() {
      this.footprint.total = this.footprint.transport + this.footprint.diet + this.footprint.electricity
    },
    addRecord(record: Omit<EmissionRecord, 'id'>) {
      const newRecord: EmissionRecord = {
        ...record,
        id: Date.now().toString()
      }
      this.records.push(newRecord)
      this.updateFootprint(record.type, this.footprint[record.type] + record.value)
    },
    setReductionGoal(goal: number) {
      this.reductionGoal = goal
    },
    clearRecords() {
      this.records = []
      this.footprint = {
        transport: 0,
        diet: 0,
        electricity: 0,
        total: 0
      }
    },
    // 从localStorage加载用户信息
    loadUserFromLocalStorage() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        try {
          const user = JSON.parse(userStr)
          this.user = user
        } catch (error) {
          console.error('解析用户信息失败:', error)
        }
      }
    },
    // 设置用户信息
    setUser(user: any) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    }
  }
})