package tw.com.hippo_center_backend.h4dto.b5;

import java.time.LocalDateTime;
import java.util.List;

import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;


public class VenueSuccessResponse {
    private boolean success;
    private String message;
    private Object data;
    private long count;
    private LocalDateTime timestamp;
	
    
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
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
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
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
		return "VenueSuccessResponse [message=" + message + ", data=" + data + ", timestamp=" + timestamp + "]";
	}
    public VenueSuccessResponse(String message, Object data) {
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();  // 設置當前時間
    }
    public VenueSuccessResponse(boolean success, String message, List<T53VenueStatusBean> data, long count) {
        this.success = success;      // true
        this.message = message;      // "查詢成功"
        this.data = data;           // results
        this.count = count;         // results.size()
        this.timestamp = LocalDateTime.now();  // 自動設置當前時間
    }
    
    
    
}
