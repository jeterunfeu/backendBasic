package com.srlab.basic.serverside.queries;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueryBuilder<P> {

    private final JPAQueryFactory jpaQueryFactory;

    private final Logger LOG = LoggerFactory.getLogger(QueryBuilder.class);

    //class
    private P m;

    public void set(P m) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        this.m = m;
    }

    public Method method( P root, String modelName) throws NoSuchMethodException {
        Method result = null;
        switch (modelName) {
            case "getRoot":
                result = root.getClass().getDeclaredMethod("getRoot");
                break;
            case "getSeq":
                result = root.getClass().getDeclaredMethod("getSeq");
                break;
            case "getParent":
                result = root.getClass().getDeclaredMethod("getParent");
                break;
        }
        result.setAccessible(true);
        return result;
    }

    public <T> ResponseEntity<?> findAllByConditions(Class<T> clazz, String tName, Map<String, String> orMap, Pageable pageable) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);
            for (String key : orMap.keySet()) {
                if (!key.equals("page") && !key.equals("size") && !key.equals("sort")) {
                    StringPath sPath = Expressions.stringPath(key);
                    builder.or(containsKeyword(sPath, orMap.get(key)));
                }
            }
            List<T> jpaQuery = jpaQueryFactory.selectFrom(entityPath)
                    .where(builder)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize() + 1)
                    .orderBy(makeOrderSpecifiers(entityPath, pageable))
                    .fetch();
            JPAQuery<Long> countQuery = jpaQueryFactory.select(entityPath.count())
                    .where(builder)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize() + 1)
                    .orderBy(makeOrderSpecifiers(entityPath, pageable));
            return new ResponseEntity<>(PageableExecutionUtils.getPage(jpaQuery, pageable, countQuery::fetchOne), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> OrderSpecifier[] makeOrderSpecifiers(EntityPathBase<T> qClass, Pageable pageable) {
        return pageable.getSort()
                .stream()
                .map(sort -> toOrderSpecifier(qClass, sort))
                .collect(Collectors.toList()).toArray(OrderSpecifier[]::new);
    }

    private static <T> OrderSpecifier toOrderSpecifier(EntityPathBase<T> qClass, Sort.Order sortOrder) {
        final Order orderMethod = toOrder(sortOrder);
        final PathBuilder<T> pathBuilder = new PathBuilder<>(qClass.getType(), qClass.getMetadata());
        return new OrderSpecifier(orderMethod, pathBuilder.get(sortOrder.getProperty()));
    }

    private static Order toOrder(Sort.Order sortOrder) {
        if (sortOrder.isAscending()) {
            return Order.ASC;
        }
        return Order.DESC;
    }

    public static BooleanExpression containsKeyword(StringPath stringPath, String keyword) {
        if (Objects.isNull(keyword) || keyword.isBlank()) {
            return null;
        }
        return stringPath.contains(keyword);
    }

    // common query start
    //DB empty check
    public <T> Boolean checkEmpty(Class<T> clazz, String tName) {
        try {
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);
            Long jpaQuery = jpaQueryFactory.select(Expressions.stringPath("seq").count())
                    .from(entityPath)
                    .fetchOne();
            return jpaQuery != 0 ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public <T> Long findMaxDepth(Class<T> clazz, String tName, P root) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "depth").max())
                    .from(entityPath)
                    .where(builder)
                    .fetchOne();

            return jpaQuery == null ? 0L : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Long findLeftMinByDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "depth").eq(depth));

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "leftNode").min())
                    .from(entityPath)
                    .where(builder)
                    .fetchOne();

            return jpaQuery == null ? 0L : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Object findByRightNode(Class<T> clazz, String tName, P root, Long leftNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "rightNode").eq(leftNode));

             Object jpaQuery = jpaQueryFactory.selectFrom(entityPath)
                    .where(builder)
                    .fetchOne();

            return jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @Modifying
    public <T> ResponseEntity<?> addDepth(Class<T> clazz, String tName, P origin, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            P root = (P) method(origin, "getRoot").invoke(origin);
            P parent = (P) method(origin, "getParent").invoke(origin);

            Long parentSeq = (Long) method(parent, "getSeq").invoke(parent);

            builder.and(Expressions.numberPath(Long.class, "root.seq")
                    .eq((Long) method(root, "getSeq").invoke(root)));
            builder.and(Expressions.numberPath(Long.class, "parent.seq").eq(parentSeq));
            builder.and(Expressions.numberPath(Long.class, "depth").gt(depth));

            StringPath path = Expressions.stringPath("depth");
            NumberPath value = Expressions.numberPath(Long.class, "depth");

            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, value.add(1L))
                    .where(builder)
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Modifying
    public <T> ResponseEntity<?> addClear(Class<T> clazz, String tName, P root, Long leftNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.numberPath(Long.class, "root.seq")
                    .eq((Long) method(root, "getSeq").invoke(root)));
            builder.and(Expressions.numberPath(Long.class, "leftNode").gt(leftNode));

            StringPath path1 = Expressions.stringPath("leftNode");
            StringPath path2 = Expressions.stringPath("rightNode");
            NumberPath add1 = Expressions.numberPath(Long.class, "leftNode");
            NumberPath add2 = Expressions.numberPath(Long.class, "rightNode");

            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path1, add1.add(2))
                    .set(path2, add2.add(2))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Modifying
    public <T> ResponseEntity<?> addMiddleClear(Class<T> clazz, String tName, P origin) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(origin, "getSeq").invoke(origin);

            builder.and(Expressions.numberPath(Long.class, "seq").eq(seq));

            StringPath path = Expressions.stringPath("rightNode");
            NumberPath value = Expressions.numberPath(Long.class, "rightNode");

            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, value.add(2))
                    .where(builder)
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Modifying
    public <T> ResponseEntity<?> addEndClear(Class<T> clazz, String tName, P root) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long rightNode = jpaQueryFactory.select(Expressions.numberPath(Long.class, "rightNode").max())
                    .from(entityPath)
                    .where(Expressions.numberPath(Long.class, "root.seq")
                            .eq((Long) method(root, "getSeq").invoke(root)))
                    .fetchOne();

            builder.and(Expressions.numberPath(Long.class, "seq")
                    .eq((Long) method(root, "getSeq").invoke(root)));

            NumberPath path = Expressions.numberPath(Long.class, "rightNode");
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, rightNode + 1L)
                    .where(builder)
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Modifying
    public <T> ResponseEntity<?> deleteRoot(Class<T> clazz, String tName, Long rootSeq) {
        try {
            LOG.info("delete");
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.or(Expressions.numberPath(Long.class, "root.seq")
                    .eq(rootSeq));
            builder.or(Expressions.numberPath(Long.class, "seq")
                    .eq(rootSeq));

            Long result = jpaQueryFactory.delete(entityPath)
                    .where(builder)
                    .execute();

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Modifying
    public <T> ResponseEntity<?> deleteByLeftNodeBetween(Class<T> clazz, String tName, P root, Long from, Long to) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.numberPath(Long.class, "leftNode").between(from, to));

            Long jpaQuery = jpaQueryFactory.delete(entityPath)
                    .where(builder)
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Modifying
    public <T> ResponseEntity<?> deleteNodeClear(Class<T> clazz, String tName, P root, Long diff) {
        try {
            BooleanBuilder builder1 = new BooleanBuilder();
            BooleanBuilder builder2 = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder1.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder1.and(Expressions.numberPath(Long.class, "leftNode").gt(diff + 1L));

            builder2.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder2.and(Expressions.numberPath(Long.class, "rightNode").gt(diff + 2L));

            StringPath path1 = Expressions.stringPath("leftNode");
            NumberPath value1 = Expressions.numberPath(Long.class, "leftNode");
            StringPath path2 = Expressions.stringPath("rightNode");
            NumberPath value2 = Expressions.numberPath(Long.class, "rightNode");

            Long jpaQuery1 = jpaQueryFactory.update(entityPath)
                    .set(path1, value1.subtract(diff + 1L))
                    .where(builder1)
                    .execute();

            Long jpaQuery2 = jpaQueryFactory.update(entityPath)
                    .set(path2, value2.subtract(diff + 1L))
                    .where(builder2)
                    .execute();

            return new ResponseEntity<>(jpaQuery1 + " / " + jpaQuery2, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> Long findMaxNodeOrder(Class<T> clazz, String tName) {
        try {
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "nodeOrder").max())
                    .from(entityPath)
                    .fetchOne();

            return jpaQuery == null ? 0L : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Object findLastNodeDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            BooleanBuilder subBuilder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            subBuilder.and(Expressions.numberPath(Long.class, "depth").eq(depth));
            subBuilder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            Long max = jpaQueryFactory.select(Expressions.numberPath(Long.class, "rightNode").max())
                    .from(entityPath)
                    .where(subBuilder)
                    .fetchOne();

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "rightNode").eq(max));

            Object jpaQuery = jpaQueryFactory.selectFrom(entityPath)
                    .where(builder)
                    .fetchOne();

            return jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Modifying
    @Transactional
    public <T> void nodeChange(Class<T> clazz, String tName, P root, Long originRightNode, Long originLeftNode,
                           Long targetRightNode, Long targetLeftNode) {
        try {
            BooleanBuilder originBuilder = new BooleanBuilder();
            BooleanBuilder targetBuilder = new BooleanBuilder();
            BooleanBuilder recoveryBuilder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            originBuilder.or(Expressions.numberPath(Long.class, "leftNode").between(originLeftNode, originRightNode));
            originBuilder.or(Expressions.numberPath(Long.class, "rightNode").between(originLeftNode, originRightNode));
            originBuilder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            targetBuilder.or(Expressions.numberPath(Long.class, "leftNode").between(targetLeftNode, targetRightNode));
            targetBuilder.or(Expressions.numberPath(Long.class, "rightNode").between(targetLeftNode, targetRightNode));
            targetBuilder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            recoveryBuilder.or(Expressions.numberPath(Long.class, "leftNode").lt(0L));
            recoveryBuilder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            Long originDiff = originRightNode - originLeftNode + 1L;
            Long targetDiff = targetRightNode - targetLeftNode + 1L;

            StringPath path1 = Expressions.stringPath("leftNode");
            NumberPath value1 = Expressions.numberPath(Long.class, "leftNode");
            StringPath path2 = Expressions.stringPath("rightNode");
            NumberPath value2 = Expressions.numberPath(Long.class, "rightNode");

            //origin multiply
            jpaQueryFactory.update(entityPath)
                    .set(path1, value1.subtract(targetDiff).multiply(-1L))
                    .set(path2, value2.subtract(targetDiff).multiply(-1L))
                    .where(originBuilder)
                    .execute();
            //target add
            jpaQueryFactory.update(entityPath)
                    .set(path1, value1.add(originDiff))
                    .set(path2, value2.add(originDiff))
                    .where(targetBuilder)
                    .execute();
            //origin multiply
            jpaQueryFactory.update(entityPath)
                    .set(path1, value1.multiply(-1L))
                    .set(path2, value2.multiply(-1L))
                    .where(recoveryBuilder)
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Modifying
    @Transactional
    public <T> void nodeChangeMore(Class<T> clazz, String tName, P root, Long depth, P parent,
                               Long targetDepth, P targetParent, Long originRightNode,
                               Long originLeftNode, Long targetRightNode, Long targetLeftNode, Boolean flag) {

        try {
            BooleanBuilder originBuilder = new BooleanBuilder();
            BooleanBuilder targetBuilder = new BooleanBuilder();
            BooleanBuilder recoveryBuilder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);
            Long parentSeq = (Long) method(parent, "getSeq").invoke(parent);
            Long targetParentSeq = (Long) method(targetParent, "getSeq").invoke(targetParent);

            originBuilder.and(Expressions.numberPath(Long.class, "leftNode").eq(originLeftNode));
            originBuilder.and(Expressions.numberPath(Long.class, "rightNode").eq(originRightNode));
            originBuilder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            targetBuilder.and(Expressions.numberPath(Long.class, "leftNode").eq(targetLeftNode));
            targetBuilder.and(Expressions.numberPath(Long.class, "rightNode").eq(targetRightNode));
            targetBuilder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            recoveryBuilder.and(Expressions.numberPath(Long.class, "leftNode").lt(0L));
            recoveryBuilder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));

            StringPath path1 = Expressions.stringPath("leftNode");
            StringPath path2 = Expressions.stringPath("rightNode");
            NumberPath path3 = Expressions.numberPath(Long.class, "depth");
            NumberPath path4 = Expressions.numberPath(Long.class, "parent.seq");

            NumberPath value1 = Expressions.numberPath(Long.class, "leftNode");
            NumberPath value2 = Expressions.numberPath(Long.class, "rightNode");

            NumberExpression exp1;
            NumberExpression exp2;
            NumberExpression exp3;
            NumberExpression exp4;

            if(flag) {
                exp1 = value1.subtract(1L);
                exp2 = value2.add(1L);
                exp3 = value1.add(1L);
                exp4 = value2.subtract(1L);
            } else {
                exp1 = value1.add(1L);
                exp2 = value2.subtract(1L);
                exp3 = value1.subtract(1L);
                exp4 = value2.add(1L);
            }

            //origin multiply
            jpaQueryFactory.update(entityPath)
                    .set(path1, exp1.multiply(-1L))
                    .set(path2, exp2.multiply(-1L))
                    .set(path3, targetDepth)
                    .set(path4, targetParentSeq)
                    .where(originBuilder)
                    .execute();
            //target add
            jpaQueryFactory.update(entityPath)
                    .set(path1, exp3)
                    .set(path2, exp4)
                    .set(path3, depth)
                    .set(path4, parentSeq)
                    .where(targetBuilder)
                    .execute();
            //origin multiply
            jpaQueryFactory.update(entityPath)
                    .set(path1, value1.multiply(-1L))
                    .set(path2, value2.multiply(-1L))
                    .where(recoveryBuilder)
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> Long findNodeOrderMin(Class<T> clazz, String tName) {
        try {
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "nodeOrder").min())
                    .from(entityPath)
                    .fetchOne();

            return jpaQuery == null ? 0L : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Modifying
    @Transactional
    public <T> void nodeOrderChange(Class<T> clazz, String tName, P root, Long nodeOrder, Boolean flag) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            BooleanBuilder subBuilder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            subBuilder.and(Expressions.numberPath(Long.class, "nodeOrder").lt(nodeOrder));

            NumberPath path = Expressions.numberPath(Long.class, "nodeOrder");

            Long orderValue;

            if(flag) {
                orderValue = jpaQueryFactory.select(Expressions.numberPath(Long.class, "nodeOrder").max())
                        .from(entityPath)
                        .where(builder)
                        .fetchOne();
            } else {
                orderValue = jpaQueryFactory.select(Expressions.numberPath(Long.class, "nodeOrder").min())
                        .from(entityPath)
                        .where(builder)
                        .fetchOne();
            }

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "nodeOrder").eq(orderValue));

            //target change
            jpaQueryFactory.update(entityPath)
                    .set(path, nodeOrder)
                    .where(builder)
                    .execute();
            //origin change
            jpaQueryFactory.update(entityPath)
                    .set(path, orderValue)
                    .where(Expressions.numberPath(Long.class, "seq").eq(seq))
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T>Long findLeftMaxByDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "depth").eq(depth));

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "leftNode").max())
                    .from(entityPath)
                    .where(builder)
                    .fetchOne();

            return jpaQuery == null ? 0L : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Long findNodeOrderMax(Class<T> clazz, String tName) {
        try {
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "nodeOrder").max())
                    .from(entityPath)
                    .fetchOne();

            return jpaQuery == null ? 0L : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Long checkDepthLastNode(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "depth").eq(depth));

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "depth").count())
                    .from(entityPath)
                    .where(builder)
                    .fetchOne();

            return jpaQuery == null ? 0L : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> void arrangeDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "depth").gt(depth));

            StringPath path = Expressions.stringPath("depth");
            NumberPath value = Expressions.numberPath(Long.class, "depth");

            jpaQueryFactory.update(entityPath)
                    .set(path, value.subtract(1))
                    .where(builder)
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
