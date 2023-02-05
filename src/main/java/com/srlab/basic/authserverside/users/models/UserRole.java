package com.srlab.basic.authserverside.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Schema(name = "user_role")
@Entity
@Table(name = "user_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name="url")
    private String url;

//    @ManyToOne
//    @JsonIgnore
//    @JoinColumns({
//            @JoinColumn(name = "user_seq")
//    })
//    private UserInfo userInfo;

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "hierarchy_seq")
    })
    private HierarchyData hierarchyData;

}
