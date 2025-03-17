package tw.com.hippo_center_backend.h3controller.b5;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h2service.b5impl.C1VenueFindServiceImpl;
import tw.com.hippo_center_backend.h2service.b5impl.A1VenueServiceImpl;
import tw.com.hippo_center_backend.h4dto.b5.VenueResponse;
// @CrossOrigin(origins = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/venue")
public class C1VenueFindController {
    @Autowired
    private A1VenueServiceImpl venueService;
    @Autowired
    private C1VenueFindServiceImpl venueFindService;
    @GetMapping("/findall")  // 查詢全部
    public ResponseEntity<VenueResponse> getAllVenues(){
    	VenueResponse response = new VenueResponse();
        
        try {
            // 直接呼叫 repository 的 findAll() 方法
            List<T51VenueBean> venues = venueService.findAll(null);
            
            if (venues != null && !venues.isEmpty()) {
                response.setSuccess(true);
                response.setMessage("查詢成功");
                response.setList(venues);
                response.setCount((long) venues.size());
                return ResponseEntity.ok(response);
            } else {
                response.setSuccess(false);
                response.setMessage("查無資料");
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            System.out.println("查詢場地時發生錯誤: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("查詢場地時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{venueId}")  // 查詢透過場地編號
    public ResponseEntity<VenueResponse> getVenueById(@PathVariable String venueId) {
        VenueResponse response = new VenueResponse();
        
        try {
            if (venueId == null || venueId.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("場地編碼不能為空");
                return ResponseEntity.badRequest().body(response);
            }
            
            T51VenueBean venueBean = venueService.findById(venueId);
            
            if (venueBean != null) {
                response.setSuccess(true);
                response.setMessage("查詢成功");
                response.setList(Collections.singletonList(venueBean)); 
                response.setCount(1L);
                return ResponseEntity.ok(response);
            } else {
                response.setSuccess(false);
                response.setMessage("查無此場地");
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            System.out.println("查詢場地時發生錯誤: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("查詢場地時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/available") // 查詢所有可租借場地
    public ResponseEntity<VenueResponse> getAvailableVenues() {
        VenueResponse response = new VenueResponse();

        try {
            // 查詢所有可租借的場地
            List<T51VenueBean> availableVenues = venueService.findAllAvailableVenues();

            if (availableVenues != null && !availableVenues.isEmpty()) {
                response.setSuccess(true);
                response.setMessage("查詢成功");
                response.setList(availableVenues);
                response.setCount((long) availableVenues.size());
                return ResponseEntity.ok(response);
            } else {
                response.setSuccess(false);
                response.setMessage("目前無可租借場地");
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            System.out.println("查詢可租借場地時發生錯誤: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("查詢可租借場地時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // 檢查特定場地是否可租借
    @GetMapping("/available/{venueId}")
    public ResponseEntity<VenueResponse> checkVenueAvailability(@PathVariable String venueId) {
        VenueResponse response = new VenueResponse();

        try {
            if (venueId == null || venueId.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("場地編碼不能為空");
                return ResponseEntity.badRequest().body(response);
            }

            boolean isAvailable = venueService.isVenueAvailable(venueId);

            if (isAvailable) {
                response.setSuccess(true);
                response.setMessage("此場地可租借");
            } else {
                response.setSuccess(false);
                response.setMessage("此場地目前不可租借");
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("檢查場地可租借狀態時發生錯誤: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("檢查場地可租借狀態時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    
    }
    @GetMapping("/download-specifications/{venueId}")
    public ResponseEntity<byte[]> downloadTechnicalSpecifications(@PathVariable String venueId) {
        System.out.println("開始下載技術規格文件，場地ID: " + venueId);
        
        try {
            System.out.println("正在調用 venueService 下載文件...");
            byte[] fileContent = venueFindService.downloadTechnicalSpecifications(venueId);
            
            if (fileContent != null) {
                System.out.println("成功獲取文件內容，文件大小: " + fileContent.length + " bytes");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDisposition(ContentDisposition.builder("attachment")
                        .filename("technical_specifications.pdf").build());
                
                System.out.println("準備返回文件內容，設置 headers 完成");
                return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
            }
            
            System.out.println("未找到指定的文件內容，venueId: " + venueId);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            System.out.println("下載文件時發生錯誤: " + e.getMessage());
            System.out.println("錯誤詳細資訊: " + e);
            e.printStackTrace();  // 這行會打印完整的堆疊跟蹤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
