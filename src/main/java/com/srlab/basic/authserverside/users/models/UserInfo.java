package com.srlab.basic.authserverside.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srlab.basic.serverside.auditables.CustomAuditable;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends CustomAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "password_status")
    private String passwordStatus;

    @Column(name = "user_name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "cell_phone")
    private String cellPhone;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "agree", columnDefinition = "CHAR(1) default 0")
    private Boolean agree;

    @Column(name = "agree_date")
    private Date agreeDate;

    @Column(name = "certification", columnDefinition = "CHAR(1) default 0")
    private Boolean certification;

    @Column(name = "certificate_date")
    private Date certificateDate;

    @OneToOne
    @JoinColumn(name = "certificate_id")
    private UserInfo certificationStaff;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "userInfo")
    private List<UserRole> userRoles = new ArrayList<>();

//    @OneToOne(mappedBy = "userInfo")
//    @JsonIgnore
//    private UserInfo user;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "projectList_seq")
    })
    private ProjectList projectList;

    public UserInfo update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }

}
