package tw.com.hippo_center_backend.h4dto.b1;
import java.time.LocalDate;



import java.util.List;


public class EventResponseDTO {
	        private Integer eventId;
            private String eventName;
            private LocalDate eventStartDate;
            private LocalDate eventEndDate;
            private LocalDate eventUpdateDate;
            private Integer eventPrice;
            private String eventCategory;
            private String venueName;  // 新增這個字段
			//private VenueDTO venue;
            private String eventDescription1;
            private List<EventImageDTO> images;

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

             public LocalDate getEventUpdateDate() {
                    return eventUpdateDate;
                }
                public void setEventUpdateDate(LocalDate eventUpdateDate) {
                    this.eventUpdateDate = eventUpdateDate;
                }
            public Integer getEventPrice() {
                return eventPrice;
            }
            public void setEventPrice(Integer eventPrice) {
                this.eventPrice = eventPrice;
            }
            public String getEventCategory() {
                return eventCategory;
            }
            public void setEventCategory(String eventCategory) {
                this.eventCategory = eventCategory;
            }
            public String getVenueName() {
				return venueName;
			}
			public void setVenueName(String venueName) {
				this.venueName = venueName;
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
                
//                public String getImageUrl() {
//    				return imageUrl;
//    			}
//    			public void setImageUrl(String imageUrl) {
//    				this.imageUrl = imageUrl;
//    			}

}