package tw.com.hippo_center_backend.h3controller.b1;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T33TicketBean;
import tw.com.hippo_center_backend.h2service.b1.T33TicketAdminService;
//import tw.com.hippo_center_backend.h2service.b1.T33TicketService;
//import tw.com.hippo_center_backend.h4dto.b1.PurchaseTicketRequest;
//import tw.com.hippo_center_backend.h8exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/admin/tickets")
public class T33TicketAdminController {

    @Autowired
    private T33TicketAdminService ticketService;

  
    
    /**
     * Get all tickets. 查詢所有票券
     * 
     * @return a list of all tickets.
     */
    @GetMapping("/all")
    public ResponseEntity<List<T33TicketBean>> getAllTickets() {
        List<T33TicketBean> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    /**
     * Get a ticket by its ID. 用票券id查出特定票券
     * 
     * @param ticketId the ID of the ticket.
     * @return the ticket if found, or 404 if not found.
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<T33TicketBean> getTicketById(@PathVariable Integer ticketId) {
        Optional<T33TicketBean> ticket = ticketService.getTicketById(ticketId);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get tickets by member ID. 用memberId查詢特定票券
     * 
     * @param memberId the ID of the member.
     * @return a list of tickets for the specified member.
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByMemberId(@PathVariable Integer memberId) {
    	System.out.println("查詢");
        List<T33TicketBean> tickets = ticketService.getTicketsByMemberId(memberId);
        System.out.println(tickets);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Get tickets by event ID.
     * 
     * @param eventId the ID of the event.
     * @return a list of tickets for the specified event.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByEventId(@PathVariable Integer eventId) {
        List<T33TicketBean> tickets = ticketService.getTicketsByEventId(eventId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Get tickets by purchase date.
     * 
     * @param purchaseDate the purchase date in format yyyy-MM-dd.
     * @return a list of tickets purchased on the specified date.
     */
    @GetMapping("/purchase-date/{purchaseDate}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByPurchaseDate(@PathVariable String purchaseDate) {
        LocalDate date = LocalDate.parse(purchaseDate);
        List<T33TicketBean> tickets = ticketService.getTicketsByPurchaseDate(date);
        return ResponseEntity.ok(tickets);
    }

    /** 查詢特定會員的票券
     * Get tickets by promotion member ID.
     * 
     * @param promotionMemberId the ID of the promotion member.
     * @return a list of tickets for the specified promotion member.
     */
  //  @GetMapping("/promotion-member/{promotionMemberId}")
  //  public ResponseEntity<List<T33TicketBean>> getTicketsByPromotionMemberId(@PathVariable String promotionMemberId) {
  //      List<T33TicketBean> tickets = ticketService.getTicketsByPromotionMemberId(promotionMemberId);
  //      return ResponseEntity.ok(tickets);
  //  }

    /**
     * Update an existing ticket. 更新票券資訊
     * 
     * @param ticketId the ID of the ticket to update.
     * @param ticket   the updated ticket data.
     * @return the updated ticket, or 404 if the ticket does not exist.
     */
    @PutMapping("/{ticketId}")
    public ResponseEntity<?> updateTicket(@PathVariable Integer ticketId,
            @RequestBody T33TicketBean ticket) {
        Optional<T33TicketBean> existingTicket = ticketService.getTicketById(ticketId);
        if (existingTicket.isPresent()) {
            T33TicketBean currentTicket = existingTicket.get();
            // 僅更新 ticketAmount（如果有傳送的話）
            if (ticket.getTicketAmount() != null) {
                currentTicket.setTicketAmount(ticket.getTicketAmount());
            }
            // 若請求中有票券狀態修改且與目前狀態不同
            if (ticket.getTicketStatus() != null &&
                !ticket.getTicketStatus().equals(currentTicket.getTicketStatus())) {
                // 如果當前票券狀態不是 VALID，只有允許更新為 VALID
                if (!currentTicket.getTicketStatus().equals(T33TicketBean.TicketStatus.VALID)
                    && !ticket.getTicketStatus().equals(T33TicketBean.TicketStatus.VALID)) {
                    return ResponseEntity.badRequest().body("票券無效");
                }
                currentTicket.setTicketStatus(ticket.getTicketStatus());
            }
            // 其他欄位若需更新，可依此方式設定
            T33TicketBean updatedTicket = ticketService.saveTicket(currentTicket);
            return ResponseEntity.ok(updatedTicket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a ticket by its ID. 刪除票券(必須要disable平常不可開啟)
     * 
     * @param ticketId the ID of the ticket to delete.
     * @return a response indicating the result of the operation.
     */
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable Integer ticketId) {
        if (ticketService.getTicketById(ticketId).isPresent()) {
            ticketService.deleteEventById(ticketId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    

    
    
    
}
