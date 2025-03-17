package tw.com.hippo_center_backend.h3controller.b3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import tw.com.hippo_center_backend.h0bean.T11NewsCategoryBean;
import tw.com.hippo_center_backend.h2service.b3.T11NewsCategoryService;

import java.util.List;

@RestController
@RequestMapping("/user/news-categories")
public class T11NewsCategoryUserController {

    @Autowired
    private T11NewsCategoryService t11NewsCategoryService;

//    // 新增消息類別
//    @PostMapping
//    public ResponseEntity<T11NewsCategoryBean> addCategory(@RequestBody T11NewsCategoryBean categoryBean) {
//    	try {
//    		// 印出我請求的資料
//    		System.err.println("接收" + categoryBean);
//    		
//    		// 調用Service的新增方法
//            T11NewsCategoryBean createdCategory = t11NewsCategoryService.insertCategory(categoryBean);
//            
//            // 傳回成功的回應
//            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
//        } catch (IllegalArgumentException e) {
//        	
//        	//印出接到的例外訊息
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 更新消息類別
//    @PutMapping("/{categoryId}")
//    public ResponseEntity<T11NewsCategoryBean> updateCategory(@PathVariable Integer categoryId, @RequestBody T11NewsCategoryBean categoryBean) {
//        try {
//            categoryBean.setNewsCategoryId(categoryId);
//            T11NewsCategoryBean updatedCategory = t11NewsCategoryService.updateCategory(categoryBean);
//            return ResponseEntity.ok(updatedCategory);
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//        }
//    }
//
//    // 刪除消息類別
//    @DeleteMapping("/{categoryId}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryId) {
//        try {
//        	t11NewsCategoryService.deleteCategory(categoryId);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    // 取得所有消息類別
    @GetMapping
    public ResponseEntity<List<T11NewsCategoryBean>> getAllCategories() {
        List<T11NewsCategoryBean> categoryList = t11NewsCategoryService.getAllCategories();
        return ResponseEntity.ok(categoryList);
    }

    // 依類別ID取得消息類別
    @GetMapping("/{categoryId}")
    public ResponseEntity<T11NewsCategoryBean> getCategoryById(@PathVariable Integer categoryId) {
        try {
            T11NewsCategoryBean category = t11NewsCategoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}

