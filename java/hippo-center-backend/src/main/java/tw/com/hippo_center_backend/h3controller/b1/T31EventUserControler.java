package tw.com.hippo_center_backend.h3controller.b1;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

/*來訪者用*/

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import tw.com.hippo_center_backend.h0bean.T32EventCategoryBean;
import tw.com.hippo_center_backend.h0bean.T31EventBean;
//import tw.com.hippo_center_backend.h0bean.T34EventImagesBean;
import tw.com.hippo_center_backend.h2service.b1.T31EventService;
import tw.com.hippo_center_backend.h4dto.b1.EventImageDTO;
import tw.com.hippo_center_backend.h4dto.b1.EventResponseDTO;

@RestController
@RequestMapping("/api/events-user")
public class T31EventUserControler {

	 @Autowired
 private T31EventService T31EventService;
    private final T31EventService eventService;

    public T31EventUserControler(T31EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        try {
            // 直接呼叫 Service 層已發布活動的方法
            List<T31EventBean> events = eventService.getPublishedEventsForSearch();
            
            List<EventResponseDTO> dtoList = events.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
 //--------------------------------------------------------------------------------------------------------   
    //目前活動
    @GetMapping("/ongoing-events")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsbyDate() {
        try {
            LocalDate today = LocalDate.now();

            // 先從 Service 取得已發布的活動，再依據日期篩選
            List<T31EventBean> events = eventService.getPublishedEventsForSearch().stream()
                    .filter(event ->
                        // 活動開始日期 <= 今天
                        !event.getEventStartDate().isAfter(today)
                        // 活動結束日期 > 今天
                        && event.getEventEndDate().isAfter(today)
                    )
                    .collect(Collectors.toList());

            if (events.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<EventResponseDTO> ongoingEventDTOs = events.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ongoingEventDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    
//-----------------------------------------------------------------------------------------------------
       
    @GetMapping("/search/multi")
    public ResponseEntity<List<EventResponseDTO>> searchEvents(
            @RequestParam(required = false) String eventCategoryName,
            @RequestParam(required = false) String venueName,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month // 月份參數
    ) {
        try {
            List<T31EventBean> events;
          
            // 如果沒有任何搜尋條件，回傳已發布活動
            if (eventCategoryName == null && venueName == null && year == null && month == null) {
                events = eventService.getPublishedEventsForSearch();
            } else {
                events = eventService.getAllEvents();
            }

            // 根據活動類別篩選
            if (eventCategoryName != null && !eventCategoryName.trim().isEmpty()) {
                events = eventService.getEventsByEventCategoryName(eventCategoryName);
            }

            // 根據場地名稱篩選
            if (venueName != null && !venueName.trim().isEmpty()) {
                events = events.stream()
                        .filter(event -> event.getVenue() != null &&
                                         event.getVenue().getVenueName().equalsIgnoreCase(venueName))
                        .collect(Collectors.toList());
            }

            if (year != null && month != null) {
                System.out.println("Filtering for year: " + year + ", month: " + month);
                events = events.stream()
                    .filter(event -> {
                        if (event.getEventStartDate() == null) {
                            System.out.println("Event start date is null");
                            return false;
                        }
                        LocalDate eventDate = event.getEventStartDate();
                        boolean matches = eventDate.getYear() == year && 
                                        eventDate.getMonthValue() == month;
                        System.out.println("Event date: " + eventDate + 
                                         ", matches filter: " + matches);
                        return matches;
                    })
                    .collect(Collectors.toList());
            }

            // 只保留已發布的活動
            events = events.stream()
                    .filter(T31EventBean::getIsPublished)
                    .collect(Collectors.toList());

            if (events.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 轉換成 DTO
            List<EventResponseDTO> eventDTOs = events.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //-----------------------------------------------------------------------------------------------------
    
    /**
     * 找出開始日期大於或等於「今天 + 1 天」
     * 且結束日期大於或等於「今天 + 2 天」的已發布活動，
     * 轉換成 DTO 回傳。
     *
     * 範例說明：
     *  - 若今天為 2025-02-09，則活動的開始日期需為 2025-02-10 以後，
     *    而結束日期需為 2025-02-11 以後。
     *
     * @return 符合條件的活動 DTO 列表；若無符合條件的活動，回傳 404 Not Found
     */
    @GetMapping("/upcoming-events")
    public ResponseEntity<List<EventResponseDTO>> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        // 定義過濾條件：開始日期 ≥ 今天+1；結束日期 ≥ 今天+2
        LocalDate startThreshold = today.plusDays(1);
        LocalDate endThreshold = today.plusDays(2);

        // 取得已發布的活動，再根據日期條件過濾
        List<T31EventBean> events = eventService.getPublishedEventsForSearch().stream()
                .filter(event ->
                        // 確保活動的開始日期不早於 startThreshold
                        !event.getEventStartDate().isBefore(startThreshold)
                        // 確保活動的結束日期不早於 endThreshold
                        && !event.getEventEndDate().isBefore(endThreshold)
                )
                .collect(Collectors.toList());

        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<EventResponseDTO> upcomingEventDTOs = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(upcomingEventDTOs);
    }

  //-----------------------------------------------------------------------------------------------------    
    
  
    /**
     * 依照日期新到舊查詢所有活動，轉換成 DTO 回傳
     * @return 活動 DTO 列表
     */
    @GetMapping("/sorted-events")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsSortedByDate() {
       	
        List<T31EventBean> sortedEvents = T31EventService.getAllEventsSortedByDate();
        if (sortedEvents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<EventResponseDTO> sortedEventDTOs = sortedEvents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sortedEventDTOs);
    }

    /**
     * 查詢過去的活動，轉換成 DTO 回傳。
     * 只回傳已發布的活動，且活動的開始日期和結束日期都早於今天。
     *
     * @return 過去活動 DTO 列表；若無符合條件的活動，回傳 404 Not Found
     */
    @GetMapping("/past-events")
    public ResponseEntity<List<EventResponseDTO>> getPastEvents() {
        // 取得今天的日期
        LocalDate today = LocalDate.now();

        // 從 service 取得過去的活動，再過濾出已發布的活動
        List<T31EventBean> pastEvents = eventService.getPastEvents().stream()
                .filter(event -> 
                        // 開始日期早於今天
                        event.getEventStartDate().isBefore(today)
                        // 結束日期早於今天
                        && event.getEventEndDate().isBefore(today)
                        // 且必須已發布
                        && event.getIsPublished()
                )
                .collect(Collectors.toList());

        if (pastEvents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<EventResponseDTO> pastEventDTOs = pastEvents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pastEventDTOs);
    }

  
 //-----------------------------------------------------------------------------------------------------
    /**
     * 根據活動名稱搜尋活動
     * @param eventName 活動名稱關鍵字
     * @return 符合條件的活動列表 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventResponseDTO>> searchEventsByName(@RequestParam String eventName) {
        try {
            List<T31EventBean> events = eventService.findByEventName(eventName);
            
            if (events.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            List<EventResponseDTO> dtoList = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(dtoList);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
 //-----------------------------------------------------------------------------------------------------
   //未決定要如何使用 活動id查詢
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Integer eventId) {
        try {
            Optional<T31EventBean> optionalEvent = eventService.getEventById(eventId);
            if (optionalEvent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            EventResponseDTO dto = convertToDTO(optionalEvent.get());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
 //-----------------------------------------------------------------------------------------------------
    
    /**
     * 根據類別類型查詢活動
     * eventCategoryType種類為固定 101=permanent, 102=limited-time, 103=upcoming, 104=market, 105=others
     * @param eventCategoryType 活動類別類型
     * @return 活動列表
     */
    @GetMapping("/categoryType/{eventCategoryType}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByEventCategoryType(@PathVariable Integer eventCategoryType) {
        try {
            // 從 Service 層獲取實體數據並轉換為 DTO
            List<T31EventBean> events = eventService.getEventsByEventCategoryType(eventCategoryType);
            
            if (events.isEmpty()) {
                System.out.println("找不到資料404");
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            // 將實體轉換為 DTO
            List<EventResponseDTO> eventDTOs = events.stream()
                    .map(this::convertToDTO1)
                    .collect(Collectors.toList());

            System.out.println("成功找到資料");
            return ResponseEntity.ok(eventDTOs);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } catch (Exception e) {
            System.out.println("未預期錯誤");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // 轉換方法
    private EventResponseDTO convertToDTO1(T31EventBean event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setEventId(event.getEventId()); // 這行很重要
        dto.setEventName(event.getEventName());
        dto.setEventStartDate(event.getEventStartDate());
        dto.setEventEndDate(event.getEventEndDate());
        dto.setEventUpdateDate(event.getEventUpdateDate());
        dto.setEventPrice(event.getEventPrice());
        dto.setEventDescription1(event.getEventDescription1());
        
        // 設置類別名稱
        if (event.getEventCategory() != null) {
            dto.setEventCategory(event.getEventCategory().getEventCategoryName());
        }
        
        // 設置場地名稱
        if (event.getVenue() != null) {
            dto.setVenueName(event.getVenue().getVenueName());
        }
        
        // 轉換圖片列表
        if (event.getImages() != null) {
            List<EventImageDTO> imageDTOs = event.getImages().stream()
                    .map(image -> {
                        EventImageDTO imageDTO = new EventImageDTO();
                        imageDTO.setImageId(image.getImageId());
                        imageDTO.setImageUrl(image.getImageUrl());
                        imageDTO.setIsCover(image.getIsCover());
                        return imageDTO;
                    })
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }
        
        return dto;
    }
 //-----------------------------------------------------------------------------------------------------
    
    /**
     * 搜尋特定日期的活動。
     * @param specificDate 指定的日期 (格式：YYYY-MM-DD)
     * @return 活動列表的 ResponseEntity
     */
    @GetMapping("/date/{specificDate}")
    public ResponseEntity<List<EventResponseDTO>> getEventsBySpecificDate(@PathVariable String specificDate) {
        try {
            // 解析日期
            LocalDate date = LocalDate.parse(specificDate);
            
            // 從 Service 層獲取實體數據
            List<T31EventBean> events = T31EventService.getEventsBySpecificDate(date);

            if (events.isEmpty()) {
                System.out.println("找不到資料404");
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
            }

            // 將實體轉換為 DTO
            List<EventResponseDTO> eventDTOs = events.stream()
                .map(this::convertToDTO3)
                .collect(Collectors.toList());

            System.out.println("成功找到資料");
            return ResponseEntity.ok(eventDTOs);

        } catch (DateTimeParseException e) {
            // 處理日期格式錯誤
            System.out.println("日期格式錯誤");
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        } catch (Exception e) {
            System.out.println("未預期錯誤");
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    // 使用相同的 convertToDTO 方法
    private EventResponseDTO convertToDTO3(T31EventBean event) {
        EventResponseDTO dto = new EventResponseDTO();

        dto.setEventName(event.getEventName());
        dto.setEventStartDate(event.getEventStartDate());
        dto.setEventEndDate(event.getEventEndDate());
        dto.setEventUpdateDate(event.getEventUpdateDate());
        dto.setEventPrice(event.getEventPrice());
        dto.setEventDescription1(event.getEventDescription1());

        // 設置類別名稱
        if (event.getEventCategory() != null) {
            dto.setEventCategory(event.getEventCategory().getEventCategoryName());
        }

        // 設置場地名稱
        if (event.getVenue() != null) {
            dto.setVenueName(event.getVenue().getVenueName());
        }

        // 轉換圖片列表
        if (event.getImages() != null) {
            List<EventImageDTO> imageDTOs = event.getImages().stream()
                .map(image -> {
                    EventImageDTO imageDTO = new EventImageDTO();
                    imageDTO.setImageId(image.getImageId());
                    imageDTO.setImageUrl(image.getImageUrl());
                    imageDTO.setIsCover(image.getIsCover());
                    return imageDTO;
                })
                .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        return dto;
    }
//-----------------------------------------------------------------------------------------------------
     
    /**
    * 搜尋特定日期範圍內的活動。
    * @param startDate 開始日期 (格式：YYYY-MM-DD)
    * @param endDate 結束日期 (格式：YYYY-MM-DD)
    * @return 活動列表的 ResponseEntity
    */
    @GetMapping("/range") 
    public ResponseEntity<List<EventResponseDTO>> getEventsByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
       try {
           // 解析日期
           LocalDate start = LocalDate.parse(startDate);
           LocalDate end = LocalDate.parse(endDate);
           
           // 從 Service 層獲取實體數據
           List<T31EventBean> events = T31EventService.getEventsByDateRange(start, end);

           if (events.isEmpty()) {
               System.out.println("找不到資料404");
               return ResponseEntity
                   .status(HttpStatus.NOT_FOUND)
                   .build();
           }

           // 將實體轉換為 DTO
           List<EventResponseDTO> eventDTOs = events.stream()
               .map(this::convertToDTO2)
               .collect(Collectors.toList());

           System.out.println("成功找到資料");
           return ResponseEntity.ok(eventDTOs);

       } catch (DateTimeParseException e) {
           // 處理日期格式錯誤
           System.out.println("日期格式錯誤");
           return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .build();
       } catch (IllegalArgumentException e) {
           // 處理無效的日期範圍
           System.out.println("無效的日期範圍");
           return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .build();
       } catch (Exception e) {
           System.out.println("未預期錯誤");
           return ResponseEntity
               .status(HttpStatus.INTERNAL_SERVER_ERROR)
               .build();
       }
    }

    // 使用相同的 convertToDTO 方法
    private EventResponseDTO convertToDTO2(T31EventBean event) {
       EventResponseDTO dto = new EventResponseDTO();

       dto.setEventName(event.getEventName());
       dto.setEventStartDate(event.getEventStartDate());
       dto.setEventEndDate(event.getEventEndDate());
       dto.setEventUpdateDate(event.getEventUpdateDate());
       dto.setEventPrice(event.getEventPrice());
       dto.setEventDescription1(event.getEventDescription1());

       // 設置類別名稱
       if (event.getEventCategory() != null) {
           dto.setEventCategory(event.getEventCategory().getEventCategoryName());
       }

       // 設置場地名稱
       if (event.getVenue() != null) {
           dto.setVenueName(event.getVenue().getVenueName());
       }

       // 轉換圖片列表
       if (event.getImages() != null) {
           List<EventImageDTO> imageDTOs = event.getImages().stream()
               .map(image -> {
                   EventImageDTO imageDTO = new EventImageDTO();
                   imageDTO.setImageId(image.getImageId());
                   imageDTO.setImageUrl(image.getImageUrl());
                   imageDTO.setIsCover(image.getIsCover());
                   return imageDTO;
               })
               .collect(Collectors.toList());
           dto.setImages(imageDTOs);
       }

       return dto;
    }
//-----------------------------------------------------------------------------------------------------
    
    //轉換dto的通用方法
    
    private EventResponseDTO convertToDTO(T31EventBean event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setEventId(event.getEventId());
        dto.setEventName(event.getEventName());
        dto.setEventStartDate(event.getEventStartDate());
        dto.setEventEndDate(event.getEventEndDate());
        dto.setEventUpdateDate(event.getEventUpdateDate());
        dto.setEventPrice(event.getEventPrice());
        dto.setEventDescription1(event.getEventDescription1());

        if (event.getEventCategory() != null) {
            dto.setEventCategory(event.getEventCategory().getEventCategoryName());
        }

        if (event.getVenue() != null) {
            dto.setVenueName(event.getVenue().getVenueName());  // 直接設置 venueName
        }

     // 修改 convertToDTO 方法中處理圖片的部分
        if (event.getImages() != null && !event.getImages().isEmpty()) {
            List<EventImageDTO> imageDTOs = event.getImages().stream()
                .map(image -> {
                    EventImageDTO imageDTO = new EventImageDTO();
                    imageDTO.setImageUrl(image.getImageUrl());
                    return imageDTO;
                })
                .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        return dto;
    }
 //-----------------------------------------------------------------------------------------------------    
    
    /**
     * 根據場地 ID 查詢活動
     * @param venueId 場地 ID
     * @return 活動列表
     */
    @GetMapping("/venue/id/{venueId}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByVenueId(@PathVariable String venueId) {
        List<T31EventBean> events = eventService.getEventsByVenueId(venueId);
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<EventResponseDTO> eventDTOs = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }

    /**
     * 根據場地名稱查詢活動（精確匹配）
     * @param venueName 場地名稱
     * @return 活動列表
     */
    @GetMapping("/venue/search")
    public ResponseEntity<List<EventResponseDTO>> getEventsByVenueName(@RequestParam String venueName) {
        List<T31EventBean> events = eventService.getEventsByVenueName(venueName);
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<EventResponseDTO> eventDTOs = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }

    /**
     * 根據場地名稱查詢活動（模糊匹配）
     * @param venueName 場地名稱關鍵字
     * @return 活動列表
     */
    @GetMapping("/venue/search/keyword")
    public ResponseEntity<List<EventResponseDTO>> searchEventsByVenueName(@RequestParam String venueName) {
        List<T31EventBean> events = eventService.searchEventsByVenueName(venueName);
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<EventResponseDTO> eventDTOs = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }


   
    
    
}


/**
//* 找出活動開始日期在今天以前且結束日期在今天以後的活動（進行中的活動），且必須為已發布的活動，
//* 轉換成 DTO 回傳。
//*
//* @return 符合條件的活動 DTO 列表；若無符合條件的活動，回傳 404 Not Found
//*/
//@GetMapping("/ongoing-events")
//public ResponseEntity<List<EventResponseDTO>> getOngoingEvents() {
//  try {
//      LocalDate today = LocalDate.now();
//      // 先從 Service 取得已發布的活動，再依據日期篩選
//      List<T31EventBean> events = eventService.getPublishedEventsForSearch().stream()
//              .filter(event ->
//                  // 活動開始日期在今天以前
//                  event.getEventStartDate().isBefore(today)
//                  // 活動結束日期在今天以後
//                  && event.getEventEndDate().isAfter(today)
//              )
//              .collect(Collectors.toList());
//
//      if (events.isEmpty()) {
//          return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//      }
//
//      List<EventResponseDTO> ongoingEventDTOs = events.stream()
//              .map(this::convertToDTO)
//              .collect(Collectors.toList());
//
//      return ResponseEntity.ok(ongoingEventDTOs);
//  } catch (Exception e) {
//      return ResponseEntity.badRequest().build();
//  }
//}

//已經廢棄

///**
//* 根據類別類型查詢活動
//* @param categoryType 活動類別類型
//* @return 符合條件的活動列表
//*/
//@GetMapping("/category")
//public ResponseEntity<List<T31EventBean>> getEventsByCategoryType(@RequestParam("categoryType") Integer categoryType) {
// List<T31EventBean> events = eventService.findByeventCategoryType(categoryType);
// return ResponseEntity.ok(events);
//}
