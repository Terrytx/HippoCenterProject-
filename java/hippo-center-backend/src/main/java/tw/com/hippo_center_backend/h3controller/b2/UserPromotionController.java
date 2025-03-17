package tw.com.hippo_center_backend.h3controller.b2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h2service.b2.PromotionService;
import tw.com.hippo_center_backend.h4dto.b2.PromotionDetailsDTO;
import tw.com.hippo_center_backend.h4dto.b2.PromotionSummaryDTO;


@RestController
@RequestMapping("/mowmow/user/promotions")
public class UserPromotionController {

    @Autowired
    private PromotionService promotionService;

    // 查詢會員擁有的促銷券
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PromotionDetailsDTO>> getMemberPromotions(@PathVariable Integer memberId) {
        try {
            List<PromotionDetailsDTO> promotions = promotionService.getPromotionsByMemberDetails(memberId);
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 使用促銷券
    @PostMapping("/use/{promotionMemberId}")
public ResponseEntity<List<PromotionDetailsDTO>> usePromotion(@PathVariable String promotionMemberId) {
    try {
        List<PromotionDetailsDTO> updatedPromotions = promotionService.usePromotionById(promotionMemberId);
        return ResponseEntity.ok(updatedPromotions); // ✅ 直接回傳最新的促銷券列表
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(null);
    }
}


    // 結帳並套用促銷券
    // @PostMapping("/checkout")
    // public ResponseEntity<String> checkout(@RequestBody CheckoutRequest request)
    // {
    // Integer memberId = request.getMemberId();
    // String promotionMemberId = request.getPromotionMemberId();
    // Integer originalPrice = request.getOriginalPrice();

    // if (memberId == null || promotionMemberId == null || originalPrice == null) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("所有參數都是必需的");
    // }

    // try {
    // T27PromotionMemberBean promotion =
    // promotionService.getPromotionById(promotionMemberId);

    // if (!promotion.isValid()) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("促銷券已過期或已使用");
    // }

    // double discountRate = promotion.getPromotion().getDiscountRate();
    // double discountedPrice = Math.round(originalPrice * discountRate);

    // // 標記促銷券為已使用
    // promotionService.usePromotionById(promotionMemberId);

    // return ResponseEntity.ok("原價：" + originalPrice + "，折扣後金額：" +
    // discountedPrice);
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    // }
    // }

    // 查詢會員的可用促銷券
    @GetMapping("/member/{memberId}/available")
    public ResponseEntity<List<PromotionSummaryDTO>> getAvailablePromotions(@PathVariable Integer memberId) {
        try {
            List<PromotionSummaryDTO> availablePromotions = promotionService
                    .getAvailablePromotionsForCheckout(memberId);
            return ResponseEntity.ok(availablePromotions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

// @PostMapping("/checkout")
// public ResponseEntity<String> checkout(
// @RequestParam Integer memberId,
// @RequestParam String promotionMemberId,
// @RequestParam Integer originalPrice) {

// // 1. 查詢促銷券
// T27PromotionMemberBean promotion =
// promotionMemberRepository.findById(promotionMemberId)
// .orElseThrow(() -> new IllegalStateException("Promotion not found"));

// // 2. 驗證促銷券是否有效
// if (!promotion.isValid()) {
// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Promotion is not
// valid or expired");
// }

// // 3. 計算折扣後的價格（四捨五入）
// double discountRate = promotion.getPromotion().getDiscountRate();
// int discountedPrice = (int) Math.round(originalPrice * discountRate);

// // 4. 標記促銷券為已使用
// promotion.markAsUsed();
// promotionMemberRepository.save(promotion);

// // 5. 返回最終金額
// return ResponseEntity.ok("Final price: " + discountedPrice);
// }
// 查詢會員擁有的促銷券
// @GetMapping("/member/{memberId}")
// public ResponseEntity<List<T27PromotionMemberBean>>
// getMemberPromotions(@PathVariable Integer memberId) {
// T41MemberBean member = memberRepository.findById(memberId)
// .orElseThrow(() -> new IllegalArgumentException("會員不存在"));

// List<T27PromotionMemberBean> promotions =
// promotionService.getPromotionsByMember(member);
// return ResponseEntity.ok(promotions);
// }
// 查詢會員的可用促銷券
// @GetMapping("/member/{memberId}/available")
// public ResponseEntity<List<T27PromotionMemberBean>>
// getAvailablePromotions(@PathVariable Integer memberId) {
// T41MemberBean member = memberRepository.findById(memberId)
// .orElseThrow(() -> new IllegalArgumentException("會員不存在"));

// List<T27PromotionMemberBean> availablePromotions =
// promotionService.getAvailablePromotionsByMember(member);
// return ResponseEntity.ok(availablePromotions);
// }