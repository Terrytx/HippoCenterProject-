package tw.com.hippo_center_backend.h2service.b5impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h1repository.T51VenueRepository;
import tw.com.hippo_center_backend.h2service.b5.VenueService;
@Service  // 管理員用Service--增刪修查
@Transactional
public class A1VenueServiceImpl implements VenueService {
	@Autowired
	private T51VenueRepository venueRepository;
	
	@Override
	public T51VenueBean insert(T51VenueBean bean) {
		// 如果id沒有，才會新增
		if (bean!=null && bean.getVenueId()!=null) {
			Optional<T51VenueBean> existingBean = venueRepository.findById(bean.getVenueId());
				System.out.println("場地是否存在= " + existingBean.isPresent());
		        System.out.println(" bean: " + bean);

			if (existingBean.isEmpty()) {
				setCreatedDateTime(bean);  // 設置創建時間
	            setUpdatedDateTime(bean);  // 同時也設置更新時間 
				System.out.println("確認場地是否存在=" + existingBean);
				return venueRepository.save(bean);
			}
		    System.out.println("場地：" + bean.getVenueId() +"已存在");
		}
		return null;
	}
	// 透過 館場 ID 修改	
	@Override
	public T51VenueBean update(T51VenueBean bean) {
		if (bean != null && bean.getVenueId() != null) {
			if (venueRepository.existsById(bean.getVenueId())) {
				setUpdatedDateTime(bean); 
				return venueRepository.save(bean);
			}
		}
		return null;
	}
	// 透 過館場 ID 新增 
	public boolean delete(T51VenueBean bean) {
		if (bean != null && bean.getVenueId() != null) {
			if (venueRepository.existsById(bean.getVenueId())) {
				venueRepository.deleteById(bean.getVenueId());
				return true;
			}
		}
		return false;
	}
//    public long count(String json) {
//        try {
//            JSONObject criteria = new JSONObject(json);
//            return venueRepository.count(VenueSpecification.getSpecification(criteria));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
	public boolean exists(String id) {
		if (id != null) {
			return venueRepository.existsById(id);
		}
		return false;
	}
	@Override    // 透過 館場 ID 尋找
	public T51VenueBean findById(String id) {
		if(id!=null) {
			Optional<T51VenueBean> optional = venueRepository.findById(id);
			if(optional.isPresent()) {
				return optional.get();
			}		
		}
		return null;
	}
	@Override    // 查詢所有館場
	public List<T51VenueBean> findAll(T51VenueBean bean) {
		List<T51VenueBean> result = null;
			if(bean!=null && bean.getVenueId()!=null && !bean.getVenueId().equals(0)) {
				Optional<T51VenueBean> optional = venueRepository.findById(bean.getVenueId());
				if(optional.isPresent()) {
					result = new ArrayList<T51VenueBean>();
					result.add(optional.get());
				}
			} else {
				result = venueRepository.findAll(); 
			}
			return result;
		
	}
	@Override    // 查詢所有可提供租借的場地（rental_statue = true）
	public List<T51VenueBean> findAllAvailableVenues() {
		 return venueRepository.findByRentalStatueTrue();
	
	}
	 // 檢查特定場地是否可提供租借
    public boolean isVenueAvailable(String venueId) {
        Optional<T51VenueBean> venue = venueRepository.findById(venueId);
        return venue.map(T51VenueBean::getRentalStatue).orElse(false);
    }
     // 檔案上傳-- --場地使用規章(檔案)
    @Override
    public T51VenueBean uploadRentalRegulation(String venueId, MultipartFile file) {
        try {
            T51VenueBean venue = findById(venueId);
            if (venue != null) {
                venue.setRentalRegulation(file.getBytes());
                setUpdatedDateTime(venue); 
                return venueRepository.save(venue);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("檔案上傳失敗", e);
        }
    }

	// 檔按下載 --場地使用規章(檔案)
    public byte[] downloadRentalRegulation(String venueId) {
        Optional<T51VenueBean> venue = venueRepository.findById(venueId);
        return venue.map(T51VenueBean::getRentalRegulation).orElse(null);
    }
	@Override   // 下載技術規格文件
	public byte[] downloadTechnicalSpecifications(String venueId) {
	    System.out.println("開始下載技術規格文件，場地ID: " + venueId);
	    
	    if (venueId != null) {
	        System.out.println("場地ID有效，開始查詢資料庫...");
	        Optional<T51VenueBean> venue = venueRepository.findById(venueId);
	        System.out.println("查詢結果: " + (venue.isPresent() ? "找到場地" : "未找到場地"));

	        if (venue.isPresent()) {
	            System.out.println("成功獲取場地資料，正在讀取技術規格文件...");
	            byte[] specs = venue.get().getTechnicalSpecifications();
	            System.out.println("技術規格文件: " + (specs != null ? "大小為 " + specs.length + " bytes" : "文件不存在(null)"));
	        
	            if (specs == null) {
	                System.out.println("警告：場地存在但沒有技術規格文件");
	            }
	            return specs;
	        } else {
	            System.out.println("無法下載文件：場地不存在，venueId: " + venueId);
	        }
	    } else {
	        System.out.println("錯誤：提供的場地ID為null");
	    }
	    return null;
	}
	
	// 上傳技術規格文件
	@Override
	public T51VenueBean uploadTechnicalSpecifications(String venueId, MultipartFile file) {
	    try {
	        System.out.println("開始上傳技術規格文件，場地ID: " + venueId);
	        System.out.println("上傳檔案大小: " + file.getSize() + " bytes");
	        System.out.println("檔案類型: " + file.getContentType());
	        T51VenueBean venue = findById(venueId);
	        if (venue != null) {
	            System.out.println("找到場地資料，開始更新技術規格文件");
	            venue.setTechnicalSpecifications(file.getBytes());
	            T51VenueBean savedVenue = venueRepository.save(venue);
	            setUpdatedDateTime(venue); 
	            System.out.println("技術規格文件上傳完成，更新後的檔案大小: " + 
	                             (savedVenue.getTechnicalSpecifications() != null ? 
	                              savedVenue.getTechnicalSpecifications().length + " bytes" : "null"));
	            return savedVenue;
	        }
	        
	        System.out.println("未找到指定場地，venueId: " + venueId);
	        return null;
	    } catch (IOException e) {
	        System.err.println("檔案上傳過程發生錯誤: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("技術規格文件上傳失敗", e);
	    }
	}
	


}

