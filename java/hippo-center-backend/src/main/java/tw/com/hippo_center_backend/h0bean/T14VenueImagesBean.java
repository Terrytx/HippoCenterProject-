package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t14_venue_images")
public class T14VenueImagesBean {
	// 圖片編號
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    // 館場編號
	@ManyToOne
	@JoinColumn(name = "venue_id", nullable = false)
	@JsonBackReference
    private T51VenueBean venue;

    // 圖片路徑
    @Column(name = "image_url" , length = 500)
    private String imageUrl;
    
    // (新增- 以BLOB直接存入資料庫)
    @Column(name = "image_data")
    @Lob  
    private byte[] imageData;

    // 圖片替代文字
    @Column(name = "alt_text", length = 255)
    private String altText;

    // 圖片顯示順序
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    // 創建時間
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public T51VenueBean getVenue() {
		return venue;
	}

	public void setVenue(T51VenueBean venue) {
		this.venue = venue;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public byte[] getImageData() {
		return imageData;
	}


	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}


	public String getAltText() {
		return altText;
	}


	public void setAltText(String altText) {
		this.altText = altText;
	}


	public Integer getSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
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

	@Override
	public String toString() {
		return "T14VenueImagesBean [imageId=" + imageId + ", venue=" + venue + ", imageUrl=" + imageUrl + ", imageData="
				+ Arrays.toString(imageData) + ", altText=" + altText + ", sortOrder=" + sortOrder + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}

	public T14VenueImagesBean() {
		super();
		this.imageId = imageId;
		this.venue = venue;
		this.imageUrl = imageUrl;
		this.imageData = imageData;
		this.altText = altText;
		this.sortOrder = sortOrder;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public T14VenueImagesBean(Integer imageId, T51VenueBean venue, String imageUrl, byte[] imageData, String altText,
			Integer sortOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.imageId = imageId;
		this.venue = venue;
		this.imageUrl = imageUrl;
		this.imageData = imageData;
		this.altText = altText;
		this.sortOrder = sortOrder;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}





	
	





}
