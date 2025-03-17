// package tw.com.hippo_center_backend.h5test.VenueBooking;

// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import java.time.LocalDate;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import tw.com.hippo_center_backend.h0bean.T52BookingBean;
// import tw.com.hippo_center_backend.h2service.b5impl.C2VenueBookingServiceImpl;

// @SpringBootTest
// public class VenueBookingTest {
// 	 @Autowired
// 	    private C2VenueBookingServiceImpl bookingService;
// 	 @Test
// 	    void testInsert() {
// 	        T52BookingBean booking = new T52BookingBean();
// 	        booking.setMemberId(null);
// 	        booking.setVenue("A02");
// 	        booking.setRentalStartDatetime(LocalDate.of(2025, 1, 22));
// 	        booking.setRentalEndDatetime(LocalDate.of(2025, 1, 22));
// 	        booking.setEventName("測試活動");
// 	        booking.setOrganizer("測試單位");
// 	        booking.setContact("測試聯絡人");
// 	        booking.setContactPersonMobile("0912345678");
	        
// 	        T52BookingBean result = bookingService.insert(booking);
	        
// 	        assertNotNull(result);
// 	        assertNotNull(result.getBookingId());
// 	        assertNotNull(result.getBookingDatetime());
// 	    }
// }
