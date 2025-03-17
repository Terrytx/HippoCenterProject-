package tw.com.hippo_center_backend.h0bean;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "t54_lostItem")
public class T54LostItemBean {
	@Id
	@Column(name = "lost_item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer lostItemId;  // 1 遺失物編號

	@Column(name = "item_name")
	private String itemName;  // 2 物品名稱

	@Column(name = "item_description")
	private String itemDescription;  // 3 物品描述
	
	@Column(name = "lost_date")
	private Date lostDate;  // 4 丟失日期
	
	@Column(name = "found_location")
	private String foundLocation;  // 5 拾獲地點
	
	@Column(name = "found_datetime")
	private LocalDateTime foundDatetime ;  // 6 拾獲時間
	
	@Column(name = "contact_name")
	private String contactName ;  // 7 拾獲姓名
	
	@Column(name = "contact_phone")
	private String contactPhone;  // 8 拾獲電話
	
	@Column(name = "claimant_name")
	private String claimantName;  // 9 領取人姓名
	
	@Column(name = "claimant_phone")
	private String claimantPhone;  // 10 領取人電話
	
	@Column(name = "claim_datetime")
	private LocalDateTime claimDatetime;  // 11 領取時間
	
	@Column(name = "claim_status")
	private String claimStatus;  // 12 招領狀態
	
	@Column(name = "item_photo_video")
	private Blob itemPhotoVideo;  // 13 物品照片或影像

	public Integer getLostItemId() {
		return lostItemId;
	}

	public void setLostItemId(Integer lostItemId) {
		this.lostItemId = lostItemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public Date getLostDate() {
		return lostDate;
	}

	public void setLostDate(Date lostDate) {
		this.lostDate = lostDate;
	}

	public String getFoundLocation() {
		return foundLocation;
	}

	public void setFoundLocation(String foundLocation) {
		this.foundLocation = foundLocation;
	}

	public LocalDateTime getFoundDatetime() {
		return foundDatetime;
	}

	public void setFoundDatetime(LocalDateTime foundDatetime) {
		this.foundDatetime = foundDatetime;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getClaimantName() {
		return claimantName;
	}

	public void setClaimantName(String claimantName) {
		this.claimantName = claimantName;
	}

	public String getClaimantPhone() {
		return claimantPhone;
	}

	public void setClaimantPhone(String claimantPhone) {
		this.claimantPhone = claimantPhone;
	}

	public LocalDateTime getClaimDatetime() {
		return claimDatetime;
	}

	public void setClaimDatetime(LocalDateTime claimDatetime) {
		this.claimDatetime = claimDatetime;
	}

	public String getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(String claimStatus) {
		this.claimStatus = claimStatus;
	}

	public Blob getItemPhotoVideo() {
		return itemPhotoVideo;
	}

	public void setItemPhotoVideo(Blob itemPhotoVideo) {
		this.itemPhotoVideo = itemPhotoVideo;
	}

	@Override
	public String toString() {
		return "T54_LostItemBean [lostItemId=" + lostItemId + ", itemName=" + itemName + ", itemDescription="
				+ itemDescription + ", lostDate=" + lostDate + ", foundLocation=" + foundLocation + ", foundDatetime="
				+ foundDatetime + ", contactName=" + contactName + ", contactPhone=" + contactPhone + ", claimantName="
				+ claimantName + ", claimantPhone=" + claimantPhone + ", claimDatetime=" + claimDatetime
				+ ", claimStatus=" + claimStatus + ", itemPhotoVideo=" + itemPhotoVideo + "]";
	}

	public T54LostItemBean() {
		super();
		}

	public T54LostItemBean(Integer lostItemId, String itemName, String itemDescription, Date lostDate,
			String foundLocation, LocalDateTime foundDatetime, String contactName, String contactPhone,
			String claimantName, String claimantPhone, LocalDateTime claimDatetime, String claimStatus,
			Blob itemPhotoVideo) {
		super();
		this.lostItemId = lostItemId;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.lostDate = lostDate;
		this.foundLocation = foundLocation;
		this.foundDatetime = foundDatetime;
		this.contactName = contactName;
		this.contactPhone = contactPhone;
		this.claimantName = claimantName;
		this.claimantPhone = claimantPhone;
		this.claimDatetime = claimDatetime;
		this.claimStatus = claimStatus;
		this.itemPhotoVideo = itemPhotoVideo;
	}
	
	
	
}
