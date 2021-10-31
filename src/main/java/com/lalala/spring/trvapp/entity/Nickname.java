package com.lalala.spring.trvapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "NICKNAME")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nickname extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NICKNAME_IDX",  nullable = false)
    private Long nicknameIdx;

    @Column(name = "WORD", nullable = false)
    private String word;

    @Column(name = "TYPE", nullable = false)
    private String type;

}
