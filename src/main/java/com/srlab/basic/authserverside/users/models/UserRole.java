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
@SequenceGenerator(
        name="USERROLE_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName="USERROLE_SEQ", //시퀀스 이름
        initialValue=1, //시작값
        allocationSize=1 //메모리를 통해 할당할 범위 사이즈
)
public class UserRole {

    @Id
    @GeneratedValue(
            strategy=GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator="USERROLE_SEQ_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
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
