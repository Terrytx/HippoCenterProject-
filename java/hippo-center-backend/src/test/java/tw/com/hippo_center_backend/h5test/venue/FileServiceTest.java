// package tw.com.hippo_center_backend.h5test.venue;

// import static org.junit.jupiter.api.Assertions.assertArrayEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.io.IOException;
// import java.io.InputStream;

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
// public class FileServiceTest {
// 	@Autowired
//     private A1VenueServiceImpl venueService;
	
//     @Autowired
//     private ResourceLoader resourceLoader;
//     @Test
//     public void testUploadAndDownloadRentalRegulation() throws IOException {
//     	// 準備測試資料
//         String venueId = "A01"; // 確保這個ID在資料庫中存在
//         System.out.println("開始測試場地ID: " + venueId);

        
//         // 讀取測試PDF檔案
//         Resource resource = resourceLoader.getResource("classpath:rules.pdf");
//         System.out.println("PDF資源是否存在: " + resource.exists());
//         System.out.println("PDF檔案路徑: " + resource.getURI());
//         InputStream inputStream = resource.getInputStream();
//         System.out.println("取得檔案輸入流成功");

        
//         // 創建 MockMultipartFile
//         MultipartFile mockFile = new MockMultipartFile(
//             "rules.pdf",           // 檔案名稱
//             "rules.pdf",           // 原始檔案名稱
//             "application/pdf",      // 內容類型
//             inputStream
//         );
//         System.out.println("建立MockMultipartFile成功");
//         System.out.println("檔案大小: " + mockFile.getSize() + " bytes");
//         System.out.println("檔案類型: " + mockFile.getContentType());
//         // 測試上傳
//         System.out.println("開始執行檔案上傳...");
//         T51VenueBean updatedVenue = venueService.uploadRentalRegulation(venueId, mockFile);
//         System.out.println("檔案上傳完成");
//         // 驗證上傳結果
//         System.out.println("開始驗證上傳結果...");
//         assertNotNull(updatedVenue, "上傳後的場地物件不應為null");
//         System.out.println("場地資訊: " + updatedVenue.getVenueId() + " - " + updatedVenue.getVenueName());
        
//         assertNotNull(updatedVenue.getRentalRegulation(), "上傳的檔案內容不應為null");
//         System.out.println("上傳檔案大小: " + updatedVenue.getRentalRegulation().length + " bytes");

//         // 測試下載
//         System.out.println("開始執行檔案下載...");
//         byte[] downloadedFile = venueService.downloadRentalRegulation(venueId);
//         System.out.println("檔案下載完成");

//         // 驗證下載結果
//         System.out.println("開始驗證下載結果...");
//         assertNotNull(downloadedFile, "下載的檔案不應為null");
//         System.out.println("下載檔案大小: " + downloadedFile.length + " bytes");
        
//         assertTrue(downloadedFile.length > 0, "下載的檔案應該有內容");

//         // 比較上傳和下載的檔案內容
//         System.out.println("開始比較檔案內容...");
//         byte[] originalBytes = mockFile.getBytes();
//         System.out.println("原始檔案大小: " + originalBytes.length + " bytes");
//         System.out.println("下載檔案大小: " + downloadedFile.length + " bytes");
        
//         assertArrayEquals(originalBytes, downloadedFile, "上傳和下載的檔案內容應該相同");
//         System.out.println("檔案內容比對完成，測試成功！");

//         inputStream.close();
//         System.out.println("測試完成，資源已釋放");
    	
//     }
    
    
// //    @Test
//     public void testUploadAndDownloadTechnicalSpecifications() throws IOException {
//         // 準備測試資料
//         String venueId = "A04"; // 確保這個ID在資料庫中存在
//         System.out.println("開始測試場地ID: " + venueId);

//         // 讀取測試PDF檔案
//         Resource resource = resourceLoader.getResource("classpath:rules.pdf");
//         System.out.println("PDF資源是否存在: " + resource.exists());
//         System.out.println("PDF檔案路徑: " + resource.getURI());
//         InputStream inputStream = resource.getInputStream();
//         System.out.println("取得檔案輸入流成功");

//         // 創建 MockMultipartFile
//         MultipartFile mockFile = new MockMultipartFile(
//             "technical_specs.pdf", // 檔案名稱
//             "technical_specs.pdf", // 原始檔案名稱
//             "application/pdf", // 內容類型
//             inputStream
//         );
//         System.out.println("建立MockMultipartFile成功");
//         System.out.println("檔案大小: " + mockFile.getSize() + " bytes");
//         System.out.println("檔案類型: " + mockFile.getContentType());

//         // 測試上傳
//         System.out.println("開始執行檔案上傳...");
//         T51VenueBean updatedVenue = venueService.uploadTechnicalSpecifications(venueId, mockFile);
//         System.out.println("檔案上傳完成");

//         // 驗證上傳結果
//         System.out.println("開始驗證上傳結果...");
//         assertNotNull(updatedVenue, "上傳後的場地物件不應為null");
//         System.out.println("場地資訊: " + updatedVenue.getVenueId() + " - " + updatedVenue.getVenueName());

//         assertNotNull(updatedVenue.getTechnicalSpecifications(), "上傳的技術規格文件不應為null");
//         System.out.println("上傳檔案大小: " + updatedVenue.getTechnicalSpecifications().length + " bytes");

//         // 測試下載
//         System.out.println("開始執行檔案下載...");
//         byte[] downloadedFile = venueService.downloadTechnicalSpecifications(venueId);
//         System.out.println("檔案下載完成");

//         // 驗證下載結果
//         System.out.println("開始驗證下載結果...");
//         assertNotNull(downloadedFile, "下載的檔案不應為null");
//         System.out.println("下載檔案大小: " + downloadedFile.length + " bytes");

//         assertTrue(downloadedFile.length > 0, "下載的檔案應該有內容");

//         // 比較上傳和下載的檔案內容
//         System.out.println("開始比較檔案內容...");
//         byte[] originalBytes = mockFile.getBytes();
//         System.out.println("原始檔案大小: " + originalBytes.length + " bytes");
//         System.out.println("下載檔案大小: " + downloadedFile.length + " bytes");

//         assertArrayEquals(originalBytes, downloadedFile, "上傳和下載的檔案內容應該相同");
//         System.out.println("檔案內容比對完成，測試成功！");

//         inputStream.close();
//         System.out.println("測試完成，資源已釋放");
//     }
// }
