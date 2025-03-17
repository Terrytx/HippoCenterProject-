package tw.com.hippo_center_backend.h2service.b5impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h0bean.T53VenueStatusBean;
import tw.com.hippo_center_backend.h1repository.T51VenueRepository;
import tw.com.hippo_center_backend.h1repository.T53VenueStatusRepository;
import tw.com.hippo_center_backend.h2service.b5.VenueStatusService;

@Service
@Transactional  
public class A3VenueStatusServiceImpl implements VenueStatusService {
	@Autowired
    private T53VenueStatusRepository venueStatusRepository;
	@Autowired
	private T51VenueRepository venueRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    @Transactional
    public T53VenueStatusBean createVenueStatus(T53VenueStatusBean bean) {
		// 如果id沒有，才會新增
		if (bean!=null && bean.getVenue()!=null && bean.getVenue().getVenueId() != null && bean.getDate() != null) {
	        // 2. 檢查資料庫是否已有此記錄
		    Optional<T53VenueStatusBean> existingStatus = 
		                venueStatusRepository.findByVenue_VenueIdAndDate(
		                    bean.getVenue().getVenueId(), 
		                    bean.getDate()
		                );
		    System.out.println("場地狀態是否存在 = " + existingStatus.isPresent());
            System.out.println("bean: " + bean);
            
         // 3. 如果記錄不存在，則新增
            if (existingStatus.isEmpty()) {
                setCreatedDateTime(bean);
                setUpdatedDateTime(bean);
                System.out.println("Service準備儲存資料到資料庫");
                try {
					T53VenueStatusBean savedBean = venueStatusRepository.save(bean);
					entityManager.flush();
					System.out.println("儲存成功，ID = " + savedBean.getVenueStatusId());
					return savedBean;
				} catch (Exception e) {
	                System.out.println("儲存過程發生錯誤：" + e.getMessage());
					e.printStackTrace();
				}
            } else {
                System.out.println("場地：" + bean.getVenue().getVenueId() +
                    "於" + bean.getDate() + "的狀態已存在");
                return existingStatus.get();  // 返回既有的記錄
            }
        }
		return null;
    }
    
    @Override
	public T53VenueStatusBean updateVenueStatus(T53VenueStatusBean bean) {
		if (bean != null && bean.getVenueStatusId() != null) {
			Optional<T53VenueStatusBean> existingRecord = venueStatusRepository.findById(bean.getVenueStatusId());
		        if (existingRecord.isPresent()) {
		            T53VenueStatusBean existing = existingRecord.get();
		            LocalDateTime originalCreatedTime = existing.getCreatedTime();
		            if (bean.getVenue() != null) {
		                existing.setVenue(bean.getVenue());
		            }
		            if (bean.getDate() != null) {
		                existing.setDate(bean.getDate());
		            }
		            if (bean.getStatus() != null) {
		                existing.setStatus(bean.getStatus());
		            }
		            if (bean.getBookingId() != null) {
		                existing.setBookingId(bean.getBookingId());
		            }
		            // 添加 note 欄位的更新
		            if (bean.getNote() != null) {
		                existing.setNote(bean.getNote());
		            }
		            // 4. 設置更新時間
		            existing.setUpdatedTime(LocalDateTime.now());
		            
		            // 5. 恢復原有的創建時間
		            existing.setCreatedTime(originalCreatedTime);
		            
		            // 6. 保存更新
		            T53VenueStatusBean saved = venueStatusRepository.save(existing);
		            
		            System.out.println("更新前的創建時間：" + originalCreatedTime);
		            System.out.println("更新後的創建時間：" + saved.getCreatedTime());
		            
		            return saved;
			}
		}
		return null;
	}
    
    public boolean exists(Integer id) {
		if (id != null) {
			return venueStatusRepository.existsById(id);
		}
		return false;
	}
    public boolean delete(T53VenueStatusBean bean) {
		if (bean != null && bean.getVenueStatusId() != null) {
			if (venueStatusRepository.existsById(bean.getVenueStatusId())) {
				venueStatusRepository.deleteById(bean.getVenueStatusId());
				return true;
			}
		}
		return false;
	}
  
