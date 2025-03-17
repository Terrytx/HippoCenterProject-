package tw.com.hippo_center_backend.h4dto.b2;

public class OrderRequest {
    private Integer memberId;
    private String promotionMemberId;
    private String address;
    private String phone;

    // Getters and Setters
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    
}
