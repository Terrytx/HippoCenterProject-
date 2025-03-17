package tw.com.hippo_center_backend.h3controller.b5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h2service.b5impl.A1VenueServiceImpl;
import tw.com.hippo_center_backend.h4dto.b5.VenueResponse;

@RestController
@RequestMapping("/api/venue/delete")
public class A1VenueDeleteController {
	@Autowired
	private A1VenueServiceImpl venueService;

	@DeleteMapping("/{venueId}")
	public ResponseEntity<VenueResponse> deleteVenue(@PathVariable String venueId) {
		VenueResponse response = new VenueResponse();
    
    try {
        // 驗證場地ID
        if (venueId == null || venueId.trim().isEmpty()) {
            response.setSuccess(false);
            response.setMessage("請選擇場地");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 創建 bean 並設置 ID
        T51VenueBean venueBean = new T51VenueBean();
        venueBean.setVenueId(venueId);
        
        // 調用 service 層的刪除方法
        boolean isDeleted = venueService.delete(venueBean);
        
        if (isDeleted) {
            response.setSuccess(true);
            response.setMessage("場地刪除成功");
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setMessage("場地刪除失敗，可能不存在此場地");
            return ResponseEntity.badRequest().body(response);
        }
        
    } catch (Exception e) {
        System.out.println("刪除場地時發生錯誤: " + e.getMessage());
        response.setSuccess(false);  
        response.setMessage("刪除場地時發生錯誤: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}}