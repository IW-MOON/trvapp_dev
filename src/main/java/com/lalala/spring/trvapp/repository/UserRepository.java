package com.lalala.spring.trvapp.repository;

import com.lalala.spring.trvapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
