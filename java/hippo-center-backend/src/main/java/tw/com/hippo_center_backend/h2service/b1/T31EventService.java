package tw.com.hippo_center_backend.h2service.b1;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import tw.com.hippo_center_backend.h0bean.T31EventBean;
import tw.com.hippo_center_backend.h0bean.T32EventCategoryBean;
import tw.com.hippo_center_backend.h0bean.T34EventImagesBean;
import tw.com.hippo_center_backend.h0bean.T51VenueBean;
import tw.com.hippo_center_backend.h1repository.T31EventRepository;
import tw.com.hippo_center_backend.h1repository.T32EventCategoryRepository;
import tw.com.hippo_center_backend.h1repository.T34EventImagesRepository;
import tw.com.hippo_center_backend.h1repository.T51VenueRepository;
import tw.com.hippo_center_backend.h4dto.b1.EventDetailResponseDTO;

@Service
public class T31EventService {

    @Autowired
    private T34EventImagesRepository eventImageRepository;
    @Autowired
    public T51VenueRepository venueRepository;
    @Autowired
    private T31EventRepository t31EventRepository;

    private static final Logger log = LoggerFactory.getLogger(T31EventService.class);
    private final T31EventRepository eventRepository;
    private final T32EventCategoryRepository eventCategoryRepository;
    private final T34EventImagesRepository eventImagesRepository;

    public T31EventService(T31EventRepository eventRepository,
            T32EventCategoryRepository eventCategoryRepository,
            T51VenueRepository venueRepository,
            T34EventImagesRepository eventImagesRepository) {
        this.eventRepository = eventRepository;
        this.eventCategoryRepository = eventCategoryRepository;
        // this.venueRepository = venueRepository;
        this.eventImagesRepository = eventImagesRepository;
    }

    // -----------------------------------------------------------------------------------------------------
    /**
     * 根據 ID 查詢活動
     * 
     * @param eventId 活動 ID
     * @return 活動資料（Optional）
     */
    public Optional<T31EventBean> getEventById(Integer eventId) {
        return eventRepository.findById(eventId);
    }

    // 找到所有活動有最新的活動最晚的活動排序
    public List<T31EventBean> getAllEventsbyDate() {
        return eventRepository.findAllByEventStartDateDesc();
    }
    // -----------------------------------------------------------------------------------------------------

    /**
     * 查詢所有活動
     * 
     * @return 活動列表
     */
    public List<T31EventBean> getAllEvents() {
        return eventRepository.findAll();
    }

    // -----------------------------------------------------------------------------------------------------
    /**
     * 根據活動類別名稱搜尋活動。
     * 
     * @param eventCategoryName
     * @return 活動列表
     */

    public List<T31EventBean> getEventsByEventCategoryName(String eventCategoryName) {
        return eventRepository.findByEventCategoryName(eventCategoryName);
    }

