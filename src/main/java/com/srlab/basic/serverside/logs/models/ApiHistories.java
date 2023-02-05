package com.srlab.basic.serverside.logs.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Schema(name = "apiHistories")
@Entity
@Table(name = "api_histories")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiHistories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "method")
    private String method;

    @Column(name = "path")
    private String path;

    @Column(name = "search")
    private String search;

    @Column(name = "body", columnDefinition = "LONG")
    private String body;

    @Column(name = "status")
    private Integer status;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_ip")
    private String userIp;

    @Column(name = "exe_date")
    private Date exeDate;

}
