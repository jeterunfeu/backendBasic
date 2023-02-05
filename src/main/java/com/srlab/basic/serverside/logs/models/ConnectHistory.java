package com.srlab.basic.serverside.logs.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

@Schema(name = "connectHistory")
@Entity
@Table(name="connect_history")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ConnectHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long seq;

    @Column(name="status")
    private String status;

    @Column(name="device")
    private String device;

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

    @Column(name="exe_date")
    private Date exeDate;

}
