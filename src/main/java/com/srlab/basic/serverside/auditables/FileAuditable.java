package com.srlab.basic.serverside.auditables;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FileAuditable extends CustomAuditable {

    @Column(name = "category")
    private String category;

    @Column(name = "key")
    private String key;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_extension")
    private String fileExt;

    @Column(name = "file_mime_type")
    private String fileMimeType;

    @Column(name = "file_size")
    private Integer fileSize;

}
