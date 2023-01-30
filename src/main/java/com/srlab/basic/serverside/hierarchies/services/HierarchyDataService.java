package com.srlab.basic.serverside.hierarchies.services;

import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.hierarchies.repositories.HierarchyDataRepository;
import com.srlab.basic.serverside.queries.QueryBuilder;
import com.srlab.basic.serverside.utils.MapStructMapper;
import com.srlab.basic.serverside.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HierarchyDataService {

    private final Logger LOG = LoggerFactory.getLogger(HierarchyDataService.class);

    @Autowired
    private HierarchyDataRepository hDataRepository;
    @Autowired
    private QueryBuilder<HierarchyData> queryBuilder;

    public HierarchyData update(HierarchyData ori, HierarchyData tar) {
        MapStructMapper.INSTANCE.update(tar, ori);
        return hDataRepository.save(ori);
    }

    public ResponseEntity<?> findOne(Long seq) {
        try{
            return new ResponseEntity<>(hDataRepository.findOneBySeq(seq).orElseGet(null), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> update(Long seq, HierarchyData data) {
        try{
            //origin
            HierarchyData origin = (HierarchyData) findOne(seq).getBody();
            MapStructMapper.INSTANCE.update(data, origin);

            //update
            return new ResponseEntity<>(hDataRepository.save(origin), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

//    public ResponseEntity<?> findAll(Map<String, String> param, Pageable pageable) {
//        return queryBuilder.findAllByConditions(HierarchyData.class, "hierarchyData", param, pageable);
//    }
//
//    public ResponseEntity<?> checkLeftMinFromDepth(HierarchyData root, Long depth) {
//        try{
//            return new ResponseEntity<>(queryBuilder.findLeftMinByDepth(HierarchyData.class, "hierarchyData", root, depth), HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<?> checkRightMaxFromDepth(HierarchyData root, Long depth) {
//        try{
//            return new ResponseEntity<>(queryBuilder.findRightMaxFromDepth(HierarchyData.class, "hierarchyData", root, depth), HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private ResponseEntity<?> findByRightNode(HierarchyData root, Long leftNode) {
//        try{
//            return new ResponseEntity<>(queryBuilder.findByRightNode(HierarchyData.class, "hierarchyData", root, leftNode), HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private ResponseEntity<?> findByLeftNode(HierarchyData root, Long rightNode) {
//        try{
//            return new ResponseEntity<>(queryBuilder.findByLeftNode(HierarchyData.class, "hierarchyData", root, rightNode), HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<?> addSet(Long seq, HierarchyData data, Boolean flag) {
//
//        try{
//            //check right
//            HierarchyData origin = (HierarchyData) findOne(seq).getBody();
//            //root
//            HierarchyData root;
//            //depth
//            Long depth = origin.getDepth();
//            //plus 2 bigger than right value
//            queryBuilder.addRight(HierarchyData.class, "hierarchyData", origin.getRoot(), origin.getRightNode());
//            //new level?
//            if(origin.getDepth() == 0L) {
//                Long limit = (Long) queryBuilder.countByDepth(HierarchyData.class, "hierarchyData", 0L).getBody();
//                data.setLeftNode(1L);
//                data.setRightNode(2L);
//                data.setDepth(limit + 1L);
//                data.setNodeOrder(0L);
//                data.setParent(origin);
//                hDataRepository.save(data);
//            } else {
//                if (flag) {
//                    queryBuilder.addDepth(HierarchyData.class, "hierarchyData", origin.getRoot(), origin.getDepth());
//                    depth++;
//                    root = origin;
//                } else {
//                    root = origin.getRoot();
//                }
//
//                //exist left, right node process
//                queryBuilder.addLeftNode(HierarchyData.class, "hierarchyData", origin.getRoot(), origin.getLeftNode());
//                queryBuilder.addRightNode(HierarchyData.class, "hierarchyData", origin.getRoot(), origin.getLeftNode());
//                //save after process
//                data.setLeftNode(origin.getLeftNode() + 1);
//                data.setRightNode(origin.getRightNode() + 1);
//                data.setDepth(depth);
//                data.setRoot(root);
//                hDataRepository.save(data);
//
//            }
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }
//
//    public ResponseEntity<?> subtractSet(Long seq) {
//        try{
//            //check right
//            HierarchyData origin = (HierarchyData) findOne(seq).getBody();
//            Long leftNode = origin.getLeftNode();
//            Long rightNode = origin.getRightNode();
//            Long diff = rightNode - leftNode;
//
//            if (origin.getDepth() != 0L) {
//                //delete
//                queryBuilder.deleteByLeftNodeBetween(HierarchyData.class, "hierarchyData", origin.getRoot(), leftNode, rightNode - 1L);
//
//                //leftNode update
//                queryBuilder.updateLeftNode(HierarchyData.class, "hierarchyData", origin.getRoot(), diff + 1L);
//                //rightNode update
//                queryBuilder.updateRightNode(HierarchyData.class, "hierarchyData", origin.getRoot(), diff + 1L);
//            } else {
//                //delete
//                queryBuilder.deleteByLeftNodeBetween(HierarchyData.class, "hierarchyData", origin.getRoot(), leftNode, rightNode);
//            }
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }
//
//
//    public ResponseEntity<?> moveUp(Long seq) {
//
//        try{
//            //depth 0 disable
//            HierarchyData origin = (HierarchyData) findOne(seq).getBody();
//            HierarchyData originCopy = origin;
//            Long leftNode = origin.getLeftNode();
//            Long rightNode = origin.getRightNode();
//            Long diff = rightNode - leftNode;
//            Long depth = origin.getDepth();
//            if (depth > 0L) {
//                //check depth`s min left node
//                if (checkLeftMinFromDepth(origin.getRoot(), depth).getBody() == leftNode) {
//                    origin.setParent((HierarchyData) queryBuilder.findMaxParentByDepth(HierarchyData.class, "hierarchyData", origin.getRoot(), depth-1L).getBody());
//                    origin.setDepth(depth - 1L);
//                }
//                //find next node by leftNode
//                HierarchyData target = (HierarchyData) findByRightNode(origin.getRoot(), leftNode - 1L).getBody();
//                HierarchyData targetCopy = target;
//                Long nextLeftNode = target.getLeftNode();
//                Long nextRightNode = target.getRightNode();
//                Long nextDiff = nextRightNode - nextLeftNode;
//                //both don`t have child
//                if (diff == 1 && nextDiff == 1) {
//                    //switch both
//                    originCopy.setLeftNode(nextLeftNode);
//                    originCopy.setRightNode(nextRightNode);
//                    targetCopy.setLeftNode(leftNode);
//                    targetCopy.setRightNode(rightNode);
//                    MapStructMapper.INSTANCE.update(originCopy, origin);
//                    MapStructMapper.INSTANCE.update(targetCopy, target);
//                    hDataRepository.save(target);
//                    hDataRepository.save(origin);
//                } else {
//                    //switch both and sub
//                    Long groupDiff = nextLeftNode - leftNode;
//
//                    Long changeLeft = rightNode - diff;
//                    Long changeRight = nextRightNode;
//                    Long changeNextLeft = leftNode;
//                    Long changeNextRight = nextLeftNode + nextDiff;
//
//                    originCopy.setLeftNode(changeLeft);
//                    originCopy.setRightNode(changeRight);
//                    targetCopy.setLeftNode(changeNextLeft);
//                    targetCopy.setRightNode(changeNextRight);
//                    MapStructMapper.INSTANCE.update(originCopy, origin);
//                    MapStructMapper.INSTANCE.update(targetCopy, target);
//                    hDataRepository.save(origin);
//                    hDataRepository.save(target);
//
//                    //sub
//                    queryBuilder.originSetting(HierarchyData.class, "hierarchyData", origin.getRoot(), leftNode, rightNode, groupDiff);
//                    queryBuilder.targetSetting(HierarchyData.class, "hierarchyData", origin.getRoot(), nextLeftNode, nextRightNode, groupDiff);
//
//                }
//            } else {
//                //depth is 0
//                Long order = origin.getNodeOrder();
//                if(order >= 1) {
//                    order = order - 1L;
//                    originCopy.setNodeOrder(order);
//                    MapStructMapper.INSTANCE.update(originCopy, origin);
//                    queryBuilder.addOrder(HierarchyData.class, "hierarchyData", order);
//                    hDataRepository.save(origin);
//                }
//            }
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<?> moveDown(Long seq) {
//        try{
//            //opposite moveup
//            //depth 0 and last disable
//            HierarchyData origin = (HierarchyData) findOne(seq).getBody();
//            HierarchyData originCopy = origin;
//            Long leftNode = origin.getLeftNode();
//            Long rightNode = origin.getRightNode();
//            Long diff = rightNode - leftNode;
//            Long depth = origin.getDepth();
//            if (depth > 0L) {
//                //check depth`s max right node
//                if (checkRightMaxFromDepth(origin.getRoot(), depth).getBody() == rightNode) {
//                    origin.setParent((HierarchyData) queryBuilder.findMinParentByDepth(HierarchyData.class, "hierarchyData", origin.getRoot(), depth).getBody());
//                    origin.setDepth(depth + 1L);
//                }
//                //find next node by rightNode
//                HierarchyData target = (HierarchyData) findByLeftNode(origin.getRoot(), rightNode + 1L).getBody();
//                HierarchyData targetCopy = target;
//                Long nextLeftNode = target.getLeftNode();
//                Long nextRightNode = target.getRightNode();
//                Long nextDiff = nextRightNode - nextLeftNode;
//                //both don`t have child
//                if (diff == 1 && nextDiff == 1) {
//                    //switch both
//                    originCopy.setLeftNode(nextLeftNode);
//                    originCopy.setRightNode(nextRightNode);
//                    targetCopy.setLeftNode(leftNode);
//                    targetCopy.setRightNode(rightNode);
//                    MapStructMapper.INSTANCE.update(originCopy, origin);
//                    MapStructMapper.INSTANCE.update(targetCopy, target);
//                    hDataRepository.save(target);
//                    hDataRepository.save(origin);
//                } else {
//                    //switch both and sub
//                    Long groupDiff = leftNode - nextLeftNode;
//
//                    Long changeLeft = nextRightNode - nextDiff;
//                    Long changeRight = rightNode;
//                    Long changeNextLeft = nextLeftNode;
//                    Long changeNextRight = leftNode + diff;
//
//                    originCopy.setLeftNode(changeLeft);
//                    originCopy.setRightNode(changeRight);
//                    targetCopy.setLeftNode(changeNextLeft);
//                    targetCopy.setRightNode(changeNextRight);
//                    MapStructMapper.INSTANCE.update(originCopy, origin);
//                    MapStructMapper.INSTANCE.update(targetCopy, target);
//                    hDataRepository.save(origin);
//                    hDataRepository.save(target);
//
//                    //sub
//                    queryBuilder.originSetting(HierarchyData.class, "hierarchyData", origin.getRoot(), nextLeftNode, nextRightNode, groupDiff);
//                    queryBuilder.targetSetting(HierarchyData.class, "hierarchyData", origin.getRoot(), leftNode, rightNode, groupDiff);
//
//                }
//            } else {
//                Long order = origin.getNodeOrder();
//                Long limit = (Long) queryBuilder.countByDepth(HierarchyData.class, "hierarchyData", 0L).getBody();
//                if(order < limit) {
//                    order = order + 1L;
//                    originCopy.setNodeOrder(order);
//                    MapStructMapper.INSTANCE.update(originCopy, origin);
//                    queryBuilder.subOrder(HierarchyData.class, "hierarchData", order);
//                    hDataRepository.save(origin);
//                }
//            }
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }catch(Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}