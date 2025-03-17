package tw.com.hippo_center_backend.h4dto.b1;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

//會員DTO
public class MemberDTO {
    public static class MemberRequest {
       
		@NotBlank(message = "帳號不得為空")
        private String account;

        @NotBlank(message = "密碼不得為空")
        @Size(min = 6, message = "密碼長度至少6位")
        private String password;

        @NotBlank(message = "會員類型不得為空")
        private String memberType;

        private String name;
        private String gender;
        private LocalDate birthday;
        private String phone;
        private String address;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
        
        public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getMemberType() {
			return memberType;
		}
		public void setMemberType(String memberType) {
			this.memberType = memberType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public LocalDate getBirthday() {
			return birthday;
		}
		public void setBirthday(LocalDate birthday) {
			this.birthday = birthday;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
    }

    public static class MemberResponse {
        
		private Integer memberId;
        private String account;
        private String memberType;
        private String name;
        private String gender;
        private LocalDate birthday;
        private String phone;
        private String address;
        private LocalDateTime createDate;
        private LocalDateTime modifyDate;
        
        // Getters and Setters
        // ... [所有字段的getter和setter方法]
        public Integer getMemberId() {
			return memberId;
		}
		public void setMemberId(Integer memberId) {
			this.memberId = memberId;
		}
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public String getMemberType() {
			return memberType;
		}
		public void setMemberType(String memberType) {
			this.memberType = memberType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public LocalDate getBirthday() {
			return birthday;
		}
		public void setBirthday(LocalDate birthday) {
			this.birthday = birthday;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public LocalDateTime getCreateDate() {
			return createDate;
		}
		public void setCreateDate(LocalDateTime createDate) {
			this.createDate = createDate;
		}
		public LocalDateTime getModifyDate() {
			return modifyDate;
		}
		public void setModifyDate(LocalDateTime modifyDate) {
			this.modifyDate = modifyDate;
		}
    }

    public static class MemberUpdateRequest {
       
		private String name;
        private String gender;
        private LocalDate birthday;
        private String phone;
        private String address;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
        
        public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public LocalDate getBirthday() {
			return birthday;
		}
		public void setBirthday(LocalDate birthday) {
			this.birthday = birthday;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
    }

    public static class PasswordUpdateRequest {
      
        @NotBlank(message = "新密碼不得為空")
        @Size(min = 6, message = "新密碼長度至少6位")
        private String newPassword;

        // Getters and Setters
        // ... [所有字段的getter和setter方法]
        public String getOldPassword() {
			return oldPassword;
		}

		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}

		public String getNewPassword() {
			return newPassword;
		}

		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}

		@NotBlank(message = "原密碼不得為空")
        private String oldPassword;

    }
}