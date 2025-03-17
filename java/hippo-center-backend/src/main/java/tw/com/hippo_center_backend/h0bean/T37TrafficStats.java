package tw.com.hippo_center_backend.h0bean;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t37_traffic_stats")
public class T37TrafficStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime visitTime;
    private String ipAddress;
    private String userAgent;
    private String pageUrl;
    private String referer;

    // 無參數建構子（必須要有，JPA 需要）
    public T37TrafficStats() {
        this.visitTime = LocalDateTime.now(); // 預設為當前時間
    }

    // 有參數建構子（方便初始化使用）
    public T37TrafficStats(String ipAddress, String userAgent, String pageUrl, String referer) {
        this.visitTime = LocalDateTime.now();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.pageUrl = pageUrl;
        this.referer = referer;
    }

    // Getter & Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalDateTime visitTime) {
        this.visitTime = visitTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    // toString 方法（方便日誌或除錯用）
    @Override
    public String toString() {
        return "TrafficStats{" +
                "id=" + id +
                ", visitTime=" + visitTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", pageUrl='" + pageUrl + '\'' +
                ", referer='" + referer + '\'' +
                '}';
    }
}
