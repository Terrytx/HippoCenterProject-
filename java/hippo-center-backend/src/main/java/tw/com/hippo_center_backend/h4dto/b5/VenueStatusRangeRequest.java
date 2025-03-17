package tw.com.hippo_center_backend.h4dto.b5;

import java.time.LocalDate;

import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;

public class VenueStatusRangeRequest {
	    private T53VenueStatusBean venueStatus;
	    private LocalDate startDate;
	    private LocalDate endDate;
	    
	    
		public T53VenueStatusBean getVenueStatus() {
			return venueStatus;
		}
		public void setVenueStatus(T53VenueStatusBean venueStatus) {
			this.venueStatus = venueStatus;
		}
		public LocalDate getStartDate() {
			return startDate;
		}
		public void setStartDate(LocalDate startDate) {
			this.startDate = startDate;
		}
		public LocalDate getEndDate() {
			return endDate;
		}
		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}
		@Override
		public String toString() {
			return "VenueStatusDTO [venueStatus=" + venueStatus + ", startDate=" + startDate + ", endDate=" + endDate
					+ "]";
		}
		public VenueStatusRangeRequest(T53VenueStatusBean venueStatus, LocalDate startDate, LocalDate endDate) {
			super();
			this.venueStatus = venueStatus;
			this.startDate = startDate;
			this.endDate = endDate;
		}
		  public VenueStatusRangeRequest() {
		        super();
		    }
	    
	
	}

