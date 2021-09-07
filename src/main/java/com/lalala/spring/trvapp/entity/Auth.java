package com.lalala.spring.trvapp.entity;

import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "AUTH")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTH_IDX",  nullable = false)
    private Long authIdx;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "JOIN_YN", length = 1, nullable = false)
    private String joinYn;

    @Column(name = "SOCIAL_AUTH_TYPE", length = 30)
    @Enumerated(EnumType.STRING)
    private SocialAuthType socialAuthType;

    @Override
    public String toString() {
        return "Auth{" +
                "authIdx=" + authIdx +
                ", token='" + token + '\'' +
                ", joinYn='" + joinYn + '\'' +
                ", socialAuthType=" + socialAuthType +
                '}';
    }
    public void updateJoinYn(){
        this.joinYn = "Y";
    }

}
