package tw.com.hippo_center_backend.h3controller.b2;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
// import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import tw.com.hippo_center_backend.h0bean.T23CartBean;
import tw.com.hippo_center_backend.h0bean.T25OrderListBean;
import tw.com.hippo_center_backend.h2service.b2.CartService;
import tw.com.hippo_center_backend.h4dto.b2.CartDTO;
import tw.com.hippo_center_backend.h5util.EcpayProductFunctions;
import tw.com.hippo_center_backend.h6jwt.JsonWebTokenUtility;


@RestController
@RequestMapping("/mowmow/user/cart")
public class UserCartController {

    @Autowired
    private CartService cartService;


    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    @Autowired
    private EcpayProductFunctions ecpayFunctions; // 依賴注入

    // 新增商品至購物車
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> payload) { // 確保這裡有 `@RequestBody` 來接收 JSON

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }

        String token = authorizationHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }

        // 解析 Token 來獲取 `memberId`
        JSONObject jsonObject = new JSONObject(userData);
        Integer memberId = jsonObject.optInt("memberId", -1);

        if (memberId == -1) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }

        // 從 JSON 解析 `productId` 和 `quantity`
        Integer productId = payload.get("productId") != null ? ((Number) payload.get("productId")).intValue() : null;
        Integer quantity = payload.get("quantity") != null ? ((Number) payload.get("quantity")).intValue() : null;

        if (productId == null || quantity == null) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(null);
        }

        System.out.println("收到的 productId: " + productId);
        System.out.println("收到的 quantity: " + quantity);
        System.out.println("收到的 memberId: " + memberId);

        T23CartBean cartItem = cartService.addToCart(memberId, productId, quantity);

        // 轉換 DTO
        CartDTO dto = new CartDTO();
        dto.setCartId(cartItem.getCartId());
        dto.setMemberId(cartItem.getMember().getMemberId());
        dto.setProductId(cartItem.getProduct().getProductId());
        dto.setProductName(cartItem.getProduct().getProductName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProduct().getPrice());

        return ResponseEntity.ok(dto);
    }

    // 清空購物車
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("未授權");
        }

        String token = authorizationHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Token 無效");
        }

        JSONObject jsonObject = new JSONObject(userData);
        Integer memberId = jsonObject.optInt("memberId", -1);

        cartService.clearCart(memberId);
        return ResponseEntity.ok("購物車已清空");
    }

    // 結帳
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> payload) {

        try {
            System.out.println("\n=== [UserCartController] /checkout 被呼叫 ===");
            // 1. 驗證 Token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("未登入");
            }

            String token = authorizationHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);
            if (userData == null) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Token 無效");
            }
            JSONObject jsonObject = new JSONObject(userData);
            Integer memberId = jsonObject.optInt("memberId", -1);
            if (memberId == -1) {
                return ResponseEntity.badRequest().body("無法取得會員ID");
            }

            // 2. 從前端傳 address, phone, promotionMemberId
            String promotionMemberId = payload.get("promotionMemberId") != null
                    ? payload.get("promotionMemberId").toString()
                    : null;
            String address = (String) payload.get("address");
            String phone   = (String) payload.get("phone");
            if (address == null || address.isEmpty() || phone == null || phone.isEmpty()) {
                return ResponseEntity.badRequest().body("錯誤：地址與電話不能為空");
            }

            // 3. 檢查購物車
            List<CartDTO> cartItems = cartService.viewCart(memberId);
            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body("錯誤：購物車內沒有商品");
            }

            // 4. 建立訂單
            T25OrderListBean order = cartService.checkout(memberId, promotionMemberId, address, phone);
            String merchantTradeNo  = order.getMerchantTradeNo();
            int totalAmount         = order.getTotalAmount();

            System.out.println("[UserCartController] 訂單已建立, merchantTradeNo=" + merchantTradeNo 
                + ", totalAmount=" + totalAmount);

            // 5. 組商品名稱(例如: "A x2#B x3")
            String itemName = cartItems.stream()
                .map(item -> item.getProductName() + " x " + item.getQuantity())
                .reduce((a, b) -> a + "#" + b)
                .orElse("商品一批");

            // 6. 限制字數(ECPay限制 400 char 以內)
            if (itemName.length() > 400) {
                itemName = itemName.substring(0, 400);
            }

            // 7. 組交易時間(yyyy/MM/dd HH:mm:ss)
            String tradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

            // 8. 用 JSON 包住傳給 EcpayFunctions
            JSONObject ecJson = new JSONObject();
            ecJson.put("id",    merchantTradeNo);
            ecJson.put("name",  itemName);
            ecJson.put("total", totalAmount);
            ecJson.put("desc",  "購物結帳");
            ecJson.put("date",  tradeDate);

            // 9. 產生 ECPay 表單
            String form = ecpayFunctions.buildEcpayForm(ecJson.toString());

            System.out.println("[UserCartController] 已產生 ECPay form, 回傳前端");
            return ResponseEntity.ok(form);

        } catch (Exception e) {
            System.out.println("[UserCartController] checkout 出錯: " + e.getMessage());
            return ResponseEntity.badRequest().body("錯誤：" + e.getMessage());
        }
    }
    // @PostMapping("/checkout")
    // public ResponseEntity<T25OrderListBean> checkout(
    // @RequestHeader("Authorization") String authorizationHeader,
    // @RequestBody Map<String, Object> payload) {

    // if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer
    // ")) {
    // return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
    // }

    // String token = authorizationHeader.substring(7);
    // String userData = jsonWebTokenUtility.validateToken(token);
    // if (userData == null) {
    // return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
    // }

    // JSONObject jsonObject = new JSONObject(userData);
    // Integer memberId = jsonObject.getInt("memberId");

    // String promotionMemberId = (String) payload.get("promotionMemberId");
    // String address = (String) payload.get("address");

    // if (address == null || address.isEmpty()) {
    // return ResponseEntity.badRequest().body(null);
    // }

    // T25OrderListBean order = cartService.checkout(memberId, promotionMemberId,
    // address);
    // return ResponseEntity.ok(order);
    // }

    // 查詢購物車內容
    @GetMapping("/view")
    public ResponseEntity<List<CartDTO>> viewCart(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }

        String token = authorizationHeader.substring(7); // 去掉 'Bearer '
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }

        JSONObject jsonObject = new JSONObject(userData);
        Integer memberId = jsonObject.optInt("memberId", -1);
        if (memberId == -1) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }

        List<CartDTO> cartDTOs = cartService.viewCart(memberId);
        return ResponseEntity.ok(cartDTOs);
    }

    // public ResponseEntity<List<CartDTO>> viewCart(@RequestParam Integer memberId)
    // {
    // // 查詢購物車
    // List<T23CartBean> cartItems = cartService.viewCart(memberId);

    // // 轉換為 DTO
    // List<CartDTO> cartDTOs = cartItems.stream().map(cart -> {
    // CartDTO dto = new CartDTO();
    // dto.setCartId(cart.getCartId());
    // dto.setMemberId(cart.getMember().getMemberId());
    // dto.setProductId(cart.getProduct().getProductId());
    // dto.setProductName(cart.getProduct().getProductName());
    // dto.setQuantity(cart.getQuantity());
    // dto.setPrice(cart.getProduct().getPrice());
    // return dto;
    // }).collect(Collectors.toList());

    // return ResponseEntity.ok(cartDTOs);
    // }

    // 刪除購物車內容
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromCart(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer productId) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("未授權");
        }

        String token = authorizationHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Token 無效");
        }

        JSONObject jsonObject = new JSONObject(userData);
        Integer memberId = jsonObject.optInt("memberId", -1);

        cartService.removeFromCart(memberId, productId);
        return ResponseEntity.ok("商品已從購物車刪除");
    }

}
