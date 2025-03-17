//package tw.com.hippo_center_backend.h3controller.b4;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import tw.com.hippo_center_backend.h0bean.T41MemberBean;
//import tw.com.hippo_center_backend.h2service.b4.AdminMemberService;
//
//@RestController
//public class AdminMemberController2 {
//	@Autowired
//	private AdminMemberService adminMemberService;
//	  
//	    // 分頁查詢會員資料
//	    @GetMapping("page/members")
//	    public String findByPage(
//	            @RequestParam(defaultValue = "0") int page,  // 默認頁數為0
//	            @RequestParam(defaultValue = "12") int size) {  // 默認每頁顯示15筆資料
//	        
//	        // 使用Service層進行分頁查詢
//	        Page<T41MemberBean> membersPage = adminMemberService.getMembers(page, size);
//
//	        // 轉換會員資料為JSON格式
//	        JSONArray allMembers = new JSONArray();
//	        for (T41MemberBean member : membersPage.getContent()) {
//	            JSONObject item = new JSONObject()
//	                    .put("memberId", member.getMemberId())
//	                    .put("account", member.getAccount())
//	                    .put("name", member.getName())
//	                    .put("gender", member.getGender())
//	                    .put("phone", member.getPhone())
//	                    .put("address", member.getAddress())
//	                    .put("memberType", member.getMemberType())
//	                    .put("createDate", member.getCreateDate())
//	                    .put("modifyDate", member.getModifyDate())
//	                    .put("birthday", member.getBirthday() != null ? member.getBirthday().toString() : JSONObject.NULL);
//	            allMembers.put(item);
//	        }
//
//	        // 返回包含分頁資料的 JSON
//	        JSONObject responseJson = new JSONObject();
//	        responseJson.put("list", allMembers);
//	        responseJson.put("totalPages", membersPage.getTotalPages());  // 返回總頁數
//	        responseJson.put("currentPage", membersPage.getNumber());  // 返回當前頁數
//	        responseJson.put("totalElements", membersPage.getTotalElements());  // 返回總資料數
//
//	        return responseJson.toString();
//	    }
//	 	
//	    // 管理員模糊查詢會員資料
//	    @GetMapping("/admin/member/search")
//	    public List<T41MemberBean> search(@RequestParam String keyword) {
//	        return adminMemberService.searchByKeyword(keyword);
//	    }
//	    
//	    // 管理員修改會員資料
//	    @PostMapping("/admin/member/update")
//	    public String updateMember(@RequestBody String entity) {
//
//	            // 接收會員ID及修改資料
//	            JSONObject obj = new JSONObject(entity);
//	            Integer memberId = obj.getInt("memberId");  // 會員ID
//	            String name = obj.isNull("name") ? null : obj.getString("name");
//	            String account = obj.isNull("account") ? null : obj.getString("account");
//	            String phone = obj.isNull("phone") ? null : obj.getString("phone");
//	            String address = obj.isNull("address") ? null : obj.getString("address");
//	            String gender = obj.isNull("gender") ? null : obj.getString("gender");
//	            String memberType = obj.isNull("memberType") ? null : obj.getString("memberType");
//	            LocalDate birthday = obj.isNull("birthday") ? null : LocalDate.parse(obj.getString("birthday"));
//
//	            T41MemberBean member = adminMemberService.findById(memberId);
//	            if (member == null) {
//	                return new JSONObject().put("message", "查無此會員資料").toString();
//	            }
//
//	            // 更新會員資料
//	            member.setAccount(account);
//	            member.setName(name);
//	            member.setPhone(phone);
//	            member.setAddress(address);
//	            member.setGender(gender);
//	            member.setMemberType(memberType);
//	            if (birthday != null) {
//	                member.setBirthday(birthday);  // 使用接收到的 LocalDate 類型 birthday
//	            }
//	            member.setModifyDate(LocalDate.now());
//
//	            T41MemberBean updatedMember = adminMemberService.updateMember(member);
//
//	            JSONObject responseJson = new JSONObject();
//	            if (updatedMember == null) {
//	                responseJson.put("success", false);
//	                responseJson.put("message", "修改失敗");
//	            } else {
//	                responseJson.put("success", true);
//	                responseJson.put("message", "修改成功");
//	            }
//
//	            return responseJson.toString(); 
//	    }
//	    
//	     
////	     //查詢所有會員資料
////		 @GetMapping("/members")
////		    public String findById() {
////		            
////		            // 管理員查詢所有會員
////		            JSONArray allMembers = new JSONArray();
////		            List<T41MemberBean> members = adminMemberService.getAllMembers();  // 查詢所有會員
////		            for (T41MemberBean member : members) {
////		                JSONObject item = new JSONObject()
////		                        .put("memberId", member.getMemberId())
////		                        .put("account", member.getAccount())
////		                        .put("name", member.getName())
////		                        .put("gender", member.getGender())
////		                        .put("phone", member.getPhone())
////		                        .put("address", member.getAddress())
////		                        .put("memberType", member.getMemberType())
////		                        .put("createDate", member.getCreateDate())
////		                        .put("modifyDate", member.getModifyDate())
////		                        .put("birthday", member.getBirthday() != null ? member.getBirthday().toString() : JSONObject.NULL);
////		                allMembers.put(item);
////		            }
//	//
////		            JSONObject responseJson = new JSONObject();
////		            responseJson.put("list", allMembers);
////		            return responseJson.toString();
////			    
////		    }
//	    
//	 // 管理員根據會員ID查詢會員資料
////	    @GetMapping("/member/{id}")
////	    public String getMemberById(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
////	        // 確保標頭中有 JWT Token
////	        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
////	            return new JSONObject().put("message", "Authorization 標頭錯誤或缺少 Token").toString();
////	        }
////
////	        // 從 Authorization 標頭獲取 JWT
////	        String token = authorizationHeader.substring(7);  // 移除 "Bearer " 前綴
////
////	        // 驗證並解析 JWT
////	        String userData = jsonWebTokenUtility.validateToken(token);
////	        if (userData == null) {
////	            return new JSONObject().put("message", "未登入或Token無效").toString();
////	        }
////
////	        try {
////	            JSONObject jsonObject = new JSONObject(userData);
////	            String memberType = jsonObject.optString("memberType", "");
////	            if (!"admin".equals(memberType)) {
////	                return new JSONObject().put("message", "您無權限執行此操作").toString(); // 只有管理員可以操作
////	            }
////
////	            // 根據會員ID查詢會員資料
////	            T41MemberBean member = adminMemberService.findById(id);
////	            if (member == null) {
////	                return new JSONObject().put("message", "查無此會員資料").toString();
////	            }
////
////	            // 回傳會員資料
////	            JSONObject memberJson = new JSONObject()
////	                    .put("memberId", member.getMemberId())
////	                    .put("account", member.getAccount())
////	                    .put("name", member.getName())
////	                    .put("phone", member.getPhone())
////	                    .put("address", member.getAddress())
////	                    .put("gender", member.getGender())
////	                    .put("birthday", member.getBirthday() != null ? member.getBirthday().toString() : null);
////
////	            JSONObject responseJson = new JSONObject();
////	            responseJson.put("member", memberJson);
////
////	            return responseJson.toString();
////	        } catch (Exception e) {
////	            e.printStackTrace();
////	            return new JSONObject().put("message", "處理請求時發生錯誤").toString();
////	        }
////	    }
//
//	    
//	}
//
