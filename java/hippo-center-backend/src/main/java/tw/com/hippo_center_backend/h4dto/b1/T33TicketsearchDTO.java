package tw.com.hippo_center_backend.h4dto.b1;

import java.time.LocalDate;

public class T33TicketsearchDTO {
    private Integer ticketId;
    private Integer ticketAmount;
    private String name;
    private String eventName;
    private String venueName;
    private Integer eventPrice;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private LocalDate purchaseDate;
    private String ticketStatus;

    // ✅ 修正建構子（移除 eventCategoryName & eventCategoryType）
    public T33TicketsearchDTO(Integer ticketId, Integer ticketAmount, String name, String eventName, 
                               String venueName, Integer eventPrice, LocalDate eventStartDate, 
                               LocalDate eventEndDate, LocalDate purchaseDate, String ticketStatus) {
        this.ticketId = ticketId;
        this.ticketAmount = ticketAmount;
        this.name = name;
        this.eventName = eventName;
        this.venueName = venueName;
        this.eventPrice = eventPrice;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.purchaseDate = purchaseDate;
        this.ticketStatus = ticketStatus;
    }

    // ✅ Getters（無需 Setters，因為 DTO 主要用於回傳）
    public Integer getTicketId() { return ticketId; }
    public Integer getTicketAmount() { return ticketAmount; }
    public String getName() { return name; }
    public String getEventName() { return eventName; }
    public String getVenueName() { return venueName; }
    public Integer getEventPrice() { return eventPrice; }
    public LocalDate getEventStartDate() { return eventStartDate; }
    public LocalDate getEventEndDate() { return eventEndDate; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public String getTicketStatus() { return ticketStatus; }
}
