package tw.com.hippo_center_backend.h2service.b4;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;

@Service
public class AdminLoginService {
	@Autowired
	private T41MemberRepository memberRepository;

	@Transactional
	public T41MemberBean login(T41MemberBean tempBean) {
	    String account = tempBean.getAccount();
	    String password = tempBean.getPassword();
	    System.out.println("Service的account" + account + "," + "password" + password);

	    if (account != null && account.length() != 0 && password != null && password.length() != 0) {
	        Optional<T41MemberBean> optional = memberRepository.findByAccount(account);
	        
	        if (optional.isPresent()) {
	            T41MemberBean bean = optional.get();
	            String storedPassword = bean.getPassword();
	            
	            // 比對密碼
	            if (storedPassword.equals(password)) {
	                // 判斷memberType是否為"Admin"
	                if ("admin".equals(bean.getMemberType())) {
	                    System.out.println("管理員登入成功");
	                    return bean;
	                } else {
	                    System.out.println("該使用者不是管理員");
	                }
	            } else {
	                System.out.println("密碼錯誤");
	            }
	        } else {
	            System.out.println("帳號不存在");
	        }
	    }
	    System.out.println("登入失敗");
	    return null;
	}
	
	// 註冊功能
	// 檢查是否輸入資料(帳號、密碼、姓名)>正常 or 沒輸入null；正常>檢查帳號是否存在>存在null or 沒存在>新增
	public T41MemberBean register(T41MemberBean tempBean) {
		String account = tempBean.getAccount();
		String password = tempBean.getPassword();
		String name = tempBean.getName();
		String address = tempBean.getAddress();
		LocalDate birthday = tempBean.getBirthday();
		String phone = tempBean.getPhone();
		// 檢查是否輸入資料(帳號、密碼、姓名)
		if (account != null && account.length() != 0 && password != null && password.length() != 0 && name != null
				&& name.length() != 0) {
			System.out.println("有輸入資料");
			Optional<T41MemberBean> optional = memberRepository.findByAccount(account);
			if (optional.isEmpty()) {
				System.out.println("帳號不存在，可以註冊");
				T41MemberBean bean = new T41MemberBean();
				bean.setAccount(account);
				System.out.println("輸入帳號" + account);
				bean.setPassword(password);
				System.out.println("輸入密碼" + password);
				bean.setName(name);
				System.out.println("輸入名字" + name);
				bean.setAddress(address);
				bean.setBirthday(birthday);
				bean.setPhone(phone);
				bean.setMemberType("admin");
				bean.setCreateDate(java.time.LocalDate.now());
				bean.setModifyDate(java.time.LocalDate.now());
				System.out.println("創建"+bean);
				return memberRepository.save(bean);
			}
		}
		System.out.println("帳號已存在，註冊失敗");
		return null;
	}
}
