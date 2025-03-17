package tw.com.hippo_center_backend.h4dto.b1;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



//活動圖片DTO
@JsonIgnoreProperties(value = {
  "imageId", 
  "isCover", 
  "altText", 
  "sortOrder", 
  "createdAt", 
  "updatedAt"
})

//活動圖片DTO
public class EventImageDTO {
	
	private Integer imageId;
    private String imageUrl;
    private Boolean isCover;
    private String altText;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
 // Getters and Setters
    // ... [所有字段的getter和setter方法]
	
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