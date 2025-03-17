package tw.com.hippo_center_backend.h4dto.b1;

public class VenueDTO {
	
	private String venueName;
    private String venueDescription;
    private Integer capacity;
    private Boolean rentalStatue;
    private Integer venueFeeByDay;
    private String usageRecommendations;
    private Integer xCoordinate;
    private Integer yCoordinate;
    private Integer floor;
    private Integer areaPings;
    private Double venueHeight;
    private String venueInfo;

    // Getters and Setters
    // ... [所有字段的getter和setter方法]
	
    private String venueId;
    public String getVenueId() {
		return venueId;
	}
	public void setVenueId(String venueId) {
		this.venueId = venueId;
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
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public Boolean getRentalStatue() {
		return rentalStatue;
	}
	public void setRentalStatue(Boolean rentalStatue) {
		this.rentalStatue = rentalStatue;
	}
	public Integer getVenueFeeByDay() {
		return venueFeeByDay;
	}
	public void setVenueFeeByDay(Integer venueFeeByDay) {
		this.venueFeeByDay = venueFeeByDay;
	}
	public String getUsageRecommendations() {
		return usageRecommendations;
	}
	public void setUsageRecommendations(String usageRecommendations) {
		this.usageRecommendations = usageRecommendations;
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
	
}