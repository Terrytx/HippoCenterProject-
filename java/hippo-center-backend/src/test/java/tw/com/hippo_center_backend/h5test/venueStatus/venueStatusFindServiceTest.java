// package tw.com.hippo_center_backend.h5test.venueStatus;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
// import tw.com.hippo_center_backend.h2service.b5impl.C3VenueStatusFindService;

// @SpringBootTest
// public class venueStatusFindServiceTest {
// 	@Autowired
//     private C3VenueStatusFindService venueStatusFindService;
// //    @Test
// 	void testFindByVenueId() {
// 	    System.out.println("=== 開始測試查詢場地狀態 ===");

	    
// 	    // 2. 執行查詢
// 	    List<T53VenueStatusBean> results = venueStatusFindService.findByVenueId("A01");

// 	    System.out.println("=== 查詢結果 ===");
// 	    if (results == null) {
// 	        System.out.println("results 為 null");
// 	    } else if (results.isEmpty()) {
// 	        System.out.println("results 為空列表");
// 	    } else {
// 	        System.out.println("查詢到 " + results.size() + " 筆資料：");
// 	        for (int i = 0; i < results.size(); i++) {
// 	            T53VenueStatusBean status = results.get(i);
// 	            System.out.println("\n第 " + (i + 1) + " 筆資料：");
// 	            System.out.println("狀態ID: " + status.getVenueStatusId());
// 	            System.out.println("場地ID: " + (status.getVenue() != null ? status.getVenue().getVenueId() : "null"));
// 	            System.out.println("日期: " + status.getDate());
// 	            System.out.println("狀態: " + status.getStatus());
// 	            System.out.println("建立時間: " + status.getCreatedTime());
// 	            System.out.println("更新時間: " + status.getUpdatedTime());
// 	        }
// 	    }
	    
// 	    assertNotNull(results);
// 	    assertFalse(results.isEmpty());
// 	    assertEquals(3, results.size());
// 	}
//     @Test
//     void testGetMonthlyAvailability() {
//         // 執行測試
//         List<T53VenueStatusBean> result = venueStatusFindService.getMonthlyAvailability(2025, 1);
        
//         // 印出結果
//         System.out.println("查詢結果數量: " + result.size());
//         for (T53VenueStatusBean status : result) {
//             System.out.println("日期: " + status.getDate());
//         }
        
//         // 基本驗證
//         assertNotNull(result, "結果不應為 null");
//     }
// }
