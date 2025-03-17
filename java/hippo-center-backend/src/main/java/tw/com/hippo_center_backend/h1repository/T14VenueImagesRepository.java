package tw.com.hippo_center_backend.h1repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.hippo_center_backend.h0bean.T14VenueImagesBean;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;

public interface T14VenueImagesRepository extends JpaRepository<T14VenueImagesBean, Integer>{
    List<T14VenueImagesBean> findByVenueOrderBySortOrderAsc(T51VenueBean venue);
    List<T14VenueImagesBean> findTop3ByVenueOrderBySortOrderAsc(T51VenueBean venue);


}
