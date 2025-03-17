package tw.com.hippo_center_backend.h0bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "t15_faqs")
public class T15FaqBean {
	// 問題編號
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Integer faqId;

    // 問題標題
    @Column(name = "faq_title", nullable = false, length = 255)
    private String faqTitle;

    // 問題回覆
    @Column(name = "faq_response", nullable = false, length = 500)
    private String faqResponse;

    // 問題狀態
    @Column(name = "faq_status", nullable = false)
    private String faqStatus;

	public int getFaqId() {
		return faqId;
	}

	public void setFaqId(int faqId) {
		this.faqId = faqId;
	}

	public String getFaqTitle() {
		return faqTitle;
	}

	public void setFaqTitle(String faqTitle) {
		this.faqTitle = faqTitle;
	}

	public String getFaqResponse() {
		return faqResponse;
	}

	public void setFaqResponse(String faqResponse) {
		this.faqResponse = faqResponse;
	}

	public String getFaqStatus() {
		return faqStatus;
	}

	public void setFaqStatus(String faqStatus) {
		this.faqStatus = faqStatus;
	}

	@Override
	public String toString() {
		return "T15_Faq [faqId=" + faqId + ", faqTitle=" + faqTitle + ", faqResponse=" + faqResponse + ", faqStatus="
				+ faqStatus + "]";
	}
	
}
