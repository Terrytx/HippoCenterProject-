package tw.com.hippo_center_backend.h4dto.b2;

public class SalesReportDTO {
    private String date;
    private Long totalAmount;

    // ✅ 修正：確保 JPQL 能對應到這個建構函數
    public SalesReportDTO(String date, Long totalAmount) {
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
