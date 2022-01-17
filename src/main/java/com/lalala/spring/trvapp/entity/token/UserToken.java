package com.lalala.spring.trvapp.entity.token;

import com.lalala.spring.trvapp.dto.token.TokenResponse;
import com.lalala.spring.trvapp.entity.BaseEntity;
import com.lalala.spring.trvapp.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "USER_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_TOKEN_IDX",  nullable = false)
    private Long userTokenIdx;

    @OneToOne
    @JoinColumn(name = "USER_IDX")
    private User user;

    @Column(name = "REFRESH_TOKEN",  nullable = false)
    private String refreshToken;

    @Column(name = "CREATION_DTM",  nullable = false)
    private LocalDateTime creationDtm;

    @Column(name = "EXPIRATION_DTM",  nullable = false)
    private LocalDateTime expirationDtm;

    public void updateRefreshToken(TokenResponse tokenResponse){
        this.creationDtm = tokenResponse.getCreationDtm();
        this.refreshToken = tokenResponse.getRefreshToken();
        this.expirationDtm = tokenResponse.getExpirationDtm();
    }

}
