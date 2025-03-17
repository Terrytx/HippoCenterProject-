package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t21_product")
public class T21ProductBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;// 產品ID

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;// 產品名稱

    @Column(name = "description", length = 500)
    private String description; // 產品描述

    @ManyToOne
    @JoinColumn(name = "t22_category_id", nullable = false)
    private T22CategoriesBean category;// 產品類別ID

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<T28ProductImageBean> images; // 產品圖片

    @Column(name = "price", nullable = false)
    private Integer price; // 產品價格

    @Column(name = "stock", nullable = false)
    private Integer stock; // 產品庫存

    @Column(name = "status", length = 50, nullable = false)
    private String status; // 產品狀態

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 產品建立時間

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 產品更新時間

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T24OrderDetailsBean> orderDetails;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T22CategoriesBean getCategory() {
        return category;
    }

    public void setCategory(T22CategoriesBean category) {
        this.category = category;
    }

    public List<T28ProductImageBean> getImages() {
        return images;
    }

    public void setImages(List<T28ProductImageBean> images) {
        this.images = images;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "T21ProductBean [productId=" + productId + ", productName=" + productName +
                ", price=" + price + ", stock=" + stock + ", status=" + status + "]";
    }

}