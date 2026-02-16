package com.trevisan.CalculadoraMicroServicesDB.Entity;


import com.trevisan.CalculadoraMicroServicesDB.Domain.Enums.TipoDeOperacao;
import com.trevisan.CalculadoraMicroServicesDB.Domain.Operations;
import com.trevisan.CalculadoraMicroServicesDB.Dtos.OperationsRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OperationEntityTest {

    @Test
    @DisplayName("Should return provided values")
    void record_getters_returnProvidedValues() {
        Operations operation = new Operations(1L, "10", "20", TipoDeOperacao.SOMA ,"30", null);

        assertEquals("10", operation.getValueOne());
        assertEquals("20", operation.getValueTwo());
        assertEquals("30", operation.getResult());
        assertNotNull(operation.getTipoDeOperacao());
    }

    @Test
    @DisplayName("Should verify hashcode between DTOs")
    void equals_and_hashcode_work() {
        Operations operationA = new Operations(1L, "10", "20", TipoDeOperacao.SOMA ,"30", null);
        Operations operationB = new Operations(1L, "10", "20", TipoDeOperacao.SOMA ,"30", null);

        assertEquals(operationA, operationB);
        assertEquals(operationA.hashCode(), operationB.hashCode());
    }

}