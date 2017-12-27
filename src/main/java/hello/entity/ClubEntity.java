package hello.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clubinfo")
public class ClubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String introduction;
    // 学术类，文娱类等
    private String type;
    private String imageUrl;

    @OneToMany(mappedBy = "club")
    private Set<UserClubEntity> userClubs = new HashSet<UserClubEntity>();

    @OneToMany
    @JoinColumn(name = "club_id")
    private Set<ActivityEntity> activities = new HashSet<ActivityEntity>();

    @OneToMany
    @JoinColumn(name = "apply_id")
    private Set<ApplicationEntity> applies = new HashSet<ApplicationEntity>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<UserClubEntity> getUserClubs() {
        return userClubs;
    }

    public void setUserClubs(Set<UserClubEntity> userClubs) {
        this.userClubs = userClubs;
    }

    public Set<ApplicationEntity> getApplies() {
        return applies;
    }

    public void setApplies(Set<ApplicationEntity> applies) {
        this.applies = applies;
    }
}
