package hello.entity;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notice")
public class NoticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String theme;
    private String content;
    private String targetDep;
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date credAt;
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date lastModified;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private UserEntity user;

    public NoticeEntity() {
    }

    public NoticeEntity(String theme, String content, String targetDep) {
        this.theme = theme;
        this.content = content;
        this.targetDep = targetDep;
    }

    public Long getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetDep() {
        return targetDep;
    }

    public void setTargetDep(String target_dep) {
        this.targetDep = targetDep;
    }

    public Date getCredAt() {
        return credAt;
    }

    public Date getLastModified() {
        return lastModified;
    }
}
