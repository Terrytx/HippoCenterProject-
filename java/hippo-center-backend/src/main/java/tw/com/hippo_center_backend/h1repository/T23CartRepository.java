package tw.com.hippo_center_backend.h1repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.hippo_center_backend.h0bean.T23CartBean;



public interface T23CartRepository extends JpaRepository<T23CartBean, Integer> {

    // 查詢會員的購物車內容
    List<T23CartBean> findByMember_MemberId(Integer memberId);

    // 查詢會員的特定商品
    Optional<T23CartBean> findByMember_MemberIdAndProduct_ProductId(Integer memberId, Integer productId);

    // 刪除會員購物車內容
    void deleteByMember_MemberId(Integer memberId);

}
