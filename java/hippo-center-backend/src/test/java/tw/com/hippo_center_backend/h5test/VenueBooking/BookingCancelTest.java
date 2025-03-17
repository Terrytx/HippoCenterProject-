// package tw.com.hippo_center_backend.h5test.VenueBooking;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import tw.com.hippo_center_backend.h0bean.T52BookingBean;
// import tw.com.hippo_center_backend.h1repository.T52BookingRepository;
// import tw.com.hippo_center_backend.h2service.b5impl.A2VenueBookingFindServiceImpl;

// @SpringBootTest
// public class BookingCancelTest {
// 	 	@Autowired
// 	    private A2VenueBookingFindServiceImpl bookingService;

// 	    @Autowired
// 	    private T52BookingRepository bookingRepository;

// 	    @Test
// 	    void cancelBooking_WhenBookingExists_ShouldCancelBooking() {
// 	        // Arrange
// 	        T52BookingBean booking = new T52BookingBean();
// 	        booking.setBookingId("BK0F37584C");
// 	        booking.setOrderStatus("PROCESSING");
// 	        booking = bookingRepository.save(booking);

// 	        // Act
// 	        T52BookingBean result = bookingService.cancelBooking(booking.getBookingId());

// 	        // Assert
// 	        assertEquals("CANCELLED", result.getOrderStatus());
// 	        assertNotNull(result.getBookingUpdatedDatetime());
// 	    }
// }
