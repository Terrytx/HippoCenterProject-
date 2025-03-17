package tw.com.hippo_center_backend.h2service.b5impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h1repository.T51VenueRepository;
import tw.com.hippo_center_backend.h2service.b5.VenueService;

@Service   // 客戶端 查詢
public class C1VenueFindServiceImpl implements VenueService{

	@Autowired
	private T51VenueRepository venueRepository;
	
		
	@Override // 透過館場Id 查詢
	public T51VenueBean findById(String id) {
		if(id!=null) {
			Optional<T51VenueBean> optional = venueRepository.findById(id);
			if(optional.isPresent()) {
				return optional.get();
			}		
		}
		return null;
	}
	
	@Override
	  // 查詢所有可提供租借的場地（rental_statue = true）
    public List<T51VenueBean> findAllAvailableVenues() {
        return venueRepository.findByRentalStatueTrue();
    }
	 // 檢查特定場地是否可提供租借
    public boolean isVenueAvailable(String venueId) {
        Optional<T51VenueBean> venue = venueRepository.findById(venueId);
        return venue.map(T51VenueBean::getRentalStatue).orElse(false);
    }

	@Override
	public T51VenueBean insert(T51VenueBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T51VenueBean> findAll(T51VenueBean bean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public T51VenueBean uploadRentalRegulation(String venueId, MultipartFile mockFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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

	@Override
	public T51VenueBean update(T51VenueBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T51VenueBean uploadTechnicalSpecifications(String venueId, MultipartFile file) {
		// TODO Auto-generated method stub
		return null;
	}
}

