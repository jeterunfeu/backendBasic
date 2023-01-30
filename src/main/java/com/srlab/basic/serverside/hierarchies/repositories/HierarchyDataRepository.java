package com.srlab.basic.serverside.hierarchies.repositories;

import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface HierarchyDataRepository extends JpaRepository<HierarchyData, Long> {
   Optional<HierarchyData> findOneBySeq(Long seq);

//    @Query(" select hd from HierarchyData hd where hd.root=:root and hd.rightNode=:left ")
//    HierarchyData findByRightNode(@Param("root") HierarchyData root, @Param("left") Long leftNode);
//
//    @Query(" select hd from HierarchyData hd where hd.root=:root and hd.leftNode=:right ")
//    HierarchyData findByLeftNode(@Param("root") HierarchyData root, @Param("right") Long rightNode);
//
//    @Query(" select min(hd.leftNode) from HierarchyData hd where hd.root=:root and hd.depth=:depth ")
//    Long findLeftMinByDepth(@Param("root") HierarchyData root, @Param("depth") Long depth);
//
//    @Query(" select hd.parent from HierarchyData hd where hd.leftNode = (select max(hd.leftNode) from hd where hd.root=:root and hd.depth=:depth) ")
//    HierarchyData findMinParentByDepth(@Param("root") HierarchyData root, @Param("depth") Long depth);
//
//    @Query(" select hd.parent from HierarchyData hd where hd.leftNode = (select min(hd.leftNode) from hd where hd.root=:root and hd.depth=:depth) ")
//    HierarchyData findMaxParentByDepth(@Param("root") HierarchyData root, @Param("depth") Long depth);
//
//    @Query(" select max(hd.rightNode) from HierarchyData hd where hd.root=:root and hd.depth=:depth ")
//    Long findRightMaxFromDepth(@Param("root") HierarchyData root, Long depth);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.rightNode=hd.rightNode+2 where hd.root=:root and hd.rightNode>:value ")
//    void addRight(@Param("root") HierarchyData root, @Param("value") Long rightNode);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.depth=hd.depth+1, hd.parent=:root where hd.root=:root and hd.depth>:depth ")
//    void addDepth(@Param("root") HierarchyData root, @Param("depth") Long depth);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.leftNode=hd.leftNode-:diff where hd.root=:root and hd.leftNode>:diff ")
//    void updateLeftNode(@Param("root") HierarchyData root, @Param("diff") Long diff);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.rightNode=hd.rightNode-:diff where hd.root=:root and hd.rightNode>:diff ")
//    void updateRightNode(@Param("root") HierarchyData root, @Param("diff") Long diff);
//
//    @Transactional
//    @Modifying
//    @Query(" delete from HierarchyData hd where hd.root=:root and hd.leftNode between :from and :to ")
//    void deleteByLeftNodeBetween(@Param("root") HierarchyData root, @Param("from") Long from, @Param("to") Long to);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.leftNode=hd.leftNode-:diff, " +
//            " hd.rightNode=hd.rightNode-:diff+1" +
//            " where hd.root=:root and hd.leftNode between :left and :right ")
//    void originSetting(@Param("root") HierarchyData root, @Param("left") Long leftNode, @Param("right") Long rightNode, @Param("diff") Long groupDiff);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.leftNode=hd.leftNode+:diff, " +
//            " hd.rightNode=hd.rightNode+:diff+1" +
//            " where hd.root=:root and hd.leftNode between :left and :right ")
//    void targetSetting(@Param("root") HierarchyData root, @Param("left") Long nextLeftNode, @Param("right") Long nextRightNode, @Param("diff") Long groupDiff);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.leftNode=hd.leftNode+2 where hd.root=:root and hd.leftNode>:left ")
//    void addLeftNode(@Param("root") HierarchyData root, @Param("left") Long leftNode);
//
//    @Transactional
//    @Modifying
//    @Query(" update HierarchyData hd set hd.rightNode=hd.rightNode+2 where hd.root=:root and hd.rightNode>:right ")
//    void addRightNode(@Param("root") HierarchyData root, @Param("right") Long rightNode);
//
//    @Query(" update HierarchyData hd set hd.nodeOrder=hd.nodeOrder+1 where hd.depth=0 and hd.nodeOrder=:order ")
//    void addOrder(@Param("order") Long order);
//
//    @Query(" update HierarchyData hd set hd.nodeOrder=hd.nodeOrder-1 where hd.depth=0 and hd.nodeOrder=:order ")
//    void subOrder(Long order);
//
//    @Query(" select count(hd) from HierarchyData hd where hd.depth=:depth ")
//    Long countByDepth(@Param("depth") Long depth);

}
