package tw.com.hippo_center_backend.h1repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.hippo_center_backend.h0bean.T32EventCategoryBean;

public interface T32EventCategoryRepository extends JpaRepository<T32EventCategoryBean, Integer> {  
     // 根據類別描述查詢
     List<T32EventCategoryBean> findByeventCategoryDescription(String eventCategoryDescription);
     // 根據類別類型查詢
     List<T32EventCategoryBean> findByeventCategoryType(Integer eventCategoryType);
     // 根據描述的部分內容進行模糊查詢
     List<T32EventCategoryBean> findByeventCategoryDescriptionContaining(String keyword);    
     // 檢查活動類別是否存在
     boolean existsById(Integer id);
     // 根據類別名稱查詢
    // List<T32EventCategoryBean> findByeventCategoryName(String eventCategoryName);
     Optional<T32EventCategoryBean> findByeventCategoryName(String eventCategoryName);
}