package tw.com.hippo_center_backend.h1repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.hippo_center_backend.h0bean.T25OrderListBean;
import tw.com.hippo_center_backend.h4dto.b2.SalesReportDTO;

public interface T25OrderListRepository extends JpaRepository<T25OrderListBean, Integer> {
    Optional<T25OrderListBean> findByMerchantTradeNo(String merchantTradeNo);

    List<T25OrderListBean> findByMember_MemberId(Integer memberId);

    // 🚀 計算總銷售額
    @Query("SELECT SUM(o.totalAmount) FROM T25OrderListBean o WHERE o.orderStatus = 'PAID'")
    double getTotalSales();

    // ✅ 改為 Native Query，確保 SQL Server 可以正確執行
    @Query(value = """
                SELECT CONVERT(VARCHAR, created_at, 23) AS date, SUM(total_amount) AS totalAmount
                FROM t25_order_list
                WHERE order_status = 'PAID'
                GROUP BY CONVERT(VARCHAR, created_at, 23)
                ORDER BY date DESC
            """, nativeQuery = true)
    List<Object[]> getSalesTrendNative();

    // 🚀 熱銷商品（計算被購買次數最多的產品）
    @Query("SELECT new tw.com.hippo_center_backend.h4dto.b2.SalesReportDTO(p.productName, COUNT(o)) " +
            "FROM T24OrderDetailsBean o JOIN o.product p GROUP BY p.productName ORDER BY COUNT(o) DESC")
    List<SalesReportDTO> getTopSellingProducts();

    // 🚀 購買最多的會員
    @Query("SELECT new tw.com.hippo_center_backend.h4dto.b2.SalesReportDTO(m.name, COUNT(o)) " +
            "FROM T25OrderListBean o JOIN o.member m WHERE o.orderStatus = 'PAID' " +
            "GROUP BY m.name ORDER BY COUNT(o) DESC")
    List<SalesReportDTO> getTopBuyingMembers();

}