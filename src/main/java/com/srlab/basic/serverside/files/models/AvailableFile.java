package com.srlab.basic.serverside.files.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.srlab.basic.serverside.auditables.FileAuditable;
import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.devices.models.Device;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Schema(name = "availableFile")
@Entity
@Table(name = "available_file")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@SequenceGenerator(
        name="AVAILABLE_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName="AVAILABLE_SEQ", //시퀀스 이름
        initialValue=1, //시작값
        allocationSize=1 //메모리를 통해 할당할 범위 사이즈
)
public class AvailableFile extends FileAuditable {

    @Id
    @GeneratedValue(
            strategy=GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator="AVAILABLE_SEQ_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
    private Long seq;

//    @ManyToOne
//    @JsonIgnore
//    @JoinColumns({
//            @JoinColumn(name = "hierarchyData_seq")
//    })
//    private HierarchyData hierarchyData;

    @ManyToOne
//    @JsonIgnoreProperties(value={"files", "replies"}, allowGetters=true)
//    @JsonIgnore
    @JsonBackReference
    @JoinColumns({
            @JoinColumn(name = "board_seq")
    })
    private Board board;

//    @ManyToOne
//    @JsonIgnore
//    @JoinColumns({
//            @JoinColumn(name = "comment_seq")
//    })
//    private Reply reply;
//
//    @ManyToOne
//    @JsonIgnore
//    @JoinColumns({
//            @JoinColumn(name = "device_seq")
//    })
//    private Device device;

}
