package com.lalala.spring.trvapp.repository.user;

import com.lalala.spring.trvapp.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialUniqId(String socialUniqId);

}
