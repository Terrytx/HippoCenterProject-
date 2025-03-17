package tw.com.hippo_center_backend.h0bean;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "t10_news")
public class T10NewsBean {
	// 消息編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "news_id")
	private Integer newsId;

	// 消息標題
	@Column(name = "news_title", nullable = false, length = 255)
	private String newsTitle;

	// 消息內容
	@Column(name = "news_content", nullable = false, columnDefinition = "TEXT")
	private String newsContent;

	// FK: 消息類別編號
	@ManyToOne
	@JoinColumn(name = "category_id", nullable = true)
	@JsonBackReference // 防止迴圈
	private T11NewsCategoryBean category;

	// 消息發布日期
	@Column(name = "publish_date")
	private Date publishDate;

	// 消息狀態
	@Column(name = "news_status", nullable = false, length = 50)
	private String newsStatus;

	// 消息創建時間
	@Column(name = "created_at", updatable = false, nullable = false)
	private Timestamp createdAt;

	// 消息更新時間
	@Column(name = "updated_at")
	private Timestamp updatedAt;
	
	@Transient  // 這不會存入資料庫
	private Integer categoryId;

	public Integer getCategoryId() {
	    return category != null ? category.getNewsCategoryId() : categoryId;
	}

	public void setCategoryId(Integer categoryId) {
	    this.categoryId = categoryId;
	}

	public Integer getNewsId() {
		return newsId;
	}

	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getNewsContent() {
		return newsContent;
	}

	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}

	public T11NewsCategoryBean getCategory() {
		return category;
	}

	public void setCategory(T11NewsCategoryBean category) {
		this.category = category;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getNewsStatus() {
		return newsStatus;
	}

	public void setNewsStatus(String newsStatus) {
		this.newsStatus = newsStatus;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@PrePersist
	protected void onCreate() {
	    this.createdAt = new Timestamp(System.currentTimeMillis()); // **確保新建時一定有 `createdAt`**
	    this.updatedAt = null; // **新增時 `updatedAt` 保持 `null`**
	}


	@PreUpdate
	protected void onUpdate() {
	    if (this.updatedAt == null) {
	        this.updatedAt = new Timestamp(System.currentTimeMillis()); // **確保修改時才更新 `updatedAt`**
	    }
	}


	@Override
	public String toString() {
		return "T10NewsBean [newsId=" + newsId + ", newsTitle=" + newsTitle + ", newsContent=" + newsContent
				+ ", category=" + category + ", publishDate=" + publishDate + ", newsStatus=" + newsStatus
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
