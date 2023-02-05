package com.srlab.basic.serverside.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Service
public class CommonDataService<M, S, R> {

    private static final Logger LOG = LoggerFactory.getLogger(CommonDataService.class);

    M m;
    S s;
    R r;

    @Autowired
    private QueryBuilder<M> queryBuilder;

    public CommonDataService() throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
    }

    public void set(Class<M> m, S s, R r) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        this.m = m.getConstructor().newInstance();
        this.s = s;
        this.r = r;
        queryBuilder.set(m);
    }

    public Method method(M model, String name) {
        try {
            Method result = null;
            M select = model == null ? m : model;
            switch (name) {
                case "update":
                    result = s.getClass().getDeclaredMethod("update", Long.class, m.getClass());
                    break;
                case "findOneBySeq":
                    result = s.getClass().getMethod("findOne", Long.class);
                    break;
                case "save":
                    result = s.getClass().getMethod("save", m.getClass());
                    break;
                case "getSeq":
                    result = select.getClass().getDeclaredMethod("getSeq");
                    break;
                case "getDepth":
                    result = select.getClass().getDeclaredMethod("getDepth");
                    break;
                case "getRoot":
                    result = select.getClass().getDeclaredMethod("getRoot");
                    break;
                case "getRightNode":
                    result = select.getClass().getDeclaredMethod("getRightNode");
                    break;
                case "getLeftNode":
                    result = select.getClass().getDeclaredMethod("getLeftNode");
                    break;
                case "getParent":
                    result = select.getClass().getDeclaredMethod("getParent");
                    break;
                case "getNodeOrder":
                    result = select.getClass().getDeclaredMethod("getNodeOrder");
                    break;
                case "setLeftNode":
                    result = select.getClass().getDeclaredMethod("setLeftNode", Long.class);
                    break;
                case "setRightNode":
                    result = select.getClass().getDeclaredMethod("setRightNode", Long.class);
                    break;
                case "setDepth":
                    result = select.getClass().getDeclaredMethod("setDepth", Long.class);
                    break;
                case "setRoot":
                    result = select.getClass().getDeclaredMethod("setRoot", m.getClass());
                    break;
                case "setNodeOrder":
                    result = select.getClass().getDeclaredMethod("setNodeOrder", Long.class);
                    break;
                case "setParent":
                    result = select.getClass().getDeclaredMethod("setParent", m.getClass());
                    break;
            }
            result.setAccessible(true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<?> findAll(String tName, Map<String, String> param, Pageable pageable) {
        return queryBuilder.findAllByConditions(m.getClass(), tName, param, pageable);
    }

    public ResponseEntity<?> findOne(Long seq) {
        try {
            Object result = method(null, "findOneBySeq").invoke(s, seq);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> insert(String tName, M data) {
        try {
            Long maxNodeOrder = queryBuilder.findMaxNodeOrder(m.getClass(), tName) + 1L;

            method(data, "setLeftNode").invoke(data, 1L);
            method(data, "setRightNode").invoke(data, 2L);
            method(data, "setDepth").invoke(data, 0L);
            method(data, "setNodeOrder").invoke(data, maxNodeOrder);

            Object result = method(null, "save").invoke(s, data);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addSet(String tName, Long seq, M data, Boolean flag) {

        try {
            //check empty
            if(queryBuilder.checkEmpty(m.getClass(), tName)) {
                return new ResponseEntity<>("DB is empty", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //check right
            M origin = (M) method(null, "findOneBySeq").invoke(s, seq);
            //root
            M root = (M) method(origin, "getRoot").invoke(origin);
            M parent = null;
            //elements
            Long depth = (Long) method(origin, "getDepth").invoke(origin);
            Long leftNode = (Long) method(origin, "getLeftNode").invoke(origin);
            Long rightNode = leftNode + 1;
            Long nodeOrder = (Long) method(origin, "getNodeOrder").invoke(origin);

            root = root == null ? origin : root;

            //plus 2 bigger than right value
            //new level?
            if ((Long) method(origin, "getDepth").invoke(origin) == 0L) {
                depth++;
                leftNode++;
                rightNode++;
                //root decided
                //nodeOrder decided
                parent = root;
            } else {
                if (flag) {
                    depth++;
                    leftNode++;
                    rightNode++;
                    //root decided
                    //nodeOrder decided
                    parent = origin;

                    //middle clearing
                    queryBuilder.addMiddleClear(m.getClass(), tName, origin);
                } else {
                    leftNode+=2;
                    rightNode+=2;
                    //root decided
                    //nodeOrder decided
                    parent = root;
                }
            }
            //exist left, right node process
            method(data, "setLeftNode").invoke(data, leftNode);
            method(data, "setRightNode").invoke(data, rightNode);
            method(data, "setDepth").invoke(data, depth);
            method(data, "setRoot").invoke(data, root);
            method(data, "setParent").invoke(data, parent);
            method(data, "setNodeOrder").invoke(data, nodeOrder);

            //clearing
            queryBuilder.addClear(m.getClass(), tName, root
                    , (Long) method(origin, "getLeftNode").invoke(origin));

            //save
            method(null, "save").invoke(s, data);

            //end clearing
            queryBuilder.addEndClear(m.getClass(), tName, root);

            return new ResponseEntity<>("process completed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> subtractSet(String tName, Long seq) {
        try {
            //check right
            M origin = (M) method(null, "findOneBySeq").invoke(s, seq);
            M root = (M) method(origin, "getRoot").invoke(origin);
            Long leftNode = (Long) method(origin, "getLeftNode").invoke(origin);
            Long rightNode = (Long) method(origin, "getRightNode").invoke(origin);
            Long rootSeq = (Long) method(origin, "getSeq").invoke(origin);
            Long diff = rightNode - leftNode;

            //need cascade!! root delete
            if(method(origin, "getRoot").invoke(origin) == null) {
                queryBuilder.deleteRoot(m.getClass(), tName, rootSeq);
                return new ResponseEntity<>("root delete completed", HttpStatus.OK);
            } else {
                //delete
                queryBuilder.deleteByLeftNodeBetween(m.getClass(), tName, root, leftNode, rightNode - 1L);
                //leftNode update
                queryBuilder.deleteNodeClear(m.getClass(), tName, root, diff);

                //end clearing
                queryBuilder.addEndClear(m.getClass(), tName, root);
            }

            return new ResponseEntity<>("process completed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void swap(M origin, M originCopy, M target, M targetCopy) {
        try{
//            Long originSeq = (Long) method(origin, "getSeq").invoke(origin);
//            Long targetSeq = (Long) method(origin, "getSeq").invoke(origin);
//
//            M getOrigin = (M) method(origin, "findOneBySeq").invoke(origin, originSeq);
//            M getTarget = (M) method(target, "findOneBySeq").invoke(target, targetSeq);

            method(origin, "update").invoke(s, origin, originCopy);
            method(target, "update").invoke(s, target, targetCopy);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> moveUp(String tName, Long seq) {

        try {
            //depth 0 disable
            M origin = (M) method(null, "findOneBySeq").invoke(s, seq);
            M root = (M) method(origin, "getRoot").invoke(origin);
            M originCopy = origin;

            Long leftNode = (Long) method(origin, "getLeftNode").invoke(origin);
            Long rightNode = (Long) method(origin, "getRightNode").invoke(origin);

            Long diff = rightNode - leftNode;
            Long depth = (Long) method(origin, "getDepth").invoke(origin);

            M target = (M) queryBuilder.findByRightNode(m.getClass(), tName, root, leftNode - 1L);
            M targetCopy = target;

            //find next node by leftNode
            Long nextLeftNode = (Long) method(target, "getLeftNode").invoke(target);
            Long nextRightNode = (Long) method(target, "getRightNode").invoke(target);
            Long nextDiff = nextRightNode - nextLeftNode;

            Long groupDiff = nextLeftNode - leftNode;

            Long changeLeft = rightNode - diff;
            Long changeRight = nextRightNode;
            Long changeNextLeft = leftNode;
            Long changeNextRight = nextLeftNode + nextDiff;

            if (depth > 0L) {
                //check depth`s min left node
                depth -= 1;

                Long leftMin = queryBuilder.findLeftMinByDepth(m.getClass(), tName, root, depth);
                M leftMaxParent = (M) queryBuilder.findMaxParentByDepth(m.getClass(), tName, root, depth);

                if(leftMin == leftNode) {
                    method(origin, "setParent").invoke(origin, leftMaxParent);
                    method(origin, "setDepth").invoke(depth);
                }

                //both don`t have child
                if (diff == 1 && nextDiff == 1) {
                    //switch both
                    method(originCopy, "setLeftNode").invoke(originCopy, nextLeftNode);
                    method(originCopy, "setRightNode").invoke(originCopy, nextRightNode);
                    method(targetCopy, "setLeftNode").invoke(targetCopy, leftNode);
                    method(targetCopy, "setRightNode").invoke(targetCopy, rightNode);

                    swap(origin, originCopy, target, targetCopy);
                } else {
                    //switch both and sub
                    method(originCopy, "setLeftNode").invoke(originCopy, changeLeft);
                    method(originCopy, "setRightNode").invoke(originCopy, changeRight);
                    method(targetCopy, "setLeftNode").invoke(targetCopy, changeNextLeft);
                    method(targetCopy, "setRightNode").invoke(targetCopy, changeNextRight);

                    swap(origin, originCopy, target, targetCopy);

                    //sub
                    queryBuilder.originSetting(m.getClass(), tName, root, leftNode, rightNode, groupDiff);
                    queryBuilder.targetSetting(m.getClass(), tName, root, nextLeftNode, nextRightNode, groupDiff);

                }
            } else {
                //depth is 0
                Long order = (Long) method(origin, "getNodeOrder").invoke(origin);
                if (order > 1) {
                    order = order - 1L;
                    method(originCopy, "setNodeOrder").invoke(originCopy, order);
                    queryBuilder.addOrder(m.getClass(), tName, order);
                    method(origin, "update").invoke(s, origin, originCopy);
                }
            }
            return new ResponseEntity<>("up completed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> moveDown(String tName, Long seq) {
        try {
            //opposite moveup
            //depth 0 and last disable
            M origin = (M) method(null, "findOneBySeq").invoke(s, seq);
            M originCopy = origin;
            M root = (M) method(origin, "getRoot").invoke(origin);

            Long leftNode = (Long) method(origin, "getLeftNode").invoke(origin);
            Long rightNode = (Long) method(origin, "getRightNode").invoke(origin);
            Long diff = rightNode - leftNode;
            Long depth = (Long) method(origin, "getDepth").invoke(origin);

            Long rightMax = queryBuilder.findRightMaxFromDepth(m.getClass(), tName, root, depth);

            M rightMinParent = (M) queryBuilder.findMinParentByDepth(m.getClass(), tName, root, depth);

            //find next node by rightNode
            M target = (M) queryBuilder.findByLeftNode(m.getClass(), tName, root, rightNode + 1L);
            M targetCopy = target;

            Long nextLeftNode = (Long) method(target, "getLeftNode").invoke(target);
            Long nextRightNode = (Long) method(target, "getRightNode").invoke(target);
            Long nextDiff = nextRightNode - nextLeftNode;

            //switch both and sub
            Long groupDiff = leftNode - nextLeftNode;

            Long changeLeft = nextRightNode - nextDiff;
            Long changeRight = rightNode;
            Long changeNextLeft = nextLeftNode;
            Long changeNextRight = leftNode + diff;

            Long order = (Long) method(origin, "getNodeOrder").invoke(origin);
            Long limit = queryBuilder.countByDepth(m.getClass(), tName, 0L);

            if (depth > 0L) {
                //check depth`s max right node
                if (rightMax == rightNode) {
                    method(origin, "setParent").invoke(origin, rightMinParent);
                    method(null, "setDepth").invoke(depth + 1L);
                }
                //both don`t have child
                if (diff == 1 && nextDiff == 1) {
                    //switch both
                    method(originCopy, "setLeftNode").invoke(originCopy, nextLeftNode);
                    method(originCopy, "setRightNode").invoke(originCopy, nextRightNode);
                    method(targetCopy, "setLeftNode").invoke(targetCopy, leftNode);
                    method(targetCopy, "setRightNode").invoke(targetCopy, rightNode);

                    swap(origin, originCopy, target, targetCopy);
                } else {
                    method(originCopy, "setLeftNode").invoke(originCopy, changeLeft);
                    method(originCopy, "setRightNode").invoke(originCopy, changeRight);
                    method(targetCopy, "setLeftNode").invoke(targetCopy, changeNextLeft);
                    method(targetCopy, "setRightNode").invoke(targetCopy, changeNextRight);

                    swap(origin, originCopy, target, targetCopy);

                    //sub
                    queryBuilder.originSetting(m.getClass(), tName, root, nextLeftNode, nextRightNode, groupDiff);
                    queryBuilder.targetSetting(m.getClass(), tName, root, leftNode, rightNode, groupDiff);
                }
            } else {
                if (order < limit) {
                    order += 1L;
                    method(originCopy, "setNodeOrder").invoke(originCopy);
                    queryBuilder.subOrder(m.getClass(), tName, order);
                    method(origin, "update").invoke(origin, originCopy);
                }
            }
            return new ResponseEntity<>("down completed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
