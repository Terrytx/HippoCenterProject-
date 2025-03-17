package tw.com.hippo_center_backend.h2service.b2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.hippo_center_backend.h1repository.T25OrderListRepository;
import tw.com.hippo_center_backend.h1repository.T27PromotionMemberRepository;
import tw.com.hippo_center_backend.h4dto.b2.PromotionUsageDTO;
import tw.com.hippo_center_backend.h4dto.b2.SalesReportDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private T25OrderListRepository orderRepository;

    @Autowired
    private T27PromotionMemberRepository promotionRepository;

    // 🚀 總銷售額
    public double getTotalSales() {
        return orderRepository.getTotalSales();
    }

    // 🚀 銷售趨勢（每日銷售）
    public List<SalesReportDTO> getSalesTrend() {
        List<Object[]> results = orderRepository.getSalesTrendNative();

        return results.stream()
                .map(row -> new SalesReportDTO(
                        (String) row[0], // 轉換日期字串
                        ((Number) row[1]).longValue() // 轉換數字
                ))
                .collect(Collectors.toList());
    }

    // 🚀 熱銷商品
    public List<SalesReportDTO> getTopSellingProducts() {
        return orderRepository.getTopSellingProducts();
    }

    // 🚀 購買最多的會員
    public List<SalesReportDTO> getTopBuyingMembers() {
        return orderRepository.getTopBuyingMembers();
    }

    public List<PromotionUsageDTO> getUsedPromotions() {
        return promotionRepository.getUsedPromotionsNative().stream()
                .map(row -> {
                    PromotionUsageDTO dto = new PromotionUsageDTO();
                    dto.setMonth((String) row[0]);  // 日期
                    dto.setUsedCount(((Number) row[1]).longValue()); // 已使用
                    dto.setActiveCount(0);
                    dto.setExpiredCount(0);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public List<PromotionUsageDTO> getActivePromotions() {
        return promotionRepository.getActivePromotionsNative().stream()
                .map(row -> {
                    PromotionUsageDTO dto = new PromotionUsageDTO();
                    dto.setMonth((String) row[0]);  
                    dto.setUsedCount(0);
                    dto.setActiveCount(((Number) row[2]).longValue());  
                    dto.setExpiredCount(0);  
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public List<PromotionUsageDTO> getExpiredPromotions() {
        return promotionRepository.getExpiredPromotionsNative().stream()
                .map(row -> {
                    PromotionUsageDTO dto = new PromotionUsageDTO();
                    dto.setMonth((String) row[0]);  
                    dto.setUsedCount(0); 
                    dto.setActiveCount(0);  
                    dto.setExpiredCount(((Number) row[3]).longValue());  
                    return dto;
                })
                .collect(Collectors.toList());
    }
    

}
