package tw.com.hippo_center_backend.h2service.b4;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;

@Service
public class AdminMemberService {
	@Autowired
	private T41MemberRepository memberRepository;
	
	// 查詢會員資料 (單一查詢)
    public T41MemberBean findById(Integer memberId) {
        System.out.println("開始搜尋");
        if (memberId != null) {
            System.out.println("memberId=" + memberId);
            Optional<T41MemberBean> optional = memberRepository.findById(memberId);
            System.out.println(optional);
            if (optional.isPresent()) {
                System.out.println("optional存在" + optional);
                return optional.get();
            } else {
                System.out.println("optional不存在");
            }
        } else {
            System.out.println("ID = null，找不到");
        }
        return null;
    }
	
    
    // 分頁查詢，返回指定頁數和每頁大小的資料
    public Page<T41MemberBean> getMembers(int page, int size, Sort sort) {
        // 創建分頁請求，這裡設置了當前頁數(page)和每頁大小(size)
        Pageable pageable = PageRequest.of(page, size, sort);
        System.out.println("page"+page+"，size"+size+", sort"+sort);
        // 使用findAll方法查詢並返回分頁結果
        return memberRepository.findAll(pageable);
    }
    
    // 分頁查詢管理員，返回指定頁數和每頁大小的資料
    public Page<T41MemberBean> getAdmins(int page, int size, Sort sort) {
        // 創建分頁請求，這裡設置了當前頁數(page)和每頁大小(size)
        Pageable pageable = PageRequest.of(page, size, sort);
        System.out.println("page"+page+"，size"+size+", sort"+sort);
        // 使用findAll方法查詢並返回分頁結果
        return memberRepository.searchAdmin(pageable);
    }
    
    // 分頁查詢管理員，返回指定頁數和每頁大小的資料
    public Page<T41MemberBean> getGeneral(int page, int size, Sort sort) {
        // 創建分頁請求，這裡設置了當前頁數(page)和每頁大小(size)
        Pageable pageable = PageRequest.of(page, size, sort);
        System.out.println("page"+page+"，size"+size+", sort"+sort);
        // 使用findAll方法查詢並返回分頁結果
        return memberRepository.searchGeneral(pageable);
    }
    
    // 查詢會員資料 (模糊查詢)
    public List<T41MemberBean> searchByKeyword(String keyword) {
        // 模糊查詢所有會員資料
        return memberRepository.searchByKeywordNative(keyword);
    }
    
    // 修改會員資料
    public T41MemberBean updateMember(T41MemberBean tempBean) {
        if (tempBean != null && tempBean.getMemberId() != null) {
            if (memberRepository.existsById(tempBean.getMemberId())) {
                return memberRepository.save(tempBean);
            }
        }
        return null;
    }

}
