package hello.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.persistence.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clubuser")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String grade;
    private String college;
    private String major;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String qq;
    @Column(unique = true)
    private String wechat;
    @Column(unique = true)
    private String email;
    private String introduction;
    @JsonIgnore
    private String wxID;

    @Transient
    private static byte[] sharedSecret;

    @OneToMany
    @JoinColumn(name = "author_id")
    private Set<ActivityEntity> activities = new HashSet<ActivityEntity>();

    @OneToMany(mappedBy = "user")
    private Set<UserClubEntity> userClubs = new HashSet<UserClubEntity>();


    public UserEntity() {
    }

    public UserEntity(
            String name, String grade, String college, String major,
            String department,String phone,
            String qq, String wechat, String email
    ) {
        this.name = name;
        this.grade = grade;
        this.college = college;
        this.major = major;
        this.phone = phone;
        this.qq = qq;
        this.wechat = wechat;
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWxID() {
        return wxID;
    }

    public void setWxID(String wxID) {
        this.wxID = wxID;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Set<ActivityEntity> getActivities() {
        return activities;
    }

    public void setActivities(Set<ActivityEntity> activities) {
        this.activities = activities;
    }

    public Set<UserClubEntity> getUserClubs() {
        return userClubs;
    }

    public void setUserClubs(Set<UserClubEntity> userClubs) {
        this.userClubs = userClubs;
    }

    @Transient
    private static byte[] getSharedSecret() {
        if (sharedSecret == null) {
            SecureRandom random = new SecureRandom();
            sharedSecret = new byte[32];
            random.nextBytes(sharedSecret);
        }
        return sharedSecret;
    }

    @Transient
    public static void setSharedSecret() {
        SecureRandom random = new SecureRandom();
        sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);
    }

    public String generateAuthToken(String openid) {
        // Create HMAC signer
        try {
            JWSSigner signer = new MACSigner(getSharedSecret());
            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(openid)
                    .issuer("patrickcty")
                    .expirationTime(new Date(new Date().getTime() + 36000 * 1000))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            // Apply the HMAC protection
            try {
                signedJWT.sign(signer);
            } catch (JOSEException e) {
                e.printStackTrace();
            }
            return signedJWT.serialize();
        } catch (KeyLengthException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String checkAuthToken(String authToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(authToken);
            try {
                JWSVerifier verifier = new MACVerifier(getSharedSecret());
                if (signedJWT.verify(verifier) &&
                        new Date().before(
                                signedJWT.getJWTClaimsSet().
                                        getExpirationTime()) &&
                        signedJWT.getJWTClaimsSet().getIssuer().equals("patrickcty")
                        ) {
                    // 这个地方应该更新一下 token 的过期时间，但是没找到怎么做
                    // => 这个好像不能更新，只能更新 token，因为事件信息是包含在 token 里面的
                    return signedJWT.getJWTClaimsSet().getSubject();
                }
                else {
                    return "";
                }
            } catch (JOSEException e) {
                e.printStackTrace();
                return "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
