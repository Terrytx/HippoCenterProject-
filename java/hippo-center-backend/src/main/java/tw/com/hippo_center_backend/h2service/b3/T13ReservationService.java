package tw.com.hippo_center_backend.h2service.b3;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T12ToursBean;
import tw.com.hippo_center_backend.h0bean.T13ReservationBean;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T12ToursRepository;
import tw.com.hippo_center_backend.h1repository.T13ReservationRepository;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;

@Service
@Transactional
public class T13ReservationService {

	@Autowired
    private T13ReservationRepository t13ReservationRepository;

    @Autowired
    private T12ToursRepository t12ToursRepository;

    @Autowired
    private T41MemberRepository t41MemberRepository;

    // 根據 JSON 資料新增預約
    public T13ReservationBean create(String json) {
        try {
            JSONObject obj = new JSONObject(json);

         // 使用 account 代替 memberId
            String account = obj.optString("account", null);
            Integer toursId = obj.optInt("toursId", -1);
            Integer numGuests = obj.optInt("numGuests", 0);

            if (account == null || toursId == -1 || numGuests <= 0) {
                throw new IllegalArgumentException("Invalid input: account, toursId, or numGuests is missing or invalid.");
            }

            // 查詢會員
            T41MemberBean member = t41MemberRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            // 查詢導覽行程
            T12ToursBean tour = t12ToursRepository.findById(toursId)
                    .orElseThrow(() -> new IllegalArgumentException("Tour not found"));

            // 檢查剩餘人數是否足夠
            if (tour.getCapacity() < numGuests) {
                throw new IllegalArgumentException("Not enough capacity for this reservation");
            }

            // 建立預約
            T13ReservationBean reservation = new T13ReservationBean();
            reservation.setMember(member);
            reservation.setTours(tour);
            reservation.setNumGuests(numGuests);
            reservation.setReservationDate(new Timestamp(System.currentTimeMillis())); // 預設為當前時間
            reservation.setReservationStatus("Pending");
            reservation.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // 保存預約
            T13ReservationBean savedReservation = t13ReservationRepository.save(reservation);

            // 更新剩餘人數
            tour.setCapacity(tour.getCapacity() - numGuests);
            t12ToursRepository.save(tour);

            return savedReservation;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error creating reservation: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    // 根據 JSON 資料更新預約
    public T13ReservationBean modify(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            Integer reservationId = obj.optInt("reservationId", -1);
            Integer numGuests = obj.optInt("numGuests", 0);

            if (reservationId == -1 || numGuests <= 0) {
                throw new IllegalArgumentException("Invalid input: missing or incorrect fields");
            }

            T13ReservationBean existingReservation = t13ReservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

            Integer originalNumGuests = existingReservation.getNumGuests();
            T12ToursBean tour = existingReservation.getTours();

            // 檢查剩餘人數是否足夠
            if ((tour.getCapacity() + originalNumGuests) < numGuests) {
                throw new IllegalArgumentException("Not enough capacity for this reservation update");
            }

            // 更新預約資料
            existingReservation.setNumGuests(numGuests);
            existingReservation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            T13ReservationBean updatedReservation = t13ReservationRepository.save(existingReservation);

            // 更新剩餘人數
            tour.setCapacity(tour.getCapacity() + originalNumGuests - numGuests);
            t12ToursRepository.save(tour);

            return updatedReservation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error modifying reservation: " + e.getMessage());
        }
    }

    // 刪除指定 ID 的預約，並恢復剩餘人數
    public boolean remove(Integer id) {
        if (id != null && t13ReservationRepository.existsById(id)) {
            T13ReservationBean reservation = t13ReservationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

            T12ToursBean tour = reservation.getTours();
            tour.setCapacity(tour.getCapacity() + reservation.getNumGuests());
            t12ToursRepository.save(tour);

            t13ReservationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 查詢所有預約
    public List<T13ReservationBean> findAllReservations() {
        return t13ReservationRepository.findAll();
    }

    // 查詢特定預約
    public Optional<T13ReservationBean> findReservationById(Integer id) {
        return t13ReservationRepository.findById(id);
    }
    
    // 根據導覽ID查詢預約者
    public List<T13ReservationBean> findReservationsByTourId(Integer toursId) {
        if (toursId == null) {
            throw new IllegalArgumentException("toursId 不能為空");
        }
        return t13ReservationRepository.findByTours_ToursId(toursId);
    }

    // 根據 memberId 查詢預約資料
    public List<T13ReservationBean> findByMemberAccount(String memberAccount) {
        return t13ReservationRepository.findByMember_Account(memberAccount);
    }

    // 根據 JSON 條件查詢預約
    public List<T13ReservationBean> find(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                throw new IllegalArgumentException("Query JSON cannot be null or empty.");
            }

            // 解析 JSON 條件
            JSONObject obj = new JSONObject(json);
            Integer toursId = obj.has("toursId") && !obj.isNull("toursId") ? obj.getInt("toursId") : null;

            // 根據條件篩選
            if (toursId != null) {
                return t13ReservationRepository.findByTours_ToursId(toursId);
            } else {
                throw new IllegalArgumentException("No valid query conditions provided.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing JSON input: " + e.getMessage());
        }
    }
    
    // 新增日期查詢
    public List<T13ReservationBean> findByDate(String startDateStr, String endDateStr) {
        try {
            if (startDateStr == null || endDateStr == null) {
                throw new IllegalArgumentException("必須提供 startDate 和 endDate");
            }

            System.out.println("查詢日期範圍：" + startDateStr + " ~ " + endDateStr);

            // 解析日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = new Date(sdf.parse(startDateStr).getTime());

            // endDate +1 天
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(sdf.parse(endDateStr));
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1); // 增加 1 天
            Date endDatePlusOne = new Date(calendar.getTimeInMillis());

            // 呼叫 Repository 查詢
            List<T13ReservationBean> reservations = t13ReservationRepository.findByDateRange(startDate, endDatePlusOne);

            // 觸發 Hibernate 預載關聯資訊（避免 Lazy Loading 問題）
            for (T13ReservationBean reservation : reservations) {
                if (reservation.getMember() != null) {
                    reservation.getMember().getName();
                    reservation.getMember().getAccount();
                    reservation.getMember().getPhone();
                }
                if (reservation.getTours() != null) {
                    reservation.getTours().getToursId();
                }
            }

            return reservations;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查詢錯誤: " + e.getMessage());
        }
    }


    // 計算所有預約數量，根據 JSON 條件進行查詢
    public long count(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            // 檢查 JSON 中是否包含 toursId
            Integer toursId = obj.has("toursId") && !obj.isNull("toursId") ? obj.getInt("toursId") : null;

            if (toursId != null) {
                // 如果有 toursId，計算該導覽的預約人數總和
                Long count = t13ReservationRepository.countReservationsByToursId(toursId);
                return count != null ? count : 0;
            } else {
                // 如果沒有條件，返回所有預約的總數量
                return t13ReservationRepository.count();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}