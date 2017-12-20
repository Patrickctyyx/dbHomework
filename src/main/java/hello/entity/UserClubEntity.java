package hello.entity;

import javax.persistence.*;

@Entity
@Table(name = "usertoclub")
public class UserClubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ClubEntity club;

    @ManyToOne
    private UserEntity user;

    private String userIdentity = "officer";

    public Long getId() {
        return id;
    }

    public ClubEntity getClub() {
        return club;
    }

    public void setClub(ClubEntity club) {
        this.club = club;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }
}
