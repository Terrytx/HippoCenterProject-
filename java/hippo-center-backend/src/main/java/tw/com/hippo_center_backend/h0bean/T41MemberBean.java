package tw.com.hippo_center_backend.h0bean;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t41_member")
public class T41MemberBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "member_type", nullable = false)
    private String memberType = "general";

    @Column(name = "account", nullable = false, unique = true)
    private String account;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "modify_date")
    private LocalDate modifyDate;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T23CartBean> carts;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T25OrderListBean> orders;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T27PromotionMemberBean> promotions;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T43ResetPasswordBean> reset;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T44MyLoveBean> myLoves;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T13ReservationBean> reservation;

    @OneToMany(mappedBy = "memberId", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T33TicketBean> ticket;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

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

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDate modifyDate) {
        this.modifyDate = modifyDate;
    }

    public List<T23CartBean> getCarts() {
        return carts;
    }

    public void setCarts(List<T23CartBean> carts) {
        this.carts = carts;
    }

    public List<T25OrderListBean> getOrders() {
        return orders;
    }

    public void setOrders(List<T25OrderListBean> orders) {
        this.orders = orders;
    }

    public List<T27PromotionMemberBean> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<T27PromotionMemberBean> promotions) {
        this.promotions = promotions;
    }

    public List<T44MyLoveBean> getMyLoves() {
        return myLoves;
    }

    public void setMyLoves(List<T44MyLoveBean> myLoves) {
        this.myLoves = myLoves;
    }

    public List<T13ReservationBean> getReservation() {
        return reservation;
    }

    public void setReservation(List<T13ReservationBean> reservation) {
        this.reservation = reservation;
    }

    public List<T33TicketBean> getTicket() {
        return ticket;
    }

    public void setTicket(List<T33TicketBean> ticket) {
        this.ticket = ticket;
    }

    public List<T43ResetPasswordBean> getReset() {
        return reset;
    }

    public void setReset(List<T43ResetPasswordBean> reset) {
        this.reset = reset;
    }

    @Override
    public String toString() {
        return "T41MemberBean [memberId=" + memberId + ", memberType=" + memberType + ", account=" + account
                + ", password=" + password + ", name=" + name + ", gender=" + gender + ", birthday=" + birthday
                + ", phone=" + phone + ", address=" + address + ", createDate=" + createDate + ", modifyDate="
                + modifyDate + ", reset=" + reset + ", myLoves=" + myLoves + "]";
    }

}
