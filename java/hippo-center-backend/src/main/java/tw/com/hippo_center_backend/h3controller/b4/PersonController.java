package tw.com.hippo_center_backend.h3controller.b4;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h2service.b4.PersonService;
import tw.com.hippo_center_backend.h6jwt.JsonWebTokenUtility;

@RestController
public class PersonController {
	@Autowired
	private PersonService personService;

	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility; // 引入 JWT 工具類

	@GetMapping("/secure/person")
	public String findById(@RequestHeader("Authorization") String authorizationHeader) {
		// 確保標頭中有 JWT Token
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return new JSONObject().put("message", "Authorization 標頭錯誤或缺少 Token").toString();
		}

		// 從 Authorization 標頭獲取 JWT
		String token = authorizationHeader.substring(7); // 移除 "Bearer " 前綴
		System.out.println("token:" + token);
		// 直接使用 JWT 工具進行驗證和解析
		String userData = jsonWebTokenUtility.validateToken(token); // 解碼並驗證 JWT
		System.out.println("userData:" + userData);
		if (userData == null) {
			return new JSONObject().put("message", "未登入或Token無效").toString();
		}

		// 假設返回的 userData 是一個 JSON 字符串，這裡我們直接解析它
		try {
			JSONObject jsonObject = new JSONObject(userData);
			Integer memberId = jsonObject.optInt("memberId", -1); // 解析 memberId
			System.out.println("解析memberId" + memberId);
			if (memberId == -1) {
				return new JSONObject().put("message", "無效的 memberId").toString();
			}

			// 使用 memberId 查詢資料庫中的會員資料
			T41MemberBean member = personService.findById(memberId);
			if (member == null) {
				return new JSONObject().put("message", "查無此會員資料").toString();
			}
			;

			JSONObject responseJson = new JSONObject();
			JSONArray array = new JSONArray();

			// 如果查詢到會員資料，將其資料放入 responseJson 中
			System.out.println("controller:member != null");
			JSONObject item = new JSONObject()
					.put("memberId", member.getMemberId())
					.put("account", member.getAccount())
					.put("name", member.getName())
					.put("gender", member.getGender())
					.put("phone", member.getPhone())
					.put("address", member.getAddress());
			if (member.getBirthday() != null) {
				item.put("birthday", member.getBirthday().toString());
			} else {
				item.put("birthday", JSONObject.NULL); // 可以設置為 NULL 或其他表示生日為空的值
			}
			array.put(item);
			System.out.println(item);
			System.out.println(array);
			responseJson = responseJson.put("list", array);
			System.out.println("controller" + responseJson);
			return responseJson.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("message", "解析 Token 出錯").toString();
		}
	}

	// 修改個人資料
	@PostMapping("/secure/person")
	public String updateMember(@RequestHeader("Authorization") String authorizationHeader, @RequestBody String entity) {
		// 確保標頭中有 JWT Token
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return new JSONObject().put("message", "Authorization 標頭錯誤或缺少 Token").toString();
		}

		// 從 Authorization 標頭獲取 JWT
		String token = authorizationHeader.substring(7); // 移除 "Bearer " 前綴

		// 直接使用 JWT 工具進行驗證和解析
		String userData = jsonWebTokenUtility.validateToken(token); // 解碼並驗證 JWT
		if (userData == null) {
			return new JSONObject().put("message", "未登入或Token無效").toString();
		}

		// 假設返回的 userData 是一個 JSON 字符串，這裡我們直接解析它
		try {
			JSONObject jsonObject = new JSONObject(userData);
			Integer memberId = jsonObject.optInt("memberId", -1); // 解析 memberId
			if (memberId == -1) {
				return new JSONObject().put("message", "無效的 memberId").toString();
			}

			JSONObject responseJson = new JSONObject();
			T41MemberBean tempMemberBean = new T41MemberBean();

			// 接收資料
			System.out.println("接收資料");
			JSONObject obj = new JSONObject(entity);

			String name = obj.isNull("name") ? null : obj.getString("name");
			String phone = obj.isNull("phone") ? null : obj.getString("phone");
			String address = obj.isNull("address") ? null : obj.getString("address");
			String gender = obj.isNull("gender") ? null : obj.getString("gender");
			// 處理生日字段，避免空字符串引發錯誤
			String birthdayString = obj.isNull("birthday") ? null : obj.getString("birthday");
			LocalDate birthday = null;

			if (birthdayString != null && !birthdayString.isEmpty()) {
				try {
					birthday = LocalDate.parse(birthdayString); // 嘗試解析日期
				} catch (DateTimeParseException e) {
					System.out.println("無效的日期格式: " + birthdayString); // 如果格式錯誤，記錄錯誤
				}
			}

			T41MemberBean existingMember = personService.findById(memberId);
			// 將參數放入tempMemBean
			tempMemberBean.setMemberId(existingMember.getMemberId()); // 保持原有的 memberId
			tempMemberBean.setName(name);
			tempMemberBean.setPhone(phone);
			tempMemberBean.setAddress(address);
			tempMemberBean.setGender(gender);
			tempMemberBean.setBirthday(birthday);
			tempMemberBean.setMemberType(tempMemberBean.getMemberType());
			tempMemberBean.setModifyDate(java.time.LocalDate.now());
			tempMemberBean.setCreateDate(existingMember.getCreateDate()); // 保持原有的
			tempMemberBean.setAccount(existingMember.getAccount()); // 保持原有的
			tempMemberBean.setPassword(existingMember.getPassword()); // 保持原有的

			System.out.println("tempMemberBean" + tempMemberBean);

			// 呼叫Model
			T41MemberBean bean = personService.updateMember(tempMemberBean);

			// 根據Model執行結果決定要呼叫的View
			if (bean == null) {
				responseJson.put("success", false);
				responseJson.put("message", "修改失敗");
			} else {
				responseJson.put("success", true);
				responseJson.put("message", "修改成功");
			}
			return responseJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("message", "解析 Token 出錯").toString();
		}

	}
}
