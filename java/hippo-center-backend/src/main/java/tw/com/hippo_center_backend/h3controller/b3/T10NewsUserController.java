package tw.com.hippo_center_backend.h3controller.b3;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T10NewsBean;
import tw.com.hippo_center_backend.h2service.b3.T10NewsService;

@RestController
@RequestMapping("/user/news")
public class T10NewsUserController {

    @Autowired
    private T10NewsService t10NewsService;

//    // 新增消息
//    @PostMapping
//    public ResponseEntity<T10NewsBean> addNews(@RequestBody T10NewsBean newsBean) {
//        try {
//            T10NewsBean createdNews = t10NewsService.insertNews(newsBean);
//            return new ResponseEntity<>(createdNews, HttpStatus.CREATED);
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 更新消息
//    @PutMapping("/{newsId}")
//    public ResponseEntity<T10NewsBean> updateNews(@PathVariable Integer newsId, @RequestBody T10NewsBean newsBean) {
//        try {
//            newsBean.setNewsId(newsId);
//            T10NewsBean updatedNews = t10NewsService.updateNews(newsBean);
//            return ResponseEntity.ok(updatedNews);
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//        }
//    }
//
//    // 刪除消息
//    @DeleteMapping("/{newsId}")
//    public ResponseEntity<Void> deleteNews(@PathVariable Integer newsId) {
//        try {
//        	t10NewsService.deleteNews(newsId);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    // 取得所有消息
    @GetMapping
    public ResponseEntity<List<T10NewsBean>> getAllNews() {
        List<T10NewsBean> newsList = t10NewsService.getAllNewsForUsers(); // 只返回符合條件的消息

        if (newsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(newsList);
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
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getNewsByCategory(@PathVariable Integer categoryId) {
        try {
            List<T10NewsBean> newsList = t10NewsService.getNewsByCategory(categoryId);

            Date today = new Date();
            newsList = newsList.stream()
                    .filter(news -> news.getPublishDate() != null && !news.getPublishDate().after(today)) // ✅ 過濾未來消息
                    .filter(news -> "上架".equals(news.getNewsStatus())) // ✅ 只顯示上架的消息
                    .toList();

            if (newsList.isEmpty()) {
                return new ResponseEntity<>("該類別沒有新聞", HttpStatus.NO_CONTENT);
            }

            return ResponseEntity.ok(newsList);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("找不到該類別", HttpStatus.NOT_FOUND);
        }
    }

}
