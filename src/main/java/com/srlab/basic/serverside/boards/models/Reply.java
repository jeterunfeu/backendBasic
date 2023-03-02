package com.srlab.basic.serverside.boards.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.files.models.AvailableFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "reply")
@Entity
@Table(name = "reply")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SequenceGenerator(
        name = "REPLY_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName = "REPLY_SEQ", //시퀀스 이름
        initialValue = 1, //시작값
        allocationSize = 1 //메모리를 통해 할당할 범위 사이즈
)
public class Reply extends CustomAuditable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator = "REPLY_SEQ_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
    private Long seq;

    @Column(name = "comment_type", columnDefinition = "CHAR(1) default 1")
    private Boolean commentType;

    @Column(name = "reply",  columnDefinition = "CLOB")
    private String reply;

    @Column(name = "writer")
    private String writer;

    @Column(name = "user_only", columnDefinition = "CHAR(1) default 1")
    private Boolean userOnly;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private Reply root;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reply parent;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 1")
    private Long leftNode;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 2")
    private Long rightNode;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 0")
    private Long depth;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 1")
    private Long nodeOrder;

//    @OneToMany(mappedBy = "reply", cascade = CascadeType.MERGE, orphanRemoval = true)
//    private List<AvailableFile> files = new ArrayList<>();

    @ManyToOne
//    @JsonIgnoreProperties(value={"files", "replies"}, allowGetters=true)
//    @JsonIgnore
    @JsonBackReference
    @JoinColumns({
            @JoinColumn(name = "board_seq")
    })
    private Board board;

//    public Integer getLikeCount() {
//        return super.getLikeCount();
//    }
//
//    public void setLikeCount(Integer likeCount) {
//        super.setLikeCount(likeCount);
//    }
//
//    public Integer getDislikeCount() {
//        return super.getDislikeCount();
//    }
//
//    public void setDislikeCount(Integer dislikeCount) {
//        super.setDislikeCount(dislikeCount);
//    }

}
