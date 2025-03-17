// package tw.com.hippo_center_backend.h5test.venue;

// import static org.junit.jupiter.api.Assertions.*;
// import java.io.IOException;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;
// import tw.com.hippo_center_backend.h0bean.T51VenueBean;
// import tw.com.hippo_center_backend.h2service.b5impl.A1VenueServiceImpl;

// @SpringBootTest
// public class FileMultipleImageServiceTest {
// 	@Autowired
//     private A1VenueServiceImpl venueService;
	
//     @Autowired
//     private ResourceLoader resourceLoader;
	
// //	@Test
// 	public void testUploadAndDownloadRentalRegulationForAllVenues() throws IOException {
// 	    String[] venueIds = {"A01", "A02", "A03", "A04", "A05", "A06", "B02", "B03", "B04", "B05", "B06", "C01", "C02"};
	    
// 	    // 讀取測試PDF檔案
// 	    Resource resource = resourceLoader.getResource("classpath:rules.pdf");
// 	    assertTrue(resource.exists(), "PDF檔案不存在");
// 	    System.out.println("PDF檔案路徑: " + resource.getURI());

// 	    // 準備測試檔案
// 	    MultipartFile mockFile = new MockMultipartFile(
// 	        "rules.pdf",
// 	        "rules.pdf",
// 	        "application/pdf",
// 	        resource.getInputStream()
// 	    );
	    
// 	    byte[] originalContent = mockFile.getBytes();
// 	    System.out.println("原始檔案大小: " + originalContent.length + " bytes");

// 	    for (String venueId : venueIds) {
// 	        System.out.println("\n開始處理場地: " + venueId);
	        
// 	        try {
// 	            // 上傳測試
// 	            T51VenueBean updatedVenue = venueService.uploadRentalRegulation(venueId, mockFile);
// 	            assertNotNull(updatedVenue, "上傳後的場地物件不應為null");
// 	            assertNotNull(updatedVenue.getRentalRegulation(), "上傳的檔案內容不應為null");
// 	            System.out.println("上傳成功 - " + venueId);

// 	            // 下載測試
// 	            byte[] downloadedFile = venueService.downloadRentalRegulation(venueId);
// 	            assertNotNull(downloadedFile, "下載的檔案不應為null");
// 	            assertTrue(downloadedFile.length > 0, "下載的檔案應該有內容");
// 	            assertArrayEquals(originalContent, downloadedFile, 
// 	                "場地 " + venueId + " 的檔案內容與原始檔案不符");
// 	            System.out.println("下載驗證成功 - " + venueId);
	            
// 	        } catch (Exception e) {
// 	            System.err.println("處理場地 " + venueId + " 時發生錯誤: " + e.getMessage());
// 	            throw e;
// 	        }
// 	    }
	    
// 	    System.out.println("\n所有場地檔案處理完成");
// 	}
	
// 	@Test
// 	public void testUploadAndDownloadTechnicalSpecificationsForAllVenues() throws IOException {
// 	    String[] venueIds = {"A01", "A02", "A03", "A04", "A05", "B02", "B03", "B04", "B05", "B06", "C01", "C02"};
	    
// 	    // 讀取測試PDF檔案
// 	    Resource resource = resourceLoader.getResource("classpath:rules.pdf");
// 	    assertTrue(resource.exists(), "PDF檔案不存在");
	    
// 	    // 準備測試檔案
// 	    MultipartFile mockFile = new MockMultipartFile(
// 	        "technical_specs.pdf",
// 	        "technical_specs.pdf", 
// 	        "application/pdf",
// 	        resource.getInputStream()
// 	    );
	    
// 	    byte[] originalContent = mockFile.getBytes();
	    
// 	    for (String venueId : venueIds) {
// 	        System.out.println("\n開始處理場地: " + venueId);
	        
// 	        try {
// 	            // 上傳測試
// 	            T51VenueBean updatedVenue = venueService.uploadTechnicalSpecifications(venueId, mockFile);
// 	            assertNotNull(updatedVenue, "上傳後的場地物件不應為null");
// 	            assertNotNull(updatedVenue.getTechnicalSpecifications(), "上傳的技術規格文件不應為null");
// 	            System.out.println("上傳成功 - " + venueId);

// 	            // 下載測試
// 	            byte[] downloadedFile = venueService.downloadTechnicalSpecifications(venueId);
// 	            assertNotNull(downloadedFile, "下載的檔案不應為null");
// 	            assertTrue(downloadedFile.length > 0, "下載的檔案應該有內容");
// 	            assertArrayEquals(originalContent, downloadedFile, 
// 	                "場地 " + venueId + " 的檔案內容與原始檔案不符");
// 	            System.out.println("下載驗證成功 - " + venueId);
	            
// 	        } catch (Exception e) {
// 	            System.err.println("處理場地 " + venueId + " 時發生錯誤: " + e.getMessage());
// 	            throw e;
// 	        }
// 	    }
	    
// 	    System.out.println("\n所有場地檔案處理完成");
// 	}
// }
