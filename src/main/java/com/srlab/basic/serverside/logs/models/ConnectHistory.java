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
@SequenceGenerator(
        name="CONNECT_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName="CONNECT_SEQ", //시퀀스 이름
        initialValue=1, //시작값
        allocationSize=1 //메모리를 통해 할당할 범위 사이즈
)
public class ConnectHistory {

    @Id
    @GeneratedValue(
            strategy=GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator="CONNECT_SEQ_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
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
