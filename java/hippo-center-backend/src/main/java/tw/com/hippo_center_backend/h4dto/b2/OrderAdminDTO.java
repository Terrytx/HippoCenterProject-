package tw.com.hippo_center_backend.h4dto.b2;

import java.time.LocalDateTime;

public class OrderAdminDTO {
    private Integer orderId; // ✅ 用於邏輯
    private String merchantTradeNo; // ✅ 訂單編號
    private String orderStatus; // ✅ 訂單狀態
    private Integer totalAmount;
    private String orderAddress;
    private String phone;
    private String promotionMember; // ✅ 促銷活動
    private LocalDateTime createdAt;

    // 🔹 Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMerchantTradeNo() {
        return merchantTradeNo;
    }

    public void setMerchantTradeNo(String merchantTradeNo) {
        this.merchantTradeNo = merchantTradeNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPromotionMember() {
        return promotionMember;
    }

    public void setPromotionMember(String promotionMember) {
        this.promotionMember = promotionMember;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
