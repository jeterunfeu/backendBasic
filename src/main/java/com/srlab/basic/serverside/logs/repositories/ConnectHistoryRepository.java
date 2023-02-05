package com.srlab.basic.serverside.logs.repositories;

import com.srlab.basic.serverside.logs.models.ConnectHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectHistoryRepository extends JpaRepository<ConnectHistory, Long> {

}
