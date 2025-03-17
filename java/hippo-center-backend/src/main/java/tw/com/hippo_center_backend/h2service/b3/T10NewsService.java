package tw.com.hippo_center_backend.h2service.b3;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T10NewsBean;
import tw.com.hippo_center_backend.h0bean.T11NewsCategoryBean;
import tw.com.hippo_center_backend.h1repository.T10NewsRepository;
import tw.com.hippo_center_backend.h1repository.T11NewsCategoryRepository;

@Service
@Transactional
public class T10NewsService {

    @Autowired
    private T10NewsRepository t10NewsRepository;
    
    @Autowired
    private T11NewsCategoryRepository t11NewsCategoryRepository;

    // 新增消息
    public T10NewsBean insertNews(T10NewsBean bean) {
        if (bean == null) {
            throw new IllegalArgumentException("消息不能為空");
        }

        if (bean.getNewsId() != null) {
            throw new IllegalArgumentException("新發的消息不應該有Id");
        }

        if (bean.getCategoryId() != null) {
            T11NewsCategoryBean category = t11NewsCategoryRepository.findById(bean.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("找不到對應的類別 ID: " + bean.getCategoryId()));
            bean.setCategory(category);
        }

        // ✅ **確保 `createdAt` 只有在新增時設定**
        bean.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // ❌ **不要設定 `updatedAt`，讓它保持 `null`，Hibernate 會在修改時自動設定**
        bean.setUpdatedAt(null);

        return t10NewsRepository.save(bean);
    }


    // 更新消息
    public T10NewsBean updateNews(T10NewsBean newsBean) {
        if (newsBean == null || newsBean.getNewsId() == null) {
            throw new IllegalArgumentException("消息 ID 不能為空");
        }

        T10NewsBean existingNews = t10NewsRepository.findById(newsBean.getNewsId())
            .orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + newsBean.getNewsId() + " 的新聞"));

        // 更新新聞的基本資訊
        existingNews.setNewsTitle(newsBean.getNewsTitle());
        existingNews.setNewsContent(newsBean.getNewsContent());
        existingNews.setPublishDate(newsBean.getPublishDate());
        existingNews.setNewsStatus(newsBean.getNewsStatus());

        // 確保 categoryId 被正確轉換為 category
        if (newsBean.getCategoryId() != null) {
            T11NewsCategoryBean category = t11NewsCategoryRepository.findById(newsBean.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("找不到類別 ID: " + newsBean.getCategoryId()));
            existingNews.setCategory(category);
        }
        
        // ✅ **在修改時更新 `updatedAt`**
        existingNews.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return t10NewsRepository.save(existingNews);
    }


    // 刪除消息
    public boolean deleteNews(Integer newsId) {
        if (newsId == null) {
            throw new IllegalArgumentException("消息不是null");
        }

        if (!t10NewsRepository.existsById(newsId)) {
            throw new IllegalArgumentException("News with ID" + newsId + "not found");
        }

        t10NewsRepository.deleteById(newsId);
        return true;
    }

//    // 取得所有消息
//    public List<T10NewsBean> getAllNews() {
//        return t10NewsRepository.findAll();
//    }
    
    // ✅ 管理端：取得所有消息（包含未來發布的消息）
    public List<T10NewsBean> getAllNewsForAdmin() {
        return t10NewsRepository.findAll(); // ✅ 這應該包含所有欄位，包括 created_at 和 updated_at
    }


    // ✅ 前端：只取得 `publishDate` 已達的 "上架" 消息
    public List<T10NewsBean> getAllNewsForUsers() {
        Date today = new Date();
        return t10NewsRepository.findAll().stream()
            .filter(news -> news.getPublishDate() != null && !news.getPublishDate().after(today)) // 過濾未來的消息
            .filter(news -> "上架".equals(news.getNewsStatus())) // 只顯示上架的消息
            .collect(Collectors.toList());
    }

    // 依照消息ID取得消息
    public T10NewsBean getNewsById(Integer newsId) {
        if (newsId == null) {
            throw new IllegalArgumentException("NewsID cannot be null");
        }

        return t10NewsRepository
                .findById(newsId)
                .orElseThrow(
                        () -> new IllegalArgumentException("News with ID" + newsId + "not found"));
    }
    
    public List<T10NewsBean> getNewsByCategory(Integer categoryId) {
        T11NewsCategoryBean category = t11NewsCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("找不到該類別"));

        Date today = new Date();
        
        return t10NewsRepository.findByCategory(category).stream()
            .filter(news -> news.getPublishDate() != null && !news.getPublishDate().after(today)) // ✅ 過濾未來消息
            .filter(news -> "上架".equals(news.getNewsStatus())) // ✅ 只顯示上架的消息
            .collect(Collectors.toList());
    }

}
