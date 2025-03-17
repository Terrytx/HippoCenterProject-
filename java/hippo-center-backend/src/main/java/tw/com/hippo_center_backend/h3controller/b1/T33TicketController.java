package tw.com.hippo_center_backend.h3controller.b1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import tw.com.hippo_center_backend.h0bean.T33TicketBean;
import tw.com.hippo_center_backend.h0bean.T35TicketOrderBean;
import tw.com.hippo_center_backend.h2service.b1.ConfirmEmailService;
import tw.com.hippo_center_backend.h2service.b1.T33TicketService;
import tw.com.hippo_center_backend.h4dto.b1.PurchaseTicketRequest;
import tw.com.hippo_center_backend.h4dto.b1.T33TicketsearchDTO;
import tw.com.hippo_center_backend.h5util.b5.EcpayFunctions;
import tw.com.hippo_center_backend.h6jwt.JsonWebTokenUtility;

// @CrossOrigin(origins = { "http://localhost:8080", "http://127.0.0.1:5500" })
@RestController
@RequestMapping("/api/tickets")
public class T33TicketController {

//    private static final Logger logger = LoggerFactory.getLogger(T33TicketController.class);

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    @Autowired
    private T33TicketService ticketService;

    @Autowired
    private ConfirmEmailService emailService;

    // ★ 加入老師的 EcpayFunctions：
    @Autowired
    private EcpayFunctions ecpayFunctions;

    /**
     * 購票流程：維持你原本的建立訂單 / 票券邏輯，
     * 但在最後產生綠界表單並回傳給前端。
     */
    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseTicket(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PurchaseTicketRequest request) {

        try {
            System.err.println("🔍 [後端] 接收到購票請求");
            System.err.println("📝 Authorization Header: " + authorizationHeader);

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                System.err.println("❌ Authorization header 格式錯誤");
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("未登入");
            }

            // 驗證並解析 Token，獲取會員資訊
            String token = authorizationHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);
            if (userData == null) {
                System.err.println("❌ Token 驗證失敗");
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Token 無效");
            }
            JSONObject jsonObject = new JSONObject(userData);
            Integer memberId = jsonObject.getInt("memberId");

            System.err.println("👤 Member ID (From Token) = " + memberId);
            System.err.println("Request Content = " + request);

            if (request == null || request.getEventId() == null || request.getTicketAmount() <= 0) {
                System.out.println("❌ 請求參數無效");
                return ResponseEntity.badRequest().body("錯誤：無效的購票資訊");
            }

            // 確認 Token 內的 memberId 與 request 會員是否相符
            if (!memberId.equals(request.getMemberId())) {
                System.err.println("❌ 會員ID不匹配");
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("錯誤：會員資訊不符");
            }

            // ---- (1) 呼叫 Service 建立訂單與票券 ----
            List<T33TicketBean> tickets = ticketService.purchaseTicket(
                    request.getEventId(),
                    memberId,
                    request.getTicketAmount(),
                    request.getEventPrice());

            if (tickets == null || tickets.isEmpty()) {
                System.out.println("❌ 票券購買失敗：未成功創建票券");
                return ResponseEntity.badRequest().body("錯誤：票券購買失敗");
            }

            // ---- (2) 取得訂單資訊，用於整合綠界 ----
            T35TicketOrderBean order = tickets.get(0).getTicketOrder();
            String merchantTradeNo = order.getMerchantTradeNo(); // 訂單編號
            int totalAmount = order.getFinalPrice(); // 總價
            String itemName = String.format("活動門票 x %d", request.getTicketAmount());
            String tradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

            System.out.println("✅ 訂單創建成功，訂單編號：" + merchantTradeNo);

            // ---- (3) 發送購票確認郵件 (如有需要可留著) ----
            try {
                emailService.sendPurchaseConfirmation(tickets, order);
                System.out.println("✉️ 購票確認郵件已發送");
            } catch (Exception e) {
                System.err.println("⚠️ 郵件發送失敗：" + e.getMessage());
            }

            // ---- (4) 呼叫「老師的 EcpayFunctions」產生綠界交易表單 ----
            JSONObject ecpayBody = new JSONObject();
            // 設定老師程式所需的欄位
            ecpayBody.put("id", merchantTradeNo); // MerchantTradeNo
            ecpayBody.put("name", itemName); // ItemName
            ecpayBody.put("total", String.valueOf(totalAmount)); // 轉成字串再放到JSON
            ecpayBody.put("desc", "活動門票購買"); // TradeDesc
            ecpayBody.put("date", tradeDate); // yyyy/MM/dd HH:mm:ss

            // 透過 ecpayFunctions 產生 HTML form
            String paymentForm = ecpayFunctions.buildEcpayForm(ecpayBody.toString());

            // ---- (5) 將表單字串回傳前端 ----
            // 前端可直接把這個 form 注入 DOM 或者彈跳新頁面送出
            return ResponseEntity.ok(paymentForm);

        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ 參數錯誤：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("錯誤：" + e.getMessage());
        } catch (Exception e) {
            System.err.println("⚠️ 購票流程發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("系統錯誤：" + e.getMessage());
        }
    }

    /**
     * （以下為示範：原有查詢票券等功能，不動）
     */
    @GetMapping("/{ticketId}/details")
    public ResponseEntity<T33TicketsearchDTO> getTicketDetails(@PathVariable Integer ticketId) {
        try {
            T33TicketsearchDTO ticketDTO = ticketService.findTicketWithMemberAndEvent(ticketId);
            return ResponseEntity.ok(ticketDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByMemberId(@PathVariable Integer memberId) {
        List<T33TicketBean> tickets = ticketService.getTicketsByMemberId(memberId);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/all")
    public ResponseEntity<List<T33TicketBean>> getAllTickets() {
        List<T33TicketBean> tickets = ticketService.findAllTickets();
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByEventId(@PathVariable Integer eventId) {
        List<T33TicketBean> tickets = ticketService.getTicketsByEventId(eventId);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/purchase-date/{date}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByPurchaseDate(@PathVariable String date) {
        LocalDate purchaseDate = LocalDate.parse(date);
        List<T33TicketBean> tickets = ticketService.getTicketsByPurchaseDate(purchaseDate);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/{eventId}/member/{memberId}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByEventIdAndMemberId(@PathVariable Integer eventId,
            @PathVariable Integer memberId) {
        List<T33TicketBean> tickets = ticketService.getTicketsByEventIdAndMemberId(eventId, memberId);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/name/{eventName}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByEventName(@PathVariable String eventName) {
        List<T33TicketBean> tickets = ticketService.getTicketsByEventName(eventName);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/member/{memberId}")
    public ResponseEntity<List<T33TicketBean>> getTicketsByEvent(
            @RequestParam(required = false) Integer eventId,
            @RequestParam(required = false) String eventName,
            @PathVariable Integer memberId) {

        if (eventId == null && (eventName == null || eventName.isEmpty())) {
            return ResponseEntity.badRequest().body(null);
        }
        List<T33TicketBean> tickets;
        if (eventId != null) {
            tickets = ticketService.getTicketsByEventIdAndMemberId(eventId, memberId);
        } else {
            tickets = ticketService.getTicketsByEventName(eventName);
        }
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    
}
