package tw.com.hippo_center_backend.h1repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.hippo_center_backend.h0bean.T44MyLoveBean;

public interface T44MyLoveRepository extends JpaRepository<T44MyLoveBean, Integer> {
    // 根據會員 ID 和 myloveType 查詢最愛資料
    List<T44MyLoveBean> findByMember_MemberIdAndMyloveType(Integer memberId, Character myloveType);

    // 根據會員ID、最愛Type和最愛編號查詢最愛資料
    Optional<T44MyLoveBean> findByMember_MemberIdAndMyloveTypeAndMyloveId(Integer memberId, Character myloveType,
            Integer myloveId);
}
