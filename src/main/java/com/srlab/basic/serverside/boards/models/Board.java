package com.srlab.basic.serverside.boards.models;

import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
public class Board extends CustomAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "title")
    private String title;

    @Column(name = "body_type", columnDefinition = "CHAR(1) default 1")
    private Boolean bodyType;

    @Column(name = "body", columnDefinition = "LONG")
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

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "board")
    private List<AvailableFile> files = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "board")
    private List<Reply> replies = new ArrayList<>();

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "hierarchyData_seq")
    })
    private HierarchyData hierarchyData;

}
