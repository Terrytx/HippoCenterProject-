//package tw.com.hippo_center_backend.h1repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import tw.com.hippo_center_backend.h0bean.T36CarouselImagesBean;
//
//@Repository
//public interface T36CarouselImagesRepository extends JpaRepository<T36CarouselImagesBean, Integer> {
//    
//    /**
//     * 查找所有啟用的輪播圖，依照 sortOrder 升序排列
//     */
//    List<T36CarouselImagesBean> findByIsActiveTrueOrderBySortOrderAsc();
//
//    /**
//     * 查找在指定時間範圍內有效的輪播圖
//     * 條件：
//     * - startDate 必須早於現在
//     * - endDate 必須晚於現在
//     * - isActive = true（啟用狀態）
//     */
//    List<T36CarouselImagesBean> findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(
//            LocalDateTime now1, LocalDateTime now2);
//
//    /**
//     * 根據 sortOrder 值查找輪播圖
//     */
//    List<T36CarouselImagesBean> findBySortOrder(Integer sortOrder);
//    
//    @Query("SELECT c FROM T36CarouselImagesBean c WHERE c.isActive = true ORDER BY c.sortOrder ASC")
//    List<T36CarouselImagesBean> findActiveCarouselImages();	
//}
