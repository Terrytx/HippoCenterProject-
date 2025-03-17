package tw.com.hippo_center_backend.h4dto.b2;

public class PromotionSummaryDTO {
    private String title; // 促銷活動標題
    private double discountRate; // 折扣率

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}