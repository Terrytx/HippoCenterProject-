package tw.com.hippo_center_backend.h2service.b4;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;

@Service
public class PersonService {
	@Autowired
	private T41MemberRepository memberRepository;
	
	//查詢會員資料(搜尋)
	public T41MemberBean findById(Integer memberId) {
		System.out.println("開始搜尋");
		if (memberId != null) {
			System.out.println("memberId="+memberId);
			Optional<T41MemberBean> optional = memberRepository.findById(memberId);
			System.out.println(optional);
			if (optional.isPresent()) {
				System.out.println("optional存在"+optional);
				return optional.get();
			}else {
				System.out.println("optional不存在");
			}
		}else {
			System.out.println("ID = null，找不到");
		}
		return null;
	}
    
	//修改會員資料
	public T41MemberBean updateMember(T41MemberBean tempBean) {
		if (tempBean != null && tempBean.getMemberId() != null) {
			if (memberRepository.existsById(tempBean.getMemberId())) {
				return memberRepository.save(tempBean);
			}
		}
		return null;
	}
	
}
