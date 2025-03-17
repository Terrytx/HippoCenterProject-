export const dateConfig = {
    // 原有的方法
    formatDate(year, month) {
        return `${year}年${String(month + 1).padStart(2, '0')}月`
    },

    parseDate(dateString) {
        const matches = dateString.match(/(\d{4})年(\d{2})月/)
        if (matches) {
            return {
                year: parseInt(matches[1]),
                month: parseInt(matches[2]) - 1
            }
        }
        return null
    },
    toISOString: (date) => {
        return date.toISOString()
    },
    formatFullDate: (date) => {
        if (!date) return ''
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        // 改為回傳 YYYY-MM-DD 格式
        return `${year}-${month}-${day}`
        // 或使用 toISOString() 並只取日期部分
        // return date.toISOString().split('T')[0]
    },

    dateToObject(date) {
        const d = new Date(date)
        return {
            year: d.getFullYear(),
            month: d.getMonth(),
            date: d.getDate()
        }
    },
    // 將日期物件轉換回可供顯示的格式（如果需要顯示中文格式）
    formatDisplayDate: (date) => {
        if (!date) return ''
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}年${month}月${day}日`
    },

    // 其他方法保持不變
    objectToDate: (dateObj) => {
        if (!dateObj) return null
        return new Date(dateObj.year, dateObj.month, dateObj.date)
    }

}