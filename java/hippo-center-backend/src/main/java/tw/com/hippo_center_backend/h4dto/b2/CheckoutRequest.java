package tw.com.hippo_center_backend.h4dto.b2;

public class CheckoutRequest {
    private Integer totalAmount;
    private Integer memberId;
    private String promotionMemberId;
    private Integer originalPrice;

    // Getter 和 Setter
    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getPromotionMemberId() {
        return promotionMemberId;
    }

    public void setPromotionMemberId(String promotionMemberId) {
        this.promotionMemberId = promotionMemberId;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    
}
