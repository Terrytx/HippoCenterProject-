package tw.com.hippo_center_backend.h4dto.b4;

public class ResetPasswordRequestDTO {

	private String email;        // 使用者的 email
    private String contextPath;  // 設置的 context path
    private String resetCode;    // 用戶的重設碼
    private String newPassword;  // 新的密碼
    private String uuid;		 // uuid
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
    
}
