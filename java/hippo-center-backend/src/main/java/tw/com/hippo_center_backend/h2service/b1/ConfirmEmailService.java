package tw.com.hippo_center_backend.h2service.b1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import tw.com.hippo_center_backend.h0bean.T33TicketBean;
import tw.com.hippo_center_backend.h0bean.T35TicketOrderBean;

import java.util.List;

@Service
public class ConfirmEmailService {

    @Autowired
    private JavaMailSender mailSender;

    // **購票確認郵件方法**
    public void sendPurchaseConfirmation(List<T33TicketBean> tickets, T35TicketOrderBean order) {
        try {
            if (tickets == null || tickets.isEmpty()) {
                System.err.println("❌ 沒有有效的票券資訊");
                return;
            }

            // 取第一張票的會員資訊作為收件者
            T33TicketBean firstTicket = tickets.get(0);
            String recipientEmail = firstTicket.getMemberId().getAccount();
            String memberName = firstTicket.getMemberId().getName();

            // **設定郵件標題**
            String subject = "【購票成功通知】Hippo Center 活動門票";

            // **組裝郵件內容**
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append(String.format("""
                親愛的 %s 您好，
                
                感謝您購買 Hippo Center 的活動票券，以下是您的票券資訊：
                
                訂單編號：%s
                購買日期：%s
                總金額：NT$ %d
                
                活動資訊：
                活動名稱：%s
                活動地點：%s
                活動日期：%s ~ %s
                購買數量：%d 張
                票券單價：NT$ %d
                
                票券明細：
                """,
                memberName,
                order.getMerchantTradeNo(),
                order.getPurchaseDate(),
                order.getFinalPrice(),
                firstTicket.getEventId().getEventName(),
                firstTicket.getEventId().getVenue().getVenueName(),
                firstTicket.getEventId().getEventStartDate(),
                firstTicket.getEventId().getEventEndDate(),
                tickets.size(),
                firstTicket.getEventPrice()
            ));

            // **列出所有票券資訊**
            for (int i = 0; i < tickets.size(); i++) {
                T33TicketBean ticket = tickets.get(i);
                contentBuilder.append(String.format("""
                    
                    ▸ 票券 #%d：
                    票券編號：%d
                    QR Code：%s
                    """,
                    i + 1,
                    ticket.getTicketId(),
                    ticket.getQrCode()
                ));
            }

            contentBuilder.append("""
                
                重要提醒：
                1. 請妥善保管您的票券 QR Code，勿與他人分享
                2. 入場時請出示 QR Code 以供驗證
                3. 如需退票，請在活動開始前三天完成退票申請
                
                如有任何問題，請聯繫客服：hippoboy321@gmail.com
                
                感謝您的支持！
                
                Hippo Center 團隊
                此郵件為系統自動發送，請勿直接回覆。
                """);

            // **建立郵件物件**
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("荷馬市藝術化中心<hippoboy321@gmail.com>");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(contentBuilder.toString());

            // **發送郵件**
            mailSender.send(message);
            System.out.println("✉️ 成功發送購票通知至：" + recipientEmail);

        } catch (Exception e) {
            System.err.println("❌ 發送購票通知郵件失敗：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
