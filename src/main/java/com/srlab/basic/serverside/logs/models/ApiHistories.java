package com.srlab.basic.serverside.logs.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "api_histories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Long userId;

    @Column(name = "user_ip")
    private String userIp;

    public ApiHistories(String method, String path, String search, String body, Integer status, Long userId, String userIp) {
        this.method = method;
        this.path = path;
        this.search = search;
        this.body = body;
        this.status = status;
        this.userId = userId;
        this.userIp = userIp;
    }
}
