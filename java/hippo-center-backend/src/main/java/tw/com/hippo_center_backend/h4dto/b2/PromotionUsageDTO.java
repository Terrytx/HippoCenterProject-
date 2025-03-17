package tw.com.hippo_center_backend.h4dto.b2;

public class PromotionUsageDTO {
    private String month;
    private long usedCount;
    private long activeCount;  // (包含 NEW & ACTIVE)
    private long expiredCount;
    
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public long getUsedCount() {
        return usedCount;
    }
    public void setUsedCount(long usedCount) {
        this.usedCount = usedCount;
    }
    public long getActiveCount() {
        return activeCount;
    }
    public void setActiveCount(long activeCount) {
        this.activeCount = activeCount;
    }
    public long getExpiredCount() {
        return expiredCount;
    }
    public void setExpiredCount(long expiredCount) {
        this.expiredCount = expiredCount;
    }

    
}

