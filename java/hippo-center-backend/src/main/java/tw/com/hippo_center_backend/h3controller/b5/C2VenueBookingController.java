package tw.com.hippo_center_backend.h3controller.b5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T52BookingBean;
import tw.com.hippo_center_backend.h2service.b5impl.C2VenueBookingServiceImpl;
import tw.com.hippo_center_backend.h4dto.b5.BookingResponse;

@RestController
@RequestMapping("/api/booking")
public class C2VenueBookingController {
    
    @Autowired
    private C2VenueBookingServiceImpl bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse<T52BookingBean>> insertBooking(@RequestBody T52BookingBean bookingBean) {
        // 驗證場地選擇
        if (bookingBean.getVenue() == null || bookingBean.getVenue().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(BookingResponse.error("請選擇場地"));
        }

        // 驗證租借日期
        if (bookingBean.getRentalStartDatetime() == null || bookingBean.getRentalEndDatetime() == null) {
            return ResponseEntity.badRequest()
                .body(BookingResponse.error("請填寫租借起迄日期"));
        }

        // 驗證日期順序
        if (bookingBean.getRentalStartDatetime().isAfter(bookingBean.getRentalEndDatetime())) {
            return ResponseEntity.badRequest()
                .body(BookingResponse.error("租借結束日期不能早於開始日期"));
        }

        // 驗證聯絡人
        if (!StringUtils.hasText(bookingBean.getContact())) {
            return ResponseEntity.badRequest()
                .body(BookingResponse.error("請填寫聯絡人"));
        }

        // 驗證電子郵件
        if (!StringUtils.hasText(bookingBean.getContactEmail())) {
            return ResponseEntity.badRequest()
                .body(BookingResponse.error("請填寫電子郵件"));
        }

        // 通過驗證後，執行新增
        // bookingId 會透過 @PrePersist 自動生成
        T52BookingBean result = bookingService.insert(bookingBean);
        if (result != null) {
            // 確保回傳的結果包含生成的 bookingId
            return ResponseEntity.ok(
                BookingResponse.success("預約申請成功", result)
            );
        }

        return ResponseEntity.badRequest()
            .body(BookingResponse.error("預約申請失敗"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse<T52BookingBean>> getBookingById(@PathVariable String id) {
        if (!StringUtils.hasText(id)) {
            return ResponseEntity.badRequest()
                .body(BookingResponse.error("預約編號不能為空"));
        }

        T52BookingBean result = bookingService.findById(id);
        if (result != null) {
            return ResponseEntity.ok(
                BookingResponse.success("查詢成功", result)
            );
        }
        
        // 找不到資料時返回 404
        return ResponseEntity.notFound()
            .build();
    }

    // 新增：根據 bookingId 查詢
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<BookingResponse<T52BookingBean>> getBookingByBookingId(@PathVariable String bookingId) {
        if (!StringUtils.hasText(bookingId)) {
            return ResponseEntity.badRequest()
                .body(BookingResponse.error("預約編號不能為空"));
        }

        T52BookingBean result = bookingService.findByBookingId(bookingId);
        if (result != null) {
            return ResponseEntity.ok(
                BookingResponse.success("查詢成功", result)
            );
        }
        
        return ResponseEntity.notFound()
            .build();
    }
}