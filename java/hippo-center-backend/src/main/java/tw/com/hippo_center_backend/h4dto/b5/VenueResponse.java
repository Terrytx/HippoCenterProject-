package tw.com.hippo_center_backend.h4dto.b5;

import java.util.List;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
// 回應訊息
public class VenueResponse {
    private Boolean success;
    private String message;
    private List<T51VenueBean> list;
    private Long count;
       
    
	@Override
	public String toString() {
		return "VenueResponse [success=" + success + ", message=" + message + ", list=" + list + ", count=" + count
				+ "]";
	}
   public VenueResponse(Boolean success, String message, List<T51VenueBean> list, Long count) {
        this.success = success;
        this.message = message;
        this.list = list;
        this.count = count;
    }
	public VenueResponse(boolean success2, String message2, List<T53VenueStatusBean> results, long size) {
		// TODO Auto-generated constructor stub
	}
	
    public VenueResponse() {
        super();
    }
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
	public List<T51VenueBean> getList() {
		return list;
	}
	public void setList(List<T51VenueBean> list) {
		this.list = list;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
  
 

	
}
