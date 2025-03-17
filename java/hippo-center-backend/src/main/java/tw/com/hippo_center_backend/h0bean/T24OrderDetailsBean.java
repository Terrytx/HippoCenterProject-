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
@Table(name = "t24_order_details")
public class T24OrderDetailsBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId; // 訂單明細ID

    @ManyToOne
    @JoinColumn(name = "t25_order_id", nullable = false)
    private T25OrderListBean order; // 訂單ID

    @ManyToOne
    @JoinColumn(name = "t21_product_id", nullable = false)
    private T21ProductBean product; // 產品ID

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 數量

    @Column(name = "added_at")
    private LocalDateTime addedAt; // 加入時間

    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public T25OrderListBean getOrder() {
        return order;
    }

    public void setOrder(T25OrderListBean order) {
        this.order = order;
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

    public void setQuantity(Integer quantity) {
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
        return "T24OrderDetailsBean [orderDetailId=" + orderDetailId + ", quantity=" + quantity + "]";
    }

}
