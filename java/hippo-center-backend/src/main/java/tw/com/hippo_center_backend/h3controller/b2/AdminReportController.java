package tw.com.hippo_center_backend.h3controller.b2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.hippo_center_backend.h2service.b2.ReportService;
import tw.com.hippo_center_backend.h4dto.b2.PromotionUsageDTO;
import tw.com.hippo_center_backend.h4dto.b2.SalesReportDTO;

import java.util.List;

@RestController
@RequestMapping("/mowmow/admin/reports")
public class AdminReportController {

    @Autowired
    private ReportService reportService;

    // 🚀 取得銷售總額
    @GetMapping("/total-sales")
    public ResponseEntity<Double> getTotalSales() {
        return ResponseEntity.ok(reportService.getTotalSales());
    }

    // 🚀 取得銷售趨勢（每日銷售）
    @GetMapping("/sales-trend")
    public ResponseEntity<List<SalesReportDTO>> getSalesTrend() {
        return ResponseEntity.ok(reportService.getSalesTrend());
    }

    // 🚀 取得熱銷商品
    @GetMapping("/top-products")
    public ResponseEntity<List<SalesReportDTO>> getTopProducts() {
        return ResponseEntity.ok(reportService.getTopSellingProducts());
    }

    // 🚀 取得購買最多的會員
    @GetMapping("/top-members")
    public ResponseEntity<List<SalesReportDTO>> getTopMembers() {
        return ResponseEntity.ok(reportService.getTopBuyingMembers());
    }

    // 🚀 取得已使用促銷券 (USED)
    @GetMapping("/promotion-used")
    public ResponseEntity<List<PromotionUsageDTO>> getUsedPromotions() {
        return ResponseEntity.ok(reportService.getUsedPromotions());
    }

    // 🚀 取得未使用促銷券 (NEW + ACTIVE)
    @GetMapping("/promotion-active")
    public ResponseEntity<List<PromotionUsageDTO>> getActivePromotions() {
        return ResponseEntity.ok(reportService.getActivePromotions());
    }

    // 🚀 取得已過期促銷券 (EXPIRED)
    @GetMapping("/promotion-expired")
    public ResponseEntity<List<PromotionUsageDTO>> getExpiredPromotions() {
        return ResponseEntity.ok(reportService.getExpiredPromotions());
    }

}
