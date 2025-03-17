// package tw.com.hippo_center_backend.h5test.venue;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import java.io.IOException;
// import java.io.InputStream;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;

// import tw.com.hippo_center_backend.h0bean.T14VenueImagesBean;
// import tw.com.hippo_center_backend.h2service.b5impl.C1VenueImageServiceImpl;


// @SpringBootTest
// public class VenueMultipleImageServiceTest {
// 	@Autowired
//     private C1VenueImageServiceImpl venueImageService;
// 	@Autowired
//     private ResourceLoader resourceLoader;

// 	@Test
// 	public void testUploadMultipleVenueImages() throws IOException {
// 	    String[] venueIds = {"A01", "A02", "A03", "A04", "A05", "A06", "B02", "B03", "B04", "B05", "B06", "C01", "C02"};
	    
// 	    for (String venueId : venueIds) {
// 	        System.out.println("開始處理場地: " + venueId);
	        
// 	        for (int imageNum = 1; imageNum <= 3; imageNum++) {
// 	            String fileName = venueId + "-" + imageNum + ".jpg";
// 	            String altText = "場地" + venueId + "圖片" + imageNum;
	            
// 	            Resource resource = resourceLoader.getResource("classpath:images/" + fileName);
	            
// 	            if (!resource.exists()) {
// 	                System.out.println("警告: 找不到圖片 " + fileName);
// 	                continue;
// 	            }
	
// 	            try (InputStream inputStream = resource.getInputStream()) {
// 	                MultipartFile mockFile = new MockMultipartFile(
// 	                    fileName,
// 	                    fileName,
// 	                    "image/jpeg",
// 	                    inputStream
// 	                );
	
// 	                T14VenueImagesBean uploadedImage = venueImageService.uploadImage(
// 	                    venueId,
// 	                    mockFile,
// 	                    altText,
// 	                    imageNum
// 	                );
	
// 	                // 驗證上傳結果
// 	                assertNotNull(uploadedImage, "上傳圖片失敗: " + fileName);
// 	                assertEquals(venueId, uploadedImage.getVenue().getVenueId());
// 	                assertEquals(altText, uploadedImage.getAltText());
// 	                assertEquals(imageNum, uploadedImage.getSortOrder());
	                
// 	                System.out.println("成功上傳: " + fileName);
// 	            } catch (Exception e) {
// 	                System.err.println("上傳失敗 " + fileName + ": " + e.getMessage());
// 	                throw e;
// 	            }
// 	        }
// 	        System.out.println("完成場地 " + venueId + " 的圖片上傳\n");
// 	    }
// 	}
	
	
// }