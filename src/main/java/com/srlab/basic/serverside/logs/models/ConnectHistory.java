package com.srlab.basic.serverside.logs.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name="connect_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConnectHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long seq;

    @Column(name="user_id")
    private Long userId;

    @Column(name="os_name")
    private String osName;

    @Column(name="os_version")
    private String osVersion;

    @Column(name="browser_name")
    private String browserName;

    @Column(name="browser_version")
    private String browserVersion;

    @Column(name="user_ip")
    private String userIp;

}
