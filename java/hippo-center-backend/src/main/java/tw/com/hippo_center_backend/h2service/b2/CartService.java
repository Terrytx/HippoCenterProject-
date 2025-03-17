package tw.com.hippo_center_backend.h2service.b2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter; // ✅ 新增這行

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.hippo_center_backend.h0bean.T21ProductBean;
import tw.com.hippo_center_backend.h0bean.T23CartBean;
import tw.com.hippo_center_backend.h0bean.T24OrderDetailsBean;
import tw.com.hippo_center_backend.h0bean.T25OrderListBean;
import tw.com.hippo_center_backend.h0bean.T25OrderListBean.OrderStatus;
import tw.com.hippo_center_backend.h0bean.T27PromotionMemberBean;
import tw.com.hippo_center_backend.h0bean.T28ProductImageBean;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T23CartRepository;
import tw.com.hippo_center_backend.h1repository.T24OrderDetailsRepository;
import tw.com.hippo_center_backend.h1repository.T25OrderListRepository;
import tw.com.hippo_center_backend.h1repository.T27PromotionMemberRepository;
import tw.com.hippo_center_backend.h4dto.b2.CartDTO;
import tw.com.hippo_center_backend.h4dto.b2.SimpleCartDTO;

@Service
public class CartService {

    @Autowired
    private T23CartRepository cartRepository;

    @Autowired
    private T24OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private T25OrderListRepository orderListRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private T27PromotionMemberRepository promotionMemberRepository;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    // T41MemberBean member = new T41MemberBean();
    // member.setMemberId(memberId); // 正確設置 memberId

    // 新增商品至購物車
    public T23CartBean addToCart(Integer memberId, Integer productId, Integer quantity) {
        // 查詢產品
        T21ProductBean product = productService.getProductById(productId);

        // 檢查庫存
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product ID: " + productId);
        }

        // 查詢購物車是否已有該商品
        T23CartBean cartItem = cartRepository.findByMember_MemberIdAndProduct_ProductId(memberId, productId)
                .orElse(null);

