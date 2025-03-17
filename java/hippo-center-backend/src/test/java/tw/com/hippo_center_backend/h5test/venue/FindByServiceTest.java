// package tw.com.hippo_center_backend.h5test.venue;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import tw.com.hippo_center_backend.h0bean.T51VenueBean;
// import tw.com.hippo_center_backend.h2service.b5impl.C1VenueFindServiceImpl;
// @SpringBootTest
// public class FindByServiceTest {
// 	@Autowired
// 	private C1VenueFindServiceImpl findService;
// 	@Test
// 	public void testFindAllAvailableVenues() {
// 		// 查詢可租用的場地
// 	    List<T51VenueBean> availableVenues = findService.findAllAvailableVenues();
// 	    System.out.println("查詢結果筆數: " + (availableVenues != null ? availableVenues.size() : 0));
// 	    System.out.println("availableVenues=" +availableVenues);
// 	    if (availableVenues != null) {
// 	        System.out.println("=== 查詢結果明細 ===");
// 	        for (T51VenueBean venue : availableVenues) {
// 	            System.out.println("場地ID: " + venue.getVenueId());
// 	            System.out.println("場地名稱: " + venue.getVenueName());
// 	            System.out.println("租借狀態: " + venue.getRentalStatue());
// 	            System.out.println("--------------------");
// 	        }
// 	    }
// 	    System.out.println("開始驗證結果...");
// 	    assertNotNull(availableVenues, "查詢結果不應為 null");
// 	    assertEquals(2, availableVenues.size(), "應該有 2 筆可租借場地");
// 	    boolean allAvailable = availableVenues.stream()
// 	            .allMatch(T51VenueBean::getRentalStatue);
// 	    System.out.println("是否所有場地都可租借: " + allAvailable);
// 	    assertTrue(allAvailable, "所有回傳的場地都應該是可租借的");
	    
// 	    System.out.println("=== 測試完成 ===");
// }
	
// }
