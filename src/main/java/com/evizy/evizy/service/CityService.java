package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.City;
import com.evizy.evizy.domain.dto.CityRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public CityRequest create(CityRequest request) throws BusinessFlowException {
        City city = City.builder()
                .name(request.getName())
                .build();
        cityRepository.save(city);
        return CityRequest.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }

    public void delete(Long id) throws BusinessFlowException {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "City not found!");
        }

        cityRepository.delete(optionalCity.get());
    }

    public CityRequest update(Long id, CityRequest request) throws BusinessFlowException {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "City not found!");
        }

        City city = optionalCity.get();
        city.setName(request.getName());
        cityRepository.save(city);

        return CityRequest.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }

    public CityRequest find(Long id) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isEmpty())
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "City not found!");
        City city = optionalCity.get();
        return CityRequest
                .builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }

    public List<CityRequest> find() {
        List<City> cityList = cityRepository.findAll();
        List<CityRequest> cityRequests = new ArrayList<>();
        for(City city : cityList) {
            cityRequests.add(CityRequest
                    .builder()
                    .id(city.getId())
                    .name(city.getName())
                    .build());
        }
        return cityRequests;
    }
}
