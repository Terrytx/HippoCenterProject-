    package tw.com.hippo_center_backend.h2service.b1;

    import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.hippo_center_backend.h0bean.T33TicketBean;
import tw.com.hippo_center_backend.h1repository.T33TicketRepository;
import tw.com.hippo_center_backend.h8exception.ResourceNotFoundException;
    
    @Service
    public class T33TicketAdminService {
    
        @Autowired
        private T33TicketRepository ticketRepository;
    
        /**
         *  找出特定會員所購買的票
         *
         * @param memberId the ID of the member.
         * @return a list of tickets.
         */
        public List<T33TicketBean> getTicketsByMemberId(Integer memberId) {
            return ticketRepository.findByMemberId_MemberId(memberId);
        }
    
        /**
         *  找出特定活動的票
         *
         * @param eventId the ID of the event.
         * @return a list of tickets.
         */
        public List<T33TicketBean> getTicketsByEventId(Integer eventId) {
            return ticketRepository.findByEventId_EventId(eventId);
        }
    
        /**
         *  找出特定日期購買的票券
         *
         * @param purchaseDate the purchase date.
         * @return a list of tickets.
         */
        public List<T33TicketBean> getTicketsByPurchaseDate(LocalDate purchaseDate) {
            return ticketRepository.findByPurchaseDate(purchaseDate);
        }
    
        /**
         *  找出使用特定優惠券的顧客
         *
         * @param promotionMemberId the ID of the promotion member.
         * @return a list of tickets.
         */
       // public List<T33TicketBean> getTicketsByPromotionMemberId(String promotionMemberId) {
       //     return ticketRepository.findByPromotionMember_PromotionMemberId(promotionMemberId);
        //}
    
        /**
         * .找出特定活動及會員id購買的票
         *
         * @param eventId  the ID of the event.
         * @param memberId the ID of the member.
         * @return a list of tickets.
         */
        public List<T33TicketBean> getTicketsByEventAndMember(Integer eventId, Integer memberId) {
            return ticketRepository.findByEventId_EventIdAndMemberId_MemberId(eventId, memberId);
        }
    
        /**
         * 儲存票券進資料庫
         *
         * @param ticket the ticket to save.
         * @return the saved ticket.
         */
        public T33TicketBean saveTicket(T33TicketBean ticket) {
            return ticketRepository.save(ticket);
        }
    
        /**
         * 找出特定票券id的票
         *
         * @param ticketId the ID of the ticket.
         * @return an optional containing the ticket if found, or empty otherwise.
         */
        public Optional<T33TicketBean> getTicketById(Integer ticketId) {
            return ticketRepository.findById(ticketId);
        }
    
        // /**
        //  *刪除特定票券id的票 (慎用) 基本上disable
    
        @Transactional
        public void deleteEventById(Integer eventId) {
            if (ticketRepository.existsById(eventId)) {
                ticketRepository.deleteById(eventId);
            } else {
                throw new ResourceNotFoundException("活動 ID 不存在: " + eventId);
            }
        }

        /**
         * Retrieve all tickets. 查詢所有票券
         *
         * @return a list of all tickets.
         */
        public List<T33TicketBean> getAllTickets() {
            return ticketRepository.findAll();
        }
        
        T33TicketBean ticket = new T33TicketBean();

        @Transactional
        public T33TicketBean updateTicketStatus(Integer ticketId, T33TicketBean.TicketStatus newStatus) {
            Optional<T33TicketBean> optionalTicket = ticketRepository.findById(ticketId);
            if (optionalTicket.isPresent()) {
                T33TicketBean ticket = optionalTicket.get();
                // 例如，只允許從 VALID 狀態轉換，如果票券非 VALID，則不允許更新
                if (!ticket.getTicketStatus().equals(T33TicketBean.TicketStatus.VALID)) {
                    throw new IllegalStateException("只有狀態為 VALID 的票券才能更新");
                }
                ticket.setTicketStatus(newStatus);
                return ticketRepository.save(ticket);
            } else {
                throw new ResourceNotFoundException("票券不存在，ID: " + ticketId);
            }
        }

       /**
        * 檢查票券狀態
        *
        * @param ticketId 票券ID
        * @return 票券狀態資訊
        */
       public String checkTicketStatus(Integer ticketId) {
           Optional<T33TicketBean> optionalTicket = ticketRepository.findById(ticketId);
           if (optionalTicket.isPresent()) {
               T33TicketBean ticket = optionalTicket.get();
               int statusCode = ticket.getTicketStatus().getCode();
               String description = ticket.getTicketStatus().getDescription();
               boolean isUsable = ticket.getTicketStatus().isUsable();
               
               return String.format("票券狀態: %s (代碼: %d), 是否可用: %s", 
                   description, statusCode, isUsable ? "是" : "否");
           } else {
               throw new ResourceNotFoundException("票券不存在，ID: " + ticketId);
           }
       }

       /**
        * 根據狀態代碼更新票券
        *
        * @param ticketId 票券ID
        * @param statusCode 狀態代碼
        * @return 更新後的票券
        */
       @Transactional
       public T33TicketBean updateTicketByStatusCode(Integer ticketId, int statusCode) {
           T33TicketBean.TicketStatus newStatus = T33TicketBean.TicketStatus.getByCode(statusCode);
           return updateTicketStatus(ticketId, newStatus);
       }
        
        
        
    }
    