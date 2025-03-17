package tw.com.hippo_center_backend.h1repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.hippo_center_backend.h0bean.T26PromotionBean;

public interface T26PromotionRepository extends JpaRepository<T26PromotionBean, Integer> {

    // 查詢促銷碼
    Optional<T26PromotionBean> findByPromotionCode(String promotionCode);
}
