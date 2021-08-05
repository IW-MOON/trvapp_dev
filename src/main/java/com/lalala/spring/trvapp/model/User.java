package com.lalala.spring.trvapp.model;

import com.lalala.spring.trvapp.type.SocialLoginType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "USER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NICK_NAME", length = 30)
    private String nickName;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "SOCIAL_UNIQ_ID")
    private String socialUniqId;

    @Column(name = "SOCIAL_LOGIN_TYPE", length = 30)
    @Enumerated(EnumType.STRING)
    private SocialLoginType socialLoginType;



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", socialUniqId='" + socialUniqId + '\'' +
                ", socialLoginType=" + socialLoginType +
                '}';
    }

    //    @Builder
//    public User(String nickName, String email, String socialUniqId, SocialLoginType socialLoginType) {
//        this.nickName = nickName;
//        this.email = email;
//        this.socialUniqId = socialUniqId;
//        this.socialLoginType = socialLoginType;
//    }
}
