package tw.com.hippo_center_backend.h1repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.hippo_center_backend.h0bean.T52BookingBean;

public interface T52BookingRepository extends JpaRepository<T52BookingBean, String> {

    List<T52BookingBean> findByOrderStatus(String orderStatus);

    Optional<T52BookingBean> findByBookingId(String bookingId);

    Optional<T52BookingBean> findById(String bookingId);

    @Query(value = "SELECT * FROM t52_booking WHERE " +
            "booking_id LIKE :search OR " +
            "venue LIKE :search OR " +
            "organizer LIKE :search OR " +
            "contact LIKE :search", nativeQuery = true)
    List<T52BookingBean> findBySmartSearch(@Param("search") String search);
}
