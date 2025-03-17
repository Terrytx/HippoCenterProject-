package tw.com.hippo_center_backend.h2service.b5;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tw.com.hippo_center_backend.h0bean.T14VenueImagesBean;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h4dto.b5.VenueImageOrderDTO;

public interface VenueImageService {
	 // 設置創建和更新時間的 default 方法
    default void setUpdatedDateTime(T14VenueImagesBean bean) {
        LocalDateTime now = LocalDateTime.now();
        bean.setUpdatedAt(now);
    }

    default void setCreatedDateTime(T14VenueImagesBean bean) {
        if (bean.getCreatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            bean.setCreatedAt(now);
        }
    }

    // 新增圖片
    T14VenueImagesBean insert(T14VenueImagesBean bean);
    
    // 上傳圖片
    T14VenueImagesBean uploadImage(String venueId, MultipartFile file, String altText, Integer sortOrder);
	List<T14VenueImagesBean> updateImagesOrder(List<VenueImageOrderDTO> imageOrders);
    List<VenueImageOrderDTO> findTop3ImagesByVenue(String venueId);

	T51VenueBean findById(String id);

}
