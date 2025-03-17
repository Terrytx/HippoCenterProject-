package tw.com.hippo_center_backend.h2service.b2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T26PromotionBean;
import tw.com.hippo_center_backend.h0bean.T27PromotionMemberBean;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T26PromotionRepository;
import tw.com.hippo_center_backend.h1repository.T27PromotionMemberRepository;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;
import tw.com.hippo_center_backend.h4dto.b2.PromotionDetailsDTO;
import tw.com.hippo_center_backend.h4dto.b2.PromotionSummaryDTO;

@Service
@Transactional
public class PromotionService {

    @Autowired
    private T26PromotionRepository promotionRepository;

    @Autowired
    private T27PromotionMemberRepository promotionMemberRepository;

    @Autowired
    private T41MemberRepository memberRepository;

    // 發送促銷券給會員
    public void sendPromotionToMember(T41MemberBean member) {
        T26PromotionBean currentMonthPromotion = getCurrentMonthPromotion();
        sendPromotion(member, currentMonthPromotion, "MONTHLY");

        if (isBirthdayMonth(member)) {
            T26PromotionBean birthdayPromotion = getBirthdayPromotion();
            sendPromotion(member, birthdayPromotion, "BIRTHDAY");
        }
    }

    private void sendPromotion(T41MemberBean member, T26PromotionBean promotion, String promotionType) {
        boolean hasValidPromotion = promotionMemberRepository.findByMemberAndPromotion(member, promotion)
                .stream()
                .anyMatch(promo -> promo.getPromotionStatus() == T27PromotionMemberBean.PromotionStatus.NEW);

        if (!hasValidPromotion) {
            T27PromotionMemberBean promotionMember = new T27PromotionMemberBean(member, promotion, promotionType);
            promotionMemberRepository.save(promotionMember);
            System.out.println("✅ 已成功發送促銷券：" + promotion.getPromotionCode() + " 給會員：" + member.getName());
        } else {
            System.out.println("⚠️ 會員已擁有有效的促銷券：" + promotion.getPromotionCode());
        }
    }

    private T26PromotionBean getCurrentMonthPromotion() {
        String promotionCode = LocalDate.now().getYear() + String.format("%02d", LocalDate.now().getMonthValue());
        return promotionRepository.findByPromotionCode(promotionCode)
                .orElseThrow(() -> new IllegalStateException("當月促銷活動不存在"));
    }

    private T26PromotionBean getBirthdayPromotion() {
        return promotionRepository.findByPromotionCode("BIRTHDAY")
                .orElseThrow(() -> new IllegalStateException("生日促銷活動不存在"));
    }

    private boolean isBirthdayMonth(T41MemberBean member) {
        return member.getBirthday() != null && member.getBirthday().getMonthValue() == LocalDate.now().getMonthValue();
    }

    @Transactional
    public void expireOldPromotions() {
        List<T27PromotionMemberBean> promotions = promotionMemberRepository
                .findByPromotionStatus(T27PromotionMemberBean.PromotionStatus.NEW);

        promotions.stream()
                .filter(promo -> promo.getEndDate() != null && promo.getEndDate().isBefore(LocalDateTime.now()))
                .forEach(promo -> {
                    promo.setPromotionStatus(T27PromotionMemberBean.PromotionStatus.EXPIRED);
                    promotionMemberRepository.save(promo);
                });
    }

    public void markPromotionAsUsed(T27PromotionMemberBean promotion) {
        if (promotion.getPromotionStatus() != T27PromotionMemberBean.PromotionStatus.NEW) {
            throw new IllegalArgumentException("促銷券已使用或無效");
        }
        promotion.setPromotionStatus(T27PromotionMemberBean.PromotionStatus.USED);
        promotion.setUsedAt(LocalDateTime.now());
        promotionMemberRepository.save(promotion);
        System.out.println("✅ 促銷券已成功使用：" + promotion.getPromotionMemberId());
    }

    // 查詢會員的促銷券 (回傳 DTO)
    public List<PromotionDetailsDTO> getPromotionsByMemberDetails(Integer memberId) {
        T41MemberBean member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("會員不存在"));

