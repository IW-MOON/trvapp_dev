package com.lalala.spring.trvapp.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

//    @Column(name = "SYS_CTNR_ID", length = 15, nullable = false, updatable = false)
//    @CreatedBy
//    private String sysCtnrId;
//
//    @Column(name = "SYS_CHNGR_ID", length = 15)
//    @LastModifiedBy
//    private String sysChngrId;

    @Column(name = "SYS_CRET_DTM", length = 14, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime sysCretDtm ;

    @Column(name = "SYS_CHNG_DTM", length = 14)
    @LastModifiedDate
    private LocalDateTime sysChngDtm ;

}
