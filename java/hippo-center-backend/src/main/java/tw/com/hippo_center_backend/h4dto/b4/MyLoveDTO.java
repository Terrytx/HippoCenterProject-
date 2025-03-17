package tw.com.hippo_center_backend.h4dto.b4;

import java.time.LocalDate;

public class MyLoveDTO {
    private Integer myloveNum;  // 最愛編號
    private Integer myloveId;   // 最愛ID
    private String myloveType;  // 最愛類型（p 或 e）
    private String name;        // 商品名稱
    private String eventName;   // 展演名稱
    private Integer price;   // 商品價格
    private Integer eventPrice;   // 展演價格
    private String imageUrl;    // 商品圖片 URL
    private LocalDate eventStartDate;

    // Getters and Setters
    public Integer getMyloveNum() {
        return myloveNum;
    }

    public void setMyloveNum(Integer myloveNum) {
        this.myloveNum = myloveNum;
    }

    public Integer getMyloveId() {
        return myloveId;
    }

    public void setMyloveId(Integer myloveId) {
        this.myloveId = myloveId;
    }

    public String getMyloveType() {
        return myloveType;
    }

    public void setMyloveType(String myloveType) {
        this.myloveType = myloveType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getEventPrice() {
		return eventPrice;
	}

	public void setEventPrice(Integer eventPrice) {
		this.eventPrice = eventPrice;
	}

	public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

	public LocalDate getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(LocalDate eventStartDate) {
		this.eventStartDate = eventStartDate;
	}
    
}

//public class MyLoveDTO {
//	private Integer myloveNum;
//    private Integer myloveId;
//    private String myloveType;  // 用來表示類型 "P" 或 "E"
//    private String name;  // 這裡用來存放商品名稱或展演名稱
//
//    // Getters 和 Setters
//    public Integer getMyloveNum() {
//        return myloveNum;
//    }
//
//    public void setMyloveNum(Integer myloveNum) {
//        this.myloveNum = myloveNum;
//    }
//
//    public Integer getMyloveId() {
//        return myloveId;
//    }
//
//    public void setMyloveId(Integer myloveId) {
//        this.myloveId = myloveId;
//    }
//
//    public String getMyloveType() {
//        return myloveType;
//    }
//
//    public void setMyloveType(String myloveType) {
//        this.myloveType = myloveType;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//}
