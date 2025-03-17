package tw.com.hippo_center_backend.h4dto.b5;

import java.util.Arrays;

// 接收排序數據 -- 館場圖片使用
public class VenueImageOrderDTO {
	private Integer imageId;
    private String venueId;  // 只包含 ID，不包含整個對象
	private byte[] imageData;
    private String altText;
    private Integer sortOrder;
    
	public VenueImageOrderDTO(Integer imageId, byte[] imageData, String altText, Integer sortOrder) {
		super();
		this.imageId = imageId;
		this.imageData = imageData;
		this.altText = altText;
		this.sortOrder = sortOrder;
	}
	public Integer getImageId() {
		return imageId;
	}
	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}
	public String getVenueId() {
		return venueId;
	}
	public void setVenueId(String venueId) {
		this.venueId = venueId;
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
	@Override
	public String toString() {
		return "VenueImageDTO [imageId=" + imageId + ", venueId=" + venueId + ", imageData="
				+ Arrays.toString(imageData) + ", altText=" + altText + ", sortOrder=" + sortOrder + "]";
	}
	public VenueImageOrderDTO(Integer imageId, String venueId, byte[] imageData, String altText, Integer sortOrder) {
		super();
		this.imageId = imageId;
		this.venueId = venueId;
		this.imageData = imageData;
		this.altText = altText;
		this.sortOrder = sortOrder;
	}
	
	
	
	 
}
