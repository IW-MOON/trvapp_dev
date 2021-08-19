package com.lalala.spring.trvapp.repository;

import com.lalala.spring.trvapp.model.User;
import com.lalala.spring.trvapp.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    public Optional<UserToken> findByUser(User user);

}
