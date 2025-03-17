package tw.com.hippo_center_backend.h3controller.b5;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h2service.b5impl.A1VenueServiceImpl;
import tw.com.hippo_center_backend.h4dto.b5.VenueResponse;

@RestController
@RequestMapping("/api/venue/create")
@CrossOrigin
public class A1VenueCreateController {
   @Autowired
    private A1VenueServiceImpl venueService;
   // 場地新增邏輯-- ID必填、場地名稱必填、是否出租必填
   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<VenueResponse> createVenue(
		   @RequestParam(required = false) String venueId,
           @RequestParam(required = false) String venueName,
           @RequestParam(required = false) String venueDescription,
           @RequestParam(required = false) Integer xCoordinate,
           @RequestParam(required = false) Integer yCoordinate,
           @RequestParam(required = false) Integer floor,
           @RequestParam(required = false) Boolean rentalStatue,
           @RequestParam(required = false) Integer capacity,
           @RequestParam(required = false) Integer areaPings,
           @RequestParam(required = false) Double venueHeight,
           @RequestParam(required = false) String venueInfo,
           @RequestParam(required = false) String usageRecommendations,
           @RequestParam(required = false) MultipartFile rentalRegulation,
           @RequestParam(required = false) MultipartFile technicalSpecifications,
           @RequestParam(required = false) Integer venueFeeByDay) {
        	   
	   VenueResponse response = new VenueResponse();
	   try {
		   T51VenueBean venueBean = new T51VenueBean();
	
	   // 驗證場地ID--必填欄位
	   System.out.println("0");
       if (venueId == null || venueId.trim().isEmpty()) {
           response.setSuccess(false);
           response.setMessage("請輸入場地ID編碼");
           return ResponseEntity.badRequest().body(response);
       }
       System.out.println("1");
       // 基本驗證--必填欄位
       if (venueName == null || venueName.trim().isEmpty()) {
           response.setSuccess(false);
           response.setMessage("請輸入場地名稱");
           return ResponseEntity.badRequest().body(response);
       }
       // 驗證是否出租狀態--必填欄位
       if (rentalStatue == null) {
           response.setSuccess(false);
           response.setMessage("請選擇場地是否可出租");
           return ResponseEntity.badRequest().body(response);
       }

       System.out.println("2");
       // 驗證坐標值（如果有值的話）
       if ((xCoordinate!= null && xCoordinate < 0 )|| 
           (yCoordinate != null && yCoordinate < 0)) {
           response.setSuccess(false);
           response.setMessage("座標值不能為負數");
           return ResponseEntity.badRequest().body(response);
       }
       System.out.println("3");
       // 驗證容納人數（如果有值）
       if (capacity!= null && capacity <= 0) {
           response.setSuccess(false);
           response.setMessage("容納人數必須大於0或不輸入");
           return ResponseEntity.badRequest().body(response);
       }
       System.out.println("4");
       // 驗證場地坪數（如果有值）
       if (areaPings != null && areaPings <= 0) {
           response.setSuccess(false);
           response.setMessage("場地坪數必須大於0或不輸入");
           return ResponseEntity.badRequest().body(response);
       }
       System.out.println("5");
       // 驗證場地高度須高於2.8（如果有值）
       if (venueHeight != null && venueHeight <=2.8) {
           response.setSuccess(false);
           response.setMessage("場地高度必須大於2.8或不輸入");
           return ResponseEntity.badRequest().body(response);
       }
       

       
       System.out.println("6");
       // 處理檔案上傳
       System.out.println("檔案上傳1開始");
       if (rentalRegulation != null && !rentalRegulation.isEmpty()) {
           venueBean.setRentalRegulation(rentalRegulation.getBytes());
       }
       System.out.println("檔案上傳2");
       
       if (technicalSpecifications != null && !technicalSpecifications.isEmpty()) {
           venueBean.setTechnicalSpecifications(technicalSpecifications.getBytes());
       }
       System.out.println("檔案上傳3結束");

       
       // [新增] 設置 Bean 的值
       venueBean.setVenueId(venueId); // 1 館場編號
       venueBean.setVenueName(venueName);  // 2 館場名稱
       venueBean.setVenueDescription(venueDescription); // 3 館場描述
       venueBean.setxCoordinate(xCoordinate); // 6 x軸(像素座標)
       venueBean.setyCoordinate(yCoordinate); // 7 y軸(像素座標)
       venueBean.setFloor(floor);  // 8 圖層
       venueBean.setRentalStatue(rentalStatue); // 9 是否提供租借(狀態)
       venueBean.setCapacity(capacity); // 10 容納人數
       venueBean.setAreaPings(areaPings);  // 11 場地坪數
       venueBean.setVenueHeight(venueHeight); // 12 場地高度
       venueBean.setVenueInfo(venueInfo);  // 13 場地租用簡介!! 前面已有描述
       venueBean.setUsageRecommendations(usageRecommendations);  // 14 使用建議
       venueBean.setVenueFeeByDay(venueFeeByDay);  // 17 每日場地費
       
       
       // 調用 service 層的插入方法
       System.out.println("新增開始");
       T51VenueBean savedVenue = venueService.insert(venueBean);
       if (savedVenue != null) {
           response.setSuccess(true);
           response.setMessage("場地創建成功");
           System.out.println("新增結束");
           response.setList(Collections.singletonList(savedVenue));
           response.setCount(1L);
           System.out.println("7");
           return ResponseEntity.ok(response);
       } else {
           response.setSuccess(false);
           response.setMessage("場地已存在");
           System.out.println("8");
           return ResponseEntity.badRequest().body(response);
       }
    
   } catch (Exception e) {
       System.out.println("創建場地時發生錯誤: " + e.getMessage());
       response.setSuccess(false);
       response.setMessage("創建場地時發生錯誤: " + e.getMessage());
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
   }
 }
}
   



