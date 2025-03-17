package tw.com.hippo_center_backend.h1repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.hippo_center_backend.h0bean.T33TicketBean;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface T33TicketRepository extends JpaRepository<T33TicketBean, Integer> {

	// 查找所有票券
    List<T33TicketBean> findAll();
    
	// 依照會員 ID 查找該會員購買的票券
    List<T33TicketBean> findByMemberId_MemberId(Integer memberId);

    // 依照活動 ID 查找所有票券
    List<T33TicketBean> findByEventId_EventId(Integer eventId);

    // 依照購買日期查找票券
    List<T33TicketBean> findByPurchaseDate(LocalDate purchaseDate);

    // 依照活動 ID 與會員 ID 查找票券
    List<T33TicketBean> findByEventId_EventIdAndMemberId_MemberId(Integer eventId, Integer memberId);

   //  @Query("SELECT t FROM T33TicketBean t WHERE t.eventId.eventName = :eventName")
   //  List<T33TicketBean> findByEventName(@Param("eventName") String eventName);

    // 根據 eventId 內的 eventName 來查詢票券
    List<T33TicketBean> findByEventId_EventName(String eventName);
    
    @Query("SELECT t FROM T33TicketBean t JOIN FETCH t.memberId JOIN FETCH t.eventId WHERE t.ticketId = :ticketId")
    T33TicketBean findTicketWithMemberAndEvent(@Param("ticketId") Integer ticketId);
   
  

}
