package com.lalala.spring.trvapp.repository;

import com.lalala.spring.trvapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findBySocialUniqId(String socialUniqId);

}
