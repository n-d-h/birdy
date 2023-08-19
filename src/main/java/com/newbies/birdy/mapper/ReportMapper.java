package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.ReportDTO;
import com.newbies.birdy.entities.Report;
import com.newbies.birdy.entities.ReportKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {

    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);


    @Mapping(target = "userId", source = "userReport.id")
    @Mapping(target = "productName", source = "productReport.productName")
    @Mapping(target = "productId", source = "productReport.id")
    @Mapping(target = "fullName", source = "userReport.fullName")
    ReportDTO toDTO(Report report);

    @Mapping(target = "userReport", ignore = true)
    @Mapping(target = "productReport", ignore = true)
    @Mapping(target = "id", source = ".", qualifiedByName = "mapReportKey")
    Report toEntity(ReportDTO dto);

    @Named("mapReportKey")
    default ReportKey mapReportKey(ReportDTO dto) {
        ReportKey a = new ReportKey();
        a.setUserId(dto.getUserId());
        a.setProductId(dto.getProductId());
        return a;
    }
}
