package tw.com.hippo_center_backend.h5util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import tw.com.hippo_center_backend.h0bean.T33TicketBean;
import tw.com.hippo_center_backend.h0bean.T35TicketOrderBean;
import tw.com.hippo_center_backend.h4dto.b1.TicketInfo;

public class QRCodeUtils {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "your-32-byte-secret-key-here"; // 請更換為實際的密鑰

    // 生成 QR Code 內容
    public static String generateQRCodeContent(T33TicketBean ticket, T35TicketOrderBean order) {
        String qrData = String.format("OrderID:%d|TicketID:%d|Event:%s|StartDate:%s|EndDate:%s",
            order.getId(),
            ticket.getTicketId(),
            ticket.getEventId().getEventName(),
            ticket.getEventId().getEventStartDate(),
            ticket.getEventId().getEventEndDate()
        );
        
        try {
            return encryptData(qrData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code content", e);
        }
    }

    // 解密 QR Code 內容
    public static TicketInfo decodeQRCodeContent(String encryptedContent) {
        try {
            String decryptedData = decryptData(encryptedContent);
            return parseTicketInfo(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode QR code content", e);
        }
    }

    // 加密數據
    private static String encryptData(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 解密數據
    private static String decryptData(String encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    // 解析票券信息
    private static TicketInfo parseTicketInfo(String data) {
        TicketInfo info = new TicketInfo();
        String[] parts = data.split("\\|");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                switch (keyValue[0]) {
                    case "OrderID":
                        info.setOrderId(Long.parseLong(keyValue[1]));
                        break;
                    case "TicketID":
                        info.setTicketId(Long.parseLong(keyValue[1]));
                        break;
                    case "Event":
                        info.setEventName(keyValue[1]);
                        break;
                    case "StartDate":
                        info.setStartDate(keyValue[1]);
                        break;
                    case "EndDate":
                        info.setEndDate(keyValue[1]);
                        break;
                }
            }
        }
        return info;
    }

    // 生成 QR Code 圖片
    public static byte[] generateQRCodeImage(String content, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate QR code image", e);
        }
    }
}

