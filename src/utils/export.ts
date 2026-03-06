import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

export interface ExportData {
  date: string
  category: string
  type: string
  amount: number
  description: string
}

export class ExportService {
  static async exportToPDF(data: ExportData[], title: string = '碳足迹数据报表') {
    const doc = new jsPDF()

    const pageWidth = doc.internal.pageSize.getWidth()
    const pageHeight = doc.internal.pageSize.getHeight()
    const margin = 20
    const contentWidth = pageWidth - 2 * margin

    doc.setFontSize(20)
    doc.setFont('helvetica', 'bold')
    doc.text(title, pageWidth / 2, 20, { align: 'center' })

    doc.setFontSize(10)
    doc.setFont('helvetica', 'normal')
    const exportDate = new Date().toLocaleString('zh-CN')
    doc.text(`Export Date: ${exportDate}`, pageWidth / 2, 30, { align: 'center' })

    const headers = ['Date', 'Category', 'Type', 'Emission (kg CO2e)', 'Description']
    const columnWidths = [contentWidth * 0.2, contentWidth * 0.15, contentWidth * 0.15, contentWidth * 0.2, contentWidth * 0.3]

    let y = 45
    const lineHeight = 10

    doc.setFillColor(76, 175, 80)
    doc.rect(margin, y, contentWidth, lineHeight, 'F')
    doc.setTextColor(255, 255, 255)
    doc.setFontSize(10)
    doc.setFont('helvetica', 'bold')

    headers.forEach((header, index) => {
      doc.text(header, margin + columnWidths.slice(0, index).reduce((sum, width) => sum + width, 0) + 5, y + 7)
    })

    doc.setTextColor(0, 0, 0)
    doc.setFont('helvetica', 'normal')

    y += lineHeight

    data.forEach((row, index) => {
      if (y + lineHeight > pageHeight - 20) {
        doc.addPage()
        y = 20
      }

      const values = [
        row.date,
        row.category,
        row.type,
        row.amount.toFixed(2),
        row.description || '-'
      ]

      if (index % 2 === 0) {
        doc.setFillColor(240, 240, 240)
        doc.rect(margin, y, contentWidth, lineHeight, 'F')
      }

      values.forEach((value, index) => {
        doc.text(value, margin + columnWidths.slice(0, index).reduce((sum, width) => sum + width, 0) + 5, y + 7)
      })

      y += lineHeight
    })

    const totalEmission = data.reduce((sum, row) => sum + row.amount, 0)
    y += 10
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(12)
    doc.text(`Total Emission: ${totalEmission.toFixed(2)} kg CO2e`, margin, y)

    doc.save(`${title}_${new Date().toISOString().split('T')[0]}.pdf`)
  }

