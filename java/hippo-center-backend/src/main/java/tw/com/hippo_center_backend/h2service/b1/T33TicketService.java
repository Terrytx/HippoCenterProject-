package tw.com.hippo_center_backend.h2service.b1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import tw.com.hippo_center_backend.h0bean.T31EventBean;
import tw.com.hippo_center_backend.h0bean.T33TicketBean;
import tw.com.hippo_center_backend.h0bean.T35TicketOrderBean;
import tw.com.hippo_center_backend.h0bean.T35TicketOrderBean.OrderStatus;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T31EventRepository;
import tw.com.hippo_center_backend.h1repository.T33TicketRepository;
import tw.com.hippo_center_backend.h1repository.T35TicketOrderRepository;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;
import tw.com.hippo_center_backend.h4dto.b1.T33TicketsearchDTO;

@Service
public class T33TicketService {

    @Autowired
    private T35TicketOrderRepository orderRepository;
    @Autowired
    private T31EventRepository eventRepository;
    @Autowired
    private T33TicketRepository ticketRepository;
    @Autowired
    private T31EventService eventService;
    @Autowired
    private T41MemberRepository memberRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ConfirmEmailService emailService;

    @Transactional
    public List<T33TicketBean> purchaseTicket(Integer eventId, Integer memberId, Integer ticketAmount, Integer eventPrice) {
        // 驗證活動是否存在
    	 
        T31EventBean event = eventService.validateEventExists(eventId);

        // 驗證活動是否已發布
        if (!event.getIsPublished()) {
            throw new IllegalArgumentException("此活動尚未發布，無法購買票券");
        }

        // 驗證活動時間
        LocalDate currentDate = LocalDate.now();
        
        // 檢查活動是否已開始
        if (currentDate.isBefore(event.getEventStartDate())) {
            throw new IllegalArgumentException("活動尚未開始，無法購買票券");
        }
        
        // 檢查活動是否已結束
        if (currentDate.isAfter(event.getEventEndDate())) {
            throw new IllegalArgumentException("活動已結束，無法購買票券");
        }

        // 驗證會員是否存在
        T41MemberBean member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("會員不存在"));

        // 計算總價
        Integer totalPrice = ticketAmount * eventPrice;

        // 建立訂單
        T35TicketOrderBean order = new T35TicketOrderBean();
        order.setMember(member);
        order.setEvent(event);
        order.setTicketPrice(eventPrice);
        order.setFinalPrice(totalPrice);
        order.setPurchaseDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setMerchantTradeNo("T" + System.currentTimeMillis());
        orderRepository.save(order);

