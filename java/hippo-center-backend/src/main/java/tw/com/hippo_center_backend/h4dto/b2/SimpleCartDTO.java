package tw.com.hippo_center_backend.h4dto.b2;

import java.time.LocalDateTime;

public class SimpleCartDTO {
    private Integer memberId;
    private Integer productId;
    private Integer quantity;
    private LocalDateTime addedAt;

    public SimpleCartDTO(Integer memberId, Integer productId, Integer quantity, LocalDateTime addedAt) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    // Getters
    public Integer getMemberId() { return memberId; }
    public Integer getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public LocalDateTime getAddedAt() { return addedAt; }
}
