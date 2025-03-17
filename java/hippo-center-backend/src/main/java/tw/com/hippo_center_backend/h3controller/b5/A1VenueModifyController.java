package tw.com.hippo_center_backend.h3controller.b5;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h2service.b5impl.A1VenueServiceImpl;
import tw.com.hippo_center_backend.h4dto.b5.VenueResponse;
import tw.com.hippo_center_backend.h5util.b5.ModifyStatus;

@RestController
@RequestMapping("/api/venue/modify")
public class A1VenueModifyController {
    @Autowired
    private A1VenueServiceImpl venueService;

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VenueResponse> modifyVenue(
            @RequestParam(required = false) String venueId,
            @RequestParam(required = false) String venueName,
            @RequestParam(required = false) String capacity,
            @RequestParam(required = false) String areaPings,
            @RequestParam(required = false) String venueHeight,
            @RequestParam(required = false) Boolean rentalStatue,
            @RequestParam(required = false) String venueInfo,
            @RequestParam(required = false) String usageRecommendations,
            @RequestParam(required = false) MultipartFile rentalRegulation,
            @RequestParam(required = false) MultipartFile technicalSpecifications,
            @RequestParam(required = false) String venueFeeByDay) {

        VenueResponse response = new VenueResponse();
        try {
            // 驗證場地ID--必填欄位
            if (venueId == null || venueId.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("請選擇修改場地編碼");
                return ResponseEntity.badRequest().body(response);
            }

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

            // 根據 venueId 查詢原始資料
            T51VenueBean existingVenue = venueService.findById(venueId);
            if (existingVenue == null) {
                response.setSuccess(false);
                response.setMessage("查無此場地");
                return ResponseEntity.badRequest().body(response);
            }

            ModifyStatus modifyStatus = new ModifyStatus();

            // 檢查名稱變更
            if (venueName != null) {
                boolean isModified = !venueName.equals(existingVenue.getVenueName());
                modifyStatus.addStatus(isModified);
            }

            // 檢查並轉換數值型別欄位
            Integer capacityValue = null;
            if (capacity != null && !capacity.trim().isEmpty() && !capacity.equals("null")) {
                try {
                    capacityValue = Integer.valueOf(capacity.trim());
                    boolean isModified = !capacityValue.equals(existingVenue.getCapacity());
                    modifyStatus.addStatus(isModified);
                } catch (NumberFormatException e) {
                    response.setSuccess(false);
                    response.setMessage("容納人數必須為數字");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            Integer areaPingsValue = null;
            if (areaPings != null && !areaPings.trim().isEmpty() && !areaPings.equals("null")) {
                try {
                    areaPingsValue = Integer.valueOf(areaPings.trim());
                    boolean isModified = !areaPingsValue.equals(existingVenue.getAreaPings());
                    modifyStatus.addStatus(isModified);
                } catch (NumberFormatException e) {
                    response.setSuccess(false);
                    response.setMessage("場地坪數必須為數字");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            Double venueHeightValue = null;
            if (venueHeight != null && !venueHeight.trim().isEmpty() && !venueHeight.equals("null")) {
                try {
                    venueHeightValue = Double.valueOf(venueHeight.trim());
                    boolean isModified = !venueHeightValue.equals(existingVenue.getVenueHeight());
                    modifyStatus.addStatus(isModified);
                } catch (NumberFormatException e) {
                    response.setSuccess(false);
                    response.setMessage("場地高度必須為數字");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            Integer venueFeeByDayValue = null;
            if (venueFeeByDay != null && !venueFeeByDay.trim().isEmpty() && !venueFeeByDay.equals("null")) {
                try {
                    venueFeeByDayValue = Integer.valueOf(venueFeeByDay.trim());
                    boolean isModified = !venueFeeByDayValue.equals(existingVenue.getVenueFeeByDay());
                    modifyStatus.addStatus(isModified);
                } catch (NumberFormatException e) {
                    response.setSuccess(false);
                    response.setMessage("場地租金必須為數字");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // 檢查租借狀態變更
            if (rentalStatue != null) {
                boolean isModified = !rentalStatue.equals(existingVenue.getRentalStatue());
                modifyStatus.addStatus(isModified);
            }

            // 檢查文字欄位變更
            if (venueInfo != null) {
                boolean isModified = !venueInfo.equals(existingVenue.getVenueInfo());
                modifyStatus.addStatus(isModified);
            }

            if (usageRecommendations != null) {
                boolean isModified = !usageRecommendations.equals(existingVenue.getUsageRecommendations());
                modifyStatus.addStatus(isModified);
            }

            // 檢查檔案變更
            if (rentalRegulation != null && !rentalRegulation.isEmpty()) {
                modifyStatus.addStatus(true);
            }

            if (technicalSpecifications != null && !technicalSpecifications.isEmpty()) {
                modifyStatus.addStatus(true);
            }

            // 檢查是否有任何修改
            if (modifyStatus.getStatus() == 0) {
                response.setSuccess(false);
                response.setMessage("無異動，請至少修改一個欄位的值");
                return ResponseEntity.badRequest().body(response);
            }

            // 建立新的 Bean 並設置值
            T51VenueBean venueBean = new T51VenueBean();
            venueBean.setVenueId(venueId);
            venueBean.setVenueName(venueName);
            venueBean.setRentalStatue(rentalStatue);
            venueBean.setCapacity(capacityValue);
            venueBean.setAreaPings(areaPingsValue);
            venueBean.setVenueHeight(venueHeightValue);
            venueBean.setVenueInfo(venueInfo);
            venueBean.setUsageRecommendations(usageRecommendations);
            venueBean.setVenueFeeByDay(venueFeeByDayValue);

            // 處理檔案
            if (rentalRegulation != null && !rentalRegulation.isEmpty()) {
                venueBean.setRentalRegulation(rentalRegulation.getBytes());
            } else {
                venueBean.setRentalRegulation(existingVenue.getRentalRegulation());
            }

            if (technicalSpecifications != null && !technicalSpecifications.isEmpty()) {
                venueBean.setTechnicalSpecifications(technicalSpecifications.getBytes());
            } else {
                venueBean.setTechnicalSpecifications(existingVenue.getTechnicalSpecifications());
            }

            // 調用 service 層的修改方法
            T51VenueBean updatedVenue = venueService.update(venueBean);

            if (updatedVenue != null) {
                response.setSuccess(true);
                response.setMessage("場地修改成功");
                response.setList(Collections.singletonList(updatedVenue));
                response.setCount(1L);
                return ResponseEntity.ok(response);
            } else {
                response.setSuccess(false);
                response.setMessage("場地修改失敗");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("修改場地時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/upload-file")
    public ResponseEntity<VenueResponse> uploadFile(
            @RequestParam String venueId,
            @RequestParam String fileType, // "rentalRegulation" 或 "technicalSpecifications"
            @RequestParam MultipartFile file) {

        VenueResponse response = new VenueResponse();
        try {
            // 檢查檔案是否為 PDF
            if (!file.getContentType().equals("application/pdf")) {
                response.setSuccess(false);
                response.setMessage("只接受 PDF 檔案格式");
                return ResponseEntity.badRequest().body(response);
            }

            // 查詢場地
            T51VenueBean venue = venueService.findById(venueId);
            if (venue == null) {
                response.setSuccess(false);
                response.setMessage("查無此場地");
                return ResponseEntity.badRequest().body(response);
            }

            // 更新檔案
            if ("rentalRegulation".equals(fileType)) {
                venue.setRentalRegulation(file.getBytes());
            } else if ("technicalSpecifications".equals(fileType)) {
                venue.setTechnicalSpecifications(file.getBytes());
            }

            // 儲存更新
            T51VenueBean updatedVenue = venueService.update(venue);

            response.setSuccess(true);
            response.setMessage("檔案上傳成功");
            response.setList(Collections.singletonList(updatedVenue));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("檔案上傳失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}