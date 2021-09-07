package com.lalala.spring.trvapp.repository;

import com.lalala.spring.trvapp.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    public Optional<Auth> findByAuthIdx(Long idx);
}
