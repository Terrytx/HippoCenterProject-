package tw.com.hippo_center_backend.h0bean;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t12_tours")
public class T12ToursBean {
	// 導覽編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tours_id")
	private Integer toursId;
	
	// 導覽名稱
	@Column(name = "tours_name", nullable = false, length = 255)
	private String toursName;
	
	// 導覽日期
	@Column(name = "tours_date", nullable = false)
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private LocalDate toursDate;
	
	// 導覽說明
	@Column(name = "tours_description", length = 500)
	private String toursDescription;
	
	// 導覽場次
	@Column(name = "tours_session", nullable = false, length = 100)
	private String toursSession;
	
	// 時間區段
	@Column(name = "time_slot", nullable = false, length = 100)
	private String timeSlot;
	
	// 可預約人數
	@Column(name = "capacity", nullable = false)
	private Integer capacity;
	
	// 導覽狀態
	@Column(name = "tours_status", nullable = false, length = 50)
	private String toursStatus;
	
	// 創建時間
	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp createdAt;
	
	// 更新時間
	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp updatedAt;

	@OneToMany(mappedBy = "tours", cascade = {}, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<T13ReservationBean> reservations;

	public Integer getToursId() {
		return toursId;
	}

	public void setToursId(Integer toursId) {
		this.toursId = toursId;
	}

	public String getToursName() {
		return toursName;
	}

	public void setToursName(String toursName) {
		this.toursName = toursName;
	}

	public LocalDate getToursDate() {
		return toursDate;
	}

	public void setToursDate(LocalDate toursDate) {
		this.toursDate = toursDate;
	}

	public String getToursDescription() {
		return toursDescription;
	}

	public void setToursDescription(String toursDescription) {
		this.toursDescription = toursDescription;
	}


	public String getToursSession() {
		return toursSession;
	}

	public void setToursSession(String toursSession) {
		this.toursSession = toursSession;
	}

	public void setSession(String session) {
		this.toursSession = session;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getToursStatus() {
		return toursStatus;
	}

	public void setToursStatus(String toursStatus) {
		this.toursStatus = toursStatus;
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

	public List<T13ReservationBean> getReservations() {
		return reservations;
	}

	public void setReservations(List<T13ReservationBean> reservations) {
		this.reservations = reservations;
	}

	@Override
	public String toString() {
		return "T12ToursBean [toursId=" + toursId + ", toursName=" + toursName + ", toursDate=" + toursDate
				+ ", toursDescription=" + toursDescription + ", toursSession=" + toursSession + ", timeSlot=" + timeSlot
				+ ", capacity=" + capacity + ", toursStatus=" + toursStatus + "]";
	}
	
}
