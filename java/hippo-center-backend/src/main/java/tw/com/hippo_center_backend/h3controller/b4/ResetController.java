package tw.com.hippo_center_backend.h3controller.b4;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import tw.com.hippo_center_backend.h2service.b4.ResetService;
import tw.com.hippo_center_backend.h4dto.b4.ResetPasswordRequestDTO;

@RestController
@RequestMapping("/user")
public class ResetController {

    @Autowired
    private ResetService resetService;

    // 步驟 1: 用戶發送重設密碼郵件
    @PostMapping("/requestResetPassword")
    public ResponseEntity<Map<String, String>> requestResetPassword(
            @RequestBody ResetPasswordRequestDTO request,
            HttpServletRequest httpRequest) {

        // 設置 contextPath
        String contextPath = httpRequest.getContextPath();
        if (contextPath == null || contextPath.isEmpty()) {
            contextPath = "/";
        }

        try {
            // 呼叫 service 發送重設密碼郵件
            resetService.createResetPasswordToken(request.getEmail(), contextPath);

            return ResponseEntity.ok()
                    .body(Map.of("message", "重設密碼郵件已發送至您的信箱"));

        } catch (MessagingException e) {
            // 郵件發送失敗
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "郵件發送失敗：" + e.getMessage()));

        } catch (RuntimeException e) {
            // 其他錯誤（如帳號不存在等）
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            // 其他未預期的錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "系統發生未預期的錯誤"));
        }
    }

    // public ResponseEntity<String> requestResetPassword(@RequestBody
    // ResetPasswordRequestDTO request, HttpServletRequest httpRequest) {
    // // 如果 URL 沒有包含 contextPath，這裡可以設置為根目錄
    // String contextPath = httpRequest.getContextPath();
    // if (contextPath == null || contextPath.isEmpty()) {
    // contextPath = "/"; // 根目錄
    // }
    // try {
    // // 呼叫 service 發送重設密碼郵件
    // resetService.createResetPasswordToken(request.getEmail(), contextPath);
    // return ResponseEntity.ok("重設密碼郵件已發送至您的信箱");
    // } catch (RuntimeException e) {
    // return ResponseEntity.badRequest().body(e.getMessage()); // 若出現錯誤，回傳錯誤訊息
    // }
    // }

    // 步驟 2: 用戶進行重設密碼
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        try {
            // 使用 JSON 中的資料進行密碼重設
            resetService.resetPassword(request.getResetCode(), request.getNewPassword(), request.getUuid());
            return ResponseEntity.ok("密碼重設成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 如果失敗，返回錯誤訊息
        }
    }
}
