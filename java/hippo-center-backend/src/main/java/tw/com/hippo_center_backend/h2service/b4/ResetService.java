package tw.com.hippo_center_backend.h2service.b4;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import tw.com.hippo_center_backend.h0bean.T41MemberBean;
import tw.com.hippo_center_backend.h0bean.T43ResetPasswordBean;
import tw.com.hippo_center_backend.h1repository.T41MemberRepository;
import tw.com.hippo_center_backend.h1repository.T43ResetPasswordRepository;

@Service
public class ResetService {

    @Autowired
    private T41MemberRepository memberRepository; // 查詢會員
    @Autowired
    private T43ResetPasswordRepository resetPasswordRepository; // 查詢重設密碼資料
    private final JavaMailSender mailSender; // 用來發送郵件

    public ResetService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 發送重設密碼的郵件，帶有 uuid && resetCode
    private String generateResetEmailLink(String contextPath, String uuid) {
        return "http://192.168.23.89:7788" + contextPath + "user/changePassword/" + uuid;
    }

    private void sendResetPasswordEmail(String contextPath, String email, String uuid, String resetCode)
            throws MessagingException {
        String resetUrl = generateResetEmailLink(contextPath, uuid);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("重設密碼");
        helper.setFrom("荷馬市藝術文化中心<hippoboy321@gmail.com>");

        String htmlContent = String.format("""
                <html>
                <head>
                    <style>
                        body {
                            font-family: '微軟正黑體', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }

                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #fff;
                        }

                        .header {
                            background-color: #000000;
                            color: #fff;
                            padding: 20px;
                            text-align: center;
                            border-radius: 5px;
                        }

                        .content {
                            background-color: white;
                            padding: 20px;
                            margin-top: 20px;
                            border-radius: 5px;
                            border: 1px solid #e0e0e0;
                        }

                        .reset-info {
                            margin: 20px 0;
                            padding: 20px;
                            background-color: #fff;
                            border-left: 4px solid #f8961e;
                            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        }

                        .reset-info p {
                            margin: 10px 0;
                        }

                        .reset-info strong {
                            color: #000000;
                            min-width: 100px;
                            display: inline-block;
                        }

                        .footer {
                            text-align: center;
                            margin-top: 20px;
                            padding: 20px;
                            color: #666;
                            border-top: 2px solid #f8961e;
                        }

                        .reset-button {
                            display: inline-block;
                            padding: 10px 20px;
                            background-color: #f8961e;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                            margin: 20px 0;
                        }

                        h1 {
                            margin: 0;
                            font-size: 24px;
                        }

                        p {
                            font-size: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>重設密碼通知</h1>
                        </div>
                        <div class="content">
                            <p>親愛的使用者您好，</p>
                            <p>我們收到了您重設密碼的請求，以下是重設密碼的相關資訊：</p>

                            <div class="reset-info">
                                <p><strong>重設驗證碼：</strong>%s</p>
                                <p><strong>有效期限：</strong>一小時</p>
                            </div>

                            <p>請點擊下方按鈕前往重設密碼頁面：</p>
                            <a href="%s" class="reset-button">重設密碼</a>

                            <p>如果您沒有要求重設密碼，請忽略此郵件。</p>
                        </div>
                        <div class="footer">
                            <p>此郵件為系統自動發送，請勿直接回覆</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                resetCode,
                resetUrl);

        helper.setText(htmlContent, true); // 設置 HTML 格式
        mailSender.send(message);
    }
    // private String generateResetEmailLink(String contextPath, String uuid) {
    // return "http://localhost:5173" + contextPath + "user/changePassword/" + uuid;
    // }
    //
    // private void sendResetPasswordEmail(String contextPath, String email, String
    // uuid, String resetCode) {
    // String resetUrl = generateResetEmailLink(contextPath, uuid);
    // String message ="重設驗證碼: " + resetCode + "\n"
    // + "請點擊以下路徑，並輸入驗證碼來重設密碼: \n"
    // + resetUrl + "\n\n"
    // + "有效時間為一小時。";
    //
    // SimpleMailMessage emailMessage = new SimpleMailMessage();
    // emailMessage.setTo(email);
    // emailMessage.setSubject("重設密碼");
    // emailMessage.setText(message);
    // emailMessage.setFrom("荷馬市藝術文化中心<hippoboy321@gmail.com>");
    // mailSender.send(emailMessage);
    // }

    // 生成 uuid && resetCode 並儲存
    public void createResetPasswordToken(String email, String contextPath) throws MessagingException {
        // 檢查帳號是否存在
        Optional<T41MemberBean> memberOptional = memberRepository.findByAccount(email);
        if (memberOptional.isEmpty()) {
            throw new RuntimeException("Account not found");
        }

        try {
            T41MemberBean member = memberOptional.get();
            String resetCode = String.format("%06d", new Random().nextInt(1000000)); // 生成 6 位數的 resetCode
            String uuid = UUID.randomUUID().toString(); // 生成重設碼

            // 儲存重設資料
            T43ResetPasswordBean resetPassword = new T43ResetPasswordBean();
            resetPassword.setMember(member); // 根據帳號設置會員
            resetPassword.setResetCode(resetCode);
            resetPassword.setUuid(uuid);
            resetPassword.setResetTime(LocalDateTime.now()); // 設定重設時間
            resetPassword.setResetOuttime(java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
            resetPassword.setResetState("PENDING"); // 初始狀態為 PENDING

            resetPasswordRepository.save(resetPassword); // 儲存重設資料

            // 發送重設郵件
            sendResetPasswordEmail(contextPath, email, uuid, resetCode);

        } catch (MessagingException e) {
            // 郵件發送失敗時的處理
            throw new MessagingException("Failed to send reset password email: " + e.getMessage(), e);
        } catch (Exception e) {
            // 其他異常的處理
            throw new RuntimeException("Error creating reset password token: " + e.getMessage(), e);
        }
    }
    // public void createResetPasswordToken(String email, String contextPath) {
    // // 檢查帳號是否存在
    // Optional<T41MemberBean> memberOptional =
    // memberRepository.findByAccount(email);
    // if (memberOptional.isEmpty()) {
    // throw new RuntimeException("Account not found");
    // }
    //
    // T41MemberBean member = memberOptional.get();
    // String resetCode = String.format("%06d", new Random().nextInt(1000000)); //
    // 生成 6 位數的 resetCode
    // String uuid = UUID.randomUUID().toString(); // 生成重設碼
    //
    // // 儲存重設資料
    // T43ResetPasswordBean resetPassword = new T43ResetPasswordBean();
    // resetPassword.setMember(member); // 根據帳號設置會員
    // resetPassword.setResetCode(resetCode);
    // resetPassword.setUuid(uuid);
    // resetPassword.setResetTime(LocalDateTime.now()); // 設定重設時間
    // resetPassword.setResetOuttime(java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
    // resetPassword.setResetState("PENDING"); // 初始狀態為 PENDING
    //
    // resetPasswordRepository.save(resetPassword); // 儲存重設資料
    //
    // sendResetPasswordEmail(contextPath, email, uuid, resetCode); // 發送重設郵件
    // }

    // 驗證重設碼是否有效
    public boolean validateResetCode(String resetCode, T43ResetPasswordBean resetPassword) {
        // 檢查是否過期
        if (resetPassword.getResetOuttime().before(new Date())) {
            System.err.println("已過期");
            return false; // 已過期
        }

        // 檢查 resetCode 是否一致
        if (!resetPassword.getResetCode().equals(resetCode)) {
            System.err.println("不一致");
            return false; // resetCode 不一致
        }

        // 檢查是否已經使用過
        if ("USED".equals(resetPassword.getResetState())) {
            System.err.println("已使用過");
            return false; // 已使用過
        }

        // 一切正常，重設碼有效
        System.err.println("驗證OK");
        return true;
    }

    // 重設密碼
    public void resetPassword(String resetCode, String newPassword, String uuid) {
        // 根據 uuid 查找重設資料
        Optional<T43ResetPasswordBean> resetPasswordOptional = resetPasswordRepository.findByUuid(uuid);
        if (resetPasswordOptional.isEmpty()) {
            throw new RuntimeException("無效的請求"); // 如果找不到資料，丟出異常
        }

        T43ResetPasswordBean resetPassword = resetPasswordOptional.get();

        // 驗證重設碼是否有效
        if (!validateResetCode(resetCode, resetPassword)) {
            throw new RuntimeException("驗證碼錯誤或失效"); // 如果驗證失敗，丟出異常
        }

        // 根據 memberId 查詢會員資料
        Integer memberId = resetPassword.getMember().getMemberId();
        Optional<T41MemberBean> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isEmpty()) {
            throw new RuntimeException("請檢查該帳號是否有申請為會員"); // 找不到會員
        }

        T41MemberBean member = memberOptional.get();

        // 密碼重設邏輯
        member.setPassword(newPassword); // 設置新密碼

        // 更新重設狀態為已使用
        resetPassword.setResetState("USED");

        // 保存更新的資料
        resetPasswordRepository.save(resetPassword);
        memberRepository.save(member); // 保存更新的用戶資料
    }

}