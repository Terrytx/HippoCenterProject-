package tw.com.hippo_center_backend.h2service.b5impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import tw.com.hippo_center_backend.h0bean.T52BookingBean;
import tw.com.hippo_center_backend.h1repository.T52BookingRepository;

@Service
public class C2VenueBookingServiceImpl { // 使用這新增 跟查詢 預約表單
    @Autowired
    private T52BookingRepository bookingRepository;
    @Autowired
    private JavaMailSender mailSender;

    public T52BookingBean insert(T52BookingBean bean) {
        if (bean != null) {
            bean.setBookingDatetime(LocalDateTime.now());
            bean.setOrderStatus("送出申請");
            // 先保存訂單
            T52BookingBean savedBean = bookingRepository.save(bean);

            // 發送確認郵件
            try {
                sendBookingConfirmationEmail(savedBean);
            } catch (Exception e) {
                // 記錄錯誤但不影響訂單保存
                System.out.println("發送郵件失敗" + e);
            }

            return savedBean;
        }
        return null;
    }

    private void sendBookingConfirmationEmail(T52BookingBean booking) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 創建日期格式化器
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");

        // 格式化日期時間
        String formattedBookingDate = booking.getBookingDatetime().format(dateFormatter);
        helper.setTo(booking.getContactEmail());
        helper.setSubject("預約確認通知");

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

                  .booking-info {
                      margin: 20px 0;
                      padding: 20px;
                      background-color: #fff;
                      border-left: 4px solid #f8961e;
                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                  }

                  .booking-info p {
                      margin: 10px 0;
                  }

                  .booking-info strong {
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
                            <h1>預約確認通知</h1>
                        </div>
                        <div class="content">
                            <p>親愛的顧客您好，</p>
                            <p>您的預約已成功送出，以下是您的預約詳情：</p>

                            <div class="booking-info">
                                <p><strong>預約編號：</strong>%s</p>
                                <p><strong>租借起日：</strong>%s</p>
                                <p><strong>租借迄日：</strong>%s</p>
                                <p><strong>訂單狀態：</strong>%s</p>
                                <p><strong>預約時間：</strong>%s</p>
                            </div>

                            <p>感謝您的預約！</p>
                        </div>
                        <div class="footer">
                            <p>此郵件為系統自動發送，請勿直接回覆</p>
                        </div>
                    </div>
                </body>
                </html>
                   """,
                booking.getBookingId(),
                booking.getRentalStartDatetime(),
                booking.getRentalEndDatetime(),
                booking.getOrderStatus(),
                formattedBookingDate);

        helper.setText(htmlContent, true); // 第二個參數 true 表示使用 HTML 格式

        mailSender.send(message);
    }

    public T52BookingBean findById(String id) {
        if (id != null) {
            Optional<T52BookingBean> optional = bookingRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    public T52BookingBean findByBookingId(String bookingId) {
        return bookingRepository.findByBookingId(bookingId).orElse(null);
    }

}