package com.srlab.basic.serverside.devices.services;

import com.srlab.basic.serverside.devices.models.Device;
import com.srlab.basic.serverside.devices.repositories.DeviceRepository;
import com.srlab.basic.serverside.queries.QueryBuilder;
import com.srlab.basic.serverside.utils.MapStructMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeviceService {

    private final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository dRepository;
    @Autowired
    private QueryBuilder<Device> queryBuilder;

    public ResponseEntity<?> findOne(Long seq) {
        try{
            return new ResponseEntity<>(dRepository.findById(seq).orElseGet(null), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> update(Long seq, Device data) {
        try{
            //origin
            Device origin = (Device) findOne(seq).getBody();
            MapStructMapper.INSTANCE.update(data, origin);

            //update
            return new ResponseEntity<>(dRepository.save(origin), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
