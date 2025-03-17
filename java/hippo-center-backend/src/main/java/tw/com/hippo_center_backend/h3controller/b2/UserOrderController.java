package tw.com.hippo_center_backend.h3controller.b2;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T25OrderListBean;
import tw.com.hippo_center_backend.h2service.b2.OrderService;
import tw.com.hippo_center_backend.h4dto.b2.OrderDTO;
import tw.com.hippo_center_backend.h4dto.b2.OrderDetailsDTO;
import tw.com.hippo_center_backend.h4dto.b2.OrderSummaryDTO;
import tw.com.hippo_center_backend.h6jwt.JsonWebTokenUtility;

@RestController
@RequestMapping("/mowmow/user/orders")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    // 查詢會員所有訂單
    @GetMapping("/findAll")
    public ResponseEntity<List<OrderSummaryDTO>> getUserOrders(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(null);
            }

            // 解析 JWT Token 來獲取 memberId
            String token = authorizationHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);
            if (userData == null) {
                return ResponseEntity.status(401).body(null);
            }

            JSONObject jsonObject = new JSONObject(userData);
            Integer memberId = jsonObject.optInt("memberId", -1);
            if (memberId == -1) {
                return ResponseEntity.status(403).body(null);
            }

            List<OrderSummaryDTO> orders = orderService.getOrdersByMember(memberId).stream().map(order -> {
                OrderSummaryDTO summary = new OrderSummaryDTO();
                summary.setOrderId(order.getOrderId());
                summary.setTotalAmount(order.getTotalAmount());
                summary.setOrderStatus(order.getOrderStatus().name());
                if (order.getPromotionMember() != null) {
                    summary.setPromotionTitle(order.getPromotionMember().getPromotion().getTitle());
                    summary.setPromotionCode(order.getPromotionMember().getPromotion().getPromotionCode());
                }
                return summary;
            }).toList();

            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 查詢會員單筆訂單詳情
@GetMapping("/{orderId}")
public ResponseEntity<OrderDTO> getUserOrderById(
        @PathVariable Integer orderId,
        @RequestHeader("Authorization") String authorizationHeader) {

    System.out.println("🔍 接收到訂單查詢請求: 訂單 ID=" + orderId);

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        System.out.println("❌ 缺少授權 Token");
        return ResponseEntity.status(401).body(null);
    }

    String token = authorizationHeader.substring(7);
    String userData = jsonWebTokenUtility.validateToken(token);
    if (userData == null) {
        System.out.println("❌ Token 無效");
        return ResponseEntity.status(401).body(null);
    }

    JSONObject jsonObject = new JSONObject(userData);
    Integer memberId = jsonObject.optInt("memberId", -1);
    System.out.println("🔍 會員 ID = " + memberId);

    if (memberId == -1) {
        System.out.println("❌ 會員 ID 無效");
        return ResponseEntity.status(403).body(null);
    }

    T25OrderListBean order = orderService.getOrderById(orderId);
    if (!order.getMember().getMemberId().equals(memberId)) {
        System.out.println("❌ 該會員無權訪問此訂單");
        return ResponseEntity.status(403).body(null);
    }

    System.out.println("✅ 查詢成功，訂單 ID=" + orderId);

    // 將 Order 轉換為 DTO
    OrderDTO dto = new OrderDTO();
    dto.setOrderId(order.getOrderId());
    dto.setTotalAmount(order.getTotalAmount());
    dto.setOrderStatus(order.getOrderStatus().name());

    if (order.getPromotionMember() != null) {
        dto.setPromotionTitle(order.getPromotionMember().getPromotion().getTitle());
        dto.setPromotionCode(order.getPromotionMember().getPromotion().getPromotionCode());
    }

    // 加入訂單商品詳情
    List<OrderDetailsDTO> orderDetails = orderService.getOrderDetailsByOrderId(orderId);
    dto.setOrderDetails(orderDetails);

    return ResponseEntity.ok(dto);
}


}
