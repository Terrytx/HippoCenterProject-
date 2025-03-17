package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t25_order_list")
public class T25OrderListBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId; // 訂單ID
    
    @Column(name = "merchant_trade_no", unique = true)  // 綠界交易編號
    private String merchantTradeNo;

    @ManyToOne
    @JoinColumn(name = "t41_member_id", nullable = false)
    private T41MemberBean member; // 會員ID

    @ManyToOne
    @JoinColumn(name = "t27_promotion_member_id")
    private T27PromotionMemberBean promotionMember; // 促銷會員ID

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount; // 總金額

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 50, nullable = false)
    private OrderStatus orderStatus; // 訂單狀態

    @Column(name = "order_address", length = 255, nullable = false)
    private String orderAddress; // 訂單地址


    @Column(name = "phone") // 聯絡電話
    private String phone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 建立時間

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新時間

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T24OrderDetailsBean> orderDetails;

    public enum OrderStatus {
        NEW, // 新訂單
        PAID, // 已付款 
        SHIPPED, // 已寄出
        DELIVERED, // 已送達
        CANCELLED // 已取消
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public T41MemberBean getMember() {
        return member;
    }

    public void setMember(T41MemberBean member) {
        this.member = member;
    }

    public T27PromotionMemberBean getPromotionMember() {
        return promotionMember;
    }

    public void setPromotionMember(T27PromotionMemberBean promotionMember) {
        this.promotionMember = promotionMember;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<T24OrderDetailsBean> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<T24OrderDetailsBean> orderDetails) {
        this.orderDetails = orderDetails;
    }

    
    public String getMerchantTradeNo() {
        return merchantTradeNo;
    }

    public void setMerchantTradeNo(String merchantTradeNo) {
        this.merchantTradeNo = merchantTradeNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "T25OrderListBean [orderId=" + orderId + ", orderStatus=" + orderStatus + "]";
    }

}