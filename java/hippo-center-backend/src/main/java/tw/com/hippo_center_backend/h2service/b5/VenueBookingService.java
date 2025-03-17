package tw.com.hippo_center_backend.h2service.b5;

import java.time.LocalDateTime;

import tw.com.hippo_center_backend.h0bean.T52BookingBean;

public interface VenueBookingService {
	default void setCreateBookingDatetime(T52BookingBean bean) {
      	LocalDateTime now = LocalDateTime.now();
      	bean.setBookingDatetime(now);
      	}

	}

