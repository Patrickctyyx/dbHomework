package hello.entity;


import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "application")
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String grade;
    private String college;
    private String major;
    private String department;
    private String phone;
    private String email;
    private String introduction;
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date credAt;
    private String status = "pending";

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apply_id")
    private ClubEntity club;

    public ApplicationEntity() {
    }

    public ApplicationEntity(
            String name, String grade, String college, String major,
            String department, String phone, String email,
            String introduction, ClubEntity club
    ) {
        this.name = name;
        this.grade = grade;
        this.college = college;
        this.major = major;
        this.department = department;
        this.phone = phone;
        this.email = email;
        this.introduction = introduction;
        this.club = club;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Date getCredAt() {
        return credAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ClubEntity getClub() {
        return club;
    }

    public void setClub(ClubEntity club) {
        this.club = club;
    }
}
