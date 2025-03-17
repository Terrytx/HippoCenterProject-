package tw.com.hippo_center_backend.h3controller.b3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T12ToursBean;
import tw.com.hippo_center_backend.h2service.b3.T12ToursService;

@RestController
@RequestMapping("/admin/tours")
public class T12ToursAdminController {

    @Autowired
    private T12ToursService t12ToursService;

    // 新增導覽
    @PostMapping
    public ResponseEntity<T12ToursBean> addTour(@RequestBody T12ToursBean toursBean) {
    	if (toursBean.getToursName() == null || toursBean.getTimeSlot() == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        
    	try {
            T12ToursBean createdTour = t12ToursService.insertTour(toursBean);
            return new ResponseEntity<>(createdTour, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 更新導覽
    @PutMapping("/{toursId}")
    public ResponseEntity<T12ToursBean> updateTour(@PathVariable Integer toursId, @RequestBody T12ToursBean toursBean) {
        try {
            toursBean.setToursId(toursId);
            T12ToursBean updatedTour = t12ToursService.updateTour(toursBean);
            return ResponseEntity.ok(updatedTour);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 刪除導覽
    @DeleteMapping("/{toursId}")
    public ResponseEntity<?> deleteTour(@PathVariable Integer toursId) {
        try {
            t12ToursService.deleteTour(toursId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("無法刪除：數據完整性約束違反，請檢查相關數據");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("發生未知錯誤");
        }
    }

    // 取得所有導覽
    @GetMapping
    public ResponseEntity<List<T12ToursBean>> getAllTours() {
        List<T12ToursBean> toursList = t12ToursService.getAllTours();

        // ✅ 避免 `null` 值影響前端，確保 `createdAt` 和 `updatedAt` 有值
        toursList.forEach(tour -> {
            if (tour.getCreatedAt() == null) {
                tour.setCreatedAt(null); // 讓前端接收 `null` 而不是 `N/A`
            }
            if (tour.getUpdatedAt() == null) {
                tour.setUpdatedAt(null); // 讓前端接收 `null`
            }
        });

        return ResponseEntity.ok(toursList);
    }


    // 依行程ID取得導覽
    @GetMapping("/{toursId}")
    public ResponseEntity<T12ToursBean> getTourById(@PathVariable Integer toursId) {
        try {
            T12ToursBean tour = t12ToursService.getTourById(toursId);
            return ResponseEntity.ok(tour);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
