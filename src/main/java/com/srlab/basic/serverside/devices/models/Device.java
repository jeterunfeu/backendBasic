package com.srlab.basic.serverside.devices.models;

import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.files.models.AvailableFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "device")
@Entity
@Table(name = "device")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "DEVICE_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName = "DEVICE_SEQ", //시퀀스 이름
        initialValue = 1, //시작값
        allocationSize = 1 //메모리를 통해 할당할 범위 사이즈
)
public class Device extends CustomAuditable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator = "DEVICE_SEQ_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
    private Long seq;
    @Column(name = "device_id", unique = true)
    private String deviceId;
    @Column(name = "password")
    private String password;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "nvrIp")
    private String nvrIp;
    @Column(name = "sourceUrl")
    private String sourceUrl;
    @Column(name = "streamUrl")
    private String streamUrl;
    @Column(name = "channel")
    private Integer channel;
    @Column(name = "role")
    private String role;
    @Column(name = "version")
    private String version;
    @Column(name = "device_type")
    private String deviceType;
    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private Device root;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Device parent;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 1")
    private Long leftNode;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 2")
    private Long rightNode;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 0")
    private Long depth;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 1")
    private Long nodeOrder;

//    @OneToMany(mappedBy = "device")
//    private List<AvailableFile> files = new ArrayList<>();
}
