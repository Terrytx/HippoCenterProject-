
package tw.com.hippo_center_backend.h2service.b4;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.hippo_center_backend.h0bean.T21ProductBean;
import tw.com.hippo_center_backend.h0bean.T31EventBean;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h0bean.T44MyLoveBean;
import tw.com.hippo_center_backend.h1repository.T21ProductRepository;
import tw.com.hippo_center_backend.h1repository.T31EventRepository;
import tw.com.hippo_center_backend.h1repository.T44MyLoveRepository;

@Service
public class MyLoveService {
    @Autowired
    private T44MyLoveRepository t44MyLoveRepository;

    @Autowired
    private T21ProductRepository t21ProductRepository;

    @Autowired
    private T31EventRepository t31EventRepository;

    // 直接返回 MyLoveBean 列表
    public List<T44MyLoveBean> getMyLoveByMemberIdAndType(Integer memberId, Character myloveType) {
        return t44MyLoveRepository.findByMember_MemberIdAndMyloveType(memberId, myloveType);
    }

    // 查詢或移除
    public String toggleMyLove(Integer memberId, Character myloveType, Integer myloveId) {
        Optional<T44MyLoveBean> existingMyLove = t44MyLoveRepository
                .findByMember_MemberIdAndMyloveTypeAndMyloveId(memberId, myloveType, myloveId);

        if (existingMyLove.isPresent()) {
            t44MyLoveRepository.delete(existingMyLove.get());
            return new JSONObject().put("message", "remove").toString();
        } else {
            T44MyLoveBean myLoveBean = new T44MyLoveBean();
            T41MemberBean member = new T41MemberBean();
            member.setMemberId(memberId);
            
            myLoveBean.setMember(member);
            myLoveBean.setMyloveType(myloveType);
            myLoveBean.setMyloveId(myloveId);
            myLoveBean.setAddLoveTime(LocalDateTime.now());

            // 根據類型查詢並設置額外信息
            if ('P' == myloveType) {
                Optional<T21ProductBean> product = t21ProductRepository.findById(myloveId);
                if (product.isPresent()) {
                    T21ProductBean p = product.get();
                    myLoveBean.setName(p.getProductName());
                    myLoveBean.setPrice(p.getPrice());
                    if (p.getImages() != null && !p.getImages().isEmpty()) {
                        myLoveBean.setImageUrl(p.getImages().get(0).getImageUrl());
                    }
                }
            } else if ('E' == myloveType) {
                Optional<T31EventBean> event = t31EventRepository.findById(myloveId);
                if (event.isPresent()) {
                    T31EventBean e = event.get();
                    myLoveBean.setName(e.getEventName());
                    myLoveBean.setPrice(e.getEventPrice());
                    myLoveBean.setEventStartDate(e.getEventStartDate());
                    myLoveBean.setEventEndDate(e.getEventEndDate());
                }
            }

            t44MyLoveRepository.save(myLoveBean);
            return new JSONObject().put("message", "add").toString();
        }
    }
}