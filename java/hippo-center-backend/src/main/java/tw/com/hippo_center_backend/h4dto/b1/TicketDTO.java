package tw.com.hippo_center_backend.h4dto.b1;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class TicketDTO {
	//購瞟請求dto
    public static class TicketRequest {
    	
    	@NotNull(message = "會員ID不得為空")
        private Integer memberId;

        @NotNull(message = "活動ID不得為空")
        private Integer eventId;

        @NotNull(message = "購買張數不得為空")
        @Min(value = 1, message = "至少購買一張")
        private Integer ticketAmount;

        private String promotionMemberId;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
    	
        public Integer getMemberId() {
			return memberId;
		}

		public void setMemberId(Integer memberId) {
			this.memberId = memberId;
		}

		public Integer getEventId() {
			return eventId;
		}

		public void setEventId(Integer eventId) {
			this.eventId = eventId;
		}

		public Integer getTicketAmount() {
			return ticketAmount;
		}

		public void setTicketAmount(Integer ticketAmount) {
			this.ticketAmount = ticketAmount;
		}

		public String getPromotionMemberId() {
			return promotionMemberId;
		}

		public void setPromotionMemberId(String promotionMemberId) {
			this.promotionMemberId = promotionMemberId;
		}

		
    }
    //購票詳細資訊dto
    public static class TicketResponse {
        private Integer ticketId;
        private Integer ticketAmount;
        private String memberName;
        private String eventName;
        private String promotionTitle;
        private LocalDate purchaseDate;
        private Integer totalPrice;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
        
        public Integer getTicketId() {
			return ticketId;
		}
		public void setTicketId(Integer ticketId) {
			this.ticketId = ticketId;
		}
		public Integer getTicketAmount() {
			return ticketAmount;
		}
		public void setTicketAmount(Integer ticketAmount) {
			this.ticketAmount = ticketAmount;
		}
		public String getMemberName() {
			return memberName;
		}
		public void setMemberName(String memberName) {
			this.memberName = memberName;
		}
		public String getEventName() {
			return eventName;
		}
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		public String getPromotionTitle() {
			return promotionTitle;
		}
		public void setPromotionTitle(String promotionTitle) {
			this.promotionTitle = promotionTitle;
		}
		public LocalDate getPurchaseDate() {
			return purchaseDate;
		}
		public void setPurchaseDate(LocalDate purchaseDate) {
			this.purchaseDate = purchaseDate;
		}
		public Integer getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(Integer totalPrice) {
			this.totalPrice = totalPrice;
		}
		
    }

    public static class TicketListResponse {
    	
    	private Integer ticketId;
        private String eventName;
        private LocalDate eventDate;
        private Integer ticketAmount;
        private LocalDate purchaseDate;
        private String promotionStatus;
        private Integer finalPrice;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
    	
        public Integer getTicketId() {
			return ticketId;
		}
		public void setTicketId(Integer ticketId) {
			this.ticketId = ticketId;
		}
		public String getEventName() {
			return eventName;
		}
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		public LocalDate getEventDate() {
			return eventDate;
		}
		public void setEventDate(LocalDate eventDate) {
			this.eventDate = eventDate;
		}
		public Integer getTicketAmount() {
			return ticketAmount;
		}
		public void setTicketAmount(Integer ticketAmount) {
			this.ticketAmount = ticketAmount;
		}
		public LocalDate getPurchaseDate() {
			return purchaseDate;
		}
		public void setPurchaseDate(LocalDate purchaseDate) {
			this.purchaseDate = purchaseDate;
		}
		public String getPromotionStatus() {
			return promotionStatus;
		}
		public void setPromotionStatus(String promotionStatus) {
			this.promotionStatus = promotionStatus;
		}
		public Integer getFinalPrice() {
			return finalPrice;
		}
		public void setFinalPrice(Integer finalPrice) {
			this.finalPrice = finalPrice;
		}
		
    }
}