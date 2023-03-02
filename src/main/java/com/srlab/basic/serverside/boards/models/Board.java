package com.srlab.basic.serverside.boards.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "board")
@Entity
@Table(name = "board")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SequenceGenerator(
        name = "BOARD_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName = "BOARD_SEQ", //시퀀스 이름
        initialValue = 1, //시작값
        allocationSize = 1 //메모리를 통해 할당할 범위 사이즈
)
public class Board extends CustomAuditable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator = "BOARD_SEQ_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
    private Long seq;

    @Column(name = "title")
    private String title;

    @Column(name = "body_type", columnDefinition = "CHAR(1) default 1")
    private Boolean bodyType;

    @Column(name = "content", columnDefinition = "CLOB")
    private String body;

    @Column(name = "writer")
    private String writer;

    @Column(name = "user_only", columnDefinition = "CHAR(1) default 1")
    private Boolean userOnly;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private Board root;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Board parent;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 1")
    private Long leftNode;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 2")
    private Long rightNode;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 0")
    private Long depth;

    @Column(nullable = false, columnDefinition = "NUMBER(19,0) default 1")
    private Long nodeOrder;

    @OneToMany(mappedBy = "board"/*,
            cascade = CascadeType.ALL, orphanRemoval = true*/)
    @JsonManagedReference
    private List<AvailableFile> files = new ArrayList<>();

//    @OneToMany
//    @JoinColumn(name = "reply_id")
    @OneToMany(mappedBy = "board"/*,
            cascade = CascadeType.ALL, orphanRemoval = true*/)
    @JsonManagedReference
    private List<Reply> replies = new ArrayList<>();
//
//    @ManyToOne
//    @JoinColumn(name = "hierarchyData_seq")
//    private HierarchyData hierarchyData;
//
//    public Board(Long seq) {
//        this.seq = seq;
//    }
//
//    public void addFiles(AvailableFile file)
//    {
//        files.add(file);
//        file.setBoard(this);
//    }

//    public void putReply(Reply reply) {
//        this.replies.add(reply);
//        if(reply.getBoard() != this) {
//            reply.setBoard(this);
//        }
//    }
//    public void putFile(AvailableFile file) {
//        this.files.add(file);
//        if(file.getBoard() != this) {
//            file.setBoard(this);
//        }
//    }

//    public Integer getLikeCount() {
//        return super.getLikeMember().length();
//    }
//
//    public void setLikeCount(Integer likeCount) {
//        super.setLikeCount(likeCount);
//    }
//
//    public Integer getDislikeCount() {
//        return super.getDislikeMember().length();
//    }
//
//    public void setDislikeCount(Integer dislikeCount) {
//        super.setDislikeCount(dislikeCount);
//    }


}
