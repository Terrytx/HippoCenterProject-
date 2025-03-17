package tw.com.hippo_center_backend.h3controller.b3;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T13ReservationBean;
import tw.com.hippo_center_backend.h2service.b3.T13ReservationService;

@RestController
@RequestMapping("/admin/reservations")
public class T13ReservationAdminController {

    @Autowired
    private T13ReservationService t13ReservationService;

    // 新增預約 (使用 JSON 資料)
    @PostMapping("/create")
    public ResponseEntity<T13ReservationBean> createReservation(@RequestBody String json) {
        try {
            T13ReservationBean createdReservation = t13ReservationService.create(json);
            return ResponseEntity.ok(createdReservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // 返回詳細錯誤訊息
        }
    }

    // 更新預約 (使用 JSON 資料)
    @PutMapping("/modify")
    public ResponseEntity<T13ReservationBean> modifyReservation(@RequestBody String json) {
        try {
            T13ReservationBean modifiedReservation = t13ReservationService.modify(json);
            return ResponseEntity.ok(modifiedReservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 刪除指定 ID 的預約
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        boolean isRemoved = t13ReservationService.remove(id);
        if (isRemoved) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 查詢所有預約
    @GetMapping("/findAll")
    public ResponseEntity<List<T13ReservationBean>> findAllReservations() {
        List<T13ReservationBean> allReservations = t13ReservationService.findAllReservations();
        return ResponseEntity.ok(allReservations);
    }

    // 查詢單一預約 (reservationId)
    @GetMapping("/{id}")
    public ResponseEntity<T13ReservationBean> findReservationById(@PathVariable Integer id) {
        Optional<T13ReservationBean> reservation = t13ReservationService.findReservationById(id);
        return reservation.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    // 根據導覽ID查詢者
    @GetMapping("/byTour/{toursId}")
    public ResponseEntity<List<Map<String, Object>>> findReservationsByTourId(@PathVariable Integer toursId) {
        try {
            List<T13ReservationBean> reservations = t13ReservationService.findReservationsByTourId(toursId);

            if (reservations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // 轉換成 JSON 格式
            List<Map<String, Object>> responseList = reservations.stream().map(reservation -> {
                Map<String, Object> data = new HashMap<>();
                data.put("toursId", reservation.getTours().getToursId());
                data.put("toursDate", reservation.getTours().getToursDate());
                data.put("toursName", reservation.getTours().getToursName());
                data.put("membername", reservation.getMember().getName());
                data.put("memberAccount", reservation.getMember().getAccount());
                data.put("peopleCount", reservation.getNumGuests());
                return data;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // 查詢指定會員的預約資料
    @PostMapping("/findmember")
    public ResponseEntity<List<Map<String, Object>>> findReservationsByMember(@RequestBody Map<String, String> request) {
        try {
            String memberAccount = request.get("memberAccount");

            if (memberAccount == null || memberAccount.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<T13ReservationBean> reservations = t13ReservationService.findByMemberAccount(memberAccount);

            if (reservations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<Map<String, Object>> responseList = reservations.stream().map(reservation -> {
                Map<String, Object> data = new HashMap<>();
                data.put("reservationId", reservation.getReservationId());
                data.put("reservationDate", reservation.getReservationDate());
                data.put("peopleCount", reservation.getNumGuests());
                
                // 會員資訊
                if (reservation.getMember() != null) {
                    data.put("memberAccount", reservation.getMember().getAccount());
                    data.put("membername", reservation.getMember().getName());
                }

                // **確保回傳導覽資訊**
                if (reservation.getTours() != null) {
                    data.put("toursId", reservation.getTours().getToursId());
                    data.put("toursName", reservation.getTours().getToursName());
                    data.put("toursDate", reservation.getTours().getToursDate());
                    data.put("toursSession", reservation.getTours().getToursSession());
                    data.put("timeSlot", reservation.getTours().getTimeSlot());
                } else {
                    data.put("toursId", "無資料");
                    data.put("toursName", "無資料");
                    data.put("toursDate", "無資料");
                    data.put("toursSession", "未知場次");
                    data.put("timeSlot", "未知時間");
                }

                return data;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



    // 查詢其他特定條件的預約 (使用 JSON 條件)
    @PostMapping("/find")
    public ResponseEntity<List<T13ReservationBean>> findReservations(@RequestBody String json) {
        try {
            List<T13ReservationBean> filteredReservations = t13ReservationService.find(json);
            return filteredReservations.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(filteredReservations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @PostMapping("/findByDate")
    public ResponseEntity<List<Map<String, Object>>> findReservationsByDate(@RequestBody Map<String, String> requestData) {
        try {
            String startDateStr = requestData.get("startDate");
            String endDateStr = requestData.get("endDate");

            if (startDateStr == null || endDateStr == null) {
                throw new IllegalArgumentException("必須提供 startDate 和 endDate");
            }

            System.out.println("查詢日期範圍：" + startDateStr + " ~ " + endDateStr);

            List<T13ReservationBean> reservations = t13ReservationService.findByDate(startDateStr, endDateStr);
            System.out.println("查詢結果: " + reservations.size() + " 筆資料");

            // 轉換成 JSON 格式
            List<Map<String, Object>> responseList = reservations.stream().map(reservation -> {
                Map<String, Object> data = new HashMap<>();
                data.put("reservationId", reservation.getReservationId());
                data.put("reservationDate", reservation.getReservationDate().toString());
                data.put("membername", reservation.getMember() != null ? reservation.getMember().getName() : "無資料");
                data.put("memberAccount", reservation.getMember() != null ? reservation.getMember().getAccount() : "無資料");
                data.put("phone", reservation.getMember() != null ? reservation.getMember().getPhone() : "無資料");
                data.put("toursId", reservation.getTours() != null ? reservation.getTours().getToursId() : "無資料");
                data.put("peopleCount", reservation.getNumGuests());
                return data;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(responseList); // **改為回傳空陣列，而非 204**
        } catch (Exception e) {
            System.out.println("查詢錯誤：" + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
//    // 計算所有或符合條件的預約數量 (使用 JSON 條件)
//    @PostMapping("/count")
//    public ResponseEntity<Long> countReservations(@RequestBody String json) {
//        try {
//            long count = t13ReservationService.count(json);
//            return ResponseEntity.ok(count);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
    
}
