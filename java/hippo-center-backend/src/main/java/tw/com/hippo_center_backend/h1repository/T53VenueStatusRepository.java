package tw.com.hippo_center_backend.h1repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
@Repository
public interface T53VenueStatusRepository extends JpaRepository<T53VenueStatusBean,Integer> {

    Optional<T53VenueStatusBean> findByVenue_VenueIdAndDate(String venueId, LocalDate date);

    @Query("SELECT vs FROM T53VenueStatusBean vs WHERE vs.venue.venueId = :venueId")
	List<T53VenueStatusBean> findByVenue_VenueId(@Param("venueId") String venueId);
    @Query("SELECT v FROM T53VenueStatusBean v WHERE YEAR(v.date) = :year AND MONTH(v.date) = :month")
    List<T53VenueStatusBean> findByYearAndMonth(
        @Param("year") int year, 
        @Param("month") int month
    );
    // 新增加入場館ID的查詢方法
    @Query("SELECT v FROM T53VenueStatusBean v WHERE YEAR(v.date) = :year AND MONTH(v.date) = :month AND v.venue.venueId = :venueId")
    List<T53VenueStatusBean> findByYearAndMonthAndVenue(
        @Param("year") int year, 
        @Param("month") int month,
        @Param("venueId") String venueId
    );

}

