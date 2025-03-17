package tw.com.hippo_center_backend.h4dto.b5;

import java.time.LocalDateTime;

public class BookingResponse<T> {
    private Boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    // 一般建構子
    public BookingResponse() {
    }
    
    // 完整建構子
    public BookingResponse(Boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    // Getter 和 Setter
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // 靜態方法
    public static <T> BookingResponse<T> success(String message, T data) {
        BookingResponse<T> response = new BookingResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public static <T> BookingResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> BookingResponse<T> error(String message) {
        BookingResponse<T> response = new BookingResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}