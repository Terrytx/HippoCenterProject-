package tw.com.hippo_center_backend.h3controller.b5;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
import tw.com.hippo_center_backend.h2service.b5impl.C3VenueStatusFindService;
import tw.com.hippo_center_backend.h4dto.b5.VenueErrorResponse;
import tw.com.hippo_center_backend.h4dto.b5.VenueResponse;
import tw.com.hippo_center_backend.h4dto.b5.VenueSuccessResponse;

@RestController
@RequestMapping("/venue-status")
public class C3VenueStatusFindController {
	@Autowired
    private C3VenueStatusFindService venueStatusFindService;

    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<T53VenueStatusBean>> getMonthlyStatus(
            @PathVariable int year,
            @PathVariable int month) {
        List<T53VenueStatusBean> result = venueStatusFindService.getMonthlyAvailability(year, month);
        return ResponseEntity.ok(result);
    }
    // 新增加入場館ID的查詢方法
    @GetMapping("/{year}/{month}/venue/{venueId}")
    public ResponseEntity<List<T53VenueStatusBean>> getMonthlyStatusByVenue(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable String venueId) {
        List<T53VenueStatusBean> result = venueStatusFindService.getMonthlyAvailabilityByVenue(year, month, venueId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<?> findByVenueId(@PathVariable String venueId) {
        try {
            // 檢查必要參數
            if (venueId == null || venueId.trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(new VenueErrorResponse(false, "場地ID為必填欄位", null, 0L));
            }

            // 呼叫 Service 查詢資料
            List<T53VenueStatusBean> results = venueStatusFindService.findByVenueId(venueId);
            
            // 處理查詢結果
            if (results != null && !results.isEmpty()) {
                return ResponseEntity.ok(
                    new VenueSuccessResponse(true, "查詢成功", results, (long)results.size())
                );
            } else {
                return ResponseEntity.ok(
                    new VenueErrorResponse(false, "查無資料", null, 0L)
                );
            }
            
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new VenueErrorResponse(false, "系統錯誤：" + e.getMessage(), null, 0L));
        }
    }
    
}
