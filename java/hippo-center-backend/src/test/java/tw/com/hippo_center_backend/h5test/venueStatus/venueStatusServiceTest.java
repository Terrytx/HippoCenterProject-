// package tw.com.hippo_center_backend.h5test.venueStatus;

// import java.time.LocalDate;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.annotation.Rollback;

// import jakarta.transaction.Transactional;
// import tw.com.hippo_center_backend.h0bean.T51VenueBean;
// import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
// import tw.com.hippo_center_backend.h2service.b5impl.A3VenueStatusServiceImpl;

// @SpringBootTest
// @Transactional
// @Rollback(false)  // 防止測試後自動回滾
// public class venueStatusServiceTest {
//     @Autowired
//     private A3VenueStatusServiceImpl venueStatusService;
    
// //    @Test
//     void testCreateVenueStatus() {
//         // 1. 準備測試資料
//         System.out.println("=== 開始測試新增場地狀態 ===");
        
//         // 2. 先取得一個現有的場地
//         T51VenueBean venue = new T51VenueBean();
//         venue.setVenueId("A02");
        
//         // 3. 創建新的場地狀態物件
//         T53VenueStatusBean venueStatus = new T53VenueStatusBean();
//         venueStatus.setVenue(venue);
//         venueStatus.setDate(LocalDate.of(2025, 1, 22));  // 設定特定日期 2025-01-18
//         venueStatus.setStatus("已...");
//         // 4. 執行新增
//         T53VenueStatusBean create = venueStatusService.createVenueStatus(venueStatus);

//         System.out.println("Test準備新增的場地狀態資料：" + create);

//     }
    
//  //   @Test
//     void testUpdateVenueStatus() {
//     	// 1. 準備測試資料
//     	System.out.println("=== 開始測試新增場地狀態 ===");
    	
//     	// 2. 先取得一個現有的場地
//     	T51VenueBean venue = new T51VenueBean();
    	
//     	venue.setVenueId("A01");
    	
//     	// 3. 創建新的場地狀態物件
//     	T53VenueStatusBean venueStatus = new T53VenueStatusBean();
//     	venueStatus.setVenueStatusId(23);
//     	venueStatus.setVenue(venue);
//     	venueStatus.setDate(LocalDate.of(2025, 1, 19));  // 設定特定日期 2025-01-18
//     	venueStatus.setStatus("出資出租");
//     	// 4. 執行新增
//     	T53VenueStatusBean update = venueStatusService.updateVenueStatus(venueStatus);
    	
//     	System.out.println("Test準備更新的場地狀態資料：" + update);
    	
//     }
//     @Test
//     void testDeleteVenueStatus() {
//        	System.out.println("=== 開始測試新增場地狀態 ===");
//         // 1. 確認要刪除的記錄是否存在
//         T53VenueStatusBean venueStatus = new T53VenueStatusBean();
//         venueStatus.setVenueStatusId(20);
        
//         // 2. 執行刪除
//         boolean result = venueStatusService.delete(venueStatus);
        
//         // 3. 驗證刪除結果
//         System.out.println("刪除結果：" + result);
    
//     }
// }