package tw.com.hippo_center_backend.h2service.b3;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T12ToursBean;
import tw.com.hippo_center_backend.h1repository.T12ToursRepository;
import tw.com.hippo_center_backend.h1repository.T13ReservationRepository;

@Service
@Transactional
public class T12ToursService {

    @Autowired
    private T12ToursRepository t12ToursRepository;
    
    @Autowired
    private T13ReservationRepository t13ReservationRepository;

    // 新增導覽
    public T12ToursBean insertTour(T12ToursBean bean) {
        if (bean == null) {
            throw new IllegalArgumentException("導覽不能為空");
        }

        if (bean.getToursId() != null) {
            throw new IllegalArgumentException("新建的導覽不應該有Id");
        }

        // 自動設置 created_at 時間戳
        bean.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return t12ToursRepository.save(bean);
    }

    // 更新導覽
    public T12ToursBean updateTour(T12ToursBean bean) {
        if (bean == null || bean.getToursId() == null) {
            throw new IllegalArgumentException("導覽不能為空，且必須有Id");
        }

        if (!t12ToursRepository.existsById(bean.getToursId())) {
            throw new IllegalArgumentException("Tour with ID " + bean.getToursId() + " not found");
        }
        
     // 自動設置 created_at 時間戳
        bean.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return t12ToursRepository.save(bean);
    }

    // 刪除導覽
    public boolean deleteTour(Integer toursId) {
        if (toursId == null) {
            throw new IllegalArgumentException("導覽ID不能為空");
        }

        Optional<T12ToursBean> tourOptional = t12ToursRepository.findById(toursId);
        if (!tourOptional.isPresent()) {
            throw new IllegalArgumentException("Tour with ID " + toursId + " not found");
        }

        // 檢查是否有關聯的預約
        Long reservationCount = t13ReservationRepository.countReservationsByToursId(toursId);
        if (reservationCount != null && reservationCount > 0) {
            throw new IllegalArgumentException("無法刪除：該導覽已有預約記錄");
        }

        // 刪除導覽
        t12ToursRepository.deleteById(toursId);
        return true;
    }

    // 取得所有導覽
    public List<T12ToursBean> getAllTours() {
        return t12ToursRepository.findAll();
    }

    // 依照行程ID取得導覽
    public T12ToursBean getTourById(Integer toursId) {
        if (toursId == null) {
            throw new IllegalArgumentException("導覽ID不能為空");
        }

        return t12ToursRepository
                .findById(toursId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Tour with ID " + toursId + " not found"));
    }
}
