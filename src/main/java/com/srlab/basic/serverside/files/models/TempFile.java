package com.srlab.basic.serverside.files.models;

import com.srlab.basic.serverside.auditables.FileAuditable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Schema(name = "tempFile")
@Entity
@Table(name = "temporary_file")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TempFile extends FileAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;


}
