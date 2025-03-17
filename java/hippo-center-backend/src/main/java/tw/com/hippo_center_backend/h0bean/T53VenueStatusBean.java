package tw.com.hippo_center_backend.h0bean;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t53_venue_status")
public class T53VenueStatusBean {
	@Id
	@Column(name = "venue_status_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer venueStatusId;  // 1 館場狀態編號

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", referencedColumnName = "venue_id")
	@JsonBackReference
	private T51VenueBean  venue;  // 2 館場編號 -FK-t51
	 
	@Column(name = "date")
	private LocalDate date; // 3 日期
	
	@Column(name = "status")
	private String status; // 4 使用狀態-可空值 (已出租/ 休館日/ 場地維護日)
	
	@Column(name = "booking_id")
	private Integer bookingId;  // 5 租借訂單編號 FK - t52 (可空值)
	
	@Column(name = "created_time", updatable = false)
	private LocalDateTime createdTime;  // 6 建立時間
	
	@Column(name = "updated_time")
	private LocalDateTime  updatedTime; // 7 更新時間
	
	@Column(name = "note")
	private String note;

	public Integer getVenueStatusId() {
		return venueStatusId;
	}

	public void setVenueStatusId(Integer venueStatusId) {
		this.venueStatusId = venueStatusId;
	}

	

	public T51VenueBean getVenue() {
		return venue;
	}

	public void setVenue(T51VenueBean venue) {
		this.venue = venue;
	}



	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "T53VenueStatusBean [venueStatusId=" + venueStatusId + ", venue=" + venue + ", date=" + date
				+ ", status=" + status + ", bookingId=" + bookingId + ", createdTime=" + createdTime + ", updatedTime="
				+ updatedTime + ", note=" + note + "]";
	}

	public T53VenueStatusBean(Integer venueStatusId, T51VenueBean venue, LocalDate date, String status,
			Integer bookingId, LocalDateTime createdTime, LocalDateTime updatedTime, String note) {
		super();
		this.venueStatusId = venueStatusId;
		this.venue = venue;
		this.date = date;
		this.status = status;
		this.bookingId = bookingId;
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
		this.note = note;
	}

	public T53VenueStatusBean() {
		// TODO Auto-generated constructor stub
	}




	
	
	
}
