package tw.com.hippo_center_backend.h3controller.b2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T25OrderListBean;
import tw.com.hippo_center_backend.h0bean.T25OrderListBean.OrderStatus;

// import tw.com.hippo_center_backend.h0bean.b2.T24OrderDetailsBean;

import tw.com.hippo_center_backend.h2service.b2.OrderService;
import tw.com.hippo_center_backend.h4dto.b2.OrderAdminDTO;
import tw.com.hippo_center_backend.h4dto.b2.OrderDTO;
import tw.com.hippo_center_backend.h4dto.b2.OrderDetailsDTO;
import tw.com.hippo_center_backend.h4dto.b2.OrderRequest;
import tw.com.hippo_center_backend.h4dto.b2.OrderSummaryDTO;

@RestController
@RequestMapping("/mowmow/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // 新增訂單
    @PostMapping("/create")
    public ResponseEntity<T25OrderListBean> createOrder(@RequestBody OrderRequest orderRequest) {
        T25OrderListBean order = orderService.createOrder(
                orderRequest.getMemberId(),
                orderRequest.getPromotionMemberId(),
                orderRequest.getAddress(),
                orderRequest.getPhone());
        return ResponseEntity.ok(order);
    }

    // 查詢會員所有訂單
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<OrderSummaryDTO>> getOrdersByMember(@PathVariable Integer memberId) {
        try {
            List<T25OrderListBean> orders = orderService.getOrdersByMember(memberId);

            // 將實體類轉換為 DTO
            List<OrderSummaryDTO> orderSummaries = orders.stream().map(order -> {
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

            return ResponseEntity.ok(orderSummaries);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 查詢單筆訂單(總額及狀態)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer orderId) {
        try {
            T25OrderListBean order = orderService.getOrderById(orderId);

            // 將實體類轉換為 DTO
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setOrderStatus(order.getOrderStatus().name());
            if (order.getPromotionMember() != null) {
                dto.setPromotionTitle(order.getPromotionMember().getPromotion().getTitle());
                dto.setPromotionCode(order.getPromotionMember().getPromotion().getPromotionCode());
            }

            // 包含訂單明細
            List<OrderDetailsDTO> details = order.getOrderDetails().stream().map(detail -> {
                OrderDetailsDTO detailDTO = new OrderDetailsDTO();
                detailDTO.setProductId(detail.getProduct().getProductId());
                detailDTO.setProductName(detail.getProduct().getProductName());
                detailDTO.setQuantity(detail.getQuantity());
                detailDTO.setPrice(detail.getProduct().getPrice());
                if (detail.getProduct().getImages() != null && !detail.getProduct().getImages().isEmpty()) {
                    detailDTO.setImageUrl(detail.getProduct().getImages().get(0).getImageUrl());
                }
                return detailDTO;
            }).toList();
            dto.setOrderDetails(details);

            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 查詢單筆訂單(訂單商品)
    @GetMapping("/{orderId}/details")
    public ResponseEntity<List<OrderDetailsDTO>> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        List<OrderDetailsDTO> details = orderService.getOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(details);
    }

    // 更新訂單狀態
    @PutMapping("/{orderId}/status")
    public ResponseEntity<T25OrderListBean> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            if (status == null) {
                throw new IllegalArgumentException("Status is required");
            }
            // 驗證狀態是否合法
            OrderStatus newStatus = OrderStatus.valueOf(status);

            // 調用服務層更新狀態
            T25OrderListBean updatedOrder = orderService.updateOrderStatus(orderId, newStatus.name());
            return ResponseEntity.ok(updatedOrder);

        } catch (IllegalArgumentException e) {
            // 傳入的狀態無效
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 刪除訂單
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("訂單已刪除");
    }

    // 查詢所有訂單
    @GetMapping("/findAll")
    public ResponseEntity<List<OrderAdminDTO>> getAllOrders() {
        try {
            List<T25OrderListBean> orders = orderService.findAllOrders();

            List<OrderAdminDTO> orderList = orders.stream().map(order -> {
                OrderAdminDTO dto = new OrderAdminDTO();
                dto.setOrderId(order.getOrderId());
                dto.setMerchantTradeNo(order.getMerchantTradeNo());
                dto.setOrderStatus(order.getOrderStatus().name());
                dto.setTotalAmount(order.getTotalAmount());
                dto.setOrderAddress(order.getOrderAddress());
                dto.setPhone(order.getPhone());
                dto.setCreatedAt(order.getCreatedAt());
                if (order.getPromotionMember() != null) {
                    dto.setPromotionMember(order.getPromotionMember().getPromotion().getTitle());
                }
                return dto;
            }).toList();

            return ResponseEntity.ok(orderList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
