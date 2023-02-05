package com.srlab.basic.serverside.devices.repositories;

import com.srlab.basic.serverside.devices.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findOneBySeq(Long seq);
}
