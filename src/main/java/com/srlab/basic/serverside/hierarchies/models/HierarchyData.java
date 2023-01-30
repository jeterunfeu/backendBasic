package com.srlab.basic.serverside.hierarchies.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srlab.basic.serverside.auditables.CustomAuditable;
import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.files.models.AvailableFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hierarchy_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HierarchyData extends CustomAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "LONG")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private HierarchyData root;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private HierarchyData parent;

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

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "hierarchyData")
    @JsonIgnore
    private List<Board> boards = new ArrayList<>();

}
