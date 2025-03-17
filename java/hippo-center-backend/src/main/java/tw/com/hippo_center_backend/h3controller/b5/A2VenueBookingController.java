package tw.com.hippo_center_backend.h3controller.b5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import tw.com.hippo_center_backend.h0bean.T52BookingBean;
import tw.com.hippo_center_backend.h2service.b5impl.A2VenueBookingFindServiceImpl;

@RestController
@RequestMapping("api/booking/ad")
public class A2VenueBookingController {

	@Autowired
	private A2VenueBookingFindServiceImpl bookingService;

    @PutMapping("/{id}")
    public ResponseEntity<T52BookingBean> updateBooking(
            @PathVariable String id,
            @RequestBody T52BookingBean bookingBean) {
    	if (bookingBean.getVenue() != null) {
            String venues = preprocessVenues(bookingBean.getVenue());
            bookingBean.setVenue(venues);
        }
    	
        bookingBean.setBookingId(id);
        T52BookingBean result = bookingService.update(bookingBean);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
    private String preprocessVenues(String venues) {
        if (venues == null || venues.trim().isEmpty()) {
            return "";
        }
        
        // 1. 分割字串，考慮多種可能的分隔符
        String[] venueArray = venues.split("[,;|\\s、]+");
        
        // 2. 清理每個場地名稱
        List<String> cleanedVenues = Arrays.stream(venueArray)
            .map(String::trim)                    // 移除前後空格
            .filter(v -> !v.isEmpty())            // 移除空字串
            .map(v -> v.replaceAll("[\\p{P}&&[^-]]", ""))  // 移除標點符號，保留連字符
            .distinct()                           // 移除重複項
            .collect(Collectors.toList());
        
        // 3. 使用統一的分隔符重新組合
        return String.join(",", cleanedVenues);
    }
    @PutMapping("/{bookingId}/cancel")  // 點選取消訂單，狀態 改成訂單已取消
    public ResponseEntity<T52BookingBean> cancelBooking(@PathVariable String bookingId) {
        try {
            T52BookingBean cancelledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(cancelledBooking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @PutMapping("/{bookingId}/confirm")  // 點選檔期確認，狀態改成 檔期已確認
    public ResponseEntity<T52BookingBean> confirmBooking(@PathVariable String bookingId) {
        try {
            T52BookingBean confirmedBooking = bookingService.confirmBooking(bookingId);
            return ResponseEntity.ok(confirmedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PutMapping("/{bookingId}/under-review") // 點選檔期確認，狀態改成 檔期已確認
    public ResponseEntity<T52BookingBean> changeToUnderReview(@PathVariable String bookingId) {
        try {
            T52BookingBean confirmedBooking = bookingService.changeToUnderReview(bookingId);
            return ResponseEntity.ok(confirmedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
    @PostMapping
    public ResponseEntity<List<T52BookingBean>> getAllBookings(@RequestBody(required = false) T52BookingBean bean) {
        List<T52BookingBean> results = bookingService.findAll(bean);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<T52BookingBean>> findByStatus(@PathVariable String status) {
        List<T52BookingBean> results = bookingService.findByStatus(status);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<T52BookingBean>> searchBookings(
            @RequestParam(required = false) String search) {
        try {
            // 處理空字串或 null 的情況
            if (search == null || search.trim().isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            List<T52BookingBean> results = bookingService.smartSearch(search);
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            // 記錄錯誤日誌
            System.err.println("搜尋出錯: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
}
    


