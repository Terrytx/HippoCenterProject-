// package tw.com.hippo_center_backend.h5test.venue;

// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import tw.com.hippo_center_backend.h0bean.T51VenueBean;
// import tw.com.hippo_center_backend.h2service.b5impl.C1VenueFindServiceImpl;
// import tw.com.hippo_center_backend.h2service.b5impl.A1VenueServiceImpl;

// @SpringBootTest
// public class FindServiceTests {
//    @Autowired
//     private A1VenueServiceImpl venueService;
//    @Autowired
//    private C1VenueFindServiceImpl venuefindService;
//    @Test
//    public void testFindall() {
// 		List<T51VenueBean> selects1 = venueService.findAll(null);
// 	    assertNotNull(selects1);
// 		System.err.println("findall=null;products="+selects1);
		
// 		T51VenueBean bean = new T51VenueBean();
// 		bean.setVenueId("A01");
// 		List<T51VenueBean> selects2 = venueService.findAll(bean);
// 		System.err.println("findall=A01;products="+selects2);
// 		System.out.println("----------");
// 	}
//    @Test
// 	public void testFindById() {
// 		T51VenueBean findById1 = venueService.findById("A01");
// 		System.out.println("findById1="+findById1);
		
// 		T51VenueBean findById2 = venueService.findById("A02");
// 		System.out.println("findById2="+findById2);
		
		
// 		System.out.println("----------");
// 	}
	
// }

