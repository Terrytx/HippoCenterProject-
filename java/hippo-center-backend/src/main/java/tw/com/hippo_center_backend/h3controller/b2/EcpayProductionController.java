package tw.com.hippo_center_backend.h3controller.b2;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import tw.com.hippo_center_backend.h0bean.T25OrderListBean;
import tw.com.hippo_center_backend.h2service.b2.OrderService;

@RestController
@RequestMapping("/pecpay")
public class EcpayProductionController {

    @Autowired
    private OrderService orderService;

    /**
     * 綠界金流付款完成(或失敗)後，會在後台自動呼叫這支 API
     */
    @PostMapping("/return")
    public ResponseEntity<String> handleEcpayReturn(HttpServletRequest request) {
        System.out.println("\n===[EcpayController] /ecpay/return 被呼叫===");

        // 1. 收集所有參數
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String paramName = names.nextElement();
            params.put(paramName, request.getParameter(paramName));
        }

        System.out.println("[EcpayController] 綠界通知參數: " + new JSONObject(params).toString(4));

        String merchantTradeNo = params.get("MerchantTradeNo");
        String rtnCode         = params.get("RtnCode");
        String tradeAmt        = params.get("TradeAmt");

        // 2. rtnCode == "1" 代表付款成功
        if ("1".equals(rtnCode)) {
            System.out.println("[EcpayController] 付款成功, 訂單編號=" + merchantTradeNo);

            try {
                // 2.1 找出訂單
                T25OrderListBean order = orderService.findByMerchantTradeNo(merchantTradeNo)
                    .orElseThrow(() -> new IllegalArgumentException("找不到訂單:" + merchantTradeNo));

                // 2.2 確認金額
                Integer paidAmount = Integer.valueOf(tradeAmt);
                if (!order.getTotalAmount().equals(paidAmount)) {
                    System.err.println("[EcpayController] 金額不符, 應收=" + order.getTotalAmount() + ", 實付=" + tradeAmt);
                    return ResponseEntity.badRequest().body("0|AmountMismatch");
                }

                // 2.3 更新訂單狀態 → PAID
                order = orderService.updateOrderStatus(order.getOrderId(), "PAID");
                if (order.getOrderStatus() == T25OrderListBean.OrderStatus.PAID) {
                    System.out.println("[EcpayController] 訂單已更新為 PAID, orderId=" + order.getOrderId());
                } else {
                    System.err.println("[EcpayController] 訂單狀態未更新, 請檢查業務邏輯");
                    return ResponseEntity.badRequest().body("0|UpdateFailed");
                }

                // 2.4 回應綠界: 1|OK
                return ResponseEntity.ok("1|OK");
            } catch (Exception e) {
                System.err.println("[EcpayController] 訂單更新失敗: " + e.getMessage());
                return ResponseEntity.badRequest().body("0|UpdateError");
            }
        } else {
            System.out.println("[EcpayController] 付款失敗 / 交易未成功, RtnCode=" + rtnCode);
            return ResponseEntity.badRequest().body("0|PaymentFailed");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEcpay() {
        System.out.println("===[EcpayController] /ecpay/test 被呼叫===");
        return ResponseEntity.ok("EcpayController 正常運作");
    }
}
