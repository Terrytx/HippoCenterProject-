package tw.com.hippo_center_backend.h2service.b3;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.hippo_center_backend.h0bean.T15FaqBean;
import tw.com.hippo_center_backend.h1repository.T15FaqRepository;

@Service
@Transactional
public class T15FaqService {
	
	@Autowired
	private T15FaqRepository t15FaqRepository;
	
	// 新增常見問題
    public T15FaqBean createFaq(T15FaqBean faq) {
        return t15FaqRepository.save(faq);
    }

    // 取得所有常見問題
    public List<T15FaqBean> getAllFaqs() {
    	List<T15FaqBean> faqs = t15FaqRepository.findAll();
        return faqs;
    }

    // 透過 ID 取得單一常見問題
    public Optional<T15FaqBean> getFaqById(Integer id) {
        return t15FaqRepository.findById(id);
    }

    // 更新常見問題
    public T15FaqBean updateFaq(Integer id, T15FaqBean updatedFaq) {
        return t15FaqRepository.findById(id).map(faq -> {
            faq.setFaqTitle(updatedFaq.getFaqTitle());
            faq.setFaqResponse(updatedFaq.getFaqResponse());
            faq.setFaqStatus(updatedFaq.getFaqStatus());
            return t15FaqRepository.save(faq);
        }).orElseThrow(() -> new RuntimeException("FAQ not found with id " + id));
    }

    // 刪除常見問題
    public void deleteFaq(Integer id) {
    	t15FaqRepository.deleteById(id);
    }

}
