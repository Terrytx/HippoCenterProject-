package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t43_reset")
public class T43ResetPasswordBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reset_id")
    private Integer resetId;

    @ManyToOne
    @JoinColumn(name = "member_Id", nullable = false)
    @JsonBackReference
    private T41MemberBean member;

    @Column(name = "reset_time", nullable = false)
    private LocalDateTime resetTime;

    @Column(name = "reset_cord", nullable = false)
    private String resetCode;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "reset_outtime", nullable = false)
    private java.util.Date resetOuttime;

    @Column(name = "reset_state", nullable = false)
    private String resetState;

	public Integer getResetId() {
		return resetId;
	}

	public void setResetId(Integer resetId) {
		this.resetId = resetId;
	}

	public T41MemberBean getMember() {
		return member;
	}

	public void setMember(T41MemberBean member) {
		this.member = member;
	}

	public LocalDateTime getResetTime() {
		return resetTime;
	}

	public void setResetTime(LocalDateTime resetTime) {
		this.resetTime = resetTime;
	}

	public String getResetCode() {
		return resetCode;
	}

	public void setResetCode(String resetCode) {
		this.resetCode = resetCode;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public java.util.Date getResetOuttime() {
		return resetOuttime;
	}

	public void setResetOuttime(java.util.Date resetOuttime) {
		this.resetOuttime = resetOuttime;
	}

	public String getResetState() {
		return resetState;
	}

	public void setResetState(String resetState) {
		this.resetState = resetState;
	}


}
