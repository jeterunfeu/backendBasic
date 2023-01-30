package com.srlab.basic.serverside.boards.repositories;

import com.srlab.basic.serverside.boards.models.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;

import javax.transaction.Transactional;

public interface BoardRepository extends JpaRepository<Board, Long> {
//    @Query(" select hd from Board hd where hd.root=:root and hd.rightNode=:left ")
//    Board findByRightNode(@Param("root") Board root, @Param("left") Long leftNode);
//
//    @Query(" select hd from Board hd where hd.root=:root and hd.leftNode=:right ")
//    Board findByLeftNode(@Param("root") Board root, @Param("right") Long rightNode);
//
//    @Query(" select min(hd.leftNode) from Board hd where hd.root=:root and hd.depth=:depth ")
//    Long findLeftMinByDepth(@Param("root") Board root, @Param("depth") Long depth);
//
//    @Query(" select max(hd.rightNode) from Board hd where hd.root=:root and hd.depth=:depth ")
//    Long findRightMaxFromDepth(@Param("root") Board root, Long depth);
//
//    @Query(" select hd.parent from Board hd where hd.leftNode = (select max(hd.leftNode) from hd where hd.root=:root and hd.depth=:depth) ")
//    Board findMinParentByDepth(@Param("root") Board root, @Param("depth") Long depth);
//
//    @Query(" select hd.parent from Board hd where hd.leftNode = (select min(hd.leftNode) from hd where hd.root=:root and hd.depth=:depth) ")
//    Board findMaxParentByDepth(@Param("root") Board root, @Param("depth") Long depth);
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.rightNode=hd.rightNode+2 where hd.root=:root and hd.rightNode>:value ")
//    void addRight(@Param("root") Board root, @Param("value") Long rightNode);
//
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.depth=hd.depth+1, hd.parent=:root where hd.root=:root and hd.depth>:depth ")
//    void addDepth(@Param("root") Board root, @Param("depth") Long depth);
//
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.leftNode=hd.leftNode-:diff where hd.root=:root and hd.leftNode>:diff ")
//    void updateLeftNode(@Param("root") Board root, @Param("diff") Long diff);
//
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.rightNode=hd.rightNode-:diff where hd.root=:root and hd.rightNode>:diff ")
//    void updateRightNode(@Param("root") Board root, @Param("diff") Long diff);
//
//    @Transactional
//    @Modifying
//    @Query(" delete from Board hd where hd.root=:root and hd.leftNode between :from and :to ")
//    void deleteByLeftNodeBetween(@Param("root") Board root, @Param("from") Long from, @Param("to") Long to);
//
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.leftNode=hd.leftNode-:diff, " +
//            " hd.rightNode=hd.rightNode-:diff+1" +
//            " where hd.root=:root and hd.leftNode between :left and :right ")
//    void originSetting(@Param("root") Board root, @Param("left") Long leftNode, @Param("right") Long rightNode, @Param("diff") Long groupDiff);
//
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.leftNode=hd.leftNode+:diff, " +
//            " hd.rightNode=hd.rightNode+:diff+1" +
//            " where hd.root=:root and hd.leftNode between :left and :right ")
//    void targetSetting(@Param("root") Board root, @Param("left") Long nextLeftNode, @Param("right") Long nextRightNode, @Param("diff") Long groupDiff);
//
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.leftNode=hd.leftNode+2 where hd.root=:root and hd.leftNode>:left ")
//    void addLeftNode(@Param("root") Board root, @Param("left") Long leftNode);
//
//    @Transactional
//    @Modifying
//    @Query(" update Board hd set hd.rightNode=hd.rightNode+2 where hd.root=:root and hd.rightNode>:right ")
//    void addRightNode(@Param("root") Board root, @Param("right") Long rightNode);
//
//    @Query(" update Board hd set hd.nodeOrder=hd.nodeOrder+1 where hd.depth=0 and hd.nodeOrder=:order ")
//    void addOrder(@Param("order") Long order);
//
//    @Query(" update Board hd set hd.nodeOrder=hd.nodeOrder+1 where hd.depth=0 and hd.nodeOrder=:order ")
//    void subOrder(Long order);
//
//    @Query(" select count(hd) from Board hd where hd.depth=:depth ")
//    Long countByDepth(@Param("depth") Long depth);

    Board findOneBySeq(Long seq);
}
