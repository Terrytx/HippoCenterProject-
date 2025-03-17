package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t27_promotion_member")
public class T27PromotionMemberBean {

    @Id
    @Column(name = "promotion_member_id", length = 20)
    private String promotionMemberId; // 自訂的主鍵，格式：202501_M1_23479

    @ManyToOne
    @JoinColumn(name = "t41_member_id", nullable = false)
    private T41MemberBean member; // 會員ID

    @ManyToOne
    @JoinColumn(name = "t26_promotion_id", nullable = false)
    private T26PromotionBean promotion; // 促銷ID

    @Column(name = "promotion_type", length = 50, nullable = false)
    private String promotionType; // 促銷類型（BIRTHDAY 或 MONTHLY）

    @Column(name = "start_date")
    private LocalDateTime startDate; // 開始日期

    @Column(name = "end_date")
    private LocalDateTime endDate; // 結束日期

    @Column(name = "promotion_status")
    @Enumerated(EnumType.STRING)
    private PromotionStatus promotionStatus; // 促銷狀態（NEW、EXPIRED、USED）

    @Column(name = "added_at")
    private LocalDateTime addedAt; // 新增時間

    @Column(name = "used_at")
    private LocalDateTime usedAt; // 使用時間

    // 🧩 自訂主鍵生成方法
    public static String generatePromotionMemberId(Integer memberId, String promotionCode) {
        String uniqueId = String.valueOf(System.currentTimeMillis()); // 轉成字串
        return promotionCode + "_M" + memberId + "_" + uniqueId.substring(uniqueId.length() - 5); // 取最後 5 碼
    }

    public enum PromotionStatus {
        NEW, // 新發放的促銷券
        ACTIVE, // 啟用中的促銷券,但尚未使用
        USED, // 會員已使用的促銷券
        EXPIRED // 促銷券已過期
    }

    // 預設建構子
    public T27PromotionMemberBean() {
    }

    public T27PromotionMemberBean(T41MemberBean member, T26PromotionBean promotion, String promotionType) {
        this.promotionMemberId = generatePromotionMemberId(member.getMemberId(), promotion.getPromotionCode());
        this.member = member;
        this.promotion = promotion;
        this.promotionType = promotionType;
        this.startDate = promotion.getStartDate();
        this.endDate = promotion.getEndDate();
        this.promotionStatus = PromotionStatus.NEW;
        this.addedAt = LocalDateTime.now();
    }

    // 方法：檢查促銷券是否有效
    public boolean isValid() {
        return PromotionStatus.NEW.equals(this.promotionStatus) && LocalDateTime.now().isBefore(this.endDate);
    }

    // 方法：標記促銷券為已過期
    public void markAsExpired() {
        this.promotionStatus = PromotionStatus.EXPIRED;
    }

    // 方法：標記促銷券為已使用
    public void markAsUsed() {
        this.promotionStatus = PromotionStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    // Getter 和 Setter
    public String getPromotionMemberId() {
        return promotionMemberId;
    }

    public void setPromotionMemberId(String promotionMemberId) {
        this.promotionMemberId = promotionMemberId;
    }

    public T41MemberBean getMember() {
        return member;
    }

    public void setMember(T41MemberBean member) {
        this.member = member;
    }

    public T26PromotionBean getPromotion() {
        return promotion;
    }

    public void setPromotion(T26PromotionBean promotion) {
        this.promotion = promotion;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
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

    public PromotionStatus getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(PromotionStatus promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    @Override
    public String toString() {
        return "T27PromotionMemberBean [promotionMemberId=" + promotionMemberId + ", promotionType=" + promotionType
                + ", promotionStatus=" + promotionStatus.name() + "]";
    }

}
