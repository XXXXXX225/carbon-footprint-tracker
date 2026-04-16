import { defineStore } from 'pinia'
import { carbonApi } from '../api'

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
      this.saveToLocalStorage()
    },
    calculateTotal() {
      this.footprint.total = this.footprint.transport + this.footprint.diet + this.footprint.electricity
    },
    async fetchAllRecords() {
      try {
        const [transport, diet, electricity] = await Promise.all([
          carbonApi.getTransportRecords(),
          carbonApi.getDietRecords(),
          carbonApi.getElectricityRecords()
        ])

        const formattedTransport = transport.map(r => ({
          id: String(r.id),
          type: 'transport' as const,
          value: r.emissionAmount,
          date: r.emissionDate,
          description: r.description || '交通出行记录'
        }))

        const formattedDiet = diet.map(r => ({
          id: String(r.id),
          type: 'diet' as const,
          value: r.emissionAmount,
          date: r.emissionDate,
          description: r.description || '饮食消费记录'
        }))

        const formattedElectricity = electricity.map(r => ({
          id: String(r.id),
          type: 'electricity' as const,
          value: r.emissionAmount,
          date: r.emissionDate,
          description: r.description || '家庭用电记录'
        }))

        this.records = [...formattedTransport, ...formattedDiet, ...formattedElectricity].sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())

        let tSum = 0, dSum = 0, eSum = 0
        formattedTransport.forEach(r => tSum += r.value)
        formattedDiet.forEach(r => dSum += r.value)
        formattedElectricity.forEach(r => eSum += r.value)

        this.footprint.transport = tSum
        this.footprint.diet = dSum
        this.footprint.electricity = eSum
        this.calculateTotal()

        this.saveToLocalStorage()
      } catch (error) {
        console.error('Failed to fetch real records:', error)
      }
    },
    addRecord(record: Omit<EmissionRecord, 'id'>) {
      const newRecord: EmissionRecord = {
        ...record,
        id: Date.now().toString()
      }
      this.records.push(newRecord)
      this.footprint[record.type] += record.value
      this.calculateTotal()
      this.saveToLocalStorage()
    },
    setReductionGoal(goal: number) {
      this.reductionGoal = goal
      this.saveToLocalStorage()
    },
    clearRecords() {
      this.records = []
      this.footprint = {
        transport: 0,
        diet: 0,
        electricity: 0,
        total: 0
      }
      this.saveToLocalStorage()
    },
    saveToLocalStorage() {
      localStorage.setItem('carbon_footprint', JSON.stringify(this.footprint))
      localStorage.setItem('carbon_records', JSON.stringify(this.records))
      localStorage.setItem('carbon_reductionGoal', JSON.stringify(this.reductionGoal))
    },
    // 从localStorage加载用户信息和数据
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

      const footprintStr = localStorage.getItem('carbon_footprint')
      if (footprintStr) {
        try {
          this.footprint = JSON.parse(footprintStr)
        } catch (e) { }
      }

      const recordsStr = localStorage.getItem('carbon_records')
      if (recordsStr) {
        try {
          this.records = JSON.parse(recordsStr)
        } catch (e) { }
      }

      const goalStr = localStorage.getItem('carbon_reductionGoal')
      if (goalStr) {
        try {
          this.reductionGoal = JSON.parse(goalStr)
        } catch (e) { }
      }
    },
    // 设置用户信息
    setUser(user: any) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    }
  }
})