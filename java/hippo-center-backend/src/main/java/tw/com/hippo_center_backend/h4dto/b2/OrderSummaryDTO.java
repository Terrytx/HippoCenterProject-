package tw.com.hippo_center_backend.h4dto.b2;

public class OrderSummaryDTO {
    private Integer orderId;
    private Integer totalAmount;
    private String orderStatus;
    private String promotionTitle; // 促銷券標題
    private String promotionCode; // 促銷碼
    private String phone;
    
    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    public Integer getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getPromotionTitle() {
        return promotionTitle;
    }
    public void setPromotionTitle(String promotionTitle) {
        this.promotionTitle = promotionTitle;
    }
    public String getPromotionCode() {
        return promotionCode;
    }
    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    // Getters and Setters
}

