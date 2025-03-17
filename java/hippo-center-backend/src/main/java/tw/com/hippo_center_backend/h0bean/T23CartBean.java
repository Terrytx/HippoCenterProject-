package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t23_cart")
public class T23CartBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId; // 購物車ID

    @ManyToOne
    @JoinColumn(name = "t41_member_id", nullable = false)
    private T41MemberBean member; // 會員ID

    @ManyToOne
    @JoinColumn(name = "t21_product_id", nullable = false)
    private T21ProductBean product; // 產品ID

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 數量

    @Column(name = "added_at")
    private LocalDateTime addedAt; // 加入時間

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public T41MemberBean getMember() {
        return member;
    }

    public void setMember(T41MemberBean member) {
        this.member = member;
    }

    public T21ProductBean getProduct() {
        return product;
    }

    public void setProduct(T21ProductBean product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) { // 非負數限制
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        this.quantity = quantity;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "T23CartBean [cartId=" + cartId + ", quantity=" + quantity + "]";
    }

}
