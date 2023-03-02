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
@SequenceGenerator(
        name="API_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName="API_SEQ", //시퀀스 이름
        initialValue=1, //시작값
        allocationSize=1 //메모리를 통해 할당할 범위 사이즈
)
public class ApiHistories {

    @Id
    @GeneratedValue(
            strategy=GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator="API_SEQ_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
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
