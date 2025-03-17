package tw.com.hippo_center_backend.h2service.b4;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;

@Service
public class MemberService {
	@Autowired
	private T41MemberRepository memberRepository;

	@Transactional
	// 登入功能
	// 登入>檢查輸入>正常 or 有誤，有誤>null；正常>查詢account，有>檢查密碼 or
	// 沒有>null；查詢密碼，正確>登入(回傳會員資料)；錯誤>null
	public T41MemberBean login(T41MemberBean tempBean) {
		// 取出參數
		String account = tempBean.getAccount();
		String password = tempBean.getPassword();
		System.out.println("Service的account" + account + "," + "password" + password);
		// 輸入檢查
		if (account != null && account.length() != 0 && password != null && password.length() != 0) {
			System.out.println("有輸入資料");
			// optional=尋找使用者輸入的帳號
			Optional<T41MemberBean> optional = memberRepository.findByAccount(account);
			List<T41MemberBean> newOpt = memberRepository.findAll();
			System.out.println("newOpt" + newOpt);
			// 如果【尋找使用者輸入的帳號】= true = 有找到
			if (optional.isPresent()) {
				System.out.println("帳號存在");
				// 把Bean的東西都拿出來，把Bean的密碼拿出來
				T41MemberBean Bean = optional.get();
				String pass = Bean.getPassword();
				// Bean的密碼跟輸入密碼去比對
				if (pass.equals(password)) {
					System.out.println("密碼正確");
					return Bean;
				} else {
					System.out.println("密碼錯誤");
				}
			} else {
				System.out.println("帳號不存在");
			}
		}
		System.out.println("登入失敗" + account + "," + password);
		return null;
	}

	// 註冊功能
	// 檢查是否輸入資料(帳號、密碼、姓名)>正常 or 沒輸入null；正常>檢查帳號是否存在>存在null or 沒存在>新增
	public T41MemberBean register(T41MemberBean tempBean) {
		String account = tempBean.getAccount();
		String password = tempBean.getPassword();
		String name = tempBean.getName();
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
				bean.setCreateDate(java.time.LocalDate.now());
				bean.setModifyDate(java.time.LocalDate.now());
				System.out.println("時間創建");
				return memberRepository.save(bean);
			}
		}
		System.out.println("帳號已存在，註冊失敗");
		return null;
	}

}
