package tw.com.hippo_center_backend.h1repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;

public interface T51VenueRepository extends JpaRepository<T51VenueBean, String>, JpaSpecificationExecutor<T51VenueBean>{

    Optional<T51VenueBean> findByVenueId(String venueId);

	List<T51VenueBean> findByRentalStatueTrue();

//	public abstract Long count(JSONObject param);
//	public abstract List<T51VenueBean> find(JSONObject param);
}