        return promotionMemberRepository.findByMember(member).stream()
                .filter(promo -> promo.getPromotionStatus() == T27PromotionMemberBean.PromotionStatus.NEW ||
                        promo.getPromotionStatus() == T27PromotionMemberBean.PromotionStatus.ACTIVE) // ✅ 過濾掉 USED 和
                                                                                                     // EXPIRED
                .map(promotion -> new PromotionDetailsDTO(
                        promotion.getPromotionMemberId(),
                        promotion.getPromotion().getTitle(),
                        promotion.getPromotion().getPromotionCode(),
                        promotion.getPromotion().getDescription(),
                        promotion.getPromotion().getDiscountRate(),
                        promotion.getPromotionStatus().name(),
                        promotion.getStartDate(),
                        promotion.getEndDate()))
                .toList();
    }

    public List<PromotionDetailsDTO> usePromotionById(String promotionMemberId) {
        T27PromotionMemberBean promotion = validatePromotion(promotionMemberId);
        markPromotionAsUsed(promotion); // ✅ 標記為已使用

        // ✅ 找到會員 ID，重新獲取最新的促銷券
        Integer memberId = promotion.getMember().getMemberId();
        return getPromotionsByMemberDetails(memberId);
    }

    // 查詢會員的可用促銷券 (回傳 DTO)
    public List<PromotionSummaryDTO> getAvailablePromotionsForCheckout(Integer memberId) {
        T41MemberBean member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("會員不存在"));
        return promotionMemberRepository
                .findByMemberAndPromotionStatus(member, T27PromotionMemberBean.PromotionStatus.NEW)
                .stream()
                .map(promotion -> {
                    PromotionSummaryDTO dto = new PromotionSummaryDTO();
                    dto.setTitle(promotion.getPromotion().getTitle());
                    dto.setDiscountRate(promotion.getPromotion().getDiscountRate());
                    return dto;
                }).toList();
    }

    public T27PromotionMemberBean validatePromotion(String promotionMemberId) {
        System.out.println("🔍 [DEBUG] 準備驗證促銷券: " + promotionMemberId);

        // ✅ 確保 `promotionMemberId` 沒有 `[ ]` 符號
        String cleanedPromotionId = promotionMemberId.replaceAll("[\\[\\]]", "").trim();
        System.out.println("🔍 [DEBUG] 修正後的 `promotionMemberId`: " + cleanedPromotionId);

        return promotionMemberRepository.findById(cleanedPromotionId)
                .map(promo -> {
                    System.out.println("🔍 促銷券找到: " + promo.getPromotionMemberId());
                    System.out.println("📅 狀態: " + promo.getPromotionStatus());
                    System.out.println("⏳ 結束時間: " + promo.getEndDate());

                    boolean isValid = (promo.getPromotionStatus() == T27PromotionMemberBean.PromotionStatus.NEW ||
                            promo.getPromotionStatus() == T27PromotionMemberBean.PromotionStatus.ACTIVE) &&
                            (promo.getEndDate() == null || promo.getEndDate().isAfter(LocalDateTime.now()));

                    System.out.println("✅ 是否有效: " + isValid);
                    if (!isValid) {
                        throw new RuntimeException("促銷券無效或已過期");
                    }

                    return promo;
                })
                .orElseThrow(() -> {
                    System.err.println("❌ 查無促銷券: " + cleanedPromotionId);
                    return new RuntimeException("促銷券無效或已過期");
                });
    }

    // ✅ 查詢所有促銷活動，按「開始時間」排序（新的在上）
    public List<T26PromotionBean> getAllPromotions() {
        return promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
    }

    // ✅ 新增促銷活動
    public void createPromotion(T26PromotionBean promotion) {
        if (promotion.getPromotionCode().length() < 5 || promotion.getPromotionCode().length() > 7) {
            throw new IllegalArgumentException("促銷碼長度必須介於 5 到 7 碼之間！");
        }

        if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
            throw new IllegalArgumentException("開始時間與結束時間不得為空");
        }

        if (promotion.getDiscountRate() <= 0 || promotion.getDiscountRate() > 1) {
            throw new IllegalArgumentException("折扣比例需介於 0 ~ 1 之間");
        }

        if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new IllegalArgumentException("結束時間不得早於開始時間");
        }

        if (promotion.getDescription() == null || promotion.getDescription().isEmpty()) {
            throw new IllegalArgumentException("促銷描述不得為空");
        }

        promotionRepository.save(promotion);
        System.out.println("✅ 促銷活動已成功新增：" + promotion.getPromotionCode());
    }

    // ✅ 發送促銷券給所有會員
    public void sendPromotionToAllMembers(String promotionCode) {
        // 1️⃣ 查找促銷活動（T26）
        T26PromotionBean promotion = promotionRepository.findByPromotionCode(promotionCode)
                .orElseThrow(() -> new IllegalArgumentException("找不到促銷活動：" + promotionCode));

        // 2️⃣ 查詢所有會員（T41）
        List<T41MemberBean> allMembers = memberRepository.findAll();
        if (allMembers.isEmpty()) {
            throw new IllegalStateException("沒有會員，無法發送促銷券！");
        }

        // 3️⃣ 為每個會員建立促銷券（T27）
        for (T41MemberBean member : allMembers) {
            // 生成促銷券 ID
            String promotionMemberId = T27PromotionMemberBean.generatePromotionMemberId(member.getMemberId(),
                    promotionCode);

            // 建立促銷券
            T27PromotionMemberBean promotionMember = new T27PromotionMemberBean(member, promotion, "GENERAL");
            promotionMember.setPromotionMemberId(promotionMemberId);

            // 儲存促銷券
            promotionMemberRepository.save(promotionMember);
            System.out.println("✅ 已發送促銷券：" + promotionMemberId + " 給會員：" + member.getName());
        }
    }

}
