package tw.com.hippo_center_backend.h3controller.b2;

import java.util.List;
import java.util.Map;

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
import tw.com.hippo_center_backend.h2service.b2.CartService;
import tw.com.hippo_center_backend.h4dto.b2.CartDTO;
import tw.com.hippo_center_backend.h4dto.b2.SimpleCartDTO;
import tw.com.hippo_center_backend.h6jwt.JsonWebTokenUtility;



@RestController
@RequestMapping("/mowmow/admin/cart")
public class AdminCartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    @GetMapping
    public ResponseEntity<List<SimpleCartDTO>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

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
        Integer memberId = jsonObject.getInt("memberId");

        cartService.clearCart(memberId);
        return ResponseEntity.ok("購物車已清空");
    }

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
        Integer memberId = jsonObject.getInt("memberId");

        List<CartDTO> cartDTOs = cartService.viewCart(memberId);
        return ResponseEntity.ok(cartDTOs);
    }

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
        Integer memberId = jsonObject.getInt("memberId");

        cartService.removeFromCart(memberId, productId);
        return ResponseEntity.ok("商品已從購物車刪除");
    }

}
