package com.srlab.basic.serverside.hierarchies.services;

import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.hierarchies.repositories.HierarchyDataRepository;
import com.srlab.basic.serverside.utils.MapStructMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HierarchyDataService {

    private final Logger LOG = LoggerFactory.getLogger(HierarchyDataService.class);

    @Autowired
    private HierarchyDataRepository hDataRepository;


    public HierarchyData save(HierarchyData data) {
        try{
            return hDataRepository.save(data);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HierarchyData findOne(Long seq) {
        try{
            return hDataRepository.findOneBySeq(seq).orElseGet(null);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public HierarchyData update(HierarchyData ori, HierarchyData tar) {
        MapStructMapper.INSTANCE.update(tar, ori);
        return hDataRepository.save(ori);
    }

    public ResponseEntity<?> update(Long seq, HierarchyData data) {
        try{
            HierarchyData origin = findOne(seq);
            MapStructMapper.INSTANCE.update(data, origin);

            return new ResponseEntity<>(hDataRepository.save(origin), HttpStatus.OK);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}