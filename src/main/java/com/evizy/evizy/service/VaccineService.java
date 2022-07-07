package com.evizy.evizy.service;


import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Vaccine;
import com.evizy.evizy.domain.dto.VaccineRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.VaccineRepository;
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
public class VaccineService {
    private final VaccineRepository vaccineRepository;

    public VaccineRequest create(VaccineRequest request) throws BusinessFlowException {
        Vaccine vaccine = Vaccine.builder()
                .name(request.getName())
                .build();
        vaccineRepository.save(vaccine);
        return VaccineRequest.builder()
                .id(vaccine.getId())
                .name(vaccine.getName())
                .build();
    }

    public void delete(Long id) throws BusinessFlowException {
        Optional<Vaccine> optionalVaccine = vaccineRepository.findById(id);
        if (optionalVaccine.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccine not found!");
        }

        vaccineRepository.delete(optionalVaccine.get());
    }

    public VaccineRequest update(Long id, VaccineRequest request) throws BusinessFlowException {
        Optional<Vaccine> optionalVaccine = vaccineRepository.findById(id);
        if (optionalVaccine.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccine not found!");
        }

        Vaccine vaccine = optionalVaccine.get();
        vaccine.setName(request.getName());
        vaccineRepository.save(vaccine);

        return VaccineRequest.builder()
                .id(vaccine.getId())
                .name(vaccine.getName())
                .build();
    }

    public VaccineRequest find(Long id) {
        Optional<Vaccine> optionalVaccine = vaccineRepository.findById(id);
        if (optionalVaccine.isEmpty())
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Vaccine not found!");
        Vaccine vaccine = optionalVaccine.get();
        return VaccineRequest
                .builder()
                .id(vaccine.getId())
                .name(vaccine.getName())
                .build();
    }

    public List<VaccineRequest> find() {
        List<Vaccine> vaccineList = vaccineRepository.findAll();
        List<VaccineRequest> vaccineRequests = new ArrayList<>();
        for(Vaccine vaccine : vaccineList) {
            vaccineRequests.add(VaccineRequest
                    .builder()
                    .id(vaccine.getId())
                    .name(vaccine.getName())
                    .build());
        }
        return vaccineRequests;
    }
}
