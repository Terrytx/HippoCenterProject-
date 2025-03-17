package tw.com.hippo_center_backend.h2service.b1;
//管理員(活動類型管理)

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import tw.com.hippo_center_backend.h0bean.T32EventCategoryBean;
import tw.com.hippo_center_backend.h1repository.T32EventCategoryRepository;

@Service
public class T32EventCategoryService {

    private final T32EventCategoryRepository t32EventCategoryRepository;

    
    public T32EventCategoryService(T32EventCategoryRepository t32EventCategoryRepository) {
        this.t32EventCategoryRepository = t32EventCategoryRepository;
    }
    
    /**
     * 查詢所有類別
     * 
     * @return 類別列表
     */
    public List<T32EventCategoryBean> findAll() {
        return t32EventCategoryRepository.findAll();
    }
    /**
     * 根據 ID 查詢類別
     * 
     * @param id 類別 ID
     * @return 類別資料（Optional）
     */
    public Optional<T32EventCategoryBean> findById(Integer id) {
        return t32EventCategoryRepository.findById(id);
    }

    public T32EventCategoryBean save(T32EventCategoryBean category) {
        if (category.getEventCategoryId() != null) {
            // 確認資料存在（僅適用於更新）
        	t32EventCategoryRepository.findById(category.getEventCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found for ID: " + category.getEventCategoryId()));
        }
        return t32EventCategoryRepository.save(category);
    }
    /**
     * 根據類別描述查詢
     * @param categoryDescription 類別描述
     * @return 符合描述的類別列表
     */
    public List<T32EventCategoryBean> geteventCategoriesByDescription(String eventCategoryDescription) {
        return t32EventCategoryRepository.findByeventCategoryDescription(eventCategoryDescription);
    }

    /**
     * 根據類別類型查詢
     * @param categoryType 類別類型
     * @return 類別列表
     */
    public List<T32EventCategoryBean> geteventCategoriesByType(Integer eventCategoryType) {
        return t32EventCategoryRepository.findByeventCategoryType(eventCategoryType);
    }

    /**
     * 根據描述的部分內容進行模糊查詢
     * @param keyword 關鍵字
     * @return 包含關鍵字的類別列表
     */
    public List<T32EventCategoryBean> searchCategoriesByKeyword(String keyword) {
        return t32EventCategoryRepository.findByeventCategoryDescriptionContaining(keyword);
    }
 
    // 新增方法
    public T32EventCategoryBean createCategory(T32EventCategoryBean category) {
        // 確保是新增操作
        category.setEventCategoryId(null);
        return t32EventCategoryRepository.save(category);
    }
    /**
     * 刪除類別
     * @param categoryId 類別 ID
     */
    public void deleteCategoryById(Integer categoryId) {
        t32EventCategoryRepository.deleteById(categoryId);
    }

    /**
     * 刪除所有類別（僅用於特殊需求，需謹慎）
     */
    public void deleteAllCategories() {
        t32EventCategoryRepository.deleteAll();
    }
      
}




//已廢棄
//public T32EventCategoryService(T32EventCategoryRepository categoryRepository) {
//this.categoryRepository = categoryRepository;
//}
///**
//* 查詢所有類別
//* @return 類別列表
//*/
//public List<T32EventCategoryBean> getAllCategories() {
//return t32EventCategoryRepository.findAll();
//}
//
///**
//* 根據類別 ID 查詢類別
//* @param eventCategoryId 類別 ID
//* @return 類別資料（Optional）
//*/
//public Optional<T32EventCategoryBean> geteventCategoryById(Integer eventCategoryId) {
//return t32EventCategoryRepository.findById(eventCategoryId);
//}

//
//// 更新方法
//public T32EventCategoryBean updateCategory(T32EventCategoryBean category) {
//  if (category.getEventCategoryId() == null) {
//      throw new IllegalArgumentException("更新時必須提供 ID");
//  }
//  
//  // 檢查是否存在
//  t32EventCategoryRepository.findById(category.getEventCategoryId())
//      .orElseThrow(() -> new EntityNotFoundException("找不到類別 ID: " + category.getEventCategoryId()));
//      
//  return t32EventCategoryRepository.save(category);
//}