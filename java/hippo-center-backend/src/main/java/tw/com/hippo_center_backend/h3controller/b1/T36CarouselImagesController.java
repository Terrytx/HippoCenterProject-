//package tw.com.hippo_center_backend.h3controller.b1;
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tw.com.hippo_center_backend.h0bean.T36CarouselImagesBean;
//import tw.com.hippo_center_backend.h2service.b1.T36CarouselImagesService;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/carousel")
//@CrossOrigin(origins = "*") // 允許跨域請求，可根據需求調整
//public class T36CarouselImagesController {
//
//    @Autowired
//    private T36CarouselImagesService carouselImagesService;
//
//    // 取得所有輪播圖
//    @GetMapping("/all")
//    public ResponseEntity<List<T36CarouselImagesBean>> getAllCarouselImages() {
//        List<T36CarouselImagesBean> carousels = carouselImagesService.getAllCarouselImages();
//        return ResponseEntity.ok(carousels);
//    }
//
//    // 根據 ID 取得輪播圖
//    @GetMapping("/{id}")
//    public ResponseEntity<T36CarouselImagesBean> getCarouselImageById(@PathVariable Integer id) {
//        Optional<T36CarouselImagesBean> carousel = carouselImagesService.getCarouselImageById(id);
//        return carousel.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // 新增或更新輪播圖
//    @PostMapping("/save")
//    public ResponseEntity<T36CarouselImagesBean> saveCarouselImage(@RequestBody T36CarouselImagesBean carouselImage) {
//        T36CarouselImagesBean savedCarousel = carouselImagesService.saveCarouselImage(carouselImage);
//        return ResponseEntity.ok(savedCarousel);
//    }
//
//    // 根據 ID 刪除輪播圖
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCarouselImageById(@PathVariable Integer id) {
//        carouselImagesService.deleteCarouselImageById(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 取得所有啟用的輪播圖（依 sortOrder 排序）
//    @GetMapping("/active")
//    public ResponseEntity<List<T36CarouselImagesBean>> getActiveCarouselImages() {
//        List<T36CarouselImagesBean> activeCarousels = carouselImagesService.getActiveCarouselImages();
//        return ResponseEntity.ok(activeCarousels);
//    }
//
//    // 取得當前時間內有效的輪播圖
//    @GetMapping("/valid")
//    public ResponseEntity<List<T36CarouselImagesBean>> getValidCarouselImages() {
//        List<T36CarouselImagesBean> validCarousels = carouselImagesService.getValidCarouselImages();
//        return ResponseEntity.ok(validCarousels);
//    }
//
//// // ✅ **合併後的排序更新 API**
////    @PutMapping("/update-sort-order")
////    public ResponseEntity<String> updateSortOrder(@RequestBody List<T36CarouselImagesBean> images) {
////        if (images == null || images.isEmpty()) {
////            return ResponseEntity.badRequest().body("圖片排序列表不能為空");
////        }
////
////        try {
////            carouselImagesService.updateSortOrder(images);
////            return ResponseEntity.ok("排序更新成功");
////        } catch (IllegalArgumentException e) {
////            return ResponseEntity.badRequest().body("錯誤: " + e.getMessage());
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器內部錯誤");
////        }
////    }
//
//}
