package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "t52_booking")
public class T52BookingBean {
	// @Id
	// @Column(name = "booking_id")
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	// private String bookingId; // 1 租借訂單編號

	@Id
	@Column(name = "booking_id", columnDefinition = "VARCHAR(36)")
	private String bookingId;

	@PrePersist
	protected void onCreate() {
		if (bookingId == null) {
			bookingId = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		}
	}

	@Column(name = "member_id")
	private Integer memberId; // 2 會員編號 FK ...

	@Column(name = "venue")
	private String venue; // 3 租借場地--多選

	@Transient // 標記此欄位不需要持久化
	private String venueName;

	@Column(name = "order_status")
	private String orderStatus; // 4 租借申請狀態(送出申請、審核中、審核通過、取消申請)

	@Column(name = "rental_start_date")
	private LocalDate rentalStartDatetime; // 5 租借開始時間

	@Column(name = "rental_end_date")
	private LocalDate rentalEndDatetime; // 6 租借結束時間

	@Column(name = "organizer")
	private String organizer; // 7 主辦單位

	@Column(name = "contact")
	private String contact; // 8 聯絡人

	@Column(name = "contact_mobile")
	private String contactPersonMobile; // 9 聯絡人手機

	@Column(name = "contact_email")
	private String contactEmail; // 10 聯絡人E-mail

	@Column(name = "event_name")
	private String eventName; // 11 活動名稱

	@Column(name = "event_type")
	private String eventType; // 12 活動類型

	@Column(name = "event_description")
	private String eventDescription; // 13 活動內容簡介

	@Column(name = "event_proposal")
	private byte[] eventProposal; // 14 活動企劃書

	@Column(name = "application_type")
	private String applicationType; // 15 申請案件類別

	@Column(name = "application_area")
	private String applicationArea; // 16 申請項目/範圍

	@Column(name = "ad_start_date")
	private LocalDate adStartDate; // 17 廣告開始時間

	@Column(name = "ad_end_date")
	private LocalDate adEndDate; // 18 廣告結束時間

	@Column(name = "ad_media_file")
	private byte[] adMediaFile; // 19 廣告刊登圖檔或影片

	@Column(name = "booking_datetime")
	private LocalDateTime bookingDatetime; // 20 表單建立日期 --時間戳記

	@Column(name = "remarks")
	private String remarks; // 21 備註

	@Column(name = "booking_updated_datetime")
	private LocalDateTime bookingUpdatedDatetime; // 5 更新時間 --1.23新增

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public LocalDate getRentalStartDatetime() {
		return rentalStartDatetime;
	}

	public void setRentalStartDatetime(LocalDate rentalStartDatetime) {
		this.rentalStartDatetime = rentalStartDatetime;
	}

	public LocalDate getRentalEndDatetime() {
		return rentalEndDatetime;
	}

	public void setRentalEndDatetime(LocalDate rentalEndDatetime) {
		this.rentalEndDatetime = rentalEndDatetime;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactPersonMobile() {
		return contactPersonMobile;
	}

	public void setContactPersonMobile(String contactPersonMobile) {
		this.contactPersonMobile = contactPersonMobile;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public byte[] getEventProposal() {
		return eventProposal;
	}

	public void setEventProposal(byte[] eventProposal) {
		this.eventProposal = eventProposal;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getApplicationArea() {
		return applicationArea;
	}

	public void setApplicationArea(String applicationArea) {
		this.applicationArea = applicationArea;
	}

	public LocalDate getAdStartDate() {
		return adStartDate;
	}

	public void setAdStartDate(LocalDate adStartDate) {
		this.adStartDate = adStartDate;
	}

	public LocalDate getAdEndDate() {
		return adEndDate;
	}

	public void setAdEndDate(LocalDate adEndDate) {
		this.adEndDate = adEndDate;
	}

	public byte[] getAdMediaFile() {
		return adMediaFile;
	}

	public void setAdMediaFile(byte[] adMediaFile) {
		this.adMediaFile = adMediaFile;
	}

	public LocalDateTime getBookingDatetime() {
		return bookingDatetime;
	}

	public void setBookingDatetime(LocalDateTime bookingDatetime) {
		this.bookingDatetime = bookingDatetime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getBookingUpdatedDatetime() {
		return bookingUpdatedDatetime;
	}

	public void setBookingUpdatedDatetime(LocalDateTime bookingUpdatedDatetime) {
		this.bookingUpdatedDatetime = bookingUpdatedDatetime;
	}

	public T52BookingBean(String bookingId, Integer memberId, String venue, String venueName, String orderStatus,
			LocalDate rentalStartDatetime, LocalDate rentalEndDatetime, String organizer, String contact,
			String contactPersonMobile, String contactEmail, String eventName, String eventType,
			String eventDescription, byte[] eventProposal, String applicationType, String applicationArea,
			LocalDate adStartDate, LocalDate adEndDate, byte[] adMediaFile, LocalDateTime bookingDatetime,
			String remarks, LocalDateTime bookingUpdatedDatetime) {
		super();
		this.bookingId = bookingId;
		this.memberId = memberId;
		this.venue = venue;
		this.venueName = venueName;
		this.orderStatus = orderStatus;
		this.rentalStartDatetime = rentalStartDatetime;
		this.rentalEndDatetime = rentalEndDatetime;
		this.organizer = organizer;
		this.contact = contact;
		this.contactPersonMobile = contactPersonMobile;
		this.contactEmail = contactEmail;
		this.eventName = eventName;
		this.eventType = eventType;
		this.eventDescription = eventDescription;
		this.eventProposal = eventProposal;
		this.applicationType = applicationType;
		this.applicationArea = applicationArea;
		this.adStartDate = adStartDate;
		this.adEndDate = adEndDate;
		this.adMediaFile = adMediaFile;
		this.bookingDatetime = bookingDatetime;
		this.remarks = remarks;
		this.bookingUpdatedDatetime = bookingUpdatedDatetime;
	}

	@Override
	public String toString() {
		return "T52BookingBean [bookingId=" + bookingId + ", memberId=" + memberId + ", venue=" + venue + ", venueName="
				+ venueName + ", orderStatus=" + orderStatus + ", rentalStartDatetime=" + rentalStartDatetime
				+ ", rentalEndDatetime=" + rentalEndDatetime + ", organizer=" + organizer + ", contact=" + contact
				+ ", contactPersonMobile=" + contactPersonMobile + ", contactEmail=" + contactEmail + ", eventName="
				+ eventName + ", eventType=" + eventType + ", eventDescription=" + eventDescription + ", eventProposal="
				+ Arrays.toString(eventProposal) + ", applicationType=" + applicationType + ", applicationArea="
				+ applicationArea + ", adStartDate=" + adStartDate + ", adEndDate=" + adEndDate + ", adMediaFile="
				+ Arrays.toString(adMediaFile) + ", bookingDatetime=" + bookingDatetime + ", remarks=" + remarks
				+ ", bookingUpdatedDatetime=" + bookingUpdatedDatetime + "]";
	}

	public T52BookingBean() {
		// TODO Auto-generated constructor stub
	}

	public Object getSelectedVenues() {
		// TODO Auto-generated method stub
		return null;
	}

}
