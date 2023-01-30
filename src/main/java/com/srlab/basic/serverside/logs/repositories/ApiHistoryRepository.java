package com.srlab.basic.serverside.logs.repositories;

import com.srlab.basic.serverside.logs.models.ApiHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiHistoryRepository extends JpaRepository<ApiHistories, Long> {

}
