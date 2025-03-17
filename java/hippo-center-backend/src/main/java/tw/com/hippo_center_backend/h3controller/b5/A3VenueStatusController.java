package tw.com.hippo_center_backend.h3controller.b5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
import tw.com.hippo_center_backend.h2service.b5impl.A3VenueStatusServiceImpl;
import tw.com.hippo_center_backend.h2service.b5impl.A3VenueStatusServiceImpl.DateConflictException;
import tw.com.hippo_center_backend.h4dto.b5.VenueErrorResponse;
import tw.com.hippo_center_backend.h4dto.b5.VenueStatusRangeRequest;
import tw.com.hippo_center_backend.h4dto.b5.VenueSuccessResponse;


@RestController
@RequestMapping("/venue-status")
public class A3VenueStatusController {
	 @Autowired
	 private A3VenueStatusServiceImpl venueStatusService;
	 
	 

	    @PostMapping("/create")
	    public ResponseEntity<?> createVenueStatus(@RequestBody T53VenueStatusBean venueStatusBean) {
	        try {
	            // 檢查必要欄位
	            if (venueStatusBean.getVenue() == null || 
	                venueStatusBean.getVenue().getVenueId() == null || 
	                venueStatusBean.getDate() == null) {
	                return ResponseEntity
	                    .badRequest()
	                    .body("場地ID和日期為必填欄位");
	            }

	            // 呼叫 Service 新增資料
	            T53VenueStatusBean result = venueStatusService.createVenueStatus(venueStatusBean);
	            
	            // 處理結果
	            if (result != null) {
	                return ResponseEntity
	                    .ok()
	                    .body(result);
	            } else {
	                return ResponseEntity
	                    .badRequest()
	                    .body("新增場地狀態失敗");
	            }
	            
	        } catch (Exception e) {
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("系統錯誤：" + e.getMessage());
	        }
	    }
	    @PutMapping("/update")
	    public ResponseEntity<?> updateVenueStatus(@RequestBody T53VenueStatusBean venueStatusBean) {
	        try {
	            // 檢查ID是否存在
	            if (venueStatusBean.getVenueStatusId() == null) {
	                return ResponseEntity
	                    .badRequest()
	                    .body("場地狀態ID為必填欄位");
	            }

	            // 呼叫 Service 更新資料
	            T53VenueStatusBean result = venueStatusService.updateVenueStatus(venueStatusBean);
	            
	            // 處理結果
	            if (result != null) {
	                return ResponseEntity
	                    .ok()
	                    .body(result);
	            } else {
	                return ResponseEntity
	                    .notFound()
	                    .build();
	            }
	            
	        } catch (Exception e) {
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("系統錯誤：" + e.getMessage());
	        }
	    }
	    @DeleteMapping("/delete/{venueStatusId}")
	    public ResponseEntity<?> deleteVenueStatus(@PathVariable Integer venueStatusId) {
	        try {
	            // 建立要刪除的 bean
	            T53VenueStatusBean bean = new T53VenueStatusBean();
	            bean.setVenueStatusId(venueStatusId);
	            
	            // 呼叫 service 執行刪除
	            boolean isDeleted = venueStatusService.delete(bean);
	            
	            if (isDeleted) {
	                return ResponseEntity.ok("成功刪除場地狀態記錄");
	            } else {
	                return ResponseEntity
	                    .status(HttpStatus.NOT_FOUND)
	                    .body("找不到指定的場地狀態記錄");
	            }
	            
	        } catch (Exception e) {
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("刪除失敗：" + e.getMessage());
	        }
	    }   
	    
		    
	    @PostMapping("/create-range")    // 新增一個區段的
	    public ResponseEntity<?> createVenueStatusRange(
	            @RequestBody VenueStatusRangeRequest request) {
	        try {
	            // 檢查必要欄位
	            if (request.getStartDate() == null || request.getEndDate() == null) {
	                return ResponseEntity
	                    .badRequest()
	                    .body(new VenueErrorResponse("起迄日期為必填欄位"));
	            }

	            if (request.getStartDate().isAfter(request.getEndDate())) {
	                return ResponseEntity
	                    .badRequest()
	                    .body(new VenueErrorResponse("起始日期不能晚於結束日期"));
	            }

	            // 呼叫 Service 建立資料
	            List<T53VenueStatusBean> results = venueStatusService.createVenueStatusForDateRange(
	                request.getVenueStatus(),
	                request.getStartDate(),
	                request.getEndDate()
	            );

	            return ResponseEntity.ok(new VenueSuccessResponse("成功建立場地狀態", results));
	            
	        } catch (DateConflictException e) {
	            return ResponseEntity
	                .status(HttpStatus.CONFLICT)
	                .body(e.getMessage());
	        } catch (Exception e) {
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new VenueErrorResponse("系統錯誤：" + e.getMessage()));
	        }
	    }
	    @PostMapping("/import")
	    public ResponseEntity<?> importVenueStatus(@RequestParam("file") MultipartFile file) {
	        try {
	            if (file.isEmpty()) {
	                return ResponseEntity.badRequest().body("請選擇要上傳的檔案");
	            }

	            // 檢查檔案副檔名
	            String fileName = file.getOriginalFilename();
	            if (fileName != null && !fileName.toLowerCase().endsWith(".csv")) {
	                return ResponseEntity.badRequest().body("只接受 CSV 檔案");
	            }

	            venueStatusService.importFromCsv(file);
	            return ResponseEntity.ok("檔案匯入成功");

	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        } catch (DateConflictException e) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("日期衝突：" + e.getMessage());
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("檔案匯入失敗：" + e.getMessage());
	        }
	    }

}
	    
	    
	    
	

	
