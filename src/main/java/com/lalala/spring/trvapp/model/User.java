package com.lalala.spring.trvapp.model;

import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Table(name = "USER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_IDX",  nullable = false)
    private Long userIdx;

    @Column(name = "NICK_NAME", length = 30)
    private String nickName;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "SOCIAL_UNIQ_ID")
    private String socialUniqId;

    @Column(name = "SOCIAL_AUTH_TYPE", length = 30)
    @Enumerated(EnumType.STRING)
    private SocialAuthType socialAuthType;

    @Column(name = "LAST_LOGIN_DTM")
    private LocalDateTime lastLoginDtm ;


    @Override
    public String toString() {
        return "User{" +
                "userIdx=" + userIdx +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", socialUniqId='" + socialUniqId + '\'' +
                ", socialLoginType=" + socialAuthType +
                '}';
    }

    public void updateLastLoginDtm(){
        this.lastLoginDtm = LocalDateTime.now();
    }

//    @PrePersist
//    public void prePersist() {
//        this.lastLoginDtm = this.lastLoginDtm == null ? LocalDateTime.now() : this.lastLoginDtm;
//    }
//
//    @PreUpdate
//    public void preUpdate() {
//        this.lastLoginDtm = this.lastLoginDtm == null ? LocalDateTime.now() : this.lastLoginDtm;
//    }



    //    @Builder
//    public User(String nickName, String email, String socialUniqId, SocialLoginType socialLoginType) {
//        this.nickName = nickName;
//        this.email = email;
//        this.socialUniqId = socialUniqId;
//        this.socialLoginType = socialLoginType;
//    }
}
