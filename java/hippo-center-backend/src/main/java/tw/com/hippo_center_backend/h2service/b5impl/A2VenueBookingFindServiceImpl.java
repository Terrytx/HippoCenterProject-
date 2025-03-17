package tw.com.hippo_center_backend.h2service.b5impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h0bean.T52BookingBean;
import tw.com.hippo_center_backend.h1repository.T51VenueRepository;
import tw.com.hippo_center_backend.h1repository.T52BookingRepository;

@Service
public class A2VenueBookingFindServiceImpl {
	@Autowired
	private T52BookingRepository bookingRepository;
	@Autowired
	private T51VenueRepository venueRepository;

	public T52BookingBean update(T52BookingBean bean) {
		if (bean != null && bean.getBookingId() != null) {
			if (bookingRepository.existsById(bean.getBookingId())) {
				bean.setBookingUpdatedDatetime(LocalDateTime.now());
				T52BookingBean updatedBooking = bookingRepository.save(bean);
				setVenueName(updatedBooking);
				return updatedBooking;
			}
		}
		return null;
	}

	public T52BookingBean cancelBooking(String bookingId) { // 點選取消訂單，狀態 改成訂單已取消
		if (bookingId == null || bookingId.isBlank()) {
			throw new IllegalArgumentException("訂單ID不能為空");
		}

		T52BookingBean booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new EntityNotFoundException("找不到訂單: " + bookingId));

		// 檢查訂單是否已經取消
		if ("訂單已取消".equals(booking.getOrderStatus())) {
			throw new IllegalStateException("訂單已經取消");
		}

		// 設定訂單狀態為取消
		booking.setOrderStatus("訂單已取消");
		booking.setBookingUpdatedDatetime(LocalDateTime.now());

		T52BookingBean savedBooking = bookingRepository.save(booking);
		setVenueName(savedBooking);
		return bookingRepository.save(booking);
	}

	public T52BookingBean confirmBooking(String bookingId) { // 點選檔期確認，狀態改成 檔期已確認
		if (bookingId == null || bookingId.isBlank()) {
			throw new IllegalArgumentException("訂單ID不能為空");
		}

		T52BookingBean booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new EntityNotFoundException("找不到訂單: " + bookingId));

		// 檢查訂單是否已經確認
		if ("檔期已確認".equals(booking.getOrderStatus())) {
			throw new IllegalStateException("訂單已經確認");
		}

		// 設定訂單狀態為已確認
		booking.setOrderStatus("檔期已確認");
		booking.setBookingUpdatedDatetime(LocalDateTime.now());

		T52BookingBean savedBooking = bookingRepository.save(booking);
		setVenueName(savedBooking);
		return bookingRepository.save(booking);
	}

	public T52BookingBean changeToUnderReview(String bookingId) { // 點選檔期確認，狀態改成 檔期已確認
		if (bookingId == null || bookingId.isBlank()) {
			throw new IllegalArgumentException("訂單ID不能為空");
		}

		T52BookingBean booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new EntityNotFoundException("找不到訂單: " + bookingId));

		// 設定訂單狀態為已確認
		booking.setOrderStatus("審核中");
		booking.setBookingUpdatedDatetime(LocalDateTime.now());

		T52BookingBean savedBooking = bookingRepository.save(booking);
		setVenueName(savedBooking);
		return bookingRepository.save(booking);
	}

	public boolean exists(String id) {
		if (id != null) {
			return bookingRepository.existsById(id);
		}
		return false;
	}

	public T52BookingBean findById(String id) {
		if (id != null) {
			Optional<T52BookingBean> optional = bookingRepository.findById(id);
			if (optional.isPresent()) {
				T52BookingBean booking = optional.get();
				setVenueName(booking);
				return booking;
			}
		}
		return null;
	}

	public List<T52BookingBean> findAll(T52BookingBean bean) {
		List<T52BookingBean> result = null;
		if (bean != null && bean.getBookingId() != null && !bean.getBookingId().equals(0)) {
			Optional<T52BookingBean> optional = bookingRepository.findById(bean.getBookingId());
			if (optional.isPresent()) {
				T52BookingBean booking = optional.get();
				setVenueName(booking); // 修改這裡：使用取出的 booking 物件
				result = new ArrayList<T52BookingBean>();
				result.add(booking);
			}
		} else {
			result = bookingRepository.findAll();
			for (T52BookingBean booking : result) {
				setVenueName(booking);
			}
		}
		return result;

	}

	public List<T52BookingBean> smartSearch(String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return new ArrayList<>();
		}

		// 在參數中加入 % 符號
		String searchPattern = "%" + searchText + "%";
		List<T52BookingBean> result = bookingRepository.findBySmartSearch(searchPattern);

		for (T52BookingBean booking : result) {
			setVenueName(booking);
		}

		return result;
	}

	public List<T52BookingBean> findByStatus(String status) {
		if (status != null && !status.trim().isEmpty()) {
			List<T52BookingBean> result = bookingRepository.findByOrderStatus(status);
			for (T52BookingBean booking : result) {
				setVenueName(booking);
			}
			return result;
		}
		return Collections.emptyList();
	}

	public void setVenueName(T52BookingBean booking) {
		if (booking != null && booking.getVenue() != null) {
			String[] venueIds = booking.getVenue().split(",");
			List<String> venueNames = new ArrayList<>();

			for (String venueId : venueIds) {
				// 去除可能的空白
				String trimmedId = venueId.trim();
				Optional<T51VenueBean> venue = venueRepository.findById(trimmedId);
				if (venue.isPresent()) {
					venueNames.add(trimmedId + " (" + venue.get().getVenueName() + ")");
				}
			}

			// 將場地名稱用逗號連接
			if (!venueNames.isEmpty()) {
				booking.setVenueName(String.join("\n", venueNames));
			}
		}

	}
}
