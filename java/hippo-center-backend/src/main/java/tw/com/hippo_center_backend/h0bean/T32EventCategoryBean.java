package tw.com.hippo_center_backend.h0bean;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;



@Entity
@Table(name = "t32_event_category")
public class T32EventCategoryBean {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY) //自動產生的id流水號
   @Column(name = "event_category_id")
   private Integer eventCategoryId;
   
   @Column(name = "event_category_name")
   private String eventCategoryName;

   @Column(name = "event_category_type")
   private Integer eventCategoryType;

   @Column(name = "event_category_description" ,length = 255)
   private String eventCategoryDescription;

   @OneToMany(mappedBy = "eventCategory", cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonIgnore
   private List<T31EventBean> events; // 建立與 T31_EventBean 的一對多關係

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

   //public List<T31EventBean> getEvents() {
   //   return events;
  // }

  // public void setEvents(List<T31EventBean> events) {
  //    this.events = events;
  // }

@Override
   public String toString() {
      return "T32EventCategoryBean [eventCategoryId=" + eventCategoryId + ", eventCategoryType=" + eventCategoryType
            + ", eventCategoryDescription=" + eventCategoryDescription + "]";  //, events=" + events + "
   }

// 無參數構造方法（必要）
public T32EventCategoryBean() {}

// 帶參數構造方法
public T32EventCategoryBean(Integer eventCategoryId, String eventCategoryDescription, Integer eventCategoryType) {
    this.eventCategoryId = eventCategoryId;
    this.eventCategoryDescription = eventCategoryDescription;
    this.eventCategoryType = eventCategoryType;
}

}