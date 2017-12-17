package hello.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activity")
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String theme;
    private String content;
    private String target_dep;
    @Temporal(TemporalType.TIMESTAMP)
    private Date start_time;
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date cred_at;
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date last_modified;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private UserEntity user;

    public ActivityEntity() {
    }

    public ActivityEntity(String theme, String content, String target_dep, Date start_time) {
        this.theme = theme;
        this.content = content;
        this.target_dep = target_dep;
        this.start_time = start_time;
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

    public String getTarget_dep() {
        return target_dep;
    }

    public void setTarget_dep(String target_dep) {
        this.target_dep = target_dep;
    }


    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getCred_at() {
        return cred_at;
    }

    public Date getLast_modified() {
        return last_modified;
    }
}
