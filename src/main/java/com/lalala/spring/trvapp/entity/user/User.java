package com.lalala.spring.trvapp.entity.user;

import com.lalala.spring.trvapp.entity.BaseEntity;
import com.lalala.spring.trvapp.entity.role.RoleType;
import com.lalala.spring.trvapp.entity.role.UserRole;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserRole> roles = new ArrayList<>();

    @Column(name = "SOCIAL_AUTH_TYPE", length = 30)
    @Enumerated(EnumType.STRING)
    private SocialAuthType socialAuthType;

    @Column(name = "LAST_LOGIN_DTM")
    private LocalDateTime lastLoginDtm ;

    @Builder
    public User(String nickName, String email, String socialUniqId, SocialAuthType socialAuthType) {
        this.nickName = nickName;
        this.email = email;
        this.socialUniqId = socialUniqId;
        this.socialAuthType = socialAuthType;
        updateLastLoginDtm();
    }

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


    public List<RoleType> getRoleTypes() {

        return roles.stream()
                    .map(userRole -> userRole.getRole().getRoleType())
                    .collect(Collectors.toList());
    }

    public void addRole(UserRole userRole) {
        if(roles == null){
            roles = new ArrayList<>();
        }
        if (!roles.contains(userRole)) {
            roles.add(userRole);
        }
    }
}
