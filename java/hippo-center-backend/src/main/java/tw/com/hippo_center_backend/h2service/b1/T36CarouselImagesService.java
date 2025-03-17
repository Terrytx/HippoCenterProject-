//package tw.com.hippo_center_backend.h2service.b1;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import tw.com.hippo_center_backend.h0bean.T36CarouselImagesBean;
//import tw.com.hippo_center_backend.h1repository.T36CarouselImagesRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//
//@Service
//public class T36CarouselImagesService {
//
//    private final T36CarouselImagesRepository carouselImagesRepository;
//
//    // **使用構造函數注入，Spring 自動注入 Bean**
//    public T36CarouselImagesService(T36CarouselImagesRepository carouselImagesRepository) {
//        this.carouselImagesRepository = carouselImagesRepository;
//    }
//
//    // 取得所有輪播圖
//    public List<T36CarouselImagesBean> getAllCarouselImages() {
//        return carouselImagesRepository.findAll();
//    }
//
//    // 根據 ID 取得輪播圖
//    public Optional<T36CarouselImagesBean> getCarouselImageById(Integer id) {
//        return carouselImagesRepository.findById(id);
//    }
//
//    // 根據 ID 刪除輪播圖
//    public void deleteCarouselImageById(Integer id) {
//        carouselImagesRepository.deleteById(id);
//    }
//
//    // 取得所有啟用的輪播圖（依 sortOrder 排序）
//    public List<T36CarouselImagesBean> getActiveCarouselImages() {
//        return carouselImagesRepository.findByIsActiveTrueOrderBySortOrderAsc();
//    }
//
//    // 取得當前時間內有效的輪播圖
//    public List<T36CarouselImagesBean> getValidCarouselImages() {
//        LocalDateTime now = LocalDateTime.now();
//        return carouselImagesRepository.findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(now, now);
//    } 
//    //更新活新增輪播圖
//    public T36CarouselImagesBean saveCarouselImage(T36CarouselImagesBean carouselImage) {
//        if (carouselImage.getCarouselImage() == null || carouselImage.getCarouselImage().length == 0) {
//            throw new IllegalArgumentException("圖片不能為空");
//        }
//        return carouselImagesRepository.save(carouselImage);
//    }
//    @Transactional
//    public void saveAll(List<T36CarouselImagesBean> images) {
//        carouselImagesRepository.saveAll(images);
//    }
//
//  
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
