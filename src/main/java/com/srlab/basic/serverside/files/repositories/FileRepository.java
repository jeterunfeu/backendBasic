package com.srlab.basic.serverside.files.repositories;

import com.srlab.basic.serverside.custom.CustomRepository;
import com.srlab.basic.serverside.files.models.AvailableFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface FileRepository extends CustomRepository<AvailableFile, Long>/*JpaRepository<AvailableFile, Long>*/ {
    AvailableFile findByKey(String key);

    AvailableFile findOneByKey(String key);

    AvailableFile findOneBySeq(Long seq);

    @Transactional
    @Modifying
    @Query(" delete from AvailableFile a where a.key=:key ")
    void deleteOneByKey(@Param("key") String key);
}
