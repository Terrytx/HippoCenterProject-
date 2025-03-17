package tw.com.hippo_center_backend.h3controller.b3;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T15FaqBean;
import tw.com.hippo_center_backend.h2service.b3.T15FaqService;

@RestController
@RequestMapping("/user/faqs")
public class T15FaqUserController {

    @Autowired
    private T15FaqService t15FaqService;

    // 新增常見問題
    @PostMapping
    public ResponseEntity<T15FaqBean> createFaq(@RequestBody T15FaqBean faq) {
        return ResponseEntity.ok(t15FaqService.createFaq(faq));
    }

    // 取得所有常見問題
    @GetMapping
    public ResponseEntity<List<T15FaqBean>> getAllFaqs() {
        return ResponseEntity.ok(t15FaqService.getAllFaqs());
    }

    // 透過 ID 取得單一常見問題
    @GetMapping("/{id}")
    public ResponseEntity<T15FaqBean> getFaqById(@PathVariable Integer id) {
        Optional<T15FaqBean> faq = t15FaqService.getFaqById(id);
        return faq.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 更新常見問題
    @PutMapping("/{id}")
    public ResponseEntity<T15FaqBean> updateFaq(@PathVariable Integer id, @RequestBody T15FaqBean updatedFaq) {
        try {
            return ResponseEntity.ok(t15FaqService.updateFaq(id, updatedFaq));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 刪除常見問題
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Integer id) {
    	t15FaqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}
