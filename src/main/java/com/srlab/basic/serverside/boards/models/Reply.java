package com.srlab.basic.serverside.boards.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.files.models.AvailableFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "comment")
@Entity
@Table(name = "reply")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Reply extends CustomAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "comment_type", columnDefinition = "CHAR(1) default 1")
    private Boolean commentType;

    @Column(name = "reply", columnDefinition = "LONG")
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

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "board")
    private List<AvailableFile> files = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "board_seq")
    })
    private Board board;

}
