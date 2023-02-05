package com.srlab.basic.serverside.devices.services;

import com.srlab.basic.serverside.devices.models.Device;
import com.srlab.basic.serverside.devices.repositories.DeviceRepository;
import com.srlab.basic.serverside.utils.MapStructMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository dRepository;


    public Device save(Device data) {
        try{
            return dRepository.save(data);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Device findOne(Long seq) {
        try{
            return dRepository.findOneBySeq(seq).orElseGet(null);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Device update(Device ori, Device tar) {
        MapStructMapper.INSTANCE.update(tar, ori);
        return dRepository.save(ori);
    }

    public ResponseEntity<?> update(Long seq, Device data) {
        try{
            Device origin = findOne(seq);
            MapStructMapper.INSTANCE.update(data, origin);

            return new ResponseEntity<>(dRepository.save(origin), HttpStatus.OK);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