        // 建立票券
        order = orderRepository.save(order);
        List<T33TicketBean> tickets = new ArrayList<>();
        try {
            for (int i = 0; i < ticketAmount; i++) {
                T33TicketBean ticket = new T33TicketBean();
                ticket.setEventId(event);
                ticket.setMemberId(member);
                ticket.setEventPrice(eventPrice);
                ticket.setPurchaseDate(LocalDate.now());
                ticket.setTicketOrder(order);
                ticket.setTicketAmount(1);

                String qrCodeContent = generateQRCodeContent(ticket, order);
                ticket.setQrCode(qrCodeContent);
                tickets.add(ticket);
            }
            
            // 儲存所有票券
            List<T33TicketBean> savedTickets = ticketRepository.saveAll(tickets);
            
            // 發送購票確認郵件
            try {
                emailService.sendPurchaseConfirmation(savedTickets, order);
            } catch (Exception e) {
                System.err.println("發送購票確認郵件失敗：" + e.getMessage());
            }
            
            return savedTickets;
            
        } catch (Exception e) {
            throw new RuntimeException("票券建立失敗", e);
        }
    }

    private String generateQRCodeContent(T33TicketBean ticket, T35TicketOrderBean order) {
        String qrData = "OrderID: " + order.getId() + 
                       ", TicketID: " + ticket.getTicketId() + 
                       ", Event: " + ticket.getEventId().getEventName() + 
                       ", StartDate: " + ticket.getEventId().getEventStartDate() +
                       ", EndDate: " + ticket.getEventId().getEventEndDate();
        
        return UUID.nameUUIDFromBytes(qrData.getBytes()).toString();
    }

    public T35TicketOrderBean saveOrder(T35TicketOrderBean order) {
        return orderRepository.save(order);
    }
    
    public T33TicketsearchDTO findTicketWithMemberAndEvent(Integer ticketId) {
        T33TicketBean ticket = ticketRepository.findTicketWithMemberAndEvent(ticketId);

        if (ticket == null) {
            throw new IllegalArgumentException("找不到對應的票券");
        }

        return new T33TicketsearchDTO(
            ticket.getTicketId(),
            ticket.getTicketAmount(),
            ticket.getMemberId().getName(),
            ticket.getEventId().getEventName(),
            ticket.getEventId().getVenue().getVenueName(),
            ticket.getEventPrice(),
            ticket.getEventId().getEventStartDate(),
            ticket.getEventId().getEventEndDate(),
            ticket.getPurchaseDate(),
            ticket.getTicketStatus().name()
        );
    }
     
        /**
         * 透過 ticketId 查詢票券資訊，包含會員與活動
         * @param ticketId 票券 ID
         * @return T33TicketBean 票券資訊，包含會員與活動
         */
        public T33TicketBean findTicketWithMemberAndEvent1(Integer ticketId) {
            T33TicketBean ticket = ticketRepository.findTicketWithMemberAndEvent(ticketId);

            if (ticket == null) {
                throw new IllegalArgumentException("找不到對應的票券");
            }

            // 確保會員與活動資訊存在
            if (ticket.getMemberId() == null) {
                throw new IllegalArgumentException("票券未綁定會員");
            }
            if (ticket.getEventId() == null) {
                throw new IllegalArgumentException("票券未綁定活動");
            }

            return ticket;
        }
      
           
    /**
     * 查找所有票券
     * @return 票券列表
     */
    public List<T33TicketBean> findAllTickets() {
        return ticketRepository.findAll();
    }
 
    /**
     * 依照會員 ID 查找該會員購買的所有票券
     * @param memberId 會員 ID
     * @return 該會員的票券列表
     */
    public List<T33TicketBean> getTicketsByMemberId(Integer memberId) {
        return ticketRepository.findByMemberId_MemberId(memberId);
    }

    /**
     * 依照活動 ID 查找所有票券
     * @param eventId 活動 ID
     * @return 該活動的票券列表
     */
    public List<T33TicketBean> getTicketsByEventId(Integer eventId) {
        return ticketRepository.findByEventId_EventId(eventId);
    }

    /**
     * 依照購買日期查找票券
     * @param purchaseDate 購買日期
     * @return 指定日期購買的票券列表
     */
    public List<T33TicketBean> getTicketsByPurchaseDate(LocalDate purchaseDate) {
        return ticketRepository.findByPurchaseDate(purchaseDate);
    }

    /**
     * 依照活動 ID 與會員 ID 查找票券
     * @param eventId 活動 ID
     * @param memberId 會員 ID
     * @return 該會員購買的特定活動的票券列表
     */
    public List<T33TicketBean> getTicketsByEventIdAndMemberId(Integer eventId, Integer memberId) {
        return ticketRepository.findByEventId_EventIdAndMemberId_MemberId(eventId, memberId);
    }

    /**
     * 依照活動名稱查找票券
     * @param eventName 活動名稱
     * @return 該活動名稱的票券列表
     */
    public List<T33TicketBean> getTicketsByEventName(String eventName) {
        return ticketRepository.findByEventId_EventName(eventName);
    }

 // 根據ID獲取票券
    public T33TicketBean getTicketById(Integer ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    // 處理退票
    @Transactional
    public Boolean refundTicket(T33TicketBean ticket) {
        try {
            // 更新票券狀態為已退款
            ticket.setTicketStatus(T33TicketBean.TicketStatus.REFUNDED);
            ticketRepository.save(ticket);
            
            // 發送退票通知
            sendRefundNotification(ticket);
            
            return true;
        } catch (Exception e) {
            System.err.println("退票處理失敗：" + e.getMessage());
            return false;
        }
    }

    // 發送退票通知郵件
    private void sendRefundNotification(T33TicketBean ticket) {
        try {
            String recipientEmail = ticket.getMemberId().getAccount();
            String eventName = ticket.getEventId().getEventName();
            LocalDate eventDate = ticket.getEventId().getEventStartDate();
            int refundAmount = ticket.getEventPrice() * ticket.getTicketAmount();

            String subject = "活動退票成功通知";
            String content = String.format("""
                親愛的 %s 您好，
                
                您的退票申請已處理完成，詳細資訊如下：
                
                活動名稱：%s
                活動日期：%s
                退票數量：%d 張
                退款金額：NT$ %d
                
                退款將在 3-5 個工作天內退回原付款帳戶。
                如有任何問題，請聯繫客服：service@hippo-center.com
                
                感謝您的支持！
                
                Hippo Center 團隊
                此郵件為系統自動發送，請勿直接回覆。
                """,
                ticket.getMemberId().getName(),
                eventName,
                eventDate,
                ticket.getTicketAmount(),
                refundAmount
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@hippo-center.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(content);

            mailSender.send(message);
            System.out.println("✉️ 退票通知郵件已發送至：" + recipientEmail);

        } catch (Exception e) {
            System.err.println("❌ 發送退票通知郵件失敗：" + e.getMessage());
            // 這裡我們只記錄錯誤但不中斷流程，因為郵件發送失敗不應影響退票過程
        }
    }

	//更新訂單狀態    
//  public void updateOrderRefundStatus(Integer memberId, Integer eventId) {
//	        Optional<T35TicketOrderBean> optionalOrder = orderRepository.findByMemberAndEvent(
//	            memberRepository.findById(memberId).orElse(null),
//	            eventRepository.findById(eventId).orElse(null)
//	        );
//
//	        optionalOrder.ifPresent(order -> {
//	            order.setOrderStatus(T35TicketOrderBean.OrderStatus.REFUNDED);
//	            orderRepository.save(order);
//	        });
//	    }
	
}




