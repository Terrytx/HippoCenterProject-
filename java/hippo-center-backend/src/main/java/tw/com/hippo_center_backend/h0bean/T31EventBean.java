package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "t31_event")
public class T31EventBean {

  @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動產生的id流水號
   @Column(name = "event_id")
   private Integer eventId;

  @Column(name = "event_name", nullable = false)
   @Size(min = 2, max = 100, message = "活動名稱長度須介於 2 到 100 字之間")
   private String eventName;   //活動名稱

   @ManyToOne//(fetch = FetchType.LAZY)
   @JoinColumn(name = "event_category_id", referencedColumnName = "event_category_id")
   private T32EventCategoryBean eventCategory; // 活動類別實體
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "venue_id", referencedColumnName = "venue_id")
   private T51VenueBean venue; // venue場館 實體

   @Column(name = "event_description_1", nullable = false)
   @Size(min = 10, max = 5000, message = "活動描述長度須介於 10到1000字之間")
   @NotNull(message = "活動描述不得為空")
   private String eventDescription1;
   
   @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
   private List<T34EventImagesBean> images; // 活動圖片
   
   // 新增方法，返回所有圖片的 URL
   public List<String> getImageUrls() {
       if (images != null) {
           return images.stream()
                        .map(T34EventImagesBean::getImageUrl)
                        .collect(Collectors.toList());
       }
       return List.of(); // 如果 images 為 null，返回空列表
   }

   @Column(name = "event_price", nullable = false)
   @Min(value = 0, message = "價格不能為負數")
   @Max(value = 1000, message = "價格不能超過 1000")
   private Integer eventPrice;

   @Column(name = "event_creation_date") //活動資訊創立時間
   private LocalDate eventCreationDate;

   @Column(name = "event_start_date") //活動開始時間
   private LocalDate eventStartDate;

   @Column(name = "event_end_date") //活動結束時間
   private LocalDate eventEndDate;
   
   @Column(name = "is_published", nullable = false)
   private Boolean isPublished = false; // 預設為未發布狀態  //當 isPublished 設為 true 時，同時更新 eventPublishTime

   @Column(name = "event_publish_date") //活動資訊發布日期 // 當活動從發布狀態改為未發布時，可以選擇是否清空 eventPublishTime
   private LocalDate eventPublishDate;

   @Column(name = "event_update_date") //活動資訊更新日期
   private LocalDate eventUpdateDate;
   
   @Override
   public String toString() {
   	return "T31EventBean [eventId=" + eventId + ", eventName=" + eventName + ", eventCategory=" + eventCategory
   			+ ", venue=" + venue + ", eventDescription1=" + eventDescription1 + ", images=" + images + ", eventPrice="
   			+ eventPrice + ", eventCreationDate=" + eventCreationDate + ", eventStartDate=" + eventStartDate
   			+ ", eventEndDate=" + eventEndDate + ", isPublished=" + isPublished + ", eventPublishDate="
   			+ eventPublishDate + ", eventUpdateDate=" + eventUpdateDate + "]";
   }
  
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


public T32EventCategoryBean getEventCategory() {
	return eventCategory;
}


public void setEventCategory(T32EventCategoryBean eventCategory) {
	this.eventCategory = eventCategory;
}


public T51VenueBean getVenue() {
	return venue;
}


public void setVenue(T51VenueBean venue) {
	this.venue = venue;
}


public String getEventDescription1() {
	return eventDescription1;
}


public void setEventDescription1(String eventDescription1) {
	this.eventDescription1 = eventDescription1;
}


public List<T34EventImagesBean> getImages() {
	return images;
}


public void setImages(List<T34EventImagesBean> images) {
	this.images = images;
}


public Integer getEventPrice() {
	return eventPrice;
}


public void setEventPrice(Integer eventPrice) {
	this.eventPrice = eventPrice;
}


public LocalDate getEventCreationDate() {
	return eventCreationDate;
}


public void setEventCreationDate(LocalDate eventCreationDate) {
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


public LocalDate getEventPublishDate() {
	return eventPublishDate;
}


public void setEventPublishDate(LocalDate eventPublishDate) {
	this.eventPublishDate = eventPublishDate;
}


public LocalDate getEventUpdateDate() {
	return eventUpdateDate;
}


public void setEventUpdateDate(LocalDate eventUpdateDate) {
	this.eventUpdateDate = eventUpdateDate;
}

   

}

