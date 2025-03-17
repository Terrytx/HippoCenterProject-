package tw.com.hippo_center_backend.h0bean;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "t13_reservations")
public class T13ReservationBean {
	// 預約編號
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    // FK: 導覽編號
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tours_id", nullable = false)
	@JsonIgnore
	private T12ToursBean tours;

    // FK: 會員編號
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "member_id", nullable = false)
	private T41MemberBean member;

    // 預約人數
    @Column(name = "num_guests", nullable = false)
    private Integer numGuests;

    // 預約日期
    @Column(name = "reservation_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reservationDate;

    // 預約狀態
    @Column(name = "reservation_status", nullable = false, length = 50)
    private String reservationStatus;

    // 創建時間
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private Timestamp updatedAt;

	public Integer getReservationId() {
		return reservationId;
	}

	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}

	public T12ToursBean getTours() {
		return tours;
	}

	public void setTours(T12ToursBean tours) {
		this.tours = tours;
	}

	public T41MemberBean getMember() {
		return member;
	}

	public void setMember(T41MemberBean member) {
		this.member = member;
	}

	public Integer getNumGuests() {
		return numGuests;
	}

	public void setNumGuests(Integer numGuests) {
		this.numGuests = numGuests;
	}

	public Date getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(Date reservationDate) {
		this.reservationDate = reservationDate;
	}

	public String getReservationStatus() {
		return reservationStatus;
	}

	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public String getAccount() {
	    return member != null ? member.getAccount() : null;
	}

	@Override
	public String toString() {
		return "T13ReservationBean [reservationId=" + reservationId + ", tours=" + tours + ", member=" + member
				+ ", numGuests=" + numGuests + ", reservationDate=" + reservationDate + ", reservationStatus="
				+ reservationStatus + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
