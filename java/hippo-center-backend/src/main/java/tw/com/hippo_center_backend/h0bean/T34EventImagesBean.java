package tw.com.hippo_center_backend.h0bean;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "t34_event_image")
public class T34EventImagesBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id") //自動產生ID 
    private Integer imageId;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;  //儲存IMAGE的URL
    
    @Column(name = "is_cover", nullable = false)
    private Boolean isCover = false; //是否為封面照片
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id", nullable = false)
    private T31EventBean event; //關聯活動
    
    @Column(name = "alt_text", length = 255)
    private String altText; //無法顯示圖片的替代文字

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder; //圖片排列

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; //圖片創建/上傳時間

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; //圖片更新時間

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
	    
	    @Override
		public String toString() {
			return "T34EventImagesBean [imageId=" + imageId + ", imageUrl=" + imageUrl + ", isCover=" + isCover
					+ ", event=" + event + ", altText=" + altText + ", sortOrder=" + sortOrder + ", createdAt="
					+ createdAt + ", updatedAt=" + updatedAt + "]";
		}

	      
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

		public Boolean getIsCover() {
			return isCover;
		}

		public void setIsCover(Boolean isCover) {
			this.isCover = isCover;
		}

		public T31EventBean getEvent() {
			return event;
		}

		public void setEvent(T31EventBean event) {
			this.event = event;
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
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	}
	
	
	
	
	
	    
	    
	    
