package tw.com.hippo_center_backend.h4dto.b1;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import tw.com.hippo_center_backend.h0bean.T34EventImagesBean;
import tw.com.hippo_center_backend.h0bean.T31EventBean;

public class EventDetailResponseDTO {
    private Integer eventId;
    private String eventName;
    private String eventCategoryName;
    private String venueName;
    private String eventDescription1;
    private List<EventImageDTO> images;
    private Integer eventPrice;
    private LocalDate eventCreationDate;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private Boolean isPublished;
    private LocalDate eventPublishDate;
    private LocalDate eventUpdateDate;

    // 構造函數
    public EventDetailResponseDTO(T31EventBean event) {
        this.eventId = event.getEventId();
        this.eventName = event.getEventName();
        this.eventCategoryName = event.getEventCategory() != null ? event.getEventCategory().getEventCategoryName()
                : null;
        this.venueName = event.getVenue() != null ? event.getVenue().getVenueName() : null;
        this.eventDescription1 = event.getEventDescription1();
        this.images = event.getImages() != null ? event.getImages().stream()
                .map(image -> {
                    EventImageDTO dto = new EventImageDTO();
                    dto.setImageUrl(image.getImageUrl());
                    dto.setIsCover(image.getIsCover());
                    return dto;
                })
                .collect(Collectors.toList())
                : Collections.emptyList();

        // this.images = event.getImages() != null ?
        // event.getImages().stream()
        // .map(T34EventImagesBean::getImageUrl)
        // .collect(Collectors.toList())
        // : Collections.emptyList(); // 確保回傳空陣列，而不是 null
        this.eventPrice = event.getEventPrice();
        this.eventCreationDate = event.getEventCreationDate();
        this.eventStartDate = event.getEventStartDate();
        this.eventEndDate = event.getEventEndDate();
        this.isPublished = event.getIsPublished();
        this.eventPublishDate = event.getEventPublishDate();
        this.eventUpdateDate = event.getEventUpdateDate();
    }

    // Getters and Setters
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventCategoryName() {
        return eventCategoryName;
    }

    public void setEventCategoryName(String eventCategoryName) {
        this.eventCategoryName = eventCategoryName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getEventDescription1() {
        return eventDescription1;
    }

    public void setEventDescription1(String eventDescription1) {
        this.eventDescription1 = eventDescription1;
    }

    public List<EventImageDTO> getImages() {
        return images;
    }

    public void setImages(List<EventImageDTO> images) {
        this.images = images;
    }

    public Integer getEventPrice() {
        return eventPrice;
    }

    public void setEventPrice(Integer eventPrice) {
        this.eventPrice = eventPrice;
    }

    public LocalDate getEventCreationDate() {
        return eventCreationDate;
    }

    public void setEventCreationDate(LocalDate eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
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