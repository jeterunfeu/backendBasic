package com.srlab.basic.serverside.files.repositories;

import com.srlab.basic.serverside.files.models.AvailableFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<AvailableFile, Long> {
    AvailableFile findByKey(String key);

    AvailableFile findOneByKey(String key);
}
