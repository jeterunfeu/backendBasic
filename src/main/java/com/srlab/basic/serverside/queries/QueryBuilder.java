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
    private Class<P> m;

    public void set(Class<P> m) throws NoSuchMethodException, InvocationTargetException,
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

    public <T> Long findRightMaxFromDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root,"getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "depth").eq(depth));

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "rightNode").max())
                    .from(entityPath)
                    .where(builder)
                    .fetchOne();

            return jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> ResponseEntity<?> findByRightNode(Class<T> clazz, String tName, P root, Long leftNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "rightNode").eq(leftNode));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath)
                    .where(builder
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> Object findByLeftNode(Class<T> clazz, String tName, P root, Long rightNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "leftNode").eq(rightNode));

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

    public <T> Object findMaxParentByDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "depth").eq(depth));

            JPQLQuery sub = JPAExpressions.select(Expressions.numberPath(Long.class, "leftNode").max())
                    .where(builder);

            Object jpaQuery = jpaQueryFactory.select(entityPath.get(Expressions.stringPath("parent")))
                    .where(Expressions.numberPath(Long.class, "leftNode").eq(sub))
                    .fetchOne();

            return jpaQuery;
        } catch (Exception e) {
            return null;
        }
    }

    public <T> Object findMinParentByDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "depth").eq(depth));

            JPQLQuery sub = JPAExpressions.select(Expressions.numberPath(Long.class, "leftNode").min()).where(builder);

            Object jpaQuery = jpaQueryFactory.select(entityPath.get(Expressions.stringPath("parent")))
                    .where(Expressions.stringPath("leftNode").eq(sub))
                    .fetchOne();

            return jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Long countByDepth(Class<T> clazz, String tName, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<>(clazz, tName);

            builder.and(Expressions.stringPath("depth").eq(depth.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(Expressions.stringPath("depth").count())
                    .from(entityPath)
                    .fetch();
            Long result = Long.parseLong(jpaQuery.get(0).toString());

            return result == null ? 0L : result;
        } catch (Exception e) {
            return null;
        }
    }

    public <T> ResponseEntity<?> originSetting(Class<T> clazz, String tName, P root, Long leftNode,
                                               Long rightNode, Long groupDiff) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class, "root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "leftNode").between(leftNode, rightNode));

            StringPath path1 = Expressions.stringPath("leftNode");
            StringPath path2 = Expressions.stringPath( "rightNode");

            NumberPath value1 = Expressions.numberPath(Long.class, "leftNode");
            NumberPath value2 = Expressions.numberPath(Long.class, "rightNode");

            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path1, value1.subtract(groupDiff))
                    .set(path2, value2.subtract(groupDiff + 1L))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> targetSetting(Class<T> clazz, String tName, P root, Long nextLeftNode,
                                               Long nextRightNode, Long groupDiff) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long seq = (Long) method(root, "getSeq").invoke(root);

            builder.and(Expressions.numberPath(Long.class,"root.seq").eq(seq));
            builder.and(Expressions.numberPath(Long.class, "leftNode").between(nextLeftNode, nextRightNode));

            StringPath path1 = Expressions.stringPath("leftNode");
            StringPath path2 = Expressions.stringPath("rightNode");

            NumberPath value1 = Expressions.numberPath(Long.class, "leftNode");
            NumberPath value2 = Expressions.numberPath(Long.class, "rightNode");

            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path1, value1.add(groupDiff))
                    .set(path2, value2.add(groupDiff + 1L))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> addOrder(Class<T> clazz, String tName, Long order) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.numberPath(Long.class, "depth").eq(0L));
            builder.and(Expressions.numberPath(Long.class, "nodeOrder").gt(order));

            StringPath path = Expressions.stringPath("nodeOrder");
            NumberPath value = Expressions.numberPath(Long.class, "nodeOrder");
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, value.add(1L))
                    .where(builder)
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> Long findMaxNodeOrder(Class<T> clazz, String tName) {
        try {
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            Long jpaQuery = jpaQueryFactory.select(Expressions.numberPath(Long.class, "nodeOrder").max())
                    .from(entityPath)
                    .fetchOne();

            return jpaQuery == null ? 0 : jpaQuery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> ResponseEntity<?> subOrder(Class<T> clazz, String tName, Long order) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.numberPath(Long.class, "depth").eq(0L));
            builder.and(Expressions.numberPath(Long.class, "nodeOrder").gt(order));

            StringPath path = Expressions.stringPath("nodeOrder");
            NumberPath value = Expressions.numberPath(Long.class, "nodeOrder");
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, value.subtract(1L))
                    .where(builder)
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
