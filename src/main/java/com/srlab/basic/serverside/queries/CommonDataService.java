package com.srlab.basic.serverside.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
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

    public CommonDataService() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    }

    public void set(Class<M> m, Class<S> s, R r) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.m = m.getConstructor().newInstance();
        this.s = s.getConstructor().newInstance();
        this.r = r;
        queryBuilder.set(m);
    }

    public Method method(String name) {
        try {
            Method result = null;

            switch (name) {
                case "findOneBySeq":
                    result = r.getClass().getMethod("findOneBySeq", Long.class);
                    break;
                case "save":
                    result = r.getClass().getMethod("save", m.getClass());
                    break;
                case "getDepth":
                    result = m.getClass().getMethod("getDepth");
                    break;
                case "getRoot":
                    result = m.getClass().getMethod("getRoot");
                    break;
                case "getRightNode":
                    result = m.getClass().getMethod("getRightNode");
                    break;
                case "getLeftNode":
                    result = m.getClass().getMethod("getLeftNode");
                    break;
                case "setLeftNode":
                    result = m.getClass().getMethod("setLeftNode", Long.class);
                    break;
                case "setRightNode":
                    result = m.getClass().getMethod("setRightNode", Long.class);
                    break;
                case "setDepth":
                    result = m.getClass().getMethod("setDepth", Long.class);
                    break;
                case "setRoot":
                    result = m.getClass().getMethod("setRoot", m.getClass());
                    break;
                case "setNodeOrder":
                    result = m.getClass().getMethod("setNodeOrder", Long.class);
                    break;
                case "setParent":
                    result = m.getClass().getMethod("setParent", m.getClass());
                    break;
                case "update":
                    result = s.getClass().getMethod("update", m.getClass(), m.getClass());
                    break;
                default :
                    LOG.info("default");
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseEntity<?> findAll(String tName, Map<String, String> param, Pageable pageable) {
        return queryBuilder.findAllByConditions(m.getClass(), tName, param, pageable);
    }

    public ResponseEntity<?> findOne(Long seq) {
        try {
            Object result = method("findOneBySeq").invoke(r, seq);
            return new ResponseEntity<>(result/*findById.invoke(inst, seq)hDataRepository.findById(seq).orElseGet(null)*/, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> checkLeftMinFromDepth(String tName, M root, Long depth) {
        try {
            return new ResponseEntity<>(queryBuilder.findLeftMinByDepth(m.getClass(), tName, root, depth), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> checkRightMaxFromDepth(String tName, M root, Long depth) {
        try {
            return new ResponseEntity<>(queryBuilder.findRightMaxFromDepth(m.getClass(), tName, root, depth), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> findByRightNode(String tName, M root, Long leftNode) {
        try {
            return new ResponseEntity<>(queryBuilder.findByRightNode(m.getClass(), tName, root, leftNode), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> findByLeftNode(String tName, M root, Long rightNode) {
        try {
            return new ResponseEntity<>(queryBuilder.findByLeftNode(m.getClass(), tName, root, rightNode), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addSet(String tName, Long seq, M data, Boolean flag) {

        try {

            //check right
            M origin = (M) findOne(seq).getBody();
            //root
            M root;
            //depth
            Long depth = (Long) method("getDepth").invoke(origin);//getDepth.invoke(origin);
            //plus 2 bigger than right value
//            queryBuilder.addRight(m.getClass(), tName, (M) getRoot.invoke(origin), (Long) getRightNode.invoke(origin));
            queryBuilder.addRight(m.getClass(), tName, (M) method("getRoot").invoke(origin), (Long) method("getRightNode").invoke(origin));
            //new level?
//            if((Long) getDepth.invoke(origin) == 0L) {
            if ((Long) method("getDepth").invoke(origin) == 0L) {
                Long limit = (Long) queryBuilder.countByDepth(m.getClass(), tName, 0L).getBody();
//                setLeftNode.invoke(data, 1L);
//                setRightNode.invoke(data, 2L);
//                setDepth.invoke(data, limit+1L);
//                setNodeOrder.invoke(data, 0L);
//                setParent.invoke(data, origin);
//                save.invoke(repositoryInstance, data);
                method("setLeftNode").invoke(data, 1L);
                method("setRightNode").invoke(data, 2L);
                method("setDepth").invoke(data, limit + 1L);
                method("setNodeOrder").invoke(data, 0L);
                method("setParent").invoke(data, origin);
                method("save").invoke(r, data);
//                hDataRepository.save(data);
            } else {
                if (flag) {
//                    queryBuilder.addDepth(m.getClass(), tName, (M) getRoot.invoke(origin), (Long) getDepth.invoke(origin));
                    queryBuilder.addDepth(m.getClass(), tName, (M) method("getRoot").invoke(origin), (Long) method("getDepth").invoke(origin));
                    depth++;
                    root = origin;
                } else {
//                    root = (M) getRoot.invoke(origin);
                    root = (M) method("getRoot").invoke(origin);
                }

                //exist left, right node process
//                queryBuilder.addLeftNode(m.getClass(), tName,(M) getRoot.invoke(origin), (Long) getLeftNode.invoke(origin));
//                queryBuilder.addRightNode(m.getClass(), tName, (M) getRoot.invoke(origin), (Long) getLeftNode.invoke(origin));
                queryBuilder.addLeftNode(m.getClass(), tName, (M) method("getRoot").invoke(origin), (Long) method("getLeftNode").invoke(origin));
                queryBuilder.addRightNode(m.getClass(), tName, (M) method("getRoot").invoke(origin), (Long) method("getLeftNode").invoke(origin));
                //save after process
//                setLeftNode.invoke(data, (Long) getLeftNode.invoke(origin) + 1L);
//                setRightNode.invoke(data, (Long) getRightNode.invoke(origin) + 1L);
//                setDepth.invoke(data, depth);
//                setRoot.invoke(data, root);
//                save.invoke(repositoryInstance, data);
                method("setLeftNode").invoke(data, (Long) method("getLeftNode").invoke(origin) + 1L);
                method("setRightNode").invoke(data, (Long) method("getRightNode").invoke(origin) + 1L);
                method("setDepth").invoke(data, depth);
                method("setRoot").invoke(data, root);
                method("save").invoke(r, data);
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> subtractSet(String tName, Long seq) {
        try {
            //check right
            M origin = (M) findOne(seq).getBody();
//            Long leftNode = (Long) getLeftNode.invoke(origin);
//            Long rightNode = (Long) getRightNode.invoke(origin);
            Long leftNode = (Long) method("getLeftNode").invoke(origin);
            Long rightNode = (Long) method("getRightNode").invoke(origin);
            Long diff = rightNode - leftNode;

//            if((Long) getDepth.invoke(origin) != 0L) {
            if ((Long) method("getDepth").invoke(origin) != 0L) {
//                //delete
//                queryBuilder.deleteByLeftNodeBetween(m.getClass(), tName, (M) getRoot.invoke(origin), leftNode, rightNode - 1L);
//                //leftNode update
//                queryBuilder.updateLeftNode(m.getClass(), tName, (M) getRoot.invoke(origin), diff + 1L);
//                //rightNode update
//                queryBuilder.updateRightNode(m.getClass(), tName, (M) getRoot.invoke(origin), diff + 1L);
                //delete
                queryBuilder.deleteByLeftNodeBetween(m.getClass(), tName, (M) method("getRoot").invoke(origin), leftNode, rightNode - 1L);
                //leftNode update
                queryBuilder.updateLeftNode(m.getClass(), tName, (M) method("getRoot").invoke(origin), diff + 1L);
                //rightNode update
                queryBuilder.updateRightNode(m.getClass(), tName, (M) method("getRoot").invoke(origin), diff + 1L);
            } else {
                //delete
                queryBuilder.deleteByLeftNodeBetween(m.getClass(), tName, (M) method("getRoot").invoke(origin), leftNode, rightNode);
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> update(Long seq, M data) {
        try {
            //origin
            M origin = (M) findOne(seq).getBody();
            //update
//            return new ResponseEntity<>(update.invoke(serviceInst, origin, data), HttpStatus.OK);
            return new ResponseEntity<>(method("update").invoke(s, origin, data), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> update(M origin, M data) {
        try {
//            return new ResponseEntity<>(update.invoke(serviceInst, origin, data), HttpStatus.OK);
            return new ResponseEntity<>(method("update").invoke(s, origin, data), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> moveUp(String tName, Long seq) {

        try {
            //depth 0 disable
            M origin = (M) findOne(seq).getBody();
            M originCopy = origin;
//            Long leftNode = (Long) getLeftNode.invoke(origin);
//            Long rightNode = (Long) getRightNode.invoke(origin);
            Long leftNode = (Long) method("getLeftNode").invoke(origin);
            Long rightNode = (Long) method("getRightNode").invoke(origin);
            Long diff = rightNode - leftNode;
//            Long depth = (Long) getDepth.invoke(origin);
            Long depth = (Long) method("getDepth").invoke(origin);
            if (depth > 0L) {
                //check depth`s min left node
//                if (checkLeftMinFromDepth(tName, (M) getRoot.invoke(origin), depth).getBody() == leftNode) {
//                    setParent.invoke(origin, (M) queryBuilder.findMaxParentByDepth(m.getClass(), tName, (M) getRoot.invoke(origin), depth-1L).getBody());
//                    setDepth.invoke(depth-1L);
//                }
                if (checkLeftMinFromDepth(tName, (M) method("getRoot").invoke(origin), depth).getBody() == leftNode) {
                    method("setParent").invoke(origin, (M) queryBuilder.findMaxParentByDepth(m.getClass(), tName, (M) method("getRoot").invoke(origin), depth - 1L).getBody());
                    method("setDepth").invoke(depth - 1L);
                }
                //find next node by leftNode
                M target = (M) findByRightNode(tName, (M) method("getRoot").invoke(origin), leftNode - 1L).getBody();
                M targetCopy = target;
//                Long nextLeftNode = (Long) getLeftNode.invoke(target);
//                Long nextRightNode = (Long) getRightNode.invoke(target);
                Long nextLeftNode = (Long) method("getLeftNode").invoke(target);
                Long nextRightNode = (Long) method("getRightNode").invoke(target);
                Long nextDiff = nextRightNode - nextLeftNode;
                //both don`t have child
                if (diff == 1 && nextDiff == 1) {
                    //switch both
//                    setLeftNode.invoke(originCopy, nextLeftNode);
//                    setRightNode.invoke(originCopy, nextRightNode);
//                    setLeftNode.invoke(targetCopy, leftNode);
//                    setRightNode.invoke(targetCopy, rightNode);
                    method("setLeftNode").invoke(originCopy, nextLeftNode);
                    method("setRightNode").invoke(originCopy, nextRightNode);
                    method("setLeftNode").invoke(targetCopy, leftNode);
                    method("setRightNode").invoke(targetCopy, rightNode);
                    update(origin, originCopy);
                    update(target, targetCopy);
                } else {
                    //switch both and sub
                    Long groupDiff = nextLeftNode - leftNode;

                    Long changeLeft = rightNode - diff;
                    Long changeRight = nextRightNode;
                    Long changeNextLeft = leftNode;
                    Long changeNextRight = nextLeftNode + nextDiff;

//                    setLeftNode.invoke(originCopy, changeLeft);
//                    setRightNode.invoke(originCopy, changeRight);
//                    setLeftNode.invoke(targetCopy, changeNextLeft);
//                    setRightNode.invoke(targetCopy, changeNextRight);
                    method("setLeftNode").invoke(originCopy, changeLeft);
                    method("setRightNode").invoke(originCopy, changeRight);
                    method("setLeftNode").invoke(targetCopy, changeNextLeft);
                    method("setRightNode").invoke(targetCopy, changeNextRight);

                    update(origin, originCopy);
                    update(target, targetCopy);

                    //sub
//                    queryBuilder.originSetting(m.getClass(), tName, (M) getRoot.invoke(origin), leftNode, rightNode, groupDiff);
//                    queryBuilder.targetSetting(m.getClass(), tName, (M) getRoot.invoke(origin), nextLeftNode, nextRightNode, groupDiff);
                    queryBuilder.originSetting(m.getClass(), tName, (M) method("getRoot").invoke(origin), leftNode, rightNode, groupDiff);
                    queryBuilder.targetSetting(m.getClass(), tName, (M) method("getRoot").invoke(origin), nextLeftNode, nextRightNode, groupDiff);

                }
            } else {
                //depth is 0
//                Long order = (Long) getNodeOrder.invoke(origin);
                Long order = (Long) method("getNodeOrder").invoke(origin);
                if (order >= 1) {
                    order = order - 1L;
//                    setNodeOrder.invoke(originCopy, order);
//                    queryBuilder.addOrder(m.getClass(), tName, order);
//                    update.invoke(serviceInst, origin);
                    method("setNodeOrder").invoke(originCopy, order);
                    queryBuilder.addOrder(m.getClass(), tName, order);
                    method("update").invoke(s, origin);
                }
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> moveDown(String tName, Long seq) {
        try {
            //opposite moveup
            //depth 0 and last disable
            M origin = (M) findOne(seq).getBody();
            M originCopy = origin;
//            Long leftNode = (Long) getLeftNode.invoke(origin);
//            Long rightNode = (Long) getRightNode.invoke(origin);
            Long leftNode = (Long) method("getLeftNode").invoke(origin);
            Long rightNode = (Long) method("getRightNode").invoke(origin);
            Long diff = rightNode - leftNode;
//            Long depth = (Long) getDepth.invoke(origin);
            Long depth = (Long) method("getDepth").invoke(origin);
            if (depth > 0L) {
                //check depth`s max right node
//                if (checkRightMaxFromDepth(tName, (M) getRoot.invoke(origin), depth).getBody() == rightNode) {
//                    setParent.invoke(origin, (M) queryBuilder.findMinParentByDepth(m.getClass(), tName, (M) getRoot.invoke(origin), depth).getBody());
//                    setDepth.invoke(depth+1L);
//                }
                if (checkRightMaxFromDepth(tName, (M) method("getRoot").invoke(origin), depth).getBody() == rightNode) {
                    method("setParent").invoke(origin, (M) queryBuilder.findMinParentByDepth(m.getClass(), tName, (M) method("getRoot").invoke(origin), depth).getBody());
                    method("setDepth").invoke(depth + 1L);
                }
                //find next node by rightNode
//                M target = (M) findByLeftNode(tName, (M) getRoot.invoke(origin), rightNode + 1L).getBody();
//                M targetCopy = target;
//                Long nextLeftNode = (Long) getLeftNode.invoke(target);
//                Long nextRightNode = (Long) getRightNode.invoke(target);
                M target = (M) findByLeftNode(tName, (M) method("getRoot").invoke(origin), rightNode + 1L).getBody();
                M targetCopy = target;
                Long nextLeftNode = (Long) method("getLeftNode").invoke(target);
                Long nextRightNode = (Long) method("getRightNode").invoke(target);
                Long nextDiff = nextRightNode - nextLeftNode;
                //both don`t have child
                if (diff == 1 && nextDiff == 1) {
                    //switch both
//                    setLeftNode.invoke(originCopy, nextLeftNode);
//                    setRightNode.invoke(originCopy, nextRightNode);
//                    setLeftNode.invoke(targetCopy, leftNode);
//                    setRightNode.invoke(targetCopy, rightNode);
                    method("setLeftNode").invoke(originCopy, nextLeftNode);
                    method("setRightNode").invoke(originCopy, nextRightNode);
                    method("setLeftNode").invoke(targetCopy, leftNode);
                    method("setRightNode").invoke(targetCopy, rightNode);
                    update(origin, originCopy);
                    update(target, targetCopy);
                } else {
                    //switch both and sub
                    Long groupDiff = leftNode - nextLeftNode;

                    Long changeLeft = nextRightNode - nextDiff;
                    Long changeRight = rightNode;
                    Long changeNextLeft = nextLeftNode;
                    Long changeNextRight = leftNode + diff;

//                    setLeftNode.invoke(originCopy, changeLeft);
//                    setRightNode.invoke(originCopy, changeRight);
//                    setLeftNode.invoke(targetCopy, changeNextLeft);
//                    setRightNode.invoke(targetCopy, changeNextRight);
                    method("setLeftNode").invoke(originCopy, changeLeft);
                    method("setRightNode").invoke(originCopy, changeRight);
                    method("setLeftNode").invoke(targetCopy, changeNextLeft);
                    method("setRightNode").invoke(targetCopy, changeNextRight);
                    update(origin, originCopy);
                    update(target, targetCopy);

                    //sub
//                    queryBuilder.originSetting(m.getClass(), tName, (M) getRoot.invoke(origin), nextLeftNode, nextRightNode, groupDiff);
//                    queryBuilder.targetSetting(m.getClass(), tName, (M) getRoot.invoke(origin), leftNode, rightNode, groupDiff);
                    queryBuilder.originSetting(m.getClass(), tName, (M) method("getRoot").invoke(origin), nextLeftNode, nextRightNode, groupDiff);
                    queryBuilder.targetSetting(m.getClass(), tName, (M) method("getRoot").invoke(origin), leftNode, rightNode, groupDiff);
                }
            } else {
//                Long order = (Long) getNodeOrder.invoke(origin);
                Long order = (Long) method("getNodeOrder").invoke(origin);
                Long limit = (Long) queryBuilder.countByDepth(m.getClass(), tName, 0L).getBody();
                if (order < limit) {
                    order = order + 1L;
//                    setNodeOrder.invoke(originCopy);
//                    queryBuilder.subOrder(m.getClass(), tName, order);
//                    update.invoke(origin, originCopy);
                    method("setNodeOrder").invoke(originCopy);
                    queryBuilder.subOrder(m.getClass(), tName, order);
                    method("update").invoke(origin, originCopy);
                }
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> insert(String tName, M data) {
        try {
            LOG.info("insert2");
            method("setRightNode").invoke(data, 2L);
            method("setLeftNode").invoke(data, 1L);
            method("setDepth").invoke(data, 0);
            Long maxNodeOrder = (Long) queryBuilder.findMaxNodeOrder(m.getClass(), tName).getBody();
            method("setNodeOrder").invoke(maxNodeOrder+1L);
            LOG.info("insert3");
            Object result = method("save").invoke(data);
            LOG.info("insert4");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e) {
            LOG.info("insert5 : " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
