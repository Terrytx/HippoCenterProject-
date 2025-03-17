package tw.com.hippo_center_backend.h3controller.b1;


import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.hippo_center_backend.h0bean.T32EventCategoryBean;
import tw.com.hippo_center_backend.h2service.b1.T32EventCategoryService;

/*管理員專用 */

@RestController
@RequestMapping("/api/v1/event-categories-admin") // locathost://8080port後面加上API 路徑前綴
public class T32EventCategoryAdminController {

	
	  private final T32EventCategoryService categoryService;

	    public T32EventCategoryAdminController(T32EventCategoryService categoryService) {
	        this.categoryService = categoryService;
	    }

    /**
     * 查詢所有類別
     *
     * @return 類別列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<T32EventCategoryBean>> getAllCategories() {
        List<T32EventCategoryBean> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    /**
     * 根據 ID 查詢類別
     *
     * @param id 類別 ID
     * @return 類別資料（若存在） //不存在會顯示404notfound
     */
    @GetMapping("/{id}")
    public ResponseEntity<T32EventCategoryBean> getCategoryById(@PathVariable Integer id) {
        Optional<T32EventCategoryBean> category = categoryService.findById(id);
       return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
   
    //基本上不會有增加類別的需求//
    
    @PostMapping("/create")
    public ResponseEntity<T32EventCategoryBean> createCategory(@RequestBody T32EventCategoryBean category) {
        try {
            // 改用 Service 層的方法，而不是直接使用 repository
            T32EventCategoryBean savedCategory = categoryService.createCategory(category);
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // 用於更新
    @PutMapping("/{id}")
    public ResponseEntity<T32EventCategoryBean> updateCategory(@PathVariable Integer id, 
                                                               @RequestBody T32EventCategoryBean category) {
        // 確保更新時傳入的 ID 和路徑中的 ID 一致
        category.setEventCategoryId(id);
        T32EventCategoryBean updatedCategory = categoryService.save(category);
        return ResponseEntity.ok(updatedCategory);
    }      

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryId) {
        try {
            Optional<T32EventCategoryBean> category = categoryService.findById(categoryId);
            if (category.isPresent()) {
                System.out.println("找到類別，ID: " + categoryId);  // 加入日誌
                categoryService.deleteCategoryById(categoryId);
                System.out.println("類別已刪除，ID: " + categoryId);  // 加入日誌
                return ResponseEntity.noContent().build();
            } else {
                System.out.println("找不到類別，ID: " + categoryId);  // 加入日誌
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("刪除失敗，原因: " + e.getMessage());  // 加入日誌
            return ResponseEntity.badRequest().build();
        }
    }
}