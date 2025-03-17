package tw.com.hippo_center_backend.h4dto.b1;

//import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

    //前端傳入的活動請求DTO
public class EventDTO {
    public static class EventRequest {
    	
    	
		private String eventName;
        private String eventCategoryName;
		private String venueName;
        private String eventDescription1;
        private List<String> imagePaths;
        private Integer eventPrice; 
        private Integer eventCreationDate;
        private LocalDate eventStartDate;
        private LocalDate eventEndDate;
        private boolean isPublished;
        private LocalDate eventPublishTime;
        private LocalDate eventUpdateDate;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
    	
        private Integer eventId;
        public Integer getEventId() {
			return eventId;
		}
		public void setEventId(Integer eventId) {
			this.eventId = eventId;
		}
		public String getEventName() {
			return eventName;
		}
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		public String getEventCategoryName() {
			return eventCategoryName;
		}
		public void setEventCategoryName(String eventCategoryName) {
			this.eventCategoryName = eventCategoryName;
		}
//		public Integer getEventCategoryName() {
//			return eventCategoryName;
//		}
//		public void setEventCategoryName(Integer eventCategoryName) {
//			this.eventCategoryName = eventCategoryName;
//		}
		public String getVenueName() {
			return venueName;
		}
		public void setVenueName(String venueName) {
			this.venueName = venueName;
		}
		public String getEventDescription1() {
			return eventDescription1;
		}
		public void setEventDescription1(String eventDescription1) {
			this.eventDescription1 = eventDescription1;
		}
		public List<String> getImagePaths() {
			return imagePaths;
		}
		public void setImagePaths(List<String> imagePaths) {
			this.imagePaths = imagePaths;
		}
		public Integer getEventPrice() {
			return eventPrice;
		}
		public void setEventPrice(Integer eventPrice) {
			this.eventPrice = eventPrice;
		}
		public Integer getEventCreationDate() {
			return eventCreationDate;
		}
		public void setEventCreationDate(Integer eventCreationDate) {
			this.eventCreationDate = eventCreationDate;
		}
		public LocalDate getEventStartDate() {
			return eventStartDate;
		}
		public void setEventStartDate(LocalDate eventStartDate) {
			this.eventStartDate = eventStartDate;
		}
		public LocalDate getEventEndDate() {
			return eventEndDate;
		}
		public void setEventEndDate(LocalDate eventEndDate) {
			this.eventEndDate = eventEndDate;
		}
		public Boolean getIsPublished() {
			return isPublished;
		}
		public void setIsPublished(Boolean isPublished) {
			this.isPublished = isPublished;
		}
		public LocalDate getEventPublishTime() {
			return eventPublishTime;
		}
		public void setEventPublishTime(LocalDate eventPublishTime) {
			this.eventPublishTime = eventPublishTime;
		}
		public LocalDate getEventUpdateDate() {
			return eventUpdateDate;
		}
		public void setEventUpdateDate(LocalDate eventUpdateTime) {
			this.eventUpdateDate = eventUpdateTime;
		}
   

		
    }
      //回傳前端的活動訊息dto
    public static class EventResponse {
    	
    	private Integer eventId;
        private String eventName;
        private LocalDate eventStartDate;
        private LocalDate eventEndDate;
        private LocalTime eventUpdateTime;
        private Integer eventPrice;
        private EventCategoryDTO eventCategory;
        private VenueDTO venue;
        private List<EventImageDTO> images;
		private String eventDescription1;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
    		
        public Integer getEventId() {
			return eventId;
		}
		public void setEventId(Integer eventId) {
			this.eventId = eventId;
		}
		public String getEventName() {
			return eventName;
		}
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		public LocalDate getEventStartDate() {
			return eventStartDate;
		}
		public void setEventStartDate(LocalDate eventStartDate) {
			this.eventStartDate = eventStartDate;
		}
		public LocalDate getEventEndDate() {
			return eventEndDate;
		}
		public void setEventEndDate(LocalDate eventEndDate) {
			this.eventEndDate = eventEndDate;
		}
	
		public LocalTime getEventUpdateTime() {
			return eventUpdateTime;
		}
		public void setEventUpdateTime(LocalTime eventUpdateTime) {
			this.eventUpdateTime = eventUpdateTime;
		}
		public Integer getEventPrice() {
			return eventPrice;
		}
		public void setEventPrice(Integer eventPrice) {
			this.eventPrice = eventPrice;
		}
		public EventCategoryDTO getEventCategory() {
			return eventCategory;
		}
		public void setEventCategory(EventCategoryDTO eventCategory) {
			this.eventCategory = eventCategory;
		}
		public VenueDTO getVenue() {
			return venue;
		}
		public void setVenue(VenueDTO venue) {
			this.venue = venue;
		}
		public List<EventImageDTO> getImages() {
			return images;
		}
		public void setImages(List<EventImageDTO> images) {
			this.images = images;
		}
		  public String getEventDescription1() {
				return eventDescription1;
			}
			public void setEventDescription1(String eventDescription1) {
				this.eventDescription1 = eventDescription1;
			}
		
    }

        //頁面card動態載入用dto
    public static class EventListResponse {
        private String eventName;
        private String categoryName;
        private LocalDate eventStartDate;
        private LocalDate eventEndDate;
        private String mainImageUrl;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
        
		public String getEventName() {
			return eventName;
		}
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public LocalDate getEventStartDate() {
			return eventStartDate;
		}
		public void setEventStartDate(LocalDate eventStartDate) {
			this.eventStartDate = eventStartDate;
		}
		public LocalDate getEventEndDate() {
			return eventEndDate;
		}
		public void setEventEndDate(LocalDate eventEndDate) {
			this.eventEndDate = eventEndDate;
		}
		public String getMainImageUrl() {
			return mainImageUrl;
		}
		public void setMainImageUrl(String mainImageUrl) {
			this.mainImageUrl = mainImageUrl;
		}
		
    }
}