    /*
     * 搜尋今天以前的活動
     * 
     * @return 活動列表
     */
    public List<T31EventBean> getPastEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findPastEvents(today);
    }

    // -----------------------------------------------------------------------------------------------------
    /**
     * 搜尋特定日期的活動。
     * 
     * @param specificDate 指定的日期
     * @return 活動列表
     */
    public List<T31EventBean> getEventsBySpecificDate(LocalDate specificDate) {
        return t31EventRepository.findByEventStartDateBetween(specificDate, specificDate);
    }

    // -----------------------------------------------------------------------------------------------------
    /**
     * 取得所有活動，依據活動開始日期降序排序（最新活動優先）
     * 
     * @return 排序後的活動列表
     */
    public List<T31EventBean> getAllEventsSortedByDate() {
        return eventRepository.findAllByEventStartDateDesc();
    }
    // ------------------------------------------------------------------------------------------------------

    /**
     * 查詢有效期內的活動（開始日期 ≤ 今日且結束日期 ≥ 今日），依照活動開始日期從最新到最舊排序。
     * 
     * @return 有效期內的活動列表
     */
    public List<T31EventBean> getValidEventsSortedByDate() {
        LocalDate today = LocalDate.now();
        // 先取得所有活動，再過濾出有效期內的活動
        List<T31EventBean> validEvents = getAllEvents().stream()
                .filter(event -> !event.getEventStartDate().isAfter(today)
                        && !event.getEventEndDate().isBefore(today))
                .collect(Collectors.toList());
        // 依照活動開始日期進行降序排序（最新活動在前）
        validEvents.sort(Comparator.comparing(T31EventBean::getEventStartDate).reversed());
        return validEvents;
    }

    // ------------------------------------------------------------------------------------------------------
    /**
     * 搜尋特定日期範圍內的活動。
     * 
     * @param startDate 開始日期
     * @param endDate   結束日期
     * @return 活動列表
     */
    public List<T31EventBean> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return t31EventRepository.findByEventStartDateBetween(startDate, endDate);
    }

    // -----------------------------------------------------------------------------------------------------
    // 新增節目
    @Transactional
    public T31EventBean insertEventWithImages(T31EventBean event, List<String> imagePaths) {
        T51VenueBean venue = venueRepository.findById(event.getVenue().getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found"));
        event.setVenue(venue);

        try {
            if (event.getEventCategory() == null || event.getEventCategory().getEventCategoryId() == null) {
                throw new RuntimeException("活動類別資訊不完整");
            }

            T32EventCategoryBean category = eventCategoryRepository
                    .findById(event.getEventCategory().getEventCategoryId())
                    .orElseThrow(() -> new RuntimeException(
                            "無法找到對應的類別，ID: " + event.getEventCategory().getEventCategoryId()));
            event.setEventCategory(category);

            event.setEventCreationDate(LocalDate.now());
            T31EventBean savedEvent = eventRepository.save(event);
            System.out.println("活動保存成功，ID: " + savedEvent.getEventId());

            Hibernate.initialize(savedEvent.getEventCategory());

            List<T34EventImagesBean> eventImages = new ArrayList<>();

            if (imagePaths != null && !imagePaths.isEmpty()) {
                Path imageDir = Paths.get("C:/image/eventimage/");
                Files.createDirectories(imageDir);

                int imageIndex = 1;
                for (String originalPath : imagePaths) {
                    if (originalPath == null || originalPath.trim().isEmpty()) {
                        log.warn("無效的圖片路徑，跳過：{}", originalPath);
                        continue;
                    }

                    try {
                        String fileName;
                        if (originalPath.startsWith("http")) {
                            fileName = Paths.get(new URI(originalPath).getPath()).getFileName().toString();
                        } else {
                            fileName = savedEvent.getEventId() + ".jpg";

                        }
                        Path targetPath = imageDir.resolve(fileName);

                        if (originalPath.startsWith("http")) {
                            try (InputStream in = URI.create(originalPath).toURL().openStream()) {
                                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } else if (originalPath.startsWith("data:image")) {
                            byte[] decodedBytes = Base64.getDecoder().decode(originalPath.split(",")[1]);
                            Files.write(targetPath, decodedBytes);
                        } else {
                            Path sourcePath = Paths.get(originalPath.trim());
                            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        String baseUrl = "http://192.168.23.89:8080/eventimage/";
                        String relativePath = baseUrl + fileName;

                        T34EventImagesBean imageBean = new T34EventImagesBean();
                        imageBean.setEvent(savedEvent);
                        imageBean.setImageUrl(relativePath);
                        imageBean.setSortOrder(imageIndex);
                        imageBean.setIsCover(imageIndex == 1);

                        eventImageRepository.save(imageBean);
                        eventImages.add(imageBean);

                        System.out.println("圖片已成功保存到：" + targetPath);
                        imageIndex++;
                    } catch (Exception e) {
                        log.error("圖片處理失敗：{}，原因：{}", originalPath, e.getMessage(), e);
                    }
                }
            }
            savedEvent.setImages(eventImages);
            return eventRepository.save(savedEvent);
        } catch (Exception e) {
            System.err.println("新增活動失敗：" + e.getMessage());
            throw new RuntimeException("新增活動時出現錯誤：" + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    // 更新節目
    @Transactional
    public T31EventBean updateEventWithImages(T31EventBean event, List<String> imagePaths) {
        if (event == null || event.getEventId() == null) {
            throw new IllegalArgumentException("Event cannot be null or has no ID");
        }

        // 查詢既有活動
        T31EventBean existingEvent = eventRepository.findById(event.getEventId())
                .orElseThrow(() -> new IllegalStateException("Event not found with ID: " + event.getEventId()));

        // 確保 Hibernate 追蹤 images 屬性
        Hibernate.initialize(existingEvent.getImages());

        // 更新活動基本資訊
        existingEvent.setEventName(event.getEventName());
        existingEvent.setEventDescription1(event.getEventDescription1());
        existingEvent.setEventPrice(event.getEventPrice());
        existingEvent.setEventStartDate(event.getEventStartDate());
        existingEvent.setEventEndDate(event.getEventEndDate());
        existingEvent.setEventUpdateDate(LocalDate.now());

        // 處理圖片
        if (imagePaths != null && !imagePaths.isEmpty()) {
            Path imageDir = Paths.get("C:/image/eventimage/");
            try {
                Files.createDirectories(imageDir);
            } catch (IOException e) {
                throw new RuntimeException("無法建立圖片目錄: " + imageDir, e);
            }

            // **刪除舊圖片**
            Iterator<T34EventImagesBean> iterator = existingEvent.getImages().iterator();
            while (iterator.hasNext()) {
                T34EventImagesBean image = iterator.next();
                try {
                    // 轉換 URL 為本機檔案路徑
                    String imagePath = image.getImageUrl().replace("http://192.168.23.89:8080/eventimage/", "");
                    Path oldImagePath = imageDir.resolve(imagePath);
                    Files.deleteIfExists(oldImagePath);
                } catch (IOException e) {
                    log.error("刪除舊圖片失敗: {}，錯誤: {}", image.getImageUrl(), e.getMessage(), e);
                }
                iterator.remove(); // Hibernate 會自動刪除
            }

            List<T34EventImagesBean> eventImages = new ArrayList<>();
            int imageIndex = 1;
            for (String originalPath : imagePaths) {
                if (originalPath == null || originalPath.trim().isEmpty()) {
                    log.warn("無效的圖片路徑，跳過：{}", originalPath);
                    continue;
                }
                try {
                    String fileName;
                    if (originalPath.startsWith("http")) {
                        // 使用原始圖片名稱
                        fileName = Paths.get(new URI(originalPath).getPath()).getFileName().toString();
                    } else {
                        // 重新命名
                        fileName = existingEvent.getEventId() + ".jpg";
                    }
                    Path targetPath = imageDir.resolve(fileName);

                    if (originalPath.startsWith("http")) {
                        try (InputStream in = URI.create(originalPath).toURL().openStream()) {
                            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } else if (originalPath.startsWith("data:image")) {
                        byte[] decodedBytes = Base64.getDecoder().decode(originalPath.split(",")[1]);
                        Files.write(targetPath, decodedBytes);
                    } else {
                        Path sourcePath = Paths.get(originalPath.trim());
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    String baseUrl = "http://192.168.23.89:8080/eventimage/";
                    String relativePath = baseUrl + fileName;

                    T34EventImagesBean imageBean = new T34EventImagesBean();
                    imageBean.setEvent(existingEvent);
                    imageBean.setImageUrl(relativePath);
                    imageBean.setSortOrder(imageIndex);
                    imageBean.setIsCover(imageIndex == 1);

                    eventImageRepository.save(imageBean);
                    eventImages.add(imageBean);
                    log.info("圖片已成功保存到：{}", targetPath);
                    imageIndex++;
                } catch (Exception e) {
                    log.error("圖片處理失敗：{}，原因：{}", originalPath, e.getMessage(), e);
                }
            }

            // **確保 images 屬性不為 null，並重新添加**
            existingEvent.getImages().clear();
            existingEvent.getImages().addAll(eventImages);
        }

        return eventRepository.save(existingEvent);
    }

    // -----------------------------------------------------------------------------------------------------
    /**
     * 根據 ID 刪除活動
     * 
     * @param eventId 活動 ID
     */
    @Transactional
    public void deleteEventById(Integer eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
        } else {
            throw new IllegalArgumentException("活動 ID 不存在: " + eventId);
        }
    }

    /**
     * 刪除所有活動
     */
    @Transactional
    public void deleteAllEvents() {
        eventRepository.deleteAll();
    }

    // -----------------------------------------------------------------------------------------------------
    // 新增圖片相關方法
    @Transactional
    public List<T34EventImagesBean> handleEventImages(T31EventBean event, List<String> imagePaths,
            String categoryName) {
        List<T34EventImagesBean> newImages = new ArrayList<>();
        if (imagePaths == null || imagePaths.isEmpty()) {
            return newImages;
        }

        Path imageDir = Paths.get("C:/image/eventimage"
                + ""
                + "", categoryName);// ("C:\\Users\\User\\Desktop\\eventImages", categoryName)
        try {
            Files.createDirectories(imageDir);

            for (int i = 0; i < imagePaths.size(); i++) {
                String originalPath = imagePaths.get(i);
                if (originalPath == null || originalPath.trim().isEmpty()) {
                    continue;
                }

                T34EventImagesBean imageBean = processAndSaveImage(
                        event, originalPath, imageDir, i, categoryName);
                if (imageBean != null) {
                    newImages.add(imageBean);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("圖片處理失敗: " + e.getMessage(), e);
        }
        return newImages;
    }

    private T34EventImagesBean processAndSaveImage(
            T31EventBean event, String originalPath, Path imageDir,
            int index, String categoryName) throws IOException {

        Path sourcePath = Paths.get(originalPath.trim());
        if (!isValidImageFile(sourcePath)) {
            log.warn("無效的圖片格式或大小: {}", originalPath);
            return null;
        }

        String fileName = String.format("%s_%d_%d.jpg",
                categoryName, event.getEventId(), (index + 1));
        Path targetPath = imageDir.resolve(fileName);

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        T34EventImagesBean imageBean = new T34EventImagesBean();
        imageBean.setEvent(event);
        imageBean.setImageUrl(targetPath.toString());
        imageBean.setIsCover(index == 0);
        imageBean.setSortOrder(index + 1);
        imageBean.setAltText(event.getEventName() + "_image_" + (index + 1));

        return eventImagesRepository.save(imageBean);
    }

    public void deleteEventImages(List<T34EventImagesBean> images) {
        if (images != null) {
            for (T34EventImagesBean image : images) {
                try {
                    Files.deleteIfExists(Paths.get(image.getImageUrl()));
                    eventImagesRepository.delete(image);
                } catch (IOException e) {
                    log.error("刪除圖片失敗: " + image.getImageUrl(), e);
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------

    // 驗證圖片文件方法
    private boolean isValidImageFile(Path filePath) {
        if (filePath == null) {
            return false;
        }

        // 獲取文件路徑
        String fileName = filePath.getFileName().toString().toLowerCase();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }

        // 有效圖片格式列表
        Set<String> validExtensions = Set.of("jpg", "jpeg", "png", "gif", "bmp");

        // 檢查圖片路徑是否存在
        try {
            return Files.exists(filePath)
                    && Files.isRegularFile(filePath)
                    && validExtensions.contains(extension);
        } catch (SecurityException e) {
            // 紀錄異常
            return false;
        }
    }

    // -----------------------------------------------------------------------------------------
    /**
     * 根據活動名稱搜尋活動--無需額外處理大小寫問題 中文英文都可處理，因編寫文字格式是UTF-8
     * 
     * @param eventName 活動名稱
     * @return 符合條件的活動列表
     */
    public List<T31EventBean> findByEventName(String eventName) {
        if (eventName == null || eventName.isEmpty()) {
            return Collections.emptyList(); // 返回空列表
        }
        return eventRepository.findByEventNameContainingIgnoreCase(eventName);
    }
    // -----------------------------------------------------------------------------------------------------

    /**
     * 驗證活動是否存在
     * 
     * @param eventId 活動 ID
     * @return 存在的活動資料（若不存在則拋出例外）
     */
    public T31EventBean validateEventExists(Integer eventId) {
        return getEventById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("活動不存在ID" + "：" + eventId));
    }

    public List<T32EventCategoryBean> CategoryType;

    public List<T31EventBean> getEventsByEventCategoryType(Integer eventCategoryType) {
        return eventRepository.findByEventCategoryEventCategoryType(eventCategoryType);
    }
    // -----------------------------------------------------------------------------------------------------

    // 活動發布狀態
    @Transactional
    public void updateEventPublishStatus(Integer eventId, boolean publish, boolean clearPublishDate) {
        eventRepository.updatePublishStatus(eventId, publish, clearPublishDate);
    }

    // 發布活動
    @Transactional
    public void publishEvent(Integer eventId) {
        updateEventPublishStatus(eventId, true, false);
    }

    // 取消發布活動
    @Transactional
    public void unpublishEvent(Integer eventId, boolean clearPublishDate) {
        updateEventPublishStatus(eventId, false, clearPublishDate);
    }

    // 獲取已發布的活動列表
    public List<EventDetailResponseDTO> getPublishedEvents() {
        return eventRepository.findByIsPublishedTrue().stream()
                .map(EventDetailResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 獲取未發布的活動列表
    public List<EventDetailResponseDTO> getUnpublishedEvents() {
        return eventRepository.findByIsPublishedFalse().stream()
                .map(EventDetailResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 回傳已發佈的活動
    public List<T31EventBean> getPublishedEventsForSearch() {
        return eventRepository.findByIsPublishedTrue();
    }

    /**
     * 根據場地 ID 查詢活動
     * 
     * @param venueId 場地 ID
     * @return 活動列表
     */
    public List<T31EventBean> getEventsByVenueId(String venueId) {
        return eventRepository.findByVenueId(venueId);
    }

    /**
     * 根據場地名稱查詢活動（精確匹配）
     * 
     * @param venueName 場地名稱
     * @return 活動列表
     */
    public List<T31EventBean> getEventsByVenueName(String venueName) {
        return eventRepository.findByVenueVenueName(venueName);
    }

    /**
     * 根據場地名稱查詢活動（模糊匹配）
     * 
     * @param venueName 場地名稱
     * @return 活動列表
     */
    public List<T31EventBean> searchEventsByVenueName(String venueName) {
        return eventRepository.findByVenueVenueNameContainingIgnoreCase(venueName);
    }

    // 新增的圖片方法
    @Transactional
    public T31EventBean insertEventWithMultipartImages(T31EventBean event, List<MultipartFile> images) {
        // 先查詢場地 & 類別，確保存在
        T51VenueBean venue = venueRepository.findById(event.getVenue().getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found"));
        event.setVenue(venue);

        T32EventCategoryBean category = eventCategoryRepository
                .findById(event.getEventCategory().getEventCategoryId())
                .orElseThrow(() -> new RuntimeException("無法找到對應類別"));
        event.setEventCategory(category);

        event.setEventCreationDate(LocalDate.now());
        T31EventBean savedEvent = eventRepository.save(event);

        // 處理圖片 (MultipartFile)
        if (images != null && !images.isEmpty()) {
            Path imageDir = Paths.get("C:/image/eventimage/");
            try {
                Files.createDirectories(imageDir);
            } catch (IOException e) {
                throw new RuntimeException("無法建立圖片目錄：" + imageDir, e);
            }

            List<T34EventImagesBean> eventImages = new ArrayList<>();
            int imageIndex = 1;
            for (MultipartFile file : images) {
                if (file.isEmpty())
                    continue; // 跳過空檔案

                // 自訂檔名
                String fileName = category.getEventCategoryName()
                        + savedEvent.getEventId()
                        + "_" + imageIndex + ".jpg";
                Path targetPath = imageDir.resolve(fileName);

                // 將檔案寫進硬碟
                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("圖片上傳失敗：" + file.getOriginalFilename(), e);
                }

                // 設定對外 URL（或看你要怎麼組合）
                String baseUrl = "http://192.168.23.89:8080/eventimage/";
                String imageUrl = baseUrl + fileName;

                T34EventImagesBean imageBean = new T34EventImagesBean();
                imageBean.setEvent(savedEvent);
                imageBean.setImageUrl(imageUrl);
                imageBean.setSortOrder(imageIndex);
                imageBean.setIsCover(imageIndex == 1);

                eventImageRepository.save(imageBean);
                eventImages.add(imageBean);

                imageIndex++;
            }

            savedEvent.setImages(eventImages);
            savedEvent = eventRepository.save(savedEvent);
        }

        return savedEvent;
    }

    // 更新票券
    @Transactional
    public T31EventBean updateEventWithMultipartImages(Integer eventId, T31EventBean event,
            List<MultipartFile> images) {
        // 查詢現有票券
        T31EventBean existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("票券 ID " + eventId + " 不存在"));

        // 更新基本欄位
        existingEvent.setEventName(event.getEventName());
        existingEvent.setEventDescription1(event.getEventDescription1());
        existingEvent.setEventPrice(event.getEventPrice());
        existingEvent.setEventStartDate(event.getEventStartDate());
        existingEvent.setEventEndDate(event.getEventEndDate());
        existingEvent.setIsPublished(event.getIsPublished());
        existingEvent.setEventUpdateDate(LocalDate.now());
        existingEvent.setEventCategory(event.getEventCategory());
        existingEvent.setVenue(event.getVenue());

        // 如果有新圖片上傳，則先刪除舊圖片，再儲存新圖片
        if (images != null && !images.isEmpty()) {
            // 刪除舊圖片（從檔案系統與資料庫）
            Iterator<T34EventImagesBean> iterator = existingEvent.getImages().iterator();
            Path imageDir = Paths.get("C:/image/eventimage/");
            while (iterator.hasNext()) {
                T34EventImagesBean oldImage = iterator.next();
                try {
                    // 假設圖片 URL 是 "http://xxx/eventimage/檔名"
                    String fileName = Paths.get(new URI(oldImage.getImageUrl()).getPath()).getFileName().toString();
                    Path oldImagePath = imageDir.resolve(fileName);
                    Files.deleteIfExists(oldImagePath);
                } catch (Exception e) {
                    // 可以記錄錯誤但不阻斷流程
                    e.printStackTrace();
                }
                iterator.remove();
                eventImageRepository.delete(oldImage);
            }

            // 儲存新圖片
            List<T34EventImagesBean> newImages = new ArrayList<>();
            try {
                Files.createDirectories(imageDir);
            } catch (IOException e) {
                throw new RuntimeException("無法建立圖片目錄：" + imageDir, e);
            }
            int imageIndex = 1;
            for (MultipartFile file : images) {
                if (file.isEmpty())
                    continue;
                String fileName = existingEvent.getEventCategory().getEventCategoryName() +
                        existingEvent.getEventId() + "_" + imageIndex + ".jpg";
                Path targetPath = imageDir.resolve(fileName);
                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("圖片上傳失敗：" + file.getOriginalFilename(), e);
                }
                String baseUrl = "http://192.168.23.89:8080/eventimage/";
                String imageUrl = baseUrl + fileName;

                T34EventImagesBean imageBean = new T34EventImagesBean();
                imageBean.setEvent(existingEvent);
                imageBean.setImageUrl(imageUrl);
                imageBean.setSortOrder(imageIndex);
                imageBean.setIsCover(imageIndex == 1);
                eventImageRepository.save(imageBean);
                newImages.add(imageBean);
                imageIndex++;
            }
            existingEvent.getImages().clear();
            existingEvent.getImages().addAll(newImages);

        }

        // 儲存更新後的票券
        return eventRepository.save(existingEvent);
    }

}
