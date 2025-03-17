// package tw.com.hippo_center_backend.h5test.venue;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import tw.com.hippo_center_backend.h0bean.T51VenueBean;
// import tw.com.hippo_center_backend.h2service.b5impl.A1VenueServiceImpl;

// @SpringBootTest
// public class UpdateServiceTests {
// 	@Autowired
// 	  private A1VenueServiceImpl venueService;

	
// 	@Test
// 	public void testUpdate() {
// 	     // 使用已存在的 ID 創建更新的 Bean
// 		T51VenueBean updateBean = new T51VenueBean();
	  
// 		System.out.println("1-");
// 	    updateBean.setVenueId("A104");
// 		System.out.println("2-");
// 		updateBean.setVenueName("a倉庫");
// 		System.out.println("3-");
// 		updateBean.setVenueDescription("演出、發表會、藝文展覽空間");
// 		System.out.println("4-");
// 		updateBean.setRentalStatue(true);
// 		System.out.println("5-");
// 		updateBean.setCapacity(999989);
// 		System.out.println("6-");
// 		updateBean.setAreaPings(9999299);
// 		System.out.println("7-");
// 		updateBean.setVenueHeight(4.9);
// 		System.out.println("8-");
// 		updateBean.setVenueInfo("2號倉庫(299坪)、3號倉庫(313坪)，挑高最低點4.9米，中軸線至尾為高約45公分之凸台，有獨立配電。23號倉庫為唯一相通之活動空間，總坪數可達600坪。");
// 		System.out.println("9-");
// 		updateBean.setUsageRecommendations("任何大型藝文演出、演唱會、發表會、展覽、車展等均可在此辦理。");
// 		System.out.println("10-");
// 		updateBean.setRentalRegulation(null);
// 		System.out.println("11-");
// 		updateBean.setTechnicalSpecifications(null);
// 		System.out.println("12-");
// 		updateBean.setVenueFeeByDay(88880);
// 		System.out.println("13-");
	    
// 		T51VenueBean upBean = venueService.update(updateBean);
// 		System.out.println("update="+upBean);
// 	}}

	