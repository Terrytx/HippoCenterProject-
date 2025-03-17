package tw.com.hippo_center_backend.h1repository;

	import org.springframework.data.jpa.repository.JpaRepository;
	import tw.com.hippo_center_backend.h0bean.T35TicketOrderBean;
	import tw.com.hippo_center_backend.h0bean.T41MemberBean;
	import tw.com.hippo_center_backend.h0bean.T31EventBean;

	import java.time.LocalDate;
	import java.util.List;
import java.util.Optional;

	public interface T35TicketOrderRepository extends JpaRepository<T35TicketOrderBean, Long> {
	    
	    // 查詢特定會員的所有訂單
	    List<T35TicketOrderBean> findByMember(T41MemberBean member);
	    
	    // 查詢特定活動的所有訂單
	    List<T35TicketOrderBean> findByEvent(T31EventBean event);
	    
	    // 依購買日期範圍查詢
	    List<T35TicketOrderBean> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
	    
	    // 查詢特定會員在特定日期範圍的訂單
	    List<T35TicketOrderBean> findByMemberAndPurchaseDateBetween(
	        T41MemberBean member, 
	        LocalDate startDate, 
	        LocalDate endDate
	    );
	    
	    // 依訂單狀態查詢
	    List<T35TicketOrderBean> findByOrderStatus(T35TicketOrderBean.OrderStatus status);
	    
	    // 查詢特定會員的特定狀態訂單
	    List<T35TicketOrderBean> findByMemberAndOrderStatus(
	        T41MemberBean member, 
	        T35TicketOrderBean.OrderStatus status
	    );
	    //找尋特定會員的訂單
	    List<T35TicketOrderBean> findByMemberAndEvent(T41MemberBean member, T31EventBean event);
	    
	    // 檢查會員是否已購買某活動票券
	    boolean existsByMemberAndEvent(T41MemberBean member, T31EventBean event);
	    
		 // 根據綠界交易編號查詢訂單
	    Optional<T35TicketOrderBean> findByMerchantTradeNo(String merchantTradeNo);

	    // 查詢某會員的所有訂單
	    List<T35TicketOrderBean> findByMember_MemberId(Long memberId);

	    // 查詢某活動的所有訂單
	    List<T35TicketOrderBean> findByEvent_EventId(Long eventId);

	    // 查詢某會員在某活動的所有訂單
	    List<T35TicketOrderBean> findByMember_MemberIdAndEvent_EventId(Long memberId, Long eventId);
       
	    
	   

		
		
		
	}
	
	
	
	
	
	

