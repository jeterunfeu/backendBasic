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

    public void set(M m, S s, R r) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        this.m = m;
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
            if (queryBuilder.checkEmpty(m.getClass(), tName)) {
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
                    leftNode += 2;
                    rightNode += 2;
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
            Long depth = (Long) method(origin, "getDepth").invoke(origin);
            Long diff = rightNode - leftNode;

            //need cascade!! root delete
            if (method(origin, "getRoot").invoke(origin) == null) {
                queryBuilder.deleteRoot(m.getClass(), tName, rootSeq);
                return new ResponseEntity<>("root delete completed", HttpStatus.OK);
            } else {
                //check last depth
                Long count = queryBuilder.checkDepthLastNode(m.getClass(), tName, root, depth);

                //delete
                queryBuilder.deleteByLeftNodeBetween(m.getClass(), tName, root, leftNode, rightNode - 1L);
                //leftNode update
                queryBuilder.deleteNodeClear(m.getClass(), tName, root, diff);

                if (count == 1L) {
                    queryBuilder.arrangeDepth(m.getClass(), tName, root, depth);
                }

                //end clearing
                queryBuilder.addEndClear(m.getClass(), tName, root);
            }

            return new ResponseEntity<>("process completed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void nodeChange(M originRoot, String tName, Long originLeftNode, Long originRightNode, Boolean flag) {
        try {
            M target;
            if (flag) {
                target = (M) queryBuilder.findByRightNode(m.getClass(), tName, originRoot, originLeftNode - 1L);
            } else {
                target = (M) queryBuilder.findByRightNode(m.getClass(), tName, originRoot, originRightNode + 2L);
            }

            Long targetLeftNode = (Long) method(target, "getLeftNode").invoke(target);
            Long targetRightNode = (Long) method(target, "getRightNode").invoke(target);

            queryBuilder.nodeChange(m.getClass(), tName, originRoot, originRightNode,
                    originLeftNode, targetRightNode, targetLeftNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upperNodeChange(M originRoot, M parent, String tName, Long depth, Long originLeftNode, Long originRightNode, Boolean flag) {
        Long lastDepth;
        try {
            if (flag) {
                lastDepth = depth - 1L;
            } else {
                lastDepth = depth + 1L;
            }
            M target = (M) queryBuilder.findLastNodeDepth(m.getClass(), tName, originRoot, lastDepth);
            Long targetLeftNode = (Long) method(target, "getLeftNode").invoke(target);
            Long targetRightNode = (Long) method(target, "getRightNode").invoke(target);
            Long targetDepth = (Long) method(target, "getDepth").invoke(target);
            M targetParent = (M) method(target, "getParent").invoke(target);

            queryBuilder.nodeChangeMore(m.getClass(), tName, originRoot, depth, parent, targetDepth,
                    targetParent, originRightNode, originLeftNode, targetRightNode, targetLeftNode, flag);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> moveUp(String tName, Long seq) {

        try {
            //bring origin
            M origin = (M) method(null, "findOneBySeq").invoke(s, seq);
            M parent = (M) method(origin, "getParent").invoke(origin);
            M originRoot = (M) method(origin, "getRoot").invoke(origin);

            //depth check
            Long depth = (Long) method(origin, "getDepth").invoke(origin);
            Long originLeftNode = (Long) method(origin, "getLeftNode").invoke(origin);
            Long originRightNode = (Long) method(origin, "getRightNode").invoke(origin);

            //first leftNode check
            Long minLeftNode = queryBuilder.findLeftMinByDepth(m.getClass(), tName, originRoot, depth);
            // last depth check
            Long maxDepth = queryBuilder.findMaxDepth(m.getClass(), tName, originRoot);

            Long nodeOrder;
            Long nodeOrderMin;

            if (depth > 0L) {

                //last node change
                if (depth == maxDepth) {
                    if (originLeftNode != minLeftNode) {
                        nodeChange(originRoot, tName, originLeftNode, originRightNode, true);
                    } else {
                        //find upper depth last node
                        upperNodeChange(originRoot, parent, tName, depth, originLeftNode, originRightNode, true);
                    }
                } else if (depth == 1L) {
                    if (originLeftNode != minLeftNode) {
                        nodeChange(originRoot, tName, originLeftNode, originRightNode, true);
                    }
                } else {
                    if (originLeftNode != minLeftNode) {
                        nodeChange(originRoot, tName, originLeftNode, originRightNode, true);
                    } else {
                        //find upper depth last node
                        upperNodeChange(originRoot, parent, tName, depth, originLeftNode, originRightNode, true);
                    }
                }
            } else {
                //nodeOrder changes
                nodeOrder = (Long) method(origin, "getNodeOrder").invoke(origin);
                nodeOrderMin = (Long) queryBuilder.findNodeOrderMin(m.getClass(), tName);
                if (nodeOrder != nodeOrderMin) {
                    queryBuilder.nodeOrderChange(m.getClass(), tName, originRoot, nodeOrder, true);
                }
            }
            return new ResponseEntity<>("move up success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> moveDown(String tName, Long seq) {
        try {
            //bring origin
            M origin = (M) method(null, "findOneBySeq").invoke(s, seq);
            M parent = (M) method(origin, "getParent").invoke(origin);
            M originRoot = (M) method(origin, "getRoot").invoke(origin);

            //depth check
            Long depth = (Long) method(origin, "getDepth").invoke(origin);
            Long originLeftNode = (Long) method(origin, "getLeftNode").invoke(origin);
            Long originRightNode = (Long) method(origin, "getRightNode").invoke(origin);

            Long nodeOrder;
            Long nodeOrderMax;

            //first leftNode check
            Long maxLeftNode = queryBuilder.findLeftMaxByDepth(m.getClass(), tName, originRoot, depth);
            // last depth check
            Long maxDepth = queryBuilder.findMaxDepth(m.getClass(), tName, originRoot);

            if (depth > 0L && depth < maxDepth) {
                    if (originLeftNode != maxLeftNode) {
                        nodeChange(originRoot, tName, originLeftNode, originRightNode, false);
                    } else {
                        //find upper depth last node
                        upperNodeChange(originRoot, parent, tName, depth, originLeftNode, originRightNode, false);
                    }
            } else if (depth == maxDepth) {
                if (originLeftNode != maxLeftNode) {
                    nodeChange(originRoot, tName, originLeftNode, originRightNode, false);
                }
            } else {
                //nodeOrder changes
                nodeOrder = (Long) method(origin, "getNodeOrder").invoke(origin);
                nodeOrderMax = queryBuilder.findNodeOrderMax(m.getClass(), tName);
                if (nodeOrder != nodeOrderMax) {
                    queryBuilder.nodeOrderChange(m.getClass(), tName, originRoot, nodeOrder, false);
                }
            }
            return new ResponseEntity<>("move up success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
