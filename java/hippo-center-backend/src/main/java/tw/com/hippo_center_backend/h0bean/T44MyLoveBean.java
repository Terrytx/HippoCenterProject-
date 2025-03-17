
package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t44_mylove")
public class T44MyLoveBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mylove_num")
    private Integer myloveNum;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private T41MemberBean member;

    @Column(name = "mylove_type", nullable = false)
    private Character myloveType;

    @Column(name = "mylove_id", nullable = false)
    private Integer myloveId;

    @Column(name = "addLove_time")
    private LocalDateTime addLoveTime;

    @Column(name = "name")
    private String name;
    
    @Column(name = "price")
    private Integer price;
    
    @Column(name = "imageUrl")
    private String imageUrl;
    
    @Column(name = "eventStartDate")
    private LocalDate eventStartDate;
    
    @Column(name = "eventEndDate")
    private LocalDate eventEndDate;
    
    public Integer getMyloveNum() {
        return myloveNum;
    }

    public void setMyloveNum(Integer myloveNum) {
        this.myloveNum = myloveNum;
    }

    public T41MemberBean getMember() {
        return member;
    }

    public void setMember(T41MemberBean member) {
        this.member = member;
    }

    public Character getMyloveType() {
        return myloveType;
    }

    public void setMyloveType(Character myloveType) {
        this.myloveType = myloveType;
    }

    public Integer getMyloveId() {
        return myloveId;
    }

    public void setMyloveId(Integer myloveId) {
        this.myloveId = myloveId;
    }

    public LocalDateTime getAddLoveTime() {
		return addLoveTime;
	}

	public void setAddLoveTime(LocalDateTime addLoveTime) {
		this.addLoveTime = addLoveTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
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

	public LocalDate getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(LocalDate eventEndDate) {
		this.eventEndDate = eventEndDate;
	}


}