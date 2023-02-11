package com.srlab.basic.serverside.files.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AvailableFile extends FileAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "hierarchyData_seq")
    })
    private HierarchyData hierarchyData;

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "board_seq")
    })
    private Board board;

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "comment_seq")
    })
    private Reply reply;

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "device_seq")
    })
    private Device device;
}
