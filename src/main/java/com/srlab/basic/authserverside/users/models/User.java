package com.srlab.basic.authserverside.users.models;

import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

@Schema(name = "user")
@Entity
@Table(name = "user_info")
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User extends CustomAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "password", nullable = false)
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
    private User certificationStaff;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "hierarchy_seq")
    })
    private HierarchyData hierarchyData;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "projectList_seq")
    })
    private ProjectList projectList;

    public User update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }

}
