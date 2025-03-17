package tw.com.hippo_center_backend.h4dto.b2;

import java.util.List;

public class ProductResponse {
    private Boolean success;
    private String message;
    private List<ProductDTO> list;
    private Long count;

        // 全參構造函數
        public ProductResponse(Boolean success, String message, List<ProductDTO> list, Long count) {
            this.success = success;
            this.message = message;
            this.list = list;
            this.count = count;
        }
    
        // 預設構造函數
        public ProductResponse() {
        }

    @Override
    public String toString() {
        return "ProductResponse [success=" + success + ", message=" + message + ", list=" + list + ", count=" + count
                + "]";
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

    public List<ProductDTO> getList() {
        return list;
    }

    public void setList(List<ProductDTO> list) {
        this.list = list;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
