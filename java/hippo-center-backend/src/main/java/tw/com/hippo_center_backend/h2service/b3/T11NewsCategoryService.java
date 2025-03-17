package tw.com.hippo_center_backend.h2service.b3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T11NewsCategoryBean;
import tw.com.hippo_center_backend.h1repository.T11NewsCategoryRepository;

@Service
@Transactional
public class T11NewsCategoryService {

    @Autowired
    private T11NewsCategoryRepository t11NewsCategoryRepository;

    // 新增消息類別
    public T11NewsCategoryBean insertCategory(T11NewsCategoryBean bean) {
    	if (bean == null || bean.getNewsCategoryName() == null || bean.getNewsCategoryName().isEmpty()) {
            throw new IllegalArgumentException("消息類別 cannot be null or empty");
        }
        return t11NewsCategoryRepository.save(bean);
   
    }

    // 更新消息類別
    public T11NewsCategoryBean updateCategory(T11NewsCategoryBean bean) {
        if (bean == null || bean.getNewsCategoryId() == null) {
            throw new IllegalArgumentException("消息類別不會是null");
        }

        if (!t11NewsCategoryRepository.existsById(bean.getNewsCategoryId())) {
            throw new IllegalArgumentException("Category with ID" + bean.getNewsCategoryId() + "not found");
        }

        return t11NewsCategoryRepository.save(bean);
    }

    // 刪除消息類別
    public boolean deleteCategory(Integer categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("消息類別不會是null");
        }

        if (!t11NewsCategoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category with ID" + categoryId + "not found");
        }

        t11NewsCategoryRepository.deleteById(categoryId);
        return true;
    }

    // 取得所有消息類別
    public List<T11NewsCategoryBean> getAllCategories() {
        return t11NewsCategoryRepository.findAll();
    }

    // 依照類別ID取得消息類別
    public T11NewsCategoryBean getCategoryById(Integer categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryID cannot be null");
        }

        return t11NewsCategoryRepository
                .findById(categoryId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Category with ID" + categoryId + "not found"));
    }
}
