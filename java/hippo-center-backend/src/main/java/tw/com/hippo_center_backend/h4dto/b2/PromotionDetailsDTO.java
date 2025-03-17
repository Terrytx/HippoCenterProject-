package tw.com.hippo_center_backend.h4dto.b2;

import java.time.LocalDateTime;

public class PromotionDetailsDTO {
    private String promotionMemberId; // 促銷券 ID
    private String title; // 促銷活動標題
    private String promotionCode; // 促銷碼
    private String description; // 描述
    private double discountRate; // 折扣率
    private String promotionStatus; // 促銷券狀態
    private LocalDateTime startDate; // 開始日期
    private LocalDateTime endDate; // 結束日期

    public PromotionDetailsDTO(String promotionMemberId, String title, String promotionCode, String description,
            double discountRate, String promotionStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.promotionMemberId = promotionMemberId;
        this.title = title;
        this.promotionCode = promotionCode;
        this.description = description;
        this.discountRate = discountRate;
        this.promotionStatus = promotionStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getPromotionMemberId() {
        return promotionMemberId;
    }

    public void setPromotionMemberId(String promotionMemberId) {
        this.promotionMemberId = promotionMemberId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public String getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(String promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
