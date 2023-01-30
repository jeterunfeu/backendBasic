package com.srlab.basic.authserverside.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

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

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "user_seq")
    })
    private UserInfo userInfo;

}
