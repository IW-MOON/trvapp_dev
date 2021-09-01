package com.lalala.spring.trvapp.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

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

    @Column(name = "SYS_CRET_DTM", nullable = false, updatable = false)
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime sysCretDtm ;

    @Column(name = "SYS_CHNG_DTM")
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime sysChngDtm ;

}