  static async exportToPDFWithCanvas(data: ExportData[], title: string = '碳足迹数据报表') {
    const container = document.createElement('div')
    container.style.position = 'fixed'
    container.style.left = '-9999px'
    container.style.top = '0'
    container.style.width = '800px'
    container.style.padding = '20px'
    container.style.fontFamily = 'Arial, sans-serif'

    const table = document.createElement('table')
    table.style.width = '100%'
    table.style.borderCollapse = 'collapse'
    table.style.marginTop = '20px'

    const titleRow = document.createElement('tr')
    const titleCell = document.createElement('td')
    titleCell.colSpan = 5
    titleCell.style.fontSize = '20px'
    titleCell.style.fontWeight = 'bold'
    titleCell.style.textAlign = 'center'
    titleCell.style.padding = '10px'
    titleCell.textContent = title
    titleRow.appendChild(titleCell)
    table.appendChild(titleRow)

    const dateRow = document.createElement('tr')
    const dateCell = document.createElement('td')
    dateCell.colSpan = 5
    dateCell.style.fontSize = '12px'
    dateCell.style.textAlign = 'center'
    dateCell.style.padding = '10px'
    dateCell.style.color = '#666'
    dateCell.textContent = `导出时间: ${new Date().toLocaleString('zh-CN')}`
    dateRow.appendChild(dateCell)
    table.appendChild(dateRow)

    const headerRow = document.createElement('tr')
    headerRow.style.backgroundColor = '#4CAF50'
    headerRow.style.color = 'white'

    const headers = ['日期', '分类', '类型', '排放量 (kg CO₂e)', '描述']
    headers.forEach(header => {
      const th = document.createElement('th')
      th.style.padding = '10px'
      th.style.border = '1px solid #ddd'
      th.style.textAlign = 'left'
      th.textContent = header
      headerRow.appendChild(th)
    })
    table.appendChild(headerRow)

    data.forEach((row, index) => {
      const tr = document.createElement('tr')
      if (index % 2 === 0) {
        tr.style.backgroundColor = '#f9f9f9'
      }

      const values = [
        row.date,
        row.category,
        row.type,
        row.amount.toFixed(2),
        row.description || '-'
      ]

      values.forEach(value => {
        const td = document.createElement('td')
        td.style.padding = '8px'
        td.style.border = '1px solid #ddd'
        td.textContent = value
        tr.appendChild(td)
      })

      table.appendChild(tr)
    })

    const totalRow = document.createElement('tr')
    totalRow.style.backgroundColor = '#e8f5e9'
    const totalLabelCell = document.createElement('td')
    totalLabelCell.colSpan = 3
    totalLabelCell.style.padding = '10px'
    totalLabelCell.style.fontWeight = 'bold'
    totalLabelCell.style.border = '1px solid #ddd'
    totalLabelCell.textContent = '总排放量:'

    const totalValueCell = document.createElement('td')
    totalValueCell.colSpan = 2
    totalValueCell.style.padding = '10px'
    totalValueCell.style.fontWeight = 'bold'
    totalValueCell.style.border = '1px solid #ddd'
    totalValueCell.style.color = '#4CAF50'
    const totalEmission = data.reduce((sum, row) => sum + row.amount, 0)
    totalValueCell.textContent = `${totalEmission.toFixed(2)} kg CO₂e`

    totalRow.appendChild(totalLabelCell)
    totalRow.appendChild(totalValueCell)
    table.appendChild(totalRow)

    container.appendChild(table)
    document.body.appendChild(container)

    try {
      const canvas = await html2canvas(container, {
        scale: 2,
        useCORS: true,
        logging: false
      })

      const imgData = canvas.toDataURL('image/png')
      const pdf = new jsPDF('p', 'mm', 'a4')

      const imgWidth = 190
      const imgHeight = (canvas.height * imgWidth) / canvas.width

      pdf.addImage(imgData, 'PNG', 10, 10, imgWidth, imgHeight)
      pdf.save(`${title}_${new Date().toISOString().split('T')[0]}.pdf`)
    } finally {
      document.body.removeChild(container)
    }
  }

  static exportToExcel(data: ExportData[], title: string = '碳足迹数据报表') {
    const worksheetData = [
      ['日期', '分类', '类型', '排放量 (kg CO₂e)', '描述'],
      ...data.map(row => [
        row.date,
        row.category,
        row.type,
        row.amount.toFixed(2),
        row.description || '-'
      ])
    ]

    const worksheet = XLSX.utils.aoa_to_sheet(worksheetData)

    const colWidths = [
      { wch: 15 },
      { wch: 12 },
      { wch: 12 },
      { wch: 20 },
      { wch: 30 }
    ]
    worksheet['!cols'] = colWidths

    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, '碳足迹数据')

    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })

    saveAs(blob, `${title}_${new Date().toISOString().split('T')[0]}.xlsx`)
  }

  static exportToCSV(data: ExportData[], title: string = '碳足迹数据报表') {
    const headers = ['日期,分类,类型,排放量 (kg CO₂e),描述']
    const rows = data.map(row =>
      `${row.date},${row.category},${row.type},${row.amount.toFixed(2)},"${row.description || '-'}"`
    )

    const csvContent = [headers, ...rows].join('\n')
    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })

    saveAs(blob, `${title}_${new Date().toISOString().split('T')[0]}.csv`)
  }
}
