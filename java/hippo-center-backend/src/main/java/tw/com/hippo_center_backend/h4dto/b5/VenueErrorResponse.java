package tw.com.hippo_center_backend.h4dto.b5;

import java.time.LocalDateTime;

public class VenueErrorResponse {
	private Boolean success;     // 加入 success 欄位
    private String message;      // 錯誤訊息
    private Object data;         // 加入 data 欄位
    private Long count;          // 加入 count 欄位
    private LocalDateTime timestamp;  // 發生時間
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
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "VenueErrorResponse [success=" + success + ", message=" + message + ", data=" + data + ", count=" + count
				+ ", timestamp=" + timestamp + "]";
	}
	public VenueErrorResponse(Boolean success, String message, Object data, Long count, LocalDateTime timestamp) {
		super();
		this.success = success;
		this.message = message;
		this.data = data;
		this.count = count;
		this.timestamp = timestamp;
	}
    public VenueErrorResponse(String message) {
        this.success = false;
        this.message = message;
        this.data = null;
        this.count = 0L;
        this.timestamp = LocalDateTime.now();
    }
    public VenueErrorResponse(boolean success, String message, Object data, long count) {
        this.success = success;      // false
        this.message = message;      // "系統錯誤：" + e.getMessage()
        this.data = data;           // null
        this.count = count;         // 0L
        this.timestamp = LocalDateTime.now();  // 自動設置當前時間
    }
	
}
