package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t51_venue")
public class T51VenueBean {
	@Id
	@Column(name = "venue_id")
	private String venueId;  // 1 館場編號
	 // 加入 OneToMany 關聯
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)  //cascade = CascadeType.ALL 表示對場地的操作會級聯到相關的圖片
    @JsonBackReference
    private List<T14VenueImagesBean> venueImages;   // 館場圖片
	
    @OneToMany(mappedBy = "venue")
    @JsonManagedReference
    private List<T53VenueStatusBean> venueStatuses;  // 館場使用狀態
	
	@Column(name = "venue_name")
	private String venueName;  // 2 館場名稱
	
	@Column(name = "venue_description")
	private String venueDescription;  // 3 館場描述
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;  // 4 創建時間
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;  // 5 更新時間
	
	@Column(name = "x_coordinate")
	private Integer xCoordinate;    // 6 x軸(像素座標)
	
	@Column(name = "y_coordinate")
	private Integer yCoordinate;   // 7 y軸(像素座標)
	
	@Column(name = "floor")
	private Integer floor;   // 8 圖層
	
	@Column(name = "rental_statue")
	private Boolean rentalStatue;  // 9 是否提供租借(狀態)
	
	@Column(name = "capacity")
	private Integer capacity;  // 10 容納人數
	
	@Column(name = "area_pings")
	private Integer areaPings;  // 11 場地坪數
	
	@Column(name = "venue_height")
	private Double venueHeight;  // 12 場地高度
	
	@Column(name = "venue_info")
	private String venueInfo;   // 13 場地租用簡介!! 前面已有描述
	
	@Column(name = "usage_recommendations")
	private String usageRecommendations;  // 14 使用建議
	
	@Lob
	@Column(name = "rental_regulation")
	private byte[] rentalRegulation;  // 15 場地使用規章(檔案)
	
	@Lob
	@Column(name = "technical_specifications")
	private byte[] technicalSpecifications;  // 16 場地技術資料
	
	@Column(name = "venue_fee_by_day")
	private Integer venueFeeByDay;  // 17 每日場地費

	public String getVenueId() {
		return venueId;
	}

	public void setVenueId(String venueId) {
		this.venueId = venueId;
	}
	
	
	
	public List<T14VenueImagesBean> getVenueImages() {
		return venueImages;
	}

	public void setVenueImages(List<T14VenueImagesBean> venueImages) {
		this.venueImages = venueImages;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public String getVenueDescription() {
		return venueDescription;
	}

	public void setVenueDescription(String venueDescription) {
		this.venueDescription = venueDescription;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	


	public Integer getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(Integer xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public Integer getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(Integer yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public Boolean getRentalStatue() {
		return rentalStatue;
	}

	public void setRentalStatue(Boolean rentalStatue) {
		this.rentalStatue = rentalStatue;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getAreaPings() {
		return areaPings;
	}

	public void setAreaPings(Integer areaPings) {
		this.areaPings = areaPings;
	}

	public Double getVenueHeight() {
		return venueHeight;
	}

	public void setVenueHeight(Double venueHeight) {
		this.venueHeight = venueHeight;
	}

	public String getVenueInfo() {
		return venueInfo;
	}

	public void setVenueInfo(String venueInfo) {
		this.venueInfo = venueInfo;
	}

	public String getUsageRecommendations() {
		return usageRecommendations;
	}

	public void setUsageRecommendations(String usageRecommendations) {
		this.usageRecommendations = usageRecommendations;
	}

	public byte[] getRentalRegulation() {
		return rentalRegulation;
	}

	public void setRentalRegulation(byte[] rentalRegulation) {
		this.rentalRegulation = rentalRegulation;
	}

	public byte[] getTechnicalSpecifications() {
		return technicalSpecifications;
	}

	public void setTechnicalSpecifications(byte[] technicalSpecifications) {
		this.technicalSpecifications = technicalSpecifications;
	}

	public Integer getVenueFeeByDay() {
		return venueFeeByDay;
	}

	public void setVenueFeeByDay(Integer venueFeeByDay) {
		this.venueFeeByDay = venueFeeByDay;
	}


	
	public T51VenueBean(String venueId, String venueName, String venueDescription, LocalDateTime createdAt,
			LocalDateTime updatedAt, Integer xCoordinate, Integer yCoordinate, Integer floor, Boolean rentalStatue,
			Integer capacity, Integer areaPings, Double venueHeight, String venueInfo, String usageRecommendations,
			byte[] rentalRegulation, byte[] technicalSpecifications, Integer venueFeeByDay) {
		super();
		this.venueId = venueId;
		this.venueName = venueName;
		this.venueDescription = venueDescription;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.floor = floor;
		this.rentalStatue = rentalStatue;
		this.capacity = capacity;
		this.areaPings = areaPings;
		this.venueHeight = venueHeight;
		this.venueInfo = venueInfo;
		this.usageRecommendations = usageRecommendations;
		this.rentalRegulation = rentalRegulation;
		this.technicalSpecifications = technicalSpecifications;
		this.venueFeeByDay = venueFeeByDay;
	}

	public T51VenueBean() {
		super();
			}

	@Override
	public String toString() {
		return "T51VenueBean [venueId=" + venueId + ", venueName=" + venueName + ", venueDescription="
				+ venueDescription + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", xCoordinate="
				+ xCoordinate + ", yCoordinate=" + yCoordinate + ", floor=" + floor + ", rentalStatue=" + rentalStatue
				+ ", capacity=" + capacity + ", areaPings=" + areaPings + ", venueHeight=" + venueHeight
				+ ", venueInfo=" + venueInfo + ", usageRecommendations=" + usageRecommendations + ", rentalRegulation="
				+ Arrays.toString(rentalRegulation) + ", technicalSpecifications="
				+ Arrays.toString(technicalSpecifications) + ", venueFeeByDay=" + venueFeeByDay + "]";
	}

	
	
	
}
