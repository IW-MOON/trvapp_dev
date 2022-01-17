package com.lalala.spring.trvapp.repository.role;

import com.lalala.spring.trvapp.entity.role.Role;
import com.lalala.spring.trvapp.entity.role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findAllByRoleType(RoleType roleType);
}
