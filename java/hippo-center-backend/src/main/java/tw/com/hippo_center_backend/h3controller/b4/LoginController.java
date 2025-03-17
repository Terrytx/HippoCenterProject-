package tw.com.hippo_center_backend.h3controller.b4;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h2service.b4.MemberService;
import tw.com.hippo_center_backend.h6jwt.JsonWebTokenUtility;

@RestController
public class LoginController {
	@Autowired
	private MemberService myMemberService;

	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;

	@PostMapping("/secure/login")
	public String login(HttpSession session, @RequestBody String entity) {
		System.out.println("START");
		JSONObject responseJson = new JSONObject();
		T41MemberBean tempMemberBean = new T41MemberBean();

		// 接收資料
		System.out.println("接收資料");
		JSONObject obj = new JSONObject(entity);
		System.out.println(obj);
		String account = obj.isNull("account") ? null : obj.getString("account");
		String password = obj.isNull("password") ? null : obj.getString("password");
		System.out.println("account" + account);
		// 驗證資料
		if (account == null || account.length() == 0 || password == null || password.length() == 0) {
			responseJson.put("success", false);
			responseJson.put("message", "請輸入帳號/密碼");
			return responseJson.toString();
		}

		// 將參數放入tempMemBean
		tempMemberBean.setAccount(account);
		tempMemberBean.setPassword(password);

		// 呼叫Model
		T41MemberBean bean = myMemberService.login(tempMemberBean);

		// 根據Model執行結果決定要呼叫的View
		if (bean == null) {
			responseJson.put("success", false);
			responseJson.put("message", "登入失敗");
		} else {
			responseJson.put("success", true);
			responseJson.put("message", "登入成功");
			session.setAttribute("memberId", bean.getMemberId());  // 存入 session
			JSONObject user = new JSONObject()
					.put("memberId", bean.getMemberId())
					.put("account", bean.getAccount());
			String token = jsonWebTokenUtility.createToken(user.toString());
			responseJson.put("token", token);
			responseJson.put("account", bean.getAccount());
		}
		return responseJson.toString();
	}
}
