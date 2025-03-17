package tw.com.hippo_center_backend.h1repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.hippo_center_backend.h0bean.T26PromotionBean;
import tw.com.hippo_center_backend.h0bean.T27PromotionMemberBean;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;


public interface T27PromotionMemberRepository extends JpaRepository<T27PromotionMemberBean, String> {
       // 查詢會員擁有的促銷券
       List<T27PromotionMemberBean> findByMember(T41MemberBean member);

       // 檢查會員是否已經收到某個促銷活動的促銷券
       boolean existsByMemberAndPromotion(T41MemberBean member, T26PromotionBean promotion);

       // 查詢所有符合指定狀態的促銷券
       List<T27PromotionMemberBean> findByPromotionStatus(T27PromotionMemberBean.PromotionStatus status);

       // 查詢會員擁有的指定促銷活動的促銷券
       List<T27PromotionMemberBean> findByMemberAndPromotion(T41MemberBean member, T26PromotionBean promotion);

       // 查詢會員擁有的指定狀態的促銷券
       List<T27PromotionMemberBean> findByMemberAndPromotionStatus(T41MemberBean member,
                     T27PromotionMemberBean.PromotionStatus status);

       @Query(value = """
                         SELECT FORMAT(pm.used_at, 'yyyy-MM') AS month,
                                COUNT(pm.promotion_member_id) AS usedCount,
                                0 AS activeCount,
                                0 AS expiredCount
                         FROM t27_promotion_member pm
                         WHERE pm.promotion_status = 'USED'
                         GROUP BY FORMAT(pm.used_at, 'yyyy-MM')
                         ORDER BY month ASC
                     """, nativeQuery = true)
       List<Object[]> getUsedPromotionsNative();

       @Query(value = """
                         SELECT FORMAT(pm.added_at, 'yyyy-MM') AS month,
                                0 AS usedCount,
                                COUNT(pm.promotion_member_id) AS activeCount,
                                0 AS expiredCount
                         FROM t27_promotion_member pm
                         WHERE pm.promotion_status IN ('NEW', 'ACTIVE')
                         GROUP BY FORMAT(pm.added_at, 'yyyy-MM')
                         ORDER BY month ASC
                     """, nativeQuery = true)
       List<Object[]> getActivePromotionsNative();

       @Query(value = """
                         SELECT FORMAT(pm.end_date, 'yyyy-MM') AS month,
                                0 AS usedCount,
                                0 AS activeCount,
                                COUNT(pm.promotion_member_id) AS expiredCount
                         FROM t27_promotion_member pm
                         WHERE pm.promotion_status = 'EXPIRED'
                         GROUP BY FORMAT(pm.end_date, 'yyyy-MM')
                         ORDER BY month ASC
                     """, nativeQuery = true)
       List<Object[]> getExpiredPromotionsNative();

}
