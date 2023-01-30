package com.srlab.basic.serverside.files.repositories;

import com.srlab.basic.serverside.files.models.TempFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileTempRepository extends JpaRepository<TempFile, Long> {
    TempFile findByKey(String key);
}
