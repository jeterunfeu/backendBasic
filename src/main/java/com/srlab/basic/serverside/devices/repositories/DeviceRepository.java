package com.srlab.basic.serverside.devices.repositories;

import com.srlab.basic.serverside.devices.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

}
