package tw.com.hippo_center_backend.h1repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.hippo_center_backend.h0bean.T28ProductImageBean;

public interface T28ProductImageRepository extends JpaRepository<T28ProductImageBean, Integer> {

    // ✅ 查找圖片
    Optional<T28ProductImageBean> findByImageUrl(String imageUrl);

    // ✅ 刪除圖片
    void deleteByImageUrl(String imageUrl);
}
