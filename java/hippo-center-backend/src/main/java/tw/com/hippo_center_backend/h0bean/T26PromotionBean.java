package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t26_promotion")
public class T26PromotionBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Integer promotionId; // 促銷ID 

    @Column(name = "title", length = 100, nullable = false)
    private String title; // 標題

    @Column(name = "promotion_code", length = 20, nullable = false)
    private String promotionCode; // 促銷編號，

    @Column(name = "description", length = 500)
    private String description; // 描述

    @Column(name = "discount_rate", nullable = false)
    private double discountRate; // 折扣率

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // 開始日期

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 結束日期

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T27PromotionMemberBean> promotionMembers;

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
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

    public List<T27PromotionMemberBean> getPromotionMembers() {
        return promotionMembers;
    }

    public void setPromotionMembers(List<T27PromotionMemberBean> promotionMembers) {
        this.promotionMembers = promotionMembers;
    }

    @Override
    public String toString() {
        return "T26PromotionBean [promotionId=" + promotionId + ", title=" + title
                + ", promotionCode=" + promotionCode + ", discountRate=" + discountRate
                + ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }

}
