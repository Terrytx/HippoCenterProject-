// package tw.com.hippo_center_backend.h5test.venue;

// import static org.junit.jupiter.api.Assertions.assertArrayEquals;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.io.IOException;
// import java.io.InputStream;
// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;

// import tw.com.hippo_center_backend.h0bean.T14VenueImagesBean;
// import tw.com.hippo_center_backend.h2service.b5impl.C1VenueImageServiceImpl;
// import tw.com.hippo_center_backend.h4dto.b5.VenueImageOrderDTO;

// @SpringBootTest
// public class VenueImageServiceTest {
// 	@Autowired
//     private C1VenueImageServiceImpl venueImageService;
// 	@Autowired
//     private ResourceLoader resourceLoader;
   
    
    
    
//     @Test
//     public void testUploadVenueImage() throws IOException {
//         // 準備測試資料
//         String venueId = "A02";  // 確保這個ID在資料庫中存在
//         String altText = "測試圖片描述";
//         Integer sortOrder = 2;
//         System.out.println("開始測試場地ID: " + venueId);

//         // 讀取測試圖片檔案
//         String testFileName = "A02-1.jpg";  // 請確保此檔案存在於resources目錄
//         Resource resource = resourceLoader.getResource("classpath:images/" + testFileName);
        
//         // 確認檔案存在
//         System.out.println("檢查測試檔案狀態...");
//         assertTrue(resource.exists(), "測試檔案不存在: " + testFileName);
//         System.out.println("圖片資源存在: " + resource.exists());
//         System.out.println("圖片檔案路徑: " + resource.getURI());

//         // 讀取檔案內容
//         InputStream inputStream = resource.getInputStream();
//         System.out.println("成功取得檔案輸入流");

//         // 創建 MockMultipartFile
//         MultipartFile mockFile = new MockMultipartFile(
//             testFileName,           // 參數名稱
//             testFileName,           // 原始檔案名稱
//             "image/jpeg",          // 內容類型
//             inputStream
//         );

//         System.out.println("建立MockMultipartFile成功");
//         System.out.println("檔案大小: " + mockFile.getSize() + " bytes");
//         System.out.println("檔案類型: " + mockFile.getContentType());

//         try {
//             // 執行上傳測試
//             System.out.println("開始執行圖片上傳...");
//             T14VenueImagesBean uploadedImage = venueImageService.uploadImage(venueId, mockFile, altText, sortOrder);

//             // 驗證上傳結果
//             assertNotNull(uploadedImage, "上傳後的圖片物件不應為null");
//             assertNotNull(uploadedImage.getVenue(), "圖片應關聯到場地");
//             assertEquals(venueId, uploadedImage.getVenue().getVenueId(), "場地ID應匹配");
//             assertEquals(altText, uploadedImage.getAltText(), "替代文字應匹配");
//             assertEquals(sortOrder, uploadedImage.getSortOrder(), "排序順序應匹配");
            
//             // 驗證圖片內容
//             byte[] uploadedImageData = uploadedImage.getImageData();
//             assertNotNull(uploadedImageData, "上傳的圖片內容不應為null");
//             assertTrue(uploadedImageData.length > 0, "上傳的圖片不應為空");
//             System.out.println("上傳的圖片大小: " + uploadedImageData.length + " bytes");

//             // 比對原始圖片和上傳後的圖片內容
//             byte[] originalContent = mockFile.getBytes();
//             assertArrayEquals(originalContent, uploadedImageData, "圖片內容應該保持一致");
//             System.out.println("圖片內容比對成功");

//             System.out.println("測試成功完成！");

//         } catch (Exception e) {
//             System.err.println("測試過程發生錯誤: " + e.getMessage());
//             e.printStackTrace();
//             throw e;
//         } finally {
//             // 清理資源
//             inputStream.close();
//             System.out.println("測試資源已釋放");
//             }
//         }
// //    @Test
//     void testFindTop3ImagesByVenue() {
//         // 測試一個已知存在的場地ID
//         String testVenueId = "A01"; // 假設資料庫中的場地ID
//         System.out.println("開始測試場地ID: " + testVenueId);
//         // 執行測試
//         List<VenueImageOrderDTO> result = venueImageService.findTop3ImagesByVenue(testVenueId);
//         System.out.println("查詢到的圖片數量: " + result.size());
//         // 驗證結果
//         assertNotNull(result);
//         assertTrue(result.size() <= 3); // 確保不超過3張圖片
        
//         if (!result.isEmpty()) {
//             // 驗證回傳的資料是否正確
//         	VenueImageOrderDTO firstImage = result.get(0);
//             assertNotNull(firstImage.getImageId());
//             assertNotNull(firstImage.getImageData());
//             assertNotNull(firstImage.getSortOrder());
//             System.out.println("第一張圖片資訊：");
//             System.out.println("- ImageId: " + firstImage.getImageId());
//             System.out.println("- 排序順序: " + firstImage.getSortOrder());
//             System.out.println("- 替代文字: " + firstImage.getAltText());
            
            
//             // 驗證排序
//             System.out.println("\n檢查圖片排序：");
//             for (int i = 1; i < result.size(); i++) {
//             	System.out.println("比較第 " + i + " 張圖片排序: " + 
//                         result.get(i-1).getSortOrder() + " < " + 
//                         result.get(i).getSortOrder());
            	
//                 assertTrue(result.get(i).getSortOrder() > result.get(i-1).getSortOrder());
//             }
//         } else {
//             System.out.println("查無圖片資料");
//         }
        
//         System.out.println("測試完成");
 
// }}