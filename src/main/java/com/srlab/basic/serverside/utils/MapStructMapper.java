package com.srlab.basic.serverside.utils;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.devices.models.Device;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.control.NoComplexMapping;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = MapStructMapper.class, mappingControl = NoComplexMapping.class)
public interface MapStructMapper {

    MapStructMapper INSTANCE = Mappers.getMapper(MapStructMapper.class);

    void update(HierarchyData src, @MappingTarget HierarchyData dst);
    @Mapping(source = "likeCount", target="likeCount", ignore=true)
    @Mapping(source = "dislikeCount", target="dislikeCount", ignore=true)
    void update(Board src, @MappingTarget Board dst);
    @Mapping(source = "likeCount", target="likeCount", ignore=true)
    @Mapping(source = "dislikeCount", target="dislikeCount", ignore=true)
    void update(Reply src, @MappingTarget Reply dst);
    void update(Device src, @MappingTarget Device dst);
    void update(AvailableFile src, @MappingTarget AvailableFile dst);

}
