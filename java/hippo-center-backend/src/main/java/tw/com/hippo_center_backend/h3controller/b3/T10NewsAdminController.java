package tw.com.hippo_center_backend.h3controller.b3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T10NewsBean;
import tw.com.hippo_center_backend.h0bean.T11NewsCategoryBean;
import tw.com.hippo_center_backend.h1repository.T11NewsCategoryRepository;
import tw.com.hippo_center_backend.h2service.b3.T10NewsService;

@RestController
@RequestMapping(value = "/admin/news", produces = "application/json")
public class T10NewsAdminController {

    @Autowired
    private T10NewsService t10NewsService;
    
    @Autowired
    private T11NewsCategoryRepository t11NewsCategoryRepository;

    // 新增消息
    @PostMapping
    public ResponseEntity<T10NewsBean> addNews(@RequestBody T10NewsBean newsBean) {
        try {
            T10NewsBean createdNews = t10NewsService.insertNews(newsBean);
            return new ResponseEntity<>(createdNews, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 更新消息
    @PutMapping("/{newsId}")
    public ResponseEntity<T10NewsBean> updateNews(@PathVariable Integer newsId, @RequestBody T10NewsBean newsBean) {
        try {
            newsBean.setNewsId(newsId);

            // 確保 categoryId 被正確轉換為 category
            if (newsBean.getCategory() == null && newsBean.getCategoryId() != null) {
                T11NewsCategoryBean category = t11NewsCategoryRepository.findById(newsBean.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("找不到類別 ID: " + newsBean.getCategoryId()));
                newsBean.setCategory(category);
            }

            T10NewsBean updatedNews = t10NewsService.updateNews(newsBean);
            return ResponseEntity.ok(updatedNews);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    // 刪除消息
    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> deleteNews(@PathVariable Integer newsId) {
        try {
        	t10NewsService.deleteNews(newsId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    // 取得所有消息
//    @GetMapping
//    public ResponseEntity<List<Map<String, Object>>> getAllNews() {
//        List<T10NewsBean> newsList = t10NewsService.getAllNews();
//        if (newsList.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        List<Map<String, Object>> responseList = newsList.stream().map(news -> {
//            Map<String, Object> newsMap = new HashMap<>();
//            newsMap.put("newsId", news.getNewsId());
//            newsMap.put("newsTitle", news.getNewsTitle());
//            newsMap.put("newsContent", news.getNewsContent());
//            newsMap.put("publishDate", news.getPublishDate());
//            newsMap.put("newsStatus", news.getNewsStatus());
//            newsMap.put("categoryId", news.getCategory() != null ? news.getCategory().getNewsCategoryId() : null);
//            newsMap.put("createdTime", news.getCreatedAt() != null ? news.getCreatedAt().toString() : null);
//            newsMap.put("updatedTime", news.getUpdatedAt() != null ? news.getUpdatedAt().toString() : null);
//            return newsMap;
//        }).toList();
//
//        return ResponseEntity.ok(responseList);
//    }
    
    // 取得所有的消息（含未到發布日期的消息）
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllNews() {
        List<T10NewsBean> newsList = t10NewsService.getAllNewsForAdmin();

        if (newsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<Map<String, Object>> responseList = newsList.stream().map(news -> {
            Map<String, Object> newsMap = new HashMap<>();
            newsMap.put("newsId", news.getNewsId());
            newsMap.put("newsTitle", news.getNewsTitle());
            newsMap.put("newsContent", news.getNewsContent());
            newsMap.put("publishDate", news.getPublishDate());
            newsMap.put("newsStatus", news.getNewsStatus());
            newsMap.put("categoryId", news.getCategory() != null ? news.getCategory().getNewsCategoryId() : null);
            newsMap.put("createdTime", news.getCreatedAt() != null ? news.getCreatedAt().toString() : null); // ✅ 確保 null 值不變
            newsMap.put("updatedTime", news.getUpdatedAt() != null ? news.getUpdatedAt().toString() : "-"); // ✅ 如果是 null，直接回傳 "-"
            return newsMap;
        }).toList();

        return ResponseEntity.ok(responseList);
    }


    // 依消息ID取得消息
    @GetMapping("/{newsId}")
    public ResponseEntity<?> getNewsById(@PathVariable Integer newsId) {
        try {
            T10NewsBean news = t10NewsService.getNewsById(newsId);
            return ResponseEntity.ok(news);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("找不到該新聞", HttpStatus.NOT_FOUND);
        }
    }
    
    // 依類別ID取得消息列表
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getNewsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(required = false, defaultValue = "all") String status) { // 可選擇是否只顯示 "上架"

        try {
            List<T10NewsBean> newsList = t10NewsService.getNewsByCategory(categoryId);

            if ("上架".equals(status)) {
                newsList = newsList.stream()
                        .filter(news -> "上架".equals(news.getNewsStatus()))
                        .toList();
            }

            if (newsList.isEmpty()) {
                return new ResponseEntity<>("該類別沒有新聞", HttpStatus.NO_CONTENT);
            }

            return ResponseEntity.ok(newsList);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("找不到該類別", HttpStatus.NOT_FOUND);
        }
    }

}
