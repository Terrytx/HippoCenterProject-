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

    // 新增的方法
    formatFullDate(date) {
        if (!date) return ''
        const d = new Date(date)
        return `${d.getFullYear()}年${String(d.getMonth() + 1).padStart(2, '0')}月${String(d.getDate()).padStart(2, '0')}日`
    },

    parseFullDate(dateString) {
        if (!dateString) return null
        return new Date(dateString)
    },

    dateToObject(date) {
        if (!date) return null
        const d = new Date(date)
        return {
            year: d.getFullYear(),
            month: d.getMonth()
        }
    },

    objectToDate(obj) {
        if (!obj) return null
        return new Date(obj.year, obj.month)
    },

    // ISO 格式轉換
    toISOString(date) {
        if (!date) return ''
        return new Date(date).toISOString().split('T')[0]
    }
}