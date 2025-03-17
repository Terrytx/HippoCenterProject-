// package tw.com.hippo_center_backend.h5test.venueStatus;

// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;

// import tw.com.hippo_center_backend.h2service.b5impl.A3VenueStatusServiceImpl;

// @SpringBootTest
// public class venueStatusUploadFileTest {
// 	@Autowired
// 	private A3VenueStatusServiceImpl venueService;
// 	@Test
// 	void testImportFromCsv() {
// 	    try {
// 	        System.out.println("=== 開始執行 CSV 匯入測試 ===");
	        
// 	        // 從資源目錄讀取CSV檔案
// 	        String filePath = "/t53venueStatus.csv";  // 修正路徑，包含空格
// 	        System.out.println("準備讀取檔案: " + filePath);
// 	        ClassPathResource resource = new ClassPathResource(filePath);
	        
// 	        // 檢查檔案是否存在
// 	        if (!resource.exists()) {
// 	            System.out.println("錯誤：找不到檔案！");
// 	            System.out.println("嘗試讀取的完整路徑: " + resource.getFile().getAbsolutePath());
// 	            return;
// 	        }
	        
// 	        File csvFile = resource.getFile();
// 	        System.out.println("檔案成功讀取，大小: " + csvFile.length() + " bytes");

// 	        // 讀取檔案內容並印出前幾行來確認格式
// 	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
// 	            System.out.println("\n檔案內容預覽：");
// 	            String line;
// 	            int lineCount = 0;
// 	            while ((line = reader.readLine()) != null && lineCount < 3) {
// 	                System.out.println(line);
// 	                lineCount++;
// 	            }
// 	        }

// 	        // 將File轉換為MultipartFile
// 	        System.out.println("\n轉換檔案格式...");
// 	        MultipartFile multipartFile = new MockMultipartFile(
// 	            "file",
// 	            "venueStatus.csv",
// 	            "text/csv",
// 	            new FileInputStream(csvFile)
// 	        );
// 	        System.out.println("檔案準備完成，開始執行匯入");

// 	        // 執行匯入
// 	        venueService.importFromCsv(multipartFile);
	        
// 	        System.out.println("=== CSV 匯入測試完成 ===");
	        
// 	    } catch (Exception e) {
// 	        System.out.println("\n*** 執行過程發生錯誤 ***");
// 	        System.out.println("錯誤類型: " + e.getClass().getName());
// 	        System.out.println("錯誤訊息: " + e.getMessage());
// 	        if (e.getCause() != null) {
// 	            System.out.println("造成原因: " + e.getCause().getMessage());
// 	        }
// 	        e.printStackTrace();
// 	    }
// 	}
// }