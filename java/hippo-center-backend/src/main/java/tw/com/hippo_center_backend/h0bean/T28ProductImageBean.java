package tw.com.hippo_center_backend.h0bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t28_product_image")
public class T28ProductImageBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId; // 圖片ID

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl; // 圖片URL

    @ManyToOne
    @JoinColumn(name = "t21_product_id", nullable = false)
    private T21ProductBean product; // 關聯的產品

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public T21ProductBean getProduct() {
        return product;
    }

    public void setProduct(T21ProductBean product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "T28ProductImageBean [imageId=" + imageId + ", imageUrl=" + imageUrl + "]";
    }

}
