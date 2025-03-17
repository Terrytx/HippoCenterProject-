//package tw.com.hippo_center_backend.h0bean;
//
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "t36_carousel_images")
//public class T36CarouselImagesBean {
//    
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "carosel_id")
//    private Integer caroselId; //輪播圖id自動產生
//    
//	
//	@Column(name = "sort_order")
//    private Integer sortOrder;
//    
//    @Column(name = "is_active")
//    private Boolean isActive;
//    
//    @Column(name = "target", length = 10)
//    private String target;
//    
//    @Lob
//    @Column(name = "carousel_image", columnDefinition = "LONGBLOB")
//    private byte[] carouselImage;
//    
//    @Column(name = "carosel_status")
//    private Boolean caroselStatus;
//    
//    @Column(name = "start_date")
//    private LocalDateTime startDate;
//    
//    @Column(name = "end_date")
//    private LocalDateTime endDate;
//    
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//    
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//    
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//    
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//
//    // toString 方法
//   
//    @Override
//   	public String toString() {
//   		return "T36CarouselImagesBean [caroselId=" + caroselId + ", sortOrder=" + sortOrder + ", isActive=" + isActive
//   				+ ", target=" + target + ", carouselImage=" + Arrays.toString(carouselImage) + ", caroselStatus="
//   				+ caroselStatus + ", startDate=" + startDate + ", endDate=" + endDate + ", createdAt=" + createdAt
//   				+ ", updatedAt=" + updatedAt + "]";
//   	}
//
//    // Getters and Setters
//    public Integer getCaroselId() {
//		return caroselId;
//	}
//
//	public void setCaroselId(Integer caroselId) {
//		this.caroselId = caroselId;
//	}
//
//	public Integer getSortOrder() {
//		return sortOrder;
//	}
//
//	public void setSortOrder(Integer sortOrder) {
//		this.sortOrder = sortOrder;
//	}
//
//	public Boolean getIsActive() {
//		return isActive;
//	}
//
//	public void setIsActive(Boolean isActive) {
//		this.isActive = isActive;
//	}
//
//	public String getTarget() {
//		return target;
//	}
//
//	public void setTarget(String target) {
//		this.target = target;
//	}
//
//	public byte[] getCarouselImage() {
//		return carouselImage;
//	}
//
//	public void setCarouselImage(byte[] carouselImage) {
//		this.carouselImage = carouselImage;
//	}
//
//	public Boolean getCaroselStatus() {
//		return caroselStatus;
//	}
//
//	public void setCaroselStatus(Boolean caroselStatus) {
//		this.caroselStatus = caroselStatus;
//	}
//
//	public LocalDateTime getStartDate() {
//		return startDate;
//	}
//
//	public void setStartDate(LocalDateTime startDate) {
//		this.startDate = startDate;
//	}
//
//	public LocalDateTime getEndDate() {
//		return endDate;
//	}
//
//	public void setEndDate(LocalDateTime endDate) {
//		this.endDate = endDate;
//	}
//
//	public LocalDateTime getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(LocalDateTime createdAt) {
//		this.createdAt = createdAt;
//	}
//
//	public LocalDateTime getUpdatedAt() {
//		return updatedAt;
//	}
//
//	public void setUpdatedAt(LocalDateTime updatedAt) {
//		this.updatedAt = updatedAt;
//	}
//
//}