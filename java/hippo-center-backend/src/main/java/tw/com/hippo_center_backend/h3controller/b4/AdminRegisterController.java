package tw.com.hippo_center_backend.h3controller.b4;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h2service.b4.AdminLoginService;

@RestController
public class AdminRegisterController {
	@Autowired
	private AdminLoginService adminLoginService;


	@PostMapping("/admin/register")
	public String login(@RequestBody String entity) {
		System.out.println("START");
		JSONObject responseJson = new JSONObject();
		T41MemberBean tempMemberBean = new T41MemberBean();

		// 接收資料
		System.out.println("接收資料");
		JSONObject obj = new JSONObject(entity);
		System.out.println(obj);
		String account = obj.isNull("account") ? null : obj.getString("account");
		String password = obj.isNull("password") ? null : obj.getString("password");
		String name = obj.isNull("name") ? null : obj.getString("name");
		String address = obj.isNull("address") ? null : obj.getString("address");
		String phone = obj.isNull("phone") ? null : obj.getString("phone");
		String birthdayString = obj.isNull("birthday") ? null : obj.getString("birthday");
		LocalDate birthday = null;

		if (birthdayString != null && !birthdayString.isEmpty()) {
		    try {
		        birthday = LocalDate.parse(birthdayString);  // 嘗試解析日期
		    } catch (DateTimeParseException e) {
		        System.out.println("無效的日期格式: " + birthdayString);  // 如果格式錯誤，記錄錯誤
		    }
		}
		String memberType = obj.isNull("memberType") ? "admin" : obj.getString("memberType");  // 這裡確保傳入 "admin"

		// 驗證資料
		if (account == null || account.length() == 0 || password == null || password.length() == 0 || name == null || name.length() == 0) {
			System.out.println("沒有輸入資料");
			responseJson.put("success", false);
			responseJson.put("message", "請輸入資料");
			return responseJson.toString();
		}

		// 將參數放入tempMemBean
		System.out.println("放入資料");
		tempMemberBean.setAccount(account);
		tempMemberBean.setPassword(password);
		tempMemberBean.setName(name);
		tempMemberBean.setPhone(phone);
		tempMemberBean.setBirthday(birthday);
		tempMemberBean.setAddress(address);
		tempMemberBean.setMemberType(memberType);
		tempMemberBean.setCreateDate(LocalDate.now());
		tempMemberBean.setModifyDate(LocalDate.now());
		

		// 呼叫Model
		System.out.println("呼叫Model"+tempMemberBean);
		T41MemberBean bean = adminLoginService.register(tempMemberBean);

		// 根據Model執行結果決定要呼叫的View
		if (bean == null) {
			responseJson.put("success", false);
			responseJson.put("message", "註冊失敗");
		} else {
			responseJson.put("success", true);
			responseJson.put("message", "註冊成功");
		}
		return responseJson.toString();
	}
}
