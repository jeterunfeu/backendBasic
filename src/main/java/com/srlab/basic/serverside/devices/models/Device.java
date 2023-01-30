package com.srlab.basic.serverside.devices.models;

import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.files.models.AvailableFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device extends CustomAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "board")
    private List<AvailableFile> files = new ArrayList<>();

}
