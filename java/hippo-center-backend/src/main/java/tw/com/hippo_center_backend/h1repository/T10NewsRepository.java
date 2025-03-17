package tw.com.hippo_center_backend.h1repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.hippo_center_backend.h0bean.T10NewsBean;
import tw.com.hippo_center_backend.h0bean.T11NewsCategoryBean;

public interface T10NewsRepository extends JpaRepository<T10NewsBean, Integer>{

	List<T10NewsBean> findByCategory(T11NewsCategoryBean Category);

	// ✅ 新增方法，只返回已達 `publishDate` 的 "上架" 消息
    @Query("SELECT n FROM T10NewsBean n WHERE n.publishDate <= :today AND n.newsStatus = '上架'")
    List<T10NewsBean> findPublishedNews(@Param("today") Date today);

    // ✅ 管理員：查詢所有新聞（不過濾 `publishDate` 和 `newsStatus`）
    @Query("SELECT n FROM T10NewsBean n")
    List<T10NewsBean> findAllNewsForAdmin();
}
