package tw.com.hippo_center_backend.h3controller.b3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import tw.com.hippo_center_backend.h2service.b3.EmailService;
import tw.com.hippo_center_backend.h2service.b3.T13ReservationService;

@RestController
@RequestMapping("/user/reservations")
public class T13ReservationUserController {

    @Autowired
    private T13ReservationService t13ReservationService;
    
    @Autowired
    private EmailService emailService; // 注入 Email 服務

    // 新增預約 (使用 JSON 資料)
    @PostMapping("/create")
    public ResponseEntity<T13ReservationBean> createReservation(@RequestBody String json) {
        try {
            T13ReservationBean createdReservation = t13ReservationService.create(json);
            
         // 確保獲取會員 Email（其實是 account）
            String userEmail = createdReservation.getAccount(); // 使用 getAccount()

            if (userEmail != null && !userEmail.isEmpty()) {
                emailService.sendReservationConfirmation(
                    userEmail,
                    createdReservation.getTours().getToursName(),
                    createdReservation.getTours().getToursDate().toString(),
                    createdReservation.getTours().getTimeSlot(),
                    createdReservation.getNumGuests()
                );
            }
            
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

    // 查詢指定會員的預約資料
    @PostMapping("/findmember")
    public ResponseEntity<List<T13ReservationBean>> findReservationsByMember(@RequestBody String json) {
        try {
            List<T13ReservationBean> reservations = t13ReservationService.find(json);
            return reservations.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
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

    // 計算所有或符合條件的預約數量 (使用 JSON 條件)
    @PostMapping("/count")
    public ResponseEntity<Long> countReservations(@RequestBody String json) {
        try {
            long count = t13ReservationService.count(json);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
