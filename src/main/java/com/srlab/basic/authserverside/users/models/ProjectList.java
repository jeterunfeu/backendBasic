package com.srlab.basic.authserverside.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srlab.basic.serverside.auditables.CustomAuditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectList extends CustomAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "program_address")
    private String programAddress;

    @Column(name = "program_port")
    private String programPort;

    @Column(name = "program_os")
    private String programOs;

    @Column(name = "program_environment")
    private String programEnvironment;

    @Column(name = "certification", columnDefinition = "CHAR(1) default 0")
    private Boolean certification;

    @Column(name = "certificate_date")
    private Date certificateDate;

    @OneToOne
    @JoinColumn(name = "certificate_id")
    private UserInfo certificationStaff;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "projectList")
    @JsonIgnore
    private List<UserInfo> users = new ArrayList<>();

}
