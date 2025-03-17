package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDate;
//import java.math.BigDecimal;
//import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "t35_event_ticket_orders", 
       indexes = {@Index(columnList = "member_id"), @Index(columnList = "event_id")})
public class T35TicketOrderBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_orders_id")
    private Long id;
    
    // 綠界交易編號
    @Column(name = "merchant_trade_no", unique = true) 
    private String merchantTradeNo; 

    // 關聯至會員（購買人）
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id",nullable = false)
    private T41MemberBean member;

    // 關聯至活動
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private T31EventBean event;

    // 訂單購買時間（自動填入當前時間）
    @CreationTimestamp
    @Column(name = "purchase_date", columnDefinition = "DATE", nullable = false, updatable = false)
    private LocalDate purchaseDate;

    // 票價
    @Column(name = "ticket_price", nullable = false)
    private Integer ticketPrice;

    // 折扣後金額
    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;

    // 票券狀態
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    public enum OrderStatus {
        UNPAID,     // 未付款
        PAID,    // 已付款
        MAILED,  // 已郵寄出郵件
        CANCELLED,   // 已取消
        REFUNDED   //已經退款
    }

    // 若使用優惠券
    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = true)
    private T27PromotionMemberBean usedCoupon;
    
    // ===========================
    //         Getter / Setter
    // ===========================

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMerchantTradeNo() {
		return merchantTradeNo;
	}

	public void setMerchantTradeNo(String merchantTradeNo) {
		this.merchantTradeNo = merchantTradeNo;
	}

	public T41MemberBean getMember() {
		return member;
	}

	public void setMember(T41MemberBean member) {
		this.member = member;
	}

	public T31EventBean getEvent() {
		return event;
	}

	public void setEvent(T31EventBean event) {
		this.event = event;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate localDate) {
		this.purchaseDate = localDate;
	}

	public Integer getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(Integer ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public Integer getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(Integer finalPrice) {
		this.finalPrice = finalPrice;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public T27PromotionMemberBean getUsedCoupon() {
		return usedCoupon;
	}

	public void setUsedCoupon(T27PromotionMemberBean usedCoupon) {
		this.usedCoupon = usedCoupon;
	}
}
