package com.trevisan.CalculadoraMicroServicesDB.Services;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Operations;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsRequestDTO;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsResponseDTO;
import com.trevisan.CalculadoraMicroServicesDB.Mappers.OperationMappers;
import com.trevisan.CalculadoraMicroServicesDB.Repositories.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationPersistService {

    private final OperationRepository repository;
    private final OperationMappers mappers;

    @Autowired
    public OperationPersistService(OperationRepository repository, OperationMappers mappers) {
        this.repository = repository;
        this.mappers = mappers;
    }

    public void saveOperationOnDB(OperationsRequestDTO dto){
        if (dto == null){
            throw new RuntimeException();
        }

        Operations newPersistOperation = new Operations(dto);
        repository.save(newPersistOperation);
    }

    public List<OperationsResponseDTO> getAllOperations(){
        return repository.findAll().stream().map(mappers::mapToOperationsResponse).toList();
    }

    public OperationsResponseDTO getPreviousOperations(){
        var operaReturned = repository.findPreviousOperationsPersisted();
        return mappers.mapToOperationsResponse(operaReturned);
    }
}
