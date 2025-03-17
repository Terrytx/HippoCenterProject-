package tw.com.hippo_center_backend.h1repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.hippo_center_backend.h0bean.T13ReservationBean;

public interface T13ReservationRepository extends JpaRepository<T13ReservationBean, Integer>{
	
	// 根據 toursId 查詢預約資料
    List<T13ReservationBean> findByTours_ToursId(Integer toursId);
	
	// 根據memberID 查詢所有預約
    List<T13ReservationBean> findByMember_Account(String account);

    // 只根據日期範圍查詢預約
    @Query("SELECT r FROM T13ReservationBean r " +
    	       "JOIN FETCH r.member m " +
    	       "JOIN FETCH r.tours t " +
    	       "WHERE r.reservationDate >= :startDate " +
    	       "AND r.reservationDate < :endDatePlusOne")  // 改成 < endDate+1
    	List<T13ReservationBean> findByDateRange(
    	        @Param("startDate") java.sql.Date startDate,
    	        @Param("endDatePlusOne") java.sql.Date endDatePlusOne);

    
    // 計算特定導覽的預約人數
    @Query("SELECT SUM(r.numGuests) FROM T13ReservationBean r WHERE r.tours.toursId = :toursId")
    Long countReservationsByToursId(@Param("toursId") Integer toursId);

    // 查詢特定導覽的所有預約
    @Query("SELECT r FROM T13ReservationBean r WHERE r.tours.toursId = :toursId")
    List<T13ReservationBean> findReservationsByToursId(@Param("toursId") Integer toursId);
    
    // 執行刪除
    @Modifying
    @Query("DELETE FROM T13ReservationBean r WHERE r.tours.toursId = :toursId")
    void deleteByToursId(@Param("toursId") Integer toursId);
}
