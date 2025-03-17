package tw.com.hippo_center_backend.h2service.b3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReservationConfirmation(String toEmail, String toursName, String toursDate, String timeSlot, int numGuests) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("導覽預約成功通知");

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
                        .reservation-info {
                            margin: 20px 0;
                            padding: 20px;
                            background-color: #fff;
                            border-left: 4px solid #f8961e;
                            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        }
                        .reservation-info p {
                            margin: 10px 0;
                        }
                        .reservation-info strong {
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
                            <h1>導覽預約確認通知</h1>
                        </div>
                        <div class="content">
                            <p>親愛的顧客您好，</p>
                            <p>您的導覽預約已成功送出，以下是您的預約詳情：</p>
                            
                            <div class="reservation-info">
                                <p><strong>導覽名稱：</strong>%s</p>
                                <p><strong>導覽日期：</strong>%s</p>
                                <p><strong>時段：</strong>%s</p>
                                <p><strong>預約人數：</strong>%s 人</p>
                            </div>
                            
                            <p>感謝您的參與！</p>
                        </div>
                        <div class="footer">
                            <p>此郵件為系統自動發送，請勿直接回覆</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                toursName,
                toursDate,
                timeSlot,
                numGuests
            );

            helper.setFrom("荷馬市藝術文化中心<hippoboy321@gmail.com>");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("發送導覽預約郵件失敗：" + e);
        }
    }
}
