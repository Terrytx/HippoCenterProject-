package tw.com.hippo_center_backend.h0bean;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t11_news_category")
public class T11NewsCategoryBean {
	// 消息類別編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "news_category_id")
    private Integer newsCategoryId;

    // 消息類別名稱
    @Column(name = "news_category_name", nullable = false, length = 255)
    private String newsCategoryName;

	@OneToMany(mappedBy = "category")
	@JsonManagedReference // 主控端
	private List<T10NewsBean> news;

	public Integer getNewsCategoryId() {
		return newsCategoryId;
	}

	public void setNewsCategoryId(Integer newsCategoryId) {
		this.newsCategoryId = newsCategoryId;
	}

	public String getNewsCategoryName() {
		return newsCategoryName;
	}

	public void setNewsCategoryName(String newsCategoryName) {
		this.newsCategoryName = newsCategoryName;
	}

	public List<T10NewsBean> getNews() {
		return news;
	}

	public void setNews(List<T10NewsBean> news) {
		this.news = news;
	}

	@Override
	public String toString() {
		return "T11NewsCategoryBean [newsCategoryId=" + newsCategoryId + ", newsCategoryName=" + newsCategoryName
				+ ", news=" + news + "]";
	}
	
}
