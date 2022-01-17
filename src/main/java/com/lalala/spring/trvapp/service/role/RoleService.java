package com.lalala.spring.trvapp.service.role;

import com.lalala.spring.trvapp.entity.role.Role;
import com.lalala.spring.trvapp.entity.role.RoleType;
import com.lalala.spring.trvapp.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByRoleType(RoleType roleType) {

        Optional<Role> roleOptional = roleRepository.findAllByRoleType(roleType);
        if(roleOptional.isEmpty()){
            return roleRepository.save(new Role(roleType));
        }
        return roleOptional.get();
    }

}
