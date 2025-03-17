// package tw.com.hippo_center_backend.h5test.VenueBooking;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import tw.com.hippo_center_backend.h0bean.T52BookingBean;
// import tw.com.hippo_center_backend.h2service.b5impl.A2VenueBookingFindServiceImpl;
// @SpringBootTest
// public class VenueBookingUpdateTest {

// 	@Autowired
//     private A2VenueBookingFindServiceImpl bookingService;
     
// //    @Test
//     void testUpdate() {
//         T52BookingBean existingBooking = bookingService.findById("BK1F987A27");
//         String originalOrganizer = existingBooking.getOrganizer();
        
//         existingBooking.setOrganizer("新主辦單位");
//         existingBooking.setEventName("新活動名稱");
//         T52BookingBean updated = bookingService.update(existingBooking);
        
//         assertNotNull(updated);
//         assertEquals("新主辦單位", updated.getOrganizer());
//         assertNotNull(updated.getBookingUpdatedDatetime());
        
//         existingBooking.setOrganizer(originalOrganizer);
//         bookingService.update(existingBooking);
//     }
    
// //    @Test
//     void testExists() {
//         assertTrue(bookingService.exists("BK12345678"));
//         assertFalse(bookingService.exists("BKXXXXXXXX"));
//     }
    
// //    @Test
//     void testFindById() {
//         T52BookingBean booking = bookingService.findById("BK12345678");
//         assertNotNull(booking);
//         assertNotNull(booking.getBookingId());
//         assertNotNull(booking.getMemberId());
//         assertNotNull(booking.getVenue());
//     }
    
// //    @Test
//     void testFindAll() {
//         List<T52BookingBean> allResults = bookingService.findAll(new T52BookingBean());
//         assertNotNull(allResults);
//         assertFalse(allResults.isEmpty());
//         System.out.println("Total bookings found: " + allResults.size());
        
//         for(T52BookingBean booking : allResults) {
//             assertNotNull(booking.getBookingId());
//             System.out.println("Booking ID: " + booking.getBookingId());
//             System.out.println("Member ID: " + booking.getMemberId());
//             System.out.println("Venue: " + booking.getVenue());
//             System.out.println("Order Status: " + booking.getOrderStatus());
//             System.out.println("-------------------");
//         }
//     }
    
//     @Test
//     void testFindByStatus() {
//         // 測試送出申請狀態
//         List<T52BookingBean> submittedBookings = bookingService.findByStatus("送出申請");
//         assertNotNull(submittedBookings);
//         submittedBookings.forEach(booking -> {
//             assertEquals("送出申請", booking.getOrderStatus());
//             System.out.println("送出申請 - Booking ID: " + booking.getBookingId());
//         });
        
//     }
    
// }
