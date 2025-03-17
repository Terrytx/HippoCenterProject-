package tw.com.hippo_center_backend.h3controller.b2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T26PromotionBean;
import tw.com.hippo_center_backend.h2service.b2.PromotionService;
import tw.com.hippo_center_backend.h4dto.b2.PromotionDetailsDTO;
import tw.com.hippo_center_backend.h4dto.b2.PromotionSummaryDTO;

@RestController
@RequestMapping("/mowmow/admin/promotions")
public class AdminPromotionController {

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

    // ✅ 查詢所有促銷活動
    @GetMapping("/all")
    public ResponseEntity<List<T26PromotionBean>> getAllPromotions() {
        try {
            List<T26PromotionBean> promotions = promotionService.getAllPromotions();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ 新增促銷活動
    @PostMapping("/add")
    public ResponseEntity<String> createPromotion(@RequestBody T26PromotionBean promotion) {
        try {
            promotionService.createPromotion(promotion);
            return ResponseEntity.ok("促銷活動已成功新增！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("新增促銷活動失敗：" + e.getMessage());
        }
    }

    // ✅ 發送促銷券給所有會員
    @PostMapping("/send/{promotionCode}")
    public ResponseEntity<String> sendPromotionToAllMembers(@PathVariable String promotionCode) {
        try {
            promotionService.sendPromotionToAllMembers(promotionCode);
            return ResponseEntity.ok("✅ 促銷券已成功發送給所有會員！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ 發送促銷券失敗：" + e.getMessage());
        }
    }

    // ✅ 檢查並更新過期促銷券 (新增的 API)
    @PostMapping("/check-expired")
    public ResponseEntity<String> checkAndExpirePromotions() {
        try {
            promotionService.expireOldPromotions();
            return ResponseEntity.ok("過期促銷券已成功檢查並更新！");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 檢查促銷券失敗：" + e.getMessage());
        }
    }

}
