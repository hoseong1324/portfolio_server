package com.portfolio.server.locations.service;

import com.portfolio.server.common.exception.CustomException;
import com.portfolio.server.common.exception.ExceptionCode;
import com.portfolio.server.locations.dto.LocationsDto;
import com.portfolio.server.locations.entity.Locations;
import com.portfolio.server.locations.repo.LocationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetLocationsService {

    private final LocationsRepository locationsRepository;

    public List<LocationsDto.LocationsSummary> excuteForList(){
        return locationsRepository.findAll().stream().map(locations ->
                LocationsDto.LocationsSummary.builder()
                        .locationId(locations.getLocationId())
                        .streetAddress(locations.getStreetAddress())
                        .postalCode(locations.getPostalCode())
                        .city(locations.getCity())
                        .stateProvince(locations.getStateProvince())
                        .countryId(locations.getCountries().getCountryId())
                        .build()
        ).sorted(Comparator.comparing(LocationsDto.LocationsSummary::getLocationId)).collect(Collectors.toList());
    }

    public LocationsDto.LocationsSummary execute(int locationId){
        Locations locations = locationsRepository.findById(locationId)
                .orElseThrow(() -> new CustomException(ExceptionCode.LOCATIONS_NOT_FOUND));

        return LocationsDto.LocationsSummary.builder()
                .locationId(locations.getLocationId())
                .streetAddress(locations.getStreetAddress())
                .postalCode(locations.getPostalCode())
                .city(locations.getCity())
                .stateProvince(locations.getStateProvince())
                .countryId(locations.getCountries().getCountryId())
                .build();
    }
}
