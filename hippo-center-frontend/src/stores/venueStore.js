import { defineStore } from 'pinia'
import axiosapi from '@/plugins/axios.js'

export const useVenueStore = defineStore('venue', {
    state: () => ({
        availableVenues: [],
        selectedVenue: null,
        isLoading: false,
        error: null,
        venueImages: []
    }),

    actions: {
        // 獲取所有可用場館
        async fetchAvailableVenues() {
            this.isLoading = true
            try {
                console.log('開始獲取場館數據')
                const response = await axiosapi.get('/api/venue/available')
                console.log('獲取到的數據:', response.data)

                if (response.data.success) {
                    this.availableVenues = response.data.list
                    console.log('更新後的場館列表:', this.availableVenues)

                    // 設定預設場館
                    // 使用 await 確保選擇場館的操作完成
                    const defaultVenue = this.availableVenues.find(v => v.venueId === 'A01')
                    if (defaultVenue) {
                        await this.selectVenue(defaultVenue)
                    }
                }
            } catch (error) {
                this.error = '獲取場館資料失敗'
                console.error('獲取場館數據失敗:', error)
            } finally {
                this.isLoading = false
            }
        },

        // 選擇場館
        async selectVenue(venue) {
            if (!venue) return

            try {
                this.selectedVenue = venue
                await this.fetchVenueImages(venue.venueId)
            } catch (error) {
                console.error('選擇場館失敗:', error)
            }
        },

        // 根據 ID 獲取特定場館
        async fetchVenueById(venueId) {
            try {
                const response = await axiosapi.get(`/api/venue/${venueId}`)
                if (response.data.success) {
                    this.selectedVenue = response.data.list[0]  // 因為後端返回的是陣列
                    await this.fetchVenueImages(venueId)
                    return this.selectedVenue
                }
            } catch (error) {
                console.error('獲取場館資料失敗:', error)
                this.error = '獲取場館資料失敗'
                return null
            }
        },


        // 獲取場館圖片
        async fetchVenueImages(venueId) {
            try {
                const response = await axiosapi.get(`/api/venue/image/${venueId}`)
                console.log('獲取到的圖片:', response.data)
                this.venueImages = response.data
            } catch (error) {
                console.error('Failed to fetch venue images:', error)
                console.error('獲取圖片失敗:', error)
                this.venueImages = []
            }
        },



        // 下載技術規格
        async downloadSpecifications(venueId) {
            try {
                const response = await axiosapi({
                    url: `/api/venue/download-specifications/${venueId}`,
                    method: 'GET',
                    responseType: 'blob',
                    headers: {
                        'Accept': 'application/pdf'
                    }
                })

                if (response.status === 200 && response.data) {
                    const blob = new Blob([response.data], { type: 'application/pdf' })
                    const url = window.URL.createObjectURL(blob)
                    const link = document.createElement('a')
                    link.href = url
                    link.download = `${this.selectedVenue.venueName}_技術資料.pdf`
                    document.body.appendChild(link)
                    link.click()
                    window.URL.revokeObjectURL(url)
                    document.body.removeChild(link)
                }
            } catch (error) {
                console.error('下載失敗:', error)
                throw error
            }
        },
    },
    getters: {
        // 取得已選擇的場館
        getSelectedVenue: (state) => state.selectedVenue,
        // 取得所有可用場館
        getAvailableVenues: (state) => state.availableVenues,
        // 取得場館圖片
        getVenueImages: (state) => state.venueImages
    }
})