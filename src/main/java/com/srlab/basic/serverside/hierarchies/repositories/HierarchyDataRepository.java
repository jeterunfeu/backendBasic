package com.srlab.basic.serverside.hierarchies.repositories;

import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface HierarchyDataRepository extends JpaRepository<HierarchyData, Long> {
   Optional<HierarchyData> findOneBySeq(Long seq);
}