        if (cartItem == null) {
            // 購物車中沒有該商品，新增項目
            cartItem = new T23CartBean();
            T41MemberBean member = new T41MemberBean();
            member.setMemberId(memberId);
            cartItem.setMember(member);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAddedAt(LocalDateTime.now());// 加入時間
        } else {
            // 購物車中已有該商品，累加數量
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > product.getStock()) {
                throw new IllegalArgumentException("Insufficient stock for product ID: " + productId);
            }
            cartItem.setQuantity(newQuantity);
        }

        // 更新產品庫存
        product.setStock(product.getStock() - quantity);
        productService.updateProduct(product);

        // 保存購物車項目
        return cartRepository.save(cartItem);
    }

    // 結帳
    @Transactional
    public T25OrderListBean checkout(Integer memberId, String promotionMemberId, String address, String phone) {

        if (memberId == null || address == null || address.isEmpty()) {
            throw new IllegalArgumentException("必須提供會員ID和訂單地址");
        }

        List<T23CartBean> cartItems = cartRepository.findByMember_MemberId(memberId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("購物車為空，無法結帳");
        }

        // 1️⃣ 計算商品總價（所有商品價格 x 數量）
        int productTotal = cartItems.stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        System.out.println("🛒 總金額（未折扣）: " + productTotal);

        // 2️⃣ 應用折扣（促銷券只影響商品價格，不影響運費）
        T27PromotionMemberBean promotion = null;
        double discountRate = 0.0;
        if (promotionMemberId != null && !promotionMemberId.trim().isEmpty()) {
            System.out.println("🔍 接收到的促銷券 ID: " + promotionMemberId);
            promotion = promotionService.validatePromotion(promotionMemberId);
            System.out.println("✅ 促銷券驗證結果：" + (promotion != null ? "成功" : "失敗"));

            if (promotion == null || promotion.getPromotion() == null) {
                throw new IllegalArgumentException("無效的促銷券 ID：" + promotionMemberId);
            }
            discountRate = promotion.getPromotion().getDiscountRate();
            if (discountRate < 0 || discountRate > 1) {
                throw new IllegalArgumentException("無效的折扣率：" + discountRate);
            }

            // 3️⃣ **先對商品總價應用折扣**
            productTotal = (int) Math.round(productTotal * (1 - discountRate));
            System.out.println("🎟️ 折扣率: " + discountRate + "，折扣後商品總金額: " + productTotal);

            // 標記促銷券為已使用
            promotion.setPromotionStatus(T27PromotionMemberBean.PromotionStatus.USED);
            promotion.setUsedAt(LocalDateTime.now());
            promotionMemberRepository.save(promotion);
            System.out.println("🎟️ 促銷券已成功標記為 USED：" + promotion.getPromotionMemberId());
        }

        // 4️⃣ **最後加上運費 100**
        int totalAmount = productTotal + 100;
        System.out.println("🚚 運費 100 加入後，最終應收金額: " + totalAmount);

        // **✅ 確保 `totalAmount` 被存入資料庫**
        T25OrderListBean order = new T25OrderListBean();
        T41MemberBean member = new T41MemberBean();
        member.setMemberId(memberId);
        order.setMember(member);
        order.setTotalAmount(totalAmount);
        order.setOrderAddress(address);
        order.setPhone(phone);
        order.setPromotionMember(promotion);
        order.setOrderStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        order = orderListRepository.save(order);

        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String merchantTradeNo = "ORDER" + order.getOrderId() + formattedDate;
        order.setMerchantTradeNo(merchantTradeNo);

        order = orderListRepository.save(order);

        for (T23CartBean cartItem : cartItems) {
            T24OrderDetailsBean orderDetail = new T24OrderDetailsBean();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setAddedAt(cartItem.getAddedAt());
            orderDetailsRepository.save(orderDetail);
        }

        cartRepository.deleteByMember_MemberId(memberId);

        System.out.println("✅ 訂單建立成功，ID：" + order.getOrderId() + "，MerchantTradeNo：" + merchantTradeNo);
        return order;
    }

    // 清空購物車
    public void clearCart(Integer memberId) {
        cartRepository.deleteByMember_MemberId(memberId);
        logger.info("Cleared cart for member ID: {}", memberId);
    }

    // 查詢購物車
    public List<CartDTO> viewCart(Integer memberId) {
        List<T23CartBean> cartItems = cartRepository.findByMember_MemberId(memberId);

        return cartItems.stream().map(cart -> {
            CartDTO dto = new CartDTO();
            dto.setCartId(cart.getCartId());
            dto.setMemberId(cart.getMember().getMemberId());
            dto.setProductId(cart.getProduct().getProductId());
            dto.setProductName(cart.getProduct().getProductName());
            dto.setQuantity(cart.getQuantity());
            dto.setPrice(cart.getProduct().getPrice());

            // 提取圖片 URL
            List<T28ProductImageBean> images = cart.getProduct().getImages();
            if (images != null && !images.isEmpty()) {
                dto.setImageUrl(images.get(0).getImageUrl()); // 只取第一張圖片
            }

            return dto;
        }).collect(Collectors.toList());
    }

    // 取得所有購物車資訊（簡化版）
    public List<SimpleCartDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(cart -> new SimpleCartDTO(
                        cart.getMember().getMemberId(),
                        cart.getProduct().getProductId(),
                        cart.getQuantity(),
                        cart.getAddedAt()))
                .toList();
    }

    // 刪除購物車中的單筆商品
    public void removeFromCart(Integer memberId, Integer productId) {
        // 查詢該會員的購物車項目
        T23CartBean cartItem = cartRepository.findByMember_MemberIdAndProduct_ProductId(memberId, productId)
                .orElseThrow(() -> new IllegalArgumentException("商品未找到"));

        // 釋放庫存
        T21ProductBean product = cartItem.getProduct();
        product.setStock(product.getStock() + cartItem.getQuantity());
        productService.updateProduct(product);

        // 刪除該項目
        cartRepository.delete(cartItem);
        logger.info("已刪除購物車中的商品 (memberId={}, productId={})", memberId, productId);
    }

}
