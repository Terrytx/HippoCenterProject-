
package tw.com.hippo_center_backend.h3controller.b4;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T44MyLoveBean;
import tw.com.hippo_center_backend.h2service.b4.MyLoveService;
import tw.com.hippo_center_backend.h4dto.b4.MyLoveAddAndRemoveDTO;
import tw.com.hippo_center_backend.h6jwt.JsonWebTokenUtility;

@RestController
public class MyLoveController {
    @Autowired
    private MyLoveService myLoveService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    @GetMapping("/secure/mylove/{myloveType}")
    public String findMyLove(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("myloveType") Character myloveType) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new JSONObject().put("message", "Authorization 標頭錯誤或缺少 Token").toString();
        }

        String token = authorizationHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            return new JSONObject().put("message", "未登入或Token無效").toString();
        }

        try {
            JSONObject jsonObject = new JSONObject(userData);
            Integer memberId = jsonObject.optInt("memberId", -1);
            if (memberId == -1) {
                return new JSONObject().put("message", "無效的 memberId").toString();
            }

            List<T44MyLoveBean> myLoveList = myLoveService.getMyLoveByMemberIdAndType(memberId, myloveType);
            if (myLoveList.isEmpty()) {
                return new JSONObject().put("message", "查無此會員的最愛資料").toString();
            }

            JSONObject responseJson = new JSONObject();
            JSONArray array = new JSONArray();

            for (T44MyLoveBean myLove : myLoveList) {
                JSONObject item = new JSONObject()
                        .put("myloveNum", myLove.getMyloveNum())
                        .put("myloveId", myLove.getMyloveId())
                        .put("myloveType", myLove.getMyloveType())
                        .put("name", myLove.getName())
                        .put("price", myLove.getPrice())
                        .put("imageUrl", myLove.getImageUrl())
                        .put("eventStartDate", myLove.getEventStartDate())
                        .put("eventEndDate", myLove.getEventEndDate());

                array.put(item);
            }

            responseJson.put("list", array);
            return responseJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("message", "解析 Token 出錯").toString();
        }
    }

    // 使用 POST 來新增或移除最愛
    @PostMapping("/secure/mylove/toggle/{myloveType}")
    public String toggleMyLove(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("myloveType") Character myloveType,
            @RequestBody MyLoveAddAndRemoveDTO request) {
        Integer myloveId = request.getMyloveId();

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

        try {
            JSONObject jsonObject = new JSONObject(userData);
            Integer memberId = jsonObject.optInt("memberId", -1); // 解析 memberId
            if (memberId == -1) {
                return new JSONObject().put("message", "無效的 memberId").toString();
            }
            return myLoveService.toggleMyLove(memberId, myloveType, myloveId);

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("message", "新增最愛資料時出錯").toString();
        }
    }

}