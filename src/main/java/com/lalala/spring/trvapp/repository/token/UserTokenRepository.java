package com.lalala.spring.trvapp.repository.token;

import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.entity.token.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    public Optional<UserToken> findByUser(User user);
    public Optional<UserToken> findByRefreshToken(String refreshToken);
}
