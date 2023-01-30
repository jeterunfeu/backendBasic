package com.srlab.basic.serverside.auditables;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CustomAuditable {

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="inserted_date", columnDefinition="timestamp")
    private Date insertedDate;

    @Column(name="inserted_ip")
    private String insertedIp;

    @CreatedBy
    @Column(name="inserted_id")
    private Long insertedId;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date", columnDefinition="timestamp")
    private Date updatedDate;

    @Column(name="updated_ip")
    private String updatedIp;

    @LastModifiedBy
    @Column(name="updated_id")
    private Long updatedId;

    //available change
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="deleted_date", columnDefinition="timestamp")
    private Date deletedDate;

    @Column(name="deleted_ip")
    private String deletedIp;

    @Column(name="deleted_id")
    private Long deletedId;

    @Column(name="view_count")
    private Long viewCount;

    @Column(name="like_count")
    private Long likeCount;

    @Column(name="dislike_count")
    private Long dislikeCount;

    @Column(name="lock_password")
    private String lockPassword;

    @Column(name="note", columnDefinition="NCHAR")
    private String note;

    @Column(name="available", columnDefinition="CHAR(1) default 1", nullable = false)
    private Boolean available;
}
