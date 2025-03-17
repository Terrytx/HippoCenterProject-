package tw.com.hippo_center_backend.h2service.b5;

import java.time.LocalDateTime;

import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;

public interface VenueStatusService {
	
	  default void setUpdatedDateTime(T53VenueStatusBean bean) {
      	LocalDateTime now = LocalDateTime.now();
      	bean.setUpdatedTime(now);}
	  
	  default void setCreatedDateTime(T53VenueStatusBean bean) {
      if (bean.getCreatedTime() == null) {  // 如果沒有創建時間才設置
          LocalDateTime now = LocalDateTime.now();
          bean.setCreatedTime(now);
      }
  }

	T53VenueStatusBean createVenueStatus(T53VenueStatusBean bean);
	T53VenueStatusBean updateVenueStatus(T53VenueStatusBean bean);

	boolean delete(T53VenueStatusBean bean);

	

}
