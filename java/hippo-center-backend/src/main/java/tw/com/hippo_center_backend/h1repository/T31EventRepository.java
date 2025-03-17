package tw.com.hippo_center_backend.h1repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.hippo_center_backend.h0bean.T31EventBean;

@Repository
public interface T31EventRepository extends JpaRepository<T31EventBean, Integer> {
    // 查詢特定名稱的活動
    List<T31EventBean> findByEventName(String eventName);
    
    //找到所有活動並依照日期排序 新到舊
  	List<T31EventBean> findAllByOrderByEventStartDateDesc();
    
  	@Query("SELECT e FROM T31EventBean e ORDER BY e.eventStartDate DESC")
  	List<T31EventBean> findAllByEventStartDateDesc();
  	
//    @Query("SELECT e FROM T31EventBean e LEFT JOIN FETCH e.venue WHERE e.eventId = :eventId")
//    Optional<T31EventBean> findByIdWithVenue(@Param("eventId") Integer eventId);
    
    @Query("SELECT e FROM T31EventBean e WHERE e.eventStartDate <= :today ORDER BY e.eventStartDate DESC")
    List<T31EventBean> findPastEvents(@Param("today") LocalDate today);

    // 包含匹配 (containing)，內部已自動處理匹配問題無須處理大小寫
    List<T31EventBean> findByEventNameContainingIgnoreCase(String eventName);
    
    // 根據活動類別查詢活動
    List<T31EventBean> findByEventCategoryEventCategoryId(Integer eventCategoryId);
    
    // 查詢特定日期範圍內的活動
    List<T31EventBean> findByEventStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    // 自訂查詢：搜尋活動名稱包含關鍵字的活動 模糊查詢
    @Query("SELECT e FROM T31EventBean e WHERE e.eventName LIKE %:keyword%")
    List<T31EventBean> searchByEventNameKeyword(@Param("keyword") String keyword);
    
    // 根據場地名稱查詢活動
    List<T31EventBean> findByVenueVenueName(String venueName);

    // 可選：使用模糊查詢根據場地名稱查詢活動
    List<T31EventBean> findByVenueVenueNameContainingIgnoreCase(String venueName);

    // 可選：使用 JPQL 自定義查詢
    @Query("SELECT e FROM T31EventBean e WHERE e.venue.venueName = :venueName")
    List<T31EventBean> searchByVenueName(@Param("venueName") String venueName);

    // 可選：使用 JPQL 自定義模糊查詢
    @Query("SELECT e FROM T31EventBean e WHERE LOWER(e.venue.venueName) LIKE LOWER(CONCAT('%', :venueName, '%'))")
    List<T31EventBean> searchByVenueNameKeyword(@Param("venueName") String venueName);
   
    //根據活動類別名稱 查詢活動，而非活動類別 ID
    @Query("SELECT e FROM T31EventBean e WHERE e.eventCategory.eventCategoryName = :eventCategoryName")
    List<T31EventBean> findByEventCategoryName(@Param("eventCategoryName") String eventCategoryName);

    
    @Query("SELECT e FROM T31EventBean e WHERE e.venue.venueId = :venueId")
    List<T31EventBean> findByVenueId(@Param("venueId") String venueId);

    // 根據類別類型查詢
    List<T31EventBean> findByEventCategoryEventCategoryType(Integer eventCategoryType);
    
    // 根據類別類型查詢
    List<T31EventBean> findByEventCategory_EventCategoryType(Integer eventCategoryType);
    
    // 驗證活動名稱使否已存在
    boolean existsByEventName(String eventName);
    
    // 查詢已發布的活動
    List<T31EventBean> findByIsPublishedTrue();
    
    // 查詢未發布的活動
    List<T31EventBean> findByIsPublishedFalse();
    
    // 根據發布狀態查詢活動
    List<T31EventBean> findByIsPublished(Boolean isPublished);
    
    
    // 更新活動發布狀態和發布日期
    @Modifying
    @Query("UPDATE T31EventBean e SET e.isPublished = :isPublished, " +
           "e.eventPublishDate = CASE WHEN :isPublished = true THEN CURRENT_DATE ELSE " +
           "CASE WHEN :clearPublishDate = true THEN null ELSE e.eventPublishDate END END " +
           "WHERE e.eventId = :eventId")
    void updatePublishStatus(@Param("eventId") Integer eventId, 
                           @Param("isPublished") Boolean isPublished,
                           @Param("clearPublishDate") Boolean clearPublishDate);
    
}