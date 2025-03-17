package tw.com.hippo_center_backend.h4dto.b1;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EventRequestDTO {
    
    @NotBlank(message = "活動名稱不得為空")
    @Size(min = 2, max = 100, message = "活動名稱長度須介於 2 到 100 字之間")
    private String eventName;
    
    @NotNull(message = "活動類別不得為空")
    private Integer eventCategoryId;
    
    @NotNull(message = "場地不得為空")
    private String venueId;
    
    @NotBlank(message = "活動描述不得為空")
    @Size(min = 10, max = 1000, message = "活動描述長度須介於 10到1000字之間")
    private String eventDescription1;
    
    @NotNull(message = "必須至少有一張活動照片")
    private List<String> imagePaths;
    
    @NotNull(message = "活動價格不得為空")
    @Min(value = 0, message = "價格不能為負數")
    @Max(value = 1000, message = "價格不能超過 1000")
    private Integer eventPrice;
    
    @NotNull(message = "活動開始日期不得為空")
    private LocalDate eventStartDate;
    
    @NotNull(message = "活動結束日期不得為空")
    private LocalDate eventEndDate;
    
    private Boolean isPublished = false;
    
    private LocalDate eventCreationDate;
    private LocalDate eventPublishDate;
    private LocalDate eventUpdateDate;
    
    // Getters and Setters
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public Integer getEventCategoryId() {
        return eventCategoryId;
    }
    
    public void setEventCategoryId(Integer eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }
    
    public String getVenueId() {
        return venueId;
    }
    
    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }
    
    public String getEventDescription1() {
        return eventDescription1;
    }
    
    public void setEventDescription1(String eventDescription1) {
        this.eventDescription1 = eventDescription1;
    }
    
    public List<String> getImagePaths() {
        return imagePaths;
    }
    
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
    
    public Integer getEventPrice() {
        return eventPrice;
    }
    
    public void setEventPrice(Integer eventPrice) {
        this.eventPrice = eventPrice;
    }
    
    public LocalDate getEventStartDate() {
        return eventStartDate;
    }
    
    public void setEventStartDate(LocalDate eventStartDate) {
        this.eventStartDate = eventStartDate;
    }
    
    public LocalDate getEventEndDate() {
        return eventEndDate;
    }
    
    public void setEventEndDate(LocalDate eventEndDate) {
        this.eventEndDate = eventEndDate;
    }
    
    public Boolean getIsPublished() {
        return isPublished;
    }
    
    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }
    
    public LocalDate getEventCreationDate() {
        return eventCreationDate;
    }
    
    public void setEventCreationDate(LocalDate eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
    }
    
    public LocalDate getEventPublishDate() {
        return eventPublishDate;
    }
    
    public void setEventPublishDate(LocalDate eventPublishDate) {
        this.eventPublishDate = eventPublishDate;
    }
    
    public LocalDate getEventUpdateDate() {
        return eventUpdateDate;
    }
    
    public void setEventUpdateDate(LocalDate eventUpdateDate) {
        this.eventUpdateDate = eventUpdateDate;
    }
}