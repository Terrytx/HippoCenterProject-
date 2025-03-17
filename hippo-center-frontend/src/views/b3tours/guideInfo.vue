<template>
        <div class="guide-info-content">
            <h2>導覽說明</h2>
            <div class="guide-info-text">
                <h3>服務概述</h3>
                <p>本館提供專業導覽服務，讓訪客深入了解展覽內容、藝術背景與創作理念。</p>
                <p>導覽分為日間導覽與夜間導覽，滿足不同時間需求。</p>
                <p>可選導覽語言：中文與英文，由經驗豐富的導覽員講解。</p>

                <h3>導覽類型</h3>
                <div class="guide-section">
                    <img src="@/assets/images/b3guide/daytour.jpg" alt="日間導覽" class="guide-img">
                    <div class="guide-text">
                        <p>時間：每周二及周四10:00、13:00（每次導覽約120分鐘）。</p>
                        <p>適合希望在自然光下欣賞展品並獲得清晰解說的訪客，涵蓋所有主要展廳，並提供互動問答環節，增進參觀樂趣。</p>
                    </div>
                </div>

                <h3>夜間導覽</h3>
                <div class="guide-section">
                    <img src="@/assets/images/b3guide/nighttour.jpg" alt="夜間導覽" class="guide-img">
                    <div class="guide-text">
                        <p>時間：每周三19:00（每次導覽約120分鐘）。</p>
                        <p>夜間導覽的獨特氛圍讓參觀更加迷人，適合想要避開日間人流或享受沉靜環境的訪客，夜間導覽額外包含戶外雕塑花園燈光秀的專屬介紹。</p>
                    </div>
                </div>

                <h3>導覽亮點</h3>
                <div class="guide-section">
                    <img src="@/assets/images/b3guide/highlights.jpg" alt="導覽亮點" class="guide-img">
                    <div class="guide-text">
                        <ul>
                            <li>深入解讀展品：導覽員將分享展品背後的創作故事與歷史背景，帶您領略藝術的深度與內涵。</li>
                            <li>專屬導覽路線：特別設計的路線涵蓋經典展廳與特色區域，讓您不錯過任何亮點。</li>
                            <li>小團體體驗：每次導覽團人數限制在15人以內，確保專屬的服務品質與交流機會。</li>
                            <li>互動性強：導覽過程中設有問答環節，鼓勵參觀者積極參與，讓體驗更加充實。</li>
                        </ul>
                    </div>
                </div>

                <h3>線上預約功能</h3>
                <p>訪客可於導覽行事曆直接點擊「導覽行程」進行預約。</p>
                <p>選擇導覽類型（日間/夜間）、語言（中文/英文）以及參加時間。</p>
                <p>預約導覽必須登入會員方可進行預約，完成後將收到確認郵件。</p>

                <h3>注意事項</h3>
                <ul>
                    <li>每場導覽名額有限，建議至少提前3天進行預約。</li>
                    <li>預約可於參觀日的24小時前至會員專區取消或更改。</li>
                    <li>若因特殊情況需更改預約，請聯繫館方客服協助處理。</li>
                </ul>

                <h3>現場報名</h3>
                <p>若當天導覽團尚有空位，訪客可至服務台現場報名（以先到先得為準）。</p>

                <h3>費用資訊</h3>
                <p>日間導覽與夜間導覽均免費提供（憑有效入場票即可參加）。</p>

                <h3>其他須知</h3>
                <p>集合地點：每場導覽於展覽館主入口處集合，請提前10分鐘到場。</p>
                <p>遲到規定：導覽開始後將不接受遲到者加入，敬請準時。</p>
                <p>語言需求：選擇語言後，導覽過程中不可更換語言組別，請提前確認需求。</p>
            </div>
        </div>
</template>


<script>
export default {
    name: "GuideInfo",
};
</script>

<style scoped>
.guide-info-content {
width: 100%;
padding: 20px;
text-align: left;
margin-top: 40px; /* 讓它與行事曆有適當距離 */
}

.guide-info-text {
line-height: 1.6;
font-size: 1rem;
color: #333;
}

.guide-info-text ul {
list-style: none; /* 移除預設的圓點符號 */
padding-left: 0; /* 移除左邊縮排 */
}

.guide-info-text ul li::before {
content: "-"; /* 破折號 */
font-weight: bold; /* 讓破折號更明顯 */
margin-right: 5px; /* 確保文字與破折號之間有間距 */
color: black; /* 讓破折號顏色與標題相近 */
}

.guide-info-text h3 {
margin-top: 30px; /* 讓標題與上方內容保持距離 */
margin-bottom: 6px; /* 增加標題與下方內容的間距 */
font-size: 1.2rem;
color: #555;
}

.guide-info-text p,
.guide-info-text li,
.guide-info-text ul {
margin-bottom: 10px;    
}

/* 圖片樣式 */
.guide-img {
width: 40%; /* 讓圖片佔 40% 寬度 */
max-width: 140px; /* 限制最大寬度，避免圖片過大 */
height: auto; /* 保持圖片比例 */
border-radius: 5px; /* 圓角 */
box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1); /* 陰影效果 */
}

.guide-section {
display: flex;
align-items: center; /* 讓圖片與文字垂直對齊 */
gap: 20px; /* 圖片與文字間的間距 */
margin-bottom: 20px; /* 區塊間的間距 */
}

.guide-text {
flex: 1; /* 讓文字內容自動填滿剩餘空間 */
font-size: 1rem;
color: #333;
}

</style>
