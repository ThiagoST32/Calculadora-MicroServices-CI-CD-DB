package com.trevisan.CalculadoraMicroServicesDB.Mappers;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Operations;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class OperationMappers {

    public OperationsResponseDTO mapToOperationsResponse(Operations operations) {
        return new OperationsResponseDTO(
                operations.getValueOne(),
                operations.getValueTwo(),
                operations.getResult(),
                operations.getTipoDeOperacao()
        );
    }
}
