package com.srlab.basic.serverside.auditables;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomAuditable {

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.szss", timezone = "Asia/Seoul")
    @Column(name="inserted_date", columnDefinition="timestamp")
    private Date insertedDate;

    @Column(name="inserted_ip")
    private String insertedIp;

    @Column(name="inserted_string_id")
    private String insertedStringId;

    @CreatedBy
    @Column(name="inserted_id")
    private Long insertedId;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name="updated_date", columnDefinition="timestamp")
    private Date updatedDate;

    @Column(name="updated_ip")
    private String updatedIp;

    @LastModifiedBy
    @Column(name="updated_id")
    private Long updatedId;

    //available change
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name="deleted_date", columnDefinition="timestamp")
    private Date deletedDate;

    @Column(name="deleted_ip")
    private String deletedIp;

    @Column(name="deleted_id")
    private Long deletedId;

    @Column(name="view_count")
    private Long viewCount;

    @Column(name="like_member")
    private String likeMember;

    @Transient
    private Integer likeCount;

    @Column(name="dislike_member")
    private String dislikeMember;

    @Transient
    private Integer dislikeCount;

    @Column(name="lock_password")
    private String lockPassword;

    @Column(name="note", columnDefinition="NCHAR")
    private String note;

    @Column(name="available", columnDefinition="CHAR(1) default 1")
    private Boolean available;

    public Integer getLikeCount() {
        return getLikeMember() == null ? 0 : getLikeMember().length();
    }

    public Integer getDislikeCount() {
        return getDislikeMember() == null ? 0 : getDislikeMember().length();
    }

}
