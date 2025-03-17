package tw.com.hippo_center_backend.h2service.b5;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;

public interface VenueService {
	T51VenueBean insert(T51VenueBean bean); // 增加場地

	T51VenueBean findById(String id); // 場地查詢

	List<T51VenueBean> findAll(T51VenueBean bean); // 查詢所有場地

	List<T51VenueBean> findAllAvailableVenues(); // 所有場地中 提供租借服務的

	T51VenueBean uploadRentalRegulation(String venueId, MultipartFile mockFile);

	// byte[] downloadRentalRegulation(String venueId);
	byte[] downloadTechnicalSpecifications(String venueId);

	default void setUpdatedDateTime(T51VenueBean bean) {
		LocalDateTime now = LocalDateTime.now();
		bean.setUpdatedAt(now);
	}

	default void setCreatedDateTime(T51VenueBean bean) {
		if (bean.getCreatedAt() == null) { // 如果沒有創建時間才設置
			LocalDateTime now = LocalDateTime.now();
			bean.setCreatedAt(now);
		}
	}

	T51VenueBean update(T51VenueBean bean);

	T51VenueBean uploadTechnicalSpecifications(String venueId, MultipartFile file);

}
