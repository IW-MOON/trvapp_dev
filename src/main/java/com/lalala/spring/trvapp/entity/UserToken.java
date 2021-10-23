package com.lalala.spring.trvapp.entity;

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


    public void updateRefreshToken(String refreshToken){
        this.creationDtm = LocalDateTime.now();
        this.refreshToken = refreshToken;
    }

}
