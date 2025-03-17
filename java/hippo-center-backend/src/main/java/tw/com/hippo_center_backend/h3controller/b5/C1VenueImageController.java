package tw.com.hippo_center_backend.h3controller.b5;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h2service.b5.VenueImageService;
import tw.com.hippo_center_backend.h4dto.b5.VenueImageOrderDTO;
// @CrossOrigin(origins = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/venue/image")
public class C1VenueImageController {
	 @Autowired
	 private VenueImageService venueImageService;

	 @GetMapping("/{venueId}")
	    public ResponseEntity<?> getTop3VenueImages(@PathVariable String venueId) {
	        System.out.println("開始查詢場地ID: " + venueId + " 的前三張圖片");
	        
	        try {
	            List<VenueImageOrderDTO> images = venueImageService.findTop3ImagesByVenue(venueId);
	            System.out.println("查詢到 " + images.size() + " 張圖片");
	            
	            if (!images.isEmpty()) {
	            	images.forEach(img -> {
	                    System.out.println("圖片ID: " + img.getImageId());
	                    System.out.println("圖片數據大小: " + (img.getImageData() != null ? img.getImageData().length : 0) + " bytes");
	                    System.out.println("排序順序: " + img.getSortOrder());
	                });
	                System.out.println("成功取得圖片，返回資料");
	                return ResponseEntity.ok(images);
	            } else {
	                System.out.println("未找到任何圖片");
	                return ResponseEntity.noContent().build();
	            }
	            
	        } catch (Exception e) {
	            System.err.println("查詢圖片時發生錯誤: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("取得圖片失敗: " + e.getMessage());
	        }
	    }

 
 
// 
// @PutMapping("/reorder")
// public ResponseEntity<?> reorderImages(@RequestBody List<VenueImageOrderDTO> imageOrders) {
//     try {
//         List<T14VenueImagesBean> updatedImages = venueImageService.updateImagesOrder(imageOrders);
//         return ResponseEntity.ok(updatedImages);
//     } catch (Exception e) {
//         return ResponseEntity.badRequest().body(e.getMessage());
//     }
// }
// 
// 
 
 
}