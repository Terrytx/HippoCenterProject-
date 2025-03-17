package tw.com.hippo_center_backend.h3controller.b1;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

//import org.hibernate.internal.build.AllowSysOut;

/*管理員用*/

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import tw.com.hippo_center_backend.h0bean.T31EventBean;
import tw.com.hippo_center_backend.h0bean.T32EventCategoryBean;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h1repository.T32EventCategoryRepository;
import tw.com.hippo_center_backend.h1repository.T51VenueRepository;
import tw.com.hippo_center_backend.h2service.b1.T31EventService;
import tw.com.hippo_center_backend.h4dto.b1.EventDetailResponseDTO;
import tw.com.hippo_center_backend.h4dto.b1.EventRequestDTO;

@RestController
@RequestMapping("/api/events-admin")
public class T31EventAdminControler {

    @Autowired
    private T32EventCategoryRepository eventCategoryRepository;

    @Autowired
    private T51VenueRepository venueRepository;

    private final T31EventService eventService;

    public T31EventAdminControler(T31EventService eventService) {
        this.eventService = eventService;

    }

    // 發布節目
    @PutMapping("/{eventId}/publish")
    public ResponseEntity<Void> publishEvent(@PathVariable Integer eventId) {
        try {
            eventService.publishEvent(eventId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            System.out.println("查無此筆資料");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 取消發布節目
    @PutMapping("/{eventId}/unpublish")
    public ResponseEntity<Void> unpublishEvent(
            @PathVariable Integer eventId,
            @RequestParam(defaultValue = "false") boolean clearPublishDate) {

        try {
            eventService.unpublishEvent(eventId, clearPublishDate);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 查詢已發布的節目
    @GetMapping("/published")
    public ResponseEntity<List<EventDetailResponseDTO>> getPublishedEvents() {
        List<EventDetailResponseDTO> publishedEvents = eventService.getPublishedEvents();
        return ResponseEntity.ok(publishedEvents);
    }

    // 查詢未發布的節目
    @GetMapping("/unpublished")
    public ResponseEntity<List<EventDetailResponseDTO>> getUnpublishedEvents() {
        List<EventDetailResponseDTO> unpublishedEvents = eventService.getUnpublishedEvents();
        return ResponseEntity.ok(unpublishedEvents);
    }

    // 找所有節目資料
    // @param all
    // @return 節目資料
    @GetMapping("/all")
    public ResponseEntity<List<EventDetailResponseDTO>> getAllEvents() {
        List<EventDetailResponseDTO> events = eventService.getAllEvents().stream()
                .map(EventDetailResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    //
    /**
     * 根據 ID 查詢節目
     * 
     * @param eventId 活動 ID
     * @return 節目資料
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailResponseDTO> getEventById(@PathVariable Integer eventId) {
        Optional<T31EventBean> event = eventService.getEventById(eventId);
        return event
                .map(eventBean -> ResponseEntity.ok(new EventDetailResponseDTO(eventBean)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * 根據活動名稱搜尋節目
     * 
     * @param eventName 節目名稱
     * @return 符合條件的節目列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventDetailResponseDTO>> searchEventsByName(
            @RequestParam("eventName") String eventName) {

        List<T31EventBean> events = eventService.findByEventName(eventName);

        List<EventDetailResponseDTO> eventDTOs = events.stream()
                .map(EventDetailResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs);
    }

    // 創建節目
    @PostMapping("/create")
    public ResponseEntity<EventDetailResponseDTO> addEvent(
            @RequestPart("data") EventRequestDTO requestDTO, // JSON 其他欄位
            @RequestParam(value = "images", required = false) List<MultipartFile> images // 多張圖片
    ) {
        try {
            // 建立 T31EventBean 並塞入 requestDTO 的資料
            T31EventBean event = new T31EventBean();
            event.setEventName(requestDTO.getEventName());
            event.setEventDescription1(requestDTO.getEventDescription1());
            event.setEventPrice(requestDTO.getEventPrice());
            event.setEventStartDate(requestDTO.getEventStartDate());
            event.setEventEndDate(requestDTO.getEventEndDate());

            // 設定活動類別、場地
            T32EventCategoryBean category = new T32EventCategoryBean();
            category.setEventCategoryId(requestDTO.getEventCategoryId());
            event.setEventCategory(category);

            T51VenueBean venue = new T51VenueBean();
            venue.setVenueId(requestDTO.getVenueId());
            event.setVenue(venue);

            // 呼叫「改寫後」的 Service 方法（接收 MultipartFile）
            T31EventBean savedEvent = eventService.insertEventWithMultipartImages(event, images);

            return ResponseEntity.ok(new EventDetailResponseDTO(savedEvent));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // @CrossOrigin(origins = "http://localhost:9733")
    // @PostMapping("/create")
    // public ResponseEntity<EventDetailResponseDTO> addEvent(@Valid @RequestBody
    // EventRequestDTO requestDTO) {

    // System.out.println("requestDTO:"+requestDTO.getImagePaths());
    // try {
    // // 創建新的 Event 實體
    // T31EventBean event = new T31EventBean();

    // // 設置基本資訊
    // event.setEventName(requestDTO.getEventName());
    // event.setEventDescription1(requestDTO.getEventDescription1());
    // event.setEventPrice(requestDTO.getEventPrice());
    // event.setEventStartDate(requestDTO.getEventStartDate());
    // event.setEventEndDate(requestDTO.getEventEndDate());

    // // 設置活動類別
    // T32EventCategoryBean category = new T32EventCategoryBean();
    // category.setEventCategoryId(requestDTO.getEventCategoryId());
    // event.setEventCategory(category);

    // // 設置場地
    // T51VenueBean venue = new T51VenueBean();
    // venue.setVenueId(requestDTO.getVenueId());
    // event.setVenue(venue);

    // // 調用 Service 方法處理活動和圖片
    // T31EventBean savedEvent = eventService.insertEventWithImages(event,
    // requestDTO.getImagePaths());

    // // 轉換為 ResponseDTO 並返回
    // return ResponseEntity.ok(new EventDetailResponseDTO(savedEvent));
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.badRequest().body(null);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // }
    // }

    // 更新活動
    @PutMapping("/update/{id}")
    public ResponseEntity<EventDetailResponseDTO> updateEvent(
            @PathVariable Integer id,
            @RequestPart("data") EventRequestDTO requestDTO,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            T31EventBean event = new T31EventBean();
            event.setEventId(id);
            event.setEventName(requestDTO.getEventName());
            event.setEventDescription1(requestDTO.getEventDescription1());
            event.setEventPrice(requestDTO.getEventPrice());
            event.setEventStartDate(requestDTO.getEventStartDate());
            event.setEventEndDate(requestDTO.getEventEndDate());
            event.setIsPublished(requestDTO.getIsPublished());

            // 從資料庫中查詢完整的類別物件，避免只有 ID 而無名稱
            T32EventCategoryBean category = eventCategoryRepository.findById(requestDTO.getEventCategoryId())
                    .orElseThrow(() -> new RuntimeException("活動類別不存在"));
            event.setEventCategory(category);

            // 同理，對場地也可做完整查詢
            T51VenueBean venue = venueRepository.findById(requestDTO.getVenueId())
                    .orElseThrow(() -> new RuntimeException("場地不存在"));
            event.setVenue(venue);

            // 呼叫更新 Service，這裡用 MultipartFile 的方式
            T31EventBean updatedEvent = eventService.updateEventWithMultipartImages(id, event, images);
            return ResponseEntity.ok(new EventDetailResponseDTO(updatedEvent));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // @PutMapping("/update/{id}")
    // public ResponseEntity<EventDetailResponseDTO> updateEvent(
    // @PathVariable Integer id,
    // @Valid @RequestBody EventRequestDTO requestDTO) {
    // try {
    // // 檢查活動是否存在
    // T31EventBean existingEvent = eventService.getEventById(id)
    // .orElseThrow(() -> new IllegalArgumentException("找不到ID為 " + id + " 的活動"));

    // // 創建新的 Event 實體並設置 ID
    // T31EventBean event = new T31EventBean();
    // event.setEventId(id);

    // // 設置基本資訊
    // event.setEventName(requestDTO.getEventName());
    // event.setEventDescription1(requestDTO.getEventDescription1());
    // event.setEventPrice(requestDTO.getEventPrice());
    // event.setEventStartDate(requestDTO.getEventStartDate());
    // event.setEventEndDate(requestDTO.getEventEndDate());
    // event.setIsPublished(requestDTO.getIsPublished());

    // // 設置活動類別
    // T32EventCategoryBean category = new T32EventCategoryBean();
    // category.setEventCategoryId(requestDTO.getEventCategoryId());
    // event.setEventCategory(category);

    // // 設置場地
    // T51VenueBean venue = new T51VenueBean();
    // venue.setVenueId(requestDTO.getVenueId());
    // event.setVenue(venue);

    // // 調用 Service 方法處理活動和圖片
    // T31EventBean updatedEvent = eventService.updateEventWithImages(event,
    // requestDTO.getImagePaths());

    // // 轉換為 ResponseDTO 並返回
    // return ResponseEntity.ok(new EventDetailResponseDTO(updatedEvent));
    // } catch (IllegalArgumentException e) {
    // System.err.println("更新活動失敗：" + e.getMessage());
    // return ResponseEntity.badRequest().body(null);
    // } catch (Exception e) {
    // System.err.println("更新活動時發生錯誤：" + e.getMessage());
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // }
    // }

    /*
     * 找尋過去的活動
     */
    @GetMapping("/past-events")
    public ResponseEntity<List<T31EventBean>> getPastEvents() {
        List<T31EventBean> pastEvents = eventService.getPastEvents();
        return ResponseEntity.ok(pastEvents);
    }

    /**
     * 根據 ID 刪除活動
     * 
     * @param eventId 活動 ID
     * @return 回應狀態
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEventById(@PathVariable Integer eventId) {
        eventService.deleteEventById(eventId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 
     * 刪除所有節目-------(慎用)平時應該disable(平時禁止使用),要使用要加上,再次確認功能
     * 
     * @return 回應狀態
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllEvents() {
        eventService.deleteAllEvents();
        System.out.println("刪除所有節目");
        return ResponseEntity.noContent().build();
    }
}
