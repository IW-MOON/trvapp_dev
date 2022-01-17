package com.lalala.spring.trvapp.repository.nickname;

import com.lalala.spring.trvapp.entity.nickname.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface NicknameRepository extends JpaRepository<Nickname, Long> {

    @Query( value = "select * from nickname where type = :type order by rand() limit 1", nativeQuery = true)
    public Nickname findByType(@Param("type") String type);

}
