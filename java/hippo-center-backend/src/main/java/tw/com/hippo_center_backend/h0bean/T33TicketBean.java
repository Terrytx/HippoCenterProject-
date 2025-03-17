package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "t33_ticket")
public class T33TicketBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "ticket_amount", nullable = false)
    private Integer ticketAmount;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private T41MemberBean memberId;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private T31EventBean eventId;

    @JsonIgnore
    @Column(name = "event_price", nullable = false)
    private Integer eventPrice;

    @ManyToOne
    @JoinColumn(name = "promotion_member_id", referencedColumnName = "promotion_member_id")
    @JsonIgnore
    private T27PromotionMemberBean promotionMember;

    private String qrCodeContent;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "qr_code", length = 500)
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "ticket_order_id", referencedColumnName = "ticket_orders_id", nullable = false)
    @JsonIgnore
    private T35TicketOrderBean ticketOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false)
    private TicketStatus ticketStatus = TicketStatus.VALID;

    public enum TicketStatus {
        VALID(1, "有效"),
        USED(2, "已使用"),
        REFUNDED(3, "已退款");

        private final int code;
        private final String description;

        TicketStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static TicketStatus getByCode(int code) {
            for (TicketStatus status : values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid ticket status code: " + code);
        }

        public boolean isUsable() {
            return this == VALID;
        }

        public boolean isRefundable() {
            return this == VALID;
        }
    }

    @Override
    public String toString() {
        return "T33TicketBean [ticketId=" + ticketId + ", ticketAmount=" + ticketAmount + ", memberId=" + memberId
                + ", eventId=" + eventId + ", eventPrice=" + eventPrice + ", promotionMember=" + promotionMember
                + ", purchaseDate=" + purchaseDate + ", qrCode=" + qrCode + ", ticketStatus=" + ticketStatus.getDescription() + "]";
    }

    // Getters and Setters
    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getTicketAmount() {
        return ticketAmount;
    }

    public void setTicketAmount(Integer ticketAmount) {
        this.ticketAmount = ticketAmount;
    }

    public T41MemberBean getMemberId() {
        return memberId;
    }

    public void setMemberId(T41MemberBean memberId) {
        this.memberId = memberId;
    }

    public T31EventBean getEventId() {
        return eventId;
    }

    public void setEventId(T31EventBean eventId) {
        this.eventId = eventId;
    }

    public Integer getEventPrice() {
        return eventPrice;
    }

    public void setEventPrice(Integer eventPrice) {
        this.eventPrice = eventPrice;
    }

    public T27PromotionMemberBean getPromotionMember() {
        return promotionMember;
    }

    public void setPromotionMember(T27PromotionMemberBean promotionMember) {
        this.promotionMember = promotionMember;
    }

    public String getQrCodeContent() {
        return qrCodeContent;
    }

    public void setQrCodeContent(String qrCodeContent) {
        this.qrCodeContent = qrCodeContent;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public T35TicketOrderBean getTicketOrder() {
        return ticketOrder;
    }

    public void setTicketOrder(T35TicketOrderBean ticketOrder) {
        this.ticketOrder = ticketOrder;
    }
}