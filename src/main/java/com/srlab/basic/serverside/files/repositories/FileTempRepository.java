package com.srlab.basic.serverside.files.repositories;

import com.srlab.basic.serverside.files.models.TempFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileTempRepository extends JpaRepository<TempFile, Long> {
    TempFile findByKey(String key);

    Optional<TempFile> findOneByKey(String seq);
}
