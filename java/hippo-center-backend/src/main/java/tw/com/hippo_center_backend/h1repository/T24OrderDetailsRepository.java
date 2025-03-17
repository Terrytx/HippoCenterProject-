package tw.com.hippo_center_backend.h1repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.hippo_center_backend.h0bean.T24OrderDetailsBean;



public interface T24OrderDetailsRepository extends JpaRepository<T24OrderDetailsBean, Integer> {

    // 根據訂單 ID 查詢訂單明細
    List<T24OrderDetailsBean> findByOrder_OrderId(Integer orderId);
}
