package tw.com.hippo_center_backend.h2service.b5impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
import tw.com.hippo_center_backend.h1repository.T53VenueStatusRepository;
@Service
public class C3VenueStatusFindService {
	@Autowired
    private T53VenueStatusRepository venueStatusRepository;
	
	 // 1. 透過場地ID找到所有相關的場地狀態
	 public List<T53VenueStatusBean> findByVenueId(String venueId) {
	        if(venueId != null) {
	            System.out.println("準備查詢場地ID: " + venueId);
	            List<T53VenueStatusBean> results = venueStatusRepository.findByVenue_VenueId(venueId);
	            System.out.println("查詢到 " + results.size() + " 筆結果");
	            return results;
	        }
	        System.out.println("場地ID為null，返回空列表");
	        return Collections.emptyList();
	    }

	    // 2. 透過場地ID和日期找到特定的場地狀態
	  public Optional<T53VenueStatusBean> findByVenueIdAndDate(String venueId, LocalDate date) {
	        if(venueId != null && date != null) {
	            return venueStatusRepository.findByVenue_VenueIdAndDate(venueId, date);
	        }
	        return Optional.empty();
	    }
	  /**
	   * 依年月查詢場地狀態              <- 這行說明這個方法是做什麼用的
	   * @param year 年份               <- 說明第一個參數 year 代表年份
	   * @param month 月份              <- 說明第二個參數 month 代表月份
	   * @return 當月每天的場地狀態列表    <- 說明這個方法會回傳什麼資料
	   */
	  public List<T53VenueStatusBean> getMonthlyAvailability(int year, int month) {
	        return venueStatusRepository.findByYearAndMonth(year, month);
	    }
		// 場館ID、年月 的查詢方法
      public List<T53VenueStatusBean> getMonthlyAvailabilityByVenue(int year, int month, String venueId) {
		    return venueStatusRepository.findByYearAndMonthAndVenue(year, month, venueId);
		}
}
