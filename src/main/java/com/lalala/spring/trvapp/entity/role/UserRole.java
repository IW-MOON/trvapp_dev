package com.lalala.spring.trvapp.entity.role;

import com.lalala.spring.trvapp.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_IDX",  nullable = false)
    private Long userRoleIdx;

    @ManyToOne
    @JoinColumn(name = "USER_IDX")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ROLE_IDX")
    private Role role;


    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
