package com.econrich.assignment.locations.controller;

import com.econrich.assignment.common.response.CommonResult;
import com.econrich.assignment.common.response.ResponseService;
import com.econrich.assignment.locations.service.GetLocationsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
@Tag(name = "002.위치", description = "위치 API")
public class LocationsController {
    private final ResponseService responseService;
    private final GetLocationsService getLocationsService;

    @GetMapping
    public CommonResult getLocations(){
        return responseService.getListResult(getLocationsService.excuteForList());
    }

    @GetMapping("/{locationId}")
    public CommonResult getLocation(@PathVariable int locationId){
        return responseService.getSingleResult(getLocationsService.execute(locationId));
    }
}
