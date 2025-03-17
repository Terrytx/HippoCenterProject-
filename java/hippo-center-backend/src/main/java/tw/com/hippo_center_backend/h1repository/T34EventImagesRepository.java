package tw.com.hippo_center_backend.h1repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.hippo_center_backend.h0bean.T34EventImagesBean;

import java.util.List;
import java.util.Optional;

@Repository
public interface T34EventImagesRepository extends JpaRepository<T34EventImagesBean, Integer> {

    // 根據活動 ID 查詢所有相關圖片
    List<T34EventImagesBean> findByEvent_EventId(Integer eventId);

    // 找到某活動的封面圖片
    T34EventImagesBean findByEvent_EventIdAndIsCoverTrue(Integer eventId);

    // 根據活動 ID 依排序順序找所有圖片
    List<T34EventImagesBean> findByEvent_EventIdOrderBySortOrderAsc(Integer eventId);

    // 使用 @Query 自訂 JPQL 查詢封面圖片
    @Query("SELECT i FROM T34EventImagesBean i WHERE i.event.eventId = :eventId AND i.isCover = true")
    Optional<T34EventImagesBean> findCoverImageByEventId(@Param("eventId") Integer eventId);
}