    @Transactional
    public List<T53VenueStatusBean> createVenueStatusForDateRange(T53VenueStatusBean bean, LocalDate startDate, LocalDate endDate) {
        System.out.println("開始執行日期範圍新增，起始日期: " + startDate + ", 結束日期: " + endDate);

        List<T53VenueStatusBean> savedRecords = new ArrayList<>();
        List<LocalDate> conflictDates = new ArrayList<>();

        // 檢查基本參數
        if (bean != null && bean.getVenue() != null && bean.getVenue().getVenueId() != null
                && startDate != null && endDate != null) {
            System.out.println("參數檢查通過，場地ID: " + bean.getVenue().getVenueId());

            // 先檢查整個日期範圍是否有衝突
            LocalDate currentDate = startDate;
            System.out.println("開始檢查日期衝突...");

            while (!currentDate.isAfter(endDate)) {
                Optional<T53VenueStatusBean> existingStatus =
                    venueStatusRepository.findByVenue_VenueIdAndDate(
                        bean.getVenue().getVenueId(),
                        currentDate
                    );

                if (existingStatus.isPresent()) {
                    System.out.println("發現衝突日期: " + currentDate);
                    conflictDates.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }

            // 如果有衝突日期，處理並拋出異常
            if (!conflictDates.isEmpty()) {
            	String venueName = "";
                Optional<T51VenueBean> venue = venueRepository.findByVenueId(bean.getVenue().getVenueId());
                if (venue.isPresent()) {
                    venueName = venue.get().getVenueName();
                }
                // 將日期排序
                Collections.sort(conflictDates);
                
                // 找出日期區間
                List<String> dateRanges = new ArrayList<>();
                LocalDate rangeStart = conflictDates.get(0);
                LocalDate rangeEnd = rangeStart;
                
                for (int i = 1; i < conflictDates.size(); i++) {
                    LocalDate currentDt = conflictDates.get(i);
                    if (currentDt.equals(rangeEnd.plusDays(1))) {
                        // 日期連續，更新區間結束日期
                        rangeEnd = currentDt;
                    } else {
                        // 日期不連續，加入當前區間並開始新區間
                        dateRanges.add(formatDateRange(rangeStart, rangeEnd));
                        rangeStart = currentDt;
                        rangeEnd = currentDt;
                    }
                }
                // 加入最後一個區間
                dateRanges.add(formatDateRange(rangeStart, rangeEnd));
                
                // 組合錯誤訊息
                String errorDateRanges = String.join(System.lineSeparator()+"　" ,dateRanges);
                System.out.println("發現日期衝突，衝突日期區間: " + errorDateRanges);

                throw new DateConflictException(
                    "場地 " + bean.getVenue().getVenueId() +  " ( " + venueName + " ) " +
                    " 在以下日期 已有預約紀錄："+System.lineSeparator()+"　" + errorDateRanges 
                );
            }

            // 如果沒有衝突，執行新增
            System.out.println("未發現日期衝突，開始新增記錄...");

            currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                System.out.println("準備新增日期: " + currentDate);

                T53VenueStatusBean newStatus = new T53VenueStatusBean();
                newStatus.setVenue(bean.getVenue());
                newStatus.setDate(currentDate);
                newStatus.setStatus(bean.getStatus());
                newStatus.setBookingId(bean.getBookingId());
                newStatus.setNote(bean.getNote());

                setCreatedDateTime(newStatus);
                setUpdatedDateTime(newStatus);

                try {
                    T53VenueStatusBean savedBean = venueStatusRepository.save(newStatus);
                    entityManager.flush();
                    System.out.println("成功新增記錄，ID: " + savedBean.getVenueStatusId());

                    savedRecords.add(savedBean);
                } catch (Exception e) {
                    System.out.println("新增記錄時發生錯誤: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("儲存場地狀態時發生錯誤", e);
                }

                currentDate = currentDate.plusDays(1);
            }
            System.out.println("所有日期新增完成，共新增 " + savedRecords.size() + " 筆記錄");
        } else {
            System.out.println("參數檢查失敗，請檢查必要欄位是否齊全");
        }
        return savedRecords;
    }

    // 新增用於格式化日期區間的輔助方法
    private String formatDateRange(LocalDate start, LocalDate end) {
        if (start.equals(end)) {
            return start.toString();
        } else {
            return start.toString() + " ~ " + end.toString();
        }
    }

    // 異常類別
    public class DateConflictException extends RuntimeException {
        public DateConflictException(String message) {
            super(message);
        }
    }
    
    @Transactional
    public void importFromCsv(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("檔案不能為空");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            System.out.println("開始匯入CSV檔案...");

            // 跳過標題行
            String line = reader.readLine();
            int rowNum = 1;

            while ((line = reader.readLine()) != null) {
                rowNum++;
                try {
                    // 移除 BOM 標記如果存在
                    if (line.startsWith("\uFEFF")) {
                        line = line.substring(1);
                    }

                    // 使用String.split但保留引號內的逗號
                    List<String> data = new ArrayList<>();
                    StringBuilder field = new StringBuilder();
                    boolean inQuotes = false;
                    
                    for (char c : line.toCharArray()) {
                        if (c == '"') {
                            inQuotes = !inQuotes;
                        } else if (c == ',' && !inQuotes) {
                            data.add(field.toString().trim());
                            field = new StringBuilder();
                        } else {
                            field.append(c);
                        }
                    }
                    data.add(field.toString().trim());

                    if (data.size() < 5) continue;

                    // 移除場地ID中的引號
                    String venueIds = data.get(0).replace("\"", "").trim();
                    String startDateStr = data.get(1).trim();
                    String endDateStr = data.get(2).trim();
                    String status = data.get(3).trim();
                    String note = data.get(4).trim();

                    System.out.println("處理第 " + rowNum + " 行資料");
                    System.out.println("場地: " + venueIds);
                    System.out.println("日期: " + startDateStr + " 到 " + endDateStr);

                    // 轉換日期
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
                    LocalDate startDate = LocalDate.parse(startDateStr, formatter);
                    LocalDate endDate = LocalDate.parse(endDateStr, formatter);

                    // 處理場地ID（支援逗號和空白作為分隔符號）
                    List<String> venueIdList = Arrays.stream(venueIds.split("[,\\s]+"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                    // 為每個場地建立預約狀態
                    for (String venueId : venueIdList) {
                        T53VenueStatusBean statusBean = new T53VenueStatusBean();

                        // 設置場地
                        T51VenueBean venue = new T51VenueBean();
                        venue.setVenueId(venueId);
                        statusBean.setVenue(venue);

                        // 設置狀態和備註
                        statusBean.setStatus(status);
                        statusBean.setNote(note);

                        try {
                            createVenueStatusForDateRange(statusBean, startDate, endDate);
                            System.out.println("成功新增場地 " + venueId + " 在 " + 
                                startDateStr + " 到 " + endDateStr + " 的狀態");
                        } catch (DateConflictException e) {
                            System.out.println("日期衝突：" + e.getMessage());
                            throw e;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("處理第 " + rowNum + " 行資料時發生錯誤：" + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("處理第 " + rowNum + " 行資料時發生錯誤", e);
                }
            }

            System.out.println("CSV檔案匯入完成");

        } catch (Exception e) {
            System.out.println("處理CSV檔案時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("匯入失敗", e);
        }
    }
    
}