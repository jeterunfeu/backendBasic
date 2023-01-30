package com.srlab.basic.serverside.queries;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class QueryBuilder<P> {

    private final Logger LOG = LoggerFactory.getLogger(QueryBuilder.class);

    //class
    private Class<P> m;

    public QueryBuilder() throws NoSuchMethodException {
    }

    public void set(Class<P> m) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.m = m;
    }

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

//    Method getSeq = m.getClass().getMethod("getSeq");

    public Method method() throws NoSuchMethodException {
        return m.getClass().getMethod("getSeq");
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
                    .where(builder
//                     containsKeyword(product.name, keyword),
//                     toEqExpression(product.category, category)
                    )
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
//        return toSlice(pageable, jpaQuery.fetch());
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
    public <T> ResponseEntity<?> findLeftMinByDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("depth").eq(depth.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath.get(Expressions.stringPath("leftNode")).min())
                    .where(builder
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> findRightMaxFromDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("depth").eq(depth.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath.get(Expressions.stringPath("rightNode")).max())
                    .where(builder
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> findByRightNode(Class<T> clazz, String tName, P root, Long leftNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("rightNode").eq(leftNode.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath)
                    .where(builder
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> findByLeftNode(Class<T> clazz, String tName, P root, Long rightNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("leftNode").eq(rightNode.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath)
                    .where(builder
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> addRight(Class<T> clazz, String tName, P root, Long rightNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("rightNode").gt(rightNode.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("rightNode"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("+2"))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> addDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("depth").gt(depth.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("depth"));
            StringPath parent = entityPath.get(Expressions.stringPath("parent.seq"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("+1"))
                    .set(parent, method().invoke(root).toString())
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> addLeftNode(Class<T> clazz, String tName, P root, Long leftNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("leftNode").gt(leftNode.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("leftNode"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("+2"))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> addRightNode(Class<T> clazz, String tName, P root, Long leftNode) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("leftNode").gt(leftNode.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("rightNode"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("+2"))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> deleteByLeftNodeBetween(Class<T> clazz, String tName, P root, Long from, Long to) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("leftNode").between(from.toString(), to.toString()));

            Long jpaQuery = jpaQueryFactory.delete(entityPath)
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> updateLeftNode(Class<T> clazz, String tName, P root, Long diff) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("leftNode").gt(diff.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("leftNode"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("-"+diff.toString()))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> updateRightNode(Class<T> clazz, String tName, P root, Long diff) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("rightNode").gt(diff.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("rightNode"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("-"+diff.toString()))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> findMaxParentByDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("depth").eq(depth.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath.get(Expressions.stringPath("parent")))
                    .where(
                            Expressions.stringPath("leftNode").eq(
                                    JPAExpressions.select(entityPath.get(Expressions.stringPath("leftNode")).max())
                                            .where(builder)
                            )
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> findMinParentByDepth(Class<T> clazz, String tName, P root, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("depth").eq(depth.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath.get(Expressions.stringPath("parent")))
                    .where(
                            Expressions.stringPath("leftNode").eq(
                                    JPAExpressions.select(entityPath.get(Expressions.stringPath("leftNode")).min())
                                            .where(builder)
                            )
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> countByDepth(Class<T> clazz, String tName, Long depth) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("depth").eq(depth.toString()));

            List<?> jpaQuery = jpaQueryFactory.select(entityPath.count())
                    .where(builder
                    )
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> originSetting(Class<T> clazz, String tName, P root, Long leftNode, Long rightNode, Long groupDiff) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("leftNode").between(leftNode.toString(), rightNode.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("leftNode"));
            StringPath rightPath = entityPath.get(Expressions.stringPath("rightNode"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("-"+groupDiff.toString()))
                    .set(rightPath, rightPath.append("-"+groupDiff.toString()+"+1"))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> targetSetting(Class<T> clazz, String tName, P root, Long nextLeftNode, Long nextRightNode, Long groupDiff) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("root.seq").eq(method().invoke(root).toString()));
            builder.and(Expressions.stringPath("leftNode").between(nextLeftNode.toString(), nextRightNode.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("leftNode"));
            StringPath rightPath = entityPath.get(Expressions.stringPath("rightNode"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("+"+groupDiff.toString()))
                    .set(rightPath, rightPath.append("+"+ groupDiff +"+1"))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> subOrder(Class<T> clazz, String tName, Long order) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            builder.and(Expressions.stringPath("depth").eq("0"));
            builder.and(Expressions.stringPath("nodeOrder").gt(order.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("nodeOrder"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("-1"))
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

            builder.and(Expressions.stringPath("depth").eq("0"));
            builder.and(Expressions.stringPath("nodeOrder").gt(order.toString()));

            StringPath path = entityPath.get(Expressions.stringPath("nodeOrder"));
            Long jpaQuery = jpaQueryFactory.update(entityPath)
                    .set(path, path.append("+1"))
                    .where(builder
                    )
                    .execute();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<?> findMaxNodeOrder(Class<T> clazz, String tName) {
        try {
            PathBuilder<T> entityPath = new PathBuilder<T>(clazz, tName);

            List<?> jpaQuery = jpaQueryFactory.select(entityPath.get(Expressions.stringPath("nodeOrder")).max())
                    .fetch();

            return new ResponseEntity<>(jpaQuery, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
