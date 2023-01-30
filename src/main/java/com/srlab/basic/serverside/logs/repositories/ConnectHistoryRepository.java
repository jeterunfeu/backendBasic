package com.srlab.basic.serverside.logs.repositories;

import com.srlab.basic.serverside.logs.models.ConnectHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectHistoryRepository extends JpaRepository<ConnectHistory, Long> {

}
