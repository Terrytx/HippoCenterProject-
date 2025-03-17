package tw.com.hippo_center_backend.h2service.b5impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import tw.com.hippo_center_backend.h0bean.T14VenueImagesBean;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h1repository.T14VenueImagesRepository;
import tw.com.hippo_center_backend.h1repository.T51VenueRepository;
import tw.com.hippo_center_backend.h2service.b5.VenueImageService;
import tw.com.hippo_center_backend.h4dto.b5.VenueImageOrderDTO;
// @CrossOrigin(origins = "http://localhost:5173") 
@Service  // 圖片上傳
@Transactional
public class C1VenueImageServiceImpl implements VenueImageService {
    @Autowired
    private T51VenueRepository venueRepository;
	@Autowired
	private T14VenueImagesRepository venueImagesRepository;

	@Override
    public T51VenueBean findById(String id) {
        if(id != null) {
            Optional<T51VenueBean> optional = venueRepository.findById(id);
            return optional.orElse(null);
        }
        return null;
    }

    @Override   // 找出前三張照片
    public List<VenueImageOrderDTO> findTop3ImagesByVenue(String venueId) {
        if (venueId != null) {
            // 檢查場地是否存在
            T51VenueBean venue = this.findById(venueId);
            System.out.println("找到場地: " + (venue != null));

            if (venue != null) {
                List<T14VenueImagesBean> images = venueImagesRepository
                    .findTop3ByVenueOrderBySortOrderAsc(venue);
                System.out.println("找到圖片數量: " + images.size());
                
                return images.stream()
                    .map(image -> {
                        VenueImageOrderDTO dto = new VenueImageOrderDTO(
                            image.getImageId(),
                            image.getImageData(),
                            image.getAltText(),
                            image.getSortOrder()
                        );
                        // 只設置必要的場地資訊
                        dto.setVenueId(venue.getVenueId());  // 只存儲場地ID
                        return dto;
                    })
                    .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
	
	@Override
    public T14VenueImagesBean insert(T14VenueImagesBean bean) {
        if (bean != null && bean.getVenue() != null) {
            setCreatedDateTime(bean);
            setUpdatedDateTime(bean);
            return venueImagesRepository.save(bean);
        }
        return null;
    }

	 @Override
	    public T14VenueImagesBean uploadImage(String venueId, MultipartFile file, String altText, Integer sortOrder) {
	        try {
	            // 檢查場地是否存在
	            T51VenueBean venue = this.findById(venueId);
	            if (venue == null) {
	                throw new RuntimeException("場地不存在");
	            }

	            // 獲取該場地現有的所有圖片並按順序排序
	            List<T14VenueImagesBean> existingImages = 
	                venueImagesRepository.findByVenueOrderBySortOrderAsc(venue);

	            // 處理 sortOrder 邏輯
	            Integer newSortOrder = handleSortOrder(existingImages, sortOrder);
	            System.out.print(newSortOrder);
	            // 創建新的圖片實體
	            T14VenueImagesBean venueImage = new T14VenueImagesBean();
	            
	            // 設置關聯的場地
	            venueImage.setVenue(venue);
	            
	            // 將圖片轉換為 byte[] 並儲存
	            venueImage.setImageData(file.getBytes());
	            
	            // 設置圖片 URL（因為資料庫限制不能為 null）
	            String fileName = file.getOriginalFilename();
	            venueImage.setImageUrl("venues/" + venueId + "/" + fileName);
	            
	            // 設置替代文字
	            venueImage.setAltText(altText);
	            
	            // 設置顯示順序
	            venueImage.setSortOrder(sortOrder);
	            
	            // 設置時間戳記
	            setCreatedDateTime(venueImage);
	            setUpdatedDateTime(venueImage);
	            
	            // 儲存到資料庫
	            return venueImagesRepository.save(venueImage);
	            
	        } catch (IOException e) {
	            throw new RuntimeException("圖片上傳失敗: " + e.getMessage(), e);
	        }
	    }
	// 處理排序邏輯的方法
	 private Integer handleSortOrder(List<T14VenueImagesBean> existingImages, Integer requestedOrder) {
	     if (existingImages.isEmpty()) {
	         // 如果是第一張圖片，設為1
	         return 1;
	     }

	     // 獲取現有的最大順序
	     int maxOrder = existingImages.stream()
	         .mapToInt(T14VenueImagesBean::getSortOrder)
	         .max()
	         .orElse(0);

	     // 如果指定的順序大於最大順序
	     if (requestedOrder > maxOrder) {
	         return maxOrder + 1;
	     }
	     
	     // 檢查是否已存在相同的排序號
	     boolean orderExists = existingImages.stream()
	         .anyMatch(img -> img.getSortOrder().equals(requestedOrder));

	     // 如果排序號已存在，新圖片的排序號加1
	     if (orderExists) {
	         return requestedOrder + 1;
	     }

	     return requestedOrder;
	     }
	 
	 @Override
	 @Transactional   // 幫圖片排順序
	 public List<T14VenueImagesBean> updateImagesOrder(List<VenueImageOrderDTO> imageOrders) {
	     try {
	         List<T14VenueImagesBean> updatedImages = new ArrayList<>();
	         
	         // 將所有需要更新的圖片載入到記憶體中
	         for (VenueImageOrderDTO orderDTO : imageOrders) {
	             T14VenueImagesBean image = venueImagesRepository.findById(orderDTO.getImageId())
	                 .orElseThrow(() -> new RuntimeException("圖片不存在: " + orderDTO.getImageId()));
	             
	             image.setSortOrder(orderDTO.getSortOrder());
	             setUpdatedDateTime(image);
	             updatedImages.add(venueImagesRepository.save(image));
	         }
	         
	         return updatedImages;
	     } catch (Exception e) {
	         throw new RuntimeException("更新圖片順序失敗", e);
	     }
	 }
	 
	 
	  // 可以新增一個方法來取得特定場地的所有圖片，並按照 sortOrder 排序
	 public List<T14VenueImagesBean> findByVenueIdOrderBySortOrder(String venueId) {
	        T51VenueBean venue = this.findById(venueId);
	        if (venue != null) {
	            return venueImagesRepository.findByVenueOrderBySortOrderAsc(venue);
	        }
	        return new ArrayList<>();
	    }
	}

