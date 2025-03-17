package tw.com.hippo_center_backend.h2service.b2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T24OrderDetailsBean;
import tw.com.hippo_center_backend.h0bean.T25OrderListBean;
import tw.com.hippo_center_backend.h0bean.T25OrderListBean.OrderStatus;
import tw.com.hippo_center_backend.h0bean.T28ProductImageBean;
import tw.com.hippo_center_backend.h1repository.T24OrderDetailsRepository;
import tw.com.hippo_center_backend.h1repository.T25OrderListRepository;
// import tw.com.hippo_center_backend.h2service.b2.CartService;
import tw.com.hippo_center_backend.h4dto.b2.OrderDetailsDTO;

@Service
@Transactional
public class OrderService {

    @Autowired
    private T25OrderListRepository orderListRepository;

    @Autowired
    private T24OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private CartService cartService;

    // 新增訂單
    public T25OrderListBean createOrder(Integer memberId, String promotionMemberId, String address, String phone) {
        // 直接使用 CartService 的 checkout 邊緣機制
        return cartService.checkout(memberId, promotionMemberId, address, phone);
    }

    // 查詢會員所有訂單
    public List<T25OrderListBean> getOrdersByMember(Integer memberId) {
        return orderListRepository.findByMember_MemberId(memberId);
    }

    // 查詢單筆訂單(總額及狀態)
    public T25OrderListBean getOrderById(Integer orderId) {
        return orderListRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    // 查詢單筆訂單(訂單商品)
    public List<OrderDetailsDTO> getOrderDetailsByOrderId(Integer orderId) {
        List<T24OrderDetailsBean> orderDetails = orderDetailsRepository.findByOrder_OrderId(orderId);

        // 將實體轉換為 DTO
        return orderDetails.stream().map(detail -> {
            OrderDetailsDTO dto = new OrderDetailsDTO();
            dto.setProductId(detail.getProduct().getProductId());
            dto.setProductName(detail.getProduct().getProductName());
            dto.setQuantity(detail.getQuantity());
            dto.setPrice(detail.getProduct().getPrice());
            List<T28ProductImageBean> images = detail.getProduct().getImages();
            if (images != null && !images.isEmpty()) {
                dto.setImageUrl(images.get(0).getImageUrl());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // 更新訂單狀態
    @Transactional
    public T25OrderListBean updateOrderStatus(Integer orderId, String newStatus) {
        System.out.println("🔍 嘗試更新訂單 ID：" + orderId + "，變更狀態為：" + newStatus);

        T25OrderListBean order = orderListRepository.findById(orderId).orElse(null);
        if (order == null) {
            System.err.println("❌ 找不到訂單：" + orderId);
            throw new IllegalStateException("找不到訂單：" + orderId);
        }

        System.out.println("🔍 [更新前] 訂單狀態：" + order.getOrderStatus());

        // ✅ 允許 `NEW -> PAID`
        if (order.getOrderStatus() == OrderStatus.NEW && newStatus.equals("PAID")) {
            order.setOrderStatus(OrderStatus.PAID);
            System.out.println("✅ 訂單狀態變更為 `PAID`");
        }
        // ✅ 允許 `PAID -> SHIPPED`
        else if (order.getOrderStatus() == OrderStatus.PAID && newStatus.equals("SHIPPED")) {
            order.setOrderStatus(OrderStatus.SHIPPED);
            System.out.println("✅ 訂單狀態變更為 `SHIPPED`");
        }
        // ✅ 允許 `SHIPPED -> DELIVERED`
        else if (order.getOrderStatus() == OrderStatus.SHIPPED && newStatus.equals("DELIVERED")) {
            order.setOrderStatus(OrderStatus.DELIVERED);
            System.out.println("✅ 訂單狀態變更為 `DELIVERED`");
        }
        // ❌ 其他狀態變更不允許
        else {
            System.err.println("⚠️ 訂單狀態未變更，請檢查條件！" + order.getOrderStatus() + " -> " + newStatus);
            throw new IllegalStateException("非法的狀態變更: " + order.getOrderStatus() + " -> " + newStatus);
        }

        order.setUpdatedAt(LocalDateTime.now());
        return orderListRepository.save(order);
    }

    // 刪除
    public void deleteOrder(Integer orderId) {
        if (!orderListRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Order not found");
        }
        orderDetailsRepository.findByOrder_OrderId(orderId)
                .forEach(orderDetailsRepository::delete);
        orderListRepository.deleteById(orderId);
    }

    public Optional<T25OrderListBean> findByMerchantTradeNo(String merchantTradeNo) {
        return orderListRepository.findByMerchantTradeNo(merchantTradeNo);
    }

    // 查詢所有訂單
    public List<T25OrderListBean> findAllOrders() {
        return orderListRepository.findAll();
    }

}
