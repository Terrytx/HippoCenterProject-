package tw.com.hippo_center_backend.h4dto.b1;

//類別DTO
public class EventCategoryDTO {
	
	private Integer eventCategoryId; //類別ID
	private String eventCategoryName; //類別名稱
	private Integer eventCategoryType; //類別類型代碼
    private String eventCategoryDescription; //類別敘述
    
    // Getters and Setters
    // ... [所有字段的getter和setter方法]
	
    public Integer getEventCategoryId() {
		return eventCategoryId;
	}
	public void setEventCategoryId(Integer eventCategoryId) {
		this.eventCategoryId = eventCategoryId;
	}
	
	 public String getEventCategoryName() {
			return eventCategoryName;
	}
	public void setEventCategoryName(String eventCategoryName) {
			this.eventCategoryName = eventCategoryName;
	}
	
	public Integer getEventCategoryType() {
		return eventCategoryType;
	}
	public void setEventCategoryType(Integer eventCategoryType) {
		this.eventCategoryType = eventCategoryType;
	}
	public String getEventCategoryDescription() {
		return eventCategoryDescription;
	}
	public void setEventCategoryDescription(String eventCategoryDescription) {
		this.eventCategoryDescription = eventCategoryDescription;
	}
	
}