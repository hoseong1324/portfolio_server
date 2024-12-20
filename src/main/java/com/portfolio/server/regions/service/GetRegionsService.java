package com.portfolio.server.regions.service;

import com.portfolio.server.regions.dto.RegionsDto;
import com.portfolio.server.regions.repo.RegionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetRegionsService {

    private final RegionsRepository regionsRepository;

    @Transactional(readOnly = true)
    public List<RegionsDto.RegionsSummary> executeForList(){
        return regionsRepository.findAll().stream().map(region ->
                RegionsDto.RegionsSummary.builder()
                        .regionId(region.getRegionId())
                        .regionName(region.getRegionName())
                        .build()
        ).collect(Collectors.toList());
    }
}
